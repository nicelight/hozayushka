# Claim-specific RED baseline — attempt 1

This is pre-production-change execution evidence for TASK-008. The probe used
the current source tree after `TASK-007` and did not introduce an artificial
failure or modify production behavior.

## Probe

Command:

```text
rg -n 'startPreset|presetButton|setOnClickListener|COUNTDOWN|TimerSnapshot|active timer|one active' app/src/main/kotlin/com/hozayushka/app/timer/TimerCapability.kt app/src/main/kotlin/com/hozayushka/app/display/DisplayCapability.kt
rg -n 'GestureDetector|onSingleTapConfirmed|onDoubleTap|SINGLE_TAP_HINT|doubleTap|cancel' app/src/main/kotlin/com/hozayushka/app/display/DisplayCapability.kt app/src/main/kotlin/com/hozayushka/app/timer/TimerCapability.kt
rg -n 'rehydrateAt|onActivityResumed|onActivityPaused|isNetworkAvailable|overdue|any tap|OVERDUE' app/src/main/kotlin/com/hozayushka/app/app/FoundationRuntime.kt app/src/main/kotlin/com/hozayushka/app/display/DisplayCapability.kt app/src/main/kotlin/com/hozayushka/app/timer/TimerCapability.kt
```

## Results by accepted claim

- `FT-006-AC-001 / REQ-012`: RED. `TimerCapability.startPreset` exists, but
  Main Display only creates preset buttons and has no start listener or
  countdown projection replacing the large clock/current-time layout.
- `FT-006-AC-002 / REQ-011`: RED for the FT-006 runtime delta. The timer store
  can hold one record, but no user-facing selected-preset start path consumes
  it; existing FT-005 one-record evidence remains prerequisite evidence only.
- `FT-006-AC-003 / REQ-013`: RED. Gesture handling exists only for forecast
  sessions; no timer single-tap hint, double-tap cancellation or overdue
  any-tap transition is wired.
- `FT-006-AC-004 / REQ-014`: RED for the feature path. `rehydrateAt` delegates
  to arithmetic, but no accepted temporary-interruption projection/gesture
  surface is present and no FT-006-specific synthetic recovery test exists.
- `FT-006-AC-005 / REQ-025`: RED. Offline connectivity affects only the clock
  colon/weather refresh path; no accepted timer gesture path or overdue
  dismissal back to Main Display is present.

The baseline is the honest claim-specific RED required before implementation;
it does not claim that the completed FT-005 prerequisite is absent or broken.
