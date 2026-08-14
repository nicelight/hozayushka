---
description: Working plan for FT-005 preset timer decomposition.
status: active
last_updated: 2026-08-10
---
# FT-005 — Plan

## Outcome

Create one clean, schema-backed task surface for the accepted preset timer
configuration outcome. The task will cover the validated owner-local preset
projection, persistence, labels, colors and selected/active integration while
leaving countdown lifecycle and overdue behavior to later features.

## Bounded task shape

- One task: `TASK-007-T3-FT-005-W6`.
- Primary owner: `Settings & Location`.
- Direct predecessor: `TASK-006-T3-FT-004-W5`; Foundation is transitive through
  the approved chain.
- Tier: `T3`, because the outcome combines Android user-facing state,
  owner-local mutable persistence and cross-slice runtime contracts.
- Authoritative indexed status: `done`; terminal evidence remains unchanged.

## Acceptance closure

All four FT-005 ACs are owned by the single task. `REQ-011` is the governing
requirement. AC-001's active-timer clause is bounded to proving that preset
configuration does not create parallel active state; Timer & Alert retains
full one-active-timer lifecycle authority for FT-006. No accepted AC is left
without an owner, and no FT-006–FT-009 AC is adopted.

## Execution-path sanity check

The plausible path is: Settings & Location validates and persists three preset
definitions → Main Display renders their labels/colors and reads selected/active
presentation → Timer & Alert consumes the validated projection without a second
configuration-created active timer → host persistence/projection checks and
project-native build/test gates prove the observable result. This is one
cohesive independently verifiable outcome; no independent prerequisite,
rollout unit or material risk requires a second task.

## Decision boundary

Existing subject-based canonical specs are sufficient. No new or feature-owned
spec is created, no global architecture choice is reopened, and Planning
Revision is reconciled to `2`. Exact persistence implementation, class split and
filename identity remain executor discretion within the accepted code roots.

## Revision-2 reconciliation

Queue action is `reconciled`. `TASK-007-T3-FT-005-W6` remains the sole FT-005
task with its existing T3/W6 identity, dependency, `done` status and evidence.
Provider migration changes no preset acceptance, so no follow-up task is
created. Exact next owner is fresh `/review-tasks-plan --all`.
