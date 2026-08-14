---
description: Implementation plan for the FT-005 preset timer configuration surface.
status: active
last_updated: 2026-08-10
---
# IMPL-FT-005 — Preset timer configuration

## Goal

Deliver three independently configurable timer presets with accepted defaults,
field bounds, positive-total validation, last-valid-value preservation, compact
floor-rounded labels, fixed neon colors and a selected/active projection that
the existing countdown boundary can consume. Settings & Location remains the
configuration owner; Timer & Alert remains the active-timer owner; Main Display
remains the presentation owner.

## Ordered work

1. `TASK-007-T3-FT-005-W6` — implement and verify the cohesive validated preset
   projection, persistence, label/color presentation and countdown-consumer
   integration behind the existing capability contracts.

## Owner, graph and dependency

- Primary owner: `Settings & Location` for validated timer-duration values and
  persistence.
- Cross-slice presentation/consumption: `Main Display` renders the three preset
  buttons and selected/active state; `Timer & Alert` reads the validated
  projection and retains sole active-timer authority.
- Existing edges: `Main Display → Settings & Location`, `Timer & Alert →
  Settings & Location` and `Main Display → Timer & Alert`; each resolves to an
  exact heading in the registered Capability Interfaces contract.
- Direct prerequisite: `TASK-006-T3-FT-004-W5`. Foundation remains transitive
  through the approved FT-004 → FT-003 → FT-002 → FT-001 → FT-000 chain. No
  dependency on FT-006–FT-009 is introduced.

## Canonical concern coverage

