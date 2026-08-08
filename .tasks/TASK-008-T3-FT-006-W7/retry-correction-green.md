# Claim-equivalent GREEN — retry attempt 2

## Correction

The task-local Main Display correction keeps the existing public Timer command
path and attaches one conditional touch listener to the supported interactive
city and weather-card child views. While Timer is `COUNTDOWN` or `OVERDUE`, the
child touch sequence is sent to the existing Main Display gesture detector and
is consumed; while Timer is `IDLE`, the listener returns `false`, preserving the
existing city/settings and weather/forecast click handlers.

## Claim mapping

- `FT-006-AC-003 / REQ-013`: active countdown child-view single tap reaches the
  existing single-tap path without cancellation, and double tap reaches the
  existing Timer double-tap cancellation path.
- `FT-006-AC-005 / REQ-025`: overdue child-view tap reaches the existing Timer
  gesture path, whose Timer-owned overdue transition dismisses to `IDLE`; no
  provider or network input is introduced.
- Existing Timer lifecycle claims remain covered by the fresh targeted unit
  test and full unit suite; no Timer owner or public contract was changed.

## Fresh probes

1. `./gradlew testDebugUnitTest --tests com.hozayushka.app.TimerLifecycleTest --rerun-tasks`
   — exit `0`, `BUILD SUCCESSFUL`.
2. Literal source-path probe over
   `app/src/main/kotlin/com/hozayushka/app/display/DisplayCapability.kt`
   confirmed exactly one conditional `activeTimerTouchListener`, attachment to
   city and every constructed weather-card child, forwarding to
   `mainGestureDetector`, and preservation of city/weather click handlers —
   exit `0`, `correction source-path checks passed`.

This is fresh host/source GREEN after the retry correction. Android target
dispatch remains separately `DEFERRED` because no target was available; no
runtime `PASS` is claimed.
