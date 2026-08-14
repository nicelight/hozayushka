---
description: Exact-boundary and owner review for TASK-031-T3-FT-007-W28.
status: supporting
---
# W28 boundary static review — attempt 1

## Allowed outcome surface

The task implementation/test delta is limited to:

- `app/src/main/kotlin/com/hozayushka/app/display/DisplayCapability.kt`
- `app/src/test/kotlin/com/hozayushka/app/DisplayProjectionTest.kt`

`git diff --name-only` scoped to those two paths resolves to exactly those two
paths. Protocol and `.tasks/TASK-031...` files are required execution evidence.

## Composition ownership

- `OverdueSurfaceGeometry` supplies adaptive surface, circular backdrop, plus
  and elapsed bounds; it does not own timer state or arithmetic.
- The overdue refresh branch sets `mainShell` to `GONE`, shows the existing
  overlay, reads `timerSnapshot.elapsedMillis`, and formats it through the
  existing `DisplayFormatters.elapsedText`.
- The existing `GestureDetector` still routes single/double taps to
  `applyTimerGesture`, preserving any-tap dismissal.
- The overlay contains only the transparent neon backdrop, blinking `+` and
  stable elapsed counter; no weather/card/city/date child is added.

## Owner and forbidden-scope check

- `TimerCapability.kt`, `TimerAlertPolicy.kt` and
  `PlatformRuntimeAdapter.kt` were read-only during this attempt; their dirty
  status was pre-existing at entry and is explicitly not claimed by W28.
- No settings/weather/resource/app-root source was written.
- No new module, dependency, public contract, graph edge, event/message path,
  permission, storage owner, network path or composition-root orchestration was
  introduced.
- W8/W23/W27 cards, protocols, evidence, statuses, scheduler checkpoint,
  lifecycle/REQ state and terminal state were not changed.

## Static visual contract

- `overdueOverlay.setBackgroundColor(Color.TRANSPARENT)` and the backdrop's
  transparent background preserve a transparent circular treatment.
- `NeonCountdownBackdropView` draws `Style.STROKE` circle and receives the
  activating preset's existing `PresetPresentation.colorHex`.
- Full elapsed text is set from the Timer & Alert projection; only plus alpha
  changes on the existing `OverduePresentation.plusVisibleAt` cadence.
