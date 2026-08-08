---
description: Current attempt-2 executor gate receipts for TASK-004-T3-FT-002-W3.
status: active
---
# Gate Results — TASK-004-T3-FT-002-W3 — Attempt 2

## Receipt basis

- attempt: 2
- receipt_status: current
- cwd: `/home/serg/Projects/Mobile_APPS/hozayushka`
- repository_revision: `a93e46118f0f0b90e311b6174e3f5a8ed7d89fef`
- input_state_basis: existing FT-001/FT-002 working-tree changes and
  untracked task/protocol artifacts were preserved; attempt 2 changed only
  the FT-002 pressure material/trigger correction, its tests and task-owned
  protocol/evidence files. No task index, scheduler status, dependency or
  FT-003..FT-009 implementation file was changed.

## Mandatory build/unit receipts

### Clean Android debug build

- attempt: 2
- receipt_status: current
- claim: clean Android debug build required by TASK-004 gates after the
  AC-003/AC-004 production correction
- command: `./gradlew clean assembleDebug`
- cwd: `/home/serg/Projects/Mobile_APPS/hozayushka`
- exit_code: `0`
- completed_at: `2026-08-08 00:37 +05`
- evidence: `BUILD SUCCESSFUL`; debug APK SHA-256
  `8021c95748c902ee5408c78140400ecb61f7710513cdb2658b5eecfc1f349cac`.
  The only compiler warning is the pre-existing `MainActivity.onBackPressed`
  deprecation warning.

### Host weather-context behavior tests

- attempt: 2
- receipt_status: current
- claim: FT-002 AC-003/AC-004 trigger/material correction plus preserved
  weather-context claims pass deterministic host tests
- command: `./gradlew testDebugUnitTest`
- cwd: `/home/serg/Projects/Mobile_APPS/hozayushka`
- exit_code: `0`
- completed_at: `2026-08-08 00:37 +05`
- evidence: `BUILD SUCCESSFUL`; XML reports under
  `app/build/test-results/testDebugUnitTest/`: 15 tests, 0 skipped, 0
  failures, 0 errors. The test set includes persisted valid-location callback
  behavior and `LAUNCH`/`LOCATION_CHANGE`/30-minute `SCHEDULED` decisions.

## Host/static receipts

### Memory Bank lint

- attempt: 2
- receipt_status: current
- claim: task/protocol/evidence artifacts satisfy project Memory Bank lint
- command: `node scripts/mb-lint.mjs`
- cwd: `/home/serg/Projects/Mobile_APPS/hozayushka`
- exit_code: `0`
- completed_at: `2026-08-08 00:38 +05`
- evidence: `mb-lint passed (76 files)`.

### Boundary, trigger and shared-material checks

- attempt: 2
- receipt_status: current
- claim: AC-003 uses the existing shared pseudo-glass material for both
  temperature and pressure-arrow rendering; AC-004 production wiring uses the
  accepted Settings/Composition Root signals; no private-store/provider or
  timer/settings bypass is present
- command: boundary bypass scans; production `LOCATION_CHANGE`/
  `SCHEDULED`/`postDelayed` assertions; two shared `material.fillAlpha`
  rendering assertions; absence assertion for `alpha = 0.32f`; `git diff --check`
- cwd: `/home/serg/Projects/Mobile_APPS/hozayushka`
- exit_code: `0`
- completed_at: `2026-08-08 00:38 +05`
- evidence: `boundary/trigger/material checks passed`; no Main Display
  provider/private-weather-store bypass, no Weather Context timer/settings
  store bypass, and no hard-coded pressure-arrow alpha remained.

## Claim-equivalent retry GREEN

- AC-003: current display source resolves one `material` from
  `WeatherCardPresentation.pseudoGlass(0.45f)` and applies its fill alpha to
  both temperature and pressure-arrow TextViews; static host assertions pass.
- AC-004: `SettingsCapability` invokes the callback only after a changed
  location is persisted; `FoundationRuntime` routes it to Weather Context as
  `LOCATION_CHANGE`, and its lifecycle-owned runnable emits `SCHEDULED` every
  30 minutes while Weather Context retains the cadence/freshness decision.
- Attempt 1 receipts remain supporting-only. Independent `/verify` must inspect
  the current source and decide the functional verdict.
