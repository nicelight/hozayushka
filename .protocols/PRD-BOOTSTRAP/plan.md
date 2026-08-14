---
description: Bootstrap plan for clarified PRD decomposition into product, requirements, epics and features.
status: completed
last_updated: 2026-08-10
---
# PRD Bootstrap Plan

## Scope

Reconcile the existing canonical L1–L3 product map with the Constitution-checked
provider-migration PRD. Preserve stable REQ/EP/FT/AC identities where their
accepted behavior remains the same, and keep architecture, testing policy, task
records and implementation plans outside this run.

## Source set

- [.memory-bank/prd.md](../../.memory-bank/prd.md)
- [.memory-bank/spec-backbone.md](../../.memory-bank/spec-backbone.md)
- [.memory-bank/spec-index.md](../../.memory-bank/spec-index.md)
- [.memory-bank/constitution.md](../../.memory-bank/constitution.md)
- [.memory-bank/user-scenarios.md](../../.memory-bank/user-scenarios.md)
- [.memory-bank/invariants.md](../../.memory-bank/invariants.md)
- [.memory-bank/contracts/boundary-map.md](../../.memory-bank/contracts/boundary-map.md)
- [.memory-bank/states/lifecycle-map.md](../../.memory-bank/states/lifecycle-map.md)

## Work units

1. Validate PRD and pre-PRD framing gates.
2. Reconcile provider selection, key applicability, cache/history identity,
   forecast capability and attribution requirements without reopening accepted
   product decisions.
3. Preserve the four value-oriented epics and nine product features.
4. Run a bounded boundary scan over FT-002, FT-003, FT-004 and FT-008.
5. Write RTM links to PRD acceptance/verification targets without selecting
   test levels or creating test artifacts.
6. Validate links, IDs, scope boundaries, and the truthful provider-migration
   route while preserving Global Backbone Planning Revision `1`.

## Boundary scan result

Completed over the accepted provider delta in FT-002, FT-003, FT-004 and
FT-008. Provider choice remains Settings behavior, provider-identified
cache/history remains Weather Context behavior, and hourly/long-term capability
differences remain in their existing forecast outcomes. No independently
acceptable outcome justifies a new EP or FT, and no operator-owned ambiguity was
found.

## Handoff

This reconciliation is complete when every REQ traces through one EP/FT row,
affected feature ACs close the accepted provider delta, and affected design
surfaces route to fresh `/spec-design` and later `/feature-to-tasks`. The
existing Global Backbone remains `complete` at Planning Revision `1`; this run
does not claim that its stale provider contract is repaired.
