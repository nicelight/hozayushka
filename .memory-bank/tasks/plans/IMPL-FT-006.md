---
description: Implementation plan for FT-006 countdown lifecycle and cancellation.
status: active
last_updated: 2026-08-12
---
# IMPL-FT-006 — Countdown lifecycle and cancellation

## Outcome

Implement one Timer & Alert lifecycle outcome: a validated selected preset
starts immediately, only one timer is active, accidental cancellation is
protected, temporary Activity/foreground/screen-off/process interruption
rehydrates from persisted start/duration data, and the timer remains usable
without network/weather service availability, including dismissal of an
already-overdue state by any tap back to Main Display.

## Bounded task shape

- Completed task: `TASK-008-T3-FT-006-W7`, owned by `Timer & Alert`.
- Follow-up task: `TASK-030-T3-FT-006-W27`, owned by `Main Display`, planned
  directly after `TASK-029-T3-FT-001-W26`; Foundation is transitive through
  the approved chain ending at `TASK-002-T3-FT-000-W1`.
- Tier: `T3`, because the follow-up changes user-visible Android runtime
  presentation while carrying timer/lifecycle regression proof and the
  accepted deferred target-readability route.
- W7 remains scheduler-closed `done`; W27 is only `planned` until the fresh
  task-plan review and later scheduler-owned readiness/execution routes.

## Acceptance closure

`TASK-008-T3-FT-006-W7` owns all five original FT-006 ACs. `REQ-011` is
retained as a scoped runtime integration claim for the one-active-timer invariant while
FT-005 retains configuration validation/defaults/labels/colors. `REQ-012`,
`REQ-013`, `REQ-014` and `REQ-025` are covered by the corresponding exact
feature AC locators. The `REQ-025` locator explicitly includes no-network
dismissal of an already-overdue state by any tap back to Main Display. FT-007
owns overdue presentation/audio; no accepted AC is left without an owner and
no FT-007–FT-009 AC is adopted.

## Execution-path sanity check

The plausible path is: FT-005's validated preset projection → Timer & Alert
start command and private persisted timer record → elapsed/remaining state
projection → Main Display countdown and protected gesture result; Android
lifecycle signals trigger rehydration from the same record. Host probes cover
state arithmetic, cardinality, gestures, already-overdue any-tap dismissal and
no-network operation; target evidence covers only lifecycle/display behavior
host checks cannot establish.
For W27 the bounded path is: existing Timer & Alert countdown projection →
Main Display active-state surface → focused host visual/lifecycle comparison.
The task renders no weather/city/date/card content on that surface, compares
countdown digits with the final idle-clock result without selecting a fixed
numeric target, and keeps the activating preset's existing color identity in a
transparent neon circular backdrop. This is one cohesive independently
verifiable presentation outcome; no independent prerequisite or second owner
is exposed.

## Canonical SDD coverage

All concrete concerns reuse existing canonical subjects:

