---
description: Claim-linked post-implementation GREEN evidence for TASK-006-T3-FT-004-W5.
status: final
---
# GREEN fixture evidence — TASK-006-T3-FT-004-W5

Attempt: 1. Final host evidence was produced after the implementation and
after the task was `in_progress`. Fixtures use synthetic provider DTOs and the
in-memory Weather Context owner store; no live request or credential is used.

## Claim results

- `FT-004-AC-001 / REQ-010, REQ-026`: `ForecastSessionTest.completeTenDayReadModelSurvivesOwnerReloadAndOpensFromLongTermEntry`
  saved a complete ten-day provider result, re-created Weather Context with
  the same isolated owner store, and observed an equal public
  `longTermProjection`. The same public model opened `openLongTerm`; missing
  data is covered by the rejection test with the accepted message.
- `FT-004-AC-002 / REQ-010, REQ-022`: the same test observed ten ordered
  selected-city dates from `2024-01-02` through `2024-01-11` and rows
  `[5, 5]`.
- `FT-004-AC-003 / REQ-010, REQ-022, REQ-026`:
  `ForecastSessionTest.longTermProjectionUsesSelectedCityDayNightAndRejectsIncompleteDailyFields`
  observed selected-city day/night choice (`10` daytime and `20` nighttime),
  the nighttime moon illustration, temperature text/background/illustration
  fields on every card, and zero pressure arrows. `DisplayProjectionTest`
  confirms Tomorrow and Day-after map to the same `LONG_TERM` intent.
- `FT-004-AC-004 / REQ-010`:
  `ForecastSessionTest.longTermSessionUsesSharedTimingAndGestureContract`
  observed 3000 ms auto-close, single-tap hint with cancellation, double-tap
  close and hold-beyond-deadline/release-close on the long-term session.
- `FT-004-AC-005 / REQ-010, REQ-026`: the incomplete daily fixture failed
  before cache replacement; an isolated empty owner state stayed closed with
  `Долгосрочный прогноз еще не подгрузился` and no rows.

## Final claim-equivalent command

```text
./gradlew testDebugUnitTest
```

Exit `0`, `BUILD SUCCESSFUL`; all `27/27` tests passed with zero skipped,
failures or errors. Report directory:
`app/build/test-results/testDebugUnitTest/`.

The current GREEN is executor evidence for `/verify`; it is not independent
verification or lifecycle closure.
