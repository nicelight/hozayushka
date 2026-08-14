---
description: Accepted global architecture shape and Architecture Spine for the V1 Android application.
status: active
last_updated: 2026-08-10
source_of_truth: .memory-bank/constitution.md, .memory-bank/prd.md, operator confirmation 2026-08-04, 2026-08-06 and 2026-08-10
---
# System Architecture

## System Goal

Deliver one manually installed Kotlin Android application for the fixed
landscape kitchen display. The application keeps the clock and timer path
usable without a network, presents weather from the user's local provider
configuration, and keeps all product state inside the application boundary.

## Target Architecture

The accepted V1 target is one deployable modular monolith. Its primary change
units are capability slices, not controllers, services, repositories, or
other technical layers. All slices are composed into one Android runtime and
share only the narrow contracts registered in
[Boundary Map](../contracts/boundary-map.md).

The accepted capability slices are:

| Capability slice | Planned project-relative code root | Observable responsibility |
|---|---|---|
| Main Display | `app/src/main/kotlin/<app-package>/display` | Fullscreen clock/date/city shell, weather-card/personalization-preview and preset presentation, and display gestures. |
| Weather Context | `app/src/main/kotlin/<app-package>/weather` | Selected-provider dispatch, normalized weather/forecast data, provider-identified local cache/history, freshness and unavailable state. |
| Forecast Sessions | `app/src/main/kotlin/<app-package>/forecast` | Hourly and long-term forecast sessions, availability checks and the shared exit gesture flow. |
| Timer & Alert | `app/src/main/kotlin/<app-package>/timer` | Preset execution, one active timer, countdown/overdue transitions and alert requests. |
| Settings & Location | `app/src/main/kotlin/<app-package>/settings` | Local provider selection, contextual OpenWeather key input, offline country/city selection and validation. |

`<app-package>` is a planned package placeholder. The application name and
package identity are non-blocking packaging decisions; the code roots above
are discovery locations, not task hard write boundaries.

## Main Architecture Units

### Single Android Application

One manually installed Android deployment and one composition root. The
application owns product behavior and coordinates the capability runtime.

### Capability Slice Runtime

The five user-observable capability slices listed above are the primary
functional change units. They use only registered public contracts.

### External Boundary Adapters

Exactly two weather adapters—Open-Meteo and OpenWeather—and the Android OS
adapter sit at explicit boundaries. Weather adapters share one provider-neutral
contract but use separate accepted HTTPS endpoints; adapters do not become
owners of selection, normalized product state, cache/history or product
workflows.

### Application Data Assets

The bundled GeoNames-derived catalog is immutable application data read by the
Settings & Location capability; it is not a second network or service boundary.

## Architecture Spine

#### AD-001 - One deployable capability-sliced application
- Binds: V1 runtime composition, deployment shape and primary change units.
- Prevents: backend services, multi-process product decomposition, event-bus machinery and technical-layer slices.
- Rule: Compose the five capability slices into one deployable Android application with one composition root.
- Verification: Foundation Gate records a boundary review against the module inventory; no automated architecture validator is claimed.
- Source: Operator confirmation on 2026-08-04; [Constitution](../constitution.md); [PRD](../prd.md).

#### AD-002 - Application-owned local state is the product source of truth
- Binds: Settings, selected location, active timer data, weather cache/history and presentation state ownership.
- Prevents: duplicate business writes, direct cross-slice storage access and treating a remote response or OS policy as durable product state.
- Rule: Each capability owns its mutable state and exposes only its public contract. The selected external weather provider owns fetched remote values, Android OS owns platform signals/policy, and the application owns selection, normalized state and product behavior. Weather Context is the single normalized weather/cache/history write owner.
- Verification: The owner/write-authority table and dependency graph in [Boundary Map](../contracts/boundary-map.md) are the planning check; data probes are routed through [Local Data](../domains/local-data.md).
- Source: Operator confirmation on 2026-08-04; [PRD Data / Domain Model](../prd.md); [Invariants](../invariants.md).

#### AD-003 - Cross-slice orchestration stays in a capability owner
- Binds: Use cases that cross Main Display, Weather Context, Forecast Sessions, Timer & Alert and Settings & Location.
- Prevents: business orchestration in a screen/handler, generic helper or composition root, and neighbor state writes.
- Rule: A cross-slice use case names one owning capability and reaches a neighbor only through its registered public contract. The composition root is limited to settings, adapters, wiring, lifecycle, start and shutdown.
- Verification: Every accepted edge resolves to one contract heading in [Boundary Map](../contracts/boundary-map.md); feature planning must preserve the same owner.
- Source: Operator confirmation on 2026-08-04; [Constitution](../constitution.md); [Capability Interfaces](../contracts/capability-interfaces.md).

