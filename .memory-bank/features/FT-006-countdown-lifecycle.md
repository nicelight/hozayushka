---
description: L3 feature for starting, protecting, cancelling and recovering an active countdown.
status: draft
id: FT-006
epic: EP-003
lifecycle: planned
last_updated: 2026-08-03
---
# FT-006 — Countdown lifecycle and cancellation

## Product outcome

Владелец запускает выбранный preset одним тапом и может быть уверен, что
countdown не сбился и не отменился от случайного одиночного касания.

## Requirements

- REQ-012, REQ-013, REQ-014, REQ-025.

## Use cases

1. Владелец коротко тапает preset and immediately starts countdown.
2. Владелец видит countdown вместо больших часов, а current time сдвигается в
   сторону.
3. Владелец делает один tap и получает hint, затем double tap для отмены.
4. Владелец возвращается после Activity/process interruption и видит корректное
   remaining или overdue состояние.

## Acceptance criteria

- Short tap starts the selected preset immediately; active timer replaces the
  large clock, current time moves aside and the originating button is lit.
- At most one countdown is active; starting behavior follows the accepted
  preset configuration and does not create parallel timers.
- Single tap during countdown does not cancel and temporarily shows the accepted
  hint. Double tap anywhere cancels and returns to the standard main display.
- Countdown remains correct across Activity changes, foreground loss, screen-off
  and temporary process stop; resumed UI recalculates the accepted lifecycle
  state from the timer data.
- Network loss does not break countdown, cancellation or return to main display.

## Edge / failure behavior

- Reboot auto-start/recovery is not part of this feature and is not implied by
  temporary process recovery.
- A single tap cannot be treated as a cancel shortcut even when the hint is
  already visible.
- Rehydrated state can be `countdown` or `overdue`; overdue presentation/audio
  behavior is owned by FT-007.

## Sources and normative inputs

- [.memory-bank/prd.md](../prd.md): `PRD-FR-026`–`PRD-FR-028`, `PRD-FR-030`,
  `PRD-FR-027`, `PRD-NFR-004`, `PRD-AC-004`–`PRD-AC-005`, `PRD-AC-008`.
- [.memory-bank/invariants.md](../invariants.md): timer recovery and protected
  cancellation rules.
- [.memory-bank/states/lifecycle-map.md](../states/lifecycle-map.md): timer
  lifecycle transitions.
- [.memory-bank/contracts/boundary-map.md](../contracts/boundary-map.md): Android
  lifecycle responsibility hint.

## Verification targets

- `PRD-AC-004`, `PRD-AC-005`, `PRD-AC-008`, `PRD-FR-027`–`PRD-FR-028`.

## SDD Design Gate

Global backbone is complete at Planning Revision `1`; feature-level design
remains draft until the Foundation Gate and `/feature-to-tasks`.

Applicable global specs: [System Architecture](../architecture/system-architecture.md),
[Boundary Map](../contracts/boundary-map.md), [Capability Interfaces](../contracts/capability-interfaces.md),
[Platform Runtime](../contracts/platform-runtime.md), [Local Data](../domains/local-data.md),
[Lifecycle Map](../states/lifecycle-map.md) and [Runtime Verification](../testing/runtime-verification.md).
The platform mechanism and feature test level remain downstream.
