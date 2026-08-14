---
description: Fresh verifier-owned evidence for TASK-027-T3-FT-001-W24 Attempt 2 re-verification.
status: final
task_id: TASK-027-T3-FT-001-W24
stage_id: S-VERIFY
attempt: 2
role: Reviewer
---
# Verifier-owned evidence — TASK-027-T3-FT-001-W24 Attempt 2

## Fresh independent gates

- `./gradlew --offline --no-daemon clean assembleDebug` — exit `0`,
  `BUILD SUCCESSFUL`; only the pre-existing `MainActivity.kt` deprecation
  warning was emitted.
- `./gradlew --offline --no-daemon --rerun-tasks testDebugUnitTest --tests com.hozayushka.app.DisplayProjectionTest.reachableMainDisplayRefreshKeepsIdleClock176AndCountdown32 --tests com.hozayushka.app.DisplayProjectionTest.w24GreenGeometryMakesClockDominantAndPresetsCircular` — exit `0`.
- `./gradlew --offline --no-daemon testDebugUnitTest --rerun-tasks` — exit `0`,
  `103` test cases, `0` failures/errors, `0` skipped; counts independently
  read from `app/build/test-results/testDebugUnitTest/TEST-*.xml`.
- `git diff --check` — exit `0`.
- `xmllint --noout .tasks/TASK-027-T3-FT-001-W24/red-green-contact-sheet.svg`
  — exit `0`; SVG contains `3` GREEN circles and the rubric contains `7`
  PASS rows.
- `clock-bounds.json` assertions — PASS for Attempt 2 RED/GREEN, reachable
  idle/countdown values, preset geometry and four-card order/spacing.

## New targeted reachability probe

The current supported path was inspected independently:

- `MainActivity.onResume()` calls `display.onActivityResumed()` and renders
  `createMainView()`.
- `createMainView()` creates the attached/resumed `MainDisplayTickerOwner`
  with `onTick = ::refresh`.
- `refresh()` computes
  `mainDisplayClockTextSizeForRefresh(timerSnapshot.state, layoutSpec)` and
  assigns the result to `hour`, `colon` and `minute` on every tick.
- The selector returns `176f` for idle/non-countdown and `32f` for countdown;
  the focused host regression passes both assertions.

This closes the prior failed verifier observation: the pre-correction Attempt 2
RED had a reachable idle reset to `132f`; the current path no longer contains
that reset.

## Claim mapping

- `FT-001-AC-002 / REQ-002`: PASS. Current reachable idle refresh is `176f`,
  countdown remains `32f`; current GREEN bounds are clock
  `[271,24,1028,252]`, three right-side presets `220x220` with common radius
  `110`, while the fresh Attempt 2 RED records `132f`, `220x224`, radius `18`.
- Visual rubric: PASS. The same-size `1280x720` RED/GREEN contact sheet keeps
  left city/date, central clock, lower cards and right controls anchored; all
  seven rubric rows have decisive bounds/source observations.
- `REQ-005`: PASS regression. Four cards remain ordered
  `yesterday/today/tomorrow/day_after`, widths `223/279/223/223`, gaps
  `16/16/16`; Main Display reads the Weather Context projection and does not
  write Weather Context state.
- `REQ-011`–`REQ-016`: PASS regression. Full host suite passes; timer,
  countdown, cancellation, overdue/audio tests and existing gesture dispatch
  remain on their existing owners and paths.
- Public boundaries: PASS. Main Display retains existing Weather Context,
  Timer & Alert, Forecast Sessions, Settings & Location and Android Runtime
  interactions; no new owner, edge, event path, resource or dependency is
  present in the task-attempt evidence.
- `REQ-023`: host/static proof only. Samsung GT-I9300I Android 11
  custom-ROM `1280x720` readability/fullscreen/keep-screen-on and runtime
  circle rendering remain `TARGET_DEVICE=DEFERRED`; no runtime/device PASS is
  claimed.

## Safety and scope

No emulator/AVD/QEMU, Android Studio virtual device, adb/device, network,
provider call, credential, secret-bearing evidence or persistent runtime state
was used. The handoff records exactly the two product/test writes permitted by
the task boundary. The broad dirty workspace is pre-existing and is not
attributed to this re-verification.

## Artifact hashes

- `DisplayCapability.kt`: `ca35e2b2fd09879651e049ce0686759b6399e70f55f67107f596237aa50d857c`
- `DisplayProjectionTest.kt`: `9ef3fc8ba26f69b3865b9f8b5871d2096aa4c8be504d22b0159c5d2309480a4e`
- `clock-bounds.json`: `b4f2167bf531eea483250ea56d46bcf762778fb8cb7211f73ced4e135ab1825e`
- `red-green-contact-sheet.svg`: `02b6aca91693210624a8d167ea6eceefb17c7237e02cc609e0fc322aeffc056e`
- `reference-visual-rubric.md`: `05edd1c23d8592a35c5141829bf6d8b944ddd3a476bb52b910b1f07f3ea7babf`
