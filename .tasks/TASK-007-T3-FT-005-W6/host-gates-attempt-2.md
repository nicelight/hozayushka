---
description: Current retry host gate evidence for TASK-007-T3-FT-005-W6.
status: supporting
---
# Host gates — attempt 2

- attempt: `2`
- receipt_status: `supporting-only`
- completed_at: `2026-08-08 05:54 +0500`

## Input basis

- repository `HEAD`: `a93e46118f0f0b90e311b6174e3f5a8ed7d89fef`
- relevant task-owned correction: `SettingsCapability.kt` and
  `TimerPresetTest.kt`; broad pre-existing workspace changes remain preserved
  as recorded in the execution context.
- no target device/emulator attached.

## Required gates

- `./gradlew clean assembleDebug` — exit `0`; `BUILD SUCCESSFUL`; APK
  SHA-256 `46f0e5ae97a88d64777821e29e80d0920b1e8b21c682f2b8e3fd9cdfbb7eb940`.
- `./gradlew testDebugUnitTest` — exit `0`; XML reports show
  `DisplayProjectionTest=5`, `ForecastSessionTest=9`, `FoundationProbesTest=3`,
  `TimerPresetTest=5`, `WeatherContextTest=10`; total `32/32`, failures `0`,
  errors `0`. Artifact directory:
  `app/build/test-results/testDebugUnitTest/`.
- `git diff --check` — exit `0`.
- `node scripts/mb-lint.mjs` — exit `0`; `mb-lint passed (77 files)`.

The clean build emitted only the pre-existing deprecated-override warning in
`MainActivity.kt`; it did not fail the gate and is outside this correction.

## Receipt disposition

These are current attempt-2 execution receipts for handoff support, not
independent verifier evidence and not proposed `/verify` reuse candidates;
the workspace has broad dirty/untracked and generated inputs.
