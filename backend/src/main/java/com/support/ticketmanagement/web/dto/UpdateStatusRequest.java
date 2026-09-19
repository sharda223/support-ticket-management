package com.support.ticketmanagement.web.dto;

import com.support.ticketmanagement.domain.TicketStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateStatusRequest(
        @NotNull TicketStatus status
) {
}
