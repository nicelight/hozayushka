---
description: L3 feature for alert/display preferences and live glass-intensity preview.
status: draft
id: FT-009
epic: EP-004
lifecycle: planned
last_updated: 2026-08-03
---
# FT-009 — Alert and glass personalization

## Product outcome

Владелец настраивает звук/громкость и силу pseudo-glass с live preview, а
корректные preferences сохраняются без отдельной save action и без modal
ошибок.

## Requirements

- REQ-019, REQ-020, REQ-021.

## Use cases

1. Владелец выбирает встроенный alert sound и app alert volume.
2. Владелец двигает glass-intensity slider и сразу видит production weather-card
   preview.
3. Владелец повторно открывает Settings и видит сохранённые корректные values.
4. Владелец исправляет invalid value по owning inline error, не теряя прежнюю
   корректную настройку.

## Acceptance criteria

- Settings expose the accepted built-in sounds, default `Классический`, app
  alert volume and glass intensity.
- Glass intensity ranges 0…1, starts at 0.45, updates preview during the
  gesture and persists after the gesture.
- Preview uses the production weather-card presentation, Today temperature or
  fallback 24 °C, two overlapping arrows and the temperature number.
- Valid changes auto-save and are available after reopening; invalid values do
  not replace the previous valid value.
- Accepted errors (`API-ключ не указан`, `Неверный API-ключ`, `Нет подключения`,
  `Город не найден`, `Укажите время больше нуля`) appear inline and no modal
  dialog is used. Bottom back-icon and system Back return to main display.
- Feature stays within accepted Settings scope and does not add extra controls.

## Edge / failure behavior

- Preview remains usable with fallback temperature when weather is unavailable;
  it does not make a network request solely to render the preview.
- Audio preference changes do not override Android silent/DND behavior; FT-007
  owns completion-time presentation.
- Invalid field values remain unsaved and retain their owning error until fixed
  or the screen is left according to the accepted Settings flow.

## Sources and normative inputs

- [.memory-bank/prd.md](../prd.md): `PRD-FR-031`–`PRD-FR-032`, `PRD-FR-035`–
  `PRD-FR-038`, `PRD-AC-003`, `PRD-AC-006`–`PRD-AC-006C`.
- [.memory-bank/glossary.md](../glossary.md): glass intensity and pseudo-glass
  definitions.
- [.memory-bank/invariants.md](../invariants.md): validation, no modal errors and
  no unaccepted Settings scope.
- [.memory-bank/contracts/boundary-map.md](../contracts/boundary-map.md): local
  Settings ownership and Android audio boundary.

## Verification targets

- `PRD-AC-003`, `PRD-AC-006`, `PRD-AC-006A`–`PRD-AC-006C`, `PRD-FR-035`–
  `PRD-FR-038`.

## SDD Design Gate

Global backbone is complete at Planning Revision `1`; feature-level design
remains draft until the Foundation Gate and `/feature-to-tasks`.

Applicable global specs: [Boundary Map](../contracts/boundary-map.md),
[Capability Interfaces](../contracts/capability-interfaces.md), [Local Data](../domains/local-data.md),
[Local Secret Handling](../contracts/local-secret-handling.md) and [Runtime Verification](../testing/runtime-verification.md).
Settings persistence, preview composition and feature verification remain
downstream; no extra Settings scope is introduced.
