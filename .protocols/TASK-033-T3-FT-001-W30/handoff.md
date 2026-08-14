---
description: Template for .protocols/TASK-NNN-TN-FT-NNN-WN/handoff.md (what the next agent needs).
status: active
---
# Handoff — TASK-033-T3-FT-001-W30

## Summary
- No production/test behavior changed. Fresh W30 baseline evidence proves the
  accepted host claims, so RED_NOT_APPLICABLE was recorded rather than an
  artificial break.
- Required host gates passed; target/device remains DEFERRED.

## Where to look
- key files:
  - `.tasks/TASK-033-T3-FT-001-W30/red-baseline.md`
  - `.tasks/TASK-033-T3-FT-001-W30/geometry.json`
  - `.tasks/TASK-033-T3-FT-001-W30/weather-slot-matrix.json`
  - `.tasks/TASK-033-T3-FT-001-W30/preset-visual-receipts.json`
  - `.tasks/TASK-033-T3-FT-001-W30/red-green-contact-sheet.svg`
  - `.tasks/TASK-033-T3-FT-001-W30/visual-rubric.md`
  - `.tasks/TASK-033-T3-FT-001-W30/boundary-static-review.md`
  - `.tasks/TASK-033-T3-FT-001-W30/host-gates.md`
  - `.tasks/TASK-033-T3-FT-001-W30/target-device.md`
  - `.tasks/TASK-033-T3-FT-001-W30/claim-linked-receipts.md`
  - `.tasks/TASK-033-T3-FT-001-W30/TASK-033-T3-FT-001-W30-S-EXE-final-report-code-01.md`
- advisory `touched_files` deviations and rationale: none; W30 behavior delta is none.
- hard write-boundary compliance: yes; only task-local evidence/protocol files were added/updated.

## How to run / verify
- gates:
  - `./gradlew clean assembleDebug` → 0
  - `./gradlew :app:testDebugUnitTest --tests com.hozayushka.app.DisplayProjectionTest` → 0
  - `./gradlew testDebugUnitTest` → 0
  - `./gradlew lintDebug` → 0
  - `git diff --check` → 0
- claim-linked RED/GREEN evidence: attempt 1 in `progress.md`; exact fresh
  probe and accepted RED_NOT_APPLICABLE reason in `red-baseline.md` and
  `claim-linked-receipts.md`.
- current-attempt reuse candidate locators: none proposed; receipts are
  supporting-only because the worktree has broad pre-existing changes.
- superseded/supporting-only receipt locators: current supporting receipt is
  `.tasks/TASK-033-T3-FT-001-W30/TASK-033-T3-FT-001-W30-S-EXE-final-report-code-01.md`.

## Known issues
- Device/runtime evidence is DEFERRED by authorization boundary. The 1280x720
  clock measurement has a 0.000015 px floating-point excess but passes the
  accepted 0.01 tolerance and has no observed clipping.

## Follow-ups
- Recommended next action: `/verify TASK-033-T3-FT-001-W30`; do not run
  `/red-verify` or `/mb-sync` inside `/exe`.
