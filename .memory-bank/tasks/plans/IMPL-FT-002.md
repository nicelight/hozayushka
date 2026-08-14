---
description: Revision-2 implementation plan for FT-002 provider migration and Weather Context isolation.
status: active
last_updated: 2026-08-12
---
# IMPL-FT-002 — Two-provider Weather Context migration

## Outcome

Deliver the accepted weather-card data path through one Weather Context and
exactly two adapters: Open-Meteo by default without a key and OpenWeather only
after explicit Settings selection with an owner-local key. Remove Yandex from
production, keep provider transport in adapters, keep normalized state in
Weather Context, and prevent every form of cross-provider fallback or mixing.

## Ordered work

1. Preserve `TASK-004-T3-FT-002-W3` and
   `TASK-018-T3-FT-002-W15` exactly as terminal historical records.
2. Consume completed `TASK-019-T3-FT-008-W16`'s selected-provider/key
   projection and its temporary provider-unidentified access deny.
3. Preserve failed `TASK-020-T3-FT-002-W17` and all three attempt histories;
   no fourth execution is permitted.
4. Reconcile completed `TASK-023-T3-FT-002-W20` after W16: valid selected
   OpenWeather key save requests the existing Weather Context refresh,
   successful data clears the obsolete missing-key error, and selected-only
   failure/redaction plus clock/timer-independence rules remain intact.
5. Preserve the reconciled downstream route: completed W18 depends on
   `TASK-023-T3-FT-002-W20`; W19 remains behind W18. Preserve W19's ID, tier,
   wave, blocked lifecycle and historical block evidence until scheduler
   recovery.
6. Preserve completed `TASK-024-T3-FT-001-W21` and
   `TASK-025-T3-FT-002-W22` as historical Main Display composition records
   without reopening them or changing provider/state ownership.
7. After the planned `TASK-027-T3-FT-001-W24`, execute the new bounded
   `TASK-028-T3-FT-002-W25` visual adjustment for reduced illustration bounds,
   moderate CLEAR sun enlargement and measured Canvas/Path pressure arrows.

## Ownership and path

- Settings & Location owns selected provider, selected location and the
  OpenWeather owner key.
- Weather Context owns dispatch, normalization, refresh cadence, matching
  cache, seven-day history, trends and provider-neutral public projections.
- Open-Meteo and OpenWeather adapters own only outbound HTTPS shape and
  provider response decoding.
- FoundationRuntime wires the existing interfaces and contains no weather
  business policy.

The path is Settings projection → Weather Context explicit branch → exactly
one adapter → provider-neutral envelope → provider+location cache/history →
existing Main Display projection. A selected-provider failure ends on that
path; it never requests or substitutes the other adapter.

TASK-019 closes with provider-unidentified legacy key access/refresh denied.
TASK-020 must replace that deny atomically in the same change that installs
selected-provider dispatch: Open-Meteo remains credential-free, while only
selected OpenWeather may receive authorized transient key access. The
[TASK-019 task record](../TASK-019-T3-FT-008-W16.task.json) and
[semantic verification](../../../.protocols/TASK-019-T3-FT-008-W16/red-verification.md)
are the transition evidence; they do not prove TASK-020 acceptance.

## Acceptance and requirement ownership

| Current claim | Owner | Decisive GREEN |
|---|---|---|
| `AC-001 / REQ-005` | historical W3 | Fixed ordered card projection remains unchanged |
| `AC-003 / REQ-006, REQ-023` | historical W3 | Palette/glass remains unchanged |
| `AC-002 / REQ-005, REQ-022` | W17 historical migration attempt | Both adapters yield equivalent provider-neutral filled-card/city-time semantics; implementation fact remains traceable, feature remains open |
| `AC-004 / REQ-007, REQ-025` activation delta | W20 | Valid selected OpenWeather key save triggers immediate refresh and successful matching data clears obsolete missing-key state |
| `AC-005 / REQ-008` | W17 historical migration attempt | Seven-day provider+location history/trends never mix; implementation fact remains traceable |
| `AC-006 / REQ-026` | W17 historical migration attempt | Both adapters preserve optional fields and neutral fallback; implementation fact remains traceable |
| `AC-007 / REQ-024` activation/redaction delta | W20 | Key-save activation adds no secret-bearing callback/artifact and preserves synthetic-only redaction proof |
| `AC-008 / REQ-007, REQ-008, REQ-029` activation delta | W20 | Key-save refresh calls only selected OpenWeather and never falls back or mixes |
| `AC-009 / REQ-005, REQ-022, REQ-023, REQ-026` visual baseline | W22 historical | Main Display six-state Canvas illustration baseline and non-overlap evidence remain traceable |
| `AC-009 / REQ-005, REQ-022, REQ-023, REQ-026` visual adjustment | W25 | Main Display reduces measured icon bounds, enlarges the CLEAR sun disk moderately and renders measured visible pressure arrows from the existing projection without changing WeatherCapability calculation or card semantics |

