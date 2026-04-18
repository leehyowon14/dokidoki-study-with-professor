package com.animalleague.april.common.api;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.http.HttpStatusCode;

public record ErrorResponse(
    OffsetDateTime timestamp,
    int status,
    String error,
    String code,
    String message,
    String path,
    List<FieldViolation> violations
) {

    public ErrorResponse {
        violations = violations == null ? List.of() : List.copyOf(violations);
    }

    public static ErrorResponse of(
        HttpStatusCode status,
        String code,
        String message,
        String path
    ) {
        return new ErrorResponse(
            OffsetDateTime.now(),
            status.value(),
            status.toString(),
            code,
            message,
            path,
            List.of()
        );
    }

    public ErrorResponse withViolations(List<FieldViolation> violations) {
        return new ErrorResponse(timestamp, status, error, code, message, path, violations);
    }

    public record FieldViolation(String field, String reason) {
    }
}

