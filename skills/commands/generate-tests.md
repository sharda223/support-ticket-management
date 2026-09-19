# Command: Generate Tests

Use this prompt in Cursor when adding or extending automated tests.

```
Generate or extend tests for the Support Ticket Management System using the existing strategy.

Follow:
- spec/testing.md
- spec/test-strategy.md
- rules/testing.md
- spec/state-machine.md
- spec/api-contract.md

Constraints:
- Do not invent product features
- Mirror existing styles: JUnit/MockMvc for backend; node --test for frontend helpers
- Backend runtime DB is PostgreSQL; tests may use H2 test profile as already configured
- No secrets in tests
- Prefer minimal new dependencies

Required coverage themes:
1. Valid status transitions
2. Invalid status transitions (409, status unchanged)
3. Persistence of ticket fields, status, comments
4. Validation / 404 error paths
5. Frontend: transition helper and error formatting if touching those modules

Output:
- List of test cases to add/update
- File paths
- Code for the tests
- How to run: `cd backend && mvn test` and/or `cd frontend && npm test`
```

## Notes

- Extend `TicketStateMachineTest` / `TicketApiIntegrationTest` before creating parallel frameworks.
- Do not replace H2 test profile with Testcontainers unless Docker is available and the user requests it.
