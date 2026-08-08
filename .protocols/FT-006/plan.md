---
description: Planning surface for FT-006 countdown lifecycle and cancellation.
status: active
last_updated: 2026-08-07
---
# FT-006 — Feature plan

## Objective

Turn the accepted preset projection and Foundation timer seam into one
independently observable countdown lifecycle: immediate start, one active
timer, protected cancellation, temporary-interruption recovery and network
independence. Timer & Alert remains the lifecycle owner; Main Display renders
its projection and submits gestures through the accepted capability contract.

## Accepted basis

- Feature: [.memory-bank/features/FT-006-countdown-lifecycle.md](../../.memory-bank/features/FT-006-countdown-lifecycle.md)
- Epic: [.memory-bank/epics/EP-003-timers-alert.md](../../.memory-bank/epics/EP-003-timers-alert.md)
- Direct feature requirements: `REQ-011`, `REQ-012`, `REQ-013`, `REQ-014`, `REQ-025`
- Global Backbone: `complete`, Planning Revision `1`
- Foundation Gate: `TASK-002-T3-FT-000-W1`, status `done`
- Approved predecessor: `TASK-007-T3-FT-005-W6`, status `planned`
- Clarified PRD: `clarification_status: complete`

## Queue

| Order | Task | Tier | Wave | Status | Depends on | Primary owner |
|---|---|---|---|---|---|---|
| 1 | `TASK-008-T3-FT-006-W7` | T3 | W7 | planned | `TASK-007-T3-FT-005-W6` | Timer & Alert |

One task is sufficient. Starting, active-state cardinality, protected gesture
handling, persisted-time rehydration and network independence are one
Timer & Alert lifecycle outcome with one user-visible proof path. The task
crosses only the registered Main Display → Timer & Alert, Main Display →
Android Runtime Adapter, Timer & Alert → Settings & Location and Timer & Alert
→ Android Runtime Adapter boundaries.
It is not split by file, layer, provider, persistence primitive or test
artifact.

## RTM-facing ownership map

`REQ-011` remains the RTM primary requirement for FT-005 preset configuration.
FT-006 owns only the runtime delta that a validated selected preset starts
through Timer & Alert without parallel active state; it does not reimplement
configuration validation or persistence. `REQ-012`, `REQ-013`, `REQ-014` and
the timer portion of `REQ-025` are owned by this countdown lifecycle task.
Overdue rendering, alert sound selection/ramp/cap and silent/DND presentation
remain downstream FT-007/FT-009 concerns; FT-006 only exposes and rehydrates
the accepted `overdue` lifecycle state where the feature contract requires it.
The `REQ-025` resilience proof also covers dismissing an already-overdue state
by any tap and returning to Main Display without network/provider input; FT-007
remains the owner of overdue presentation and audio.

## Canonical SDD coverage

All applicable concerns reuse the existing subject-based canonical specs. No
new canonical specification or behavior-spec file is required.

