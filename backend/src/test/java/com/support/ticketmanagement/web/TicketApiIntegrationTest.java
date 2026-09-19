package com.support.ticketmanagement.web;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.support.ticketmanagement.domain.TicketStatus;
import com.support.ticketmanagement.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TicketApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TicketRepository ticketRepository;

    @BeforeEach
    void cleanDatabase() {
        ticketRepository.deleteAll();
    }

    @Test
    void createTicketStartsAsOpenAndPersists() throws Exception {
        long id = createTicket("Login issue", "Cannot login", "HIGH", "alex@example.com");

        mockMvc.perform(get("/api/tickets/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Login issue"))
                .andExpect(jsonPath("$.description").value("Cannot login"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.assignee").value("alex@example.com"))
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.comments").isArray());
    }

    @Test
    void updateFieldsPersistsWithoutChangingStatus() throws Exception {
        long id = createTicket("Old title", "Old description", "LOW", null);

        mockMvc.perform(put("/api/tickets/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "New title",
                                  "description": "New description",
                                  "priority": "MEDIUM",
                                  "assignee": "sam@example.com"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New title"))
                .andExpect(jsonPath("$.description").value("New description"))
                .andExpect(jsonPath("$.priority").value("MEDIUM"))
                .andExpect(jsonPath("$.assignee").value("sam@example.com"))
                .andExpect(jsonPath("$.status").value("OPEN"));

        mockMvc.perform(get("/api/tickets/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New title"))
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @ParameterizedTest
    @CsvSource({
            "OPEN, IN_PROGRESS",
            "OPEN, CANCELLED",
            "IN_PROGRESS, RESOLVED",
            "IN_PROGRESS, CANCELLED",
            "RESOLVED, CLOSED"
    })
    void validStatusTransitionsSucceedAndPersist(TicketStatus from, TicketStatus to) throws Exception {
        long id = createTicketInStatus(from);

        mockMvc.perform(patch("/api/tickets/{id}/status", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"" + to + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(to.name()));

        mockMvc.perform(get("/api/tickets/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(to.name()));
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
            "RESOLVED, CANCELLED",
            "CLOSED, CANCELLED"
    })
    void invalidStatusTransitionsAreRejectedAndLeaveStatusUnchanged(TicketStatus from, TicketStatus to)
            throws Exception {
        long id = createTicketInStatus(from);

        mockMvc.perform(patch("/api/tickets/{id}/status", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"" + to + "\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value("Invalid status transition from " + from + " to " + to));

        mockMvc.perform(get("/api/tickets/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(from.name()));
    }

    @Test
    void addCommentPersistsOnTicket() throws Exception {
        long id = createTicket("Title", "Description", "LOW", null);

        mockMvc.perform(post("/api/tickets/{id}/comments", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"body\":\"Looking into this\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.body").value("Looking into this"))
                .andExpect(jsonPath("$.ticketId").value(id));

        mockMvc.perform(get("/api/tickets/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comments", hasSize(1)))
                .andExpect(jsonPath("$.comments[0].body").value("Looking into this"));
    }

    @Test
    void searchAndFilterWork() throws Exception {
        createTicket("VPN down", "Office network issue", "HIGH", null);
        createTicket("Email lag", "Slow mailbox", "LOW", null);
        long second = createTicket("Printer jam", "Office printer stuck", "MEDIUM", null);
        transition(second, TicketStatus.IN_PROGRESS);

        mockMvc.perform(get("/api/tickets").param("q", "office"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        mockMvc.perform(get("/api/tickets").param("status", "IN_PROGRESS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Printer jam"));

        mockMvc.perform(get("/api/tickets").param("q", "office").param("status", "OPEN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("VPN down"));
    }

    @Test
    void validationFailuresReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "",
                                  "description": "",
                                  "priority": "HIGH"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    void unknownTicketReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/tickets/{id}", 99999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Ticket not found: 99999"));
    }

    @Test
    void blankCommentRejected() throws Exception {
        long id = createTicket("Title", "Description", "LOW", null);

        mockMvc.perform(post("/api/tickets/{id}/comments", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"body\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void dataRemainsReadableAfterClearingPersistenceContext() throws Exception {
        long id = createTicket("Persist me", "Should remain", "HIGH", "ops");
        ticketRepository.flush();
        ticketRepository.findAll(); // touch repository
        // Force reload from DB rather than first-level cache of a previous entity instance
        ticketRepository.findById(id).orElseThrow();

        mockMvc.perform(get("/api/tickets/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Persist me"))
                .andExpect(jsonPath("$.assignee").value("ops"));
    }

    private long createTicket(String title, String description, String priority, String assignee)
            throws Exception {
        String payload = """
                {
                  "title": "%s",
                  "description": "%s",
                  "priority": "%s",
                  "assignee": %s
                }
                """.formatted(
                title,
                description,
                priority,
                assignee == null ? "null" : "\"" + assignee + "\"");

        MvcResult result = mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andReturn();

        JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
        return node.get("id").asLong();
    }

    private long createTicketInStatus(TicketStatus target) throws Exception {
        long id = createTicket("Ticket " + target, "Description", "MEDIUM", null);
        return switch (target) {
            case OPEN -> id;
            case IN_PROGRESS -> {
                transition(id, TicketStatus.IN_PROGRESS);
                yield id;
            }
            case RESOLVED -> {
                transition(id, TicketStatus.IN_PROGRESS);
                transition(id, TicketStatus.RESOLVED);
                yield id;
            }
            case CLOSED -> {
                transition(id, TicketStatus.IN_PROGRESS);
                transition(id, TicketStatus.RESOLVED);
                transition(id, TicketStatus.CLOSED);
                yield id;
            }
            case CANCELLED -> {
                transition(id, TicketStatus.CANCELLED);
                yield id;
            }
        };
    }

    private void transition(long id, TicketStatus to) throws Exception {
        mockMvc.perform(patch("/api/tickets/{id}/status", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"" + to + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(to.name()));
    }
}
