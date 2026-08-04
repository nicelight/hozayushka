---
description: L3 feature for the always-visible clock, date/city area and main display shell.
status: draft
id: FT-001
epic: EP-001
lifecycle: planned
last_updated: 2026-08-03
---
# FT-001 — Main clock and display shell

## Product outcome

Владелец открывает приложение и сразу получает читаемый fullscreen clock display
с датой, городом и стабильными зонами для weather cards и preset buttons.

## Requirements

- REQ-001, REQ-002, REQ-003, REQ-004, REQ-022, REQ-023.

## Use cases

1. Владелец смотрит на `HH:mm` и дату из кухонной дистанции.
2. Владелец видит выбранный город либо понятный `Выбрать город`.
3. Владелец удерживает город, чтобы открыть Settings; selected-city short tap
   не меняет экран.
4. Владелец видит accepted clock-colon behavior online, offline и при активном
   timer.

## Acceptance criteria

- App runs only in landscape fullscreen, hides system panels and keeps the
  display on while open.
- `HH:mm` has no seconds and remains the dominant visual element; city/date are
  on the left, weather-card area is lower-left and preset buttons are on the
  right.
- Date uses `dd` and Russian genitive month without year or weekday. Clock/date
  use device timezone.
- Online colon performs the accepted 3-second rise and following fade to 2%;
  offline colon is fixed at 38%; active countdown uses the accepted discrete
  382/618 ms blink.
- Empty city renders `Выбрать город`; its short tap opens Settings, a selected
  city's short tap is a no-op, and a long hold opens Settings.

## Edge / failure behavior

- Missing weather data does not remove the stable main-display shell or shift
  the clock/date zones.
- Network absence changes only the accepted colon state and weather availability;
  it does not block clock display.
- Timer-specific replacement of the clock is composed by FT-006; this feature
  retains the display shell and its accepted colon state.

## Sources and normative inputs

- [.memory-bank/prd.md](../prd.md): `PRD-FR-001`–`PRD-FR-006`, `PRD-FR-039`,
  `PRD-NFR-001`–`PRD-NFR-003`, `PRD-AC-001`, `PRD-AC-009`.
- [.memory-bank/invariants.md](../invariants.md): clock dominance and local-only
  product constraints.
- [.memory-bank/user-scenarios.md](../user-scenarios.md): core glance scenario.

## Verification targets

- `PRD-AC-001`, `PRD-AC-009`, `PRD-FR-003`–`PRD-FR-006`.

## SDD Design Gate

Global backbone is complete at Planning Revision `1`; feature-level design
remains draft until the Foundation Gate and `/feature-to-tasks`.

Applicable global specs: [System Architecture](../architecture/system-architecture.md),
[Boundary Map](../contracts/boundary-map.md), [Capability Interfaces](../contracts/capability-interfaces.md),
[Platform Runtime](../contracts/platform-runtime.md) and [Runtime Verification](../testing/runtime-verification.md).
The feature does not set `spec_design_status: complete` and does not choose
the UI toolkit or test level.
