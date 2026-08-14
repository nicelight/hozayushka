---
description: Implementation plan for the FT-007 W35 Main Display active-countdown containment boundary.
status: active
last_updated: 2026-08-14
---
# IMPL-FT-007 — W35 Main Display active-countdown containment

## Current W35 outcome

Fix the operator-observed active-countdown presentation defect in the existing
Main Display surface: the complete formatted remaining value must fit inside
the available timer surface at the accepted landscape geometry, stay readable
without clipping or overlap, preserve the activating preset's neon color and
separate timer rail, and keep weather/date/city and standard card-shell content
hidden during countdown.

W35 is a presentation-only Main Display task. It consumes the existing Timer &
Alert projection and does not calculate remaining time, write timer state,
change cancellation, render overdue state, dispatch audio or alter platform
policy.

### Queue, ownership and dependency

| Task | Status | Role for W35 |
|---|---|---|
| `TASK-009-T3-FT-007-W8` | `done` | Historical FT-007 AC-001..005 overdue baseline. |
| `TASK-026-T3-FT-007-W23` | `done` | Historical audio integration owner and regression context. |
| `TASK-031-T3-FT-007-W28` | `done` | Completed FT-007-AC-006 overdue presentation; no duplication. |
| `TASK-037-T3-FT-001-W34` | `done` | Sole direct predecessor and latest successful Main Display baseline. |
| `TASK-038-T3-FT-007-W35` | `planned` | Owns FT-007-AC-007 active-countdown containment. |

W35 depends only on W34. W8, W23 and W28 remain historical/regression context;
W31 remains `done`, W32 remains `failed`, W33 remains `blocked`, and W34
remains `done`. No historical card, status, evidence, protocol, scheduler
checkpoint or terminal state is changed.

### Implementation and proof path

The expected two-file surface is:

- `app/src/main/kotlin/com/hozayushka/app/display/DisplayCapability.kt`
- `app/src/test/kotlin/com/hozayushka/app/DisplayProjectionTest.kt`

The bounded path is the existing Timer & Alert countdown projection through the
Main Display active surface and its host geometry/projection tests. The proof
must measure complete `MM:SS` and `HH:MM:SS` strings against the actual surface
at `1280×720` and `2460×1080`, recording text/ink bounds and clipping/overlap;
the existing scalar countdown text-size comparison is not sufficient by
itself. The selected preset's existing color, transparent neon treatment,
separate rail and hidden weather/date/city/card content remain regression
claims.

### Scope and non-goals

In scope: active countdown sizing/placement, full-string containment geometry,
claim-linked host RED/GREEN, named visual-QA review, and read-only cancellation,
overdue and alert regressions. A future execution may capture fresh physical
RED/GREEN only on authorized TECNO LI6 serial `1156725456009666`; if unavailable
it records physical readability as `DEFERRED` with residual risk.

Out of scope: TimerCapability, TimerAlertPolicy, PlatformRuntimeAdapter, timer
arithmetic/persistence/lifecycle, preset configuration, Settings, weather or
provider behavior, resources, composition-root wiring, new dependencies,
permissions, modules, public/event boundaries, fixed font/dp/ratio targets,
overdue physical smoke and all historical task/state/checkpoint mutations.
Emulator/AVD/QEMU is prohibited.

### Canonical inputs and gates

Reuse the registered Main Display presentation contract (`clock-hero`,
`timer-rail`, measurement/tolerance, visual-QA and claim-linked evidence), the
Boundary Map, Main Display → Timer & Alert contract, Timer Lifecycle, Platform
Runtime and Runtime Verification target-separation route. No new canonical
specification or graph edge is needed.

Required execution gates are a clean Android build, focused
`DisplayProjectionTest`, full host tests, `lintDebug`, and
`node scripts/mb-lint.mjs && git diff --check`. The W35 task card is the
authoritative handoff for exact commands, AC/REQ mapping, hard write boundary,
RED/GREEN evidence, physical `PASS|DEFERRED` policy and stop conditions.

## Historical W28 outcome

### Original W28 outcome

Plan one cohesive W28 Main Display presentation refinement: render the
existing overdue projection on a dedicated surface without weather cards,
city, date or standard card-shell content; keep the full elapsed digits stable
and materially stronger than idle and, where the existing geometry permits,
active countdown; preserve the activating preset color in a transparent neon
circle; and keep the blinking `+` and any-tap dismissal available.

The W28 implementation/test surface is exactly:

- `app/src/main/kotlin/com/hozayushka/app/display/DisplayCapability.kt`
- `app/src/test/kotlin/com/hozayushka/app/DisplayProjectionTest.kt`

