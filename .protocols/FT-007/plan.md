---
description: Planning surface for FT-007 overdue state and alert.
status: active
last_updated: 2026-08-07
---
# FT-007 — Feature plan

## Objective

Turn the accepted `overdue` lifecycle projection from FT-006 into one
independently observable Timer & Alert outcome: the active preset expands into
its fullscreen neon state, shows a stable full elapsed counter, dismisses on a
single or double tap anywhere, and requests the permitted repeatable built-in
alert while preserving the visual state when Android suppresses audio.

## Accepted basis

- Feature: [.memory-bank/features/FT-007-overdue-alert.md](../../.memory-bank/features/FT-007-overdue-alert.md)
- Epic: [.memory-bank/epics/EP-003-timers-alert.md](../../.memory-bank/epics/EP-003-timers-alert.md)
- Direct requirements: `REQ-015`, `REQ-016`
- Global Backbone: `complete`, Planning Revision `1`
- Foundation Gate: `TASK-002-T3-FT-000-W1`, status `done`
- Approved predecessor: `TASK-008-T3-FT-006-W7`, status `planned`
- Clarified PRD: `clarification_status: complete`

## Queue

| Order | Task | Tier | Wave | Status | Depends on | Primary owner |
|---|---|---|---|---|---|---|
| 1 | `TASK-009-T3-FT-007-W8` | T3 | W8 | planned | `TASK-008-T3-FT-006-W7` | Timer & Alert |

One task is sufficient. Overdue entry, full elapsed presentation, persistent
visual state, any-tap dismissal and the repeatable audio-policy path are one
completion-time outcome with one Timer & Alert owner and one user-visible proof
path. The task crosses only the registered Main Display → Timer & Alert,
Timer & Alert → Settings & Location and Timer & Alert → Android Runtime
Adapter boundaries, plus the existing Main Display → Android Runtime display
boundary. It is not split by file, layer, sound asset, platform mechanism or
test artifact.

## Acceptance closure

All five FT-007 ACs are owned by `TASK-009-T3-FT-007-W8` through their exact
feature locators. `REQ-015` owns the overdue visual, elapsed-counter and
dismissal claims; `REQ-016` owns the alert and audio-suppression claims. FT-006
remains the owner of countdown arithmetic and temporary interruption
rehydration; this task consumes its accepted `overdue` state and proves the
overdue presentation/alert integration delta. FT-009 remains the owner of the
user-facing alert sound/volume Settings surface.

## Execution-path sanity check

The bounded path is: FT-006's persisted timer data reaches the accepted
`overdue` lifecycle state → Timer & Alert publishes the full elapsed value,
active preset color and alert request → Main Display renders the fullscreen
state and sends any-tap dismissal → Android Runtime Adapter applies the
selected built-in sound, ramp, repeat and silent/DND/route policy. Host probes
cover state projection, counter invariants, dismissal and policy decisions;
target evidence is reserved for fullscreen/readability and actual platform
audio behavior that host checks cannot establish. This is one cohesive
independently verifiable outcome; no independent prerequisite or separate
rollback unit requires another task.

## Canonical SDD coverage

All applicable concerns reuse existing subject-based canonical specs. No new
canonical specification, competing path or behavior-spec file is required.

