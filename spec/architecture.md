# Architecture

Describes the **actual** architecture of this repository. Components not present in the codebase are omitted.

## Overview

```
┌─────────────────────┐     HTTP JSON      ┌──────────────────────────────┐
│  React + Vite UI    │ ─────────────────► │  Spring Boot REST API        │
│  (frontend/)        │ ◄───────────────── │  (backend/)                  │
│  port 5173 (dev)    │                    │  port 8080                   │
└─────────────────────┘                    └──────────────┬───────────────┘
                                                          │ JPA/JDBC
                                                          ▼
                                               ┌──────────────────────┐
                                               │  PostgreSQL          │
                                               │  (runtime)           │
                                               └──────────────────────┘
```

Tests use an in-memory H2 database via the Spring `test` profile. Docker/Testcontainers are not part of the current setup.

## Backend (`backend/`)

- **Runtime:** Java 21, Spring Boot 3.3.x
- **Entry point:** `TicketManagementApplication`
- **Layers:**
  - **Web:** `TicketController`, DTOs, `TicketMapper`, `GlobalExceptionHandler`, `CorsConfig`
  - **Service:** `TicketService` (transactions + state machine enforcement)
  - **Domain:** `Ticket`, `Comment`, `TicketStatus`, `Priority`, `TicketStateMachine`
  - **Repository:** `TicketRepository`, `CommentRepository`
  - **Exceptions:** `ResourceNotFoundException`, `InvalidStatusTransitionException`

### Persistence

- JPA/Hibernate with `ddl-auto: update` for local/runtime schema evolution.
- Tables: `ticket`, `comment` (one-to-many from ticket).
- Datasource URL/username/password from environment variables (see `backend/.env.example`).

### Cross-cutting

- CORS filter allows configured frontend origins (`app.cors.allowed-origins`, default `http://localhost:5173`).
- No security/auth filter chain for login.

## Frontend (`frontend/`)

- **Runtime:** React 18 + Vite 5 + React Router 6
- **Entry:** `main.jsx` → `App.jsx`
- **Pages:** `TicketListPage`, `CreateTicketPage`, `TicketDetailPage`
- **Shared UI:** `ErrorBanner`, `LoadingState`, `EmptyState`, `StatusBadge`
- **API client:** `api.js` (`fetch` to `VITE_API_BASE_URL`)
- **Client-side transition helper:** `constants.js` `ALLOWED_TRANSITIONS` / `getNextStatuses` (UX only; backend remains authoritative)

## Communication flow

1. Browser loads Vite app.
2. UI calls REST endpoints under `{VITE_API_BASE_URL}/api/tickets...`.
3. Controller validates DTOs → service applies rules → repositories persist/load.
4. Errors return `ErrorResponse` JSON; UI surfaces `message` and field `errors`.

## Spec-driven layout (repo)

| Path | Role |
|------|------|
| `spec/` | Requirements and design specs |
| `rules/` | Implementation standards for humans/AI |
| `.cursor/rules/` | Cursor glob rules for backend/frontend |
| `docs/` | Process artifacts (e.g. prompt history) |
| `skills/commands/` | Reusable review/generate command prompts |

## Explicitly not in this architecture

- Authentication / authorization services
- Message queues, caches, microservices split
- Next.js, API gateway, reverse proxy configs in-repo
- Pagination layer
- File attachments or notification services
