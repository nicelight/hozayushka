---
description: Planning surface for FT-003 hourly forecast view and shared exit flow.
status: active
last_updated: 2026-08-08
---
# FT-003 — Feature plan

## Objective

Deliver the accepted hourly forecast outcome: Today opens a complete
eight-slot city-timezone projection, renders two rows of four cards using the
shared weather-card material, and uses the common forecast exit flow without
creating a partial or fabricated session.

## Accepted basis

- Feature: [.memory-bank/features/FT-003-hourly-forecast.md](../../.memory-bank/features/FT-003-hourly-forecast.md)
- Epic: [.memory-bank/epics/EP-002-weather-context.md](../../.memory-bank/epics/EP-002-weather-context.md)
- Direct requirements: `REQ-009`, `REQ-022`, `REQ-026`
- Global Backbone: `complete`, Planning Revision `1`
- Foundation Gate: `TASK-002-T3-FT-000-W1`, status `done`
- Approved predecessor: `TASK-004-T3-FT-002-W3`, status `done`
- Clarified PRD: `clarification_status: complete`

## Queue

| Order | Task | Tier | Wave | Status | Depends on | Primary owner |
|---|---|---|---|---|---|---|
| 1 | `TASK-005-T3-FT-003-W4` | T3 | W4 | failed | `TASK-004-T3-FT-002-W3` | Forecast Sessions (historical outcome) |
| 2 | `TASK-012-T3-FT-003-W4` | T3 | W4 | done | `TASK-004-T3-FT-002-W3` | Weather Context repair |
| 3 | `TASK-013-T3-FT-003-W5` | T3 | W5 | done | `TASK-012-T3-FT-003-W4` | Forecast Sessions / Main Display integration |

The original cohesive forecast-view outcome remains recorded by TASK-005;
TASK-012 is a single focused follow-up for the evidenced provider-shape defect.
It reuses the completed TASK-004 Weather Context baseline and does not alter
TASK-005's failed lifecycle or evidence. Splitting the repair by provider,
normalizer, fixture or test artifact would separate one independently
verifiable data-boundary outcome. Forecast Sessions remains the orchestration
owner for the feature; Weather Context owns this repair and Main Display
remains the composition owner.

The follow-up intentionally depends on TASK-004 rather than directly on the
failed TASK-005 record: its prerequisite is the accepted Weather Context
baseline, while TASK-005 is retained as historical failure evidence. The
authoritative W4 closure records TASK-012 as `done` with functional `PASS` and
semantic `semantic-pass`; target evidence is deferred/non-blocking with no
runtime PASS claim. The existing FT-004+ dependency chain and scheduler state
are outside this reconciliation and remain unchanged.

TASK-013 is the smallest remaining FT-003 follow-up after that repair. It owns
the Forecast Sessions/Main Display integration outcome: Today entry and the
user-facing completeness/fallback path, plus the shared session timing and
gestures. It also carries only the minimum regression proof that the existing
TASK-012 eight-slot projection reaches the accepted two-by-four card surface
through the registered boundaries; provider normalization and selected-field
validation remain TASK-012-owned and are not reimplemented or re-owned here.
The task was W5 because it depended on the W4 repair. The authoritative W5
closure records it as `done` after functional `PASS` and semantic
`semantic-pass`; target evidence is deferred/non-blocking with no runtime
`PASS` claim. No new module, contract, spec or behavior example was needed.
Feature/epic/REQ lifecycle, promotion, dependency block/unblock, checkpoint and
terminal-state decisions remain outside this reconciliation.

## Canonical SDD coverage

All applicable concerns reuse existing subject-based specs. No new canonical
spec or behavior-spec file is required.

