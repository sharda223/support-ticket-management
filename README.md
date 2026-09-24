# Support Ticket Management System

A full-stack Support Ticket Management System built as part of the SE/SSE assignment using Spec-Driven Development (SDD).

## Tech Stack

### Backend
- Java 21
- Spring Boot
- Spring Data JPA / Hibernate
- PostgreSQL
- REST APIs
- Maven

### Frontend
- React
- Vite
- JavaScript
- React Router
- CSS

## Features

- Create support tickets
- List all tickets
- View ticket details
- Update ticket title and description
- Update priority and assignee
- Add comments
- Search tickets by keyword
- Filter tickets by status
- Ticket status transitions
- Backend input validation
- Meaningful API/UI error handling
- Persistent database storage

## Ticket Status Flow

```text
OPEN
  ↓
IN_PROGRESS
  ↓
RESOLVED
  ↓
CLOSED

OPEN ─────→ CANCELLED
IN_PROGRESS → CANCELLED
