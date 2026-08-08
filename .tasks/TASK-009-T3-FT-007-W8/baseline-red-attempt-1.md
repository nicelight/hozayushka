---
description: Claim-specific pre-implementation RED evidence for TASK-009.
status: supporting
---
# Baseline RED — attempt 1

## Probe basis

Command run from `/home/serg/Projects/Mobile_APPS/hozayushka` after the task
was durably moved to `in_progress` and before any production behavior change:

```text
rg -n -C 3 'data class TimerSnapshot|requestAudioProbeAt|TimerLifecycleState\.OVERDUE|countdown\.visibility|activeTimerTouchListener|handleGesture' app/src/main/kotlin/com/hozayushka/app/timer/TimerCapability.kt app/src/main/kotlin/com/hozayushka/app/display/DisplayCapability.kt
rg -n -C 5 'requestAlertAudio|ToneGenerator|startTone|postDelayed|RINGER_MODE|INTERRUPTION_FILTER|route' app/src/main/kotlin/com/hozayushka/app/adapters/platform/PlatformRuntimeAdapter.kt
```

Exit code: `0` (source inspection completed).

## Claim-specific observations

- `FT-007-AC-001 / REQ-015`: RED. The current task-owned surface has only the
  FT-006 `OVERDUE` enum and `TimerSnapshot`; `DisplayCapability.refresh` only
  exposes countdown and has no fullscreen overdue projection, active-preset
  color expansion or blinking-plus/numeric split.
- `FT-007-AC-002 / REQ-015`: RED for the task-owned presentation integration.
  The timer has elapsed arithmetic, but the display has no full elapsed overdue
  counter or persistent-overdue visual surface. The existing FT-006 arithmetic
  remains a prerequisite and is not claimed as FT-007 evidence.
- `FT-007-AC-003 / REQ-015`: RED for the task-owned end-to-end alert/display
  outcome. FT-006 already has an any-tap `OVERDUE -> IDLE` transition, which is
  preserved as supporting prerequisite evidence; the baseline has no alert
  request state to stop and no dedicated overdue presentation integration.
- `FT-007-AC-004 / REQ-016`: RED. The only platform path is one fixed
  `ToneGenerator.TONE_PROP_BEEP` request lasting 250 ms; no three-signal
  selection/default seam, 5–10 second ramp, repeat scheduling or 30-minute cap
  exists.
- `FT-007-AC-005 / REQ-016`: RED for the task-owned integration. Ringer/DND
  suppression exists in the Foundation probe, but no overdue visual state is
  coupled to it and unavailable-route/app-volume suppression is not represented
  by the task-owned alert policy.

This is supporting execution evidence only; it is not a workflow verdict and
does not claim target-device behavior.
