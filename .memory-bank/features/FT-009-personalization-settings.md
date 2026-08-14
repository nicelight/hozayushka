---
description: L3 feature for alert/display preferences and live glass-intensity preview.
status: active
id: FT-009
epic: EP-004
lifecycle: planned
last_updated: 2026-08-08
clarification_status: complete
last_clarified: 2026-08-06
clarification_questions: 1
spec_design_status: complete
spec_design_links:
  - .memory-bank/contracts/boundary-map.md
  - .memory-bank/contracts/capability-interfaces.md
  - .memory-bank/contracts/weather-card-presentation.md
  - .memory-bank/domains/local-data.md
  - .memory-bank/contracts/platform-runtime.md
  - .memory-bank/testing/runtime-verification.md
source_of_truth: .memory-bank/prd.md, .memory-bank/requirements.md, operator confirmation 2026-08-06
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

### FT-009-AC-001 — Validated alert sound and volume projection

- REQ: REQ-019, REQ-020, REQ-021
- Settings expose the accepted built-in sounds, default `Классический`, app
  alert volume and glass intensity. App alert volume is a normalized integer
  from `0` to `100` percent, defaults to `70`, and is validated and persisted
  as an application setting. Value `0` disables only app-alert sound; the
  visual overdue state remains available.
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
- App alert volume `0` suppresses only sound; it never suppresses or clears the
  visual overdue state owned by FT-007.
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

## W10 execution and evidence

The authoritative task record
[`TASK-011-T3-FT-009-W10`](../tasks/TASK-011-T3-FT-009-W10.task.json) is
`done`. Fresh functional verification is `PASS` and independent semantic
verification is `semantic-pass` for FT-009 / REQ-019, REQ-020 and REQ-021:

- [functional verification](../../.protocols/TASK-011-T3-FT-009-W10/verification.md)
- [semantic verification](../../.protocols/TASK-011-T3-FT-009-W10/red-verification.md)
- [verifier-owned probe](../../.tasks/TASK-011-T3-FT-009-W10/verifier-owned-probe.md)

Target-only Settings readability and static pseudo-glass evidence remains
`DEFERRED`/non-blocking; no runtime `PASS` is claimed. Feature lifecycle and
direct RTM lifecycle remain `planned`; this evidence does not infer feature or
epic closure, promotion or dependent-state transition.

## SDD Design Gate

Global backbone is complete at Planning Revision `2` and the Foundation Gate
anchors remain closed. The product clarification for app alert volume and the
canonical Today-temperature preview route remain complete. FT-009's indexed
task-plan review is stale only by revision mismatch and is reconciled through
`/feature-to-tasks --all` after Foundation revalidation.

Applicable global specs: [Boundary Map](../contracts/boundary-map.md),
[Capability Interfaces](../contracts/capability-interfaces.md),
[Weather Card Presentation](../contracts/weather-card-presentation.md),
[Local Data](../domains/local-data.md), [Platform Runtime](../contracts/platform-runtime.md)
and [Runtime Verification](../testing/runtime-verification.md).
Settings persistence and feature verification remain downstream; no extra
Settings scope is introduced.

## Clarifications

### 2026-08-06 — App alert volume contract

The operator accepted the following product contract for `REQ-019`:

- app alert volume is a normalized integer in the inclusive range `0…100`%;
- default value is `70`%;
- `0` disables only app-alert sound while visual overdue state remains;
- the value is validated and persisted as an application setting.

This clarification preserves the existing `FT-009-AC-001` identifier and does
not add Settings controls, change tier, or change Planning Revision `1`.
Design impact: `none`. Behavior spec impact: `refresh_recommended`.

### 2026-08-06 — Today temperature and personalization projection route

Settings & Location owns persisted and validated alert/glass personalization.
Main Display consumes that validated presentation projection through the
existing `Main Display → Settings & Location` contract. Main Display already
owns the composition of the production Today card and reads normalized Today
temperature from Weather Context through its existing contract; it supplies
that value (or `24 °C`) to the Settings preview. The preview and production
Today card use the same saved projection and the shared Weather Card
Presentation rules. No `Settings & Location → Weather Context` edge is added,
and Settings does not read Weather Context storage.

Design impact: `complete`; Planning Revision remains `1`.

## Design and execution handoff

The FT-009 SDD design gate remains complete at Planning Revision `2`; the
existing dependency graph remains unchanged and has no new
Settings-to-Weather edge. Its prior task decomposition/review is stale only by
the global revision mismatch and routes through `/feature-to-tasks --all` after
Foundation revalidation. W10 execution and paired verification remain
historical evidence; feature lifecycle stays `planned`, with any scheduler
promotion and dependent-state transition owned by the scheduler/lifecycle
owner.
