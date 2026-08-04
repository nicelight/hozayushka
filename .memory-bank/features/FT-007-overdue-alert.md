---
description: L3 feature for the completed timer's overdue presentation and permitted alert sound.
status: draft
id: FT-007
epic: EP-003
lifecycle: planned
last_updated: 2026-08-03
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

- At zero, the active preset expands into the accepted fullscreen neon area with
  its color; `+` blinks and the numeric counter does not.
- Counter shows the full elapsed time from timer start, including configured
  duration; visual overdue state remains until a tap dismisses it.
- Single or double tap anywhere disables the overdue state and returns to main
  display.
- Completion starts the selected built-in signal (`Классический`, `Колокольчик`
  or `Электронный`, default `Классический`) with accepted 5–10 second ramp,
  repeats until dismissal but no longer than 30 minutes.
- Audio follows Android silent mode and DND permissions; visual overdue state is
  always shown even when audio is suppressed.

## Edge / failure behavior

- Audio denial, silent mode, DND or an unavailable audio route never removes the
  visual overdue state or blocks dismissal.
- The alert stops on accepted manual dismissal or its 30-minute audio cap;
  product behavior does not imply a cap on the visual state before dismissal.
- Overdue recovery after temporary process interruption uses the same lifecycle
  state and does not introduce reboot restoration.

## Sources and normative inputs

- [.memory-bank/prd.md](../prd.md): `PRD-FR-029`–`PRD-FR-031`, `PRD-AC-005`,
  `PRD-AC-008`.
- [.memory-bank/invariants.md](../invariants.md): visual overdue and OS-owned
  audio permission rules.
- [.memory-bank/states/lifecycle-map.md](../states/lifecycle-map.md): transition
  from `countdown` to `overdue` and dismissal.
- [.memory-bank/contracts/boundary-map.md](../contracts/boundary-map.md): Android
  audio responsibility hint.

## Verification targets

- `PRD-AC-005`, `PRD-AC-008`, `PRD-FR-029`–`PRD-FR-031`.

## SDD Design Gate

Global backbone is complete at Planning Revision `1`; feature-level design
remains draft until the Foundation Gate and `/feature-to-tasks`.

Applicable global specs: [Boundary Map](../contracts/boundary-map.md),
[Capability Interfaces](../contracts/capability-interfaces.md), [Platform Runtime](../contracts/platform-runtime.md),
[Local Data](../domains/local-data.md), [Lifecycle Map](../states/lifecycle-map.md)
and [Runtime Verification](../testing/runtime-verification.md).
The alarm/audio mechanism and feature test level remain downstream.
