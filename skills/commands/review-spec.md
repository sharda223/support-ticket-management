# Command: Review Spec

Use this prompt in Cursor when reviewing or updating specification documents.

```
Review the Spec-Driven Development documents for the Support Ticket Management System.

Inspect:
- spec/*.md
- rules/*.md
- docs/prompt-history.md (process only)

Goals:
1. Consistency across requirements, state machine, data model, API, UI, and testing docs
2. Alignment with the actual implementation (do not propose features that are not implemented or approved)
3. No contradictory status transitions or HTTP codes
4. Clear separation of design specs (e.g. spec/api.md) vs implemented contract (spec/api-contract.md) if both exist
5. Out-of-scope items remain out of scope (auth, pagination, delete, etc. unless added)

Report:
- Contradictions or gaps between specs
- Gaps between specs and code (documentation debt only)
- Ambiguities that need a human decision
- Recommended doc-only edits (paths + brief change notes)

Do NOT modify application source code.
Do NOT invent new product requirements.
```

## Notes

- Treat approved user decisions and `rules/implementation.md` as constraints.
- Prefer updating docs to match code when code was already accepted, unless the user wants a behavioural change.
