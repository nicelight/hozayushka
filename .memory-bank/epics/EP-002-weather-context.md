---
description: L2 epic for current weather cards, local weather context and forecast views.
status: draft
id: EP-002
lifecycle: planned
last_updated: 2026-08-08
---
# EP-002 — Weather context and forecasts

## Value

Владелец получает краткий, выразительный погодный контекст на основном экране,
сохраняет его доступным offline в пределах freshness rule и может открыть
почасовой либо 10-дневный forecast без выхода из glanceable experience.

## Success metrics

- Четыре карточки всегда сохраняют порядок, даты, empty state и accepted
  temperature/pseudo-glass presentation.
- Cached weather моложе 24 часов остаётся доступной без сети; более старые
  данные не показываются как актуальные.
- Forecast views показывают ровно принятые 8 hourly или 10 daily cards и
  корректно закрываются.

## Acceptance criteria

- Main cards показывают yesterday/today/tomorrow/day-after в принятой композиции,
  включая first-run yesterday и pressure-trend rules.
- Refresh, cache, 7-day local history, day/night selection, unknown-condition
  fallback и temperature palette следуют clarified PRD.
- Today открывает hourly view только при наличии hourly data; Tomorrow/Day-after
  открывают общий 10-day view только при наличии daily forecast.
- Недостающие данные оставляют main display и показывают принятое сообщение;
  forecast exit flow одинаков для двух видов forecast.

## Features

- [FT-002 — Main weather cards and local context](../features/FT-002-weather-cards-context.md)
- [FT-003 — Hourly forecast view](../features/FT-003-hourly-forecast.md)
- [FT-004 — Ten-day forecast view](../features/FT-004-ten-day-forecast.md)

## Sources and constraints

- [.memory-bank/prd.md](../prd.md): `PRD-FR-007`–`PRD-FR-022`, `PRD-AC-002`–
  `PRD-AC-003`, `PRD-AC-007`–`PRD-AC-010`.
- [.memory-bank/glossary.md](../glossary.md): accepted weather, freshness,
  palette, pseudo-glass and forecast vocabulary.
- [.memory-bank/states/lifecycle-map.md](../states/lifecycle-map.md): weather
  freshness and forecast-session hints.
- [.memory-bank/contracts/boundary-map.md](../contracts/boundary-map.md):
  weather-provider and bundled-location responsibility hints.

## Design status

Epic remains `draft` and its lifecycle remains `planned`. FT-002's W3
implementation and evidence are reconciled; FT-003's W4/W5 records preserve
TASK-005 as failed historical evidence, record TASK-012 as the
provider-normalization repair and TASK-013 as the entry/fallback/shared-session
follow-up; FT-004's W5 outcome is recorded by TASK-006 after its functional and
semantic verification. Target-only readability/runtime observations remain
deferred under the accepted policy. No epic or feature lifecycle decision,
closure, promotion or dependent-state transition is inferred by this boundary
sync.
