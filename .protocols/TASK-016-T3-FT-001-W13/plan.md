---
description: Execution plan for TASK-016-T3-FT-001-W13.
status: active
---
# Plan — TASK-016-T3-FT-001-W13

## Goal

Give Main Display one idempotent, lifecycle-gated 20 Hz scalar ticker and
reuse its four weather-card view tree until the existing weather projection or
card presentation input changes.

## Non-goals

- Weather/provider refresh, cache/history or projection semantics.
- Timer arithmetic/state/cancellation/overdue/audio behavior.
- Forecast ticker/session behavior, gestures, scheduler/status or historical records.
- New module, dependency, public contract, graph edge or event/message boundary.
- Emulator, physical-device, target-ROM, fullscreen/readability or audio evidence.

## Inputs / source specs

- Task record/index: `.memory-bank/tasks/TASK-016-T3-FT-001-W13.task.json`, `.memory-bank/tasks/index.json`
- Feature/Epic: `FT-001`, `EP-001`
- REQ IDs: `REQ-002`, `REQ-003`, `REQ-022`
- Direct SDD: System Architecture, Boundary Map, Capability Interfaces,
  Platform Runtime, Lifecycle Map, Testing Strategy, Runtime Verification
- Workflow: `.memory-bank/workflows/tier-policy.md`

## Constraints / invariants

- MUST keep Main Display as composition/ticker/render owner and MainActivity wiring-only.
- MUST preserve four-card shell, device-time clock/date, accepted colon/countdown projection and existing neighbor contracts.
- MUST start from isolated fake-scheduler state and leave it idle after each host case.
- NEVER touch the forbidden scope or write outside the three-file hard boundary
  for task-owned code changes.

## Scope

### In scope

- `DisplayCapability.kt`: ticker owner, lifecycle gating and weather-card input cache.
- `MainActivity.kt`: forwarding existing pause/resume signals to DisplayCapability.
- `DisplayProjectionTest.kt`: focused fake-scheduler and render-reuse host coverage.

### Out of scope

All paths and behaviors listed in the task card `forbidden_scope`, including
Weather Context, Timer & Alert, Forecast, PlatformRuntimeAdapter,
FoundationRuntime, scheduler/terminal state and historical task artifacts.

## Proposed changes

### Touched areas

- `app/src/main/kotlin/com/hozayushka/app/display/DisplayCapability.kt` — local ticker owner and conditional four-card rebind.
- `app/src/main/kotlin/com/hozayushka/app/app/MainActivity.kt` — lifecycle forwarding only.
- `app/src/test/kotlin/com/hozayushka/app/DisplayProjectionTest.kt` — claim-scoped host probes.

### Preflight-confirmed change surface

- Expected hints kept: all three task `touched_files`.
- Additional same-outcome files/areas: none.
- Hard `write_boundary` present and satisfied: yes.
- `forbidden_scope` / stop-condition check: clear.

## Applicable quality gates

- [ ] `./gradlew clean assembleDebug` — clean Android debug build.
- [ ] `./gradlew testDebugUnitTest` — host scheduler/projection and regression suite.
- [ ] `git diff --check` — static diff integrity.

## Claim-linked RED / GREEN (T2/T3)

- applicability: applicable; T3 task has AC-linked scheduler/render claims.
- accepted claim locators: `FT-001-AC-002 / REQ-002`, `FT-001-AC-003 / REQ-002 / REQ-022`.
- accepted not-applicable claim: `FT-001-AC-004 / REQ-003` has planned
  `RED_NOT_APPLICABLE`; use fresh online/offline/countdown colon regression.
- planned probe: isolated fake scheduler from detached/paused state, duplicate
  attach/resume, pause/detach suppression, one resume/attach loop, scalar ticks,
  unchanged-vs-changed weather render input.
- T3 isolation/cleanup: in-memory fake scheduler only; reset between cases;
  no credentials, persistence, target device or private neighbor state.

## MB-SYNC handoff / owner

Scheduler/explicit lifecycle owner routes this T3 handoff to `/verify`; `/exe`
does not run `/verify`, `/red-verify`, `/mb-sync` or close the task.

- [ ] Owner identified: scheduler/lifecycle owner after verification
- [ ] `.memory-bank/` docs needing update: none for this bounded implementation handoff
- [ ] `.memory-bank/index.md` router update needed: no
- [ ] RTM update needed: no
- [ ] Task registry/status update owner: lifecycle owner; current status remains `in_progress`
- [ ] Changelog update owner: wave sync owner

## Definition of done

Fresh RED/GREEN plus all three required gates are recorded; exact changed files
remain within hard boundary; handoff recommends `/verify TASK-016-T3-FT-001-W13`
and leaves lifecycle open.
