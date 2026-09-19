# Command: Review Code

Use this prompt in Cursor when reviewing application changes against Spec-Driven Development artifacts.

```
Review the current Support Ticket Management System code against the approved specs and rules.

Scope:
- Backend under backend/ and/or frontend under frontend/ as changed
- Do not invent new features

Check against:
- spec/requirements.md
- spec/state-machine.md
- spec/api-contract.md (or spec/api.md)
- spec/data-model.md
- spec/frontend.md / spec/ui-flow.md
- rules/implementation.md
- rules/java-springboot.md
- rules/api-standards.md
- rules/testing.md

Verify:
1. Layered architecture respected (controller thin; state machine in service)
2. Only allowed status transitions; invalid → 409 and no DB change
3. Validation and ErrorResponse behaviour
4. No secrets committed
5. No auth unless explicitly required
6. Tests cover transitions/persistence for backend changes; frontend helpers/build if UI changed
7. API paths/methods match the contract

Output:
- Findings (severity: blocker / major / minor)
- Spec references for each finding
- Suggested minimal fixes
- Explicit “looks compliant” areas
```

## Notes

- Prefer reading actual controllers/services/tests over summarizing from memory.
- Do not modify code unless the user asks for fixes after the review.
