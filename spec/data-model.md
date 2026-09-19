# Data Model Specification

## 1. Database

- **Engine:** PostgreSQL
- **Persistence:** All ticket and comment data must remain available after application restart
- **Schema management:** Application-managed (e.g. Flyway/Liquibase or Spring DDL for development) — implementation choice later; schema must match this model

## 2. Entities

### 2.1 Ticket

| Column | Type | Nullable | Notes |
|--------|------|----------|-------|
| `id` | BIGINT (PK, generated) | No | Surrogate key |
| `title` | VARCHAR(200) | No | Non-blank |
| `description` | TEXT | No | Non-blank |
| `status` | VARCHAR(32) | No | Enum: `OPEN`, `IN_PROGRESS`, `RESOLVED`, `CLOSED`, `CANCELLED` |
| `priority` | VARCHAR(16) | No | Enum: `LOW`, `MEDIUM`, `HIGH` |
| `assignee` | VARCHAR(200) | Yes | Free-text; optional |
| `created_at` | TIMESTAMP WITH TIME ZONE | No | Set on create |
| `updated_at` | TIMESTAMP WITH TIME ZONE | No | Updated on any ticket field/status change |

**Create defaults:**

- `status` = `OPEN`
- `created_at` / `updated_at` = current timestamp

**Indexes (recommended for required features):**

- Index on `status` (filter)
- Optional index supporting search on `title` (implementation may use `ILIKE` / full-text later; MVP may scan)

### 2.2 Comment

| Column | Type | Nullable | Notes |
|--------|------|----------|-------|
| `id` | BIGINT (PK, generated) | No | Surrogate key |
| `ticket_id` | BIGINT (FK → ticket.id) | No | Parent ticket |
| `body` | TEXT | No | Non-blank |
| `created_at` | TIMESTAMP WITH TIME ZONE | No | Set on create |

**Relationship:** One ticket has many comments. Deleting tickets is out of scope; FK should prevent orphan comments if tickets were ever removed.

**Author:** Not stored (no authentication).

## 3. Constraints

| Constraint | Rule |
|------------|------|
| Ticket title | NOT NULL, length 1–200 after trim |
| Ticket description | NOT NULL, non-blank after trim |
| Ticket status | NOT NULL, must be a known status value |
| Ticket priority | NOT NULL, must be `LOW` \| `MEDIUM` \| `HIGH` |
| Comment body | NOT NULL, non-blank after trim |
| Comment ticket_id | Must reference an existing ticket |

## 4. Persistence requirements

1. Creating a ticket writes a row to `ticket`.
2. Updating fields or status updates the same row and refreshes `updated_at`.
3. Adding a comment inserts into `comment` linked by `ticket_id`.
4. After stopping and restarting the application (with the same PostgreSQL instance/data), previously created tickets and comments remain readable via the API.
5. Invalid status transitions must not modify the `ticket` row.

## 5. Out of scope for data model

- User / account tables
- Audit history / status change log tables (nice-to-have; **not required**)
- Attachments
- Soft-delete flags
