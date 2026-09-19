package com.support.ticketmanagement.repository;

import com.support.ticketmanagement.domain.Ticket;
import com.support.ticketmanagement.domain.TicketStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Query("""
            SELECT t FROM Ticket t
            WHERE (:status IS NULL OR t.status = :status)
              AND (
                   :q IS NULL OR :q = ''
                   OR LOWER(t.title) LIKE LOWER(CONCAT('%', :q, '%'))
                   OR LOWER(t.description) LIKE LOWER(CONCAT('%', :q, '%'))
              )
            ORDER BY t.updatedAt DESC
            """)
    List<Ticket> search(@Param("q") String q, @Param("status") TicketStatus status);
}
