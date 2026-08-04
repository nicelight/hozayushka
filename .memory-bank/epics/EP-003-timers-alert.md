---
description: L2 epic for preset timer setup, resilient countdown and overdue alert.
status: draft
id: EP-003
lifecycle: planned
last_updated: 2026-08-03
---
# EP-003 — Timers and alert

## Value

Владелец запускает привычный timer одним тапом, не теряет корректный отсчёт при
временной остановке приложения и получает заметное, но управляемое overdue
состояние с разрешённым системными правилами звуком.

## Success metrics

- Каждый из трёх presets запускается одним тапом, а одновременно активен только
  один timer.
- Single tap не отменяет countdown; double tap отменяет его предсказуемо.
- Countdown/recovery, overdue display, dismissal и alert policy соответствуют
  PRD на target device where device verification is required.

## Acceptance criteria

- Three presets имеют accepted defaults, duration ranges, labels and colors.
- Countdown occupies the clock area, keeps correct lifecycle across accepted
  interruptions and exposes the protected-cancel hint.
- Completion renders fullscreen overdue state with full elapsed counter and
  accepted dismissal semantics.
- Audio uses the accepted built-in set/default/ramp/30-minute cap and respects
  Android silent/DND; visual state does not depend on audio permission.

## Features

- [FT-005 — Preset timer configuration](../features/FT-005-timer-presets.md)
- [FT-006 — Countdown lifecycle and cancellation](../features/FT-006-countdown-lifecycle.md)
- [FT-007 — Overdue state and alert](../features/FT-007-overdue-alert.md)

## Sources and constraints

- [.memory-bank/prd.md](../prd.md): `PRD-FR-023`–`PRD-FR-031`, `PRD-AC-004`–
  `PRD-AC-005`, `PRD-AC-008`.
- [.memory-bank/invariants.md](../invariants.md): single active timer, recovery,
  protected cancellation and visual overdue rules.
- [.memory-bank/states/lifecycle-map.md](../states/lifecycle-map.md): timer
  lifecycle transitions.
- [.memory-bank/contracts/boundary-map.md](../contracts/boundary-map.md): Android
  OS lifecycle and audio responsibility hints.

## Design status

Epic remains `draft`; timer persistence/runtime and alert boundaries are now
registered, while executable feature design follows the Foundation Gate.
