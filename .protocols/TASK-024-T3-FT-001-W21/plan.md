---
description: Execution plan for TASK-024-T3-FT-001-W21.
status: active
---
# Plan — TASK-024-T3-FT-001-W21

## Goal
Recompose the existing Main Display into left/central/right regions while
preserving all existing clock, date, weather, timer, forecast and gesture
contracts.

## Non-goals
- No weather data/content/freshness/palette/pressure changes.
- No timer/countdown/overdue/preset behavior changes.
- No lifecycle, fullscreen, provider, settings, resource, architecture or public-contract changes.
- No device/emulator/network/credential activity and no lifecycle/scheduler/terminal-state mutation.

## Inputs / source specs
- Task record: `.memory-bank/tasks/TASK-024-T3-FT-001-W21.task.json`
- Task index: `.memory-bank/tasks/index.json`
- Feature/Epic: `FT-001`, `EP-001`; regression context `FT-002`
- REQ IDs: `REQ-002`, `REQ-005`, `REQ-023`

## Constraints / invariants
- MUST keep four slots in `yesterday`, `today`, `tomorrow`, `day_after` order.
- MUST place city/date above Yesterday, idle `HH:mm` above Today/Tomorrow/Day-after, and presets on the right.
- MUST allocate Today strictly larger than each of the three equal smaller cards and use one uniform gap greater than 8dp.
- MUST preserve device-time clock/date, accepted colon states, existing capability calls and gesture routing.
- NEVER add textual day/weather labels, touch controls, data sources, new owners/edges/contracts, or writes outside the hard boundary.

## Scope
### In scope
- `DisplayCapability.kt` Main Display view composition and its local geometry projection support.
- `DisplayProjectionTest.kt` focused deterministic geometry regression support.

### Out of scope
- Everything in `forbidden_scope` from the task card, including resources, neighboring capabilities, task/lifecycle records and runtime/device state.

## Proposed changes
### Touched areas
- `app/src/main/kotlin/com/hozayushka/app/display/DisplayCapability.kt` — split existing shell into left city/Yesterday, central clock/three-card and right preset columns; retain existing child renderers and callbacks.
- `app/src/test/kotlin/com/hozayushka/app/DisplayProjectionTest.kt` — assert the relative measured geometry and retain existing clock/colon/city/timer/weather regressions.

### Preflight-confirmed change surface
- Expected hints kept: yes.
- Additional same-outcome files/areas: none.
- Hard `write_boundary` present and satisfied: yes; exactly the two task files for product/test changes.
- `forbidden_scope` / stop-condition check: clear.
- Existing unrelated dirty overlap in `DisplayCapability.kt`: preserved.

## Applicable quality gates
- [ ] `./gradlew clean assembleDebug` — clean Android debug build.
- [ ] `./gradlew testDebugUnitTest` — focused geometry plus complete host regression suite.
- [ ] `git diff --check` — static diff integrity.

## Claim-linked RED / GREEN (T2/T3)
- applicability: applicable for `FT-001-AC-002 / REQ-002`; `REQ-005` and runtime-safety claims use accepted alternative regression/boundary proof.
- accepted claim locator: `FT-001-AC-002 / REQ-002`.
- planned probe: deterministic host geometry model/assertions plus source-derived rendered contact sheets with measured bounds.
- RED: fresh pre-change observation of left/header clock-city-date composition, four equal card allocations and 8dp current gap.
- GREEN: same probe proves left/central/right regions, fixed slot order, Today > three equal smaller cards, and one gap >8dp; retained regression tests cover clock/date, colon, city/settings, timer and weather projection behavior.
- T3 isolation/safe rerun: host-only deterministic tests and task-local SVG artifacts; no external state, secrets, network or device; no cleanup beyond task-local evidence.

## MB-SYNC handoff / owner
`/exe` leaves lifecycle closure to `/verify` and per-task `/red-verify`; user explicitly forbids both and `/mb-sync` in this run.
- Owner identified: human / lifecycle owner after required verification.
- `.memory-bank/` docs needing update: none inside this bounded execution; task protocol/evidence only.
- Task registry/status update owner: unchanged by this run.
- Scheduler checkpoint / terminal state: unchanged by this run.

## Definition of done
Implementation and tests are complete for the accepted geometry delta, required gates are recorded, target-device evidence is explicitly deferred, and the compact `/exe` handoff points to fresh current-attempt evidence for later verification.
