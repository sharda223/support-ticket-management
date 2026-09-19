# Prompt History

Structured record of AI-assisted Spec-Driven Development for this repository.

**Source of truth for prompts:** Cursor agent transcript  
`~/.cursor/projects/home-sharda-support-ticket-management/agent-transcripts/9c46d74b-323e-418b-9a7c-d6cde295265c/`

**Legend**

| Label | Meaning |
|-------|---------|
| **Exact** | Verbatim (or near-verbatim) user prompt from the transcript |
| **Reconstructed** | Summarized from tools/actions/outcomes when only process evidence exists (not a fabricated “fake chat”) |

No SpecStory `.specstory/history/` export was present in the repo at documentation time.

---

## Phase 1 — Requirement analysis

| | |
|--|--|
| **When** | 2026-09-18 ~23:07 IST |
| **Type** | Exact |
| **Prompt (summary)** | Act as senior engineer; SDD only; **do not implement**. Analyse assignment for Java 21 / Spring Boot / PostgreSQL / REST / React. Identify FRs, NFRs, business rules, lifecycle, valid/invalid transitions, validation, API, frontend, DB, errors, testing, security, ambiguities. Propose requirements before writing `spec/requirements.md`. |
| **AI outcome** | Repo was empty scaffold; assignment brief recovered from Assessment screenshots; proposed requirement breakdown **without** writing files yet. |
| **Human review** | Corrected course: next prompt asked for plan-only analysis again; later locked stack and feature decisions. |

---

## Phase 2 — Specification

| | |
|--|--|
| **When** | 2026-09-18 ~23:14–23:20 IST |
| **Type** | Exact |
| **Prompt (summary)** | Understand assignment + structure; no code; propose what to build + plan; **no file changes until approved**. Then: lock Java 21, PostgreSQL, React+Vite, features, state machine, tests, no auth; **create specs under `spec/` first**; show plan; no backend/frontend code yet. |
| **AI outcome** | Created `spec/requirements.md`, `state-machine.md`, `data-model.md`, `api.md`, `frontend.md`, `testing.md`, `security.md`, `README.md`. |
| **Human review** | User **approved** the specification set as defined. |

---

## Phase 3 — Plan / rules / tasks

| | |
|--|--|
| **When** | 2026-09-18 ~23:24 IST |
| **Type** | Exact |
| **Prompt (summary)** | Specs approved. Review `rules/`; implement **backend only** (Java 21, Spring Boot, PostgreSQL, JPA, REST, Ticket/Comment, validation, state machine, tests). No frontend. Run backend tests and report before frontend. |
| **AI outcome** | `rules/` was empty → added `rules/implementation.md` + `.cursor/rules/backend-spec.mdc`, then implemented backend. |
| **Human review** | Approved completed backend before frontend work. |

---

## Phase 4 — Implementation (backend)

| | |
|--|--|
| **Type** | Reconstructed (from implementation session) |
| **Work** | Scaffold Maven Spring Boot app; entities; `TicketStateMachine`; repositories; service; controller; DTOs; `GlobalExceptionHandler`; CORS; integration + unit tests. |
| **AI-assisted debugging (evidence)** | |
| | **JDK / compiler mismatch:** Maven failed with `release version 21 not supported` because Ubuntu `java-21-openjdk` provided a runtime **without** `javac`. Fix: set `JAVA_HOME` to Corretto **21.0.7** (full JDK with `javac 21.0.7`). *Note: this was a missing-javac / incomplete JDK issue, not a separate “javac 17” binary in the logged error text.* |
| | **Test DB:** Docker unavailable → used H2 on `test` profile only; PostgreSQL retained for runtime. |
| **Human review** | Backend approved after reported **49 tests passed**. |

---

## Phase 5 — Implementation (frontend)

| | |
|--|--|
| **When** | 2026-09-18 ~23:30 IST |
| **Type** | Exact |
| **Prompt (summary)** | Backend approved. Implement React+Vite frontend per `spec/frontend.md` (list, search/filter, create, detail, update, status UI, comments, errors, loading/empty, clean UI). Use existing APIs; don’t change backend unless blocked. Run build/tests; no secrets. |
| **AI outcome** | Manual Vite scaffold (latest `create-vite` failed on Node 20.9 / `styleText`); pages + API client; CSS. |
| **AI-assisted debugging (evidence)** | |
| | Vitest run executed tests successfully then crashed on worker teardown (`tinypool` / `Maximum call stack size exceeded`). **Correction:** switched frontend unit tests to Node’s built-in `node --test`; removed Vitest. |
| | `npm run build` succeeded after fixes. |
| **Human review** | Frontend accepted as delivered in-session. |

---

## Phase 6 — Testing

| | |
|--|--|
| **Type** | Reconstructed |
| **Commands used** | `mvn test` (backend); `npm test` + `npm run build` (frontend). |
| **Results (session)** | Backend: 49 passed. Frontend: 4 unit tests passed; production build OK. |
| **Review/correction** | Ensured invalid transitions assert HTTP 409 and unchanged status; UI only offers valid next statuses. |

---

## Phase 7 — Review / documentation completion

| | |
|--|--|
| **When** | 2026-09-19 |
| **Type** | Exact (this documentation request) |
| **Prompt (summary)** | Create missing SDD artifacts (`rules/*`, `spec/architecture|api-contract|ui-flow|test-strategy`, `docs/prompt-history`, `skills/commands/*`). **Do not** modify application source; do not invent requirements; base on existing implementation/specs. |
| **AI outcome** | Documentation-only files added as requested. |
| **Human review** | Pending user acceptance of docs. |

---

## How AI output was reviewed and corrected

Examples of human/agent correction during the project:

1. **No invented assignment PDF** — ignored unrelated `ticket.pdf` (rail ERS); used Assessment screenshots + user decisions.
2. **Spec-first gate** — user blocked implementation until specs were approved.
3. **Stack locks** — PostgreSQL + React/Vite (not H2/Next.js for the delivered app).
4. **No auth** unless assignment requires it.
5. **Compiler environment** — fixed JDK selection rather than lowering Java version.
6. **Frontend tooling** — abandoned broken Vitest pool teardown; kept simple Node tests + Vite build.
7. **Scope control** — backend and frontend implemented in separate approved steps; backend not changed for frontend.

## Items not evidenced in transcript

- **Port 8080 conflict:** not found in the Cursor transcript for this project. Not recorded as an AI debugging event here.
- **SpecStory auto-history files:** not present in the repository at documentation time; this file is the assignment-facing prompt history artifact.
