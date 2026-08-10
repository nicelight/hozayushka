---
description: Planning surface for FT-002 main weather cards and local context.
status: active
last_updated: 2026-08-10
---
# FT-002 — Feature plan

## Objective

Reconcile FT-002 into the smallest executable outcome that turns the existing
weather walking shell into the accepted four-card context: ordered display-ready
cards, selected-city day/night presentation, temperature palette and static
pseudo-glass, successful-cache freshness, seven-day local pressure history and
honest first-run/offline/stale/fallback states.

## Accepted basis

- Feature: [.memory-bank/features/FT-002-weather-cards-context.md](../../.memory-bank/features/FT-002-weather-cards-context.md)
- Epic: [.memory-bank/epics/EP-002-weather-context.md](../../.memory-bank/epics/EP-002-weather-context.md)
- Direct FT-002 requirements: `REQ-005`, `REQ-006`, `REQ-007`, `REQ-008`, `REQ-026`
- FT-002 integration claims: `REQ-022`, `REQ-023`, `REQ-024`, `REQ-025`
- Global Backbone: `complete`, Planning Revision `1`
- Foundation Gate: `TASK-002-T3-FT-000-W1`, status `done`
- Historical predecessor: `TASK-003-T3-FT-001-W2`, status `done`; latest
  completed baseline for W15: `TASK-017-T3-FT-001-W14`, status `done`
- Clarified PRD: `clarification_status: complete`

## Queue

| Order | Task | Tier | Wave | Status | Depends on | Owner |
|---|---|---|---|---|---|---|
| 1 | `TASK-004-T3-FT-002-W3` | T3 | W3 | done | `TASK-003-T3-FT-001-W2` | Weather Context capability |
| 2 | `TASK-018-T3-FT-002-W15` | T3 | W15 | planned | `TASK-017-T3-FT-001-W14` | Weather Context / Yandex adapter |

The original W3 task remains the owner of the independently observable
Weather Context card/cache/history outcome. W15 is one cohesive follow-up
because production transport, provider-shaped mapping, key redaction, bounded
failure handling and composition-root selection are one independently
verifiable provider integration outcome; it is not split by file, layer or test
artifact. Weather Context remains the primary capability owner, while the
Yandex adapter is the existing external-boundary change unit.

W15 depends on the latest completed W14 baseline so it builds on the current
Weather Context projection/decode path. This is a technical prerequisite, not
a rewrite of W2-W14 history or a new product dependency between consumer
features.

## RTM-facing ownership map

FT-002 keeps the following narrow integration deltas in its accepted feature
surface. The RTM primary owners remain unchanged; this task does not inherit
their proof and does not replace their ownership.

| Requirement | FT-002-owned delta | Exact feature proof | RTM primary owner retained |
|---|---|---|---|
| `REQ-022` | Selected-city API timezone drives weather dates and day/night; device clock/date remains outside this claim. | `FT-002-AC-002` | FT-001 device clock/date; FT-003/FT-004 retain their forecast-specific claims. |
| `REQ-023` | FT-002 card palette, static glass and residual card readability/visual proof. | `FT-002-AC-003` | FT-001 display shell/readability baseline. |
| `REQ-024` | FT-002 provider and evidence path remains synthetic/redacted. User-facing key input/validation is excluded. | `FT-002-AC-007` | FT-008 settings and API-key behavior. |
| `REQ-025` | Weather failure leaves the existing clock/timer/cancellation/overdue paths usable. | `FT-002-AC-004` | FT-006 timer lifecycle. |

## Canonical SDD coverage

All applicable concerns reuse the existing subject-based canonical specs. The
provider contract already fixes the accepted endpoint/query/header, normalized
semantic fields, failure atomicity and credential restrictions; Local Secret
Handling already fixes ephemeral retrieval/redaction; Platform Runtime already
fixes network-signal ownership and failure compatibility. The W15 manifest
permission and platform/JDK transport are task-level implementation details,
not a new public/provider contract. No new specification or behavior example is
required.

