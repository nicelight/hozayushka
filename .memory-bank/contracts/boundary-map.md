---
description: Canonical accepted module/change-unit dependency graph and boundary contracts.
status: active
last_updated: 2026-08-10
source_of_truth: .memory-bank/architecture/system-architecture.md, .memory-bank/prd.md, operator confirmation 2026-08-04, 2026-08-06 and 2026-08-10
---
# Boundary Map

## Purpose

Keep one accepted inventory of project modules/change units and every allowed
significant dependency between them. `Consumer -> Provider` is the direction
of dependency. Observed imports or calls are evidence, not accepted edges by
themselves.

## Modules

| Module / Change Unit | Parent Architecture Unit | Code Root | Responsibility |
|---|---|---|---|
| Application Composition Root | [Single Android Application](../architecture/system-architecture.md#single-android-application) | `app/src/main/kotlin/<app-package>/app` | Settings, adapters, wiring, lifecycle, start and shutdown; no product business ownership. |
| Main Display | [Capability Slice Runtime](../architecture/system-architecture.md#capability-slice-runtime) | `app/src/main/kotlin/<app-package>/display` | Fullscreen clock/date/city shell, weather-card/personalization-preview and preset presentation, and display gestures. |
| Weather Context | [Capability Slice Runtime](../architecture/system-architecture.md#capability-slice-runtime) | `app/src/main/kotlin/<app-package>/weather` | Selected-provider dispatch, normalized weather/forecast data, provider-identified cache/history, freshness and unavailable state. |
| Forecast Sessions | [Capability Slice Runtime](../architecture/system-architecture.md#capability-slice-runtime) | `app/src/main/kotlin/<app-package>/forecast` | Hourly/long-term forecast session state, completeness gating and shared exit flow. |
| Timer & Alert | [Capability Slice Runtime](../architecture/system-architecture.md#capability-slice-runtime) | `app/src/main/kotlin/<app-package>/timer` | Preset execution, one active timer, countdown/overdue transitions and alert requests. |
| Settings & Location | [Capability Slice Runtime](../architecture/system-architecture.md#capability-slice-runtime) | `app/src/main/kotlin/<app-package>/settings` | Local provider selection, contextual OpenWeather key input, validation and offline country/city selection. |
| Open-Meteo Weather Adapter | [External Boundary Adapters](../architecture/system-architecture.md#external-boundary-adapters) | `app/src/main/kotlin/<app-package>/adapters/weather` | Open-Meteo Forecast HTTPS transport/schema decoding; no selection, normalization or cache/history ownership. |
| OpenWeather Weather Adapter | [External Boundary Adapters](../architecture/system-architecture.md#external-boundary-adapters) | `app/src/main/kotlin/<app-package>/adapters/weather` | OpenWeather One Call 3.0 HTTPS transport/schema decoding and transient `appid` query construction; no selection, normalization or cache/history ownership. |
| Android Runtime Adapter | [External Boundary Adapters](../architecture/system-architecture.md#external-boundary-adapters) | `app/src/main/kotlin/<app-package>/adapters/platform` | Device time/timezone, lifecycle/network signals, display flags and audio policy. |
| Bundled Location Catalog | [Application Data Assets](../architecture/system-architecture.md#application-data-assets) | `app/src/main/assets/geonames` | Immutable GeoNames-derived country/city records, aliases and coordinates. |

The package placeholder is non-blocking until packaging. A code root is a
discovery location, not a task hard write boundary. Local persistence adapters
remain private to the owning capability; there is no shared storage module or
shared business-write authority.

## Dependency Graph

`Consumer -> Provider` means Consumer depends on Provider through the linked
contract. The composition root wires the graph but is not a business provider;
its wiring relationship is therefore not a product dependency edge.

| Consumer | Provider | Contract |
|---|---|---|
| Main Display | Weather Context | [Main Display → Weather Context](capability-interfaces.md#main-display-to-weather-context) |
| Main Display | Timer & Alert | [Main Display → Timer & Alert](capability-interfaces.md#main-display-to-timer-and-alert) |
| Main Display | Forecast Sessions | [Main Display → Forecast Sessions](capability-interfaces.md#main-display-to-forecast-sessions) |
| Main Display | Settings & Location | [Main Display → Settings & Location](capability-interfaces.md#main-display-to-settings-and-location) |
| Main Display | Android Runtime Adapter | [Display Runtime Boundary](platform-runtime.md#display-runtime-boundary) |
| Forecast Sessions | Weather Context | [Forecast Sessions → Weather Context](capability-interfaces.md#forecast-sessions-to-weather-context) |
| Forecast Sessions | Android Runtime Adapter | [Session Timing Boundary](platform-runtime.md#session-timing-boundary) |
| Timer & Alert | Settings & Location | [Timer & Alert → Settings & Location](capability-interfaces.md#timer-and-alert-to-settings-and-location) |
| Timer & Alert | Android Runtime Adapter | [Timer and Audio Runtime Boundary](platform-runtime.md#timer-and-audio-runtime-boundary) |
| Weather Context | Settings & Location | [Weather Context → Settings & Location](capability-interfaces.md#weather-context-to-settings-and-location) |
| Settings & Location | Weather Context | [Location Refresh Orchestration](capability-interfaces.md#location-refresh-orchestration) |
| Weather Context | Open-Meteo Weather Adapter | [Weather Provider Boundary](weather-provider.md#weather-provider-boundary) |
| Weather Context | OpenWeather Weather Adapter | [Weather Provider Boundary](weather-provider.md#weather-provider-boundary) |
| Settings & Location | Bundled Location Catalog | [Settings & Location → Bundled Location Catalog](capability-interfaces.md#settings-and-location-to-bundled-location-catalog) |

An absent edge is not authorized. No edge grants a consumer direct access to a
neighbor's state, database, secret, or private adapter.

## Accepted Ownership Summary

- Main Display owns display composition, not weather/timer/settings data.
- Weather Context owns normalized weather data, cache/history, freshness and
  weather refresh orchestration. It selects exactly one fixed adapter from the
  persisted Settings provider and owns provider/location cache identity.
- Forecast Sessions owns transient forecast-screen state, not forecast data.
- Timer & Alert owns active timer transitions and the product overdue state;
  Android owns only the permission/policy for audio.
- Settings & Location owns validated Settings, selected location and the
  persisted provider selection, optional OpenWeather key and
  glass-personalization values; it may request Weather Context's refresh
  contract after a valid location or provider change. Main Display consumes
  the validated presentation projection through the existing edge above.
- Main Display composes both the production Today card and the Settings preview
  from that same presentation projection and its existing Weather Context read
  model. Settings & Location does not read Weather Context and no new edge is
  authorized for the preview.
- The bundled catalog is immutable data; it does not own the selected location.
- The two weather adapters are mutually independent leaves. Neither owns
  provider selection, product-semantic normalization, cache/history or a
  request to the other adapter.

## Pre-design Boundary Evidence

The following notes preserve earlier responsibility evidence as current-state
context. The accepted module inventory and graph above are the only target
authorization.

| Boundary | Purpose | Direction | Owner | Known Constraints | As-is / Deferred Note |
|---|---|---|---|---|---|
| User interaction -> local product state | Change accepted settings, start/cancel timer, choose location, open forecast views | inbound | Application | Single owner; valid Settings auto-save; invalid values retain the last valid value; API key stays local | Executable baseline now exists; target ownership remains defined above. |
| Application -> weather provider | Read current weather, pressure, forecast and available hourly/day-night fields from the selected target provider | outbound request / inbound response | Application integration boundary | Open-Meteo default/no-key; explicit OpenWeather/local-key; matching cache up to 24 hours; no backend, shared key, fallback or mixing | Production currently wires Yandex and provider-less cache/history; that is migration evidence, not an accepted target edge. |
| Application <-> Android OS | Obtain device time, lifecycle/network signals and permitted audio environment | bidirectional | Android OS for platform services; application for product behavior | Landscape fullscreen, keep-screen-on, temporary process-stop recovery; reboot recovery is out of scope; silent/DND rules are OS-owned | Target custom-ROM behavior needs the Foundation/device probe. |
| Bundled location data -> location selector | Provide offline country and scoped city search with coordinates and aliases | read-only | Application-owned packaged data | Country is selected first; city search is limited to that country; Google Services are excluded | Packaging/index format is a Foundation implementation detail inside this boundary. |

## Runtime Scope Hints

- Keep changes within the owning product concern; do not cross into backend,
  accounts, reboot recovery or unaccepted V2 scope.
- Never add API-key literals, secret-bearing logs/evidence, Google Services, a
  third weather endpoint/adapter, a new backend boundary or heavy realtime
  visual effects. The explicit OpenWeather outbound HTTPS `appid` query is the
  only accepted URL occurrence of the ephemeral owner key.
- Stop and route back to product clarification if a change alters actors,
  forecast horizon, timer cancellation semantics, offline freshness,
  API-key handling or V1 non-goals.

## Contract Ownership

- Public in-process capability contracts: [Capability Interfaces](capability-interfaces.md).
- External weather API: [Weather Provider](weather-provider.md).
- Android lifecycle/time/display/audio: [Platform Runtime](platform-runtime.md).
- API-key storage/redaction: [Local Secret Handling](local-secret-handling.md).
- Data ownership and persistence invariants: [Local Data](../domains/local-data.md).

## Inline Contracts

All accepted internal boundaries have independent complexity or reuse and are
defined in the registered subject contract files above. No additional inline
contract is needed in this topology document.

Each contract block defines only the applicable public surface, allowed
interaction, state/data authority, failure and compatibility rules, forbidden
bypasses, and verification. Topology remains exclusively in `Dependency Graph`.

## Update Rules
- `Module / Change Unit` is the unique graph key. Use stable functional
  responsibility names, not feature/task IDs, current paths or generic
  technical layers.
- Every graph row names registered modules and links to one exact contract
  heading. The graph row owns consumer, provider and direction.
- Include every accepted significant inter-module dependency. An absent edge
  is not authorized.
- Keep detailed module inventory here. `system-architecture.md` owns only
  larger architecture units and links to `#modules`.
- Keep as-is evidence explicitly labelled; it does not authorize a target edge.
- Plans and tasks link relevant graph/contract blocks through existing fields;
  they do not copy the graph or add graph-specific task fields.
