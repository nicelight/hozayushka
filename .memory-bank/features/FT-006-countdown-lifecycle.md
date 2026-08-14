---
description: L3 feature for starting, protecting, cancelling and recovering an active countdown.
status: draft
id: FT-006
epic: EP-003
lifecycle: implemented
last_updated: 2026-08-12
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

Global backbone is complete at Planning Revision `2` and the Foundation Gate
anchors remain closed; feature-level design remains complete. Its indexed
task-plan review is stale only by revision mismatch and is reconciled through
`/feature-to-tasks --all` after Foundation revalidation.

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

The indexed [`TASK-015-T3-FT-001-W12`](../tasks/TASK-015-T3-FT-001-W12.task.json)
repair is now `done` after fresh functional `PASS` and semantic
`semantic-pass`. It repaired only the public Main Display dispatch path:
non-city weather-card double tap cancels through the existing Timer & Alert
contract, while single-tap protection, city hold/double, preset and overdue
guards remain intact. See the [functional verification](../../.protocols/TASK-015-T3-FT-001-W12/verification.md),
[semantic verification](../../.protocols/TASK-015-T3-FT-001-W12/red-verification.md),
and [verifier-owned evidence](../../.tasks/TASK-015-T3-FT-001-W12/verifier-owned-evidence-attempt-1.md).

This is a cross-feature regression repair owned by FT-001, not a new FT-006
task or timer contract. `TASK-008-T3-FT-006-W7`, FT-006 lifecycle,
REQ-012/013/014/025 RTM values and Timer & Alert semantic ownership remain
unchanged. Samsung/custom-ROM/1280x720 physical evidence remains `DEFERRED`;
generic-emulator evidence is not promoted to a target-device `PASS`.

## W27 post-terminal active countdown visual follow-up

Operator visual feedback after terminal W26 requests one bounded continuation
of `FT-006-AC-001`: while a countdown is active, Main Display must switch to a
dedicated countdown surface with no weather cards, city, date or standard card
shell; countdown digits must be materially larger than the final idle clock;
and the activating preset's existing color identity must form a transparent
neon circular backdrop. The selected preset presentation, accepted one-tap
hint, double-tap cancellation, temporary-interruption recovery, one-active-
timer invariant and network independence remain unchanged. This is a
presentation delta, not a new lifecycle transition, Timer & Alert contract,
audio repair or overdue behavior change.

The new indexed
[`TASK-030-T3-FT-006-W27`](../tasks/TASK-030-T3-FT-006-W27.task.json) is the
single Main Display-owned planned T3 task after terminal
[`TASK-029-T3-FT-001-W26`](../tasks/TASK-029-T3-FT-001-W26.task.json). Its hard
write boundary is exactly `DisplayCapability.kt` and
`DisplayProjectionTest.kt`; `TimerCapability.kt`, `TimerAlertPolicy.kt` and
`PlatformRuntimeAdapter.kt` remain read-only regression owners. W23 audio
history is context only and is not reopened.

Fresh claim-linked host visual/lifecycle RED/GREEN evidence is required. The
task must compare countdown digits with the final idle-clock result using the
same host geometry/render case, prove the dedicated surface and color identity,
and retain the accepted timer transitions through focused regression checks.
Target readability/lifecycle/audio runtime evidence is `DEFERRED`; this
planning boundary authorizes no emulator, device, adb, network or audio run.
No fixed dp value, ratio or neon gradient stop is selected. If execution needs
one to produce an unambiguous product verdict, route to `/feature-doctor
FT-006` before changing the task.

W27 is now `done` after executor `PASS_FOR_HANDOFF`, fresh functional `PASS`
and independent T3 `semantic-pass`. Host evidence proves countdown `228.0`
versus idle `188.75`, hides weather/city/date/cards, uses a transparent
preset-colored neon circle, and preserves selected/active indication,
one-active-timer, protected gestures, temporary recovery and offline
independence. Focused/full host, lifecycle, build and static gates pass. Target
readability/lifecycle and physical audio audibility remain `DEFERRED`, with no
device/runtime/audio `PASS` claim. Overdue/+ rendering and audio remain the
later FT-007 route. See the [W27 sync report](../../.tasks/TASK-030-T3-FT-006-W27/TASK-030-T3-FT-006-W27-S-MB-SYNC-final-report-docs-01.md),
[functional verification](../../.tasks/TASK-030-T3-FT-006-W27/TASK-030-T3-FT-006-W27-S-VERIFY-final-report-docs-01.md)
and [semantic verification](../../.tasks/TASK-030-T3-FT-006-W27/TASK-030-T3-FT-006-W27-S-RED-VERIFY-final-report-docs-01.md).
