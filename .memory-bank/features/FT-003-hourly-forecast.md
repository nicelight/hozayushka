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
last_updated: 2026-08-12
---
# FT-003 — Hourly forecast view

## Product outcome

Владелец открывает из карточки Today компактный hourly forecast восьми
фиксированных city-local slots и возвращается на main display без
навигационного шума; неполный ответ выбранного provider не создаёт
недостоверный экран.

## Requirements

- REQ-009, REQ-022, REQ-026.

## Use cases

1. Владелец коротко тапает Today при наличии hourly data.
2. Владелец читает 06:00, 09:00, 12:00, 15:00, 18:00, 21:00, 00:00 и 03:00 в
   двух рядах по четыре.
3. Владелец использует auto-close, single-tap hint, double tap или hold/release
   для выхода.

## Acceptance Criteria

### FT-003-AC-001

- REQ: REQ-009

**Complete hourly entry.** Today opens this screen only when the selected
provider supplies all eight fixed city-local slots. For OpenWeather this
includes already elapsed current-day slots.

- Verification: deterministic complete/eight-slot and incomplete-slot entry scenarios.

### FT-003-AC-002

- REQ: REQ-009

**Eight accepted slots.** Exactly eight cards appear in two rows of four: six
slots for the current day and 00:00/03:00 for the following day.

- Verification: exact slot-label/order/layout projection check.

### FT-003-AC-003

- REQ: REQ-009, REQ-022

**Shared card presentation.** Cards use the accepted day-after visual style:
temperature background, glass-temperature and weather illustration, without
pressure arrow; slot time replaces calendar date.

- Verification: hourly-card presentation inspection against the shared day-after rules.

### FT-003-AC-004

- REQ: REQ-009

**Shared exit flow.** Screen auto-closes after 3 seconds without interaction.
Single tap cancels auto-close and shows the accepted hint; double tap closes;
hold keeps it open and release closes immediately.

- Verification: deterministic session timer and gesture-transition scenarios.

### FT-003-AC-005

- REQ: REQ-009, REQ-026

**Missing-data fallback.** Missing hourly data, including any absent OpenWeather
fixed slot, keeps the main display and shows
`Почасовой прогноз еще не подгрузился`; the missing slot is never synthesized
or borrowed from Open-Meteo.

- Verification: one-missing-slot scenarios across all eight positions and both providers.

## Edge / failure behavior

- Missing or incomplete hourly fields never produce an invented or
  cross-provider slot or a misleading screen. Coverage: FT-003-AC-001,
  FT-003-AC-005.
- Weather Context owns normalized hourly data and the availability/completeness
  predicate; Forecast Sessions owns the user-facing session creation/rejection,
  transient state and gestures. Coverage: FT-003-AC-001, FT-003-AC-004,
  FT-003-AC-005.
- City/timezone changes are composed with weather settings; slot labels follow
  selected-city API timezone, not device timezone. Coverage: FT-003-AC-002,
  FT-003-AC-003.
- Weather-card visual rules are reused without creating a second material or
  pressure-trend rule. Coverage: FT-003-AC-003.

## Sources and normative inputs

- [.memory-bank/prd.md](../prd.md): `PRD-FR-019A`–`PRD-FR-019C`, `PRD-FR-022`,
  `PRD-FR-039`, `PRD-AC-007A`, `PRD-AC-009`.
- [.memory-bank/glossary.md](../glossary.md): hourly forecast and pseudo-glass
  vocabulary.
- [.memory-bank/states/lifecycle-map.md](../states/lifecycle-map.md): forecast
  session lifecycle.

## Verification targets

- `PRD-AC-007A`, `PRD-FR-019A`–`PRD-FR-019C`, `PRD-FR-022`, `PRD-FR-039`.

## Historical W4 implementation evidence

W4/W5 records below are brownfield evidence for the pre-migration provider
contract. They do not prove OpenWeather eight-slot completeness or the current
no-mixing acceptance.

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

## Historical W5 implementation evidence

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

The Global Backbone is `complete` at Planning Revision `2`, Foundation
revalidation is successful and the Gate anchors remain closed. Exact selected-
provider timezone/mapping obligations, strict eight-slot completeness and
no-cross-provider synthesis are reconciled into
`TASK-021-T2-FT-003-W18`; feature-level design is `complete`. Existing failed
W4 and done W4/W5 records and their terminal evidence remain unchanged.

Current Revision-2 ownership is exact: W18 owns AC-001/AC-005, done W4 retains
unchanged AC-002/AC-003, and done W5 retains unchanged AC-004.

Applicable global specs: [Boundary Map](../contracts/boundary-map.md),
[Capability Interfaces](../contracts/capability-interfaces.md), [Weather Provider](../contracts/weather-provider.md),
[Weather Card Presentation](../contracts/weather-card-presentation.md),
[Platform Runtime](../contracts/platform-runtime.md), [Lifecycle Map](../states/lifecycle-map.md)
and [Runtime Verification](../testing/runtime-verification.md). These are the
authoritative global inputs for subsequent feature planning.

## W18 selected-provider completeness boundary

The authoritative
[`TASK-021-T2-FT-003-W18`](../tasks/TASK-021-T2-FT-003-W18.task.json) is `done`
after executor `PASS_FOR_HANDOFF`, fresh functional `PASS` and final independent
semantic `semantic-pass`. W18 owns only `FT-003-AC-001 / REQ-009` and
`FT-003-AC-005 / REQ-009, REQ-026`: both selected providers require all eight
fixed city-local slots, and every one of the sixteen one-missing-slot cases,
including elapsed current-day OpenWeather positions, remains on Main Display
with the exact unavailable message and no synthesis, borrowing or fallback.

Current evidence is linked from the [executor handoff](../../.protocols/TASK-021-T2-FT-003-W18/handoff.md),
[fresh functional verification](../../.protocols/TASK-021-T2-FT-003-W18/verification.md),
[verifier-owned evidence](../../.tasks/TASK-021-T2-FT-003-W18/verifier-owned-evidence.md),
[hourly completeness matrix](../../.tasks/TASK-021-T2-FT-003-W18/hourly-completeness-matrix.json),
[final semantic verification](../../.protocols/TASK-021-T2-FT-003-W18/red-verification.md)
and [semantic report](../../.tasks/TASK-021-T2-FT-003-W18/TASK-021-T2-FT-003-W18-S-RED-VERIFY-final-report-docs-01.md).
The claim-linked executor RED/GREEN and fresh verifier evidence remain scoped to
AC-001/AC-005; W4/W5 AC-002/AC-003/AC-004 evidence remains separate ownership.

REQ-009 is now `implemented` in the RTM for the reconciled hourly outcome, while
FT-003 and EP-002 lifecycle remain `planned`; this boundary does not infer
feature closure, promotion or a feature-level semantic lifecycle decision.
Failed [`TASK-020-T3-FT-002-W17`](../tasks/TASK-020-T3-FT-002-W17.task.json)
remains historical migration evidence after exhausted `3/3` attempts and is not
reopened. [`TASK-022-T2-FT-004-W19`](../tasks/TASK-022-T2-FT-004-W19.task.json)
remains blocked without execution or acceptance evidence; downstream scheduler
recovery remains external.

Target device/emulator rendering, live provider/subscription behavior and runtime
network compatibility remain `DEFERRED` by the explicit boundary; no runtime
`PASS` is claimed.
