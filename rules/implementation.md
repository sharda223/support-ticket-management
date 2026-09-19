# Implementation Rules — Support Ticket Management

These rules govern implementation. They align with approved specs under `spec/` and must not be violated.

## 1. Spec authority

- Treat `spec/*.md` as the source of truth.
- Do not invent features, statuses, or transitions beyond the specs.
- Do not implement frontend until backend is complete and tests pass (per current work plan).

## 2. Stack

- Backend: Java 21, Spring Boot, JPA, REST, PostgreSQL for the running app.
- Frontend (later): React + Vite only.
- No authentication unless the assignment is updated.

## 3. Domain & state machine

- Entities: `Ticket`, `Comment` only (for MVP).
- Statuses: `OPEN`, `IN_PROGRESS`, `RESOLVED`, `CLOSED`, `CANCELLED`.
- Valid transitions only:
  - `OPEN` → `IN_PROGRESS` | `CANCELLED`
  - `IN_PROGRESS` → `RESOLVED` | `CANCELLED`
  - `RESOLVED` → `CLOSED`
- Reject all other transitions in the **service layer**; do not persist invalid changes.
- Create always sets status `OPEN`; field updates must not change status.
- Status changes only via `PATCH /api/tickets/{id}/status`.

## 4. API

- Follow `spec/api.md` paths, payloads, and status codes.
- Invalid transition → `409` with message including current and requested status.
- Validation failures → `400`; missing ticket → `404`.

## 5. Security & secrets

- Never commit passwords, tokens, or real DB credentials.
- Use environment variables / `.env` (gitignored); commit `.env.example` with placeholders only.
- No `eval`, auth bypasses, or permissive insecure defaults beyond local CORS for the Vite origin.

## 6. Testing

- Cover valid transitions (V1–V5), invalid transitions (I1–I10 minimum set from `spec/testing.md`), and persistence (P1–P6).
- Running app: PostgreSQL. Tests may use H2 (test profile only) when Docker/Testcontainers is unavailable.
- No secrets in tests.

## 7. Change discipline

- Prefer minimal, reviewable changes.
- Do not add dependencies unless necessary.
- Follow existing project structure: `backend/`, `frontend/`, `spec/`, `docs/`, `rules/`.
