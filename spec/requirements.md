# Requirements Specification

## 1. Purpose

Build a Support Ticket Management System that allows users to create, view, update, search, and comment on support tickets, with a strictly enforced ticket status lifecycle. The system is assessed under Spec-Driven Development; correctness of the state machine, validation, persistence, and error handling is mandatory.

## 2. Functional requirements

| ID | Requirement |
|----|-------------|
| FR-01 | User can **create** a ticket with title, description, priority, and optional assignee. New tickets start in status `OPEN`. |
| FR-02 | User can **list** tickets. |
| FR-03 | User can **view** a single ticket’s details (including status, priority, assignee, timestamps, and comments). |
| FR-04 | User can **update** a ticket’s title, description, priority, and assignee. |
| FR-05 | User can **update** a ticket’s status, subject to the state machine in `state-machine.md`. |
| FR-06 | User can **add comments** to a ticket. |
| FR-07 | User can **search** tickets by keyword (matches ticket title and description). |
| FR-08 | User can **filter** tickets by status. |
| FR-09 | Search and status filter may be combined on the list endpoint. |
| FR-10 | All ticket and comment data is **persisted** in PostgreSQL and survives application restarts. |
| FR-11 | Backend **validates** all create/update/comment/status inputs and rejects invalid requests. |
| FR-12 | Frontend displays **meaningful error messages** returned by the API (validation failures, not found, invalid transitions). |

## 3. Non-functional requirements

| ID | Requirement |
|----|-------------|
| NFR-01 | Backend: Java 21 + Spring Boot exposing a REST API. |
| NFR-02 | Database: PostgreSQL as the primary datastore. |
| NFR-03 | Frontend: React application built with Vite. |
| NFR-04 | Invalid status transitions are rejected by the backend (never applied). |
| NFR-05 | No credentials, secrets, API keys, or passwords are committed to the repository. |
| NFR-06 | Configuration that varies by environment (e.g. DB URL, username, password) is supplied via environment variables or local untracked config. |
| NFR-07 | Automated tests cover valid transitions, invalid transitions, and persistence (see `testing.md`). |

## 4. Business rules

| ID | Rule |
|----|------|
| BR-01 | A newly created ticket always has status `OPEN`. Status cannot be set to another value at creation time. |
| BR-02 | Status changes must follow the allowed transitions defined in `state-machine.md`. |
| BR-03 | Any transition not listed as valid is **invalid** and must be rejected. |
| BR-04 | `CLOSED` and `CANCELLED` are terminal statuses; no further status changes are allowed. |
| BR-05 | Title is required and must be non-blank. |
| BR-06 | Description is required and must be non-blank. |
| BR-07 | Priority is required and must be one of: `LOW`, `MEDIUM`, `HIGH`. |
| BR-08 | Assignee is optional (nullable / empty allowed). When provided, it is free-text (no user directory; no authentication). |
| BR-09 | Comment body is required and must be non-blank. |
| BR-10 | Updating a ticket does not allow bypassing the state machine via a general update payload; status changes go through the status-update path defined in `api.md`. |
| BR-11 | Comments may be added to a ticket regardless of status (including `CLOSED` and `CANCELLED`), unless later restricted by an assignment update. |
| BR-12 | No authentication or authorization is implemented. |

## 5. Ticket lifecycle (summary)

Statuses: `OPEN`, `IN_PROGRESS`, `RESOLVED`, `CLOSED`, `CANCELLED`.

Happy path: `OPEN` → `IN_PROGRESS` → `RESOLVED` → `CLOSED`.

Cancellation: `OPEN` → `CANCELLED`; `IN_PROGRESS` → `CANCELLED`.

Full transition matrix: see `state-machine.md`.

## 6. Backend validation requirements

The backend must validate:

- Required fields present and non-blank where applicable
- Priority enum values
- Status enum values on status-change requests
- Target status is a **valid transition** from the current status
- Ticket exists for get/update/comment/status operations (404 if not)
- Request body shape / types (malformed JSON → 400)

Validation failures return a structured error response (see `api.md`) suitable for UI display.

## 7. Error handling requirements

| Scenario | Expected behaviour |
|----------|--------------------|
| Validation failure | HTTP 400 + field/message details |
| Invalid status transition | HTTP 400 (or 409) + clear message stating current and requested status |
| Ticket not found | HTTP 404 |
| Unexpected server error | HTTP 500 + generic message (no stack traces or secrets in response) |
| Frontend | Surfaces API error messages to the user; does not silently fail |

## 8. Security and secrets requirements

See `security.md`. Summary: no secrets in git; use env vars for DB credentials; no auth feature unless assignment changes.

## 9. Acceptance criteria

- [ ] Ticket can be created from the UI (status `OPEN`)
- [ ] Tickets can be listed
- [ ] Ticket details can be viewed
- [ ] Title, description, priority, and assignee can be updated
- [ ] Status can be changed only via valid transitions
- [ ] Comments can be added
- [ ] Keyword search works (title and description)
- [ ] Filter by status works
- [ ] Data survives application restart (PostgreSQL)
- [ ] Backend validation works
- [ ] UI shows meaningful errors
- [ ] Invalid status transitions are rejected by the backend
- [ ] State-machine and persistence tests pass
- [ ] No secrets are committed

## 10. Explicit non-goals

- Authentication / SSO / roles
- Extra statuses or transitions
- Ticket deletion
- File attachments
- Email / Slack notifications
- Pagination beyond a simple list (optional later; not required for MVP unless needed for usability — **not required by assignment**; list may return all matching tickets)

## 11. Design decisions (within required scope)

These are not new features; they make required fields implementable and testable:

| Topic | Decision |
|-------|----------|
| Priority values | `LOW`, `MEDIUM`, `HIGH` |
| Assignee | Optional free-text string |
| Keyword search | Case-insensitive match on `title` and `description` |
| Comment author | Not stored (no auth) |
| Ticket ID | Server-generated numeric ID |
| Status update | Dedicated API operation (separate from field update) |

## 12. Ambiguities resolved by product decisions

| Ambiguity | Resolution |
|-----------|------------|
| Auth required? | No |
| H2 vs PostgreSQL? | PostgreSQL only for the delivered system |
| React vs Next.js? | React + Vite |
| Comments after close/cancel? | Allowed |
| Re-open from CLOSED/RESOLVED/CANCELLED? | Forbidden |
