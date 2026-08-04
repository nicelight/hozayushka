---
description: Android OS boundary for lifecycle, time, display flags, network and permitted alert audio.
status: active
last_updated: 2026-08-04
source_of_truth: .memory-bank/prd.md, .memory-bank/constitution.md, operator confirmation 2026-08-04
---
# Platform Runtime Contract

## Boundary Ownership

Android OS owns device time, device timezone, process/Activity lifecycle,
network availability, screen state and whether alert audio is permitted. The
application owns product-visible state and translates platform signals through
the capability contracts. The target is Samsung GT-I9300I (`s3ve3gds`) with a
compatible Android 11 custom ROM.

### Display Runtime Boundary

- The application requests landscape fullscreen, hidden system panels and
  keep-screen-on while the app is open.
- Main Display uses device time/timezone for `HH:mm` and the main date.
- Platform/network availability may select the accepted online/offline colon
  behavior but cannot remove the clock shell.
- A target-device probe owns readability, fullscreen and keep-screen-on
  evidence; these are not inferred from host-side logic alone.

### Timer and Audio Runtime Boundary

- Timer & Alert receives Activity/foreground/screen/process lifecycle signals
  needed to rehydrate the active timer after a temporary interruption.
- Rehydration recalculates `countdown|overdue` from persisted timer data. V1
  does not auto-start or recover a timer after reboot.
- Audio requests follow Android silent mode, DND and route policy. The visual
  overdue state remains until product dismissal even when audio is suppressed.
- The audio request is capped at 30 minutes by product behavior; the visual
  overdue state has no equivalent automatic dismissal requirement.

### Session Timing Boundary

Forecast Sessions may use the platform clock/timing source for the accepted
three-second auto-close and gesture timing, while city date/slot labels remain
provider-timezone data. The session contract does not grant access to the
Weather Context store or timer state.

## Compatibility and Failure Rules

- Network absence is a signal, not a product failure: clock and timers remain
  usable and Weather Context applies its cache/freshness contract.
- A platform denial or unusual custom-ROM behavior is recorded as runtime
  evidence and routed to device verification; it does not authorize reboot
  recovery or a new product scope.
- No Google Services, autostart after reboot or backend is introduced to bridge
  a platform limitation.

## Verification Route

Foundation must establish the minimal Android entry path before target-device
probes can run. The required known initial state, safe rerun, observable result
and cleanup/isolation for lifecycle, fullscreen and audio checks are defined in
[Runtime Verification](../testing/runtime-verification.md).
