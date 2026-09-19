# Security and Secrets Specification

## 1. Requirements

| ID | Rule |
|----|------|
| SEC-01 | Do **not** commit credentials, secrets, API keys, passwords, private keys, or tokens |
| SEC-02 | Database username/password/URL for local and deployed environments must come from environment variables or an untracked local file (e.g. `.env` listed in `.gitignore`) |
| SEC-03 | Do not log passwords or connection strings containing credentials |
| SEC-04 | Error responses must not include stack traces or secret values in production-like configuration |
| SEC-05 | No authentication feature in this project (assignment does not require it) |

## 2. Allowed configuration pattern (illustrative)

Environment variables (names indicative, finalized at implementation):

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

Example `.env.example` (placeholders only, safe to commit):

```
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/support_tickets
SPRING_DATASOURCE_USERNAME=your_db_user
SPRING_DATASOURCE_PASSWORD=your_db_password
VITE_API_BASE_URL=http://localhost:8080
```

## 3. Git hygiene

- `.env`, credential files, and IDE run configurations containing passwords must be gitignored
- If a secret is ever committed: remove it, rotate the secret, and rewrite history only if necessary and explicitly approved

## 4. Out of scope

- OAuth, JWT, session login
- CSRF hardening beyond framework defaults for a simple JSON API (revisit if cookie auth is added later — it will not be for this assignment)
