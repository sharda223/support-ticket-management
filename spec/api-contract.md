# API Contract (Implemented)

Base URL: `{host}` (default local backend `http://localhost:8080`)  
Base path: `/api/tickets`  
Content-Type: `application/json`  
Auth: none

This contract reflects the **current** Spring controllers and DTOs. Design intent is also in `spec/api.md`.

## Shared types

### TicketResponse

| Field | Type | Notes |
|-------|------|-------|
| id | number | |
| title | string | |
| description | string | |
| status | enum | `OPEN` \| `IN_PROGRESS` \| `RESOLVED` \| `CLOSED` \| `CANCELLED` |
| priority | enum | `LOW` \| `MEDIUM` \| `HIGH` |
| assignee | string \| null | |
| createdAt | ISO-8601 instant | |
| updatedAt | ISO-8601 instant | |
| comments | CommentResponse[] \| null | Present on detail; omitted/null on list |

### CommentResponse

| Field | Type |
|-------|------|
| id | number |
| ticketId | number |
| body | string |
| createdAt | ISO-8601 instant |

### ErrorResponse

| Field | Type |
|-------|------|
| message | string |
| errors | `{ field, message }[]` \| null |

---

## Endpoints

### 1. Create ticket

- **Method/Path:** `POST /api/tickets`
- **Request:**

```json
{
  "title": "Cannot login",
  "description": "User reports login failure",
  "priority": "HIGH",
  "assignee": "alex@example.com"
}
```

| Field | Required | Validation |
|-------|----------|------------|
| title | yes | non-blank, max 200 |
| description | yes | non-blank |
| priority | yes | enum |
| assignee | no | max 200; blank → stored null |

- **Success:** `201` + `TicketResponse` with `status: "OPEN"`
- **Errors:** `400` validation / malformed body

### 2. List / search / filter

- **Method/Path:** `GET /api/tickets`
- **Query:**
  - `q` (optional) — keyword on title or description (case-insensitive)
  - `status` (optional) — exact status enum
- **Success:** `200` + `TicketResponse[]` (summaries without comment lists)
- **Pagination:** not supported

### 3. Get ticket

- **Method/Path:** `GET /api/tickets/{id}`
- **Success:** `200` + detail `TicketResponse` including `comments`
- **Errors:** `404` if missing

### 4. Update ticket fields

- **Method/Path:** `PUT /api/tickets/{id}`
- **Request:** title, description, priority, assignee (same rules as create)
- **Behaviour:** does **not** change status
- **Success:** `200` + `TicketResponse`
- **Errors:** `400`, `404`

### 5. Update status (state machine)

- **Method/Path:** `PATCH /api/tickets/{id}/status`
- **Request:**

```json
{ "status": "IN_PROGRESS" }
```

- **Valid transitions only:**
  - `OPEN` → `IN_PROGRESS` \| `CANCELLED`
  - `IN_PROGRESS` → `RESOLVED` \| `CANCELLED`
  - `RESOLVED` → `CLOSED`
- **Success:** `200` + updated ticket
- **Errors:**
  - `400` — missing/unknown status value
  - `409` — invalid transition; message like `Invalid status transition from CLOSED to OPEN`; DB unchanged
  - `404` — ticket missing

### 6. Add comment

- **Method/Path:** `POST /api/tickets/{id}/comments`
- **Request:** `{ "body": "Investigating" }` (`body` required, non-blank)
- **Success:** `201` + `CommentResponse`
- **Errors:** `400`, `404`
- Allowed for all ticket statuses, including terminal.

---

## CORS

Backend allows configured origins (default `http://localhost:5173`) for `/api/**`.
