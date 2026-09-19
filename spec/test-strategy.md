# Test Strategy

Documents the **actual** testing approach used in this project.

## Goals

1. Prove ticket state-machine rules (valid + invalid).
2. Prove persistence of tickets/comments through the API.
3. Prove validation and error status codes.
4. Prove frontend helper correctness and that the UI bundle builds.

## Backend tests

| Suite | Type | Tooling | What it covers |
|-------|------|---------|----------------|
| `TicketStateMachineTest` | Unit | JUnit 5 | Allowed/forbidden transitions on `TicketStateMachine` |
| `TicketApiIntegrationTest` | Integration (API + DB) | Spring Boot Test, MockMvc, H2 (`test` profile) | Create/list/get/update/status/comments, search/filter, validation, 404, persistence |

### State-machine validation

- Parameterized valid transitions V1–V5 via HTTP `PATCH .../status`.
- Parameterized invalid transitions including assignment cases (`CLOSED→OPEN`, etc.) expecting **409** and unchanged status.
- Domain unit matrix mirrors service rules.

### Persistence

- Create → get
- Field update → get
- Valid/invalid status → get
- Add comment → get includes comment
- Repository flush/reload smoke path in integration suite

### How to run

```bash
cd backend
export JAVA_HOME=<jdk-21-with-javac>
mvn test
```

Observed result during development: **49 tests, 0 failures** (26 unit + 23 API integration).

## Frontend tests

| Suite | Type | Tooling | What it covers |
|-------|------|---------|----------------|
| `constants.test.js` | Unit | Node.js `node --test` | `getNextStatuses` / transition map; `formatApiError` |

### Build validation

```bash
cd frontend
npm test
npm run build
```

`npm run build` (Vite) is the compile/bundle gate. No Cypress/Playwright E2E suite exists in-repo.

## Manual UI validation (recommended)

With backend (PostgreSQL) and `npm run dev` running:

1. Create ticket → appears as `OPEN` on detail.
2. Search and status filter on list.
3. Edit fields; confirm status unchanged.
4. Walk happy-path statuses; try illegal transition (UI should hide most; API still enforces).
5. Add comments on open and terminal tickets.
6. Stop/start backend against same PostgreSQL; data remains.
7. Trigger validation errors (blank fields) and confirm banners.

## Out of scope for current automated suite

- Browser E2E
- Testcontainers PostgreSQL (Docker was unavailable; H2 used for tests only)
- Load/performance tests
- Frontend component DOM tests (React Testing Library not currently wired)
