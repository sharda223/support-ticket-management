package com.support.ticketmanagement.domain;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class TicketStateMachineTest {

    @ParameterizedTest
    @CsvSource({
            "OPEN, IN_PROGRESS",
            "OPEN, CANCELLED",
            "IN_PROGRESS, RESOLVED",
            "IN_PROGRESS, CANCELLED",
            "RESOLVED, CLOSED"
    })
    void allowsValidTransitions(TicketStatus from, TicketStatus to) {
        assertTrue(TicketStateMachine.canTransition(from, to));
    }

    @ParameterizedTest
    @CsvSource({
            "CLOSED, OPEN",
            "RESOLVED, OPEN",
            "CANCELLED, OPEN",
            "OPEN, CLOSED",
            "OPEN, RESOLVED",
            "OPEN, OPEN",
            "IN_PROGRESS, OPEN",
            "IN_PROGRESS, CLOSED",
            "IN_PROGRESS, IN_PROGRESS",
            "RESOLVED, IN_PROGRESS",
            "RESOLVED, CANCELLED",
            "RESOLVED, RESOLVED",
            "CLOSED, IN_PROGRESS",
            "CLOSED, RESOLVED",
            "CLOSED, CANCELLED",
            "CLOSED, CLOSED",
            "CANCELLED, IN_PROGRESS",
            "CANCELLED, RESOLVED",
            "CANCELLED, CLOSED",
            "CANCELLED, CANCELLED"
    })
    void rejectsInvalidTransitions(TicketStatus from, TicketStatus to) {
        assertFalse(TicketStateMachine.canTransition(from, to));
    }

    @Test
    void rejectsNullStatuses() {
        assertFalse(TicketStateMachine.canTransition(null, TicketStatus.OPEN));
        assertFalse(TicketStateMachine.canTransition(TicketStatus.OPEN, null));
    }
}
