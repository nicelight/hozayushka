---
description: Execution plan for TASK-008-T3-FT-006-W7.
status: active
---
# Plan — TASK-008-T3-FT-006-W7

## Goal
Provide immediate validated-preset countdown start, one active timer, protected cancellation, temporary interruption rehydration, and network-independent overdue dismissal.

## Non-goals
- FT-005 preset validation/defaults/labels/colors/settings ownership.
- FT-007 overdue fullscreen/audio behavior and FT-008/FT-009 settings.
- Reboot recovery, new modules/dependencies/events, backend/cloud, secrets or composition-root business state.

## Inputs / source specs
- Task: `.memory-bank/tasks/TASK-008-T3-FT-006-W7.task.json`
- Feature: `.memory-bank/features/FT-006-countdown-lifecycle.md`
- Requirements: `REQ-011`, `REQ-012`, `REQ-013`, `REQ-014`, `REQ-025`
- Canonical: boundary/capability/platform/local-data/lifecycle/testing specs listed in task `normative_inputs`.

## Constraints / invariants
- MUST keep Timer & Alert as the only active-timer state/persistence owner.
- MUST use Settings' validated preset projection through its public reader.
- MUST keep Main Display as presentation/gesture owner and composition root as wiring only.
- NEVER calculate timer state from Main Display or read private stores from another capability.
- NEVER add overdue rendering/audio, reboot recovery, network dependency, or live credentials.

## Scope
### In scope
- Timer lifecycle projection and gesture transition helpers in existing timer/display capability files.
- Existing Main Display countdown rendering and preset gesture wiring.
- Isolated synthetic timing/gesture/offline tests and task evidence.

### Out of scope
- New public boundary, module, dependency, storage owner, Settings product behavior or alert policy.

## Proposed changes
### Touched areas (hypotheses OK)
- `app/src/main/kotlin/com/hozayushka/app/timer/TimerCapability.kt` — lifecycle commands/projection.
- `app/src/main/kotlin/com/hozayushka/app/display/DisplayCapability.kt` — countdown presentation and gestures.
- `app/src/main/kotlin/com/hozayushka/app/app/FoundationRuntime.kt` — only lifecycle rehydration wiring if required.
- `app/src/test/kotlin/com/hozayushka/app/TimerLifecycleTest.kt` — deterministic claim probes.

### Preflight-confirmed change surface
- Expected hints: kept within existing timer/display/app/test roots.
- Additional same-outcome files: none planned.
- Hard `write_boundary`: not set.
- `forbidden_scope` / stop-condition check: clear.

## Applicable quality gates
- [ ] `./gradlew clean assembleDebug` — clean Android build.
- [ ] `./gradlew testDebugUnitTest` — deterministic FT-006 lifecycle/integration checks.
- [ ] static boundary/redaction inspection — no forbidden ownership bypass or secrets.
- [ ] target-device evidence — deferred/non-blocking when unavailable; no runtime PASS.

## Claim-linked RED / GREEN (T2/T3)
- applicability: applicable
- accepted claim locators: FT-006-AC-001..AC-005 / REQ-011, REQ-012, REQ-013, REQ-014, REQ-025
- planned probe: source-level baseline followed by isolated synthetic timestamp/gesture tests.
- T3 isolation: in-memory stores, fixed timestamps, no provider requests/credentials, reset in `finally`/fresh fixture.

## MB-SYNC handoff / owner
- Owner identified: human/lifecycle owner after `/verify` and `/red-verify`.
- `.memory-bank/` planning/spec/index files: unchanged by this execution.
- Task status update owner: `/exe` only for `ready -> in_progress`; final closure remains outside `/exe`.

## Definition of done
Implementation and current-attempt evidence are ready for `/verify`; TASK-008 remains `in_progress`.
