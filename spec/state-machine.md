# Ticket State Machine

## 1. Statuses

| Status | Meaning | Terminal? |
|--------|---------|-----------|
| `OPEN` | Newly created; not yet being worked | No |
| `IN_PROGRESS` | Actively being handled | No |
| `RESOLVED` | Work completed; awaiting closure | No |
| `CLOSED` | Finished and closed | **Yes** |
| `CANCELLED` | Cancelled before completion | **Yes** |

Status values are case-sensitive enums as shown above.

## 2. Lifecycle overview

```
                  ┌──────────────┐
                  │    OPEN      │
                  └──────┬───────┘
               ┌─────────┼─────────┐
               │         │         │
               ▼         ▼         │
      ┌──────────────┐  CANCELLED  │
      │ IN_PROGRESS  │◄────────────┘
      └──────┬───────┘
               │         │
               ▼         ▼
      ┌──────────────┐  CANCELLED
      │   RESOLVED   │
      └──────┬───────┘
               │
               ▼
      ┌──────────────┐
      │    CLOSED    │
      └──────────────┘
```

Initial status on create: **`OPEN`** (only).

## 3. Valid status transitions

| From | To | Allowed |
|------|-----|---------|
| `OPEN` | `IN_PROGRESS` | Yes |
| `OPEN` | `CANCELLED` | Yes |
| `IN_PROGRESS` | `RESOLVED` | Yes |
| `IN_PROGRESS` | `CANCELLED` | Yes |
| `RESOLVED` | `CLOSED` | Yes |

No other transitions are valid.

### Valid transition list (canonical)

1. `OPEN` → `IN_PROGRESS`
2. `OPEN` → `CANCELLED`
3. `IN_PROGRESS` → `RESOLVED`
4. `IN_PROGRESS` → `CANCELLED`
5. `RESOLVED` → `CLOSED`

## 4. Invalid status transitions

**Rule:** Any transition not in the valid list above is invalid and must be rejected by the backend.

Explicitly called out by the assignment (must reject):

| From | To | Result |
|------|-----|--------|
| `CLOSED` | `OPEN` | Reject |
| `RESOLVED` | `OPEN` | Reject |
| `CANCELLED` | `OPEN` | Reject |

Additional invalid examples (also reject; not exhaustive):

| From | To |
|------|-----|
| `OPEN` | `RESOLVED` |
| `OPEN` | `CLOSED` |
| `OPEN` | `OPEN` (no-op / same status) |
| `IN_PROGRESS` | `OPEN` |
| `IN_PROGRESS` | `CLOSED` |
| `IN_PROGRESS` | `IN_PROGRESS` |
| `RESOLVED` | `IN_PROGRESS` |
| `RESOLVED` | `CANCELLED` |
| `RESOLVED` | `RESOLVED` |
| `CLOSED` | `IN_PROGRESS` |
| `CLOSED` | `RESOLVED` |
| `CLOSED` | `CANCELLED` |
| `CLOSED` | `CLOSED` |
| `CANCELLED` | `IN_PROGRESS` |
| `CANCELLED` | `RESOLVED` |
| `CANCELLED` | `CLOSED` |
| `CANCELLED` | `CANCELLED` |

## 5. Enforcement rules

1. Status changes are performed only through the status-update API (see `api.md`).
2. The service layer loads the current ticket, evaluates `(currentStatus → newStatus)` against the valid set, and:
   - **Allows** and persists if valid
   - **Rejects** without changing data if invalid
3. Create must not accept a client-supplied status other than implying `OPEN`.
4. Field updates (title, description, priority, assignee) must not change status.
5. Terminal statuses (`CLOSED`, `CANCELLED`) accept no further status transitions.

## 6. Error behaviour for invalid transitions

- Do not persist the new status
- Return an error response with a clear message including **current status** and **requested status**
- Suggested HTTP status: `400 Bad Request` or `409 Conflict` (API chooses one and uses it consistently; recommended: **`409 Conflict`** for business-rule transition violations, **`400`** for unknown/malformed status values)

## 7. Testing expectations

See `testing.md` for required valid/invalid transition cases and persistence checks.
