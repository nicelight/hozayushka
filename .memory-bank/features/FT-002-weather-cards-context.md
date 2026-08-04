---
description: L3 feature for main weather cards, freshness, local history and pressure context.
status: draft
id: FT-002
epic: EP-002
lifecycle: planned
last_updated: 2026-08-03
---
# FT-002 — Main weather cards and local context

## Product outcome

Владелец видит четыре различимые карточки вчера/сегодня/завтра/послезавтра с
актуальной погодой, температурным цветом и локально вычисленным pressure trend,
а при offline или stale data получает честное стабильное состояние.

## Requirements

- REQ-005, REQ-006, REQ-007, REQ-008, REQ-023, REQ-025, REQ-026.

## Use cases

1. Владелец быстро считывает четыре карточки в фиксированном порядке.
2. Владелец видит актуальный cache без сети и понимает empty state после
   истечения 24 часов.
3. Владелец получает yesterday card и pressure arrows после накопления local
   history.
4. Владелец видит neutral cloud fallback для неизвестного condition без crash.

## Acceptance criteria

- Cards always appear in yesterday/today/tomorrow/day-after order; Today is
  slightly larger and the other three have equal smaller size.
- Filled card contains illustration, temperature, calendar date and
  temperature-dependent background without textual day/weather labels.
- Day/night data follows the selected-city day/night rule; moon phase is used
  only when supplied, otherwise a regular moon is used.
- Temperature sign is shown only from −4 through +4 °C inclusive; all 78
  accepted compile-time HEX values are used with endpoint clamp, and accepted
  static pseudo-glass is shared by temperature and pressure arrows.
- Weather/current pressure refreshes after launch, city change and every 30
  minutes when network is available; last successful data is cached.
- Cache age up to 24 hours remains available offline. Older data renders all
  four cards as transparent contours without values, illustrations or arrows.
- Local history begins at installation and retains the accepted seven-day
  window. Current and yesterday trends use the accepted 3-hour/12-hour fallback
  and thresholds; absent history produces no arrow and first-run Yesterday is a
  dated empty contour without layout shift.

## Edge / failure behavior

- No city, missing key, provider failure or no network leaves clock and timers
  usable; cards show only the accepted available/empty state.
- Unknown condition or missing optional field uses the neutral cloud/available
  data fallback and never invents a textual condition or crashes.
- Insufficient pressure history suppresses arrows rather than fabricating a
  trend. Weather data before installation is never assumed.

## Sources and normative inputs

- [.memory-bank/prd.md](../prd.md): `PRD-FR-007`–`PRD-FR-018`, `PRD-NFR-004`–
  `PRD-NFR-005`, `PRD-AC-002`–`PRD-AC-003`, `PRD-AC-008`, `PRD-AC-010`.
- [.memory-bank/glossary.md](../glossary.md): weather, freshness, palette and
  pseudo-glass vocabulary.
- [.memory-bank/invariants.md](../invariants.md): cache, palette and visual
  constraints.
- [.memory-bank/states/lifecycle-map.md](../states/lifecycle-map.md): weather
  freshness lifecycle.

## Verification targets

- `PRD-AC-002`, `PRD-AC-003`, `PRD-AC-008`, `PRD-AC-010`,
  `PRD-FR-013`–`PRD-FR-018`.

## SDD Design Gate

Global backbone is complete at Planning Revision `1`; feature-level design
remains draft until the Foundation Gate and `/feature-to-tasks`.

Applicable global specs: [System Architecture](../architecture/system-architecture.md),
[Boundary Map](../contracts/boundary-map.md), [Capability Interfaces](../contracts/capability-interfaces.md),
[Weather Provider](../contracts/weather-provider.md), [Local Data](../domains/local-data.md),
[Lifecycle Map](../states/lifecycle-map.md) and [Runtime Verification](../testing/runtime-verification.md).
Provider field mapping, storage schema and feature verification remain
downstream decisions.
