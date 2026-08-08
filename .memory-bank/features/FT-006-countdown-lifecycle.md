---
description: L3 feature for starting, protecting, cancelling and recovering an active countdown.
status: draft
id: FT-006
epic: EP-003
lifecycle: implemented
last_updated: 2026-08-08
spec_design_status: complete
spec_design_links:
  - .memory-bank/contracts/boundary-map.md
  - .memory-bank/contracts/capability-interfaces.md
  - .memory-bank/contracts/platform-runtime.md
  - .memory-bank/domains/local-data.md
  - .memory-bank/states/lifecycle-map.md
  - .memory-bank/testing/runtime-verification.md
---
# FT-006 — Countdown lifecycle and cancellation

## Product outcome

Владелец запускает выбранный preset одним тапом и может быть уверен, что
countdown не сбился и не отменился от случайного одиночного касания.

## Requirements

- REQ-011, REQ-012, REQ-013, REQ-014, REQ-025.

## Use cases

1. Владелец коротко тапает preset and immediately starts countdown.
2. Владелец видит countdown вместо больших часов, а current time сдвигается в
   сторону.
3. Владелец делает один tap и получает hint, затем double tap для отмены.
4. Владелец возвращается после Activity/process interruption и видит корректное
   remaining или overdue состояние.

## Acceptance criteria

### FT-006-AC-001 — Short tap starts the selected preset immediately

- REQ: REQ-012
Active timer replaces the
  large clock, current time moves aside and the originating button is lit.
### FT-006-AC-002 — At most one countdown is active

- REQ: REQ-011
Starting behavior follows the accepted
  preset configuration and does not create parallel timers.
### FT-006-AC-003 — Protected cancellation gestures

- REQ: REQ-013
Single tap during countdown does not cancel and temporarily shows the accepted
  hint. Double tap anywhere cancels and returns to the standard main display.
### FT-006-AC-004 — Temporary interruption recovery

- REQ: REQ-014
Countdown remains correct across Activity changes, foreground loss, screen-off
  and temporary process stop; resumed UI recalculates the accepted lifecycle
  state from the timer data.
### FT-006-AC-005 — Network-independent timer path

- REQ: REQ-025
Network or weather-service unavailability does not break the timer path:
countdown and its protected cancellation remain usable, and an already-overdue
timer can be dismissed by any tap and return to the standard main display.

## Edge / failure behavior

- Reboot auto-start/recovery is not part of this feature and is not implied by
  temporary process recovery.
- A single tap cannot be treated as a cancel shortcut even when the hint is
  already visible.
- Rehydrated state can be `countdown` or `overdue`; overdue presentation/audio
  behavior is owned by FT-007.
- Network or weather-service unavailability does not remove the accepted
  any-tap dismissal transition from an already-overdue state; FT-007 owns only
  the overdue presentation and audio behavior.

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
- `FT-006-AC-005 / REQ-025`: with network/weather-service input absent,
  verify timer start/countdown, protected cancellation, and any-tap dismissal
  of an already-overdue state back to the standard main display; fullscreen
  overdue rendering and audio policy remain outside this feature's proof.

## SDD Design Gate

Global backbone is complete at Planning Revision `1` and the Foundation Gate
is closed; feature-level design is complete and reconciled for task planning.

Applicable global specs: [System Architecture](../architecture/system-architecture.md),
[Boundary Map](../contracts/boundary-map.md), [Capability Interfaces](../contracts/capability-interfaces.md),
[Platform Runtime](../contracts/platform-runtime.md), [Local Data](../domains/local-data.md),
[Lifecycle Map](../states/lifecycle-map.md) and [Runtime Verification](../testing/runtime-verification.md).
The platform mechanism and feature test level remain downstream.

## W7 implementation evidence

The W7 boundary records `TASK-008-T3-FT-006-W7` as `done` after fresh
functional `PASS` and T3 semantic `semantic-pass`. The evidence covers
immediate selected-preset start, one active timer, protected cancellation,
temporary-interruption rehydration, and network-independent overdue
dismissal. See the [functional report](../../.tasks/TASK-008-T3-FT-006-W7/TASK-008-T3-FT-006-W7-S-VERIFY-final-report-docs-03.md),
[semantic report](../../.tasks/TASK-008-T3-FT-006-W7/TASK-008-T3-FT-006-W7-S-RED-VERIFY-final-report-docs-03.md)
and [final verifier probes](../../.tasks/TASK-008-T3-FT-006-W7/verifier-owned-probes-final.md).
Target-device lifecycle/display evidence remains `DEFERRED` and non-blocking
with residual risk; no runtime `PASS` is claimed. Feature lifecycle is now
`implemented`; EP-003 remains `planned` because FT-007 is still outstanding.

## W11 cross-feature regression record

The final semantic verification of the FT-001 follow-up
[`TASK-014-T3-FT-001-W11`](../tasks/TASK-014-T3-FT-001-W11.task.json) found that
one public non-city weather-card double tap did not cancel an active countdown.
This is a regression against the existing FT-006-AC-003 / REQ-013 contract,
not a new timer contract or a new FT-006 task decision. The failure and repair
route are recorded in the
[`TASK-014-noncity-countdown-cancellation`](../bugs/TASK-014-noncity-countdown-cancellation.md)
bug note and the [semantic report](../../.tasks/TASK-014-T3-FT-001-W11/TASK-014-T3-FT-001-W11-S-RED-VERIFY-final-report-docs-01.md).
`TASK-008-T3-FT-006-W7`, FT-006's `implemented` lifecycle and its direct RTM
rows remain unchanged; repair is routed through normal indexed FT-001
planning.

The planned [`TASK-015-T3-FT-001-W12`](../tasks/TASK-015-T3-FT-001-W12.task.json)
is the repair owner for the public Main Display dispatch delta only: it must
deliver the existing double-tap command from a non-city weather-card touch
stream and cross-check the accepted single-tap hint, city hold/double and
overdue/preset guards. `TASK-008-T3-FT-006-W7`, FT-006 lifecycle/RTM values and
Timer & Alert semantic ownership remain unchanged; no new timer contract is
introduced.
