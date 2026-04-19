package com.animalleague.april.common.infrastructure;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.server.ResponseStatusException;

import com.animalleague.april.common.api.ApiException;
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

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(
        ApiException exception,
        HttpServletRequest request
    ) {
        return ResponseEntity.status(exception.getStatusCode()).body(
            ErrorResponse.of(
                exception.getStatusCode(),
                exception.getCode(),
                exception.getMessage(),
                request.getRequestURI()
            )
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
        return new ResponseEntity<>(
            ErrorResponse.of(statusCode, "RESPONSE_STATUS_ERROR", reason, request.getRequestURI()),
            exception.getHeaders(),
            statusCode
        );
    }

    @ExceptionHandler({
        NoResourceFoundException.class,
        HttpRequestMethodNotSupportedException.class,
        HttpMediaTypeNotSupportedException.class,
        HttpMediaTypeNotAcceptableException.class
    })
    public ResponseEntity<ErrorResponse> handleSpringWebException(
        Exception exception,
        HttpServletRequest request
    ) {
        return buildFrameworkErrorResponse(exception, request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(
        Exception exception,
        HttpServletRequest request
    ) {
        if (exception instanceof org.springframework.web.ErrorResponse errorResponse) {
            return buildFrameworkErrorResponse(errorResponse, exception, request.getRequestURI());
        }

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

    private ResponseEntity<ErrorResponse> buildFrameworkErrorResponse(
        Exception exception,
        String path
    ) {
        HttpStatusCode statusCode = resolveFrameworkStatusCode(exception);
        return buildFrameworkErrorResponse(resolveFrameworkHeaders(exception), exception, statusCode, path);
    }

    private ResponseEntity<ErrorResponse> buildFrameworkErrorResponse(
        org.springframework.web.ErrorResponse errorResponse,
        Exception exception,
        String path
    ) {
        return buildFrameworkErrorResponse(
            errorResponse.getHeaders(),
            exception,
            errorResponse.getStatusCode(),
            path
        );
    }

    private ResponseEntity<ErrorResponse> buildFrameworkErrorResponse(
        HttpHeaders headers,
        Exception exception,
        HttpStatusCode statusCode,
        String path
    ) {
        return new ResponseEntity<>(
            ErrorResponse.of(
                statusCode,
                resolveFrameworkErrorCode(exception),
                resolveFrameworkMessage(exception),
                path
            ),
            headers,
            statusCode
        );
    }

    private HttpStatusCode resolveFrameworkStatusCode(Exception exception) {
        if (exception instanceof NoResourceFoundException noResourceFoundException) {
            return noResourceFoundException.getStatusCode();
        }

        if (exception instanceof HttpRequestMethodNotSupportedException methodNotSupportedException) {
            return methodNotSupportedException.getStatusCode();
        }

        if (exception instanceof HttpMediaTypeNotSupportedException mediaTypeNotSupportedException) {
            return mediaTypeNotSupportedException.getStatusCode();
        }

        if (exception instanceof HttpMediaTypeNotAcceptableException mediaTypeNotAcceptableException) {
            return mediaTypeNotAcceptableException.getStatusCode();
        }

        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private HttpHeaders resolveFrameworkHeaders(Exception exception) {
        if (exception instanceof org.springframework.web.ErrorResponse errorResponse) {
            return errorResponse.getHeaders();
        }

        return HttpHeaders.EMPTY;
    }

    private String resolveFrameworkErrorCode(Exception exception) {
        if (exception instanceof NoResourceFoundException) {
            return "NOT_FOUND";
        }

        if (exception instanceof HttpRequestMethodNotSupportedException) {
            return "METHOD_NOT_ALLOWED";
        }

        if (exception instanceof HttpMediaTypeNotSupportedException) {
            return "UNSUPPORTED_MEDIA_TYPE";
        }

        if (exception instanceof HttpMediaTypeNotAcceptableException) {
            return "NOT_ACCEPTABLE";
        }

        if (exception instanceof ServletRequestBindingException) {
            return "BAD_REQUEST";
        }

        return "FRAMEWORK_ERROR";
    }

    private String resolveFrameworkMessage(Exception exception) {
        if (exception instanceof NoResourceFoundException) {
            return "요청한 리소스를 찾을 수 없습니다.";
        }

        if (exception instanceof HttpRequestMethodNotSupportedException) {
            return "허용되지 않은 HTTP 메서드입니다.";
        }

        if (exception instanceof HttpMediaTypeNotSupportedException) {
            return "지원하지 않는 Content-Type 입니다.";
        }

        if (exception instanceof HttpMediaTypeNotAcceptableException) {
            return "응답 가능한 미디어 타입이 없습니다.";
        }

        if (exception instanceof ServletRequestBindingException) {
            return "요청 헤더 또는 바인딩 값이 올바르지 않습니다.";
        }

        return "요청 처리에 실패했습니다.";
    }
}
