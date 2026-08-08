---
description: Claim-linked GREEN fixture evidence for TASK-013-T3-FT-003-W5.
status: active
---
# GREEN fixture result — Attempt 1

## Current claim-equivalent command

```text
./gradlew testDebugUnitTest --tests 'com.hozayushka.app.ForecastSessionTest.completeReadModelIsConsumedByHourlySessionWithSharedCardInputs' --tests 'com.hozayushka.app.ForecastSessionTest.incompleteHourlyDataStaysUnavailableAndDoesNotCreateSession' --tests 'com.hozayushka.app.ForecastSessionTest.sharedSessionTimingAndGesturesFollowAcceptedTransitions' --tests 'com.hozayushka.app.ForecastSessionTest.holdKeepsSessionOpenBeyondOriginalDeadlineAndReleaseClosesImmediately'
```

- cwd: `/home/serg/Projects/Mobile_APPS/hozayushka`
- exit code: `0`; `BUILD SUCCESSFUL`; 4 selected tests passed.
- fixture: in-memory synthetic `Asia/Dushanbe` provider data; no live request,
  API key or external side effect.
- test report: `app/build/test-results/testDebugUnitTest/TEST-com.hozayushka.app.ForecastSessionTest.xml`

## Observed result

- `AC-003` regression: the complete normalized read model is consumed through
  the public Weather Context read port by Forecast Sessions; output is exactly
  two rows of four, ordered `06:00, 09:00, 12:00, 15:00, 18:00, 21:00, 00:00,
  03:00`; every card has shared illustration text, a temperature background,
  and zero pressure arrows.
- `AC-001`/`AC-005`: complete data opens `OPEN`; incomplete data returns
  `CLOSED`, no rows and the exact `Почасовой прогноз еще не подгрузился` message.
- `AC-004`: no-interaction snapshot at `3000 ms` closes; single tap enters
  `HINT` and remains open beyond the deadline; double tap closes; hold at
  `600 ms` remains open at `3500 ms` and release closes immediately.

This is executor self-attested supporting evidence. `/verify` and T3
`/red-verify` remain required.