Main Display consumes the existing Timer & Alert projection. It does not
calculate elapsed time, write timer state, dispatch audio or introduce a new
lifecycle owner.

## Historical W28 reconciled queue and ownership

| Task | Status | Role for W28 |
|---|---|---|
| `TASK-009-T3-FT-007-W8` | `done` | Historical owner of FT-007-AC-001..005 baseline. |
| `TASK-026-T3-FT-007-W23` | `done` | Historical audio request/start/repeat/stop owner and read-only audio regression context. |
| `TASK-030-T3-FT-006-W27` | `done` | The sole direct predecessor for W28; active-countdown presentation reference. |
| `TASK-031-T3-FT-007-W28` | `done` | Completed Main Display owner of FT-007-AC-006 only. |

W28 has exactly one direct dependency: `TASK-030-T3-FT-006-W27`. W8 and W23
remain historical/transitive context and regression owners; neither is a
direct predecessor of W28. The Foundation dependency remains inherited
through the approved W27 chain. No task identity, dependency field, status or
terminal evidence is changed by this plan repair.

TimerCapability, TimerAlertPolicy, PlatformRuntimeAdapter, audio policy and
timer lifecycle/recovery remain read-only regression owners for W28. Any need
to change them is a stop-and-replan condition.

## Historical W28 acceptance ownership

The existing feature acceptance remains unchanged:

- W8 retains ownership of `FT-007-AC-001` through `FT-007-AC-005` baseline
  behavior and proof.
- W23 retains its completed audio integration delta under AC-004/AC-005 and
  remains read-only regression context for W28.
- W28 owns only `FT-007-AC-006 / REQ-015 / REQ-023`; it does not backfill,
  duplicate or reopen W8/W23 proof.

FT-006 remains the owner of countdown arithmetic, one-active-timer behavior,
cancellation and temporary interruption recovery. No new requirement,
acceptance criterion or fixed visual ratio is introduced.

## Historical W28 bounded execution path

The path stops at the Main Display projection:

`existing overdue projection → DisplayCapability overdue branch →
DisplayProjectionTest same-size idle/active/overdue comparison`.

The branch preserves the public Timer & Alert projection, any-tap dismissal,
elapsed arithmetic, lifecycle transitions and audio policy. The proof compares
the existing idle and W27 active-countdown geometry, then checks dedicated
content exclusion, elapsed-digit hierarchy, preset color identity, transparent
circle, blinking `+`, numeric stability and clipping/overlap.

## Historical W28 canonical SDD coverage

All applicable concerns reuse registered subject-based specs; no new spec,
module, edge, public contract, event boundary, resource route or behavior
specification is created.