| Concern | Action | Canonical basis | Planning reason |
|---|---|---|---|
| Architecture and capability ownership | `reuse` | [Capability Slice Runtime](../../.memory-bank/architecture/system-architecture.md#capability-slice-runtime), [AD-002](../../.memory-bank/architecture/system-architecture.md#ad-002---application-owned-local-state-is-the-product-source-of-truth), [AD-003](../../.memory-bank/architecture/system-architecture.md#ad-003---cross-slice-orchestration-stays-in-a-capability-owner) | Timer & Alert owns overdue transitions and alert requests; the composition root only wires accepted signals. |
| Module inventory and dependency graph | `reuse` | [Modules](../../.memory-bank/contracts/boundary-map.md#modules), [Dependency Graph](../../.memory-bank/contracts/boundary-map.md#dependency-graph), [Accepted Ownership Summary](../../.memory-bank/contracts/boundary-map.md#accepted-ownership-summary) | Every changed unit and crossed edge is already registered; no new graph edge is needed. |
| Main Display overdue projection and dismissal | `reuse` | [Main Display to Timer and Alert](../../.memory-bank/contracts/capability-interfaces.md#main-display-to-timer-and-alert), [Orchestration Ownership](../../.memory-bank/contracts/capability-interfaces.md#orchestration-ownership) | Main Display renders the public projection and submits any-tap dismissal; it does not own timer state. |
| Settings sound/volume input | `reuse` | [Timer and Alert to Settings and Location](../../.memory-bank/contracts/capability-interfaces.md#timer-and-alert-to-settings-and-location), [Durable Data Rules](../../.memory-bank/domains/local-data.md#durable-data-rules) | Timer & Alert consumes the validated projection and accepted default; it does not add or duplicate the later FT-009 Settings validation/persistence surface. |
| Timer lifecycle and overdue state | `reuse` | [Timer Lifecycle](../../.memory-bank/states/lifecycle-map.md#timer-lifecycle), [Timer State Contract](../../.memory-bank/states/lifecycle-map.md#timer-state-contract), [Ownership Matrix](../../.memory-bank/domains/local-data.md#ownership-matrix) | The accepted `countdown → overdue → idle` transitions and persisted start/duration authority already exist. |
| Android display and audio runtime | `reuse` | [Display Runtime Boundary](../../.memory-bank/contracts/platform-runtime.md#display-runtime-boundary), [Timer and Audio Runtime Boundary](../../.memory-bank/contracts/platform-runtime.md#timer-and-audio-runtime-boundary), [Compatibility and Failure Rules](../../.memory-bank/contracts/platform-runtime.md#compatibility-and-failure-rules) | Android supplies display/audio policy and route permission; visual overdue state is independent of suppression. |
| Verification and target-ROM route | `reuse` | [Deterministic Host-Side Checks](../../.memory-bank/testing/runtime-verification.md#deterministic-host-side-checks), [Target-Device Evidence](../../.memory-bank/testing/runtime-verification.md#target-device-evidence), [Verification Route](../../.memory-bank/contracts/platform-runtime.md#verification-route) | Host checks prove state/policy invariants; device evidence is limited to host-insufficient fullscreen, ramp and platform suppression behavior. |

No `needed_before_tasks` Backbone row remains and Planning Revision remains
positive and unchanged at `1`.

## Scope boundary

### In scope

- Transition/rendering of the accepted `overdue` projection from the active
  preset into its fullscreen neon area and color.
- Blinking `+` with a non-blinking numeric counter showing elapsed time from
  the persisted timer start, including configured duration.
- Keeping the visual overdue state until a single or double tap anywhere;
  returning to Main Display and stopping the alert on accepted dismissal.
- The three accepted built-in signals (`Классический`, `Колокольчик`,
  `Электронный`), default `Классический`, 5–10 second ramp, repeat-until-
  dismissal behavior and 30-minute audio cap.
- Applying Android silent mode, DND and unavailable-route policy without
  suppressing the visual state or blocking dismissal.
- Re-establishing the same overdue projection and alert-policy path when the
  already accepted persisted lifecycle state resumes after a temporary
  interruption, without adding reboot recovery.

### Out of scope

- Preset field validation/defaults/labels/colors and Settings configuration
  from FT-005.
- Countdown start, elapsed/remaining arithmetic, protected countdown
  cancellation and lifecycle rehydration ownership from FT-006; only the
  overdue projection/integration delta is consumed here.
- User-facing alert sound/volume Settings, auto-save and validation from
  FT-009; this task uses the existing validated projection/default seam.
- API-key, location, weather, forecast, glass-personalization, backend/cloud,
  accounts, Google Services, reboot auto-start/recovery, event infrastructure,
  new permissions or unaccepted UI controls.

## Primary owner, boundaries and execution path

- Primary owner: `Timer & Alert`, code root
  `app/src/main/kotlin/com/hozayushka/app/timer`.
- Main Display consumes the overdue projection and owns fullscreen composition
  and gesture dispatch through [Main Display to Timer and Alert](../../.memory-bank/contracts/capability-interfaces.md#main-display-to-timer-and-alert).
- Timer & Alert reads validated sound/volume through [Timer and Alert to Settings and Location](../../.memory-bank/contracts/capability-interfaces.md#timer-and-alert-to-settings-and-location), owns the alert request and visual overdue lifecycle, and never writes Settings directly.
- Android Runtime Adapter owns lifecycle/display/audio signals and silent/DND/
  route policy through [Timer and Audio Runtime Boundary](../../.memory-bank/contracts/platform-runtime.md#timer-and-audio-runtime-boundary).
- The composition root may forward lifecycle and navigation wiring only; it
  does not calculate elapsed time, choose product state or orchestrate the
  alert workflow.
- The bounded path is the FT-006 overdue state → Timer & Alert overdue
  projection/alert request → Main Display rendering and dismissal → permitted
  Android audio request. No direct private-store access or new edge is selected.

## Expected advisory change surface

- `app/src/main/kotlin/com/hozayushka/app/timer/` — overdue projection,
  dismissal transition, alert scheduling/repeat/cap and policy request.
- `app/src/main/kotlin/com/hozayushka/app/display/` — fullscreen overdue
  composition, blinking plus, stable elapsed counter and any-tap dispatch;
  no timer arithmetic or private-store access.
- `app/src/main/kotlin/com/hozayushka/app/settings/` — only the existing
  validated sound/volume/default read seam when required; no Settings screen
  or later FT-009 preference flow.
- `app/src/main/kotlin/com/hozayushka/app/adapters/platform/` — existing
  audio-policy, lifecycle and display signal seam only.
- `app/src/main/kotlin/com/hozayushka/app/app/` — wiring/lifecycle forwarding
  only when required by the accepted composition root.
- `app/src/main/res/` — accepted static overdue resources and built-in sound
  assets if the current project-native implementation needs them.
- `app/src/test/kotlin/com/hozayushka/app/` and
  `app/src/test/resources/fixtures/` — deterministic overdue state,
  dismissal, elapsed-counter and audio-policy checks.

These paths are advisory and non-exhaustive. No hard `write_boundary` is set;
the semantic scope, forbidden scope and stop conditions remain binding.

## Applicable gates and claim-linked proof

- `./gradlew clean assembleDebug` — clean Android build.
- `./gradlew testDebugUnitTest` — deterministic overdue projection,
  dismissal, elapsed-counter and audio-policy checks.
- Use isolated/resettable timer state with known initial `idle`, synthetic
  timestamps and synthetic platform-policy inputs. The target-device route
  applies only to fullscreen/readability, actual ramp and platform audio
  suppression behavior host checks cannot establish. This planning run creates
  no runtime evidence and uses no live credentials.

| Claim | Decisive result | Artifact |
|---|---|---|
| `FT-007-AC-001 / REQ-015` | At completion the active preset color fills the accepted fullscreen overdue area; `+` blinks and the numeric counter does not. | Deterministic overdue presentation result, plus scoped target-display observation where host proof is insufficient |
| `FT-007-AC-002 / REQ-015` | The counter equals elapsed time from the persisted start and includes configured duration; the visual overdue state remains until a tap. | Isolated elapsed/projection and dismissal-boundary result |
| `FT-007-AC-003 / REQ-015` | Single tap and double tap from overdue both stop the alert, clear the overdue state and return to Main Display. | Deterministic gesture/transition result |
| `FT-007-AC-004 / REQ-016` | Each accepted built-in signal uses the default/selected value, ramps for 5–10 seconds, repeats until dismissal and stops by 30 minutes. | Isolated audio-policy scheduler result, plus target-ROM observation where required |
| `FT-007-AC-005 / REQ-016` | Silent, DND or unavailable route suppresses only audio; visual overdue state remains and any tap still dismisses it. | Platform-policy integration result, plus scoped target observation where required |

## Constraints and invariants

- Timer & Alert is the sole owner of active timer state, overdue transitions,
  elapsed interpretation and alert requests; all consumers use its public
  projection and commands.
- The numeric overdue counter is derived from the accepted persisted start
  point and does not blink; only the `+` may blink. The visual state has no
  automatic 30-minute dismissal.
- Any tap in overdue is the accepted dismissal command. Audio denial, silent
  mode, DND and unavailable route cannot block dismissal or clear the visual
  state.
- Settings & Location owns validated sound/volume values; Android owns
  platform permission/policy; no duplicate validation, direct store access or
  Settings-to-Timer write bypass is authorized.
- No reboot recovery, new permission, live credential, event bus, backend,
  cloud/account boundary, new dependency or unaccepted product control is
  introduced.

## Direct normative inputs

- [.memory-bank/features/FT-007-overdue-alert.md](../../.memory-bank/features/FT-007-overdue-alert.md)
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
- [.memory-bank/tasks/TASK-008-T3-FT-006-W7.task.json](../../.memory-bank/tasks/TASK-008-T3-FT-006-W7.task.json)

## Handoff

After this task-plan surface is accepted, the immediate route is
`/review-tasks-plan FT-007`; execution, `/mb-doctor`, `/verify`,
`/red-verify` and `/mb-sync` are not part of this planning run.
