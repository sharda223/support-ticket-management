# Java / Spring Boot Rules

Aligned with the implemented backend under `backend/` and specs in `spec/`.

## Java 21

- Target and compile with **Java 21** (`pom.xml` `<java.version>21</java.version>`).
- Use a JDK that includes `javac` 21 (runtime-only JRE installs fail Maven compile).
- Prefer records for request/response DTOs where immutable payloads are sufficient.
- Prefer enums for closed sets: `TicketStatus`, `Priority`.

## Spring Boot standards

- Spring Boot 3.x with starters already used: Web, Data JPA, Validation, Test.
- Configuration via `application.yml`; override secrets with env vars (`SPRING_DATASOURCE_*`).
- Keep `spring.jpa.open-in-view=false`.
- Runtime datastore: **PostgreSQL**. Test profile may use H2 only.

## Layered architecture

| Layer | Package | Responsibility |
|-------|---------|----------------|
| Web | `...web`, `...web.dto` | Controllers, DTOs, mappers, exception advice, CORS |
| Service | `...service` | Business rules, state machine enforcement, transactions |
| Domain | `...domain` | Entities, enums, `TicketStateMachine` |
| Repository | `...repository` | Spring Data JPA interfaces |
| Exception | `...exception` | Domain/API exceptions |

- Controllers stay thin: validate input, call service, return DTOs.
- Persist status changes only after `TicketStateMachine.canTransition` succeeds in the service.

## Validation

- Use Jakarta Bean Validation on request DTOs (`@Valid`, `@NotBlank`, `@NotNull`, `@Size`).
- Trim user text in the service before save.
- Create always forces status `OPEN`; field update must not change status.
- Unknown/malformed enum JSON → handled as bad request.

## Exception handling

Centralize in `GlobalExceptionHandler`:

| Case | HTTP |
|------|------|
| Bean validation | 400 + field errors |
| Malformed body / unknown enum | 400 |
| Invalid status transition | 409 |
| Ticket not found | 404 |
| Unexpected | 500 (generic message; no secrets/stacks in response) |

## Database / repository

- JPA entities: `Ticket`, `Comment` only.
- Prefer repository queries for list/search/filter (`TicketRepository.search`).
- Do not commit credentials; use env vars / gitignored `.env`.
- Invalid transitions must not update the ticket row.

## REST practices

- Base path `/api/tickets` as implemented.
- JSON request/response; consistent DTO shapes.
- Status change only via `PATCH /api/tickets/{id}/status`.
- See `rules/api-standards.md` and `spec/api-contract.md`.

## Security / secrets

- No authentication in this project (assignment does not require it).
- Never commit passwords, tokens, or real DB URLs with credentials.
- Commit `.env.example` placeholders only.
- CORS allowed for configured Vite origin(s) only.
