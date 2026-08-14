---
description: Revision-2 implementation plan for strict selected-provider hourly completeness.
status: active
last_updated: 2026-08-12
---
# IMPL-FT-003 — Strict eight-slot hourly availability

## Outcome

On top of the two-adapter Weather Context, open the existing hourly session
only when the selected provider supplies all eight fixed city-local slots.
Otherwise preserve Main Display and show the accepted unavailable message.

## Ordered work

1. Preserve `TASK-005` failed and `TASK-012`/`TASK-013` done records,
   evidence and historical semantics.
2. Preserve TASK-020's implemented provider-neutral migration facts separately
   from its failed selected-OpenWeather activation outcome.
3. Reconcile completed `TASK-021-T2-FT-003-W18` behind the completed
   `TASK-023-T3-FT-002-W20` activation repair. W18 owns fresh AC-001/AC-005
   completeness and no-synthesis evidence; TASK-020 remains failed historical
   evidence and is not reopened, while downstream W19 remains scheduler-owned.

## Ownership and execution path

Weather Context owns normalized selected-provider forecast data. Forecast
Sessions owns entry and session lifecycle. The bounded path is selected-city
date/timezone + selected-provider hourly records → exact fixed-slot projection
→ complete eight-card session or unavailable result.

The task does not call adapters, choose providers, read another provider
partition, change the 2x4 layout, or modify the accepted auto-close/gesture
flow.

## Acceptance map

| Current claim | Owner | Proof |
|---|---|---|
| `AC-001 / REQ-009` | W18 | Both providers open only with all eight selected-provider slots |
| `AC-005 / REQ-009, REQ-026` | W18 | Any missing slot for either provider yields exact unavailable result and no synthesis |
| `AC-002 / REQ-009` | W4 done | Existing exact order and 2x4 layout |
| `AC-003 / REQ-009, REQ-022` | W4 done | Existing city-time presentation |
| `AC-004 / REQ-009` | W5 done | Existing shared exit flow |

## Advisory surface and proof

- `weather/WeatherCapability.kt` — selected-provider fixed-slot projection
- `forecast/ForecastSessionCapability.kt` — complete/incomplete entry result
- `WeatherContextTest.kt`, `ForecastSessionTest.kt` and redacted fixtures

RED is the absence of a complete two-provider/elapsed-slot matrix. GREEN is
deterministic complete-provider proof plus sixteen one-missing-slot cases,
fixed to selected-city date/timezone, with exact message and no session.
Existing layout/presentation/exit behavior is regression-only.

W18 closure evidence is recorded in the [executor handoff](../../../.protocols/TASK-021-T2-FT-003-W18/handoff.md),
[fresh functional verification](../../../.protocols/TASK-021-T2-FT-003-W18/verification.md),
[verifier-owned evidence](../../../.tasks/TASK-021-T2-FT-003-W18/verifier-owned-evidence.md),
[deterministic matrix](../../../.tasks/TASK-021-T2-FT-003-W18/hourly-completeness-matrix.json)
and [final semantic verification](../../../.protocols/TASK-021-T2-FT-003-W18/red-verification.md).

No hard `write_boundary` is selected. Project-native clean build and host
tests remain execution gates; this planning run performs no Gradle,
emulator/device or runtime evidence.

## Constraints

Exactly eight fixed slots or unavailable. No partial view, nearest-hour
matching, interpolation, synthesis, cross-provider borrowing, plugin
abstraction, new dependency or screen redesign.

## Direct normative inputs

- [.memory-bank/features/FT-003-hourly-forecast.md](../../features/FT-003-hourly-forecast.md)
- [.memory-bank/contracts/capability-interfaces.md](../../contracts/capability-interfaces.md)
- [.memory-bank/contracts/weather-provider.md](../../contracts/weather-provider.md)
- [.memory-bank/domains/local-data.md](../../domains/local-data.md)
- [.memory-bank/testing/runtime-verification.md](../../testing/runtime-verification.md)
- [.memory-bank/tasks/TASK-021-T2-FT-003-W18.task.json](../TASK-021-T2-FT-003-W18.task.json)
- [.memory-bank/tasks/TASK-023-T3-FT-002-W20.task.json](../TASK-023-T3-FT-002-W20.task.json)

## Handoff

TASK-021 is `done` behind the completed indexed W20 repair, with current
claim-linked evidence for AC-001/AC-005. Downstream `TASK-022-T2-FT-004-W19` is
now `done` with fresh long-term completeness verification and feature-level
FT-004 `semantic-pass`; its earlier block remains historical task-card
evidence. Scheduler post-sync gates, promotion and feature/epic lifecycle
handling remain outside this plan.
