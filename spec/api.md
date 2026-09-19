# REST API Specification

Base path: `/api`

Content type: `application/json`

No authentication headers required.

## 1. Common types

### TicketStatus

`OPEN` | `IN_PROGRESS` | `RESOLVED` | `CLOSED` | `CANCELLED`

### Priority

`LOW` | `MEDIUM` | `HIGH`

### TicketResponse

```json
{
  "id": 1,
  "title": "Cannot login",
  "description": "User reports login failure",
  "status": "OPEN",
  "priority": "HIGH",
  "assignee": "alex@example.com",
  "createdAt": "2026-09-18T12:00:00Z",
  "updatedAt": "2026-09-18T12:00:00Z",
  "comments": []
}
```

`comments` is included on detail responses. List responses may omit `comments` or include an empty array (implementation must be consistent; **recommended:** omit heavy comment lists on list endpoint, include on get-by-id).

### CommentResponse

```json
{
  "id": 10,
  "ticketId": 1,
  "body": "Investigating",
  "createdAt": "2026-09-18T12:05:00Z"
}
```

### ErrorResponse

```json
{
  "message": "Human-readable summary",
  "errors": [
    { "field": "title", "message": "must not be blank" }
  ]
}
```

`errors` may be omitted when not field-specific (e.g. not found, invalid transition).

For invalid transitions, `message` must mention current and requested status, e.g.:

`Invalid status transition from CLOSED to OPEN`

## 2. Endpoints

### 2.1 Create ticket

`POST /api/tickets`

**Request body:**

```json
{
  "title": "Cannot login",
  "description": "User reports login failure",
  "priority": "HIGH",
  "assignee": "alex@example.com"
}
```

| Field | Required | Notes |
|-------|----------|-------|
| title | Yes | Non-blank, max 200 |
| description | Yes | Non-blank |
| priority | Yes | `LOW` \| `MEDIUM` \| `HIGH` |
| assignee | No | Free-text |

**Ignored if sent:** `status`, `id`, timestamps — server sets `status=OPEN`.

**Responses:**

- `201 Created` + `TicketResponse`
- `400 Bad Request` on validation failure

---

### 2.2 List / search / filter tickets

`GET /api/tickets`

**Query parameters:**

| Param | Required | Notes |
|-------|----------|-------|
| `q` | No | Keyword; case-insensitive match on title **or** description |
| `status` | No | Exact status filter |

Both may be combined (AND).

**Response:** `200 OK` + array of ticket summaries (without nested comments recommended).

---

### 2.3 Get ticket by id

`GET /api/tickets/{id}`

**Responses:**

- `200 OK` + `TicketResponse` including `comments` (newest-first or oldest-first; **recommended:** oldest-first)
- `404 Not Found` if id does not exist

---

### 2.4 Update ticket fields

`PUT /api/tickets/{id}`

Updates title, description, priority, and assignee only. **Does not change status.**

**Request body:**

```json
{
  "title": "Cannot login — SSO",
  "description": "Updated details",
  "priority": "MEDIUM",
  "assignee": "sam@example.com"
}
```

Same validation rules as create for provided fields. All four fields should be sent (full replace of updatable fields). `assignee` may be `null` or `""` to clear.

**Responses:**

- `200 OK` + `TicketResponse`
- `400 Bad Request` on validation failure
- `404 Not Found`

---

### 2.5 Update ticket status

`PATCH /api/tickets/{id}/status`

**Request body:**

```json
{
  "status": "IN_PROGRESS"
}
```

**Behaviour:**

1. Load ticket
2. Validate `status` is a known enum value
3. If transition from current → requested is invalid → reject, no DB change
4. If valid → persist new status, update `updatedAt`

**Responses:**

- `200 OK` + `TicketResponse`
- `400 Bad Request` if status value is missing/unknown
- `409 Conflict` if transition is invalid per state machine
- `404 Not Found`

---

### 2.6 Add comment

`POST /api/tickets/{id}/comments`

**Request body:**

```json
{
  "body": "Investigating SSO configuration"
}
```

**Responses:**

- `201 Created` + `CommentResponse`
- `400 Bad Request` if body blank
- `404 Not Found` if ticket missing

Allowed for all ticket statuses (including terminal).

---

## 3. HTTP status summary

| Code | When |
|------|------|
| 200 | Successful get/list/update |
| 201 | Successful create (ticket or comment) |
| 400 | Validation / malformed request / unknown status value |
| 404 | Ticket not found |
| 409 | Invalid status transition |
| 500 | Unexpected server error |

## 4. CORS

Frontend (Vite) will call the API from a different origin in local development. Backend must allow the frontend origin (configuration detail at implementation time).

## 5. Out of scope

- Auth endpoints
- Delete ticket / delete comment
- Bulk operations
- Pagination (not required by assignment; may be added later without changing core contract)
