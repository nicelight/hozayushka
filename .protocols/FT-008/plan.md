---
description: Planning surface for FT-008 weather access and offline location settings.
status: active
last_updated: 2026-08-07
---
# FT-008 — Feature plan

## Objective

Create one independently verifiable Settings & Location outcome: local
personal API-key access, default/selected city persistence, offline
country-first and scoped city search, accepted aliases/attribution, and the
validated city-change refresh request with failure preservation.

## Accepted basis

- Feature: [.memory-bank/features/FT-008-weather-location-settings.md](../../.memory-bank/features/FT-008-weather-location-settings.md)
- Epic: [.memory-bank/epics/EP-004-settings-location.md](../../.memory-bank/epics/EP-004-settings-location.md)
- Direct requirements: `REQ-017`, `REQ-018`, `REQ-024`
- Global Backbone: `complete`, Planning Revision `1`
- Foundation Gate: `TASK-002-T3-FT-000-W1`, status `done`
- Approved predecessor: `TASK-009-T3-FT-007-W8`, status `planned`
- Clarified PRD: `clarification_status: complete`

## Queue

| Order | Task | Tier | Wave | Status | Depends on | Primary owner |
|---|---|---|---|---|---|---|
| 1 | `TASK-010-T3-FT-008-W9` | T3 | W9 | planned | `TASK-009-T3-FT-007-W8` | Settings & Location |

One task is sufficient. Key validation, local retention, offline catalog
selection and city-change refresh are one user-facing Settings outcome with
one mutable owner and one claim-linked proof path. It crosses only the
registered Main Display → Settings & Location, Settings & Location → Weather
Context, Weather Context → Yandex Weather Adapter and Settings & Location →
Bundled Location Catalog boundaries. It is not split by screen/file, catalog
asset, adapter, persistence primitive or test artifact.

## Acceptance closure

All six FT-008 ACs are owned by `TASK-010-T3-FT-008-W9` through exact feature
locators. `REQ-017` covers key/location persistence and refresh, `REQ-018`
covers the offline catalog and attribution, and `REQ-024` covers secret safety.
FT-002 remains the owner of normalized weather/cache/freshness behavior and
FT-009 remains the owner of alert/glass personalization.

## Canonical SDD coverage

All applicable concerns reuse existing subject-based canonical specs. No new
canonical specification, competing path or optional behavior-spec file is
required.

| Concern | Action | Canonical basis |
|---|---|---|
| Architecture, ownership and graph | `reuse` | `system-architecture.md#capability-slice-runtime`, `#ad-002---application-owned-local-state-is-the-product-source-of-truth`, `#ad-003---cross-slice-orchestration-stays-in-a-capability-owner`, `#ad-006---user-api-keys-are-local-and-redacted`; `boundary-map.md#modules`, `#dependency-graph`, `#accepted-ownership-summary` |
| Settings/location and refresh contracts | `reuse` | `capability-interfaces.md#main-display-to-settings-and-location`, `#location-refresh-orchestration`, `#settings-and-location-to-bundled-location-catalog`, `#weather-context-to-settings-and-location` |
| Provider and failure boundary | `reuse` | `weather-provider.md#weather-provider-boundary`, `#refresh-cache-and-failure-rules`, `#credential-and-evidence-rules` |
| Local data and persistence | `reuse` | `local-data.md#ownership-matrix`, `#durable-data-rules`, `#validation-and-serialization-boundaries` |
| Secret handling | `reuse` | `local-secret-handling.md#local-api-key-handling-contract`, `#storage-mechanism-boundary`, `#evidence-and-verification` |
| Platform compatibility and proof | `reuse` | `platform-runtime.md#compatibility-and-failure-rules`; `runtime-verification.md#deterministic-host-side-checks`, `#redacted-integration-fixtures`, `#secret-and-artifact-checks` |

No `needed_before_tasks` Backbone row remains and Planning Revision remains
positive and unchanged at `1`.

## Scope and execution path

Settings & Location owns the validated key and location state, reads the
immutable bundled catalog, and requests Weather Context refresh only after a
valid location write. Weather Context owns provider refresh, normalization,
cache/history and freshness; the adapter owns transport mapping. Main Display
only opens Settings and renders the selected-city projection. The composition
root wires the graph but owns no Settings or refresh business logic.

In scope: the accepted six ACs, Khujand default, GeoNames subset/search,
Russian/canonical/ASCII aliases, attribution, inline errors, last-valid-value
preservation, redacted refresh request and offline/network failure behavior.
Out of scope: FT-002 weather semantics, FT-009 personalization, new location
sources, Google Services, backend/cloud/accounts, shared credentials, new
events/dependencies and unaccepted Settings controls.

## Verification route

- `./gradlew clean assembleDebug`
- `./gradlew testDebugUnitTest`
- Known isolated Settings state, synthetic credential, bundled redacted
  fixture, safe reset/cleanup and no live credentials.
- Host checks prove settings/catalog/provider semantics; later device evidence
  is limited to host-insufficient Settings readability/navigation behavior.
- Planning produces no runtime evidence.

## Handoff

After this task-plan surface is accepted, the immediate route is
`/review-tasks-plan FT-008`; execution and all verification/sync skills are
outside this planning run.
