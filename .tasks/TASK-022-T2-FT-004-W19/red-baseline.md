---
description: Attempt-1 claim-linked RED baseline for TASK-022-T2-FT-004-W19.
status: supporting
---
# Claim-linked RED — TASK-022-T2-FT-004-W19

## Attempt and basis

- attempt: 1
- command: `./gradlew testDebugUnitTest --tests com.hozayushka.app.ForecastSessionTest.selectedProvidersKeepTenDayHorizonAndOpenWeatherUsesHonestEightPlusTwoProjection --no-daemon`
- cwd: `/home/serg/Projects/Mobile_APPS/hozayushka`
- input basis: pre-production-change W19 test probe added to the existing dirty upstream W18/W20 baseline; synthetic in-memory provider data only; no credentials, network, device or runtime state.
- completed: 2026-08-12 (local Asia/Dushanbe session)

## Observed RED

The new claim-specific probe compiled but failed at `ForecastSessionTest.kt:289`
because the current long-term projection kept the session `CLOSED` for the
selected OpenWeather eight-record fixture. Current production code at
`WeatherCapability.kt:589-595` requires exactly ten daily records and requires
all ten day/night values and conditions to be filled. Therefore the accepted
OpenWeather 8+2 projection, explicit empty tail positions, and its provider-
specific complete threshold were not present.

This RED covers FT-004-AC-001/002/005/006 and REQ-010, REQ-022, REQ-026. It is
an honest pre-change behavior failure, not setup, syntax, unrelated or
artificial failure.

## Concise command result

- Gradle compiled the probe and ran one test.
- Result: `1 test completed, 1 failed`; task exited non-zero (`BUILD FAILED`).
- Failure report: `app/build/reports/tests/testDebugUnitTest/index.html`.

## Boundary

- No production behavior had been changed before this RED.
- No forbidden scope, live provider, secret, emulator/device, adb, QEMU or
  external network was touched.
