package com.support.ticketmanagement.web.dto;

import com.support.ticketmanagement.domain.Priority;
import com.support.ticketmanagement.domain.TicketStatus;
import java.time.Instant;
import java.util.List;

public record TicketResponse(
        Long id,
        String title,
        String description,
        TicketStatus status,
        Priority priority,
        String assignee,
        Instant createdAt,
        Instant updatedAt,
        List<CommentResponse> comments
) {
}
