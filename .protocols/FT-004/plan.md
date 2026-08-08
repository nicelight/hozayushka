---
description: Planning surface for the FT-004 ten-day forecast view and shared exit flow.
status: active
last_updated: 2026-08-08
---
# FT-004 — Feature plan

## Objective

Deliver the selected-city ten-day forecast screen opened from Tomorrow or
Day-after only when the complete daily read model exists. The screen renders
today plus the next nine city-local calendar days in two rows of five, reuses
the shared card and forecast-session contracts, and returns to Main Display
with the accepted missing-data behavior when the read model is unavailable.

## Accepted basis

- Feature: [.memory-bank/features/FT-004-ten-day-forecast.md](../../.memory-bank/features/FT-004-ten-day-forecast.md)
- Epic: [.memory-bank/epics/EP-002-weather-context.md](../../.memory-bank/epics/EP-002-weather-context.md)
- Direct requirements: `REQ-010`, `REQ-022`, `REQ-026`
- Global Backbone: `complete`, Planning Revision `1`
- Foundation Gate: `TASK-002-T3-FT-000-W1`, status `done`
- Approved predecessor: `TASK-013-T3-FT-003-W5`, status `done`; it depends on
  `TASK-012-T3-FT-003-W4`, status `done`, which preserves the completed FT-003
  provider-normalization prerequisite transitively.
- Clarified PRD: `clarification_status: complete`

## Queue

| Order | Task | Tier | Wave | Status | Depends on | Primary owner |
|---|---|---|---|---|---|---|
| 1 | `TASK-006-T3-FT-004-W5` | T3 | W5 | blocked | `TASK-013-T3-FT-003-W5` | Forecast Sessions |

One task is sufficient. Daily provider normalization, the complete ten-day
read-model gate, Tomorrow/Day-after entry, the two-by-five projection and the
shared forecast exit flow form one independently observable forecast-screen
outcome. Splitting by provider, file, module or test artifact would separate
prerequisites of the same user-visible result. Forecast Sessions is the
orchestration owner; Weather Context remains the data owner and Main Display
remains the composition owner.

## Canonical SDD coverage

All applicable concerns reuse existing subject-based specs. No new canonical
spec or behavior-spec file is required.

