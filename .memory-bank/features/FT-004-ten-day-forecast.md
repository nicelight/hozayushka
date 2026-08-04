---
description: L3 feature for the shared ten-day forecast screen.
status: draft
id: FT-004
epic: EP-002
lifecycle: planned
last_updated: 2026-08-03
---
# FT-004 — Ten-day forecast view

## Product outcome

Владелец открывает из Tomorrow или Day-after один общий экран прогноза на
текущий день выбранного города и следующие девять дней.

## Requirements

- REQ-010, REQ-022, REQ-026.

## Use cases

1. Владелец коротко тапает Tomorrow или Day-after при наличии 10-day data.
2. Владелец читает десять последовательных карточек в двух рядах по пять.
3. Владелец закрывает экран auto-close, single/double tap или hold/release.

## Acceptance criteria

- Both Tomorrow and Day-after open the same screen only when the saved 10-day
  forecast exists.
- Exactly ten cards appear in two rows of five, starting with selected-city
  today and including the next nine calendar days.
- Cards show `dd`, temperature background, temperature and weather illustration,
  without pressure arrows; day/night selection follows main-card rules.
- Screen auto-closes after 3 seconds without interaction. Single tap cancels
  auto-close and shows `Дважды нажмите, чтобы закрыть`; double tap closes; hold
  keeps it open and release closes immediately.
- Missing long-term data keeps the main display and shows
  `Долгосрочный прогноз еще не подгрузился`.

## Edge / failure behavior

- A missing or partial forecast never opens a misleading empty long-term screen.
- Date boundaries follow selected-city API timezone while main clock/date retain
  device timezone.
- This feature uses the shared forecast exit flow and does not introduce a
  second gesture contract.

## Sources and normative inputs

- [.memory-bank/prd.md](../prd.md): `PRD-FR-019`–`PRD-FR-022`, `PRD-FR-039`,
  `PRD-AC-007`, `PRD-AC-009`.
- [.memory-bank/glossary.md](../glossary.md): accepted 10-day forecast
  vocabulary.
- [.memory-bank/states/lifecycle-map.md](../states/lifecycle-map.md): forecast
  session lifecycle.

## Verification targets

- `PRD-AC-007`, `PRD-FR-019`–`PRD-FR-022`, `PRD-FR-039`.

## SDD Design Gate

Global backbone is complete at Planning Revision `1`; feature-level design
remains draft until the Foundation Gate and `/feature-to-tasks`.

Applicable global specs: [Boundary Map](../contracts/boundary-map.md),
[Capability Interfaces](../contracts/capability-interfaces.md), [Weather Provider](../contracts/weather-provider.md),
[Platform Runtime](../contracts/platform-runtime.md), [Lifecycle Map](../states/lifecycle-map.md)
and [Runtime Verification](../testing/runtime-verification.md).
Forecast field mapping, date composition and session details remain downstream.
