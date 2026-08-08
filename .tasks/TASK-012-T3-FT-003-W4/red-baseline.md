---
description: Claim-linked pre-implementation RED for TASK-012-T3-FT-003-W4.
status: active
---
# RED baseline — TASK-012-T3-FT-003-W4

## Attempt

- attempt: 1
- source basis: repository revision
  `a93e46118f0f0b90e311b6174e3f5a8ed89fef` plus the pre-existing dirty
  worktree and the new task-scoped regression probe in
  `WeatherContextTest.kt`.
- isolation: in-memory Weather Context, synthetic provider/request, two-day
  deterministic hourly list; no live request, key, or external side effect.

## Claim mapping and result

- `FT-003-AC-002 / REQ-009`: RED. The supported 48-record provider shape
  failed at `assertNotNull(weather.refresh(...))`; current production gate
  requires raw hourly cardinality to equal eight, so the valid full-day result
  is rejected before normalization.
- `FT-003-AC-003 / REQ-022`: RED. The same rejected refresh prevents proving
  selected-city timezone labels and the following-day boundary.
- `FT-003-AC-005 / REQ-009 / REQ-026`: the missing selected time,
  temperature, and illustration-input variants remain rejected by the current
  completeness path; this baseline preserves the required failure behavior
  while the valid-shape acceptance is repaired.

## Exact probe

- command: `./gradlew testDebugUnitTest --tests 'com.hozayushka.app.WeatherContextTest.supportedFullDayHourlyPayloadNormalizesAcceptedCityLocalSlots' --tests 'com.hozayushka.app.WeatherContextTest.selectedHourlyRequiredFieldMissingKeepsProjectionUnavailable'`
- cwd: `/home/serg/Projects/Mobile_APPS/hozayushka`
- result: exit `1`; `2 tests completed, 1 failed`; valid full-day test failed at
  `WeatherContextTest.kt:188` on `assertNotNull(refresh)`.
- output/report: `app/build/reports/tests/testDebugUnitTest/index.html` and
  `app/build/test-results/testDebugUnitTest/TEST-com.hozayushka.app.WeatherContextTest.xml`.

The failed test is the accepted behavior probe, not an artificial break or
setup failure. This RED is retained and is paired with the fresh
claim-equivalent GREEN in `green-fixture.md`.
