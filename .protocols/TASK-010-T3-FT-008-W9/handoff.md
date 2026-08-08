---
description: Execution handoff for TASK-010-T3-FT-008-W9.
status: active
---
# Handoff — TASK-010-T3-FT-008-W9

## Summary

- PASS_FOR_HANDOFF: FT-008 local key/location, offline catalog/aliases,
  attribution, coordinate-bearing refresh seam and inline failure preservation
  are implemented in the registered owners. Current host/build/static/
  redacted evidence is recorded for `/verify`; no final T3 closure is claimed.

## Where to look

- key files:
  - `app/src/main/kotlin/com/hozayushka/app/settings/SettingsCapability.kt`
  - `app/src/main/kotlin/com/hozayushka/app/settings/LocationCatalog.kt`
  - `app/src/main/kotlin/com/hozayushka/app/weather/WeatherCapability.kt`
  - `app/src/main/kotlin/com/hozayushka/app/adapters/weather/WeatherProviderAdapter.kt`
  - `app/src/main/kotlin/com/hozayushka/app/app/FoundationRuntime.kt`
  - `app/src/main/kotlin/com/hozayushka/app/app/MainActivity.kt`
  - `app/src/main/assets/geonames/cities15000.tsv`
  - `app/src/main/assets/geonames/README.md`
  - `app/src/main/res/values/strings.xml`
  - `app/src/test/kotlin/com/hozayushka/app/SettingsLocationTest.kt`
  - `.tasks/TASK-010-T3-FT-008-W9/ft008-host-evidence-attempt-1.md`
- advisory `touched_files` deviations and rationale: `display/` was already
  dirty before this attempt and was not changed by FT-008; existing
  `weather/`, `app/`, resources and test roots were extended only for the
  accepted Settings/Weather integration outcome.
- hard write-boundary compliance: not set; semantic forbidden scope clear and
  no forbidden path was touched.

## How to run / verify

- gates: `./gradlew clean assembleDebug`, `./gradlew testDebugUnitTest`,
  `node scripts/mb-lint.mjs`, scoped static/redaction/boundary checks.
- claim-linked RED/GREEN evidence: `../../.tasks/TASK-010-T3-FT-008-W9/baseline-red-attempt-1.md`
  and `../../.tasks/TASK-010-T3-FT-008-W9/ft008-host-evidence-attempt-1.md`.
- current-attempt reuse candidate locators: none; receipts are supporting-only.
- superseded/supporting-only receipt locators: current attempt-1 receipts in
  `progress.md`, all marked `supporting-only`.

## Known issues

- Target device/emulator unavailable: Settings readability/navigation evidence
  is `DEFERRED`, non-blocking, with residual 1280×720 Android 11 risk. No
  runtime PASS is claimed.
- The bundled transformed GeoNames asset contains 34,079 ten-column rows;
  Khujand (`1514879`) is the default and Russian alias path is covered.

## Follow-ups

- Next owner: `/verify TASK-010-T3-FT-008-W9`; after functional PASS, required
  T3 semantic route is `/red-verify TASK-010-T3-FT-008-W9`.
- `/exe` did not run `/verify`, `/red-verify` or `/mb-sync`, and did not alter
  dependent statuses, scheduler checkpoint or terminal state.
