package com.support.ticketmanagement.web;

import com.support.ticketmanagement.domain.TicketStatus;
import com.support.ticketmanagement.service.TicketService;
import com.support.ticketmanagement.web.dto.CommentResponse;
import com.support.ticketmanagement.web.dto.CreateCommentRequest;
import com.support.ticketmanagement.web.dto.CreateTicketRequest;
import com.support.ticketmanagement.web.dto.TicketResponse;
import com.support.ticketmanagement.web.dto.UpdateStatusRequest;
import com.support.ticketmanagement.web.dto.UpdateTicketRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public ResponseEntity<TicketResponse> create(@Valid @RequestBody CreateTicketRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ticketService.create(request));
    }

    @GetMapping
    public List<TicketResponse> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) TicketStatus status) {
        return ticketService.list(q, status);
    }

    @GetMapping("/{id}")
    public TicketResponse get(@PathVariable Long id) {
        return ticketService.getById(id);
    }

    @PutMapping("/{id}")
    public TicketResponse update(@PathVariable Long id, @Valid @RequestBody UpdateTicketRequest request) {
        return ticketService.update(id, request);
    }

    @PatchMapping("/{id}/status")
    public TicketResponse updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateStatusRequest request) {
        return ticketService.updateStatus(id, request.status());
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long id,
            @Valid @RequestBody CreateCommentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ticketService.addComment(id, request));
    }
}
