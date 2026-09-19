# API Standards

Aligned with the implemented API in `TicketController` and documented in `spec/api.md` / `spec/api-contract.md`.

## REST conventions

- Resource-oriented paths under `/api`.
- Primary resource: `tickets` (plural).
- Sub-resource: `comments` under a ticket.
- JSON (`application/json`) for request and response bodies.
- No authentication headers required.

## HTTP methods (as implemented)

| Method | Usage |
|--------|--------|
| `GET` | List/search/filter; get by id |
| `POST` | Create ticket; create comment |
| `PUT` | Full replace of updatable ticket fields (not status) |
| `PATCH` | Partial update for status only |

## Status codes (as implemented)

| Code | When |
|------|------|
| 200 | Successful get/list/field update/status update |
| 201 | Ticket or comment created |
| 400 | Validation failure; malformed body; unknown enum value |
| 404 | Ticket not found |
| 409 | Invalid status transition |
| 500 | Unexpected server error |

## Request / response format

- CamelCase JSON fields (`createdAt`, `updatedAt`, `ticketId`).
- List responses: ticket summaries (`comments` omitted / null).
- Detail responses: include `comments` array (oldest-first from entity ordering).
- Error body:

```json
{
  "message": "Human-readable summary",
  "errors": [
    { "field": "title", "message": "must not be blank" }
  ]
}
```

`errors` may be null/omitted for non-field failures.

## Validation errors

- Triggered by `@Valid` on DTOs.
- Message typically `"Validation failed"` plus field details.
- Invalid transition message pattern: `Invalid status transition from {FROM} to {TO}`.

## Naming

- Paths: `/api/tickets`, `/api/tickets/{id}`, `/api/tickets/{id}/status`, `/api/tickets/{id}/comments`.
- Query params: `q` (keyword), `status` (exact enum).
- Enum values uppercase: `OPEN`, `IN_PROGRESS`, `RESOLVED`, `CLOSED`, `CANCELLED`; priorities `LOW`, `MEDIUM`, `HIGH`.

## Search / filter / pagination

- Search: optional `q` — case-insensitive match on title **or** description.
- Filter: optional `status` — exact match; combinable with `q` (AND).
- **Pagination is not implemented** — list returns all matching tickets. Do not document or invent page/size params unless added later.
