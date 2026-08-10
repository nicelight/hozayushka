---
description: Independent verifier-owned evidence for TASK-017-T3-FT-001-W14.
status: active
---
# Verifier-owned evidence — TASK-017-T3-FT-001-W14

## Independent reruns

No executor receipt was reused. The current handoff offered no eligible
current-attempt reuse candidate, so the verifier reran the required gates and
the task-scoped probes against the current checkout.

- `./gradlew clean assembleDebug` — exit `0`, `BUILD SUCCESSFUL`.
- `./gradlew testDebugUnitTest` — exit `0`, `BUILD SUCCESSFUL`.
- `./gradlew testDebugUnitTest --tests com.hozayushka.app.WeatherContextTest`
  — exit `0`, `BUILD SUCCESSFUL`; the final full-suite XML reports 13 tests,
  0 skipped, 0 failures and 0 errors for this suite.
- `./gradlew testDebugUnitTest --tests
  'com.hozayushka.app.WeatherContextTest.repeatedProjectionReadsReuseOneDisplayReadySnapshot'`
  — exit `0`, `BUILD SUCCESSFUL`; the claim-level test passed.
- `git diff --check` — exit `0`, no output.

Final full-suite XML under `app/build/test-results/testDebugUnitTest/` reports
59 tests across 9 suites; every suite has `skipped="0"`, `failures="0"` and
`errors="0"`. `DisplayProjectionTest` reports 9/9, preserving W13 display
regression coverage.

## New targeted observations and claim mapping

The focused `WeatherContextTest` rerun independently covers the task-owned
harm-driving claims:

- `repeatedProjectionReadsReuseOneDisplayReadySnapshot` (`WeatherContextTest.kt:64–73`)
  returned the same `WeatherProjection` object for unchanged input and observed
  `loadRecordCalls == 0` after the accepted refresh. The production
  `SharedPreferencesWeatherCacheStore.loadRecord()` is the persisted record
  decode path (`WeatherCapability.kt:103–145`); the reuse path checks the private
  snapshot before its single cache miss load (`WeatherCapability.kt:395–400`).
- `acceptedRefreshInvalidatesOnceAndFailedRefreshPreservesLastProjection`
  (`WeatherContextTest.kt:77–99`) observed a new projection after accepted
  refresh, identity reuse of that rebuilt result, then the same successful
  projection and zero cache loads after a provider failure.
- `locationTimePressureAndFreshnessBoundariesRebuildTheSnapshot`
  (`WeatherContextTest.kt:102–133`) observed unchanged reuse, new identities
  after validated location change, the pressure boundary, selected-city
  day/night transition and the 24-hour `STALE_EMPTY` four-card empty result.
- Existing focused regression cases retained the four-card ordering, selected
  city timezone/date and moon fallback, pressure thresholds/12-hour fallback,
  unknown-condition fallback, refresh/freshness and incomplete-refresh
  preservation (`WeatherContextTest.kt:41–61`, `:137–155`, `:169–254`,
  `:340–360`).

Bounded source observations:

```text
projection_snapshot_decl=1
projection_snapshot_type=1
rebuild_projection=1
public_read_port=1
public_projection=1
projection_cache_miss_loads=1
forecast_and_long_term_loads=2
```

This confirms one private Weather Context snapshot/rebuild path, unchanged
public `WeatherReadPort`/`projection` surface, and no W14 Forecast optimization.
The W14 code/test diff names exactly:

```text
app/src/main/kotlin/com/hozayushka/app/weather/WeatherCapability.kt
app/src/test/kotlin/com/hozayushka/app/WeatherContextTest.kt
```

No current diff was found under Forecast, Timer, Yandex provider or Settings
roots. MainActivity/DisplayCapability have separate pre-existing W13 changes;
W14 did not modify those paths or the public Main Display → Weather Context
edge. No W14 task-card, scheduler, historical-task, RTM or Planning Revision
write was made by this verification.

The focused fixtures use in-memory cache/settings/provider state, reset the
counting load counter between cases, use a synthetic request, and contain no
live credentials or persistent production storage. No target device, emulator
or target-ROM claim was made.

## Executor claim path

- Initial RED: `.tasks/TASK-017-T3-FT-001-W14/attempt-1-red.txt` records the
  pre-change `assertSame` failure and repeated `loadRecord()` path. It is
  supporting evidence only; current source/diff inspection is independent.
- Executor GREEN: `.tasks/TASK-017-T3-FT-001-W14/attempt-1-green-weather-context.txt`
  records the 13-case WeatherContext result and invalidation/failure coverage.
- Regression RED_NOT_APPLICABLE: accepted baseline weather/time/failure
  behavior was not intentionally broken; the fresh host regression is the
  accepted alternative proof for REQ-007/REQ-022/REQ-025.

