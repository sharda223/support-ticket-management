package com.support.ticketmanagement.web.dto;

import java.util.List;

public record ErrorResponse(
        String message,
        List<FieldErrorDetail> errors
) {
    public record FieldErrorDetail(String field, String message) {
    }

    public static ErrorResponse of(String message) {
        return new ErrorResponse(message, null);
    }

    public static ErrorResponse of(String message, List<FieldErrorDetail> errors) {
        return new ErrorResponse(message, errors);
    }
}