| Concern | Action | Canonical basis | Planning reason |
|---|---|---|---|
| Deployable architecture and capability ownership | `reuse` | [System Architecture](../../.memory-bank/architecture/system-architecture.md#architecture-spine), [Data Flow and Ownership](../../.memory-bank/architecture/system-architecture.md#data-flow-and-ownership) | Weather Context owns cross-slice weather orchestration; composition-root work is wiring only. |
| Accepted module inventory and graph | `reuse` | [Boundary Map modules](../../.memory-bank/contracts/boundary-map.md#modules), [Dependency Graph](../../.memory-bank/contracts/boundary-map.md#dependency-graph) | All changed units and accepted edges already have registered identities. |
| Platform lifecycle/network/time signals and wiring | `reuse` | [Platform Runtime Boundary Ownership](../../.memory-bank/contracts/platform-runtime.md#boundary-ownership), [Platform Compatibility and Failure Rules](../../.memory-bank/contracts/platform-runtime.md#compatibility-and-failure-rules), [Runtime Composition](../../.memory-bank/architecture/system-architecture.md#runtime-composition) | Android OS owns the signals; the Application Composition Root and Android Runtime Adapter only lift them through existing contracts. Weather Context owns refresh/freshness/projection. No new product edge is introduced. |
| Main Display weather projection | `reuse` | [Main Display to Weather Context](../../.memory-bank/contracts/capability-interfaces.md#main-display-to-weather-context) | Main Display reads exactly four display-ready cards and never writes weather state. |
| Settings/location refresh input | `reuse` | [Weather Context to Settings and Location](../../.memory-bank/contracts/capability-interfaces.md#weather-context-to-settings-and-location), [Location Refresh Orchestration](../../.memory-bank/contracts/capability-interfaces.md#location-refresh-orchestration) | Weather Context consumes validated location and refreshes only after the accepted Settings boundary. |
| Yandex mapping and failure semantics | `reuse` | [Weather Provider Boundary](../../.memory-bank/contracts/weather-provider.md#weather-provider-boundary), [FT-002 Current and Daily Mapping](../../.memory-bank/contracts/weather-provider.md#ft-002-current-and-daily-mapping), [Refresh, Cache and Failure Rules](../../.memory-bank/contracts/weather-provider.md#refresh-cache-and-failure-rules) | Existing provider contract defines the accepted source, timezone split, required/optional fields and atomic failure behavior. |
| Card presentation and fallback | `reuse` | [Display-ready card contract](../../.memory-bank/contracts/weather-card-presentation.md#display-ready-card-contract), [Temperature and glass rules](../../.memory-bank/contracts/weather-card-presentation.md#temperature-and-glass-rules), [Pressure trend and fallback rules](../../.memory-bank/contracts/weather-card-presentation.md#pressure-trend-and-fallback-rules) | Existing contract covers order, palette, pseudo-glass, day/night, moon and neutral-condition behavior. |
| Weather data ownership and retention | `reuse` | [FT-002 Weather Context Records](../../.memory-bank/domains/local-data.md#ft-002-weather-context-records), [Retention and Cleanup](../../.memory-bank/domains/local-data.md#retention-and-cleanup) | Weather Context remains the sole cache/history owner with installation-relative seven-day retention. |
| Freshness and first-run lifecycle | `reuse` | [Weather Freshness Contract](../../.memory-bank/states/lifecycle-map.md#weather-freshness-contract), [FT-002 First-run and Failure Projection](../../.memory-bank/states/lifecycle-map.md#ft-002-first-run-and-failure-projection) | Accepted `fresh`, `stale_empty`, failed-refresh and dated first-run projections are already defined. |
| Local API-key boundary | `reuse` | [Local API-Key Handling Contract](../../.memory-bank/contracts/local-secret-handling.md#local-api-key-handling-contract), [Evidence and Verification](../../.memory-bank/contracts/local-secret-handling.md#evidence-and-verification) | Provider requests and fixtures must remain synthetic/redacted; FT-008 owns user-facing key settings. |
| Verification route | `reuse` | [Deterministic Host-Side Checks](../../.memory-bank/testing/runtime-verification.md#deterministic-host-side-checks), [Redacted Integration Fixtures](../../.memory-bank/testing/runtime-verification.md#redacted-integration-fixtures), [Target-Device Evidence](../../.memory-bank/testing/runtime-verification.md#target-device-evidence), [Platform Runtime Verification Route](../../.memory-bank/contracts/platform-runtime.md#verification-route) | Host checks prove data and signal/failure rules; device evidence is reserved for visual/runtime outcomes not reliably proven on host. |

W15 has no canonical-spec extension or competing identity. Downstream forecast
read models and the Settings credential/location seam remain compatible through
the existing normalized Weather Context and Settings boundaries; these are
dependency-context regression checks only, not W15-owned feature acceptance.
The exact contract locators for that context are carried in the W15 task's
`source_artifacts`; completed historical task records and lifecycle/RTM values
are not rewritten.

FT-002 frontmatter remains `spec_design_status: complete` with the existing
subject links plus Local Secret Handling. No `needed_before_tasks` Backbone
row remains and Planning Revision remains positive and unchanged at `1`.

## Scope boundary

In scope for the historical W3 outcome: normalized current/daily provider mapping for the accepted card
projection, four-card order and sizing, selected-city timezone/day-night and
moon fallback, 78-color palette with sign/clamp rules, shared static glass,
refresh triggers, successful cache/freshness, installation-relative seven-day
history, pressure arrows, first-run yesterday, stale/offline/failed-refresh and
unknown-condition states, plus the existing Main Display read boundary. W15
adds the production Yandex request/response path, current/daily/hourly adapter
mapping, finite timeout/error mapping, off-main composition wiring, the minimum
`INTERNET` permission and synthetic/redacted host proof.

Out of scope: FT-001 clock/date/fullscreen/gesture behavior, hourly or long-term
forecast session UI/state, preset/countdown/overdue behavior, Settings catalog or
API-key input/validation, personalization controls, backend/cloud/accounts,
Google Services, reboot recovery, live credentials and any new dependency or
graph edge.

## Expected advisory change surface

- `app/src/main/kotlin/com/hozayushka/app/weather/` — Weather Context models,
  normalization, refresh cadence, cache/freshness, history and projection.
- `app/src/main/kotlin/com/hozayushka/app/adapters/weather/` — provider request/
  response mapping behind the accepted Yandex boundary, without cache ownership.
- `app/src/main/kotlin/com/hozayushka/app/settings/` — only the existing public
  location/credential read and valid-city refresh seam needed by Weather Context;
  no Settings product surface.
- `app/src/main/kotlin/com/hozayushka/app/display/` — card projection rendering
  behind Main Display ownership; do not replace FT-001 shell behavior.
- `app/src/main/kotlin/com/hozayushka/app/app/` — wiring/lifecycle signal handoff
  only when required by the existing composition-root responsibility.
- `app/src/main/kotlin/com/hozayushka/app/adapters/platform/` — only the
  accepted network/time signal boundary if the current scaffold needs its seam.
- `app/src/main/res/` — accepted card resources and the single compile-time
  palette source when needed by the existing Android scaffold.
- `app/src/test/kotlin/com/hozayushka/app/` and
  `app/src/test/resources/fixtures/` — deterministic host probes and redacted
  current/daily, missing-field, stale-cache and provider-failure fixtures.

These paths are advisory and non-exhaustive. W15's deliberate hard write
boundary is recorded only on its indexed task card; the semantic scope,
forbidden scope and stop conditions remain binding.

## Applicable quality gates and UAT

- `./gradlew clean assembleDebug` — proves the Android application assembles
  after the Weather Context and projection changes.
- `./gradlew testDebugUnitTest` — proves deterministic mapping, palette,
  freshness, history, trend, fallback and boundary checks.
- W15 additionally requires fake-transport request-shape, Yandex-shaped
  current/daily/hourly mapping, timeout/error/cache-preservation,
  off-main-wiring, fixture-isolation and secret/artifact redaction checks.
- Target-device evidence from
  [Runtime Verification](../../.memory-bank/testing/runtime-verification.md#target-device-evidence)
  — only for accepted 1280×720 card readability and static pseudo-glass results
  that host checks cannot reliably establish. This planning run creates no
  runtime evidence.

## Claim-linked proof plan

The W3 task card owns the original FT-002 acceptance claims. W15 owns only the
production provider integration delta and its T3 harm-driving transport,
secret, failure and wiring claims. Execution must first record honest
pre-implementation RED for each applicable W15 claim, preserve any already-green
condition and then prove claim-equivalent GREEN with redacted fixtures and
isolated local state; W3/W14 evidence is dependency context, never inherited
proof.

| Claim | Decisive result | Artifact |
|---|---|---|
| `FT-002-AC-001 / REQ-005` | Four cards are ordered yesterday/today/tomorrow/day-after; Today is larger and the other three are equal smaller cards. | Deterministic projection/layout assertion |
| `FT-002-AC-002 / REQ-005, REQ-022` | Filled cards contain date, temperature, illustration and temperature background without textual day/weather labels; selected-city timezone drives only weather date/day-night and moon fallback. | Redacted fixture mapping and projection output |
| `FT-002-AC-003 / REQ-006, REQ-023` | Sign, all 78 palette values, endpoint clamp and shared static glass match the accepted contract; readability/static effect conditions pass where device evidence is required. | Host palette/material result plus scoped target-device observation |
| `FT-002-AC-004 / REQ-007, REQ-025` | Launch/city-change/30-minute refresh, successful cache, 24-hour offline freshness and stale four-contour state pass; clock/timer path remains usable on failure. | Deterministic lifecycle/cache fixture result and integration output |
| `FT-002-AC-005 / REQ-008` | Installation-relative seven-day history, 3-hour/12-hour thresholds, yesterday maximum-change rule and dated first-run empty contour pass without layout shift. | Isolated history/trend probe output |
| `FT-002-AC-006 / REQ-026` | Unknown condition and missing optional data use neutral fallbacks without crash or invented text while available temperature/color remains. | Redacted missing-field fixture result |
| `FT-002-AC-007 / REQ-024` | Synthetic provider credentials are redacted and absent from source, resources, logs, fixtures, screenshots and evidence; FT-008 remains the owner of key input/validation. | Secret/artifact scan result |

### W15 production integration claims

| Claim | Decisive result | Artifact |
|---|---|---|
| Accepted Yandex request shape | Fake transport observes the canonical endpoint, coordinates, `hours=true` and header; no key value is retained in the receipt. | Redacted request-shape receipt |
| Provider-shaped mapping | Redacted Yandex response produces existing current/daily/hourly DTOs and preserves selected-city timezone and required-data completeness. | Deterministic parser/compatibility output |
| Timeout/error/fallback | Finite transport failures and malformed/incomplete required data do not replace the successful cache and preserve stable clock/timer behavior. | Isolated failure/cache comparison |
| Local secret path | Synthetic key is retrieved only through Settings/WeatherAccessReader, used only for the request header and absent from artifacts/APK. | Secret/artifact scan receipt |
| Composition/runtime wiring | Production adapter is selected, redacted fixture remains isolated, `INTERNET` is the only added permission and production refresh is off the UI thread. | Boundary/wiring and host executor receipt |

## Constraints and invariants

- Weather Context owns refresh, normalization, cache/history writes, freshness,
  pressure trends and fallback; consumers never write its private storage.
- Main Display consumes the display-ready projection through the accepted
  Main Display → Weather Context contract and does not read raw provider fields.
- Settings & Location owns selected location and credential validation; a city
  refresh is requested only after valid Settings state is persisted.
- The provider adapter contains transport/mapping only and never owns cache or
  history; the composition root only wires/lifts accepted signals.
- Android OS owns device time, lifecycle and network availability; the
  Application Composition Root and Android Runtime Adapter own only the
  accepted signal/wiring lift. Weather Context owns refresh cadence,
  cache/freshness and failure projection through existing capability contracts.
- Use selected-city API timezone for weather dates/day boundaries and device
  timezone only for the clock shell; preserve the existing FT-001 composition.
- Preserve clock, timer lifecycle, cancellation and overdue dismissal when
  network/provider weather is unavailable.
- Never add live API keys, embedded/shared secrets, backend/cloud/accounts,
  Google Services, reboot recovery, realtime blur or a new graph edge.
- W15 must not add a Gradle dependency or modify the accepted provider/public
  capability contracts; a need for either is a planning halt.

## Direct normative inputs

- [.memory-bank/features/FT-002-weather-cards-context.md](../../.memory-bank/features/FT-002-weather-cards-context.md)
- [.memory-bank/epics/EP-002-weather-context.md](../../.memory-bank/epics/EP-002-weather-context.md)
- [.memory-bank/requirements.md](../../.memory-bank/requirements.md)
- [.memory-bank/prd.md](../../.memory-bank/prd.md)
- [.memory-bank/invariants.md](../../.memory-bank/invariants.md)
- [.memory-bank/glossary.md](../../.memory-bank/glossary.md)
- [.memory-bank/architecture/system-architecture.md](../../.memory-bank/architecture/system-architecture.md)
- [.memory-bank/contracts/boundary-map.md](../../.memory-bank/contracts/boundary-map.md)
- [.memory-bank/contracts/capability-interfaces.md](../../.memory-bank/contracts/capability-interfaces.md)
- [.memory-bank/contracts/platform-runtime.md](../../.memory-bank/contracts/platform-runtime.md)
- [.memory-bank/contracts/weather-provider.md](../../.memory-bank/contracts/weather-provider.md)
- [.memory-bank/contracts/weather-card-presentation.md](../../.memory-bank/contracts/weather-card-presentation.md)
- [.memory-bank/contracts/local-secret-handling.md](../../.memory-bank/contracts/local-secret-handling.md)
- [.memory-bank/domains/local-data.md](../../.memory-bank/domains/local-data.md)
- [.memory-bank/states/lifecycle-map.md](../../.memory-bank/states/lifecycle-map.md)
- [.memory-bank/testing/runtime-verification.md](../../.memory-bank/testing/runtime-verification.md)
- [.memory-bank/workflows/tier-policy.md](../../.memory-bank/workflows/tier-policy.md)

## Handoff

After this planning surface is accepted, the immediate route is
`/review-tasks-plan FT-002`; execution, review, `/mb-doctor`, `/verify`,
`/red-verify` and `/mb-sync` are not part of this planning run.
