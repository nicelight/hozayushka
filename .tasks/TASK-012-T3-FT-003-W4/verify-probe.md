---
description: Verifier-owned functional and adversarial probe evidence for TASK-012-T3-FT-003-W4.
status: final
---
# Verifier-owned probe evidence — TASK-012-T3-FT-003-W4

## Functional outcome probe

- command: `./gradlew testDebugUnitTest --tests 'com.hozayushka.app.WeatherContextTest.supportedFullDayHourlyPayloadNormalizesAcceptedCityLocalSlots' --tests 'com.hozayushka.app.WeatherContextTest.selectedHourlyRequiredFieldMissingKeepsProjectionUnavailable'`
- result: exit `0`, `BUILD SUCCESSFUL`; both selected tests passed.
- fixture: synthetic/redacted 48 records (`2 × 24`) with `Asia/Dushanbe` API timezone.
- observed projection: exactly `06:00, 09:00, 12:00, 15:00, 18:00, 21:00, 00:00, 03:00`; the final two cards carry `2024-01-03`, and projection timezone is `Asia/Dushanbe`.
- invalid variants: selected `09:00` removed/replaced by `10:00`, selected `12:00` temperature null, and selected `15:00` condition/illustration input null; each produced `refresh == null` and `hourlyProjection == null`.

## Device-time independence probe

- command: `TZ=America/Los_Angeles ./gradlew --no-daemon testDebugUnitTest --tests 'com.hozayushka.app.WeatherContextTest.supportedFullDayHourlyPayloadNormalizesAcceptedCityLocalSlots' --tests 'com.hozayushka.app.WeatherContextTest.selectedHourlyRequiredFieldMissingKeepsProjectionUnavailable'`
- result: exit `0`, `BUILD SUCCESSFUL`; the same selected-city labels and boundary assertions passed under a different host/device timezone environment.

## Historical semantic probe rerun

- source: `.tasks/TASK-005-T3-FT-003-W4/ProviderHourlyShapeProbe.java` (read-only reuse of the prior redacted probe source; no historical artifact was changed).
- command: compile against current debug classes and run with synthetic `Asia/Dushanbe`, two-day/48-record payload.
- observed output: `provider_hourly_count=48`, `accepted_slots_present=true`, `refresh_result=NON_NULL`, `hourly_projection=NON_NULL`.
- temporary compiled output was isolated under `/tmp/hozayushka-verify-probe.*` and explicitly removed after execution.

## Gate observations

- `./gradlew clean assembleDebug`: exit `0`, `BUILD SUCCESSFUL`; APK SHA-256 `6e5f042862ff829a35630a6319b3da96a993b118ac528d8a4c9c82e2b8a92de7`.
- `./gradlew testDebugUnitTest`: exit `0`; `22` tests, `0` skipped, `0` failures, `0` errors.
- `node scripts/mb-lint.mjs`, `git diff --check`, boundary/static and credential-shape scans: exit `0`.
- `adb devices`: no authorized target; device evidence is `DEFERRED` and non-blocking. No runtime PASS is claimed.
