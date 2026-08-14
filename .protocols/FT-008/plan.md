---
description: Revision-2 planning surface for FT-008 provider, key and offline location settings.
status: active
last_updated: 2026-08-11
---
# FT-008 — Feature plan

## Objective

Extend the accepted offline location Settings with explicit provider choice:
Open-Meteo is the first-run/default no-key path; OpenWeather is enabled only by
owner selection with its personal local key. Keep failures contextual and
selection-stable, and show both Open-Meteo and GeoNames attribution.

## Accepted basis

- Feature: [.memory-bank/features/FT-008-weather-location-settings.md](../../.memory-bank/features/FT-008-weather-location-settings.md)
- Direct requirements: `REQ-017`, `REQ-018`, `REQ-024`, `REQ-027`,
  `REQ-028`
- Global Backbone: `complete`, Planning Revision `2`
- Foundation Gate: `TASK-002-T3-FT-000-W1`, status `done`; Foundation
  revalidation is successful
- Planning-time production evidence: the unconditional Yandex-era key surface
  was as-is behavior to migrate, not the target contract

## Reconciled queue

| Order | Task | Tier | Wave | Status | Depends on | Role |
|---|---|---|---|---|---|---|
| 1 | `TASK-010-T3-FT-008-W9` | T3 | W9 | done | `TASK-009-T3-FT-007-W8` | Historical key/location/catalog implementation |
| 2 | `TASK-019-T3-FT-008-W16` | T3 | W16 | done | `TASK-018-T3-FT-002-W15` | Current provider/key/attribution delta |

Queue action: `reconciled`. W9 identity, dependency, terminal status and
evidence remain unchanged. W16 is `done` after final Attempt-3 functional
`PASS` and semantic `semantic-pass`; the two unsuccessful attempts remain
traceable in task-owned evidence. Downstream promotion remains external.

## Current acceptance ownership

| Acceptance | Current owner | Boundary |
|---|---|---|
| `FT-008-AC-002`, `FT-008-AC-003`, `FT-008-AC-004`, `FT-008-AC-005` | `TASK-010-T3-FT-008-W9` | Unchanged default/selected location, offline catalog, aliases and GeoNames attribution |
| `FT-008-AC-001`, `FT-008-AC-006`, `FT-008-AC-007`, `FT-008-AC-008` | `TASK-019-T3-FT-008-W16` | OpenWeather-only key, provider-context failure, selection and Open-Meteo attribution |

W9's AC-001/AC-006 locators remain historical evidence for the former
single-provider semantics; W16 is their sole current Revision-2 owner. W16
regression-checks location/catalog preservation but does not duplicate W9
implementation ownership.

## Canonical SDD coverage

No spec is created or extended. Reuse:

- [AD-006](../../.memory-bank/architecture/system-architecture.md#ad-006---openweather-owner-key-is-local-and-redacted)
  and [AD-008](../../.memory-bank/architecture/system-architecture.md#ad-008---selected-provider-isolation-is-owned-by-weather-context)
- [Weather Access Settings Surface](../../.memory-bank/contracts/capability-interfaces.md#weather-access-settings-surface)
  and [Location Refresh Orchestration](../../.memory-bank/contracts/capability-interfaces.md#location-refresh-orchestration)
- [Provider Selection](../../.memory-bank/contracts/weather-provider.md#provider-selection-and-dispatch),
  [Credential Rules](../../.memory-bank/contracts/weather-provider.md#credential-and-evidence-rules)
  and [Attribution Boundary](../../.memory-bank/contracts/weather-provider.md#attribution-and-terms-boundary)
- [Local API-Key Contract](../../.memory-bank/contracts/local-secret-handling.md#local-api-key-handling-contract),
  [storage](../../.memory-bank/contracts/local-secret-handling.md#storage-mechanism-boundary)
  and [evidence](../../.memory-bank/contracts/local-secret-handling.md#evidence-and-verification)

## Scope and proof

W16 is one T3 Settings/secret outcome. Its RED was the absence of provider
state, the unconditional former key path and missing Open-Meteo attribution.
Final GREEN proves first-run Open-Meteo, explicit OpenWeather switch, contextual key
persistence/reopen, provider-stable inline failures, and ordered dual
attribution using only synthetic redacted evidence.

Provider HTTP transport, actual selected dispatch, cache/history and forecast
completeness remain downstream W17–W19. No third provider, shared key,
backend/proxy, plugin framework, registry, DI framework or event bus is added.
No hard `write_boundary` is declared; the three advisory files and semantic
controls are sufficient.

The current transition safeguard denies provider-unidentified legacy key
access/refresh. Planned TASK-020 must atomically replace it with selected-
OpenWeather-authorized access while implementing selected-provider dispatch.
Physical-device/live-provider evidence remains `DEFERRED`; no runtime `PASS`
is claimed.

## Handoff

W16 closure is reconciled. Scheduler-owned post-sync lint/strict-doctor and the
separate TASK-020 promotion-eligibility pass remain outside this plan.