| Concern | Action | Canonical basis | Planning reason |
|---|---|---|---|
| Architecture and module ownership | `reuse` | [System Architecture](../../.memory-bank/architecture/system-architecture.md#architecture-spine), [Data Flow and Ownership](../../.memory-bank/architecture/system-architecture.md#data-flow-and-ownership), [AD-003](../../.memory-bank/architecture/system-architecture.md#ad-003-cross-slice-orchestration-stays-in-a-capability-owner) | Forecast Sessions owns the cross-slice session outcome; the composition root only wires existing slices. |
| Module inventory and dependency graph | `reuse` | [Boundary Map modules](../../.memory-bank/contracts/boundary-map.md#modules), [Dependency Graph](../../.memory-bank/contracts/boundary-map.md#dependency-graph) | Main Display → Forecast Sessions and Forecast Sessions → Weather Context are already accepted edges. |
| Main Display hourly entry/session surface | `reuse` | [Main Display to Forecast Sessions](../../.memory-bank/contracts/capability-interfaces.md#main-display-to-forecast-sessions), [FT-003 Hourly Forecast Session Surface](../../.memory-bank/contracts/capability-interfaces.md#ft-003-hourly-forecast-session-surface) | Today supplies intent and renders the returned projection; Forecast Sessions owns acceptance/rejection and transient gestures. |
| Hourly data contract and completeness | `reuse` | [Forecast Sessions to Weather Context](../../.memory-bank/contracts/capability-interfaces.md#forecast-sessions-to-weather-context), [FT-003 Forecast Data Contract](../../.memory-bank/contracts/capability-interfaces.md#ft-003-forecast-data-contract), [FT-003 Hourly Mapping](../../.memory-bank/contracts/weather-provider.md#ft-003-hourly-mapping) | Existing contracts define the eight slots, required fields, timezone and no-partial-data rule. |
| Shared weather-card presentation | `reuse` | [Display-ready card contract](../../.memory-bank/contracts/weather-card-presentation.md#display-ready-card-contract), [Temperature and glass rules](../../.memory-bank/contracts/weather-card-presentation.md#temperature-and-glass-rules) | Hourly cards reuse the accepted material and illustration rules and do not create a second pressure/material rule. |
| Hourly records and ownership | `reuse` | [FT-003 Hourly Forecast Records](../../.memory-bank/domains/local-data.md#ft-003-hourly-forecast-records), [Validation and Serialization Boundaries](../../.memory-bank/domains/local-data.md#validation-and-serialization-boundaries) | Weather Context owns normalized data; Forecast Sessions owns only transient session state. |
| Session lifecycle and timing | `reuse` | [Forecast Screen Session](../../.memory-bank/states/lifecycle-map.md#forecast-screen-session), [Shared Forecast Session Contract](../../.memory-bank/states/lifecycle-map.md#shared-forecast-session-contract), [FT-003 Hourly Session Contract](../../.memory-bank/states/lifecycle-map.md#ft-003-hourly-session-contract) | Existing lifecycle fixes the three-second timer, hint, double-tap and hold/release behavior. |
| Android timing boundary | `reuse` | [Session Timing Boundary](../../.memory-bank/contracts/platform-runtime.md#session-timing-boundary), [Platform Compatibility and Failure Rules](../../.memory-bank/contracts/platform-runtime.md#compatibility-and-failure-rules) | Platform supplies timing; it does not become forecast state or authorize reboot recovery. |
| Verification and redacted fixtures | `reuse` | [Deterministic Host-Side Checks](../../.memory-bank/testing/runtime-verification.md#deterministic-host-side-checks), [Redacted Integration Fixtures](../../.memory-bank/testing/runtime-verification.md#redacted-integration-fixtures), [Target-Device Evidence](../../.memory-bank/testing/runtime-verification.md#target-device-evidence) | Host checks prove mapping/order/gestures; target evidence is used only where host proof cannot establish the display/runtime result. |

## Scope boundary

In scope: hourly provider-field normalization behind the existing Weather
Provider boundary, selected-city timezone handling, the exact eight-slot
complete read model, Today entry gating, two-by-four card composition, shared
card presentation without pressure arrows, the three-second/single-tap/
double-tap/hold-release exit flow, and the exact missing-data message.

Out of scope: FT-001 clock/date/fullscreen behavior, FT-002 current/daily card
and freshness/history ownership, FT-004 ten-day forecast, timer/preset/alert
behavior, Settings/location/API-key input, a new provider or backend, event
infrastructure, reboot recovery, live credentials, and any new product control.

## Primary owner, boundaries and execution path

- Primary owner: `Forecast Sessions`, code root
  `app/src/main/kotlin/<app-package>/forecast`.
- Cross-slice edges: Main Display → Forecast Sessions for Today intent and
  projection rendering; Forecast Sessions → Weather Context for the complete
  hourly read model. Weather Context → Yandex Weather Adapter remains the
  provider boundary for normalization.
- Main Display renders the session and does not read Weather Context storage or
  raw provider fields. Forecast Sessions does not write Weather Context state.
- The current scaffold contains `ForecastSessionCapability`, the Weather
  capability seam and a redacted provider fixture path. The bounded path is
  fixture/provider mapping → Weather Context normalized hourly projection →
  Forecast Sessions completeness/session state → Main Display rendering. No
  new module, graph edge or event/message boundary is selected.
- Composition-root changes, if required, are wiring-only. Business
  orchestration remains in Forecast Sessions.

## Repair follow-up

TASK-012 repairs only the Weather Context/Yandex Weather Adapter normalization
path exposed by the TASK-005 semantic failure. A supported full-day response
may contain 48 hourly records across two city-local days; normalization must
select the existing eight accepted slots at the selected-city boundary and
must still reject the result when any selected slot lacks required fields.
The public eight-slot projection, all-or-nothing rule, registered graph edges,
feature acceptance criteria, tier, wave and Planning Revision remain
unchanged. Fresh `/exe` → `/verify` → `/red-verify` evidence is required after
the task-plan review; target-device evidence remains deferred/non-blocking.

## Expected advisory change surface

- `app/src/main/kotlin/com/hozayushka/app/forecast/` — hourly session state,
  completeness gate, projection and shared exit gestures.
- `app/src/main/kotlin/com/hozayushka/app/weather/` — normalized hourly
  projection and availability read model behind Weather Context ownership.
- `app/src/main/kotlin/com/hozayushka/app/adapters/weather/` — redacted hourly
  provider DTO/mapping behind the existing provider boundary.
- `app/src/main/kotlin/com/hozayushka/app/display/` — Today entry and session
  rendering through the Forecast Sessions contract; no forecast-state writes.
- `app/src/main/kotlin/com/hozayushka/app/adapters/platform/` — only the
  existing timing seam if the scaffold needs the accepted platform source.
- `app/src/main/kotlin/com/hozayushka/app/app/` — wiring only if required.
- `app/src/main/res/` — accepted Russian fallback/hint strings and static card
  resources when required by the existing scaffold.
- `app/src/test/kotlin/com/hozayushka/app/` and
  `app/src/test/resources/fixtures/` — deterministic redacted mapping,
  completeness, timezone, session and gesture checks.

These paths are advisory and non-exhaustive. No hard `write_boundary` is set;
semantic scope, forbidden scope and stop conditions remain binding.

## Applicable gates and claim-linked proof

- `./gradlew clean assembleDebug` — clean Android build.
- `./gradlew testDebugUnitTest` — deterministic hourly mapping, timezone,
  completeness, projection and shared-session checks.
- The target-device route in Runtime Verification is applicable only to
  residual forecast-card readability/static-material or interaction timing
  outcomes that host checks cannot establish. This planning run creates no
  runtime evidence.

| Claim | Decisive result | Artifact |
|---|---|---|
| `FT-003-AC-001 / REQ-009` | Today creates a session only for a complete required hourly read model; unavailable data leaves Main Display visible with the accepted message. | Deterministic entry/completeness result |
| `FT-003-AC-002 / REQ-009` | Exactly `06:00`, `09:00`, `12:00`, `15:00`, `18:00`, `21:00`, `00:00`, `03:00` appear in two rows of four; the final two are on the following city-local day. | Redacted fixture projection output |
| `FT-003-AC-003 / REQ-009, REQ-022` | Hourly cards use the shared temperature background, glass and illustration inputs, show slot time instead of date, omit pressure arrows and use the selected-city timezone. | Host presentation/timezone result plus scoped device observation where needed |
| `FT-003-AC-004 / REQ-009` | Three-second auto-close, single-tap hint/cancel, double-tap close and hold/release close each match the accepted transition. | Deterministic session gesture/timing result |
| `FT-003-AC-005 / REQ-009, REQ-026` | Missing/incomplete required data does not create a session or invented slot and returns `Почасовой прогноз еще не подгрузился`. | Deterministic rejection/fallback result |

## Constraints and invariants

- Weather Context owns provider normalization, hourly data, availability and
  any cache writes; Forecast Sessions owns only transient session state and
  gestures; consumers do not bypass private storage.
- Selected-city API timezone controls hourly labels and day boundaries;
  device timezone remains the Main Display clock/date source.
- Required hourly data is all-or-nothing for session creation. No missing slot,
  partial sequence or invented field may be rendered.
- Hourly cards reuse the accepted static pseudo-glass and illustration rules;
  pressure arrows are absent and no second material rule is introduced.
- Main Display and the composition root do not own Forecast Sessions business
  orchestration. No new edge, dependency, event/message boundary or provider
  is authorized.
- No live API key, secret-bearing fixture/evidence, backend, Google Services,
  reboot recovery or unaccepted UI scope.

## Direct normative inputs

- [.memory-bank/features/FT-003-hourly-forecast.md](../../.memory-bank/features/FT-003-hourly-forecast.md)
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
`/review-tasks-plan FT-003`; execution, `/mb-doctor`, `/verify`,
`/red-verify` and `/mb-sync` are not part of this planning run.
