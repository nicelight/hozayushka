---
description: Revision-2 planning surface for FT-002 weather cards and provider-neutral Weather Context.
status: active
last_updated: 2026-08-12
---
# FT-002 — Feature plan

## Objective

Replace the as-is Yandex production path with one provider-neutral Weather
Context that explicitly dispatches to exactly two adapters: default/no-key
Open-Meteo or owner-selected OpenWeather with its local key. Preserve the
accepted card presentation while isolating normalized cache and seven-day
history by provider plus location and forbidding fallback or mixing.

## Accepted basis

- Feature: [.memory-bank/features/FT-002-weather-cards-context.md](../../.memory-bank/features/FT-002-weather-cards-context.md)
- Direct requirements: `REQ-005`–`REQ-008`, `REQ-022`, `REQ-024`–`REQ-026`,
  `REQ-029`
- Global Backbone: `complete`, Planning Revision `2`
- Foundation Gate: `TASK-002-T3-FT-000-W1`, status `done`; Foundation
  revalidation is successful
- Current production evidence: Yandex is an implementation to remove, not an
  accepted target provider

## Reconciled queue

| Order | Task | Tier | Wave | Status | Depends on | Role |
|---|---|---|---|---|---|---|
| 1 | `TASK-004-T3-FT-002-W3` | T3 | W3 | done | `TASK-003-T3-FT-001-W2` | Historical card/cache implementation |
| 2 | `TASK-018-T3-FT-002-W15` | T3 | W15 | done | `TASK-017-T3-FT-001-W14` | Historical Yandex production integration |
| 3 | `TASK-020-T3-FT-002-W17` | T3 | W17 | failed | `TASK-019-T3-FT-008-W16` | Preserved terminal migration attempt; no fourth execution |
| 4 | `TASK-023-T3-FT-002-W20` | T3 | W20 | done | `TASK-019-T3-FT-008-W16` | Completed selected OpenWeather activation repair |
| 5 | `TASK-025-T3-FT-002-W22` | T3 | W22 | done | `TASK-024-T3-FT-001-W21` | Historical Main Display six-state condition-illustration baseline |
| 6 | `TASK-028-T3-FT-002-W25` | T3 | W25 | planned | `TASK-027-T3-FT-001-W24` | Operator-feedback bounds/sun/pressure rendering adjustment |

Queue action: `created` for W25; the prior W20 action remains `reconciled` and
W22 remains a completed historical baseline. W17 remains
failed with its identity, 3/3 attempt history, evidence and terminal
disposition unchanged. `TASK-023-T3-FT-002-W20` is the indexed `done` task
that depends on the completed W16 Settings/key projection rather than on the
failed W17 card. The direct W18 downstream dependency remains explicitly wired
to `TASK-023-T3-FT-002-W20`; W19 remains transitively behind W18. Scheduler
blocked-state recovery, promotion and downstream lifecycle changes remain
external. W16 is the completed prerequisite: Settings owns provider selection
and the contextual OpenWeather key.

## Current acceptance ownership

| Acceptance | Current owner | Boundary |
|---|---|---|
| `FT-002-AC-001`, `FT-002-AC-003` | `TASK-004-T3-FT-002-W3` | Unchanged order, sizing, palette and pseudo-glass |
| `FT-002-AC-002`, `FT-002-AC-005`, `FT-002-AC-006` | `TASK-020-T3-FT-002-W17` historical failed attempt | Accepted migration implementation facts remain traceable, but W17 does not close the feature |
| `FT-002-AC-004`, `FT-002-AC-007`, `FT-002-AC-008` activation delta | `TASK-023-T3-FT-002-W20` | Valid selected OpenWeather key save triggers refresh, clears obsolete missing-key state on success, preserves redaction and keeps selected-provider isolation |
| `FT-002-AC-009` historical baseline | `TASK-025-T3-FT-002-W22` | Completed six-state Main Display illustration baseline and non-overlap evidence remain traceable |
| `FT-002-AC-009` adjustment | `TASK-028-T3-FT-002-W25` | Main Display reduces measured illustration bounds, enlarges the CLEAR sun disk moderately and renders visible measured pressure arrows without changing Weather Context semantics |