- [System Architecture](../../architecture/system-architecture.md#capability-slice-runtime), [AD-002](../../architecture/system-architecture.md#ad-002---application-owned-local-state-is-the-product-source-of-truth), [AD-003](../../architecture/system-architecture.md#ad-003---cross-slice-orchestration-stays-in-a-capability-owner)
- [Boundary Map modules](../../contracts/boundary-map.md#modules), [Dependency Graph](../../contracts/boundary-map.md#dependency-graph), [Ownership Summary](../../contracts/boundary-map.md#accepted-ownership-summary)
- [Main Display → Timer & Alert](../../contracts/capability-interfaces.md#main-display-to-timer-and-alert), [Timer & Alert → Settings & Location](../../contracts/capability-interfaces.md#timer-and-alert-to-settings-and-location), [Orchestration Ownership](../../contracts/capability-interfaces.md#orchestration-ownership)
- [Local Data ownership](../../domains/local-data.md#ownership-matrix), [Durable Data Rules](../../domains/local-data.md#durable-data-rules), [Validation and Serialization Boundaries](../../domains/local-data.md#validation-and-serialization-boundaries)
- [Timer Lifecycle](../../states/lifecycle-map.md#timer-lifecycle), [Timer State Contract](../../states/lifecycle-map.md#timer-state-contract)
- [Display Runtime Boundary](../../contracts/platform-runtime.md#display-runtime-boundary), [Timer and Audio Runtime Boundary](../../contracts/platform-runtime.md#timer-and-audio-runtime-boundary), [Compatibility and Failure Rules](../../contracts/platform-runtime.md#compatibility-and-failure-rules), [Verification Route](../../contracts/platform-runtime.md#verification-route)
- [Deterministic Host-Side Checks](../../testing/runtime-verification.md#deterministic-host-side-checks), [Target-Device Evidence](../../testing/runtime-verification.md#target-device-evidence)

No feature-owned canonical hub or optional behavior spec is created. Exact
class split, timer clock implementation and filename identity remain executor
discretion within the accepted code roots unless they require an operator
checkpoint for a new dependency, public boundary or product behavior.

## Expected advisory change surface

- `app/src/main/kotlin/com/hozayushka/app/timer/`
- `app/src/main/kotlin/com/hozayushka/app/display/`
- `app/src/main/kotlin/com/hozayushka/app/settings/`
- `app/src/main/kotlin/com/hozayushka/app/adapters/platform/`
- `app/src/main/kotlin/com/hozayushka/app/app/`
- `app/src/main/res/`
- `app/src/test/kotlin/com/hozayushka/app/`
- `app/src/test/resources/fixtures/`

The W7 surface remains advisory and historical. W27's production/test hard
boundary is exactly `app/src/main/kotlin/com/hozayushka/app/display/DisplayCapability.kt`
and `app/src/test/kotlin/com/hozayushka/app/DisplayProjectionTest.kt`.
TimerCapability, TimerAlertPolicy, PlatformRuntimeAdapter, resources,
composition-root wiring, network/provider paths and audio behavior are
read-only/regression or forbidden scope.

## Gates, UAT and proof

- `./gradlew clean assembleDebug` — clean Android build.
- `./gradlew testDebugUnitTest` — deterministic timer lifecycle and integration
  checks.
- Use isolated/resettable timer state with known initial `idle`, synthetic
  timestamps and cleanup. Do not use live credentials or claim runtime
  evidence during planning.
- Apply target-device verification only to accepted lifecycle/screen-off and
  display results that host checks cannot prove; reboot recovery remains
  excluded.

| Claim | Proof result |
|---|---|
| `FT-006-AC-001 / REQ-012` | Immediate selected-preset start, countdown projection, moved-aside current time and active-origin indication. |
| `FT-006-AC-002 / REQ-011` | No parallel active timer while consuming the validated FT-005 projection. |
| `FT-006-AC-003 / REQ-013` | Single tap preserves countdown/shows hint; double tap cancels and returns to Main Display. |
| `FT-006-AC-004 / REQ-014` | Persisted start/duration recalculates remaining or overdue after temporary interruption; target route only where host proof is insufficient. |
| `FT-006-AC-005 / REQ-025` | With network/weather-service input absent, start/countdown and protected cancellation remain usable; an already-overdue state is dismissed by any tap and returns to Main Display. The proof does not claim FT-007 rendering or audio behavior. |

## Constraints and non-goals

Timer & Alert owns active timer writes and transitions. Main Display owns
composition and gestures, Settings & Location owns validated preset values,
Android owns platform signals/policy, and the composition root owns wiring only.
Do not add reboot recovery, overdue fullscreen/audio behavior, new dependencies,
event infrastructure, direct private-storage access, backend/cloud/accounts,
Google Services or unaccepted UI scope.

## Direct normative inputs

- [.memory-bank/features/FT-006-countdown-lifecycle.md](../../features/FT-006-countdown-lifecycle.md)
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

## W7 boundary reconciliation

`TASK-008-T3-FT-006-W7` is `done` with fresh functional `PASS` and T3 semantic
`semantic-pass`. The indexed task links the final verification, semantic
verification and verifier-owned probe evidence for immediate start, one active
timer, protected cancellation, temporary rehydration and network-independent
overdue dismissal. Target-device evidence remains `DEFERRED` and non-blocking;
no runtime `PASS` is claimed. FT-006 and REQ-012/013/014/025 are reconciled to
`implemented`; FT-005's REQ-011 ownership remains `planned`. Scheduler
promotion, dependent-state reconciliation, checkpoint and terminal-state
updates remain outside `/mb-sync`.

## W27 planning boundary

W27 reuses the existing feature ACs and canonical subject specs; no new spec or
behavior-spec file is created. It owns the active countdown presentation delta
under `FT-006-AC-001` and carries regression proof for `FT-006-AC-002`,
`FT-006-AC-003`, `FT-006-AC-004` and `FT-006-AC-005`. Its task card requires a named visual-QA
rubric, fresh host RED/GREEN artifacts, isolated timer/lifecycle fixtures and
honest `DEFERRED` target/device/audio runtime records. No emulator, device, adb,
network or audio run is planned. If a fixed dp/ratio/gradient-stop decision is
needed, pause and route to `/feature-doctor FT-006`.

## Handoff

Queue action is `created`; W7 remains `done` and
`TASK-030-T3-FT-006-W27` remains `planned`. Exact next owner is fresh
`/review-tasks-plan FT-006`; scheduler and sync state remain untouched.
