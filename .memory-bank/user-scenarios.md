---
description: Reviewed core user scenarios and decomposition implications for the clarified V1.
status: active
review_status: reviewed
last_updated: 2026-08-03
---
# User Scenarios

## Primary Actors

- **Владелец** — единственный пользователь, который вручную запускает
  приложение на стационарном кухонном смартфоне и взаимодействует коротким
  тапом, двойным тапом или удержанием.
- **Yandex Weather API** — внешний источник текущей погоды, давления и
  доступного прогноза.
- **Android OS** — владелец платформенных lifecycle, времени, сети,
  хранения/процесса и правил разрешения звука.

Источник: [.memory-bank/prd.md](prd.md), разделы `Users / Actors` и
`Integrations / Dependencies`.

## Core Scenarios

### 1. Быстрый взгляд и запуск preset timer

Владелец открывает fullscreen main display, считывает время, дату и доступную
погоду, затем одним тапом запускает один из трёх preset-таймеров. Countdown
занимает область часов, переживает временную остановку процесса, а single tap не
отменяет его. После завершения владелец отключает overdue state касанием.

Источник: [.memory-bank/prd.md](prd.md), `Goals`, `PRD-FR-001`–`PRD-FR-005`,
`PRD-FR-023`–`PRD-FR-031`, `PRD-AC-001`, `PRD-AC-004`–`PRD-AC-005`.

### 2. Просмотр свежей погоды и прогноза

Владелец видит четыре карточки в фиксированном порядке. При наличии данных он
открывает из «Сегодня» hourly view или из «Завтра»/«Послезавтра» общий
10-дневный forecast view. При отсутствии нужных данных приложение остаётся на
main display и показывает короткое сообщение.

Источник: [.memory-bank/prd.md](prd.md), `PRD-FR-007`–`PRD-FR-022` и
`PRD-AC-002`, `PRD-AC-007`–`PRD-AC-007A`.

### 3. Настройка location, API key и персональных предпочтений

Владелец открывает Settings, offline выбирает страну, затем город, вводит
личный API key и настраивает durations, sound, volume и glass intensity.
Корректные значения сохраняются автоматически; ошибки остаются рядом с
владеющим полем и не затирают последнее корректное значение.

Источник: [.memory-bank/prd.md](prd.md), `PRD-FR-032`–`PRD-FR-039` и
`PRD-AC-006`–`PRD-AC-006C`.

## Out Of Scope Scenarios

- Backend, cloud sync, accounts, multi-user access или общий встроенный API key.
- Google Services, автозапуск и восстановление timer после reboot.
- История погоды до установки, Telegram-бот и Android TTS.
- Дополнительные Settings/элементы V1 без отдельного operator decision.
- Forecast view без требуемых weather/hourly data.

Источник: [.memory-bank/prd.md](prd.md), `Non-goals` и `Edge Cases / Failure Handling`.

## Architecture/Domain Implications

- Main display, timer/alert lifecycle, weather/cache/history, forecast sessions
  и Settings/location должны быть различимыми decomposition concerns.
- Weather provider и Android OS остаются внешними boundaries; локальные
  Settings, timer state, cache/history и freshness evaluation принадлежат
  приложению.
- Timer recovery is lifecycle-sensitive; weather freshness and forecast
  availability are data-sensitive; visual composition remains subordinate to
  readability of the clock.
- Storage schemas, module structure, detailed API contracts and platform
  mechanisms remain open for `/spec-design`.

## Review Status

- Status: reviewed
- Notes: Scenarios are derived from the clarified PRD with no unresolved
  actor- or scenario-sensitive product branch. The accepted 10-day forecast
  horizon in the PRD supersedes the original 14-day wording in IDEA/product
  brief inputs; this does not create a new blocker.
