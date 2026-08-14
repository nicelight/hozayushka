---
description: L2 epic for current weather cards, local weather context and forecast views.
status: draft
id: EP-002
lifecycle: planned
last_updated: 2026-08-12
---
# EP-002 — Weather context and forecasts

## Value

Владелец получает краткий, выразительный погодный контекст выбранного provider
на основном экране, сохраняет совпадающий cache доступным offline в пределах
freshness rule и может открыть почасовой либо 10-позиционный long-term forecast
без выхода из glanceable experience.

## Success metrics

- Четыре карточки всегда сохраняют порядок, даты, empty state и accepted
  temperature/pseudo-glass presentation.
- Cached weather моложе 24 часов остаётся доступной без сети только для
  совпадающего provider; cache/history разных providers не смешиваются.
- Hourly view требует ровно восемь фиксированных slots. Long-term view сохраняет
  10 positions: Open-Meteo заполняет 10, OpenWeather — 8 и оставляет 2 empty.
- Ошибка выбранного provider не меняет selection и не запускает automatic
  fallback; forecast views корректно закрываются.

## Acceptance criteria

- Main cards показывают yesterday/today/tomorrow/day-after в принятой композиции,
  включая first-run yesterday и pressure-trend rules.
- Refresh выполняется только для выбранного provider после launch, валидной
  смены города/provider и каждые 30 минут; cache и 7-day local history сохраняют
  provider identity. Day/night selection, unknown-condition fallback и
  temperature palette следуют clarified PRD.
- Today открывает hourly view только при наличии всех восьми фиксированных
  provider slots; Tomorrow/Day-after открывают общий long-term view только при
  полном provider-supported наборе — 10 Open-Meteo или 8 OpenWeather records.
- Десятипозиционная long-term проекция честно оставляет последние две
  OpenWeather positions unavailable/empty без синтеза или Open-Meteo data.
- Недостающие данные оставляют main display и показывают принятое сообщение;
  forecast exit flow одинаков для двух видов forecast.

## Features

- [FT-002 — Main weather cards and local context](../features/FT-002-weather-cards-context.md)
- [FT-003 — Hourly forecast view](../features/FT-003-hourly-forecast.md)
- [FT-004 — Ten-position long-term forecast view](../features/FT-004-ten-day-forecast.md)

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

Epic remains `draft` with lifecycle `planned`. Existing W3/W4/W5/W14/W15 task
evidence remains historical brownfield evidence; in particular, W15 proves the
superseded Yandex-only adapter and does not satisfy the current selectable
Open-Meteo/OpenWeather product acceptance.

The Global Backbone is `complete` at Planning Revision `2`; the provider
boundary, Weather Context ownership, cache identity, strict hourly and
10-versus-8+2 contracts are authoritative. FT-002, FT-003 and FT-004
feature-level design reconciliation is `complete`; Revision-2 execution now
records failed W17, completed W20 repair, completed W18 and completed W19
tasks. W16 remains a completed Settings prerequisite.

W17 implemented the production migration facts verified by the final
functional `PASS`: exactly Open-Meteo and OpenWeather remain, Yandex is removed,
selected-only dispatch and provider/location state isolation are present, and
credential evidence remains redacted. The accepted outcome nevertheless
failed required semantic verification because first-time OpenWeather selection
refreshes before key entry, while later valid-key save causes no refresh and
leaves an obsolete missing-key error current. Authoritative TASK-020 is
`failed` after `3/3` unsuccessful attempts. Completed TASK-021 owns the current
selected-provider hourly completeness delta under REQ-009, and completed
TASK-022 owns the current long-term capability delta under REQ-010.

EP-002 stays `draft` with lifecycle `planned`; FT-002 and FT-003 remain
planned, while FT-004 is `implemented` after W5's AC-003/AC-004 evidence,
W19's fresh AC-001/AC-002/AC-005/AC-006 evidence and the feature-level
semantic-pass. REQ-009 and REQ-010 are implemented from the reconciled W18/W19
outcomes. W20, W18 and W19 are complete on the authorized host/build/static/
redacted route; their evidence is linked from the corresponding features and
implementation plans. No fourth TASK-020 execution or scheduler promotion is
eligible. EP-004 remains separate and `planned`; its FT-008/FT-009 ownership
and existing evidence are unchanged. Device/live-provider evidence remains
`DEFERRED`, with no runtime `PASS` claim.
