package com.support.ticketmanagement.web.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCommentRequest(
        @NotBlank String body
) {
}
