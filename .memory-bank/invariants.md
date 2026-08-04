---
description: Глобальные инварианты и запреты проекта (MUST/NEVER).
status: active
last_updated: 2026-08-03
source_of_truth: .memory-bank/constitution.md, .memory-bank/prd.md
---
# Invariants

## MUST
- MUST сохранять время главным визуальным элементом landscape fullscreen UI на целевом экране 1280×720.
- MUST допускать только один активный timer; одиночное касание во время countdown не отменяет его, а двойное касание отменяет.
- MUST восстанавливать `countdown|overdue` по сохранённым моменту запуска и длительности после временной остановки процесса; автоматическое восстановление после reboot не входит в V1.
- MUST показывать visual overdue state после завершения timer; звуковой сигнал подчиняется разрешениям Android и ограничен 30 минутами.
- MUST считать weather cache актуальным и доступным offline не более 24 часов после последнего успешного обновления; более старые карточки переходят в согласованное empty state.
- MUST хранить личный weather API key локально и не допускать его попадания в APK, source, logs или verification evidence.
- MUST хранить все 78 принятых temperature HEX values явно в одном compile-time source и clamp значения за пределами диапазона к крайним цветам.
- MUST поддерживать offline-поиск страны и затем городов только выбранной страны без Google Services.
- MUST оставлять корректные сохранённые Settings values при ошибке validation и показывать принятую ошибку inline без modal dialog.

## NEVER
- NEVER добавлять backend, cloud sync, accounts, multi-user режим или общий встроенный API key в V1.
- NEVER зависеть от Google Services для принятого сценария выбора location.
- NEVER использовать realtime blur, refraction/lensing, background capture или постоянную динамическую glass-анимацию.
- NEVER считать forecast screen доступным при отсутствии соответствующих данных; в этом случае остаётся основной экран и показывается принятое inline/toast-сообщение.
- NEVER включать исторические weather data, собранные до установки приложения, или автоматическое восстановление timer после reboot.
- NEVER расширять V1 дополнительными Settings, функциями или элементами левой области без отдельного принятого operator decision.

## Notes
- Правила выведены из [.memory-bank/prd.md](prd.md), [.memory-bank/constitution.md](constitution.md) и принятого product framing; детальные реализации и проверки принадлежат downstream specs/tasks.
- Ссылайся на этот файл из архитектурных, контрактных и execution docs, если правило является cross-cutting.