| Concern | Action | Canonical basis | Planning reason |
|---|---|---|---|
| Architecture and capability ownership | `reuse` | [System Architecture](../../.memory-bank/architecture/system-architecture.md#capability-slice-runtime), [AD-002](../../.memory-bank/architecture/system-architecture.md#ad-002---application-owned-local-state-is-the-product-source-of-truth), [AD-003](../../.memory-bank/architecture/system-architecture.md#ad-003---cross-slice-orchestration-stays-in-a-capability-owner) | Timer & Alert owns timer transitions and writes; the composition root only wires lifecycle. |
| Module inventory and dependency graph | `reuse` | [Boundary Map modules](../../.memory-bank/contracts/boundary-map.md#modules), [Dependency Graph](../../.memory-bank/contracts/boundary-map.md#dependency-graph), [Ownership Summary](../../.memory-bank/contracts/boundary-map.md#accepted-ownership-summary) | Every changed unit and crossed edge is already registered. |
| Main Display timer command/projection | `reuse` | [Main Display → Timer & Alert](../../.memory-bank/contracts/capability-interfaces.md#main-display-to-timer-and-alert) | Main Display renders countdown data and submits start/cancel gestures; it does not calculate time or write timer state. |
| Preset projection input | `reuse` | [Timer & Alert → Settings & Location](../../.memory-bank/contracts/capability-interfaces.md#timer-and-alert-to-settings-and-location) | FT-006 consumes the validated projection produced by FT-005 and does not change Settings ownership. |
| Timer lifecycle and persisted state | `reuse` | [Timer Lifecycle](../../.memory-bank/states/lifecycle-map.md#timer-lifecycle), [Timer State Contract](../../.memory-bank/states/lifecycle-map.md#timer-state-contract), [Local Data ownership](../../.memory-bank/domains/local-data.md#ownership-matrix), [Durable Data Rules](../../.memory-bank/domains/local-data.md#durable-data-rules) | Accepted `idle|countdown|overdue`, start-point arithmetic and temporary recovery are already defined. |
| Android display/lifecycle/time boundary | `reuse` | [Display Runtime Boundary](../../.memory-bank/contracts/platform-runtime.md#display-runtime-boundary), [Timer and Audio Runtime Boundary](../../.memory-bank/contracts/platform-runtime.md#timer-and-audio-runtime-boundary), [Compatibility and Failure Rules](../../.memory-bank/contracts/platform-runtime.md#compatibility-and-failure-rules) | Android supplies display/lifecycle/network signals; the product recalculates state and does not add reboot recovery. |
| Verification and target-ROM route | `reuse` | [Deterministic Host-Side Checks](../../.memory-bank/testing/runtime-verification.md#deterministic-host-side-checks), [Target-Device Evidence](../../.memory-bank/testing/runtime-verification.md#target-device-evidence), [Verification Route](../../.memory-bank/contracts/platform-runtime.md#verification-route) | Host checks prove arithmetic and gestures; device evidence is limited to host-insufficient interruption/readability behavior. |

FT-006 frontmatter remains `spec_design_status: complete` with its existing
subject links. No `needed_before_tasks` Backbone row remains and Planning
Revision remains positive and unchanged at `1`.

## Scope boundary

In scope: short-tap start from the validated selected preset, countdown
projection replacing the dominant clock with the current time moved aside,
active-button indication, one-active-timer enforcement, protected single-tap
hint/double-tap cancellation, persisted start/duration rehydration across
Activity/foreground/screen-off/temporary process interruption, and operation
without network/weather-service availability, including any-tap dismissal of an
already-overdue state back to Main Display.

Out of scope: preset field validation/defaults/labels/colors and Settings
configuration (FT-005), fullscreen overdue rendering and alert audio behavior
(FT-007), alert personalization (FT-009), reboot auto-start/recovery, direct
private-store access, a new event/message boundary, a new dependency/provider,
backend/cloud/accounts/Google Services and unaccepted UI controls.

## Primary owner, boundaries and execution path

- Primary owner: `Timer & Alert`, code root
  `app/src/main/kotlin/com/hozayushka/app/timer`.
- Cross-slice edges: Main Display → Timer & Alert for start, lifecycle
  projection and accepted gestures; Timer & Alert → Settings & Location for
  validated preset data; Timer & Alert → Android Runtime Adapter for lifecycle
  and time signals; and the existing Main Display → Android Runtime Adapter
  display boundary for countdown composition.
- Timer & Alert owns active-timer persistence, elapsed arithmetic and every
  `idle|countdown|overdue` transition. Main Display never reads the timer store
  or calculates remaining time. Settings & Location remains the preference
  owner. The composition root wires signals only.
- The bounded path is validated preset projection → Timer & Alert persisted
  record/state calculation → Main Display projection/gesture result, with
  platform lifecycle signals feeding rehydration. No new graph edge is
  selected.

## Expected advisory change surface

- `app/src/main/kotlin/com/hozayushka/app/timer/` — active-timer command,
  persisted record, lifecycle calculation and public projection.
- `app/src/main/kotlin/com/hozayushka/app/display/` — countdown layout,
  selected/active presentation and protected gesture dispatch through Timer &
  Alert; no timer arithmetic or private-store access.
- `app/src/main/kotlin/com/hozayushka/app/settings/` — only the existing
  validated preset read seam when required by the accepted contract; no new
  Settings surface.
- `app/src/main/kotlin/com/hozayushka/app/adapters/platform/` — existing
  lifecycle/time signal seam only; platform remains policy owner.
- `app/src/main/kotlin/com/hozayushka/app/app/` — wiring/lifecycle forwarding
  only when required by the existing composition-root responsibility.
- `app/src/main/res/` — accepted countdown/hint presentation resources when
  required by the existing Android scaffold.
- `app/src/test/kotlin/com/hozayushka/app/` and
  `app/src/test/resources/fixtures/` — deterministic timer, gesture,
  persistence/rehydration and network-independent checks.

These paths are advisory and non-exhaustive. No hard `write_boundary` is set;
the semantic scope, forbidden scope and stop conditions remain binding.

## Applicable gates and claim-linked proof

- `./gradlew clean assembleDebug` — clean Android build.
- `./gradlew testDebugUnitTest` — deterministic timer arithmetic, active-state,
  gesture, persistence/rehydration and offline-path checks.
- The target-device route applies only to lifecycle/screen-off/readability
  behavior that host checks cannot establish. This planning run creates no
  runtime evidence.

| Claim | Decisive result | Artifact |
|---|---|---|
| `FT-006-AC-001 / REQ-012` | A selected valid preset starts immediately; countdown occupies the clock area, current time moves aside and the originating button is highlighted. | Deterministic start/projection result, plus scoped device observation where host proof is insufficient |
| `FT-006-AC-002 / REQ-011` | At every accepted start point there is no parallel active timer; the runtime consumes the validated preset projection without taking configuration ownership. | Isolated Timer & Alert/Settings integration result |
| `FT-006-AC-003 / REQ-013` | Single tap preserves countdown and shows the accepted hint; double tap cancels and returns to the standard display. | Deterministic gesture timing/transition result |
| `FT-006-AC-004 / REQ-014` | Rehydration recomputes the correct remaining or overdue state from persisted start/duration after temporary interruption; reboot is not claimed. | Isolated persistence/rehydration result, plus target-ROM lifecycle observation where required |
| `FT-006-AC-005 / REQ-025` | With network/weather-service availability absent, timer start/countdown and protected cancellation remain functional; an already-overdue state is dismissed by any tap and returns to Main Display. | Offline/no-provider deterministic result covering the already-overdue any-tap dismissal; no FT-007 rendering/audio claim |

## Constraints and invariants

- Timer & Alert is the sole active-timer state and persistence owner; all
  consumers use its public projection and commands.
- The persisted start point and positive duration are the source for elapsed
  and remaining calculations. Rehydration may produce `countdown` or
  `overdue`; reboot recovery is excluded.
- A single countdown tap is never cancellation; double tap is the accepted
  cancellation command. No unaccepted alternate gesture is added.
- Network absence is a platform signal and must not disable the timer path.
- No real API key, live provider request, secret-bearing fixture/evidence,
  new dependency, event bus, direct storage bypass or composition-root business
  orchestration is authorized.

## Direct normative inputs

- [.memory-bank/features/FT-006-countdown-lifecycle.md](../../.memory-bank/features/FT-006-countdown-lifecycle.md)
- [.memory-bank/epics/EP-003-timers-alert.md](../../.memory-bank/epics/EP-003-timers-alert.md)
- [.memory-bank/requirements.md](../../.memory-bank/requirements.md)
- [.memory-bank/prd.md](../../.memory-bank/prd.md)
- [.memory-bank/invariants.md](../../.memory-bank/invariants.md)
- [.memory-bank/architecture/system-architecture.md](../../.memory-bank/architecture/system-architecture.md)
- [.memory-bank/contracts/boundary-map.md](../../.memory-bank/contracts/boundary-map.md)
- [.memory-bank/contracts/capability-interfaces.md](../../.memory-bank/contracts/capability-interfaces.md)
- [.memory-bank/domains/local-data.md](../../.memory-bank/domains/local-data.md)
- [.memory-bank/states/lifecycle-map.md](../../.memory-bank/states/lifecycle-map.md)
- [.memory-bank/contracts/platform-runtime.md](../../.memory-bank/contracts/platform-runtime.md)
- [.memory-bank/testing/runtime-verification.md](../../.memory-bank/testing/runtime-verification.md)
- [.memory-bank/workflows/tier-policy.md](../../.memory-bank/workflows/tier-policy.md)

## Handoff

After this planning surface is accepted, the immediate route is
`/review-tasks-plan FT-006`; execution, `/mb-doctor`, `/verify`,
`/red-verify` and `/mb-sync` are not part of this planning run.
