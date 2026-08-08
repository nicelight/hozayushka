---
description: Fresh verifier-owned probes for TASK-008-T3-FT-006-W7.
status: final
---
# Fresh verifier-owned probes — TASK-008-T3-FT-006-W7

## Commands and results

- `./gradlew clean assembleDebug` — exit `0`, `BUILD SUCCESSFUL`.
- `./gradlew testDebugUnitTest --rerun-tasks` — exit `0`, `BUILD SUCCESSFUL`.
- Temporary `VerifierOwnedFt006ReverificationTest` — exit `0`, two tests
  passed. The temporary test file was removed after execution.
- `git diff --check` — exit `0`.
- `adb devices` — no target listed; target evidence remains `DEFERRED`, with no
  runtime `PASS` claim.

The only compiler diagnostic was the pre-existing `MainActivity.onBackPressed`
deprecation warning.

## Timer claim observations

The fresh fixed-time probe used isolated in-memory Settings and Timer stores,
with no provider/network/credential input:

- a validated SECOND preset starts `COUNTDOWN` immediately with its preset
  identity and one stored record;
- a single tap returns the same `COUNTDOWN` snapshot and exposes the hint;
- the exact duration boundary is `OVERDUE`, and any tap dismisses it to `IDLE`;
- a dismissed timer does not resurrect on later gestures;
- replacement starts keep one active record and expose only the latest preset;
- synthetic rehydration reads persisted start/duration arithmetic.

These observations cover the Timer owner for AC-001/002/003/004/005. The
network-independent probe did not use a provider.

## Supported child/weather-card dispatch probe

Current source inspection produced:

- `DisplayCapability.kt:377` attaches `activeTimerTouchListener` to city;
- `DisplayCapability.kt:379` attaches it to the initial card children;
- `DisplayCapability.kt:425` calls `cards.removeAllViews()` on every refresh;
- `DisplayCapability.kt:427-428` creates replacement weather cards;
- `DisplayCapability.kt:736` gives each replacement card its own click handler,
  but the refresh rebuild contains no `activeTimerTouchListener` attachment.

The literal source probe found exactly two active-listener attachments, both
before the refresh rebuild, and none in the rebuilt-card region. Since the
display ticker invokes this rebuild every 50 ms, the supported visible
weather-card instances after refresh do not forward touch sequences to
`mainGestureDetector`. Their click handler can therefore consume the path
without reaching Timer. This disproves `double tap anywhere` cancellation and
overdue any-tap dismissal on refreshed weather-card children. City routing is
covered by the conditional listener; the defect is the weather-card rebuild
path.

## Scope and ownership

Timer remains the sole owner of persistence, arithmetic and lifecycle state;
Main Display uses the public Timer path. No composition-root timer business
state, private Settings/provider bypass, new boundary, or FT-007 fullscreen or
audio behavior was observed. Reboot recovery was not claimed.