#### AD-004 - No event or message boundary in V1
- Binds: In-process communication between the capability slices.
- Prevents: an event bus, mediator or asynchronous message schema added without a current product boundary.
- Rule: Use direct in-process capability contracts for the accepted single-runtime flows. The Open-Meteo/OpenWeather REST calls and Android OS adapter remain API/platform boundaries, not an internal message system.
- Verification: `event_message_contracts` is explicitly `not_applicable` in the backbone matrix and the accepted graph contains no event/message edge.
- Source: Operator confirmation on 2026-08-04; [PRD Non-goals and Integrations](../prd.md).

#### AD-005 - Keep device and city time domains separate
- Binds: Main clock/date, weather card dates, day/night selection and hourly slot labels.
- Prevents: using the device timezone for city weather boundaries or shifting the main clock with provider timezone data.
- Rule: Device time/timezone drives the main display. Selected-city API timezone drives weather dates, forecast day boundaries, day/night selection and hourly labels.
- Verification: Deterministic timezone fixtures are required by [Runtime Verification](../testing/runtime-verification.md); the boundary is also recorded in [Local Data](../domains/local-data.md).
- Source: `PRD-FR-039`, `PRD-AC-009` in [PRD](../prd.md).

#### AD-006 - OpenWeather owner key is local and redacted
- Binds: OpenWeather key input, secret persistence, outbound request construction, logs, APK contents and verification evidence.
- Prevents: embedded/shared keys, reuse on Open-Meteo, source literals, persisted non-secret copies, secret-bearing logs, screenshots or fixtures.
- Rule: Accept the key only from the user for explicit OpenWeather selection, keep it inside the local secret boundary, and expose it only ephemerally to the outbound HTTPS One Call 3.0 `appid` query construction. Redact the key and request URL before every diagnostic, persistence or evidence boundary. Open-Meteo receives no credential.
- Verification: The redacted-fixture and artifact-scan route is defined in [Local Secret Handling](../contracts/local-secret-handling.md) and [Runtime Verification](../testing/runtime-verification.md).
- Source: [Constitution Product Non-negotiables](../constitution.md); `PRD-FR-033` and `PRD-AC-006` in [PRD](../prd.md).

#### AD-007 - One personalization projection serves Today and Settings preview
- Binds: Glass-intensity presentation, Settings preview temperature input and the Main Display ↔ Settings & Location boundary.
- Prevents: A Settings → Weather Context dependency, private weather-storage reads, and visual divergence between the production Today card and its Settings preview.
- Rule: Settings & Location owns persisted and validated alert/glass personalization. Main Display consumes that projection through the existing Main Display → Settings & Location contract, combines it with its existing Weather Context read (or the accepted `24 °C` fallback), and renders both surfaces with the shared Weather Card Presentation rules.
- Verification: A deterministic presentation probe compares production Today and Settings preview under the same saved projection; boundary review confirms no new Settings → Weather Context edge.
- Source: Operator confirmation on 2026-08-06; [Boundary Map](../contracts/boundary-map.md); [Capability Interfaces](../contracts/capability-interfaces.md); [Weather Card Presentation](../contracts/weather-card-presentation.md).

#### AD-008 - Selected-provider isolation is owned by Weather Context
- Binds: Settings provider selection, adapter dispatch, provider capabilities, cache/history identity and provider-failure recovery.
- Prevents: automatic failover, hidden selection changes, cross-provider cache/history/forecast mixing and adapter-owned product state.
- Rule: Settings & Location persists `open_meteo|open_weather` with Open-Meteo as default. On each refresh Weather Context invokes exactly the selected one of the two fixed adapters, normalizes its response, and reads/writes only cache/history matching provider plus location. Open-Meteo projects 10 daily records; OpenWeather projects 8 records plus two explicit unavailable positions. Both hourly paths require all eight fixed city-local slots.
- Verification: Deterministic two-provider fixtures prove one-adapter dispatch, provider/location cache and history isolation, failure without a second request, 10-versus-8+2 daily projection and strict eight-slot hourly rejection through [Runtime Verification](../testing/runtime-verification.md).
- Source: Operator decision on 2026-08-10; `PRD-FR-013`, `PRD-FR-019`–`PRD-FR-022`, `PRD-FR-032`–`PRD-FR-037`; [Weather Provider](../contracts/weather-provider.md).

## Runtime Composition

The single composition root is planned at
`app/src/main/kotlin/<app-package>/app`. It creates settings, platform and
provider adapters, wires the five capability slices, owns application start
and shutdown, and translates lifecycle signals into the relevant public
contracts. It does not own product rules or mutate slice state directly.

