# Support Ticket Management System — Specifications

This directory holds the Spec-Driven Development (SDD) artifacts for the project.

**Status:** Specs are approved and the system is **implemented**:

- Backend: Java 21, Spring Boot, JPA, REST (`backend/`)
- Database: PostgreSQL at runtime (H2 used only in the Spring `test` profile)
- Frontend: React + Vite (`frontend/`)
- Auth: none (not required)

Related process artifacts live outside this folder: `rules/`, `docs/prompt-history.md`, `.specstory/history/`, `skills/commands/`.

## Spec-Driven Development flow

```
Requirements → Specification → Implementation → Testing → Review / Fix
```

| Phase | What happened in this project |
|-------|-------------------------------|
| Requirements | Features, lifecycle, validation, and acceptance criteria captured |
| Specification | Specs under `spec/` (and coding rules under `rules/`) written and approved before coding |
| Implementation | Backend first, then frontend, against the approved specs |
| Testing | Backend unit/API tests; frontend unit tests + Vite build |
| Review / Fix | Spec/code alignment docs; environment fixes (e.g. JDK 21 with `javac`); tooling corrections |

## Document index

### Requirements & domain

| Document | Purpose |
|----------|---------|
| [requirements.md](./requirements.md) | Functional/non-functional requirements, business rules, acceptance criteria |
| [state-machine.md](./state-machine.md) | Ticket statuses and valid/invalid transitions |
| [data-model.md](./data-model.md) | Ticket and Comment persistence model and constraints |
| [security.md](./security.md) | Secrets handling; no credentials in git |

### API & UI design vs as-built

| Document | Purpose |
|----------|---------|
| [api.md](./api.md) | Original REST API design specification |
| [api-contract.md](./api-contract.md) | **Implemented** endpoints, payloads, and error behaviour |
| [frontend.md](./frontend.md) | Frontend requirements (screens, errors, UX rules) |
| [ui-flow.md](./ui-flow.md) | **Actual** React routes and user flows |

### Architecture & testing

| Document | Purpose |
|----------|---------|
| [architecture.md](./architecture.md) | As-built architecture (Spring Boot, PostgreSQL, React/Vite layers) |
| [testing.md](./testing.md) | Required test cases (state machine, persistence, validation) |
| [test-strategy.md](./test-strategy.md) | Actual testing approach, commands, and manual checks |

## Recommended reading order

1. `requirements.md` → `state-machine.md` → `data-model.md`
2. `api.md` then `api-contract.md` (design → implemented)
3. `frontend.md` then `ui-flow.md`
4. `architecture.md`
5. `testing.md` then `test-strategy.md`
6. `security.md`

## What the implemented system covers

- Create, list, view tickets
- Update title, description, priority, assignee
- Status changes via dedicated API with backend-enforced state machine
- Comments
- Keyword search and status filter
- Backend validation and structured errors
- Frontend error, loading, and empty states

## Out of scope

Not part of this project unless requirements change:

- Authentication / authorization / roles
- Extra statuses or transitions beyond the approved state machine
- Ticket/comment delete
- Attachments, notifications, email, webhooks
- Pagination
- Analytics / multi-tenancy
