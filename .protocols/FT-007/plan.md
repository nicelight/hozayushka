---
description: Planning surface for the FT-007 W35 active-countdown containment boundary.
status: active
last_updated: 2026-08-14
---
# FT-007 — W35 feature plan

## Current W35 route

### Objective

Plan one cohesive Main Display follow-up for the operator-observed active
countdown presentation defect. The complete formatted remaining value must fit
inside the dedicated timer surface at the accepted landscape geometry, remain
readable without clipping or overlap, preserve the activating preset's neon
color and separate timer rail, and keep weather/date/city and standard
card-shell content hidden during countdown.

The route owns presentation sizing and containment only. It does not change
countdown arithmetic, preset configuration, gesture semantics, overdue state,
alert audio or platform policy.

### Accepted basis

- Feature: [.memory-bank/features/FT-007-overdue-alert.md](../../.memory-bank/features/FT-007-overdue-alert.md)
- Epic: [.memory-bank/epics/EP-003-timers-alert.md](../../.memory-bank/epics/EP-003-timers-alert.md)
- W35 owned acceptance: `FT-007-AC-007 / REQ-012 / REQ-023`
- Read-only regressions: `REQ-013` cancellation, `REQ-015` overdue
  presentation/dismissal and `REQ-016` alert audio policy
- Global Backbone: `complete`, Planning Revision `2`
- Foundation Gate: `TASK-002-T3-FT-000-W1`, status `done`
- Sole direct predecessor: `TASK-037-T3-FT-001-W34`, status `done`
- Historical FT-007 presentation owner: `TASK-031-T3-FT-007-W28`, status `done`
- Operator RED reference: `/tmp/hozayushka-physical-smoke-countdown-2026-08-14.png`

### Queue and dependency interpretation

| Order | Task | Tier | Wave | Status | Depends on | Role |
|---|---|---|---|---|---|---|
| 1 | `TASK-009-T3-FT-007-W8` | T3 | W8 | done | `TASK-008-T3-FT-006-W7` | Historical AC-001..005 overdue baseline |
| 2 | `TASK-026-T3-FT-007-W23` | T3 | W23 | done | `TASK-009-T3-FT-007-W8` | Historical audio integration owner |
| 3 | `TASK-031-T3-FT-007-W28` | T3 | W28 | done | `TASK-030-T3-FT-006-W27` | Completed AC-006 overdue presentation |
| 4 | `TASK-038-T3-FT-007-W35` | T3 | W35 | planned | `TASK-037-T3-FT-001-W34` | New AC-007 active-countdown containment |

W35 has exactly one direct dependency: successful W34. W34 is the current
successful Main Display baseline and already carries the accepted W28 route
transitively; W8/W23/W28 remain historical regression context, not duplicated
ownership or additional direct predecessors. W31/W34 remain `done`, W32
remains `failed`, and W33 remains `blocked`.

### Acceptance ownership

- W8 retains `FT-007-AC-001` through `FT-007-AC-005` baseline ownership.
- W23 retains its completed audio request/start/repeat/stop and denial/error
  delta under AC-004/AC-005.
- W28 retains completed `FT-007-AC-006` overdue presentation ownership; W35
  does not duplicate its fullscreen overdue outcome.
- W35 owns only `FT-007-AC-007 / REQ-012 / REQ-023`.
- FT-006/Timer & Alert retains remaining-time arithmetic, one-active-timer,
  countdown cancellation and lifecycle recovery ownership. W35 checks those
  paths read-only where the presentation branch can regress them.

### Bounded execution path

`existing Timer & Alert countdown projection → Main Display active countdown
surface → full-string geometry/projection receipt → host RED/GREEN at
1280×720 and 2460×1080 → conditional authorized TECNO physical RED/GREEN`.

The host receipt must measure representative complete formatted values in both
accepted forms (`MM:SS` and `HH:MM:SS`) against the actual available surface,
including text/ink bounds and clipping/overlap. A scalar text-size comparison
alone is insufficient. The projection must retain the existing preset color,
transparent neon ring, separate rail and hidden weather/date/city/card content.

