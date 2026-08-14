---
description: L2 epic for weather access, offline location and alert/display personalization settings.
status: draft
id: EP-004
lifecycle: planned
last_updated: 2026-08-11
---
# EP-004 — Settings, location and personalization

## Value

Владелец оставляет default Open-Meteo без key либо явно выбирает OpenWeather со
своим локальным key, настраивает город и привычки alert/visual display, после
чего приложение сохраняет корректные значения без лишних save dialogs или
сетевой зависимости для выбора города.

## Success metrics

- Country/city selection полностью работает offline в принятом bundled dataset.
- Provider selection и корректные settings values сохраняются и возвращаются
  после повторного открытия; OpenWeather key остаётся локальным без утечки, а
  Open-Meteo не требует key.
- Glass preview реагирует live, а validation errors не уничтожают последнее
  корректное значение.
- Settings показывают требуемые Open-Meteo и GeoNames attribution.

## Acceptance criteria

- Default Open-Meteo/no-key и explicit OpenWeather/local-key selection,
  default/selected location, coordinates, timer preferences, sound, volume and
  glass intensity доступны в Settings согласно PRD.
- Country-first/city-scoped search использует accepted names/aliases and
  attribution; Google Services не требуются.
- Validation/network errors показываются inline, invalid input не сохраняется,
  valid input auto-saves, bottom Back и system Back возвращают main display.
- OpenWeather key отсутствует в APK/source/logs/evidence; Open-Meteo path не
  использует key; provider failure не меняет selection и не сообщает о
  fallback. Visual preview uses production weather-card presentation and
  accepted fallback.
- Open-Meteo и GeoNames attribution показаны в принятом Settings flow.

## Features

- [FT-008 — Weather access and offline location settings](../features/FT-008-weather-location-settings.md)
- [FT-009 — Alert and glass personalization](../features/FT-009-personalization-settings.md)

## Sources and constraints

- [.memory-bank/prd.md](../prd.md): `PRD-FR-032`–`PRD-FR-039`, `PRD-AC-006`–
  `PRD-AC-006C`.
- [.memory-bank/invariants.md](../invariants.md): local key, offline location,
  no modal validation and no unaccepted settings scope.
- [.memory-bank/contracts/boundary-map.md](../contracts/boundary-map.md): local
  state, provider and bundled-data boundaries.
- [.memory-bank/glossary.md](../glossary.md): glass intensity and visual terms.

## Design status

Epic remains `draft` with lifecycle `planned`. The Global Backbone is
`complete` at Planning Revision `2`; default/no-key Open-Meteo, explicit
OpenWeather/local-key transport and attribution contracts are authoritative.
FT-008 feature-level design is `complete` and lifecycle is `implemented` after
`TASK-019-T3-FT-008-W16` closed with final Attempt-3 functional `PASS` and
semantic `semantic-pass`; its two unsuccessful attempts remain task-traceable.
The transition deny for provider-unidentified legacy access remains an explicit
input to planned TASK-020 and is not a new epic behavior. FT-009 remains
`planned`, so EP-004 remains `planned`; no epic promotion is inferred.
Physical-device/live-provider evidence remains `DEFERRED`, with no runtime
`PASS` claim.
