---
description: L2 epic for weather access, offline location and alert/display personalization settings.
status: draft
id: EP-004
lifecycle: planned
last_updated: 2026-08-03
---
# EP-004 — Settings, location and personalization

## Value

Владелец один раз настраивает личный weather access, город и привычки alert/
visual display, после чего приложение сохраняет корректные значения и остаётся
используемым без лишних save dialogs или сетевой зависимости для выбора города.

## Success metrics

- Country/city selection полностью работает offline в принятом bundled dataset.
- API key и корректные settings values сохраняются локально без утечки и
  возвращаются после повторного открытия Settings.
- Glass preview реагирует live, а validation errors не уничтожают последнее
  корректное значение.

## Acceptance criteria

- API key, default/selected location, coordinates, timer preferences, sound,
  volume and glass intensity доступны в Settings согласно PRD.
- Country-first/city-scoped search использует accepted names/aliases and
  attribution; Google Services не требуются.
- Validation/network errors показываются inline, invalid input не сохраняется,
  valid input auto-saves, bottom Back и system Back возвращают main display.
- API key absent from APK/source/logs/evidence; visual preview uses production
  weather-card presentation and accepted fallback.

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

Epic remains `draft`; local data ownership and provider/Settings boundaries are
now registered, while concrete feature design follows the Foundation Gate.
