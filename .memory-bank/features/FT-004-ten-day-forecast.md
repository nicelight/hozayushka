---
description: L3 feature for the shared ten-position long-term forecast screen.
status: draft
id: FT-004
epic: EP-002
lifecycle: implemented
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
# FT-004 — Ten-position long-term forecast view

## Product outcome

Владелец открывает из Tomorrow или Day-after один общий экран из десяти
датированных positions: Open-Meteo заполняет все десять, а OpenWeather честно
показывает восемь поддерживаемых records и две unavailable/empty positions.

## Requirements

- REQ-010, REQ-022, REQ-026.

## Use cases

1. Владелец коротко тапает Tomorrow или Day-after при полном
   provider-supported наборе: 10 Open-Meteo или 8 OpenWeather daily records.
2. Владелец читает десять последовательных positions в двух рядах по пять;
   последние две OpenWeather positions остаются честно пустыми.
3. Владелец закрывает экран auto-close, single/double tap или hold/release.

## Acceptance Criteria

### FT-004-AC-001

**Complete long-term entry.** Both Tomorrow and Day-after open the same screen
only when the selected provider's complete supported daily set exists: 10
Open-Meteo or 8 OpenWeather records.
- REQ: REQ-010, REQ-026

- Verification: deterministic entry scenarios for complete and incomplete provider-supported sets.

### FT-004-AC-002

**Ten ordered daily positions.** Exactly ten dated positions appear in two rows
of five, starting with selected-city today and including the next nine calendar
days.
- REQ: REQ-010, REQ-022

- Verification: exact ten-position date/order/layout projection check.

### FT-004-AC-003

**Shared daily-card presentation.** Available cards show `dd`, temperature
background, temperature and weather illustration without pressure arrows;
day/night selection follows main-card rules.
- REQ: REQ-010, REQ-022, REQ-026

- Verification: available-card presentation and selected-city day/night inspection.

### FT-004-AC-004

**Shared exit flow.** Screen auto-closes after 3 seconds without interaction. Single tap cancels
  auto-close and shows `Дважды нажмите, чтобы закрыть`; double tap closes; hold
  keeps it open and release closes immediately.
- REQ: REQ-010

- Verification: deterministic session timer and gesture-transition scenarios.

### FT-004-AC-005

**Missing-data fallback.** Fewer than 10 Open-Meteo records or fewer than 8
OpenWeather records keeps the main display and shows
`Долгосрочный прогноз еще не подгрузился`.
- REQ: REQ-010, REQ-026

- Verification: provider-specific incomplete-set entry-rejection scenarios.

### FT-004-AC-006 — Honest provider-capability projection

- REQ: REQ-010, REQ-026

Open-Meteo fills all ten positions. OpenWeather fills only the first eight;
positions nine and ten remain unavailable/empty without temperature or weather
illustration. Those two positions do not make a complete eight-record
OpenWeather set unavailable and are never synthesized or filled from Open-Meteo.

- Verification: deterministic 10-record Open-Meteo and 8-record OpenWeather projection scenarios.

## Edge / failure behavior

- A set below the selected provider's supported completeness threshold never
  opens a misleading long-term screen. Coverage: FT-004-AC-001,
  FT-004-AC-005.
- Expected OpenWeather positions nine and ten remain empty without being treated
  as a provider error or borrowing Open-Meteo data. Coverage: FT-004-AC-002,
  FT-004-AC-006.
- Date boundaries follow selected-city API timezone while main clock/date retain
  device timezone. Coverage: FT-004-AC-002, FT-004-AC-003.
- This feature uses the shared forecast exit flow and does not introduce a
  second gesture contract. Coverage: FT-004-AC-004.

## Sources and normative inputs

- [.memory-bank/prd.md](../prd.md): `PRD-FR-019`–`PRD-FR-022`, `PRD-FR-039`,
  `PRD-AC-007`, `PRD-AC-009`.
- [.memory-bank/glossary.md](../glossary.md): accepted 10-position forecast
  vocabulary.
- [.memory-bank/states/lifecycle-map.md](../states/lifecycle-map.md): forecast
  session lifecycle.

## Verification targets

- `PRD-AC-007`, `PRD-FR-019`–`PRD-FR-022`, `PRD-FR-039`.

## Historical W5 implementation evidence

The W5 record below proves the pre-migration ten-filled-card contract. It remains
brownfield evidence and does not prove the accepted OpenWeather 8+2 projection.

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

The Global Backbone is `complete` at Planning Revision `2`, Foundation
revalidation is successful and the Gate anchors remain closed. Provider-
supported completeness and the ten-position Open-Meteo 10/OpenWeather 8+2
projection are reconciled into `TASK-022-T2-FT-004-W19`; feature-level design
is `complete`. Existing W5 task identity, terminal state and evidence remain
unchanged.

Current Revision-2 ownership is exact: W19 owns AC-001/AC-002/AC-005/AC-006;
W5 retains unchanged AC-003/AC-004.

Applicable global specs: [Boundary Map](../contracts/boundary-map.md),
[Capability Interfaces](../contracts/capability-interfaces.md), [Weather Provider](../contracts/weather-provider.md),
[Platform Runtime](../contracts/platform-runtime.md), [Lifecycle Map](../states/lifecycle-map.md)
and [Runtime Verification](../testing/runtime-verification.md). These are the
authoritative global inputs; historical TASK-006 evidence remains brownfield
evidence and does not complete feature-level migration planning.

## Semantic Verification

Feature-level Revision-2 red verification found no material semantic break for
W19-owned AC-001/AC-002/AC-005/AC-006. The independent report confirms the
selected-city ten-date horizon, Open-Meteo `10/10`, OpenWeather `8+2` dated
empty projection, exact one-short unavailable behavior, provider/cache/history
isolation, preserved shared presentation/session boundaries and synthetic /
redacted evidence. Target Android/custom-ROM and live-provider/network proof
remains `DEFERRED`; no runtime `PASS` is claimed.

Report: [FT-004 red-verification report](../../.tasks/FT-004/FT-004-S-RED-VERIFY-final-report-docs-01.md).

SEMANTIC_VERDICT: semantic-pass

## W19 provider-capability completion boundary

The authoritative
[`TASK-022-T2-FT-004-W19`](../tasks/TASK-022-T2-FT-004-W19.task.json) is `done`
after executor `PASS_FOR_HANDOFF`, fresh functional `PASS` and the required
feature-level `semantic-pass`. W19 owns AC-001/AC-002/AC-005/AC-006 and its
claim-linked closure metadata points to the existing handoff, RED baseline,
completeness matrix, verification report, gate receipts and
[FT-004 semantic report](../../.tasks/FT-004/FT-004-S-RED-VERIFY-final-report-docs-01.md).

Together with the unchanged W5 evidence for AC-003/AC-004, the feature's
Revision-2 acceptance surface is implemented; REQ-010 is `implemented` in the
RTM. W18 and W20 remain `done`; TASK-020 remains failed historical evidence
after exhausted `3/3` attempts. Target Android/custom-ROM and live-provider/
network proof remains `DEFERRED`, with no runtime `PASS` claim. This boundary
does not close or promote EP-002, alter scheduler terminal state, or change
EP-004; the latter remains owned by FT-008/FT-009 and its existing lifecycle.
