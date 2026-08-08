---
description: Execution context for TASK-012-T3-FT-003-W4.
status: active
---
# Context — TASK-012-T3-FT-003-W4

## Purpose

Repair the Weather Context/provider normalization defect so a supported
48-record full-day response produces the existing eight-slot city-local
hourly projection, while retaining all-or-nothing selected-slot validation.

## Execution Attempt

- attempt: 1
- started: 2026-08-08 03:00 Asia/Dushanbe

## Inputs

- Task record: `.memory-bank/tasks/TASK-012-T3-FT-003-W4.task.json`
- Task index: `.memory-bank/tasks/index.json`
- Feature/REQ: `.memory-bank/features/FT-003-hourly-forecast.md`, `REQ-009`, `REQ-022`, `REQ-026`
- Historical defect: `.memory-bank/bugs/TASK-005-hourly-provider-shape.md`
- Historical semantic report: `.tasks/TASK-005-T3-FT-003-W4/TASK-005-T3-FT-003-W4-S-RED-VERIFY-final-report-docs-01.md`

## Normative basis

- `.memory-bank/contracts/weather-provider.md#ft-003-hourly-mapping`
- `.memory-bank/contracts/boundary-map.md#dependency-graph`
- `.memory-bank/contracts/capability-interfaces.md#forecast-sessions-to-weather-context`
- `.memory-bank/contracts/capability-interfaces.md#ft-003-forecast-data-contract`
- `.memory-bank/domains/local-data.md#ft-003-hourly-forecast-records`
- `.memory-bank/testing/runtime-verification.md#deterministic-host-side-checks`
- `.memory-bank/workflows/tier-policy.md#claim-linked-red--green-for-t2t3`

## Preflight result

- `TASK-012` is `ready`, tier `T3`, feature `FT-003`, wave `W4`.
- Dependency `TASK-004-T3-FT-002-W3` is `done`.
- Global Backbone Planning Revision is `1`; latest FT-003 review is
  `APPROVE` with `REVIEWED_PLANNING_REVISION: 1`.
- `TASK-005` remains historical `failed` and is not modified or depended on.
- No non-empty hard write boundary is configured; semantic forbidden scope is
  preserved.
- Initial source basis is repository revision
  `a93e46118f0f0b90e311b6174e3f5a8ed7d89fef` plus the pre-existing dirty
  worktree. Relevant Weather/test files were already modified by prior work;
  unrelated changes are preserved.

## Open questions / blockers

- None at preflight. Target device/emulator evidence is deferred and
  non-blocking for this host-provable provider-shape repair.

## Next session

- Start by reading `context.md`, `plan.md`, `progress.md`.
- Next action: execute the claim-specific pre-implementation RED probe.