`TASK-018` remains authoritative evidence for its historical Yandex outcome,
but it owns no Revision-2 target claim. Dependency evidence is not reused as
proof for W17.

## Canonical SDD coverage

All concerns reuse existing canonical subjects; no spec is created or
extended:

- [System Architecture AD-006](../../.memory-bank/architecture/system-architecture.md#ad-006---openweather-owner-key-is-local-and-redacted)
  and [AD-008](../../.memory-bank/architecture/system-architecture.md#ad-008---selected-provider-isolation-is-owned-by-weather-context)
- [Boundary Map](../../.memory-bank/contracts/boundary-map.md#dependency-graph)
  and [Capability Interfaces](../../.memory-bank/contracts/capability-interfaces.md#main-display-to-weather-context)
- [Weather Provider](../../.memory-bank/contracts/weather-provider.md#provider-neutral-boundary),
  [selection](../../.memory-bank/contracts/weather-provider.md#provider-selection-and-dispatch),
  [capabilities](../../.memory-bank/contracts/weather-provider.md#provider-capability-matrix),
  [mapping](../../.memory-bank/contracts/weather-provider.md#mapping-and-timezone-obligations),
  [cache/history](../../.memory-bank/contracts/weather-provider.md#cache-history-and-refresh-rules),
  [failure](../../.memory-bank/contracts/weather-provider.md#failure-rules) and
  [credentials](../../.memory-bank/contracts/weather-provider.md#credential-and-evidence-rules)
- [Local Data FT-002 records](../../.memory-bank/domains/local-data.md#ft-002-weather-context-records)
  and [redacted fixtures](../../.memory-bank/testing/runtime-verification.md#redacted-integration-fixtures)

## Scope and evidence route

W17 remains the historical cohesive T3 migration unit: keep the existing
provider interface, replace Yandex with two concrete adapters, add explicit
selected-provider dispatch, retain normalization/cache/history in Weather
Context, and wire the existing modular monolith. Its accepted implementation
facts remain current baseline evidence, but its terminal semantic failure keeps
the activation outcome open. No plugin framework, registry, DI framework, event
bus, fallback or parallel provider request is authorized.

RED is the observed Yandex-only/providerless-state production baseline. GREEN
requires fake-transport request/decode results for both providers, exactly one
selected invocation, provider+location cache/history isolation, atomic failure
without fallback, and synthetic-key redaction where OpenWeather `appid`
exists only in the ephemeral outbound HTTPS request. FT-003 and FT-004 own
forecast completeness and projection, not W17.

No hard `write_boundary` is declared: the task card's concrete advisory files,
forbidden scope and stop conditions are the deliberate boundary while file
identities change during Yandex replacement.

The completed W16 safeguard and W17 implementation establish the selected
provider/key seam. `TASK-023-T3-FT-002-W20` repairs only the missing activation
edge: a valid
OpenWeather key save while OpenWeather remains selected requests the existing
Weather Context refresh, with no raw-key callback, no Open-Meteo fallback and
the existing successful normalization path clearing the obsolete missing-key
error. Its task-owned proof also includes a matched redacted host-side
clock/timer independence receipt. Physical-device/live-provider evidence
remains `DEFERRED`, with no runtime `PASS` claim.

## W20 activation repair boundary

`TASK-023-T3-FT-002-W20` is one cohesive T3 cross-slice repair owned by Weather
Context orchestration. Settings & Location remains the validated persistence
and secret owner; the Application Composition Root only wires the existing
off-UI refresh executor. The task proves the valid-key-save -> selected
OpenWeather refresh -> obsolete-error clearance sequence, selected-only
failure isolation and redacted synthetic-key handling. It does not re-prove or
rewrite W17's accepted migration implementation facts, reopen W17, or alter
FT-003/FT-004 forecast scope. W20 is now `done` after fresh functional `PASS`
and final independent T3 `semantic-pass`; the current handoff, verifier-owned
host/timer, functional and semantic evidence are linked from the task record.

The repair uses only existing registered edges and subject contracts. No new
module, public contract, event/message boundary, storage owner, provider,
fallback or dependency is introduced. Attempt 1 remains supporting-only; the
fresh Attempt-2 and verifier-owned proof is the current closure basis. Device/
live-provider evidence remains `DEFERRED`, with no runtime `PASS` claim.

## Handoff

TASK-020 remains `failed` after 3/3 attempts; `TASK-023-T3-FT-002-W20` and
`TASK-021-T2-FT-003-W18` are `done`, with W18's hourly evidence linked from
FT-003. TASK-022 remains transitively blocked through W18. Scheduler-owned
post-sync gates, downstream dependency recovery and the separate
promotion-eligibility pass remain outside this plan.

## W22 bounded visual baseline

W22 was one cohesive Main Display outcome: render deliberate, legible
condition illustrations over the existing four main weather cards without
obscuring temperature, date or pressure content. It is now `done` after
`TASK-024-T3-FT-001-W21`; its identity, evidence and history remain the
baseline for the same card surface. Current queue action is `created` for the
new W25 follow-up; the prior W20 action remains `reconciled`.

Current-state inspection found the normalized `WeatherIllustration` enum and
projection fields, but no illustration layer in `weatherCard`; the Unicode
`WeatherCardPresentation.illustrationText` helper belongs to forecast-card
composition and is not a sufficient Main Display treatment. Android's
existing Canvas/Path/Paint primitives are sufficient for sun, cloud, rain,
snow, neutral-cloud and moon silhouettes. No drawable asset, resource
pipeline, dependency or new public boundary is required or planned.

W22 is owned by Main Display composition through the existing
`Main Display -> Weather Context` read boundary. It must not change provider
selection/dispatch, cache/history, refresh/freshness, pressure calculation,
day/night source, temperature palette, pseudo-glass, clock/timer behavior,
forecast screens or textual day/weather-label rules. Stale and first-run
empty cards continue to render no illustration. The hard write boundary is
the existing `DisplayCapability.kt` plus focused `DisplayProjectionTest.kt`;
`app/src/main/res/` and `app/src/main/assets/` are forbidden for this task.

W22 required and recorded fresh task-scoped RED/GREEN, host unit/build gates,
static source/resource inspection, a deterministic rendered image/contact
sheet and measured bounds review proving recognizable condition states and no
painted illustration intersection with temperature/date/pressure content.
Target 1280x720 Samsung/custom-ROM readability remains deferred/non-blocking
without an authorized observation.

## W25 operator-feedback visual adjustment

The new task `TASK-028-T3-FT-002-W25` is the smallest cohesive T3 follow-up
after planned `TASK-027-T3-FT-001-W24`, because both tasks write the same Main
Display composition surface and must remain sequential. It owns no new feature
AC: it owns the accepted bounds/sun/pressure adjustment under
`FT-002-AC-009`; `REQ-008` and the registered pressure presentation contract
govern the output, while `WeatherCapability` remains the calculation owner.

W25 reduces the painted bounds of all six existing states, moderately enlarges
the CLEAR sun disk within its reduced overall composition, and replaces only
the Main Display Unicode pressure glyphs with measured Canvas/Path arrows. The
hard write boundary is exactly `DisplayCapability.kt` plus
`DisplayProjectionTest.kt`; no `WeatherCapability.kt` production change,
provider/network/settings/audio/timer/forecast/resource/asset/lifecycle path
or prior task-state change is authorized. Required handoff proof is fresh
RED/GREEN, measured icon and sun bounds at small/Today geometry, a pressure
UP/DOWN/zero-arrow contact sheet with explicit stroke-width/pixel visibility,
an independent rubric, build/unit/static gates and target-device `DEFERRED`.
This planning boundary performs no execution, emulator, adb, device, network,
provider or credential action.
