---
description: Revision-2 planning surface for FT-004 provider-capability long-term forecast.
status: active
last_updated: 2026-08-12
---
# FT-004 — Feature plan

## Objective

Preserve the shared long-term screen while applying the selected provider's
honest daily capability: Open-Meteo requires and fills 10 records; OpenWeather
requires 8 and renders the same ten dated positions with the last two empty.

## Accepted basis

- Feature: [.memory-bank/features/FT-004-ten-day-forecast.md](../../.memory-bank/features/FT-004-ten-day-forecast.md)
- Direct requirements: `REQ-010`, `REQ-022`, `REQ-026`
- Global Backbone: `complete`, Planning Revision `2`
- Foundation Gate: `TASK-002-T3-FT-000-W1`, status `done`; Foundation
  revalidation is successful
- Sequential prerequisite: `TASK-021-T2-FT-003-W18`

## Reconciled queue

| Order | Task | Tier | Wave | Status | Depends on | Role |
|---|---|---|---|---|---|---|
| 1 | `TASK-006-T3-FT-004-W5` | T3 | W5 | done | `TASK-013-T3-FT-003-W5` | Historical ten-day screen and exit behavior |
| 2 | `TASK-022-T2-FT-004-W19` | T2 | W19 | done | `TASK-021-T2-FT-003-W18` | Current 10 versus 8+2 capability delta |

Queue action: `completed` after the scheduler recorded W19 closure. W19 keeps
its identity, T2 tier, W19 wave, dependency on W18 and acceptance scope; the
earlier transitive block remains historical task-card evidence. W18 and W20
remain done, while TASK-020 remains failed after 3/3 unsuccessful attempts.

## Current acceptance ownership

| Acceptance | Current owner | Boundary |
|---|---|---|
| `FT-004-AC-001`, `FT-004-AC-002`, `FT-004-AC-005`, `FT-004-AC-006` | `TASK-022-T2-FT-004-W19` | Provider thresholds, ten dates and OpenWeather 8+2 honesty |
| `FT-004-AC-003`, `FT-004-AC-004` | `TASK-006-T3-FT-004-W5` | Unchanged available-card presentation and shared exit flow |

W5 remains historical for its former single-provider acceptance; it does not
prove the Revision-2 8+2 delta. W19's independent evidence is recorded in the
[W19 verification](../../.protocols/TASK-022-T2-FT-004-W19/verification.md),
[completeness matrix](../../.tasks/TASK-022-T2-FT-004-W19/long-term-completeness-matrix.json)
and [FT-004 semantic report](../../.tasks/FT-004/FT-004-S-RED-VERIFY-final-report-docs-01.md).

## Canonical SDD coverage

No spec is created or extended. Reuse:

- [FT-004 Long-Term Forecast Session Surface](../../.memory-bank/contracts/capability-interfaces.md#ft-004-long-term-forecast-session-surface)
  and [Forecast Sessions to Weather Context](../../.memory-bank/contracts/capability-interfaces.md#forecast-sessions-to-weather-context)
- [Provider Capability Matrix](../../.memory-bank/contracts/weather-provider.md#provider-capability-matrix),
  [Provider-Neutral Response](../../.memory-bank/contracts/weather-provider.md#provider-neutral-response-contract),
  [Mapping and Timezone](../../.memory-bank/contracts/weather-provider.md#mapping-and-timezone-obligations)
  and [Failure Rules](../../.memory-bank/contracts/weather-provider.md#failure-rules)
- [FT-004 Long-Term Forecast Records](../../.memory-bank/domains/local-data.md#ft-004-long-term-forecast-records)
  and [Deterministic Host-Side Checks](../../.memory-bank/testing/runtime-verification.md#deterministic-host-side-checks)

## Scope and proof

W19 is one T2 domain/session outcome. GREEN requires both entry cards against
complete and one-short provider sets, ten selected-city dates for both
providers, all ten Open-Meteo values, and OpenWeather positions 1–8 filled with
9–10 explicitly empty. No daily record may be synthesized, duplicated or
borrowed. The existing 2x5 available-card presentation and exit flow are
regression checks only.

No hard `write_boundary` is declared; advisory Weather Context/Forecast
Session/test files and semantic controls are sufficient.

## Handoff

W19 completion is reconciled at the wave boundary. The scheduler/`/autopilot`
owner now performs caller-owned post-sync lint and strict doctor before any
separate terminal or promotion decision; this plan does not change those
states. Target-device/live-provider evidence remains `DEFERRED`, with no
runtime `PASS` claim.
