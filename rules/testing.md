# Testing Rules

Aligned with `spec/testing.md`, backend tests under `backend/src/test`, and frontend tests under `frontend/src`.

## Backend testing

### Unit tests

- Pure domain logic without Spring context where possible.
- Existing: `TicketStateMachineTest` — valid and invalid transitions via parameterized cases.

### Integration / API tests

- `@SpringBootTest` + `@AutoConfigureMockMvc` + `@ActiveProfiles("test")`.
- Existing: `TicketApiIntegrationTest` exercises real HTTP endpoints against the test DB.
- Test profile uses **H2** (`application-test.yml`). Runtime app uses **PostgreSQL**.
- Clean data between tests (`deleteAll` in `@BeforeEach`).

### State-machine tests (required)

Cover at least:

- Create → status `OPEN`
- Valid: `OPEN→IN_PROGRESS`, `OPEN→CANCELLED`, `IN_PROGRESS→RESOLVED`, `IN_PROGRESS→CANCELLED`, `RESOLVED→CLOSED`
- Invalid examples including `CLOSED→OPEN`, `RESOLVED→OPEN`, `CANCELLED→OPEN` and other non-allowed pairs
- Assert **409** and that status is unchanged after rejection
- Assert persistence after successful transitions

### Persistence scenarios

- Create/read/update fields
- Status change persisted / rejected unchanged
- Comments persisted and returned on detail
- Reload from repository / API after write

### Validation / error scenarios

- Blank title/description → 400
- Blank comment body → 400
- Unknown ticket id → 404

## Frontend testing

- Prefer lightweight unit tests for pure helpers (no browser E2E required by current assignment).
- Existing: `frontend/src/constants.test.js` via `node --test` — transition helper + `formatApiError`.
- Keep `npm run build` green as a compile/bundle check.

## Naming and structure

| Kind | Pattern | Example |
|------|---------|---------|
| Domain unit | `*Test` | `TicketStateMachineTest` |
| API integration | `*IntegrationTest` | `TicketApiIntegrationTest` |
| Frontend unit | `*.test.js` | `constants.test.js` |

- Name methods after behaviour: `allowsValidTransitions`, `invalidStatusTransitionsAreRejectedAndLeaveStatusUnchanged`.
- Prefer parameterized tests for transition matrices.
- Do not embed secrets in tests.

## Commands

```bash
# Backend
cd backend && mvn test

# Frontend
cd frontend && npm test && npm run build
```
