---
description: Execution plan for TASK-028-T3-FT-002-W25.
status: active
---
# Plan — TASK-028-T3-FT-002-W25

## Goal

Reduce the existing six-state Main Display illustration envelopes at 223×444
and 279×444, moderately enlarge the CLEAR sun disk while keeping its whole
envelope reduced, and render projection-supplied pressure direction/count with
visible measured Canvas/Path arrows.

## Non-goals

No WeatherCapability/provider/selection/cache/history/refresh/day-night,
forecast, timer/clock/audio/gesture/lifecycle, resource/asset/dependency,
network/credential, public-contract, module, composition-root, task-state or
checkpoint change. The separate forecast Unicode illustration path is out of
scope.

## Inputs / source specs

- Task record: `.memory-bank/tasks/TASK-028-T3-FT-002-W25.task.json`
- Feature/AC: `.memory-bank/features/FT-002-weather-cards-context.md#FT-002-AC-009`
- REQ IDs: `REQ-005`, `REQ-006`, `REQ-007`, `REQ-008`, `REQ-022`, `REQ-023`, `REQ-025`, `REQ-026`, `REQ-029`
- Direct canonical display, boundary, provider, runtime, lifecycle and testing specs are recorded in `context.md`.

## Scope

### In scope

- `DisplayCapability.kt`: local measured drawing adjustment and Canvas/Path
  pressure arrow view in the existing card composition.
- `DisplayProjectionTest.kt`: focused state, geometry, pressure-direction/count,
  stroke-contract and regression assertions.
- `.tasks/TASK-028-T3-FT-002-W25/`: RED/GREEN measurements/contact sheets,
  rubric, host/static and deferred-target evidence.

### Out of scope

All paths listed in the task `forbidden_scope`, including `.memory-bank/`, task
registry/status, scheduler checkpoint, lifecycle/RTM state, terminal state,
WeatherCapability and its subtree, resources/assets, device/network/credentials.

## Proposed changes

### Touched areas (hypotheses OK)

- `DisplayCapability.kt` — tune only the existing illustration Canvas drawing
  scale and replace Main Display pressure TextViews with a measured Path view.
- `DisplayProjectionTest.kt` — preserve existing behavior and add cheap focused
  assertions for the W25 drawing contract.

### Preflight-confirmed change surface

- Expected hints kept: yes; exactly the two hard-boundary source/test files.
- Additional same-outcome files/areas: task-local protocol/evidence only.
- Hard `write_boundary` present and satisfied: yes.
- `forbidden_scope` / stop-condition check: clear.

## Applicable quality gates

- [ ] `./gradlew :app:testDebugUnitTest --tests com.hozayushka.app.DisplayProjectionTest` — focused display contract.
- [ ] `./gradlew clean assembleDebug` — clean Android debug build.
- [ ] `./gradlew testDebugUnitTest` — complete host unit suite.
- [ ] `git diff --check` — static diff integrity.
- [ ] source/resource/diff boundary inspection — no forbidden production/test/resource scope.

## Claim-linked RED / GREEN (T2/T3)

- applicability: applicable.
- accepted claim locators: `FT-002-AC-009 / REQ-005, REQ-022, REQ-023, REQ-026`; renderer integration `REQ-008 / weather-card-presentation.md#pressure-trend-and-fallback-rules`; material `REQ-006 / #temperature-and-glass-rules`.
- planned probe and environment: deterministic 223×444 and 279×444 SVG/PNG-equivalent contact sheets, measured geometry JSON, focused host assertions and static source inspection; host-only and offline.
- observable RED: current source uses full W22 illustration envelope and Unicode/TextView pressure glyphs; baseline artifacts will be captured before production writes.
- corresponding GREEN: each final state is ≤90% of its matching RED width/height, CLEAR disk is 1.15–1.30× baseline diameter, Path arrows are visible with 4–8 px row-card stroke, count zero paints no arrow pixels, and card/projection regressions pass.
- accepted not-applicable regressions: WeatherCapability/provider/cache/history/freshness, timer/audio/gesture/lifecycle, resources/secrets/network use static/source and host alternative proof because meaningful RED would require forbidden behavior changes.
- T3 isolation, safe rerun, cleanup and permission boundary: only local deterministic host probes and the two hard-boundary source/test writes; no external state or secret-bearing input.

## MB-SYNC handoff / owner

- Owner identified: none in this `/exe` handoff; `/verify` and required T3
  `/red-verify` remain due. User explicitly forbids `/mb-sync`.
- Explicit standalone closure owner: none recorded by this execution.
- `.memory-bank/` docs needing update: none; no WHY/WHERE contract changed.
- Task registry/status/checkpoint/lifecycle owner: unchanged and external.

## Definition of done

Fresh RED/GREEN, measured bounds/contact sheets/rubric, focused/full host gates,
static diff/boundary evidence and deferred-target note are recorded. Handoff is
`PASS_FOR_HANDOFF` only if the bounded host outcome is evidenced; no task closure
or runtime/device PASS is claimed.
