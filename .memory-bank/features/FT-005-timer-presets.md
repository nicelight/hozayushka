---
description: L3 feature for the three preset timer definitions, labels and configuration outcome.
status: draft
id: FT-005
epic: EP-003
lifecycle: planned
last_updated: 2026-08-03
---
# FT-005 — Preset timer configuration

## Product outcome

Владелец имеет три понятных preset-кнопки и может независимо задать длительность
каждого preset в принятых пределах до запуска countdown.

## Requirements

- REQ-011.

## Use cases

1. Владелец видит три presets с defaults 3, 10 и 30 минут.
2. Владелец задаёт часы/минуты/секунды каждого preset.
3. Владелец понимает длительность по старшей ненулевой единице и цвету кнопки.

## Acceptance criteria

- Exactly three presets exist and each can be configured independently; no more
  than one timer can be active at a time.
- Inputs accept hours 0–99, minutes 0–59 and seconds 0–59; total duration must
  be greater than zero.
- Button labels show only the highest non-zero unit (`ч`, `м` or `с`) with
  floor rounding; initial durations are 3m, 10m and 30m.
- Buttons use the accepted orange, pink and purple neon outlines, and the
  selected/active visual state is available to the countdown feature.

## Edge / failure behavior

- Zero total duration or out-of-range field does not become an accepted preset;
  the last valid value remains available through Settings behavior.
- Fractional lower units do not appear in the label and are not rounded up.
- Preset configuration does not create a second active timer or alter overdue
  dismissal semantics.

## Sources and normative inputs

- [.memory-bank/prd.md](../prd.md): `PRD-FR-023`–`PRD-FR-025`, `PRD-FR-032`,
  `PRD-FR-037`–`PRD-FR-038`.
- [.memory-bank/invariants.md](../invariants.md): single active timer and
  accepted Settings validation boundary.
- [.memory-bank/states/lifecycle-map.md](../states/lifecycle-map.md): timer
  lifecycle entry from `idle`.

## Verification targets

- `PRD-FR-023`–`PRD-FR-025`, `PRD-AC-004`, `PRD-AC-006C`.

## SDD Design Gate

Global backbone is complete at Planning Revision `1`; feature-level design
remains draft until the Foundation Gate and `/feature-to-tasks`.

Applicable global specs: [Boundary Map](../contracts/boundary-map.md),
[Capability Interfaces](../contracts/capability-interfaces.md), [Local Data](../domains/local-data.md),
[Lifecycle Map](../states/lifecycle-map.md) and [Runtime Verification](../testing/runtime-verification.md).
The persistence primitive and UI implementation remain downstream.
