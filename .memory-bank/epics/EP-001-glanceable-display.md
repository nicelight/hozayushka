---
description: L2 epic for the always-visible kitchen clock and main display interaction surface.
status: draft
id: EP-001
lifecycle: planned
last_updated: 2026-08-03
---
# EP-001 — Glanceable main display

## Value

Владелец получает спокойный fullscreen display, где время читается издалека и
остаётся главным элементом, а дата, город и точки входа в таймеры/settings
доступны без навигационного шума.

## Success metrics

- `HH:mm` визуально доминирует и читается на целевом 1280×720 landscape display.
- Основной экран сохраняет согласованную композицию при наличии и отсутствии
  weather data.
- Запуск Settings из city interaction не нарушает основной display flow.

## Acceptance criteria

- Landscape fullscreen, скрытые системные панели и keep-screen-on соблюдаются.
- Часы, дата, город, weather-card area и preset-button area находятся в
  принятой композиции; colon имеет online/offline/timer состояния.
- Device timezone используется для больших часов и основной даты, а выбранный
  city/API timezone не меняет их отображение.
- No-city and city-hold behavior открывают Settings согласно PRD, а selected-city
  short tap остаётся no-op.

## Features

- [FT-001 — Main clock and display shell](../features/FT-001-main-clock-display.md)

## Sources and constraints

- [.memory-bank/prd.md](../prd.md): `PRD-FR-001`–`PRD-FR-006`, `PRD-FR-039`,
  `PRD-NFR-001`–`PRD-NFR-003`, `PRD-AC-001`, `PRD-AC-009`.
- [.memory-bank/invariants.md](../invariants.md): clock dominance and accepted
  local-only product boundaries.
- [.memory-bank/user-scenarios.md](../user-scenarios.md): core glance scenario.

## Design status

Epic remains `draft`; its global architecture and platform boundary are now
registered, while display feature design follows the Foundation Gate.
