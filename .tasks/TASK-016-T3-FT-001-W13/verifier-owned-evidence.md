---
description: Independent verifier-owned evidence for TASK-016-T3-FT-001-W13.
status: active
---
# Verifier-owned evidence — TASK-016-T3-FT-001-W13

## Independent reruns

Fresh verifier reruns used the current workspace after source/diff inspection;
no executor receipt was reused.

- `./gradlew clean assembleDebug` — exit `0`, `BUILD SUCCESSFUL`.
- `./gradlew testDebugUnitTest` — exit `0`, `BUILD SUCCESSFUL`.
- `./gradlew testDebugUnitTest --tests com.hozayushka.app.DisplayProjectionTest`
  — exit `0`, `BUILD SUCCESSFUL`; the focused XML reports 9 tests, 0 skipped,
  0 failures and 0 errors.
- Full-suite XML after the final rerun contains 56 tests total across 9 suites,
  with `skipped="0"`, `failures="0"` and `errors="0"` in every suite:
  `app/build/test-results/testDebugUnitTest/`.
- `git diff --check` — exit `0`, no output.

## New targeted observations and claim mapping

The focused rerun independently observed these existing task-scoped tests in
`app/src/test/kotlin/com/hozayushka/app/DisplayProjectionTest.kt`:

- `mainDisplayTickerCoalescesLifecycleStartsAndStopsWhilePausedOrDetached`
  (lines 166–205): from a fresh fake scheduler, duplicate attach/resume leaves
  one pending callback; one callback schedules one `50 ms` continuation; pause
  removes pending work and produces no callback; detach suppresses work; one
  resume restores one callback; final `reset()` leaves the disposable scheduler
  idle.
- `unchangedWeatherProjectionKeepsFourCardTreeAndChangedInputRebindsOnce`
  (lines 207–240): unchanged `WeatherProjection + glassIntensity` preserves the
  same four-node list and does not rebind; one changed projection creates exactly
  one new four-node list; repeating the same changed input does not rebind.
- `stableShellReservesHeaderContentBeforeFourCardsAndThreePresets` (lines
  46–62): the accepted shell remains four weather cards and three presets.
- `deviceTimezoneDrivesClockAndRussianGenitiveDate` (lines 37–44): the same
  instant formats as `03:30 / 01 августа` in `Asia/Dushanbe` and `31 июля` in
  `America/New_York`, proving the formatter keeps the supplied device zone.
- `colonModesUseAcceptedConnectivityAndCountdownValues` (lines 64–73): offline
  brightness `0.38`, countdown `1.0` at `381 ms` and `0.0` at `382 ms`, online
  pulse `1.0` at `3000 ms` and `0.02` at `5999 ms`.

The bounded source probe over Main Display lines 200–802 observed:

```text
main_ticker_owner_definitions=1
main_ticker_owner_instantiations=1
main_direct_start_markers=0
main_scheduler_adapter_calls=3
main_card_remove_sites=1
main_card_add_sites=1
activity_display_lifecycle_forwards=2
weather_card_slots=4
bounded_source_shape=PASS
```

The current source also keeps `refresh()` clock/date reads on
`platform.deviceTimeText(now)` and `platform.deviceZoneId()` and leaves the
separate Forecast ticker outside this W13 bounded scan.

## Boundary and safety observations

- The current task-code diff names exactly the three allowed files:
  `DisplayCapability.kt`, `MainActivity.kt` and `DisplayProjectionTest.kt`.
- No current diff exists under Weather Context, Timer & Alert, Forecast or the
  Android Runtime Adapter; no dependency/module/event-message diff was found.
- `MainActivity` adds only the two existing display lifecycle forwards. The
  ticker owner and card reuse remain inside Main Display; Weather Context and
  Timer & Alert contracts are consumed, not rewritten.
- The targeted tests use only in-memory fake scheduler/render state and reset
  the scheduler. No credentials, persistence, target device or private
  neighbor state was used. No target-device PASS is claimed.

## Executor claim path

- Initial RED and executor GREEN remain supporting evidence only:
  `.tasks/TASK-016-T3-FT-001-W13/attempt-1-red-source.txt`,
  `attempt-1-green-host.txt`, `attempt-1-green-source.txt`.
- The accepted `FT-001-AC-004 / REQ-003` `RED_NOT_APPLICABLE` path is retained;
  the fresh full host suite and focused colon test provide the required
  regression proof without intentionally breaking Timer & Alert behavior.

