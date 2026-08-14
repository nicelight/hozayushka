---
description: Read-only lifecycle and dismissal regression evidence for W28.
status: supporting
---
# W28 lifecycle / dismissal regression

Applicability: `RED_NOT_APPLICABLE` for intentionally breaking the existing
Timer & Alert contract. Such a break would exceed the visual-only two-file
boundary. The accepted alternative proof is focused/full host regression plus
source boundary inspection.

- `DisplayCapability.kt` continues to consume `timer.snapshotAt(now)`, call the
  existing `timer.advanceAt(now)`, and submit `TimerGesture.SINGLE_TAP` or
  `DOUBLE_TAP` through the existing gesture handlers.
- The new overdue branch only changes overlay composition, backdrop color and
  measured view bounds/text sizes. It does not calculate elapsed time or write
  timer state.
- `DisplayProjectionTest.w28ReadOnlyOverdueAnyTapStillDismissesThroughTimerContract`
  passed: synthetic timer reaches `OVERDUE`, a single tap returns it to `IDLE`
  and reports dismissal.
- Full host `TimerLifecycleTest`: `5` tests, `0` failures, `0` errors.
- Full host `DisplayProjectionTest`: `22` tests, `0` failures, `0` errors.
- W8/W27 lifecycle evidence remains historical/read-only; no task card,
  protocol, checkpoint or terminal state was changed.

Target-ROM interruption and fullscreen behavior are not inferred here; see
[`target-device.md`](target-device.md).
