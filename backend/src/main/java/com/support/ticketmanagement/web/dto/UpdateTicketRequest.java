package com.support.ticketmanagement.web.dto;

import com.support.ticketmanagement.domain.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateTicketRequest(
        @NotBlank @Size(max = 200) String title,
        @NotBlank String description,
        @NotNull Priority priority,
        @Size(max = 200) String assignee
) {
}
