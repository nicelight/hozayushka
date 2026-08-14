---
description: Reviewed core user scenarios and decomposition implications for the clarified V1.
status: active
review_status: reviewed
last_updated: 2026-08-10
---
# User Scenarios

## Primary Actors

- **Владелец** — единственный пользователь, который вручную запускает
  приложение на стационарном кухонном смартфоне и взаимодействует коротким
  тапом, двойным тапом или удержанием.
- **Open-Meteo** — default внешний weather provider без пользовательского API
  key для принятого персонального non-commercial use.
- **OpenWeather** — optional внешний weather provider, явно выбираемый
  владельцем и использующий его локальный API key.
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

Владелец видит четыре карточки в фиксированном порядке. Из «Сегодня» hourly
view открывается только при наличии всех восьми фиксированных city-local
слотов. Из «Завтра»/«Послезавтра» открывается общий десятипозиционный long-term
view: Open-Meteo заполняет 10 позиций, OpenWeather — 8 и оставляет последние 2
unavailable/empty. При отсутствии полного provider-supported набора приложение
остаётся на main display и показывает принятое короткое сообщение.

Источник: [.memory-bank/prd.md](prd.md), `PRD-FR-007`–`PRD-FR-022` и
`PRD-AC-002`, `PRD-AC-007`–`PRD-AC-007A`.

### 3. Настройка provider, location и персональных предпочтений

Владелец открывает Settings, оставляет default Open-Meteo без API key либо явно
выбирает OpenWeather и вводит свой локальный key, затем offline выбирает страну
и город и настраивает durations, sound, volume и glass intensity. Корректные
значения сохраняются автоматически; ошибки остаются рядом с владеющим полем и
не затирают последнее корректное значение. Ошибка выбранного provider не
меняет selection и не запускает cross-provider fallback.

Источник: [.memory-bank/prd.md](prd.md), `PRD-FR-032`–`PRD-FR-039` и
`PRD-AC-006`–`PRD-AC-006C`.

## Out Of Scope Scenarios

- Backend, cloud sync, accounts, multi-user access или общий встроенный API key.
- Google Services, автозапуск и восстановление timer после reboot.
- История погоды до установки, Telegram-бот и Android TTS.
- Дополнительные Settings/элементы V1 без отдельного operator decision.
- Forecast view без требуемых weather/hourly data.
- Автоматический cross-provider fallback, смешивание provider data или синтез
  недоступных hourly/daily records.

Источник: [.memory-bank/prd.md](prd.md), `Non-goals` и `Edge Cases / Failure Handling`.

## Architecture/Domain Implications

- Main display, timer/alert lifecycle, weather/cache/history, forecast sessions
  и Settings/location должны быть различимыми decomposition concerns.
- Weather provider и Android OS остаются внешними boundaries; локальные
  Settings, provider selection, timer state, provider-identified cache/history
  и freshness evaluation принадлежат приложению.
- Разные provider capabilities влияют на forecast availability и проекцию, но
  не разрешают скрытую смену provider или смешивание ответов.
- Timer recovery is lifecycle-sensitive; weather freshness and forecast
  availability are data-sensitive; visual composition remains subordinate to
  readability of the clock.
- Storage schemas, module structure, detailed API contracts and platform
  mechanisms remain open for `/spec-design`.

## Review Status

- Status: reviewed
- Notes: Scenarios are derived from the clarified provider-migration PRD with
  no unresolved actor- or scenario-sensitive product branch. Open-Meteo as
  default/no-key, explicit OpenWeather with local key, no cross-provider
  fallback, 10-versus-8 daily behavior and strict eight-slot hourly
  availability supersede Yandex-only and 14-day wording in earlier inputs.
