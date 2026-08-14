---
description: Execution plan for TASK-029-T3-FT-001-W26.
status: active
---
# Plan — TASK-029-T3-FT-001-W26

## Goal

Implement and prove the bounded idle Main Display visual correction: adaptive
clock dominance, transparent per-preset neon gradient borders with preserved
identities, and the requested card hierarchy/gap refinement.

## Non-goals

- No Timer & Alert, Weather Context, Platform Runtime, resource, asset,
  lifecycle, task-card, checkpoint or target-runtime change.
- No active countdown/overdue behavior change and no device/runtime `PASS`.

## Inputs / source specs

- Task record: `.memory-bank/tasks/TASK-029-T3-FT-001-W26.task.json`
- Feature/AC: `FT-001-AC-002`; REQs `REQ-002`, `REQ-005`, `REQ-023`.
- Direct canonical inputs: architecture, Boundary Map, Capability Interfaces,
  Weather Card Presentation, Platform Runtime, Lifecycle Map, Runtime
  Verification, Testing Strategy, Invariants and Tier Policy.
- W24/W25 closure evidence is prerequisite context, not W26 RED.

## Constraints / invariants (MUST / NEVER)

- MUST keep four ordered display-ready cards and existing weather content,
  freshness, palette, day/night and pressure ownership.
- MUST preserve preset order, labels, color identity, selected/active styling,
  touch routing, countdown/overdue/audio and gesture semantics.
- MUST use existing Android primitives and no fixed product dp, ratio or
  gradient stops.
- NEVER write outside the two-file hard boundary; never use emulator/device,
  adb, network or credentials.

## Scope

### In scope

- `DisplayCapability.kt` Main Display layout/style and its existing geometry
  helpers.
- `DisplayProjectionTest.kt` focused claim-equivalent measurement/regression
  support.

### Out of scope

- Neighbor capabilities, resources/assets, lifecycle/task state/checkpoint,
  Memory Bank and `/mb-sync`.

## Preflight-confirmed change surface

- Expected hints kept: exactly the two task boundary files.
- Additional same-outcome files/areas: none.
- Hard `write_boundary`: present and satisfied.
- `forbidden_scope` / stop-condition check: clear.

## Applicable quality gates

- [x] `./gradlew :app:testDebugUnitTest --tests com.hozayushka.app.DisplayProjectionTest` — focused display proof.
- [x] `./gradlew testDebugUnitTest` — full host regression.
- [x] `./gradlew clean assembleDebug` — clean Android debug build.
- [x] `git diff --check` — static diff integrity.

## Claim-linked RED / GREEN (T2/T3)

- applicability: applicable for `FT-001-AC-002 / REQ-002`, `REQ-005`, and
  material visual `REQ-023`; timer/weather claims use accepted regression
  alternatives.
- planned probe: deterministic host geometry/style model at 1280×720 and one
  bounded alternate host layout, with same-size RED/GREEN contact sheet.
- observable RED: current source/model reported W24-era `176` clock, `4`
  preset gap, `16` card gap, filled preset interiors and solid strokes.
- corresponding GREEN: same model reports adaptive `188.75` target / `139.75`
  alternate clock, transparent distinct gradient borders, equal non-Today
  allocation, Today larger, relational about-20%-smaller intent and `24`
  uniform gaps.
- T3 isolation/cleanup: host-only, disposable task evidence; no runtime or
  external state. No execute reuse candidate is offered due to broad dirty
  provenance.

## MB-SYNC handoff / owner

- Owner: `/verify`, then T3 `/red-verify` and explicit lifecycle owner.
- `.memory-bank/` docs needing update: none in this bounded execution;
  `/mb-sync` explicitly forbidden by the operator.
- Task registry/status update owner: lifecycle owner; not `/exe`.

## Definition of done

- Fresh RED precedes production change; claim-equivalent GREEN and all task
  gates are recorded; actual changed files stay within boundary; target runtime
  is explicitly `DEFERRED`; handoff is `PASS_FOR_HANDOFF` or bounded failure.
