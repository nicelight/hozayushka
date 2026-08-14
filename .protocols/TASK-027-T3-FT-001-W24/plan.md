---
description: Execution plan for TASK-027-T3-FT-001-W24.
status: active
---
# Plan — TASK-027-T3-FT-001-W24

## Goal

Prove and implement the smallest Main Display-only visual correction: idle
`HH:mm` is the dominant central/upper focal element relative to the W21
baseline, and the three existing right-side preset controls have equal square
bounds and a common effective radius at least half their measured side.

## Non-goals

- No TimerCapability, TimerAlertPolicy, PlatformRuntimeAdapter, audio, W23,
  Weather Context, resources, provider/network, credentials or lifecycle work.
- No changes to labels, colors, selected/active styling, click listeners,
  timer gestures, countdown/overdue/audio behavior, card projection/content,
  public contracts, modules, graph edges or composition-root orchestration.
- No target-device/emulator execution and no lifecycle/checkpoint/terminal
  state or historical task mutation.

## Inputs / source specs

- Task record: `.memory-bank/tasks/TASK-027-T3-FT-001-W24.task.json`
- Task index: `.memory-bank/tasks/index.json`
- Feature/Epic: `FT-001-main-clock-display`, `EP-001-glanceable-display`
- REQ IDs: `REQ-002`, with `REQ-005` and `REQ-023` as regression/material-NFR
  constraints and timer REQs as regression-only.

## Richer execution inputs

- System Architecture, Boundary Map, Capability Interfaces, Weather Card
  Presentation, Platform Runtime, Lifecycle Map, Runtime Verification,
  Invariants and Tier Policy.
- FT-001 W24 plan/decision log, IMPL-FT-001, W21 task history and current
  source/test files.

## Constraints / invariants (MUST / NEVER)

- MUST keep left city/date, central/upper clock, lower four ordered slots and
  right preset composition; Today remains larger and the other three cards
  equal/smaller with existing gaps.
- MUST keep device-time `HH:mm`, date/colon states, city/settings routing,
  projection ownership, gestures and timer/countdown/overdue/audio semantics.
- MUST measure clock and all three existing preset bounds and record decisive
  contact-sheet/rubric evidence.
- NEVER select a new absolute product dp, size or ratio; route material
  qualitative ambiguity to `/feature-doctor FT-001`.
- NEVER write product/test content outside the exact two-file hard boundary.

## Scope

### In scope

- `DisplayCapability.kt`: existing Main Display geometry/presentation only.
- `DisplayProjectionTest.kt`: focused host measurement, render/contact-sheet
  support and regressions inside the existing test boundary.
- Required protocol and task evidence artifacts owned by `/exe`.

### Out of scope

- All neighboring owners, resources, network/provider/device/lifecycle state,
  task cards, Memory Bank durable source, scheduler/checkpoint/terminal state.

## Proposed changes

### Touched areas (hypotheses OK)

- `app/src/main/kotlin/com/hozayushka/app/display/DisplayCapability.kt` —
  visual clock dominance and square/circular preset presentation.
- `app/src/test/kotlin/com/hozayushka/app/DisplayProjectionTest.kt` —
  claim-equivalent deterministic host bounds/contact-sheet/rubric support and
  focused regressions.

### Preflight-confirmed change surface

- Expected hints kept: exactly the two task paths.
- Additional same-outcome files/areas and rationale: none.
- Hard `write_boundary` present and satisfied: yes.
- `forbidden_scope` / stop-condition check: clear.

## Applicable quality gates

- [x] `./gradlew clean assembleDebug` — clean Android debug build; exit `0`.
- [x] `./gradlew testDebugUnitTest` — full host unit/regression suite; exit `0`.
- [x] `git diff --check` — static diff integrity; exit `0`.

## Claim-linked RED / GREEN (T2/T3)

- applicability: applicable
- accepted claim locator(s): `FT-001-AC-002 / REQ-002`
- planned test/probe and environment: deterministic host 1280×720 geometry
  and same-size contact sheet; no device/network/credentials.
- observable RED: fresh current-source W21 baseline with clock bounds
  `[271,24,1028,252]` and preset model bounds `220×224`, radius `18f`.
- corresponding GREEN: same route proves the larger clock style `176 > 132`,
  preserved anchors/cards and three equal square controls with one common
  radius `110 >= half(side)` plus `4/4` whitespace gaps.
- accepted not-applicable reason and alternative proof: weather projection and
  timer/audio lifecycle are not intentionally broken; scoped source inspection
  plus full host suite provide regression proof.
- T3 isolation, safe rerun, cleanup, and permission boundary: host-only
  deterministic disposable artifacts; no runtime state, device, network,
  credentials or secret-bearing evidence; production/test writes only in the
  two hard-boundary files.

## MB-SYNC handoff / owner

Scheduler or an explicit lifecycle owner performs later verification/closure
and sync. `/exe` records handoff only; user explicitly forbids `/verify`,
`/red-verify`, `/mb-sync` and lifecycle changes in this run.

- [ ] Owner identified: none for closure in this run
- [ ] Explicit standalone owner basis recorded: n/a
- [ ] `.memory-bank/` docs needing update: none inside current execution boundary
- [ ] `.memory-bank/index.md` router update needed: no
- [ ] RTM update needed: no
- [ ] Task registry/status update owner: lifecycle owner later
- [ ] Changelog update owner: lifecycle owner only if workflow requires

## Definition of done

- Bounded implementation is reviewed by exact diff.
- Fresh RED and claim-equivalent GREEN bounds/contact sheet/rubric exist.
- Clean build, complete host unit suite and static diff pass.
- Target device is explicitly `DEFERRED` with residual risk.
- Handoff is `PASS_FOR_HANDOFF`-ready for later `/verify` and T3 semantic
  verification, without invoking them here.
