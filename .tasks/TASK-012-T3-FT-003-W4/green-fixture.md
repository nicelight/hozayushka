---
description: Claim-equivalent GREEN fixture evidence for TASK-012-T3-FT-003-W4.
status: active
---
# GREEN fixture result — TASK-012-T3-FT-003-W4

## Attempt and correction

- attempt: 1
- correction: Weather Context now validates every accepted key in the raw
  provider list without requiring raw cardinality eight, then stores only the
  accepted city-local keys in the normalized hourly read model. Public
  projection types and boundaries are unchanged.
- probe changes: none after RED; the same two compiled tests remain the
  claim-equivalent probe.

## Result

- command: `./gradlew testDebugUnitTest --tests 'com.hozayushka.app.WeatherContextTest.supportedFullDayHourlyPayloadNormalizesAcceptedCityLocalSlots' --tests 'com.hozayushka.app.WeatherContextTest.selectedHourlyRequiredFieldMissingKeepsProjectionUnavailable'`
- cwd: `/home/serg/Projects/Mobile_APPS/hozayushka`
- result: exit `0`; `BUILD SUCCESSFUL`; both claim tests passed.
- valid fixture: synthetic `Asia/Dushanbe` response with 48 records across
  `2024-01-02` and `2024-01-03`; result is exactly eight cards in order
  `06:00, 09:00, 12:00, 15:00, 18:00, 21:00, 00:00, 03:00`; the final two
  dates are `2024-01-03`; projection timezone is `Asia/Dushanbe`.
- invalid fixture variants: missing selected `09:00` time, selected `12:00`
  temperature, and selected `15:00` condition/illustration input each return
  `refresh == null` and `hourlyProjection == null`.
- test report: `app/build/test-results/testDebugUnitTest/TEST-com.hozayushka.app.WeatherContextTest.xml`.

This is executor self-attested supporting evidence; independent `/verify` and
T3 `/red-verify` remain required.