| Concern | Action | Canonical basis | W28 use |
|---|---|---|---|
| Main Display ownership and graph | `reuse` | [Boundary Map modules](../../contracts/boundary-map.md#modules), [Dependency Graph](../../contracts/boundary-map.md#dependency-graph), [Accepted Ownership Summary](../../contracts/boundary-map.md#accepted-ownership-summary) | Main Display owns only overdue composition; existing Timer & Alert edge is consumed. |
| Display-to-timer contract | `reuse` | [Main Display to Timer and Alert](../../contracts/capability-interfaces.md#main-display-to-timer-and-alert) | Any-tap dismissal and projection input are read-only regression obligations. |
| Display runtime | `reuse` | [Display Runtime Boundary](../../contracts/platform-runtime.md#display-runtime-boundary) | Display policy is an existing boundary; no adapter write is planned. |
| Timer/audio runtime | `reuse` | [Timer and Audio Runtime Boundary](../../contracts/platform-runtime.md#timer-and-audio-runtime-boundary) | PlatformRuntimeAdapter, audio policy and W23 proof are read-only regression owners. |
| Timer lifecycle | `reuse` | [Timer Lifecycle](../../states/lifecycle-map.md#timer-lifecycle), [Timer State Contract](../../states/lifecycle-map.md#timer-state-contract) | Existing overdue projection, dismissal and recovery remain unchanged. |
| Verification | `reuse` | [Deterministic Host-Side Checks](../../testing/runtime-verification.md#deterministic-host-side-checks), [Target-Device Evidence](../../testing/runtime-verification.md#target-device-evidence) | Host visual proof is required; target/device/audio remain `DEFERRED`. |

## Historical W28 scope boundary

### In scope

- The W28 overdue branch in `DisplayCapability.kt`.
- The claim-linked host projection comparison in `DisplayProjectionTest.kt`.
- FT-007-AC-006 visual proof at one fixed render size, without inventing
  fixed dp, font, surface or gradient ratios.
- Read-only regression checks for any-tap dismissal, lifecycle projection and
  audio-policy separation.

### Out of scope

- Changes to TimerCapability, TimerAlertPolicy or PlatformRuntimeAdapter.
- Changes to audio request/start/repeat/stop, built-in signal selection,
  volume, ramp, cap, silent/DND/route policy or physical audibility.
- Changes to elapsed arithmetic, lifecycle transitions, persistence,
  temporary recovery, one-active-timer behavior or any-tap command semantics.
- Changes to W8/W23/W27 task cards, evidence, protocols, status or history.
- Resources, composition-root wiring, Settings, weather, network, new
  dependencies, permissions, event/message boundaries or runtime launches.

## Historical W28 expected change surface

The advisory and hard W28 change surface is exactly:

- `app/src/main/kotlin/com/hozayushka/app/display/DisplayCapability.kt`
- `app/src/test/kotlin/com/hozayushka/app/DisplayProjectionTest.kt`

No other production or test path is an expected W28 change unit. The indexed
W28 task remains the source of the literal executor hard boundary.

## Historical W28 gates, UAT and proof

- `./gradlew clean assembleDebug` — clean Android build.
- `./gradlew :app:testDebugUnitTest --tests com.hozayushka.app.DisplayProjectionTest` — focused W28 projection and read-only regression.
- `./gradlew testDebugUnitTest` — host regression suite.
- `node scripts/mb-lint.mjs && git diff --check` — Memory Bank/schema and diff integrity.
- A named visual-QA rubric records PASS/FAIL for focal hierarchy, dedicated
  content exclusion, preset color identity, transparent circle, blinking `+`,
  stable numeric result, readability, clipping/overlap and host/device
  separation.

W28 evidence must use fresh claim-linked RED/GREEN at one render size and
distinguish idle, W27 active-countdown and overdue results. If the active
comparison requires a new fixed visual target or cannot avoid clipping/overlap,
stop and route to `/feature-doctor FT-007`; do not weaken lifecycle/audio
contracts or choose a ratio silently. Target, physical-device and audio
runtime evidence remain `DEFERRED`; host geometry and fake audio cannot become
runtime or physical-audibility `PASS`.

## Historical W28 constraints and invariants

- Main Display is the sole W28 composition owner; Timer & Alert remains the
  sole timer-state/lifecycle/elapsed/alert owner.
- The overdue numeric counter does not blink; only the `+` may blink.
- Any-tap dismissal remains available even when audio is suppressed.
- No private-store bypass, new lifecycle state, dependency, permission,
  resource pipeline, network path, composition-root orchestration or product
  scope is introduced.
- Planning does not execute code, launch runtime targets, alter task status,
  change lifecycle/REQ state, update checkpoints or write terminal evidence.

## Historical W28 direct normative inputs

- [.memory-bank/features/FT-007-overdue-alert.md](../../features/FT-007-overdue-alert.md)
- [.memory-bank/epics/EP-003-timers-alert.md](../../epics/EP-003-timers-alert.md)
- [.memory-bank/spec-backbone.md](../../spec-backbone.md)
- [.memory-bank/spec-index.md](../../spec-index.md)
- [.memory-bank/requirements.md](../../requirements.md)
- [.memory-bank/prd.md](../../prd.md)
- [.memory-bank/invariants.md](../../invariants.md)
- [.memory-bank/architecture/system-architecture.md](../../architecture/system-architecture.md)
- [.memory-bank/contracts/boundary-map.md](../../contracts/boundary-map.md)
- [.memory-bank/contracts/capability-interfaces.md](../../contracts/capability-interfaces.md)
- [.memory-bank/contracts/platform-runtime.md](../../contracts/platform-runtime.md)
- [.memory-bank/states/lifecycle-map.md](../../states/lifecycle-map.md)
- [.memory-bank/testing/runtime-verification.md](../../testing/runtime-verification.md)
- [.memory-bank/workflows/tier-policy.md](../../workflows/tier-policy.md)
- [.memory-bank/tasks/TASK-026-T3-FT-007-W23.task.json](../../tasks/TASK-026-T3-FT-007-W23.task.json)
- [.memory-bank/tasks/TASK-030-T3-FT-006-W27.task.json](../../tasks/TASK-030-T3-FT-006-W27.task.json)
- [.memory-bank/tasks/TASK-031-T3-FT-007-W28.task.json](../../tasks/TASK-031-T3-FT-007-W28.task.json)

## Historical W28 handoff

W28 is retained as historical provenance and is now terminal `done` under its
recorded executor, functional and semantic evidence. Its direct W27
dependency and all evidence remain unchanged. The current planning handoff is
the W35 route at the top of this document.
