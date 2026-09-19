package com.support.ticketmanagement.web;

import com.support.ticketmanagement.domain.Comment;
import com.support.ticketmanagement.domain.Ticket;
import com.support.ticketmanagement.web.dto.CommentResponse;
import com.support.ticketmanagement.web.dto.TicketResponse;
import java.util.List;

public final class TicketMapper {

    private TicketMapper() {
    }

    public static TicketResponse toSummary(Ticket ticket) {
        return new TicketResponse(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getStatus(),
                ticket.getPriority(),
                ticket.getAssignee(),
                ticket.getCreatedAt(),
                ticket.getUpdatedAt(),
                null
        );
    }

    public static TicketResponse toDetail(Ticket ticket) {
        List<CommentResponse> comments = ticket.getComments().stream()
                .map(TicketMapper::toComment)
                .toList();
        return new TicketResponse(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getStatus(),
                ticket.getPriority(),
                ticket.getAssignee(),
                ticket.getCreatedAt(),
                ticket.getUpdatedAt(),
                comments
        );
    }

    public static CommentResponse toComment(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getTicket().getId(),
                comment.getBody(),
                comment.getCreatedAt()
        );
    }
}
