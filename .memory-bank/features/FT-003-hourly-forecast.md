---
description: L3 feature for the eight-slot hourly forecast screen and its shared exit flow.
status: draft
id: FT-003
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

### FT-003-AC-001

- REQ: REQ-009

**Complete hourly entry.** Today opens this screen only when required hourly
data exists.

### FT-003-AC-002

- REQ: REQ-009

**Eight accepted slots.** Exactly eight cards appear in two rows of four: six
slots for the current day and 00:00/03:00 for the following day.

### FT-003-AC-003

- REQ: REQ-009, REQ-022

**Shared card presentation.** Cards use the accepted day-after visual style:
temperature background, glass-temperature and weather illustration, without
pressure arrow; slot time replaces calendar date.

### FT-003-AC-004

- REQ: REQ-009

**Shared exit flow.** Screen auto-closes after 3 seconds without interaction.
Single tap cancels auto-close and shows the accepted hint; double tap closes;
hold keeps it open and release closes immediately.

### FT-003-AC-005

- REQ: REQ-009, REQ-026

**Missing-data fallback.** Missing hourly data keeps the main display and shows
`Почасовой прогноз еще не подгрузился`.

## Edge / failure behavior

- Missing or incomplete hourly fields never produce an invented slot or a
  misleading screen.
- Weather Context owns normalized hourly data and the availability/completeness
  predicate; Forecast Sessions owns the user-facing session creation/rejection,
  transient state and gestures.
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

## W4 implementation evidence

The W4 boundary preserves `TASK-005-T3-FT-003-W4` as failed historical
evidence and records `TASK-012-T3-FT-003-W4` as done after fresh functional
`PASS` and required T3 semantic `semantic-pass`. The follow-up repaired only
the supported full-day provider normalization path; it did not replace the
failed task, alter the public eight-slot contract or close the whole feature.
See the [historical functional report](../../.tasks/TASK-005-T3-FT-003-W4/TASK-005-T3-FT-003-W4-S-VERIFY-final-report-docs-02.md),
[historical semantic report](../../.tasks/TASK-005-T3-FT-003-W4/TASK-005-T3-FT-003-W4-S-RED-VERIFY-final-report-docs-01.md),
[repair functional report](../../.tasks/TASK-012-T3-FT-003-W4/TASK-012-T3-FT-003-W4-S-VERIFY-final-report-docs-01.md),
[repair semantic report](../../.tasks/TASK-012-T3-FT-003-W4/TASK-012-T3-FT-003-W4-S-RED-VERIFY-final-report-docs-01.md)
and [target-device receipt](../../.tasks/TASK-012-T3-FT-003-W4/target-device.md).
Target-device evidence remains `DEFERRED` and non-blocking with residual risk;
no runtime `PASS` is claimed. Feature lifecycle remains `planned` pending an
explicit lifecycle owner decision.

## W5 implementation evidence

The W5 boundary records `TASK-013-T3-FT-003-W5` as done after fresh functional
`PASS` and required T3 semantic `semantic-pass`. The follow-up establishes the
Today entry/fallback path, shared session timing/gestures and the minimum
consumer regression for the TASK-012 normalized eight-slot projection; it does
not reopen TASK-005, repeat TASK-012 provider normalization or change the
accepted contracts. See the [functional report](../../.tasks/TASK-013-T3-FT-003-W5/TASK-013-T3-FT-003-W5-S-VERIFY-final-report-docs-01.md),
[semantic report](../../.tasks/TASK-013-T3-FT-003-W5/TASK-013-T3-FT-003-W5-S-RED-VERIFY-final-report-docs-01.md)
and [target-device receipt](../../.tasks/TASK-013-T3-FT-003-W5/target-device.md).
Target-device evidence remains `DEFERRED` and non-blocking with residual risk;
no runtime `PASS` is claimed. Feature lifecycle remains `planned`; no feature
closure or scheduler promotion is inferred by this boundary sync.

## SDD Design Gate

Global backbone is complete at Planning Revision `1` and the Foundation Gate
is closed; feature-level design is complete for task planning.

Applicable global specs: [Boundary Map](../contracts/boundary-map.md),
[Capability Interfaces](../contracts/capability-interfaces.md), [Weather Provider](../contracts/weather-provider.md),
[Weather Card Presentation](../contracts/weather-card-presentation.md),
[Platform Runtime](../contracts/platform-runtime.md), [Lifecycle Map](../states/lifecycle-map.md)
and [Runtime Verification](../testing/runtime-verification.md).
FT-003 hourly mapping, data ownership, public session surface and lifecycle
details are covered by the linked subject blocks above. No unresolved
decomposition-relevant design marker remains.
