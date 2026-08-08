---
description: L3 feature for the always-visible clock, date/city area and main display shell.
status: draft
id: FT-001
epic: EP-001
lifecycle: implemented
spec_design_status: complete
spec_design_links:
  - .memory-bank/architecture/system-architecture.md
  - .memory-bank/contracts/boundary-map.md
  - .memory-bank/contracts/capability-interfaces.md
  - .memory-bank/contracts/platform-runtime.md
  - .memory-bank/testing/runtime-verification.md
last_updated: 2026-08-08
---
# FT-001 — Main clock and display shell

## Product outcome

Владелец открывает приложение и сразу получает читаемый fullscreen clock display
с датой, городом, четырьмя weather cards в нижней левой зоне и preset buttons.

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

### FT-001-AC-001 — Runtime display policy

- REQ: REQ-001, REQ-023
App runs only in landscape fullscreen, hides system panels and keeps the
  display on while open.

### FT-001-AC-002 — Main display composition

- REQ: REQ-002, REQ-023
`HH:mm` has no seconds and remains the dominant visual element; city/date are
  on the left, exactly four weather cards occupy the stable lower-left area and
  three preset buttons are on the right. FT-001 owns only this card count,
  composition and placement; FT-002 owns card data, content, freshness and
  weather-specific presentation.

### FT-001-AC-003 — Device-time clock and date

- REQ: REQ-002, REQ-022
Date uses `dd` and Russian genitive month without year or weekday. Clock/date
  use device timezone.

### FT-001-AC-004 — Clock colon states

- REQ: REQ-003
Online colon performs the accepted 3-second rise and following fade to 2%;
  offline colon is fixed at 38%; active countdown uses the accepted discrete
  382/618 ms blink.

### FT-001-AC-005 — City interaction

- REQ: REQ-004
Empty city renders `Выбрать город`; its short tap opens Settings, a selected
  city's short tap is a no-op, and a long hold opens Settings.

## Edge / failure behavior

- Missing weather data does not remove the stable main-display shell or shift
  the clock/date zones; the four lower-left weather-card positions remain
  stable.
- Network absence changes only the accepted colon state and weather availability;
  it does not block clock display.
- Timer-specific replacement of the clock is composed by FT-006; this feature
  retains the display shell and its accepted colon state.

## Boundary ownership

- Main Display owns the shell composition, the four-card lower-left placement
  and city gesture intent.
- The existing Main Display → Settings & Location contract owns the destination
  request and return path. Settings & Location owns the destination surface and
  settings state; FT-001 does not add catalog, API-key, validation or
  personalization behavior.
- Weather Context/FT-002 owns weather data and card content. FT-001 verifies
  only the four-card composition boundary and does not take FT-002 data
  ownership.

## Sources and normative inputs

- [.memory-bank/prd.md](../prd.md): `PRD-FR-001`–`PRD-FR-006`, `PRD-FR-039`,
  `PRD-NFR-001`–`PRD-NFR-003`, `PRD-AC-001`, `PRD-AC-009`.
- [.memory-bank/invariants.md](../invariants.md): clock dominance and local-only
  product constraints.
- [.memory-bank/user-scenarios.md](../user-scenarios.md): core glance scenario.

## Verification targets

- `PRD-AC-001`, `PRD-AC-009`, `PRD-FR-003`–`PRD-FR-006`.

## W2 implementation evidence

The indexed implementation task `TASK-003-T3-FT-001-W2` is `done` with
functional `PASS` and semantic `semantic-pass`. Host/static evidence is
recorded; target-only fullscreen, readability, keep-screen-on and interaction
observations remain `DEFERRED` with residual risk under the accepted policy.
See the [task card](../tasks/TASK-003-T3-FT-001-W2.task.json), [functional
verification](../../.tasks/TASK-003-T3-FT-001-W2/TASK-003-T3-FT-001-W2-S-VERIFY-final-report-docs-02.md)
and [semantic verification](../../.tasks/TASK-003-T3-FT-001-W2/TASK-003-T3-FT-001-W2-S-RED-VERIFY-final-report-docs-01.md).

## Post-terminal generic-emulator follow-up

Supplementary Reviewer evidence on `Tecno_Pova_6_API_35` confirms that the
populated city and transient status/hint rows collapse to zero height, making
the accepted city gesture and Settings entry unreachable. TASK-003 remains the
unchanged `done` historical record; the separate planned follow-up
[`TASK-014-T3-FT-001-W11`](../tasks/TASK-014-T3-FT-001-W11.task.json) owns only
the FT-001-AC-002/AC-005 generic-emulator layout/reachability delta. Samsung
GT-I9300I Android 11 custom-ROM and 1280×720 evidence remain `DEFERRED`.

## W11 boundary failure

The indexed follow-up [`TASK-014-T3-FT-001-W11`](../tasks/TASK-014-T3-FT-001-W11.task.json)
is `failed` after its final retry: functional verification passed, but the
required independent semantic verification returned `semantic-fail`. A public
non-city weather-card double tap left an active countdown running, violating
the existing FT-006-AC-003 / REQ-013 cancellation contract; selected-city
double-tap cancellation and delayed Settings protection passed. The task-local
defect is recorded in the
[`TASK-014-noncity-countdown-cancellation`](../bugs/TASK-014-noncity-countdown-cancellation.md)
bug note. See the [semantic report](../../.tasks/TASK-014-T3-FT-001-W11/TASK-014-T3-FT-001-W11-S-RED-VERIFY-final-report-docs-01.md)
and [red-verification protocol](../../.protocols/TASK-014-T3-FT-001-W11/red-verification.md).

This failure does not reopen or replace the historical `TASK-003-T3-FT-001-W2`
record and does not change FT-001's existing `implemented` lifecycle. Repair
must return through normal indexed `/feature-to-tasks FT-001` planning before
fresh execution and verification.

## W12 public active-countdown dispatch repair

The normal indexed repair plan creates [`TASK-015-T3-FT-001-W12`](../tasks/TASK-015-T3-FT-001-W12.task.json)
with `planned` status. It owns only the bounded Main Display
`FT-001-AC-005` city hold/Settings-preservation acceptance delta. The existing
downstream protected-cancellation contract (REQ-013; regression guard only;
canonical basis in TASK-015 `normative_inputs`) is exercised by the public
non-city weather-card single/double checks to prevent cross-feature regression;
it is not a W12 acceptance locator, a new FT-006 task, or an FT-006 lifecycle
change. The repair remains inside Main Display and the existing Timer & Alert
and Settings & Location contracts: it captures one active public touch stream
through terminal delivery without live-state rechecking, while preserving city
long-hold -> Settings, selected/empty city taps, preset interactions and
overdue behavior. This does not reopen or rewrite TASK-014's failed historical
layout/reachability record; TASK-014 remains `failed` and unchanged, and
TASK-003 remains `done` and unchanged. Host stream evidence cannot replace the
mandatory generic-emulator public scenarios.
Samsung/custom-ROM/1280x720 remains `DEFERRED`.

## SDD Design Gate

Global backbone is complete at Planning Revision `1`, the Foundation Gate is
closed, and the registered Boundary Map contains the accepted module inventory
and dependency graph. Feature-level design is complete for task planning.

Applicable global specs: [System Architecture](../architecture/system-architecture.md),
[Boundary Map](../contracts/boundary-map.md), [Capability Interfaces](../contracts/capability-interfaces.md),
[Platform Runtime](../contracts/platform-runtime.md) and [Runtime Verification](../testing/runtime-verification.md).
The feature does not choose the UI toolkit or test level. Exact implementation
choices remain execution discretion unless they change an accepted boundary or
require a new dependency.
