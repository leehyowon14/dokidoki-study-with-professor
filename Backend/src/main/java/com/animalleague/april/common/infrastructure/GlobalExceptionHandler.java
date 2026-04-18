package com.animalleague.april.common.infrastructure;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import com.animalleague.april.common.api.ErrorResponse;
import com.animalleague.april.common.api.ErrorResponse.FieldViolation;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
        MethodArgumentNotValidException exception,
        HttpServletRequest request
    ) {
        return buildValidationResponse(
            HttpStatus.BAD_REQUEST,
            "VALIDATION_ERROR",
            "요청 본문 검증에 실패했습니다.",
            request.getRequestURI(),
            exception.getBindingResult().getFieldErrors()
        );
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(
        BindException exception,
        HttpServletRequest request
    ) {
        return buildValidationResponse(
            HttpStatus.BAD_REQUEST,
            "BIND_ERROR",
            "요청 값 바인딩에 실패했습니다.",
            request.getRequestURI(),
            exception.getBindingResult().getFieldErrors()
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
        ConstraintViolationException exception,
        HttpServletRequest request
    ) {
        List<FieldViolation> violations = exception.getConstraintViolations().stream()
            .map(violation -> new FieldViolation(violation.getPropertyPath().toString(), violation.getMessage()))
            .toList();

        return ResponseEntity.badRequest().body(
            ErrorResponse.of(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR",
                "요청 파라미터 검증에 실패했습니다.",
                request.getRequestURI()
            ).withViolations(violations)
        );
    }

    @ExceptionHandler({
        HttpMessageNotReadableException.class,
        MethodArgumentTypeMismatchException.class,
        MissingServletRequestParameterException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(
        Exception exception,
        HttpServletRequest request
    ) {
        return ResponseEntity.badRequest().body(
            ErrorResponse.of(
                HttpStatus.BAD_REQUEST,
                "BAD_REQUEST",
                resolveBadRequestMessage(exception),
                request.getRequestURI()
            )
        );
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(
        ResponseStatusException exception,
        HttpServletRequest request
    ) {
        HttpStatusCode statusCode = exception.getStatusCode();
        String reason = exception.getReason() == null ? "요청 처리에 실패했습니다." : exception.getReason();
        return ResponseEntity.status(statusCode).body(
            ErrorResponse.of(statusCode, "RESPONSE_STATUS_ERROR", reason, request.getRequestURI())
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(
        Exception exception,
        HttpServletRequest request
    ) {
        log.error(
            "처리되지 않은 예외 발생. method={}, uri={}, remoteAddr={}",
            request.getMethod(),
            request.getRequestURI(),
            request.getRemoteAddr(),
            exception
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_SERVER_ERROR",
                "서버 내부 오류가 발생했습니다.",
                request.getRequestURI()
            )
        );
    }

    private ResponseEntity<ErrorResponse> buildValidationResponse(
        HttpStatus status,
        String code,
        String message,
        String path,
        List<FieldError> fieldErrors
    ) {
        List<FieldViolation> violations = fieldErrors.stream()
            .map(fieldError -> new FieldViolation(fieldError.getField(), fieldError.getDefaultMessage()))
            .toList();

        return ResponseEntity.status(status).body(
            ErrorResponse.of(status, code, message, path).withViolations(violations)
        );
    }

    private String resolveBadRequestMessage(Exception exception) {
        if (exception instanceof HttpMessageNotReadableException) {
            return "요청 본문을 읽을 수 없습니다.";
        }

        if (exception instanceof MethodArgumentTypeMismatchException) {
            return "요청 파라미터 타입이 올바르지 않습니다.";
        }

        if (exception instanceof MissingServletRequestParameterException) {
            return "필수 요청 파라미터가 누락되었습니다.";
        }

        return "잘못된 요청입니다.";
    }
}
