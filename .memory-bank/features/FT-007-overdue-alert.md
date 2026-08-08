---
description: L3 feature for the completed timer's overdue presentation and permitted alert sound.
status: draft
id: FT-007
epic: EP-003
lifecycle: implemented
spec_design_status: complete
spec_design_links:
  - .memory-bank/contracts/boundary-map.md
  - .memory-bank/contracts/capability-interfaces.md
  - .memory-bank/contracts/platform-runtime.md
  - .memory-bank/domains/local-data.md
  - .memory-bank/states/lifecycle-map.md
  - .memory-bank/testing/runtime-verification.md
last_updated: 2026-08-08
---
# FT-007 — Overdue state and alert

## Product outcome

После окончания countdown владелец получает заметное fullscreen overdue state,
может быстро его отключить и слышит повторяющийся сигнал только если это
разрешено Android.

## Requirements

- REQ-015, REQ-016.

## Use cases

1. Владелец видит, что timer завершён, по fullscreen neon state и blinking `+`.
2. Владелец видит полный elapsed counter без мигания числового значения.
3. Владелец отключает overdue state одним или двойным тапом в любом месте.
4. Владелец слышит выбранный repeatable signal с ramp, если silent/DND не
   запрещают его.

## Acceptance criteria

### FT-007-AC-001 — Fullscreen overdue presentation

- REQ: REQ-015
- At zero, the active preset expands into the accepted fullscreen neon area with
  its color; `+` blinks and the numeric counter does not.

### FT-007-AC-002 — Full elapsed counter and persistent visual state

- REQ: REQ-015
- Counter shows the full elapsed time from timer start, including configured
  duration; visual overdue state remains until a tap dismisses it.

### FT-007-AC-003 — Tap dismissal

- REQ: REQ-015
- Single or double tap anywhere disables the overdue state and returns to main
  display.

### FT-007-AC-004 — Repeatable built-in alert policy

- REQ: REQ-016
- Completion starts the selected built-in signal (`Классический`, `Колокольчик`
  or `Электронный`, default `Классический`) with accepted 5–10 second ramp,
  repeats until dismissal but no longer than 30 minutes.

### FT-007-AC-005 — Audio suppression does not suppress visual overdue state

- REQ: REQ-016
- Audio follows Android silent mode and DND permissions; visual overdue state is
  always shown even when audio is suppressed.

## Edge / failure behavior

- Audio denial, silent mode, DND or an unavailable audio route never removes the
  visual overdue state or blocks dismissal.
- The alert stops on accepted manual dismissal or its 30-minute audio cap;
  product behavior does not imply a cap on the visual state before dismissal.
- Overdue recovery after temporary process interruption uses the same lifecycle
  state: the visual overdue projection, any-tap dismissal and permitted alert
  policy are re-established from the persisted timer data; this does not
  introduce reboot restoration.

## Sources and normative inputs

- [.memory-bank/prd.md](../prd.md): `PRD-FR-029`–`PRD-FR-031`, `PRD-AC-005`,
  `PRD-AC-008`.
- [.memory-bank/invariants.md](../invariants.md): visual overdue and OS-owned
  audio permission rules.
- [.memory-bank/states/lifecycle-map.md](../states/lifecycle-map.md): transition
  from `countdown` to `overdue` and dismissal.
- [.memory-bank/contracts/capability-interfaces.md](../contracts/capability-interfaces.md):
  Main Display → Timer & Alert any-tap dismissal and Timer & Alert → Settings &
  Location validated sound/volume projection.
- [.memory-bank/contracts/boundary-map.md](../contracts/boundary-map.md): Android
  audio responsibility hint.

## Verification targets

- `PRD-AC-005`, `PRD-AC-008`, `PRD-FR-029`–`PRD-FR-031`, including resumed
  overdue proof after temporary process interruption.

## SDD Design Gate

Global backbone is complete at Planning Revision `1` and the Foundation Gate
is closed; feature-level design is complete for task planning.

Applicable global specs: [Boundary Map](../contracts/boundary-map.md),
[Capability Interfaces](../contracts/capability-interfaces.md), [Platform Runtime](../contracts/platform-runtime.md),
[Local Data](../domains/local-data.md), [Lifecycle Map](../states/lifecycle-map.md)
and [Runtime Verification](../testing/runtime-verification.md).
The alarm/audio mechanism and feature test level remain downstream.

## W8 implementation evidence

The W8 boundary records `TASK-009-T3-FT-007-W8` as `done` after retry-2 fresh
functional `PASS` and T3 semantic `semantic-pass`. The evidence covers the
fullscreen active-preset overdue projection, full elapsed counter, any-tap
dismissal, built-in signals, ramp/repeat/cap behavior, silent/DND/route
suppression and temporary-resume re-establishment. See the [functional report](../../.tasks/TASK-009-T3-FT-007-W8/TASK-009-T3-FT-007-W8-S-VERIFY-final-report-docs-01.md),
[semantic report](../../.tasks/TASK-009-T3-FT-007-W8/TASK-009-T3-FT-007-W8-S-RED-VERIFY-final-report-docs-01.md)
and [verifier-owned probe](../../.tasks/TASK-009-T3-FT-007-W8/verifier-owned-probe.md).
Target-device evidence remains `DEFERRED` and non-blocking with residual risk;
no runtime `PASS` is claimed. FT-007 lifecycle is now `implemented`; no feature
closure, promotion or dependent-state transition is inferred by this boundary
sync.