Historical W3/W15 source locators and evidence remain intact; this table is the
single current Revision-2 ownership map.

## Advisory implementation surface

- `app/src/main/kotlin/com/hozayushka/app/adapters/weather/WeatherProviderAdapter.kt`
- remove/replace `YandexWeatherAdapter.kt` with project-conventional
  `OpenMeteoWeatherAdapter.kt` and `OpenWeatherWeatherAdapter.kt`
- `app/src/main/kotlin/com/hozayushka/app/weather/WeatherCapability.kt`
- `app/src/main/kotlin/com/hozayushka/app/app/FoundationRuntime.kt`
- adapter/context unit tests and redacted fixtures under `app/src/test/`

This is advisory and non-exhaustive. No hard `write_boundary` is selected
because replacement may require adjacent project-conventional files; the card's
semantic scope, forbidden scope and stop conditions remain binding.

## Claim-linked proof

- RED: only Yandex is wired; request credentials are mandatory; dispatch,
  cache and history do not carry complete selected-provider/location identity.
- GREEN mapping: deterministic redacted Open-Meteo and OpenWeather fixtures
  produce the provider-neutral current/card fields with selected-city timezone.
- GREEN dispatch: exact adapter implementation inventory is two; each selected
  refresh records one invocation and zero invocations of the other adapter.
- GREEN identity: launch/city/provider/cadence/freshness/history matrices prove
  matching provider+location partitions and no mixed trends.
- GREEN failure: auth, HTTP, timeout, malformed and incomplete responses are
  atomic and never trigger fallback/substitution.
- GREEN secret: a synthetic OpenWeather marker is observable only as
  redacted/presence evidence for the ephemeral HTTPS `appid`; Open-Meteo sends
  no credential and durable scans remain clean.

Applicable execution gates are the project-native clean build and host unit
suite plus Memory Bank/diff integrity. This planning run runs none of Gradle,
emulator or device routes and claims no runtime evidence.

## W17 terminal outcome

The authoritative
[`TASK-020-T3-FT-002-W17`](../TASK-020-T3-FT-002-W17.task.json) is `failed`
after `3/3` unsuccessful attempts. The final
[functional verification](../../../.protocols/TASK-020-T3-FT-002-W17/verification.md)
is `PASS` and preserves implemented migration facts: exact-two-provider
production inventory, Yandex removal, selected-only ordinary dispatch,
provider/location cache-history identity, provider-neutral mapping/fallbacks
and redacted credential handling. Attempt-1/2/3 executor and verifier history
remains durable and supporting-only.

The required
[semantic verification](../../../.protocols/TASK-020-T3-FT-002-W17/red-verification.md)
is `semantic-fail`: first-time OpenWeather selection refreshes before key
entry, while a later valid-key save performs zero provider calls and leaves the
obsolete missing-key error current. Therefore the accepted activation outcome,
FT-002 lifecycle and open RTM rows are not promoted by those implementation
facts. Device/live-provider evidence remains `DEFERRED`; no runtime `PASS` is
claimed.

## W20 repair outcome

`TASK-023-T3-FT-002-W20` is the smallest indexed repair for the admitted
activation defect and is `done` after fresh functional `PASS` and final
independent T3 `semantic-pass`. Its path is:

`valid selected OpenWeather key save` -> `existing Settings -> Weather Context
refresh command` -> `one coherent selected provider/location/key snapshot` ->
`OpenWeather only` -> `successful normalization clears prior missing-key error`.

The callback carries no raw key. Invalid/inapplicable saves are inert. A
selected-provider failure preserves selection and matching state, updates only
the selected-provider result and never requests Open-Meteo. The task is T3
because it crosses a runtime boundary and handles a local credential, but it
does not introduce a new public edge, event/message mechanism or storage owner.
Current evidence is linked from the [executor handoff](../../../.protocols/TASK-023-T3-FT-002-W20/handoff.md),
[fresh functional verification](../../../.protocols/TASK-023-T3-FT-002-W20/verification.md),
[verifier-owned evidence](../../../.tasks/TASK-023-T3-FT-002-W20/verifier-owned-evidence.md),
[fresh timer receipt](../../../.tasks/TASK-023-T3-FT-002-W20/verifier-owned-weather-refresh-timer-independence.json)
and [semantic verification](../../../.protocols/TASK-023-T3-FT-002-W20/red-verification.md).
Attempt 1 remains supporting-only. W20 is host/build/static/redacted proof;
target-device and live-provider evidence remain `DEFERRED`, with no runtime
`PASS` claimed.

