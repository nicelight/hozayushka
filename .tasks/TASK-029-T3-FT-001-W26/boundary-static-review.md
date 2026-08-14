# Boundary/static review — Attempt 1

## Actual execution change surface

Product/test changes in this execution are limited to:

- `app/src/main/kotlin/com/hozayushka/app/display/DisplayCapability.kt`
- `app/src/test/kotlin/com/hozayushka/app/DisplayProjectionTest.kt`

The current worktree was already broadly dirty from provider migration and
W24/W25 history. This review treats the recorded Attempt-1 boundary-file
hashes and the explicit commands issued in this session as the W26 basis; no
pre-existing unrelated diff is attributed to W26.

## Main Display ownership

- Layout geometry remains inside `DisplayLayoutSpec` and
  `MainDisplayGeometry`; the four-card projection is still consumed by
  `bindWeatherCards` from `weather.projection(now)`.
- Clock refresh still branches on the existing Timer snapshot and preserves
  countdown size `32f`; only idle sizing receives available Main Display
  geometry.
- Preset click listeners, `TimerPresetSlot.entries` order, labels, selected
  alpha, active flag/width and existing `handlePresetTap` dispatch remain in
  `DisplayCapability.kt`.
- The gradient border is a private `Button` drawing tactic using existing
  Android `Canvas`, `Paint`, `LinearGradient` and `GradientDrawable.OVAL`; it
  creates no resource, asset, dependency, public capability or graph edge.

## Forbidden-scope review

No command in this attempt wrote `TimerCapability.kt`,
`TimerAlertPolicy.kt`, `PlatformRuntimeAdapter.kt`, Weather/Forecast/Settings/
Application code, resources, assets, task cards, lifecycle/checkpoint state or
W24/W25 protocols. No emulator/AVD/QEMU/adb/device/network/credential action
was run. Existing neighboring dirty changes were preserved and not used as
W26 RED.

The full host suite retained timer, countdown, cancellation, overdue/audio,
weather projection, provider and settings regression coverage; no W26 source
change re-owned those semantics.

## Numeric-choice boundary

No new fixed product dp/ratio/gradient-stop target is introduced. The larger
gaps derive from the existing outer vertical padding; preset side is derived
from available height and the existing right-column width; idle clock size is
derived from available central width/height and the existing four-card count.
Gradient endpoint positions are omitted so the platform shader supplies its
default interpolation; each existing preset color remains an endpoint identity.

## No runtime claim

The host/static result is not device/runtime evidence. Samsung/custom-ROM
1280×720 readability/fullscreen/keep-screen-on and actual gradient rendering
are recorded as `DEFERRED` in `target-device.md`.
