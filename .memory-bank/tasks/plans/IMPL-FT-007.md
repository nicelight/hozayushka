---
description: Implementation plan for FT-007 overdue state and permitted alert.
status: active
last_updated: 2026-08-07
---
# IMPL-FT-007 — Overdue state and alert

## Outcome

Implement one Timer & Alert outcome on top of the FT-006 lifecycle: completion
renders the active preset as a fullscreen neon overdue state with a blinking
`+` and stable full elapsed counter, any tap dismisses it, and the accepted
repeatable built-in alert follows ramp, cap and Android silent/DND policy
without controlling the visual state.

## Bounded task shape

- One task: `TASK-009-T3-FT-007-W8`.
- Primary owner: `Timer & Alert`.
- Direct predecessor: `TASK-008-T3-FT-006-W7`; Foundation is transitive through
  the approved chain ending at `TASK-002-T3-FT-000-W1`.
- Tier: `T3`, because the outcome combines production Android runtime/display
  behavior, mutable lifecycle integration, platform audio policy, a bounded
  alert scheduler and target-device evidence.
- Status: `planned`; planning does not claim scheduler closure or promotion.

## Acceptance closure

The single task owns all five FT-007 ACs. `REQ-015` is covered by the
fullscreen overdue projection, full elapsed counter, persistent visual state
and any-tap dismissal. `REQ-016` is covered by the built-in signal policy and
the rule that audio suppression never removes the visual state. FT-006 retains
countdown arithmetic and temporary recovery ownership; FT-009 retains the
user-facing sound/volume Settings surface. No accepted FT-007 AC is left
without an owner and no FT-008/FT-009 outcome is adopted.

## Execution-path sanity check

The plausible path is: persisted FT-006 timer state reaches `overdue` → Timer
& Alert calculates the full elapsed projection and starts the permitted alert
request → Main Display renders the fullscreen state and sends any-tap
dismissal → Android Runtime Adapter applies sound ramp/repeat/cap and current
silent/DND/route policy. Host checks cover state, counter, gestures and policy
decisions; target evidence is limited to fullscreen/readability and
host-insufficient audio behavior. This is one cohesive independently
verifiable outcome and does not require a second task.

## Canonical SDD coverage

All concrete concerns reuse the existing subject-based canonical specs:

- [Capability Slice Runtime](../../architecture/system-architecture.md#capability-slice-runtime), [AD-002](../../architecture/system-architecture.md#ad-002---application-owned-local-state-is-the-product-source-of-truth), [AD-003](../../architecture/system-architecture.md#ad-003---cross-slice-orchestration-stays-in-a-capability-owner)
- [Boundary Map modules](../../contracts/boundary-map.md#modules), [Dependency Graph](../../contracts/boundary-map.md#dependency-graph), [Accepted Ownership Summary](../../contracts/boundary-map.md#accepted-ownership-summary)
- [Main Display to Timer and Alert](../../contracts/capability-interfaces.md#main-display-to-timer-and-alert), [Timer and Alert to Settings and Location](../../contracts/capability-interfaces.md#timer-and-alert-to-settings-and-location), [Orchestration Ownership](../../contracts/capability-interfaces.md#orchestration-ownership)
- [Ownership Matrix](../../domains/local-data.md#ownership-matrix), [Durable Data Rules](../../domains/local-data.md#durable-data-rules), [Validation and Serialization Boundaries](../../domains/local-data.md#validation-and-serialization-boundaries)
- [Timer Lifecycle](../../states/lifecycle-map.md#timer-lifecycle), [Timer State Contract](../../states/lifecycle-map.md#timer-state-contract)
- [Display Runtime Boundary](../../contracts/platform-runtime.md#display-runtime-boundary), [Timer and Audio Runtime Boundary](../../contracts/platform-runtime.md#timer-and-audio-runtime-boundary), [Compatibility and Failure Rules](../../contracts/platform-runtime.md#compatibility-and-failure-rules), [Verification Route](../../contracts/platform-runtime.md#verification-route)
- [Deterministic Host-Side Checks](../../testing/runtime-verification.md#deterministic-host-side-checks), [Target-Device Evidence](../../testing/runtime-verification.md#target-device-evidence)

No feature-owned canonical hub or optional behavior-spec file is created. Exact
audio mechanism, class split and filename identity remain execution-level
choices within the accepted code roots unless they require an operator
checkpoint for a new dependency, public boundary or product behavior.

## Expected advisory change surface

- `app/src/main/kotlin/com/hozayushka/app/timer/` — overdue projection,
  dismissal, repeat/cap scheduling and alert request.
- `app/src/main/kotlin/com/hozayushka/app/display/` — fullscreen overdue state,
  blinking `+`, stable elapsed counter and any-tap gesture dispatch.
- `app/src/main/kotlin/com/hozayushka/app/settings/` — existing validated
  sound/volume/default read seam only; no FT-009 Settings screen.
- `app/src/main/kotlin/com/hozayushka/app/adapters/platform/` — existing
  lifecycle/display/audio policy boundary.
- `app/src/main/kotlin/com/hozayushka/app/app/` — wiring only when required.
- `app/src/main/res/` — overdue resources and accepted built-in sound assets if
  required by the existing project-native implementation.
- `app/src/test/kotlin/com/hozayushka/app/` and
  `app/src/test/resources/fixtures/` — deterministic overdue and policy probes.

The surface is advisory and non-exhaustive; no hard write boundary is added.

## Gates, UAT and proof

- `./gradlew clean assembleDebug` — clean Android build.
- `./gradlew testDebugUnitTest` — deterministic overdue state, dismissal,
  elapsed-counter and audio-policy checks.
- Use known isolated `idle` state, synthetic timestamps/policy inputs, safe
  reset or disposable storage and cleanup. Use no live credentials and record
  no runtime evidence during planning. Apply the target-device route only to
  host-insufficient fullscreen, readability, ramp and silent/DND observations.

| Claim | Proof result |
|---|---|
| `FT-007-AC-001 / REQ-015` | Active preset color fills the accepted fullscreen overdue area; `+` blinks while the numeric counter remains stable. |
| `FT-007-AC-002 / REQ-015` | Full elapsed time is calculated from the persisted start and includes configured duration; visual state remains until tap. |
| `FT-007-AC-003 / REQ-015` | Single and double tap both stop the alert, dismiss overdue and return to Main Display. |
| `FT-007-AC-004 / REQ-016` | Accepted built-in signals use default/selected value, 5–10 second ramp, repeat-until-dismissal and a 30-minute audio cap. |
| `FT-007-AC-005 / REQ-016` | Silent/DND/unavailable route suppresses only audio; visual overdue and any-tap dismissal remain available. |

## Constraints and non-goals

Timer & Alert owns overdue state, elapsed interpretation and alert requests;
Main Display owns composition and gesture dispatch; Settings & Location owns
validated sound/volume values; Android owns audio permission/policy; and the
composition root owns wiring only. Do not reimplement FT-006 countdown or
recovery, add FT-009 Settings behavior, add reboot recovery, direct private
storage access, event infrastructure, new permissions, backend/cloud/accounts,
Google Services or unaccepted UI scope.

## Direct normative inputs

- [.memory-bank/features/FT-007-overdue-alert.md](../../features/FT-007-overdue-alert.md)
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

The fresh next step is `/review-tasks-plan FT-007`. Execution, `/mb-doctor`,
`/verify`, `/red-verify` and `/mb-sync` are not part of this planning run.
