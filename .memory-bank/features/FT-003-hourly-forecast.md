---
description: L3 feature for the eight-slot hourly forecast screen and its shared exit flow.
status: draft
id: FT-003
epic: EP-002
lifecycle: planned
last_updated: 2026-08-03
---
# FT-003 — Hourly forecast view

## Product outcome

Владелец открывает из карточки Today компактный hourly forecast ближайших
принятых слотов и возвращается на main display без навигационного шума.

## Requirements

- REQ-009, REQ-022, REQ-026.

## Use cases

1. Владелец коротко тапает Today при наличии hourly data.
2. Владелец читает 06:00, 09:00, 12:00, 15:00, 18:00, 21:00, 00:00 и 03:00 в
   двух рядах по четыре.
3. Владелец использует auto-close, single-tap hint, double tap или hold/release
   для выхода.

## Acceptance criteria

- Today opens this screen only when required hourly data exists.
- Exactly eight cards appear in two rows of four: six slots for the current day
  and 00:00/03:00 for the following day.
- Cards use the accepted day-after visual style: temperature background,
  glass-temperature and weather illustration, without pressure arrow; slot time
  replaces calendar date.
- Screen auto-closes after 3 seconds without interaction. Single tap cancels
  auto-close and shows the accepted hint; double tap closes; hold keeps it open
  and release closes immediately.
- Missing hourly data keeps the main display and shows
  `Почасовой прогноз еще не подгрузился`.

## Edge / failure behavior

- Missing or incomplete hourly fields never produce an invented slot or a
  misleading screen.
- City/timezone changes are composed with weather settings; slot labels follow
  selected-city API timezone, not device timezone.
- Weather-card visual rules are reused without creating a second material or
  pressure-trend rule.

## Sources and normative inputs

- [.memory-bank/prd.md](../prd.md): `PRD-FR-019A`–`PRD-FR-019C`, `PRD-FR-022`,
  `PRD-FR-039`, `PRD-AC-007A`, `PRD-AC-009`.
- [.memory-bank/glossary.md](../glossary.md): hourly forecast and pseudo-glass
  vocabulary.
- [.memory-bank/states/lifecycle-map.md](../states/lifecycle-map.md): forecast
  session lifecycle.

## Verification targets

- `PRD-AC-007A`, `PRD-FR-019A`–`PRD-FR-019C`, `PRD-FR-022`, `PRD-FR-039`.

## SDD Design Gate

Global backbone is complete at Planning Revision `1`; feature-level design
remains draft until the Foundation Gate and `/feature-to-tasks`.

Applicable global specs: [Boundary Map](../contracts/boundary-map.md),
[Capability Interfaces](../contracts/capability-interfaces.md), [Weather Provider](../contracts/weather-provider.md),
[Platform Runtime](../contracts/platform-runtime.md), [Lifecycle Map](../states/lifecycle-map.md)
and [Runtime Verification](../testing/runtime-verification.md).
Provider hourly mapping and forecast-session details remain downstream.
