---
description: Fresh verifier-owned evidence for TASK-027-T3-FT-001-W24 Attempt 2.
status: final
task_id: TASK-027-T3-FT-001-W24
stage_id: S-VERIFY
attempt: 2
role: Reviewer
---
# Verifier-owned evidence — Attempt 2

## Scope and safety

- Independent review of the current source; Attempt 1 verdict/evidence was not
  reused as proof.
- Host-only, offline, disposable Gradle/test state. No emulator, AVD, QEMU,
  adb, physical device, network/provider call, credential or secret was used.
- Target-device evidence remains `TARGET_DEVICE=DEFERRED`.

## Fresh gates

- `./gradlew --offline clean assembleDebug` — exit `0`, `BUILD SUCCESSFUL`.
- `./gradlew --offline --rerun-tasks testDebugUnitTest --tests com.hozayushka.app.DisplayProjectionTest.reachableMainDisplayRefreshKeepsIdleClock176AndCountdown32 --tests com.hozayushka.app.DisplayProjectionTest.mainDisplayTickerCoalescesLifecycleStartsAndStopsWhilePausedOrDetached --tests com.hozayushka.app.DisplayProjectionTest.w24GreenGeometryMakesClockDominantAndPresetsCircular` — exit `0`.
- `./gradlew --offline testDebugUnitTest` — exit `0`; XML suites total `103`
  tests, `0` failures, `0` errors, `0` skipped.
- `git diff --check` — exit `0`.
- `xmllint --noout .tasks/TASK-027-T3-FT-001-W24/red-green-contact-sheet.svg` —
  exit `0`.

## New targeted probes

### Reachable idle ticker

Independent source inspection found the supported path:

- `MainActivity.onResume()` calls `display.onActivityResumed()` at
  `app/src/main/kotlin/com/hozayushka/app/app/MainActivity.kt:19-24`.
- The attached/resumed ticker invokes `refresh()` through `onTick = ::refresh`
  at `DisplayCapability.kt:670-731,1315-1342`.
- `refresh()` selects `mainDisplayClockTextSizeForRefresh(...)` and assigns the
  result to `hour`, `colon` and `minute` after each refresh at
  `DisplayCapability.kt:1255-1293`.
- The selector returns `176f` for `IDLE` and `32f` for `COUNTDOWN` at
  `DisplayCapability.kt:80-83`; the focused test passed both assertions.

Result: the reachable refresh path retains idle `176f` and countdown `32f`.

### Circular preset controls

- Production layout gives every existing `TimerPresetSlot` button equal
  `presetColumnWidthDp × presetColumnWidthDp` params at `DisplayCapability.kt:977-987`.
- Production style uses `GradientDrawable.OVAL` at `DisplayCapability.kt:1722-1733`.
- Independent GREEN geometry test passed three `220×220` bounds, common
  effective radius `110`, `110 >= 220/2`, and `4/4` gaps.
- The same test preserved slot order, four weather slots, `223/279/223/223`
  card widths and `16/16/16` card gaps.

### Regression and boundary checks

- Full host suite passed display/date/colon/city/touch/ticker, timer preset and
  lifecycle, overdue/audio, weather projection, forecast and settings suites.
- Main Display still reads `weather.projection(now)` and calls existing Timer,
  Forecast and Settings capability contracts; no new owner, public contract,
  graph edge or neighbor-state write was found in the reviewed W24 surface.
- Current-attempt handoff records product/test writes only in the two hard-boundary
  files; unrelated dirty workspace paths were not attributed to Attempt 2.

## Supporting artifacts

- `.tasks/TASK-027-T3-FT-001-W24/clock-bounds.json`
- `.tasks/TASK-027-T3-FT-001-W24/red-green-contact-sheet.svg`
- `.tasks/TASK-027-T3-FT-001-W24/reference-visual-rubric.md`
- `.protocols/TASK-027-T3-FT-001-W24/handoff.md`
