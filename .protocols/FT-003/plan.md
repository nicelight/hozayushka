---
description: Revision-2 planning surface for FT-003 strict selected-provider hourly forecast.
status: active
last_updated: 2026-08-12
---
# FT-003 — Feature plan

## Objective

Preserve the accepted hourly screen and exit flow while reconciling entry to
the two-provider target: exactly eight fixed selected-city-local slots from the
selected provider, or no session. OpenWeather elapsed-slot absence is
unavailable, never permission to synthesize or borrow Open-Meteo data.

## Accepted basis

- Feature: [.memory-bank/features/FT-003-hourly-forecast.md](../../.memory-bank/features/FT-003-hourly-forecast.md)
- Direct requirements: `REQ-009`, `REQ-022`, `REQ-026`
- Global Backbone: `complete`, Planning Revision `2`
- Foundation Gate: `TASK-002-T3-FT-000-W1`, status `done`; Foundation
  revalidation is successful
- Provider activation prerequisite: new repair
  `TASK-023-T3-FT-002-W20`; W17 remains preserved failed migration evidence.

## Reconciled queue

| Order | Task | Tier | Wave | Status | Depends on | Role |
|---|---|---|---|---|---|---|
| 1 | `TASK-005-T3-FT-003-W4` | T3 | W4 | failed | `TASK-004-T3-FT-002-W3` | Preserved failed historical attempt |
| 2 | `TASK-012-T3-FT-003-W4` | T3 | W4 | done | `TASK-004-T3-FT-002-W3` | Historical hourly data/presentation repair |
| 3 | `TASK-013-T3-FT-003-W5` | T3 | W5 | done | `TASK-012-T3-FT-003-W4` | Historical entry/exit completion |
| 4 | `TASK-021-T2-FT-003-W18` | T2 | W18 | done | `TASK-023-T3-FT-002-W20` | Completed current provider-completeness delta |

Queue action: `reconciled` for the completed W18 boundary. W18 keeps its ID,
T2 tier, W18 wave and acceptance scope; its runnable prerequisite remains the
completed W20 repair. The prior W17 dependency block is retained in the task
record as historical scheduler evidence, while W18's own claim-linked
execution and verification evidence is now current. W17 remains failed after
3/3 attempts; W20's migration repair facts are not inherited as W18 acceptance
evidence. W19 remains downstream and scheduler recovery is external.

## Current acceptance ownership

| Acceptance | Current owner | Boundary |
|---|---|---|
| `FT-003-AC-001`, `FT-003-AC-005` | `TASK-021-T2-FT-003-W18` | Selected-provider all-eight entry and no synthesis/borrowing |
| `FT-003-AC-002`, `FT-003-AC-003` | `TASK-012-T3-FT-003-W4` | Unchanged slot order/layout and shared card presentation |
| `FT-003-AC-004` | `TASK-013-T3-FT-003-W5` | Unchanged session exit flow |

The failed W4 attempt and overlapping historical locators remain historical
semantics only; this table is the single current ownership map. W18 produced its
own claim-linked proof and did not inherit W17 adapter evidence. Fresh W18
evidence covers AC-001/AC-005 only; the W4/W5 ownership rows remain unchanged.

## Canonical SDD coverage

No spec is created or extended. Reuse:

- [FT-003 Hourly Forecast Session Surface](../../.memory-bank/contracts/capability-interfaces.md#ft-003-hourly-forecast-session-surface)
  and [Forecast Data Contract](../../.memory-bank/contracts/capability-interfaces.md#ft-003-forecast-data-contract)
- [Provider Capability Matrix](../../.memory-bank/contracts/weather-provider.md#provider-capability-matrix),
  [Mapping and Timezone Obligations](../../.memory-bank/contracts/weather-provider.md#mapping-and-timezone-obligations)
  and [Failure Rules](../../.memory-bank/contracts/weather-provider.md#failure-rules)
- [FT-003 Hourly Forecast Records](../../.memory-bank/domains/local-data.md#ft-003-hourly-forecast-records)
  and [Deterministic Host-Side Checks](../../.memory-bank/testing/runtime-verification.md#deterministic-host-side-checks)

## Scope and proof

W18 is a T2 domain/session task, not an adapter or UI redesign task. GREEN
requires a complete Open-Meteo case, a complete OpenWeather case, and one
missing-slot result for every one of eight positions for each provider,
including elapsed current-day OpenWeather slots. Every incomplete case remains
on Main Display with the accepted message and proves no interpolation,
neighbor substitution, cache borrowing or cross-provider read.

No hard `write_boundary` is declared; advisory Weather Context/Forecast
Session/test files plus semantic forbidden scope are sufficient.

## Handoff

W18 execution and verification are complete and reconciled at the W18 boundary.
Exact next owner is the scheduler for caller-owned post-sync gates and the
downstream W19 recovery route; no W19 unblock, promotion or feature lifecycle
transition is applied by this plan.
