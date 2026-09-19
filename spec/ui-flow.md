# UI Flow

Documents the **actual** React Router routes and user flows in `frontend/`.

## Routes

| Path | Page component | Purpose |
|------|----------------|---------|
| `/` | `TicketListPage` | List, search, filter |
| `/tickets/new` | `CreateTicketPage` | Create ticket |
| `/tickets/:id` | `TicketDetailPage` | View, edit, status, comments |

Global nav in `App.jsx`: brand home, “All tickets”, “New ticket”.

API base: `VITE_API_BASE_URL` (default `http://localhost:8080`).

---

## Flow: Ticket list

1. User opens `/`.
2. App loads tickets via `GET /api/tickets` (optional `q`, `status` from URL search params).
3. Table shows id, title, status badge, priority, assignee, updated time.
4. Click title → navigate to `/tickets/:id`.
5. “New ticket” → `/tickets/new`.

### Search / filter

- Search box + submit updates `?q=`
- Status `<select>` updates `?status=` (`All statuses` clears param)
- Both combine (AND) when present

### States

- **Loading:** “Loading tickets…”
- **Error:** `ErrorBanner` with API/network message
- **Empty:** “No tickets found” guidance

---

## Flow: Create ticket

1. User opens `/tickets/new`.
2. Form: title, description, priority, optional assignee.
3. Submit → `POST /api/tickets`.
4. **Success:** navigate to `/tickets/{id}`.
5. **Failure:** stay on form; show `ErrorBanner` (validation/network).

---

## Flow: Ticket details

1. User opens `/tickets/:id`.
2. Load via `GET /api/tickets/{id}`.
3. Header shows title, status badge, priority, assignee, timestamps.

### Edit ticket fields

- Form: title, description, priority, assignee.
- Submit → `PUT /api/tickets/{id}`.
- Success message or error banner; does not change status.

### Status transition

- Options from client helper `getNextStatuses(current)` matching backend rules.
- Terminal (`CLOSED`, `CANCELLED`): no transition control; explanatory text.
- Submit → `PATCH /api/tickets/{id}/status`.
- Backend remains source of truth; `409` shown if rejected.

### Comments

- List existing comments (or empty state).
- Add form → `POST /api/tickets/{id}/comments`, then reload detail.
- Success/error banners as appropriate.

### Not found / load error

- `ErrorBanner` + empty “Ticket not available” state; link back to list.

---

## Cross-cutting UI behaviour

| Concern | Behaviour |
|---------|-----------|
| Loading | `LoadingState` on list/detail fetch |
| Empty | `EmptyState` on list/comments |
| Errors | `ErrorBanner` using API `message` + field errors; network: “Could not reach the server…” |
| Success | Lightweight success banner on detail actions |

No login, roles, or additional routes exist.
