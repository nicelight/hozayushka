---
description: Словарь терминов, сущностей и agreed vocabulary проекта.
status: active
last_updated: 2026-08-10
source_of_truth: .memory-bank/prd.md, .memory-bank/analysis/product-brief.md
---
# Glossary

## Terms
- **Основной экран**: постоянно отображаемый fullscreen landscape экран с часами, датой, городом, погодными карточками и preset-кнопками таймеров.
- **Preset-таймер**: одна из трёх отдельно настраиваемых длительностей, запускаемая собственной кнопкой одним нажатием.
- **Активный таймер**: единственный выполняющийся обратный отсчёт; его время занимает основную область часов.
- **Просроченный таймер**: состояние после достижения нуля, в котором счёт продолжается вверх от полного времени с момента запуска, а приложение показывает крупное предупреждение и подаёт сигнал.
- **Погодная карточка**: визуальный блок одного календарного дня с иллюстрацией, glass-температурой, температурно-зависимым фоном и, на основном экране, возможной glass-стрелкой давления.
- **Выбранный weather provider**: явно сохранённый источник погоды, к которому относятся текущий запрос, ошибки, normalized cache/history и forecast availability; default — Open-Meteo, optional — OpenWeather.
- **Open-Meteo**: default weather provider для принятого персонального non-commercial use; не требует пользовательского API key и заполняет все 10 позиций long-term forecast.
- **OpenWeather**: optional weather provider, выбираемый владельцем явно; требует его локальный API key и предоставляет восемь поддерживаемых daily records для десятипозиционного long-term layout.
- **OpenWeather API key**: личный ключ владельца для выбранного OpenWeather; остаётся локальным и не попадает в APK, source, logs или verification evidence. Для Open-Meteo этот key не используется.
- **Актуальные погодные данные**: последнее успешное normalized update выбранного provider возрастом не более 24 часов; cache другого provider не считается fallback.
- **Тренд давления**: локально вычисленное направление и степень изменения атмосферного давления по сохранённой истории.
- **10-позиционный long-term forecast**: отдельный экран в два ряда по пять, начиная с текущего календарного дня выбранного города; Open-Meteo заполняет 10 daily positions, а OpenWeather — первые 8 и оставляет последние 2 unavailable/empty без синтеза или данных другого provider.
- **Почасовой прогноз**: отдельный экран из восьми фиксированных city-local трёхчасовых слотов текущего и следующего дня; он открывается только при наличии всех восьми слотов, иначе действует принятый unavailable flow.
- **Cross-provider fallback**: автоматический запрос, скрытая смена provider или подстановка cache/forecast другого provider после ошибки выбранного; в V1 запрещён.
- **Температурная палитра**: явная таблица Windy-derived HEX-цветов для каждой целой температуры от −30 до +47 °C; значения за пределами диапазона используют ближайший крайний цвет.
- **Pseudo-glass**: принятый статичный визуальный материал температуры и стрелок давления из прозрачного fill и светлой/тёмной кромок, без realtime blur или физической симуляции стекла.
- **Glass intensity**: пользовательская настройка силы pseudo-glass в диапазоне 0…1 с начальным значением 0.45 и live preview погодной карточки.

## Sources
- [.memory-bank/prd.md](prd.md): clarified product requirements and accepted V1 vocabulary; its provider-migration and 10-position forecast decisions supersede Yandex-only and 14-day wording in earlier sources.
- [.memory-bank/analysis/product-brief.md](analysis/product-brief.md): original accepted product framing; provider and forecast-horizon wording is superseded by the clarified PRD.
- [.memory-bank/analysis/brainstorming/BR-001.md](analysis/brainstorming/BR-001.md): принятые определения температурной палитры и pseudo-glass.
- [`IDEA.md`](../IDEA.md): detailed source definitions and interaction rules.
