---
description: Execution context for TASK-031-T3-FT-007-W28.
status: active
---
# Context — TASK-031-T3-FT-007-W28

## Task
- id: `TASK-031-T3-FT-007-W28`
- tier: `T3`
- feature / owned claim: `FT-007-AC-006 / REQ-015 / REQ-023`
- lifecycle at entry: `in_progress` (preserved; no status mutation)
- direct dependency: `TASK-030-T3-FT-006-W27` = `done`
- historical read-only context: W8 original overdue baseline; W23 audio integration; W27 active-countdown presentation.

## Execution Attempt
- attempt: 1
- started: `2026-08-13 00.41 +0500`
- resume basis: task was already `in_progress`; no prior W28 protocol existed, so this protocol was initialized from framework templates.

## Accepted outcome
Refine only Main Display overdue composition: dedicated no-weather/no-city/no-date/no-standard-card surface; stable full elapsed digits larger than idle and W27 active countdown where existing geometry permits; transparent neon circular backdrop keyed to activating preset; blinking `+`; preserve any-tap dismissal and Timer & Alert/lifecycle/audio behavior.

## Normative basis
- `.memory-bank/features/FT-007-overdue-alert.md#FT-007-AC-006`
- `.memory-bank/contracts/capability-interfaces.md#main-display-to-timer-and-alert`
- `.memory-bank/contracts/boundary-map.md#modules`, `#dependency-graph`, `#accepted-ownership-summary`
- `.memory-bank/contracts/platform-runtime.md#display-runtime-boundary`, `#timer-and-audio-runtime-boundary`, `#verification-route`
- `.memory-bank/states/lifecycle-map.md#timer-lifecycle`, `#timer-state-contract`
- `.memory-bank/testing/runtime-verification.md#deterministic-host-side-checks`, `#target-device-evidence`
- `.memory-bank/workflows/tier-policy.md#hard-write-boundary`, `#task-scoped-acceptance-evidence`, `#claim-linked-red--green-for-t2t3`, `#tier-obligations`
- `.memory-bank/tasks/TASK-031-T3-FT-007-W28.task.json` (complete indexed card)
- `.protocols/FT-007/plan.md`, `.memory-bank/tasks/plans/IMPL-FT-007.md`
- `.tasks/TASK-MB-REVIEW-TASKS-PLAN/TASK-MB-REVIEW-TASKS-PLAN-S-TASKS-FT-007-W28-POST-REPAIR-FRESH-final-report-docs-01.md` (`REVIEWED_PLANNING_REVISION: 2`, `FINAL_VERDICT: APPROVE`)

## Change boundary
- hard allowed outcome files: `app/src/main/kotlin/com/hozayushka/app/display/DisplayCapability.kt`, `app/src/test/kotlin/com/hozayushka/app/DisplayProjectionTest.kt`
- forbidden: timer/lifecycle/audio/settings/weather/resources/app wiring, historical W8/W23/W27 state/evidence, scheduler/checkpoint/terminal state, runtime target/device/adb/network/credentials/audio.
- pre-existing worktree: broad unrelated dirty and untracked state, including both allowed files; no execute receipt will be offered for reuse unless input provenance can be bounded.
