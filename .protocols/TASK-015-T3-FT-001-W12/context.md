---
description: Execution context for TASK-015 bounded Main Display active-countdown dispatch repair.
status: active
---
# Context — TASK-015-T3-FT-001-W12

## Purpose

Repair only the Main Display-local active-countdown public touch dispatch so
the captured stream survives timer state changes and preserves the existing
city hold/Settings route and Timer & Alert gesture contracts.

## Execution Attempt
- attempt: 1
- started: 2026-08-08T23:24:18+05:00
- state: in progress

## Inputs (what drives this task)
- Task record: `.memory-bank/tasks/TASK-015-T3-FT-001-W12.task.json`
- Task index: `.memory-bank/tasks/index.json`
- Specs: `.memory-bank/features/FT-001-main-clock-display.md`, direct canonical files in task `normative_inputs`
- Acceptance criteria source: `FT-001-AC-005 / REQ-004`; REQ-013 is a regression guard only

## Richer inputs
- Source Artifacts: task card, W11 defect evidence, `.protocols/FT-001/plan.md`, `.protocols/FT-001/decision-log.md`
- Normative Inputs: System Architecture AD-001/AD-003/AD-004, Boundary Map modules/graph/ownership, Main Display capability contracts, Lifecycle Map timer state contract, Platform Runtime, Runtime Verification and T3 tier policy
- Constraints / Invariants: Main Display remains gesture-intent owner; Timer & Alert remains state/lifecycle/arithmetic/persistence/overdue owner; Settings & Location remains destination/state owner; no new module/edge/public contract/event boundary
- Verification Targets: task card `verification_targets` and `evidence_required`

## Loaded context set (what was read)
- `AGENTS.md`
- `.memory-bank/roles/implementer.md`
- `.agents/skills/exe/SKILL.md`
- `.memory-bank/tasks/TASK-015-T3-FT-001-W12.task.json`
- `.memory-bank/features/FT-001-main-clock-display.md`
- direct task-linked canonical SDD specs and tier/runtime policy
- W11 task-linked history, defect and semantic-fail evidence
- current `DisplayCapability.kt`, `DisplayProjectionTest.kt`, `MainActivity.kt`, and `TimerCapability.kt`

## Decisions / assumptions
- Decision: use one internal stateful Main Display dispatcher shared by root/background, weather cards, city and preset touch listeners; capture only at `ACTION_DOWN` and retain terminal delivery through `ACTION_UP`/`ACTION_CANCEL`.
- Decision: preserve idle city click/long-click routing and idle preset start by falling through when no active countdown stream was captured.
- Observation: the current generic-emulator run did not reproduce the historical W11 non-city double-tap failure; it is retained as a pre-implementation GREEN/supporting baseline, not backfilled RED.
- Decision: continue with the accepted Main Display integration outcome (shared dispatcher across all public surfaces) while attributing no claim-specific correction to the already-green baseline.

## Commands run / environment notes
- Read-only preflight and source/spec inspection → OK; runnable task confirmed.
- Task status `ready -> in_progress` recorded before the first prospective probe or production change.
- Current APK clean build/install/public probe → exact generic AVD reached; no credentials/private state used.

## Open questions / blockers
- None at preflight.

## Next session
- Start by reading: `context.md`, `plan.md`, `progress.md`
- Next action (one concrete step): obtain current-attempt claim-scoped RED before editing production behavior.
