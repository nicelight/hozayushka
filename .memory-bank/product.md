---
description: Product brief (C4 L1): что это, для кого, core value, ограничения.
status: draft
last_updated: 2026-08-03
---
# Product: кухонные часы с погодой и таймерами

## What this is

Персональное Kotlin Android-приложение для постоянно включённого
горизонтального смартфона на кухне. Оно объединяет крупные часы, дату, краткий
погодный контекст и три быстрых preset-таймера в одном спокойном fullscreen
display. Время остаётся главным визуальным элементом.

## Core value

Владелец одним взглядом получает время и погоду, а одним касанием запускает
частый таймер без навигационного шума и риска случайной отмены активного
отсчёта.

## Audience

- Единственный владелец приложения.
- Контекст: стационарный кухонный смартфон, landscape 1280×720, частые короткие
  взгляды и управление коротким тапом, double tap и hold.
- Внешние actors: Yandex Weather API как источник погодных данных и Android OS
  как источник platform lifecycle, времени, сети и разрешённого звука.

## Primary user flow

1. Владелец вручную открывает приложение и видит fullscreen main display с
   `HH:mm`, датой, городом и четырьмя погодными карточками.
2. При необходимости одним тапом запускает один из трёх preset-таймеров;
   countdown занимает область часов и сохраняет корректный lifecycle вне
   foreground.
3. После нуля приложение показывает overdue state и разрешённый системными
   правилами повторяющийся звук; касание возвращает main display.
4. Долгое удержание города открывает Settings. Владелец offline выбирает
   страну и город, вводит личный API key, настраивает timer/alert preferences и
   glass intensity; корректные значения auto-save.
5. Тапы по погодным карточкам открывают hourly или общий 10-дневный forecast
   screen только при наличии соответствующих данных.

## Constraints
- Tech stack: Kotlin Android для Samsung GT-I9300I (`s3ve3gds`) с совместимой
  Android 11 custom ROM; основной язык UI — русский.
- Product/runtime: только landscape fullscreen, системные панели скрыты, экран
  удерживается включённым, часы доминируют на 1280×720, основной UI неподвижен.
- Data/integration: Yandex Weather API с личным локально хранимым key; свежий
  cache доступен offline до 24 часов; location selection использует bundled
  GeoNames `cities15000` без Google Services.
- Visual: 78 явных температурных HEX values от −30 до +47 °C с clamp и
  статичный pseudo-glass без тяжёлых realtime effects.
- Delivery: V1 не публикуется и не распространяется как APK; detailed
  architecture, storage, API contracts and testing gates остаются за
  `/spec-design` и последующими design/task workflows.
- Non-goals: backend/cloud sync/accounts/multi-user, общий API key, Google
  Services, reboot recovery/autostart, pre-install weather history, Telegram/
  TTS V2 и не согласованные функции/settings.

## Sources

- [.memory-bank/prd.md](prd.md): clarified product behavior, constraints and
  acceptance criteria.
- [.memory-bank/user-scenarios.md](user-scenarios.md): reviewed actors and core
  scenarios.
- [.memory-bank/constitution.md](constitution.md): KISS, product
  non-negotiables and evidence ownership.
