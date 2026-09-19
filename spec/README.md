# Support Ticket Management System — Specifications

This directory contains the Spec-Driven Development (SDD) artifacts for the project.

**Status:** Specification in progress. Application implementation must not start until these documents are reviewed and accepted.

## Tech stack (decided)

| Layer | Choice |
|-------|--------|
| Backend | Java 21, Spring Boot, REST API |
| Database | PostgreSQL (primary; required for persistence across restarts) |
| Frontend | React with Vite |
| Auth | None (not required by the assignment) |

## Document index

| Document | Description |
|----------|-------------|
| [requirements.md](./requirements.md) | Functional and non-functional requirements, business rules, acceptance criteria |
| [state-machine.md](./state-machine.md) | Ticket statuses and allowed/forbidden transitions |
| [data-model.md](./data-model.md) | Persistence model and database constraints |
| [api.md](./api.md) | REST API contract |
| [frontend.md](./frontend.md) | React UI requirements |
| [testing.md](./testing.md) | Required tests (state machine + persistence) |
| [security.md](./security.md) | Secrets and credential handling |

## Recommended reading order

1. `requirements.md`
2. `state-machine.md`
3. `data-model.md`
4. `api.md`
5. `frontend.md`
6. `testing.md`
7. `security.md`

## Out of scope

The following are **not** part of this project unless the assignment is updated:

- Authentication / authorization / roles
- Additional ticket statuses or transitions beyond those specified
- Soft delete / hard delete of tickets
- Attachments, notifications, email, webhooks
- Analytics dashboards or reporting
- Multi-tenancy

## Implementation gate

Do **not** implement `backend/` or `frontend/` until the specification set above is complete and explicitly approved.