## W22 visual illustration baseline

`TASK-025-T3-FT-002-W22` was the operator-requested Main Display follow-up,
planned after `TASK-024-T3-FT-001-W21`. It is now `done` and remains the
historical AC-009 baseline: a deliberately designed, legible visual layer for
`CLEAR`, `CLOUD`, `NEUTRAL_CLOUD`, `RAIN`, `SNOW` and `MOON` states, including
the existing selected-city day/night and optional moon-phase input, with
painted bounds kept separate from temperature, date and pressure-arrow
content.

The architecture preflight selects the existing Main Display capability as the
owner and the existing `Main Display -> Weather Context` read boundary. The
current projection already carries the semantic enum and `moonPhase`; the main
card currently omits the illustration, while the Unicode helper is used by
forecast-card composition. Android Canvas/Path/Paint primitives are sufficient,
so no new drawable/resource pipeline, asset, dependency, module, graph edge,
public contract or Weather Context/provider change is authorized.

The task keeps four-card order, Today sizing, temperature palette, pseudo-glass,
pressure semantics, stale/first-run empty behavior, no visible day/weather
labels, selected-provider identity, city-timezone day/night mapping and
clock/timer independence intact. Proof is host/build/static plus a deterministic
rendered image/contact sheet and measured bounds artifact; target 1280x720
Samsung/custom-ROM readability remains `DEFERRED` without a target observation.
No planning execution, emulator/device, ADB, network, live provider or
credential action is part of this plan.

## W25 bounded visual adjustment

`TASK-028-T3-FT-002-W25` is the smallest cohesive T3 follow-up after planned
`TASK-027-T3-FT-001-W24`, which is the latest task on the shared Main Display
write surface. It owns no new AC: it owns the accepted bounds/sun/pressure
adjustment under `FT-002-AC-009`, with `REQ-008` serving as the pressure-output
contract whose calculation remains WeatherCapability-owned.

The executor must capture fresh pre-change measurements, then reduce every
existing six-state painted icon envelope at both 223×444 and 279×444 geometry,
enlarge the CLEAR central sun disk moderately while keeping its full envelope
reduced, and replace only the Main Display Unicode pressure glyphs with
Canvas/Path arrows. The task requires measured icon/sun bounds, UP/DOWN/zero
arrow contact-sheet proof, explicit stroke width and pixel visibility, an
independent rubric, non-overlap/content/order/Today/stale/day-night regression
checks, and the existing clean-build/unit/static gates.

The hard write boundary is exactly `DisplayCapability.kt` and
`DisplayProjectionTest.kt`; WeatherCapability production code, provider,
network, settings, audio, timer, forecast, resources, assets, lifecycle and
all prior task state remain forbidden. Target 1280×720 Samsung/custom-ROM
readability remains `DEFERRED` when no authorized observation exists. This
planning boundary performs no execution, emulator, adb, device, network or
credential action.

## Constraints

Use the existing modular monolith, interface, adapters, Settings projection,
cache/history owner and composition root. Do not add a third provider,
fallback/mixing, provider plugin architecture, adapter registry, service
locator, event bus, backend/shared key, live endpoint proof or forecast
presentation scope.

## Direct normative inputs

- [.memory-bank/features/FT-002-weather-cards-context.md](../../features/FT-002-weather-cards-context.md)
- [.memory-bank/contracts/weather-provider.md](../../contracts/weather-provider.md)
- [.memory-bank/contracts/weather-card-presentation.md](../../contracts/weather-card-presentation.md)
- [.memory-bank/contracts/capability-interfaces.md](../../contracts/capability-interfaces.md)
- [.memory-bank/contracts/boundary-map.md](../../contracts/boundary-map.md)
- [.memory-bank/contracts/local-secret-handling.md](../../contracts/local-secret-handling.md)
- [.memory-bank/domains/local-data.md](../../domains/local-data.md)
- [.memory-bank/testing/runtime-verification.md](../../testing/runtime-verification.md)
- [.memory-bank/workflows/tier-policy.md](../../workflows/tier-policy.md)
- [.memory-bank/tasks/TASK-020-T3-FT-002-W17.task.json](../TASK-020-T3-FT-002-W17.task.json)

## Handoff

Exact next owner: scheduler dependency recovery may re-evaluate blocked W18 and
then W19 under their preserved histories. TASK-020 remains `failed` after 3/3;
no fourth `/exe` is eligible. W20 promotion, downstream unblock and scheduler
checkpoint updates remain external to this sync.
