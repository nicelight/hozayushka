---
description: Execution handoff for TASK-012-T3-FT-003-W4.
status: active
---
# Handoff — TASK-012-T3-FT-003-W4

## Summary

- Repaired Weather Context/provider normalization for supported 48-hour
  full-day input while preserving the existing eight-slot public projection and
  all-or-nothing completeness.
- Historical `TASK-005` failure and all its artifacts remain untouched.

## Where to look

- key files: `WeatherCapability.kt`, `WeatherContextTest.kt`; current attempt
  evidence under `.tasks/TASK-012-T3-FT-003-W4/`.
- advisory touched-file deviations: none known.
- hard write-boundary compliance: not set; semantic forbidden scope applies.

## How to run / verify

- gates: task `./gradlew clean assembleDebug` and
  `./gradlew testDebugUnitTest`; focused fixture/static/boundary/redaction
  receipts are recorded under `.tasks/TASK-012-T3-FT-003-W4/`.
- claim-linked RED/GREEN evidence: `.protocols/TASK-012-T3-FT-003-W4/progress.md`.
- current-attempt reuse candidate locators: none offered; broad dirty state and
  generated inputs prevent bounded reuse.
- superseded/supporting-only receipts: none.

## Known issues

- Target device/emulator is unavailable; record `DEFERRED` plus residual risk
  and never claim runtime PASS.

## Follow-ups

- Run `/verify TASK-012-T3-FT-003-W4` and, after functional PASS, per-task
  `/red-verify`. Do not run those routes from `/exe`.
