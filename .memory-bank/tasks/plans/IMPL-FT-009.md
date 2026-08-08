---
description: Implementation plan for FT-009 alert and glass personalization settings.
status: active
last_updated: 2026-08-07
---
# FT-009 — Feature plan

## Objective

Create one independently verifiable `Settings & Location` outcome: validated
alert sound and app-volume preferences, glass-intensity persistence and a live
preview that uses the production Today weather-card presentation.

## Accepted basis

- Feature: [.memory-bank/features/FT-009-personalization-settings.md](../../features/FT-009-personalization-settings.md)
- Epic: [.memory-bank/epics/EP-004-settings-location.md](../../epics/EP-004-settings-location.md)
- Direct requirements: `REQ-019`, `REQ-020`, `REQ-021`
- Feature acceptance: `FT-009-AC-001`
- Global Backbone: `complete`, Planning Revision `1`
- Foundation Gate: `TASK-002-T3-FT-000-W1`, status `done`
- Approved sequential predecessor: `TASK-010-T3-FT-008-W9`, status `planned`
- Clarified PRD: `clarification_status: complete`

## Queue

| Order | Task | Tier | Wave | Status | Depends on | Primary owner |
|---|---|---|---|---|---|---|
| 1 | `TASK-011-T3-FT-009-W10` | T3 | W10 | planned | `TASK-010-T3-FT-008-W9` | Settings & Location |

One task is sufficient. Alert sound/volume validation, glass persistence,
preview composition, invalid-value preservation and consumer projection form
one user-facing Settings outcome with one mutable owner and one claim-linked
proof path. The task crosses only registered Main Display → Settings &
Location, Main Display → Weather Context and Timer & Alert → Settings &
Location contracts; no new graph edge is introduced.

## Acceptance closure

The single accepted FT-009 AC is owned by `TASK-011-T3-FT-009-W10` through the
exact feature locator. `REQ-019` covers sound and app-volume settings,
`REQ-020` covers glass intensity and live production-card preview, and
`REQ-021` covers validation, inline errors, last-valid-value preservation,
auto-save and Back navigation. FT-007 remains the owner of completion-time
overdue presentation and alert execution.

## Canonical SDD coverage

All applicable concerns reuse existing subject-based canonical specs. No new
canonical specification, competing path, feature-owned hub, graph edge or
optional behavior-spec file is required.

| Concern | Action | Canonical basis |
|---|---|---|
| Architecture, ownership and personalization projection | `reuse` | `system-architecture.md#capability-slice-runtime`, `#ad-002---application-owned-local-state-is-the-product-source-of-truth`, `#ad-003---cross-slice-orchestration-stays-in-a-capability-owner`, `#ad-007---one-personalization-projection-serves-today-and-settings-preview`; `boundary-map.md#modules`, `#dependency-graph`, `#accepted-ownership-summary` |
| Settings and consumer contracts | `reuse` | `capability-interfaces.md#main-display-to-weather-context`, `#main-display-to-settings-and-location`, `#settings-personalization-surface`, `#timer-and-alert-to-settings-and-location`; `platform-runtime.md#timer-and-audio-runtime-boundary` |
| Production card and preview presentation | `reuse` | `weather-card-presentation.md#temperature-and-glass-rules`, `#personalization-preview` |
| Local validation and persistence | `reuse` | `local-data.md#ownership-matrix`, `#durable-data-rules`, `#validation-and-serialization-boundaries` |
| Platform compatibility and proof | `reuse` | `platform-runtime.md#timer-and-audio-runtime-boundary`, `#compatibility-and-failure-rules`; `runtime-verification.md#deterministic-host-side-checks`, `#target-device-evidence` |

No `needed_before_tasks` Backbone row remains and Planning Revision remains
positive and unchanged at `1`.

## Scope and execution path

`Settings & Location` owns validation, persistence and the validated
alert/glass presentation projection. `Main Display` consumes that projection,
combines it with its existing Today weather read or the accepted `24 °C`
fallback and composes both the production Today card and Settings preview.
`Timer & Alert` reads the alert sound/volume projection without writing it;
Android Runtime Adapter retains silent/DND policy authority. The composition
root only wires accepted boundaries.

In scope: accepted built-in sounds and default, normalized app alert volume
`0…100` with default `70`, glass intensity `0…1` with default `0.45`, valid
auto-save/reload, invalid-value preservation, exact inline errors, no modal
dialog, Back navigation, live static-card preview, fallback temperature,
two-arrow preview content and boundary-safe consumer projection.

Out of scope: completion-time alert scheduling/ramp/duration/dismissal,
weather normalization/cache/history, direct Weather Context storage access,
new Settings controls, a Settings → Weather Context edge, heavy visual effects,
new dependencies, backend/cloud/accounts, Google Services, reboot recovery and
runtime evidence during planning.

Primary owner/code root: `Settings & Location`,
`app/src/main/kotlin/com/hozayushka/app/settings`. Advisory consumers are
`app/src/main/kotlin/com/hozayushka/app/display/`,
`app/src/main/kotlin/com/hozayushka/app/timer/`, composition wiring only when
needed, and deterministic tests under
`app/src/test/kotlin/com/hozayushka/app/` with fixture support under
`app/src/test/resources/fixtures/`.

## Verification route

- `./gradlew clean assembleDebug`
- `./gradlew testDebugUnitTest`
- Known isolated Settings/platform state, deterministic presentation fixtures,
  safe reset/cleanup and no live credentials.
- Host checks prove validation, persistence, projection, no-network preview,
  ownership and platform-policy semantics; target-device evidence is limited
  to host-insufficient Settings readability/static pseudo-glass outcomes.
- T3 execution must establish claim-linked RED/GREEN and safe rerun evidence;
  planning produces no runtime evidence.

## Handoff

After this task-plan surface is accepted, the immediate route is
`/review-tasks-plan FT-009`; execution, `/mb-doctor`, `/verify`,
`/red-verify` and `/mb-sync` are outside this planning run.
