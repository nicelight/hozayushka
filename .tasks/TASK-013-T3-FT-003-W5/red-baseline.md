---
description: Claim-linked pre-implementation baseline for TASK-013-T3-FT-003-W5.
status: active
---
# Pre-implementation RED / baseline — Attempt 1

## Claim mapping

- `FT-003-AC-001 / REQ-009`: Today entry/completeness/fallback.
- `FT-003-AC-004 / REQ-009`: shared three-second/tap/double-tap/hold-release
  lifecycle.
- `FT-003-AC-003 / REQ-009 / REQ-022`: regression-only consumption of the
  repaired eight-slot read model through Forecast Sessions and shared card
  presentation.
- `FT-003-AC-005 / REQ-009 / REQ-026`: regression-only unavailable predicate,
  no session/fabricated slot and exact fallback.

## Baseline command

```text
./gradlew testDebugUnitTest --tests 'com.hozayushka.app.ForecastSessionTest.completeRedactedFixtureMapsEightSlotsIntoTwoRowsAndUsesCityTimezone' --tests 'com.hozayushka.app.ForecastSessionTest.incompleteHourlyDataStaysUnavailableAndDoesNotCreateSession' --tests 'com.hozayushka.app.ForecastSessionTest.sharedSessionTimingAndGesturesFollowAcceptedTransitions' --tests 'com.hozayushka.app.ForecastSessionTest.holdKeepsSessionOpenBeyondOriginalDeadlineAndReleaseClosesImmediately'
```

- cwd: `/home/serg/Projects/Mobile_APPS/hozayushka`
- exit code: `0`
- result: `BUILD SUCCESSFUL`; 4 selected tests passed (the Gradle task
  reported 22 actionable tasks with 1 executed and 21 up-to-date).
- source basis: existing dirty worktree at attempt start; no production file
  was changed by this attempt before the probe.

## Honest baseline observation

- `AC-001`, `AC-004` and `AC-005` were already behaviorally GREEN on the
  current scaffold: complete entry, 3000 ms/tap/hold transitions and exact
  unavailable rejection were observable before this task's change.
  Per the T3 RED/GREEN contract, this pre-implementation GREEN is preserved
  and no artificial production change is made for those claims.
- `AC-003` had no fresh task-specific assertion that the repaired normalized
  projection is assembled into the Forecast Sessions public snapshot with the
  shared illustration/material/no-pressure presentation inputs. This is the
  accepted regression-only RED (`absent assembled projection-to-card consumer
  assertion`), not a fabricated runtime failure.

## Planned correction

Add one deterministic in-memory regression assertion at the existing public
`WeatherCapability → ForecastSessionCapability` boundary. It will consume the
TASK-012 normalized projection, open the hourly session, and compare its two
rows of four against the shared presentation inputs without touching provider
normalization or provider adapter code.
