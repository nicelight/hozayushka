---
description: L3 feature for the shared ten-day forecast screen.
status: draft
id: FT-004
epic: EP-002
lifecycle: planned
spec_design_status: complete
spec_design_links:
  - .memory-bank/contracts/boundary-map.md
  - .memory-bank/contracts/capability-interfaces.md
  - .memory-bank/contracts/weather-provider.md
  - .memory-bank/contracts/weather-card-presentation.md
  - .memory-bank/domains/local-data.md
  - .memory-bank/states/lifecycle-map.md
  - .memory-bank/contracts/platform-runtime.md
  - .memory-bank/testing/runtime-verification.md
last_updated: 2026-08-08
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

### FT-004-AC-001

**Complete long-term entry.** Both Tomorrow and Day-after open the same screen only when the saved 10-day
  forecast exists.
- REQ: REQ-010, REQ-026

### FT-004-AC-002

**Ten ordered daily cards.** Exactly ten cards appear in two rows of five, starting with selected-city
  today and including the next nine calendar days.
- REQ: REQ-010, REQ-022

### FT-004-AC-003

**Shared daily-card presentation.** Cards show `dd`, temperature background, temperature and weather illustration,
  without pressure arrows; day/night selection follows main-card rules.
- REQ: REQ-010, REQ-022, REQ-026

### FT-004-AC-004

**Shared exit flow.** Screen auto-closes after 3 seconds without interaction. Single tap cancels
  auto-close and shows `Дважды нажмите, чтобы закрыть`; double tap closes; hold
  keeps it open and release closes immediately.
- REQ: REQ-010

### FT-004-AC-005

**Missing-data fallback.** Missing long-term data keeps the main display and shows
  `Долгосрочный прогноз еще не подгрузился`.
- REQ: REQ-010, REQ-026

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

## W5 implementation evidence

The W5 boundary records `TASK-006-T3-FT-004-W5` as done after independent
functional `PASS` and required T3 semantic `semantic-pass`. The evidence
covers the isolated Weather Context save/reload, Tomorrow/Day-after entry,
exact ten-card projection, shared card presentation, completeness fallback and
shared session exit flow. See the [functional report](../../.tasks/TASK-006-T3-FT-004-W5/TASK-006-T3-FT-004-W5-S-VERIFY-final-report-docs-01.md),
[semantic report](../../.tasks/TASK-006-T3-FT-004-W5/TASK-006-T3-FT-004-W5-S-RED-VERIFY-final-report-docs-01.md)
and [target-device receipt](../../.tasks/TASK-006-T3-FT-004-W5/target-device.md).
Target-device evidence remains `DEFERRED` and non-blocking with residual risk;
no runtime `PASS` is claimed. Feature lifecycle remains `planned`; no feature
closure, promotion or dependent-state transition is inferred by this boundary
sync.

## SDD Design Gate

Global backbone is complete at Planning Revision `1` and the Foundation Gate
is closed; feature-level design is complete for task planning.

Applicable global specs: [Boundary Map](../contracts/boundary-map.md),
[Capability Interfaces](../contracts/capability-interfaces.md), [Weather Provider](../contracts/weather-provider.md),
[Platform Runtime](../contracts/platform-runtime.md), [Lifecycle Map](../states/lifecycle-map.md)
and [Runtime Verification](../testing/runtime-verification.md).
The FT-004 field mapping, date composition and session details are evidenced by
the completed TASK-006 outcome; no new canonical spec or lifecycle decision is
created by this boundary sync.