The runtime has these external boundaries:

- [Weather Provider](../contracts/weather-provider.md) for the exact
  Open-Meteo Forecast and OpenWeather One Call 3.0 adapters/endpoints,
  provider-neutral response contract and selected-provider failure rules.
- [Android platform runtime contract](../contracts/platform-runtime.md) for
  device time, lifecycle/network signals, display flags and audio policy.
- Bundled GeoNames location assets read through the catalog contract in
  [Capability Interfaces](../contracts/capability-interfaces.md).

## Data Flow and Ownership

1. Main Display reads display-ready weather, timer and validated Settings
   presentation projections through public contracts; it never reads another
   slice's storage. The production Today card and the Settings preview use the
   same saved glass-intensity projection and the same Weather Card Presentation
   rules. Main Display supplies Today temperature from its existing Weather
   Context read model, or the accepted `24 °C` fallback; this presentation
   composition does not authorize a Settings & Location → Weather Context edge.
2. Settings & Location validates and persists user settings, selected provider,
   optional OpenWeather key and location. A valid city or provider change asks
   Weather Context to refresh; it does not choose an adapter for Weather
   Context or write weather state.
3. Weather Context reads the validated selection/context, requests exactly one
   matching adapter, normalizes the provider envelope and writes only the
   provider/location-matching cache/history partition. It evaluates freshness
   and provider-specific forecast completeness, then exposes stable
   provider-neutral read models to Main Display and Forecast Sessions.
4. Timer & Alert reads timer preferences, owns timer arithmetic and lifecycle
   state, and requests permitted audio through the Android boundary. Its
   visual overdue state does not depend on audio permission.
5. Forecast Sessions asks Weather Context for complete hourly or long-term
   data, owns the transient session/exit state, and never writes weather data.
6. Android lifecycle/network/time signals enter through the platform boundary;
   they do not become a second product source of truth.

The durable data contract and write ownership are defined in
[Local Data](../domains/local-data.md). Accepted consumer/provider edges and
their exact contracts are defined only in [Boundary Map](../contracts/boundary-map.md).

## Deployment and Current-State Evidence

- V1 is a single manually installed APK for the Samsung GT-I9300I (`s3ve3gds`)
  on a compatible Android 11 custom ROM; publication/distribution is out of
  scope.
- The application runs landscape fullscreen, hides system panels, keeps the
  screen on while open and gives clock readability priority over visual effects.
- The current workspace contains the executable Android production source and
  Gradle application baseline established by `TASK-001-T3-FT-000-W0` and
  accepted by the closed host-only Foundation Gate
  `TASK-002-T3-FT-000-W1`. The observed surface includes the `app` module,
  `app/src` production and test roots, the composition root, capability and
  adapter roots, and the deterministic fixture path. Target-device
  compatibility remains deferred as recorded by the Foundation evidence.
- The observed production weather path still wires a credential-required
  Yandex adapter and provider-less cache/history. This is migration evidence
  only: it cannot authorize a third target adapter, automatic fallback or
  relabelling legacy state as Open-Meteo/OpenWeather.

## Deferred Design Routes

These are implementation-level routes, not unresolved global target decisions:

| Route | Owner | Boundary that must remain unchanged |
|---|---|---|
| Establish Gradle/project package, UI toolkit and minimal executable entry | `/foundation-to-tasks` | One deployable app and composition-root rule. |
| Select the project-native local persistence primitive and its first schema probe | `/foundation-to-tasks` | Capability write ownership and local-data invariants. |
| Implement provider-specific serializers and deterministic fixture examples | `/feature-to-tasks` for FT-002–FT-004/FT-008 | The two fixed endpoints, Weather Context normalization ownership, timezone/unit mapping, provider capabilities, cache identity and no-fallback rules remain unchanged. |
| Probe temporary process stop, screen-off and audio behavior on the target ROM | Foundation/runtime verification | No reboot recovery requirement; visual overdue state remains authoritative. |
| Choose final application name/package before packaging | Packaging implementation | Planned code-root convention only. |

## Canonical Routes

- Module inventory and dependency graph: [Boundary Map](../contracts/boundary-map.md).
- Internal capability contracts: [Capability Interfaces](../contracts/capability-interfaces.md).
- Provider contract: [Weather Provider](../contracts/weather-provider.md).
- Platform contract: [Platform Runtime](../contracts/platform-runtime.md).
- Data ownership and persistence invariants: [Local Data](../domains/local-data.md).
- Lifecycle states: [Lifecycle Map](../states/lifecycle-map.md).
- Concrete proof/evidence routes: [Runtime Verification](../testing/runtime-verification.md).
