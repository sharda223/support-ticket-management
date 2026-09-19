package com.support.ticketmanagement.service;

import com.support.ticketmanagement.domain.Comment;
import com.support.ticketmanagement.domain.Ticket;
import com.support.ticketmanagement.domain.TicketStateMachine;
import com.support.ticketmanagement.domain.TicketStatus;
import com.support.ticketmanagement.exception.InvalidStatusTransitionException;
import com.support.ticketmanagement.exception.ResourceNotFoundException;
import com.support.ticketmanagement.repository.CommentRepository;
import com.support.ticketmanagement.repository.TicketRepository;
import com.support.ticketmanagement.web.TicketMapper;
import com.support.ticketmanagement.web.dto.CommentResponse;
import com.support.ticketmanagement.web.dto.CreateCommentRequest;
import com.support.ticketmanagement.web.dto.CreateTicketRequest;
import com.support.ticketmanagement.web.dto.TicketResponse;
import com.support.ticketmanagement.web.dto.UpdateTicketRequest;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final CommentRepository commentRepository;

    public TicketService(TicketRepository ticketRepository, CommentRepository commentRepository) {
        this.ticketRepository = ticketRepository;
        this.commentRepository = commentRepository;
    }

    @Transactional
    public TicketResponse create(CreateTicketRequest request) {
        Ticket ticket = new Ticket();
        ticket.setTitle(request.title().trim());
        ticket.setDescription(request.description().trim());
        ticket.setPriority(request.priority());
        ticket.setAssignee(normalizeAssignee(request.assignee()));
        ticket.setStatus(TicketStatus.OPEN);
        return TicketMapper.toDetail(ticketRepository.save(ticket));
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> list(String q, TicketStatus status) {
        String keyword = StringUtils.hasText(q) ? q.trim() : null;
        return ticketRepository.search(keyword, status).stream()
                .map(TicketMapper::toSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public TicketResponse getById(Long id) {
        return TicketMapper.toDetail(findTicket(id));
    }

    @Transactional
    public TicketResponse update(Long id, UpdateTicketRequest request) {
        Ticket ticket = findTicket(id);
        ticket.setTitle(request.title().trim());
        ticket.setDescription(request.description().trim());
        ticket.setPriority(request.priority());
        ticket.setAssignee(normalizeAssignee(request.assignee()));
        return TicketMapper.toDetail(ticketRepository.save(ticket));
    }

    @Transactional
    public TicketResponse updateStatus(Long id, TicketStatus newStatus) {
        Ticket ticket = findTicket(id);
        TicketStatus current = ticket.getStatus();
        if (!TicketStateMachine.canTransition(current, newStatus)) {
            throw new InvalidStatusTransitionException(current, newStatus);
        }
        ticket.setStatus(newStatus);
        return TicketMapper.toDetail(ticketRepository.save(ticket));
    }

    @Transactional
    public CommentResponse addComment(Long ticketId, CreateCommentRequest request) {
        Ticket ticket = findTicket(ticketId);
        Comment comment = new Comment();
        comment.setBody(request.body().trim());
        ticket.addComment(comment);
        Comment saved = commentRepository.save(comment);
        return TicketMapper.toComment(saved);
    }

    private Ticket findTicket(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found: " + id));
    }

    private static String normalizeAssignee(String assignee) {
        if (!StringUtils.hasText(assignee)) {
            return null;
        }
        return assignee.trim();
    }
}