| Concern | Action | Canonical basis | Planning reason |
|---|---|---|---|
| Architecture and module ownership | `reuse` | [System Architecture](../../.memory-bank/architecture/system-architecture.md#architecture-spine), [Data Flow and Ownership](../../.memory-bank/architecture/system-architecture.md#data-flow-and-ownership), [AD-003](../../.memory-bank/architecture/system-architecture.md#ad-003---cross-slice-orchestration-stays-in-a-capability-owner) | Forecast Sessions owns the cross-slice session outcome; the composition root only wires existing slices. |
| Module inventory and dependency graph | `reuse` | [Boundary Map modules](../../.memory-bank/contracts/boundary-map.md#modules), [Dependency Graph](../../.memory-bank/contracts/boundary-map.md#dependency-graph) | Main Display → Forecast Sessions, Forecast Sessions → Weather Context and Weather Context → Yandex Weather Adapter are already accepted edges. |
| Main Display long-term entry/session surface | `reuse` | [Main Display to Forecast Sessions](../../.memory-bank/contracts/capability-interfaces.md#main-display-to-forecast-sessions), [FT-004 Long-Term Forecast Session Surface](../../.memory-bank/contracts/capability-interfaces.md#ft-004-long-term-forecast-session-surface) | Tomorrow/Day-after supplies intent and renders the returned projection; Forecast Sessions owns acceptance/rejection and transient gestures. |
| Long-term data contract and completeness | `reuse` | [Forecast Sessions to Weather Context](../../.memory-bank/contracts/capability-interfaces.md#forecast-sessions-to-weather-context), [FT-004 Long-Term Forecast Session Surface](../../.memory-bank/contracts/capability-interfaces.md#ft-004-long-term-forecast-session-surface), [FT-004 Long-Term Mapping](../../.memory-bank/contracts/weather-provider.md#ft-004-long-term-mapping) | Existing contracts define exactly ten records, required fields, city timezone and the no-partial-data rule. |
| Provider mapping and redacted fixture | `reuse` | [Weather Provider Boundary](../../.memory-bank/contracts/weather-provider.md#weather-provider-boundary), [FT-004 Long-Term Mapping](../../.memory-bank/contracts/weather-provider.md#ft-004-long-term-mapping), [Credential and Evidence Rules](../../.memory-bank/contracts/weather-provider.md#credential-and-evidence-rules) | FT-004 fills the accepted daily field mapping without changing the Yandex boundary, horizon or secret rules. |
| Shared weather-card presentation | `reuse` | [Display-ready card contract](../../.memory-bank/contracts/weather-card-presentation.md#display-ready-card-contract), [Temperature and Glass Rules](../../.memory-bank/contracts/weather-card-presentation.md#temperature-and-glass-rules) | Long-term cards reuse the accepted material, temperature background and illustration rules and omit pressure arrows. |
| Long-term records and ownership | `reuse` | [FT-004 Long-Term Forecast Records](../../.memory-bank/domains/local-data.md#ft-004-long-term-forecast-records), [Validation and Serialization Boundaries](../../.memory-bank/domains/local-data.md#validation-and-serialization-boundaries) | Weather Context owns normalized daily data; Forecast Sessions owns only transient session state. |
| Session lifecycle and timing | `reuse` | [Forecast Screen Session](../../.memory-bank/states/lifecycle-map.md#forecast-screen-session), [Shared Forecast Session Contract](../../.memory-bank/states/lifecycle-map.md#shared-forecast-session-contract), [FT-004 Long-Term Session Contract](../../.memory-bank/states/lifecycle-map.md#ft-004-long-term-session-contract) | The existing lifecycle fixes the three-second timer, hint, double-tap and hold/release behavior for both forecast types. |
| Android timing boundary | `reuse` | [Session Timing Boundary](../../.memory-bank/contracts/platform-runtime.md#session-timing-boundary), [Platform Compatibility and Failure Rules](../../.memory-bank/contracts/platform-runtime.md#compatibility-and-failure-rules) | Platform supplies timing; it does not become forecast state or authorize reboot recovery. |
| Verification and redacted fixtures | `reuse` | [Deterministic Host-Side Checks](../../.memory-bank/testing/runtime-verification.md#deterministic-host-side-checks), [Redacted Integration Fixtures](../../.memory-bank/testing/runtime-verification.md#redacted-integration-fixtures), [Target-Device Evidence](../../.memory-bank/testing/runtime-verification.md#target-device-evidence) | Host checks prove mapping/order/timezone/gestures; target evidence is used only where host proof cannot establish the display/runtime result. |

## Scope boundary

In scope: redacted daily provider mapping, selected-city API timezone handling,
the exact ten-record complete read model, Tomorrow/Day-after entry gating,
two rows of five daily cards with `dd`, shared card presentation without
pressure arrows, the shared three-second/single-tap/double-tap/hold-release
exit flow, and the exact missing-data message.

Out of scope: FT-001 clock/date/fullscreen behavior, FT-002 current/daily card
freshness/history/pressure ownership, FT-003 hourly forecast content (the
shared exit contract is consumed), timer/preset/alert behavior, Settings/
location/API-key input, a new provider or backend, event infrastructure,
reboot recovery, live credentials and any new product control.

## Primary owner, boundaries and execution path

- Primary owner: `Forecast Sessions`, code root
  `app/src/main/kotlin/com/hozayushka/app/forecast`.
- Cross-slice edges: Main Display → Forecast Sessions for Tomorrow/Day-after
  intent and returned projection rendering; Forecast Sessions → Weather
  Context for the complete daily read model; Weather Context → Yandex Weather
  Adapter for provider normalization.
- Main Display renders the session and does not read Weather Context storage or
  raw provider fields. Forecast Sessions does not write Weather Context state.
- The bounded path is redacted daily fixture/provider mapping → Weather
  Context normalized ten-day projection → Forecast Sessions completeness and
  session state → Main Display rendering. No new module, graph edge or
  event/message boundary is selected. Composition-root changes, if required,
  are wiring-only.

## Expected advisory change surface

- `app/src/main/kotlin/com/hozayushka/app/forecast/` — long-term session state,
  completeness gate, two-by-five projection and shared exit flow reuse.
- `app/src/main/kotlin/com/hozayushka/app/weather/` — normalized daily
  projection and availability read model behind Weather Context ownership.
- `app/src/main/kotlin/com/hozayushka/app/adapters/weather/` — redacted daily
  provider DTO/mapping behind the existing provider boundary.
- `app/src/main/kotlin/com/hozayushka/app/display/` — Tomorrow/Day-after entry
  and session rendering through Forecast Sessions; no forecast-state writes.
- `app/src/main/kotlin/com/hozayushka/app/adapters/platform/` — only the
  existing timing seam if the scaffold needs the accepted platform source.
- `app/src/main/kotlin/com/hozayushka/app/app/` — wiring only if required.
- `app/src/main/res/` — accepted Russian fallback/hint strings and static card
  resources when required by the existing scaffold.
- `app/src/test/kotlin/com/hozayushka/app/` and
  `app/src/test/resources/fixtures/` — deterministic redacted mapping,
  completeness, timezone, projection and shared-session checks.

These paths are advisory and non-exhaustive. No hard `write_boundary` is set;
semantic scope, forbidden scope and stop conditions remain binding.

## Applicable gates and claim-linked proof

- `./gradlew clean assembleDebug` — clean Android build.
- `./gradlew testDebugUnitTest` — deterministic daily mapping, completeness,
  city-timezone, owner-local save/reload persistence, projection and
  shared-session checks.
- The target-device route in Runtime Verification is applicable only to
  residual forecast-card readability/static-material or interaction-timing
  outcomes that host checks cannot establish. This planning run creates no
  runtime evidence.

| Claim | Decisive result | Artifact |
|---|---|---|
| `FT-004-AC-001 / REQ-010, REQ-026` | A successful normalized ten-day result survives an isolated Weather Context save/reload with an identical complete read model; Tomorrow and Day-after create the same long-term session only for that saved model, while unavailable data leaves Main Display visible. | Isolated save/reload/entry result with reset and cleanup outcome |
| `FT-004-AC-002 / REQ-010, REQ-022` | Exactly today plus the next nine city-local calendar days render in two rows of five, in order and with the selected-city timezone. | Redacted fixture projection output |
| `FT-004-AC-003 / REQ-010, REQ-022, REQ-026` | Each daily card shows `dd`, temperature background, temperature and illustration, uses selected-city day/night selection and omits pressure arrows. | Host presentation/timezone result plus scoped device observation where needed |
| `FT-004-AC-004 / REQ-010` | Three-second auto-close, single-tap hint/cancel, double-tap close and hold/release close each match the shared transition. | Deterministic session timing/gesture result |
| `FT-004-AC-005 / REQ-010, REQ-026` | Missing/incomplete required daily data creates no session and shows `Долгосрочный прогноз еще не подгрузился`. | Deterministic rejection/fallback result |

## Constraints and invariants

- Weather Context owns provider normalization, daily data, availability and
  any cache writes; Forecast Sessions owns only transient long-term session
  state and gestures; consumers do not bypass private storage.
- Selected-city API timezone controls daily dates, day boundaries and
  day/night selection; device timezone remains the Main Display clock/date
  source.
- Required daily data is all-or-nothing for session creation. No partial
  sequence, invented day or fabricated field may be rendered.
- Long-term cards reuse the accepted static pseudo-glass and illustration
  rules and omit pressure arrows; no second material or trend rule is added.
- Main Display and the composition root do not own Forecast Sessions business
  orchestration. No new edge, dependency, event/message boundary or provider
  is authorized.
- No live API key, secret-bearing fixture/evidence, backend, Google Services,
  reboot recovery or unaccepted UI scope.

## Direct normative inputs

- [.memory-bank/features/FT-004-ten-day-forecast.md](../../.memory-bank/features/FT-004-ten-day-forecast.md)
- [.memory-bank/epics/EP-002-weather-context.md](../../.memory-bank/epics/EP-002-weather-context.md)
- [.memory-bank/requirements.md](../../.memory-bank/requirements.md)
- [.memory-bank/prd.md](../../.memory-bank/prd.md)
- [.memory-bank/invariants.md](../../.memory-bank/invariants.md)
- [.memory-bank/glossary.md](../../.memory-bank/glossary.md)
- [.memory-bank/architecture/system-architecture.md](../../.memory-bank/architecture/system-architecture.md)
- [.memory-bank/contracts/boundary-map.md](../../.memory-bank/contracts/boundary-map.md)
- [.memory-bank/contracts/capability-interfaces.md](../../.memory-bank/contracts/capability-interfaces.md)
- [.memory-bank/contracts/weather-provider.md](../../.memory-bank/contracts/weather-provider.md)
- [.memory-bank/contracts/weather-card-presentation.md](../../.memory-bank/contracts/weather-card-presentation.md)
- [.memory-bank/domains/local-data.md](../../.memory-bank/domains/local-data.md)
- [.memory-bank/states/lifecycle-map.md](../../.memory-bank/states/lifecycle-map.md)
- [.memory-bank/contracts/platform-runtime.md](../../.memory-bank/contracts/platform-runtime.md)
- [.memory-bank/testing/runtime-verification.md](../../.memory-bank/testing/runtime-verification.md)
- [.memory-bank/workflows/tier-policy.md](../../.memory-bank/workflows/tier-policy.md)

## Handoff

After this planning surface is accepted, the immediate route is
`/review-tasks-plan FT-004`; execution, `/mb-doctor`, `/verify`,
`/red-verify` and `/mb-sync` are not part of this planning run.
