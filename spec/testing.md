# Testing Specification

## 1. Goals

Automated tests must prove:

1. **Valid** status transitions succeed and persist the new status
2. **Invalid** status transitions are rejected and leave status unchanged
3. **Persistence** of tickets (and comments) across a realistic persistence boundary (database-backed tests)

## 2. Scope

| Area | Required? |
|------|-----------|
| State-machine unit or service tests | Yes |
| Invalid transition rejection | Yes |
| Persistence / integration tests with database | Yes |
| API-level integration tests for status endpoint | Recommended (counts toward state-machine integration) |
| Frontend E2E | Not required by assignment (optional) |

## 3. Valid transition test cases

For each valid transition, assert HTTP/service success and resulting status:

| # | From | To |
|---|------|-----|
| V1 | `OPEN` | `IN_PROGRESS` |
| V2 | `OPEN` | `CANCELLED` |
| V3 | `IN_PROGRESS` | `RESOLVED` |
| V4 | `IN_PROGRESS` | `CANCELLED` |
| V5 | `RESOLVED` | `CLOSED` |

Also assert create always yields `OPEN`.

## 4. Invalid transition test cases

Minimum set (must include assignment examples):

| # | From | To | Expect |
|---|------|-----|--------|
| I1 | `CLOSED` | `OPEN` | Reject; status remains `CLOSED` |
| I2 | `RESOLVED` | `OPEN` | Reject; status remains `RESOLVED` |
| I3 | `CANCELLED` | `OPEN` | Reject; status remains `CANCELLED` |

Additional recommended cases (still required by “reject all invalid transitions”):

| # | From | To |
|---|------|-----|
| I4 | `OPEN` | `CLOSED` |
| I5 | `OPEN` | `RESOLVED` |
| I6 | `IN_PROGRESS` | `OPEN` |
| I7 | `IN_PROGRESS` | `CLOSED` |
| I8 | `RESOLVED` | `CANCELLED` |
| I9 | `CLOSED` | `CANCELLED` |
| I10 | Same-status no-ops (e.g. `OPEN` → `OPEN`) | Reject |

## 5. Persistence test cases

| # | Scenario |
|---|----------|
| P1 | Create ticket → read by id → fields match |
| P2 | Update fields → read again → updates persisted |
| P3 | Valid status change → read again → new status persisted |
| P4 | Invalid status change → read again → status unchanged |
| P5 | Add comment → get ticket → comment present |
| P6 | Create data, clear application context / use real DB session, verify data still present (integration with PostgreSQL or Testcontainers/PostgreSQL test setup) |

H2 may be used **only in tests** if explicitly configured as a test profile; the **running application** must use PostgreSQL. Prefer Testcontainers PostgreSQL or a dedicated test database for fidelity.

## 6. Validation tests (recommended)

- Blank title / description rejected
- Invalid priority rejected
- Comment with blank body rejected
- Get unknown id → 404

## 7. Pass criteria

- All V* and I* and P* required cases pass in CI or local `mvn test` / Gradle test
- No test embeds real production secrets
