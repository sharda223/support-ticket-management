# Frontend Specification

## 1. Stack

- **React** with **Vite**
- Consumes the REST API defined in `api.md`
- No authentication UI

## 2. Screens / views

### 2.1 Ticket list

- Display tickets (id, title, status, priority, assignee, updated time at minimum)
- **Search** input bound to keyword query (`q`)
- **Status filter** control (all statuses + “All”)
- Action to open create-ticket flow
- Clicking a ticket navigates to / opens ticket detail

### 2.2 Create ticket

- Form fields: title, description, priority, assignee (optional)
- Submit calls `POST /api/tickets`
- On success: navigate to detail or refresh list
- On failure: show API validation/error messages

### 2.3 Ticket detail

- Show full ticket fields: title, description, status, priority, assignee, timestamps
- Show comments list
- **Edit fields:** title, description, priority, assignee → `PUT /api/tickets/{id}`
- **Change status:** control offering only **valid next statuses** for the current status (UX aid). Backend remains the source of truth; if an invalid transition is attempted, show the API error
- **Add comment:** text input + submit → `POST /api/tickets/{id}/comments`
- On ticket not found: show meaningful not-found message

## 3. Error handling

| Requirement | Detail |
|-------------|--------|
| Meaningful errors | Display `message` and, when present, field-level `errors` from `ErrorResponse` |
| Network failure | Show a clear “could not reach server” (or equivalent) message |
| No silent failure | Failed create/update/status/comment must not appear successful |
| Invalid transition | Show backend message (current → requested) |

## 4. UX rules aligned to state machine

- For terminal statuses (`CLOSED`, `CANCELLED`), disable or hide status-change actions
- For non-terminal statuses, only present allowed next statuses in the UI when practical
- Do not invent statuses in the UI beyond the five defined statuses

## 5. Configuration

- API base URL via environment variable (e.g. `VITE_API_BASE_URL`)
- Do not hard-code secrets (none expected for this app)

## 6. Out of scope

- Login / signup
- Role-based views
- Rich text editors (plain text is sufficient)
- Real-time websockets