### Canonical SDD coverage

All applicable concerns reuse registered subject-based specs. No new canonical
spec, module, graph edge, public contract, event boundary or behavior-spec file
is created.

| Concern | Action | Canonical basis | W35 role |
|---|---|---|---|
| Main Display ownership/topology | `reuse` | [Boundary Map modules](../../.memory-bank/contracts/boundary-map.md#modules), [Dependency Graph](../../.memory-bank/contracts/boundary-map.md#dependency-graph), [Accepted Ownership Summary](../../.memory-bank/contracts/boundary-map.md#accepted-ownership-summary) | Main Display owns the presentation-only correction. |
| Main Display presentation/geometry | `reuse` | [Clock hero](../../.memory-bank/contracts/main-display-presentation.md#clock-hero), [Timer rail](../../.memory-bank/contracts/main-display-presentation.md#timer-rail), [Measurement and tolerance](../../.memory-bank/contracts/main-display-presentation.md#measurement-and-tolerance-method), [Claim-linked evidence](../../.memory-bank/contracts/main-display-presentation.md#claim-linked-evidence) | Full countdown-string bounds, containment and rail separation use the existing measurement contract. |
| Timer projection and gestures | `reuse` | [Main Display to Timer and Alert](../../.memory-bank/contracts/capability-interfaces.md#main-display-to-timer-and-alert) | Existing projection, cancel and any-tap command remain read-only regression obligations. |
| Timer lifecycle/audio runtime | `reuse` | [Timer lifecycle](../../.memory-bank/states/lifecycle-map.md#timer-lifecycle), [Timer and Audio Runtime Boundary](../../.memory-bank/contracts/platform-runtime.md#timer-and-audio-runtime-boundary) | Overdue transition and audio request/stop policy remain unchanged. |
| Verification and target separation | `reuse` | [Deterministic Host-Side Checks](../../.memory-bank/testing/runtime-verification.md#deterministic-host-side-checks), [Target-Device Evidence](../../.memory-bank/testing/runtime-verification.md#target-device-evidence) | Host proof is mandatory; TECNO physical proof is conditional and otherwise `DEFERRED`. |

### Scope

#### In scope

- Active countdown sizing/placement inside the existing Main Display timer
  surface in `DisplayCapability.kt`.
- Claim-linked geometry/projection proof in `DisplayProjectionTest.kt` for
  complete `MM:SS` and `HH:MM:SS` strings at `1280×720` and `2460×1080`.
- Preservation checks for the existing preset color/ring, separate rail,
  hidden weather/date/city/card content and no clipping/overlap.
- Read-only host regressions for countdown cancellation, overdue presentation/
  dismissal and alert audio behavior.
- Conditional physical RED/GREEN on the authorized TECNO serial, or honest
  physical `DEFERRED` evidence with residual risk.

#### Out of scope

- TimerCapability, TimerAlertPolicy, PlatformRuntimeAdapter, audio code,
  elapsed arithmetic, persistence, lifecycle transitions or gesture semantics.
- W8/W23/W28 evidence/status/history and W31/W32/W33/W34 task identity,
  evidence, protocols or lifecycle history.
- Preset configuration, Settings, weather/provider/data behavior, resources,
  composition-root wiring, dependencies, permissions, new event boundaries or
  new modules.
- Fixed dp/font/surface ratios or gradient targets not already accepted by the
  canonical presentation contract.
- Any overdue or audio physical-smoke claim from the current countdown
  screenshot; overdue was not tested in that observation.

### Gates and claim-linked proof

- `./gradlew clean assembleDebug` — clean Android debug build.
- `./gradlew :app:testDebugUnitTest --tests com.hozayushka.app.DisplayProjectionTest` — focused projection/geometry and read-only regressions.
- `./gradlew testDebugUnitTest` — full host regression suite.
- `./gradlew lintDebug` — project-native Android lint.
- `node scripts/mb-lint.mjs && git diff --check` — Memory Bank/schema and diff integrity.

Fresh host RED/GREEN is required at both canonical render sizes. The receipt
must record the exact formatted string, surface bounds, measured text/ink
bounds, decisive containment/clipping result, preset color, rail bounds and
hidden-content result. A named visual-QA rubric records PASS/FAIL for full
readability, containment, hierarchy, color identity, hidden content, rail
separation and host/device separation.

The current TECNO screenshot is a pre-write physical RED reference only. A
future execution may capture fresh physical RED/GREEN using only authorized
serial `1156725456009666`; if unavailable, record
`PHYSICAL_COUNTDOWN_READABILITY=DEFERRED` plus unavailable-target condition and
residual risk. Host geometry is never runtime PASS and no emulator/AVD/QEMU is
valid. Existing overdue/cancel/audio proof remains host regression evidence;
this planning route does not test or claim overdue behavior.

If containment requires a new fixed visual target, a new owner/boundary, or a
Timer & Alert/audio/lifecycle change, stop and route to `/feature-doctor FT-007`,
`/feature-to-tasks FT-007` or `/spec-design` as applicable; do not choose the
missing product target silently.

### Planning handoff

Queue action is `created`: create `TASK-038-T3-FT-007-W35` as `planned`, index it
once, and preserve all historical task/status/evidence/checkpoint/terminal
state. The immediate next owner is fresh `/review-tasks-plan FT-007`;
execution, `/verify`, `/red-verify`, `/mb-doctor`, `/mb-sync`, emulator and
device work are outside this planning run.

## Historical W28 objective

Reconcile one bounded W28 Main Display visual follow-up for the accepted
overdue projection. W28 renders a dedicated no-weather/no-city/no-date/
no-standard-card surface, preserves the full elapsed counter and blinking `+`,
uses the activating preset's existing color identity in a transparent neon
circle, and proves the visual hierarchy without changing timer, lifecycle or
audio behavior.

## Historical W28 accepted basis

- Feature: [.memory-bank/features/FT-007-overdue-alert.md](../../.memory-bank/features/FT-007-overdue-alert.md)
- Epic: [.memory-bank/epics/EP-003-timers-alert.md](../../.memory-bank/epics/EP-003-timers-alert.md)
- W28 owned acceptance: `FT-007-AC-006 / REQ-015 / REQ-023`
- W28 read-only regression context: `REQ-014` lifecycle and `REQ-016` audio
- Global Backbone: `complete`, Planning Revision `2`
- Foundation Gate: `TASK-002-T3-FT-000-W1`, status `done`
- Sole direct predecessor: `TASK-030-T3-FT-006-W27`, status `done`
- Historical/read-only audio owner: `TASK-026-T3-FT-007-W23`, status `done`
- Clarified PRD: `clarification_status: complete`

## Historical W28 queue and dependency interpretation

| Order | Task | Tier | Wave | Status | Depends on | Role |
|---|---|---|---|---|---|---|
| 1 | `TASK-009-T3-FT-007-W8` | T3 | W8 | done | `TASK-008-T3-FT-006-W7` | Historical AC-001..005 baseline owner |
| 2 | `TASK-026-T3-FT-007-W23` | T3 | W23 | done | `TASK-009-T3-FT-007-W8` | Historical audio integration owner |
| 3 | `TASK-031-T3-FT-007-W28` | T3 | W28 | done | `TASK-030-T3-FT-006-W27` | Completed Main Display AC-006 owner |

W28 depends directly only on W27. W8 and W23 remain historical/transitive
context and read-only regression owners; neither is a direct W28 predecessor or
new W28 owner. The existing registry entry remains identity-only and no task
status, dependency field, lifecycle/REQ state, checkpoint or terminal evidence
is changed here.

## Historical W28 acceptance ownership

- W8 retains the completed baseline ownership of
  `FT-007-AC-001` through `FT-007-AC-005`.
- W23 retains its completed request/start/repeat/stop and denial/error audio
  integration delta under AC-004/AC-005; W28 does not claim its proof.
- W28 owns only `FT-007-AC-006 / REQ-015 / REQ-023`.
- FT-006 retains elapsed arithmetic, countdown/cancellation and temporary
  interruption recovery ownership; W28 consumes those projections read-only.

The feature acceptance text and all AC locators remain unchanged. No new
requirement or fixed visual ratio is introduced.

## Historical W28 boundary and execution path

The exact W28 change surface is:

- `app/src/main/kotlin/com/hozayushka/app/display/DisplayCapability.kt`
- `app/src/test/kotlin/com/hozayushka/app/DisplayProjectionTest.kt`

The bounded path is:

`existing overdue projection → Main Display overdue composition →
DisplayProjectionTest same-size idle/W27-active/overdue comparison`.

Main Display consumes the registered Timer & Alert projection and submits the
existing any-tap command. TimerCapability, TimerAlertPolicy,
PlatformRuntimeAdapter, audio policy and timer lifecycle/recovery are
read-only regression owners. W28 does not calculate elapsed time, write timer
state, dispatch audio, alter dismissal, add a lifecycle state or use a private
store.

## Historical W28 canonical SDD coverage

All applicable concerns reuse registered subject-based specs; no new canonical
specification, module, dependency, graph edge, public contract, event boundary
or behavior-spec file is created.

| Concern | Action | Canonical basis | W28 role |
|---|---|---|---|
| Main Display ownership/topology | `reuse` | [Boundary Map modules](../../.memory-bank/contracts/boundary-map.md#modules), [Dependency Graph](../../.memory-bank/contracts/boundary-map.md#dependency-graph), [Accepted Ownership Summary](../../.memory-bank/contracts/boundary-map.md#accepted-ownership-summary) | Main Display owns only overdue composition. |
| Timer projection and dismissal | `reuse` | [Main Display to Timer and Alert](../../.memory-bank/contracts/capability-interfaces.md#main-display-to-timer-and-alert) | Existing projection/any-tap contract is read-only. |
| Display runtime | `reuse` | [Display Runtime Boundary](../../.memory-bank/contracts/platform-runtime.md#display-runtime-boundary) | Existing display policy remains unchanged. |
| Timer/audio runtime | `reuse` | [Timer and Audio Runtime Boundary](../../.memory-bank/contracts/platform-runtime.md#timer-and-audio-runtime-boundary) | PlatformRuntimeAdapter and audio behavior are regression-only. |
| Timer lifecycle | `reuse` | [Timer Lifecycle](../../.memory-bank/states/lifecycle-map.md#timer-lifecycle), [Timer State Contract](../../.memory-bank/states/lifecycle-map.md#timer-state-contract) | Existing state transitions/recovery are regression-only. |
| Verification | `reuse` | [Deterministic Host-Side Checks](../../.memory-bank/testing/runtime-verification.md#deterministic-host-side-checks), [Target-Device Evidence](../../.memory-bank/testing/runtime-verification.md#target-device-evidence) | Host proof required; target/device/audio `DEFERRED`. |

## Historical W28 scope

### In scope

- Dedicated overdue composition and hierarchy in `DisplayCapability.kt`.
- Claim-linked host proof in `DisplayProjectionTest.kt`.
- Same-size comparison of idle, W27 active countdown and overdue.
- Content exclusion, full elapsed value, stable numeric result, blinking `+`,
  transparent circle, activating preset color and clipping/overlap checks.
- Read-only regression checks for any-tap, lifecycle projection and audio
  separation.

### Out of scope

- TimerCapability, TimerAlertPolicy, PlatformRuntimeAdapter or any audio code.
- Elapsed arithmetic, persistence, lifecycle transitions, recovery,
  one-active-timer behavior or any-tap command semantics.
- W8/W23/W27 task cards, evidence, protocols, status or history.
- Resources, Settings, weather, network, composition-root wiring, dependencies,
  permissions, event/message boundaries or runtime target launches.
- Fixed dp, font-size, surface-size or gradient-stop targets when existing
  geometry cannot satisfy the accepted comparison.

## Historical W28 gates and claim-linked proof

- `./gradlew clean assembleDebug` — clean Android build.
- `./gradlew :app:testDebugUnitTest --tests com.hozayushka.app.DisplayProjectionTest` — focused W28 projection/regression suite.
- `./gradlew testDebugUnitTest` — host regression suite.
- `node scripts/mb-lint.mjs && git diff --check` — schema/Memory Bank and diff integrity.

Fresh host RED/GREEN is required for `FT-007-AC-006` at one render size. The
artifact must record dedicated-surface content exclusion, elapsed/idle/active
hierarchy, transparent circular backdrop, activating preset color, blinking
plus, numeric stability and clipping/overlap. A named visual-QA rubric records
PASS/FAIL, decisive observation and artifact locator for each criterion.

Any-tap dismissal, Timer & Alert lifecycle and audio-policy behavior are
read-only regression gates. Boundary inspection must show no writes to
TimerCapability, TimerAlertPolicy or PlatformRuntimeAdapter and no adoption of
W8/W23 proof. Target/device/audio evidence is `DEFERRED` with residual
readability/custom-ROM/audio risk; host geometry or fake audio is never a
runtime or physical-audibility `PASS`.

If satisfying the active-countdown comparison requires a new fixed visual
target, clipping/overlap or a contract/owner change, stop and route to
`/feature-doctor FT-007` or `/feature-to-tasks FT-007` as applicable. Do not
weaken lifecycle/audio contracts or choose a ratio silently.

## Historical W28 normative inputs

- [.memory-bank/features/FT-007-overdue-alert.md](../../.memory-bank/features/FT-007-overdue-alert.md)
- [.memory-bank/epics/EP-003-timers-alert.md](../../.memory-bank/epics/EP-003-timers-alert.md)
- [.memory-bank/spec-backbone.md](../../.memory-bank/spec-backbone.md)
- [.memory-bank/spec-index.md](../../.memory-bank/spec-index.md)
- [.memory-bank/requirements.md](../../.memory-bank/requirements.md)
- [.memory-bank/prd.md](../../.memory-bank/prd.md)
- [.memory-bank/invariants.md](../../.memory-bank/invariants.md)
- [.memory-bank/architecture/system-architecture.md](../../.memory-bank/architecture/system-architecture.md)
- [.memory-bank/contracts/boundary-map.md](../../.memory-bank/contracts/boundary-map.md)
- [.memory-bank/contracts/capability-interfaces.md](../../.memory-bank/contracts/capability-interfaces.md)
- [.memory-bank/contracts/platform-runtime.md](../../.memory-bank/contracts/platform-runtime.md)
- [.memory-bank/states/lifecycle-map.md](../../.memory-bank/states/lifecycle-map.md)
- [.memory-bank/testing/runtime-verification.md](../../.memory-bank/testing/runtime-verification.md)
- [.memory-bank/workflows/tier-policy.md](../../.memory-bank/workflows/tier-policy.md)
- [.memory-bank/tasks/TASK-026-T3-FT-007-W23.task.json](../../.memory-bank/tasks/TASK-026-T3-FT-007-W23.task.json)
- [.memory-bank/tasks/TASK-030-T3-FT-006-W27.task.json](../../.memory-bank/tasks/TASK-030-T3-FT-006-W27.task.json)
- [.memory-bank/tasks/TASK-031-T3-FT-007-W28.task.json](../../.memory-bank/tasks/TASK-031-T3-FT-007-W28.task.json)

## Historical W28 handoff

The prior W28 plan is retained as provenance only. W28 is now terminal `done`
under its recorded executor, functional and semantic evidence; its direct W27
dependency and historical evidence remain unchanged. The current planning
handoff is the W35 route above.
