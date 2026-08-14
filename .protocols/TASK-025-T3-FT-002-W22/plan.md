---
description: Execution plan for TASK-025-T3-FT-002-W22.
status: active
---
# Plan — TASK-025-T3-FT-002-W22

## Goal

Render deliberate non-text weather condition silhouettes in the four existing
Main Display cards, with deterministic measured non-overlap against card
temperature/date/pressure content.

## Non-goals

No Weather Context/provider/selection/cache/history/refresh/day-night mapping,
forecast-card, timer/clock/lifecycle, resource/asset/dependency/network/
credential, public-contract, module, composition-root or scheduler change.

## Inputs / source specs

- Task record: `.memory-bank/tasks/TASK-025-T3-FT-002-W22.task.json`
- Feature/AC: `.memory-bank/features/FT-002-weather-cards-context.md#FT-002-AC-009`
- REQ IDs: `REQ-005`, `REQ-007`, `REQ-008`, `REQ-022`, `REQ-023`, `REQ-025`,
  `REQ-026`, `REQ-029`
- Direct canonical display, boundary, provider, runtime, lifecycle and testing
  specs are recorded in `context.md`.

## Scope

### In scope

- `DisplayCapability.kt`: card-local content bounds and an Android `View` that
  draws CLEAR, CLOUD, NEUTRAL_CLOUD, RAIN, SNOW and MOON using Canvas, Path and
  Paint; existing projection and moonPhase inputs are consumed unchanged.
- `DisplayProjectionTest.kt`: deterministic state, geometry, ordering, sizing,
  stale/empty and source-boundary regressions.
- `.tasks/TASK-025-T3-FT-002-W22/`: RED/GREEN, contact sheet, bounds, review,
  boundary/resource, host-gate and target-device evidence.

### Out of scope

All paths listed in the task `forbidden_scope`, including `.memory-bank/`,
task registry/status, scheduler checkpoint, lifecycle/RTM state and target
device state.

## Preflight-confirmed change surface

- Expected hints kept: yes; exactly the two hard-boundary source/test files.
- Additional same-outcome files/areas: task-local protocol/evidence only.
- Hard `write_boundary` present and satisfied: yes.
- `forbidden_scope` / stop-condition check: clear; Canvas/Path/Paint is
  sufficient without an asset/resource pipeline.

## Applicable quality gates

- [ ] `./gradlew clean assembleDebug` — clean Android debug build.
- [ ] `./gradlew testDebugUnitTest` — complete host display/weather regression.
- [ ] `git diff --check` — static diff integrity.

## Claim-linked RED / GREEN (T2/T3)

- applicability: applicable for `FT-002-AC-009 / REQ-005, REQ-022, REQ-023,
  REQ-026`.
- planned probe: deterministic four-card geometry plus task-local host-rendered
  contact sheet and bounds JSON; source inspection for the existing no-
  illustration Main Display RED.
- RED: current `weatherCard` creates only temperature/date/pressure TextViews;
  `projection.illustration` is not rendered by Main Display. See
  `illustration-red-green.md` and `illustration-red-baseline.svg`.
- GREEN: fresh implementation image/contact sheet, measured bounds and host
  assertions for all six state silhouettes and preserved projection semantics.
- accepted not-applicable regressions: provider isolation, timer/lifecycle,
  resource/secret/network paths use source/static/host alternative proof because
  intentionally breaking them is outside the hard scope.
- T3 isolation/safe rerun: only deterministic local source/test/artifact work;
  no device, network, credentials or external state.

## MB-SYNC handoff / owner

- Owner identified: none in this `/exe` handoff; `/verify`, `/red-verify` and
  lifecycle owner remain due. User explicitly forbids `/mb-sync`.
- `.memory-bank/` docs needing update: none; task-specific visual evidence is
  task-local and no WHY/WHERE contract changed.
- Task registry/status update owner: explicit lifecycle owner after required
  verification; unchanged by `/exe`.

## Definition of done

Fresh RED/GREEN and required host/build/static evidence are recorded, target
device remains explicitly `DEFERRED`, and the next owner is given exact files,
commands and residual risks. No task terminal state is changed here.
