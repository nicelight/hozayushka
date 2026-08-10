---
description: L2 epic for preset timer setup, resilient countdown and overdue alert.
status: draft
id: EP-003
lifecycle: planned
last_updated: 2026-08-09
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
  PRD; target-device verification remains deferred/non-blocking for the T3
  product queue when no authorized target is available, with residual risk
  recorded and no runtime `PASS` claim without a target.

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

Epic remains `draft` with lifecycle `planned`. The W6 boundary records
`TASK-007-T3-FT-005-W6` as `done` after the corrected attempt-2 path and fresh
attempt-3 functional `PASS` plus semantic `semantic-pass`. The W7 boundary
records `TASK-008-T3-FT-006-W7` as `done` after fresh functional `PASS` plus
semantic `semantic-pass`; FT-006 lifecycle is `implemented`, while
target-device evidence remains `DEFERRED` and non-blocking with no runtime
`PASS` claim. The W8 boundary records `TASK-009-T3-FT-007-W8` as `done` after
retry-2 fresh functional `PASS` plus semantic `semantic-pass`; FT-007 lifecycle
is `implemented`, while target-device evidence remains `DEFERRED` and
non-blocking with no runtime `PASS` claim. EP-003 remains `planned` because
FT-005/REQ-011 is still planned. No epic closure, promotion or dependent-state
transition is inferred by this sync.
The W11 semantic-fail evidence also records a non-city active-countdown
double-tap cancellation defect against the existing FT-006 contract; FT-006
and REQ-013 lifecycle state is preserved and the repair route was the indexed
FT-001 W12 task. W12 is now `done` after fresh functional `PASS` and semantic
`semantic-pass`; its public Main Display dispatch repair restored the existing
REQ-013 path without changing Timer & Alert ownership or semantics. Samsung,
custom-ROM and 1280x720 physical-device evidence remains `DEFERRED`, and no
runtime target `PASS` is claimed. EP-003 remains `planned` because FT-005 /
REQ-011 is still planned; no epic closure, promotion or dependent-state
transition is inferred by this boundary sync.
