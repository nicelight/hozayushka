# Context — TASK-014-T3-FT-001-W11

## Purpose
Repair only the confirmed FT-001 Main Display allocation regression so the existing city/status/hint rows are measurable and the accepted selected-city hold route is reachable on the authorized generic API 35 emulator.

## Execution Attempt
- attempt: 1
- started: 2026-08-08T14:43:11+05:00
- state: completed for handoff; functional `/verify` passed and required `/red-verify` exposed the countdown city-hold defect

## Execution Attempt
- attempt: 2
- started: 2026-08-08T15:23:37+05:00
- state: completed for handoff; functional `/verify` passed and required `/red-verify` exposed delayed Settings navigation after city double tap

## Execution Attempt
- attempt: 3
- started: 2026-08-08T16:04:32+05:00
- state: completed for handoff; scheduler-authorized final retry, no fourth attempt permitted

## Inputs (what drives this task)
- Task record: `.memory-bank/tasks/TASK-014-T3-FT-001-W11.task.json`
- Task index: `.memory-bank/tasks/index.json`
- Specs: `.memory-bank/features/FT-001-main-clock-display.md`, direct canonical files in task `normative_inputs`
- Acceptance criteria source: `FT-001-AC-002`, `FT-001-AC-005`; `REQ-002`, `REQ-004`, `REQ-023`

## Richer inputs
- Source Artifacts: `.protocols/RUNTIME-VERIFICATION/tecno-pova-6-api35-review.md`, `.protocols/RUNTIME-VERIFICATION/adb-snippets-redacted.md`, `.protocols/RUNTIME-VERIFICATION/18-safe-normal-main.png`
- Normative Inputs: System Architecture AD-001/AD-003, Boundary Map modules/graph/ownership, Main Display to Settings and Location, Display Runtime Boundary, Runtime Verification supplementary target
- Constraints / Invariants: preserve existing Main Display composition, gesture semantics, owners and graph; no FT-006/FT-008/FT-009/FT-000 behavior change
- Verification Targets: task card `verification_targets` and `evidence_required`

## Loaded context set (what was read)
- `AGENTS.md`
- `.memory-bank/roles/implementer.md`
- `.agents/skills/exe/SKILL.md`
- `.memory-bank/tasks/TASK-014-T3-FT-001-W11.task.json`
- `.memory-bank/features/FT-001-main-clock-display.md`
- direct task-linked canonical SDD specs and tier-policy sections
- authoritative runtime review, redacted ADB snippets and safe-normal screenshot
- current `DisplayCapability.kt`, `DisplayProjectionTest.kt`, and `MainActivity.kt`

## Decisions / assumptions
- Attempt-1 decision retained: one Main Display-owned header allocation correction restored non-zero rows.
- Retry correction basis: `.protocols/TASK-014-T3-FT-001-W11/red-verification.md` and `.tasks/TASK-014-T3-FT-001-W11/TASK-014-T3-FT-001-W11-S-RED-VERIFY-final-report-docs-01.md` prove that `activeTimerTouchListener` consumes the visible city's 800 ms hold during countdown.
- Attempt-2 decision: add only city-specific active-countdown gesture routing so long hold reaches the existing FT-001 Settings callback while single/double taps keep using the accepted Timer & Alert commands; non-city touch routing remains unchanged.
- Attempt-3 retry basis: `.protocols/TASK-014-T3-FT-001-W11/red-verification.md` and `.tasks/TASK-014-T3-FT-001-W11/TASK-014-T3-FT-001-W11-S-RED-VERIFY-final-report-docs-01.md` now record the attempt-2 semantic failure: city double tap cancels by 250 ms but the detector loses its terminating event after Timer reaches `idle`, allowing delayed Settings navigation by 750 ms without a hold.
- Attempt-3 bounded correction: keep the active-countdown city detector's already-owned touch stream through its terminal `ACTION_UP`/`ACTION_CANCEL` even when its double-tap callback has just cancelled the Timer; idle city gestures, genuine countdown hold, non-city timer gestures and all public contracts remain unchanged.
- Dirty baseline: target source/test files already contain unrelated uncommitted product work; preserve it. Pre-task SHA-256: `DisplayCapability.kt` `869a6bbfa7222186fdea09b379c6a61ab38d5c94fbb1e2759ffa176978baf23b`; `DisplayProjectionTest.kt` `2ac34a121f7f311a036abe6efb4d90f7e00b5d9d029d9ec6787206ea6b7a88af`; `MainActivity.kt` `737c4898b2e11063e3b7a586b998137803593585211f69e79c75046890819059`.

## Commands run / environment notes
- Read-only preflight: task/index/dependency/review/backbone/proof-path/runtime evidence/source inspection → runnable.
- Scheduler checkpoint already selected TASK-014 and latest strict doctor is PASS; scheduler files remain forbidden for writes.
- Attempt 2: focused regression, clean build, 53-test full suite, static diff, exact generic-emulator identity/current-APK install, active-countdown public interaction sequence and final-state checks all passed.
- Attempt 3: focused delayed-navigation regression, clean build, 54-test full suite, static diff, exact generic-emulator identity/current-APK install/hash, full public layout/gesture sequence and safe final-state checks all passed.

## Open questions / blockers
- None. The exact stream-lifetime correction preserved both accepted city hold and timer double tap without a product/contract decision or private-state access.

## Next session
- Start by reading: `context.md`, `plan.md`, `progress.md`
- Next action: `/verify TASK-014-T3-FT-001-W11` using the completed attempt-3 handoff.