| Concern | Action | Canonical coverage and reason |
|---|---|---|
| Accepted module inventory and dependency topology | `reuse` | [Boundary Map modules](../../contracts/boundary-map.md#modules) and [Dependency Graph](../../contracts/boundary-map.md#dependency-graph) already register Settings & Location, Main Display, Timer & Alert and their accepted edges. |
| Ownership and cross-slice placement | `reuse` | [AD-002](../../architecture/system-architecture.md#ad-002---application-owned-local-state-is-the-product-source-of-truth), [AD-003](../../architecture/system-architecture.md#ad-003---cross-slice-orchestration-stays-in-a-capability-owner), and [Accepted Ownership Summary](../../contracts/boundary-map.md#accepted-ownership-summary) settle the owners and forbid bypasses. |
| Preset capability contracts | `reuse` | [Main Display to Settings and Location](../../contracts/capability-interfaces.md#main-display-to-settings-and-location), [Main Display to Timer and Alert](../../contracts/capability-interfaces.md#main-display-to-timer-and-alert) and [Timer and Alert to Settings and Location](../../contracts/capability-interfaces.md#timer-and-alert-to-settings-and-location) define the existing read/projection boundaries. |
| Local settings data and validation | `reuse` | [Ownership Matrix](../../domains/local-data.md#ownership-matrix), [Durable Data Rules](../../domains/local-data.md#durable-data-rules) and [Validation and Serialization Boundaries](../../domains/local-data.md#validation-and-serialization-boundaries) cover owner-local persistence, validation and last-valid-value preservation. |
| Timer state relationship | `reuse` | [Timer Lifecycle](../../states/lifecycle-map.md#timer-lifecycle) and [Timer State Contract](../../states/lifecycle-map.md#timer-state-contract) define one active timer and keep full lifecycle ownership with Timer & Alert. |
| Verification route | `reuse` | [Deterministic Host-Side Checks](../../testing/runtime-verification.md#deterministic-host-side-checks) covers validation, labels and one-active-timer checks; [Target-Device Evidence](../../testing/runtime-verification.md#target-device-evidence) remains conditional for visual results host-side checks cannot prove. |

No canonical spec is created or extended. No new graph edge, module, state,
storage owner, public contract or product decision is selected by this plan.

## Scope

### In scope

- Three independent preset duration records with defaults 3m, 10m and 30m.
- Hours 0–99, minutes 0–59 and seconds 0–59 validation with a positive total.
- Rejection of zero/out-of-range values while preserving the last valid value and
  reloading valid changes from owner-local persistence.
- Highest-non-zero-unit labels with floor rounding.
- Orange, pink and purple neon button outlines and a selected/active projection
  available through the existing Timer & Alert contract.
- Proof that configuration does not create a second active timer.

### Out of scope

- Countdown start, elapsed arithmetic, protected cancellation, temporary
  interruption recovery and overdue behavior from FT-006/FT-007.
- Overdue sound set, audio volume, ramp/cap, silent/DND handling and
  personalization settings from FT-007/FT-009.
- API-key input, location/catalog, provider refresh, generalized Settings error
  surface and glass preview from FT-008/FT-009.
- Backend/cloud/accounts, Google Services, reboot recovery, event/message
  infrastructure, new dependencies and unaccepted UI controls.

## Expected advisory change surface

- `app/src/main/kotlin/com/hozayushka/app/settings/SettingsCapability.kt` —
  validated preset state, owner-local persistence and last-valid-value behavior.
- `app/src/main/kotlin/com/hozayushka/app/timer/TimerCapability.kt` — read the
  validated preset projection and preserve active-state ownership.
- `app/src/main/kotlin/com/hozayushka/app/display/DisplayCapability.kt` — render
  three preset buttons, labels, fixed colors and selected/active state.
- `app/src/main/kotlin/com/hozayushka/app/app/FoundationRuntime.kt` — wiring only
  if the existing composition requires the new projection.
- `app/src/main/res/values/` — accepted Russian strings/style values if needed.
- `app/src/test/kotlin/com/hozayushka/app/` — deterministic validation,
  persistence, label/color and cross-boundary checks.

These paths are advisory and non-exhaustive; no hard `write_boundary` is set.

## Quality gates and claim-linked proof

- `./gradlew clean assembleDebug` — clean Android build.
- `./gradlew testDebugUnitTest` — deterministic preset validation, persistence,
  labels, colors and projection checks.
- Target-device evidence is conditional and limited to an FT-005 visual result
  that host-side checks cannot establish. This planning run creates no runtime
  evidence and uses no live API key.

| Claim | Decisive result | Proof artifact |
|---|---|---|
| `FT-005-AC-001 / REQ-011` | Exactly three independent presets are stored and exposed; configuration does not create parallel active timer state; Timer & Alert remains the sole active-state owner. | Deterministic settings/timer integration result |
| `FT-005-AC-002 / REQ-011` | All field boundaries and positive-total rule pass; invalid input leaves the last valid persisted value available after reload. | Isolated host validation/persistence result |
| `FT-005-AC-003 / REQ-011` | Defaults and highest-non-zero floor-rounded labels match every mixed-unit case. | Deterministic label result |
| `FT-005-AC-004 / REQ-011` | Three accepted neon color tokens and selected/active consumer projection match the contract. | Host presentation/integration result, plus scoped device observation only if needed |

## Constraints and invariants

- Settings & Location owns validated preset values and persistence; Timer & Alert
  owns active lifecycle state; Main Display owns composition only.
- Consumers use the registered capability contracts and never access private
  neighbor stores or raw adapters.
- A valid configured duration is positive; invalid input cannot replace the
  previous valid value; configuration cannot create a parallel active timer.
- Planning Revision is reconciled to `2`; do not add a canonical spec, graph edge or
  widen the product surface.

## Direct normative inputs

- [.memory-bank/features/FT-005-timer-presets.md](../../features/FT-005-timer-presets.md)
- [.memory-bank/epics/EP-003-timers-alert.md](../../epics/EP-003-timers-alert.md)
- [.memory-bank/requirements.md](../../requirements.md)
- [.memory-bank/prd.md](../../prd.md)
- [.memory-bank/invariants.md](../../invariants.md)
- [.memory-bank/architecture/system-architecture.md](../../architecture/system-architecture.md)
- [.memory-bank/contracts/boundary-map.md](../../contracts/boundary-map.md)
- [.memory-bank/contracts/capability-interfaces.md](../../contracts/capability-interfaces.md)
- [.memory-bank/domains/local-data.md](../../domains/local-data.md)
- [.memory-bank/states/lifecycle-map.md](../../states/lifecycle-map.md)
- [.memory-bank/contracts/platform-runtime.md](../../contracts/platform-runtime.md)
- [.memory-bank/testing/runtime-verification.md](../../testing/runtime-verification.md)
- [.memory-bank/workflows/tier-policy.md](../../workflows/tier-policy.md)

## Handoff

Queue action is `reconciled`; no new task is created and
`TASK-007-T3-FT-005-W6` remains `done`. Exact next owner is fresh
`/review-tasks-plan --all`.
Execution, `/mb-doctor`, `/verify`, `/red-verify` and `/mb-sync` are not part of
this planning run.
