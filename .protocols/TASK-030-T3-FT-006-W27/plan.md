---
description: Execution plan for TASK-030-T3-FT-006-W27.
status: active
---
# Plan — TASK-030-T3-FT-006-W27

## Goal

Present an active validated countdown on a dedicated Main Display surface that
contains no weather cards, city, date or standard card shell; makes countdown
digits materially larger than the final idle clock in the same host geometry;
uses a transparent circular neon backdrop with the activating preset's existing
color identity; and preserves selected/active preset indication and accepted
Timer & Alert behavior.

## Non-goals

- No TimerCapability, TimerAlertPolicy or PlatformRuntimeAdapter changes.
- No overdue visual/audio/permission/policy behavior, resources, assets,
  lifecycle owner, timer arithmetic, storage, network/provider or composition
  root changes.
- No new module, public contract, dependency edge, event path or product
  numeric choice requiring `/feature-doctor`.
- No emulator, device, adb, network, credentials or audio runtime evidence;
  target/device/audio remains `DEFERRED`.

## Inputs / source specs

- Task record: `.memory-bank/tasks/TASK-030-T3-FT-006-W27.task.json`
- Task index: `.memory-bank/tasks/index.json`
- Feature/Epic: `.memory-bank/features/FT-006-countdown-lifecycle.md`,
  `.memory-bank/epics/EP-003-timers-alert.md`
- REQ IDs: `REQ-011`, `REQ-012`, `REQ-013`, `REQ-014`, `REQ-023`, `REQ-025`

## Richer execution inputs

- `.memory-bank/contracts/capability-interfaces.md#main-display-to-timer-and-alert`
- `.memory-bank/contracts/boundary-map.md#dependency-graph`
- `.memory-bank/contracts/platform-runtime.md#display-runtime-boundary`
- `.memory-bank/states/lifecycle-map.md#timer-state-contract`
- `.memory-bank/testing/runtime-verification.md#deterministic-host-side-checks`
- `.memory-bank/workflows/tier-policy.md#claim-linked-red--green-for-t2t3`
- W26/W23 closure protocols, FT-006 plan/decision and W27 task-plan APPROVE.

## Constraints / invariants (MUST / NEVER)

- MUST keep Main Display as composition/gesture consumer of Timer & Alert data.
- MUST keep one active timer, single-tap hint, double-tap cancellation,
  temporary interruption recovery, network independence and accepted overdue
  ownership intact.
- MUST preserve activating preset color identity and selected/active styling.
- NEVER change weather/card ownership except hiding those elements on the
  dedicated active-countdown surface.
- NEVER choose a fixed dp, ratio, font ratio or neon gradient stop when the
  accepted relational outcome can be met without it.

## Scope

### In scope

- `app/src/main/kotlin/com/hozayushka/app/display/DisplayCapability.kt`
- `app/src/test/kotlin/com/hozayushka/app/DisplayProjectionTest.kt`

### Out of scope

- All other production/test/resource/task/history/protocol/checkpoint paths;
  protocol and task-local evidence files are only framework-owned execution
  bookkeeping required by `/exe`.

## Proposed changes

### Touched areas

- `DisplayCapability.kt` — project active countdown into a dedicated visual
  surface and retain existing timer/click contracts.
- `DisplayProjectionTest.kt` — add deterministic geometry/contact-sheet/rubric
  assertions and preserve lifecycle/gesture/offline regressions through existing
  host fixtures.

### Preflight-confirmed change surface

- Expected hints kept: exactly the two task boundary files.
- Additional same-outcome files/areas: none.
- Hard `write_boundary` present and satisfied: yes (production/test outcome).
- `forbidden_scope` / stop-condition check: clear.
- Existing unrelated dirty files: present and preserved; target files are
  already dirty from W26 and will be edited only for W27 delta.

## Applicable quality gates

- [ ] `./gradlew :app:testDebugUnitTest --tests com.hozayushka.app.DisplayProjectionTest` — focused display/lifecycle regression.
- [ ] `./gradlew testDebugUnitTest` — full host regression.
- [ ] `./gradlew clean assembleDebug` — clean Android debug build.
- [ ] `node scripts/mb-lint.mjs && git diff --check` — integrity/static boundary.

## Claim-linked RED / GREEN (T3)

- applicability: applicable for AC-001 visual presentation; accepted
  `RED_NOT_APPLICABLE` alternatives for AC-002–AC-005 and REQ-016.
- accepted claim locators: `FT-006-AC-001 / REQ-012`; material visual NFR
  `REQ-023`; regression claims `FT-006-AC-002` through `FT-006-AC-005`.
- planned probe: same rendered-size deterministic host geometry/render model
  with idle and active countdown states, plus isolated TimerCapability host
  fixtures already in `DisplayProjectionTest`.
- observable RED: current countdown is embedded in standard header; weather,
  city, date and cards remain present; countdown size is below final idle clock;
  no dedicated circular preset-colored surface exists.
- corresponding GREEN: active countdown surface has explicit excluded content,
  larger countdown text than idle result, transparent circular neon backdrop
  keyed through existing preset color identity, selected/active indication and
  same accepted timer projection/gesture path.
- T3 isolation/cleanup: host-only deterministic pure geometry/projection
  checks; no persistent state, credentials, network, device or audio side
  effect.

## MB-SYNC handoff / owner

- Owner identified: `/verify`, then `/red-verify` (T3); `/mb-sync` forbidden by
  operator for this execution.
- `.memory-bank/` docs needing update: none under this two-file task; no task
  card/checkpoint/lifecycle/RTM update is authorized.

## Definition of done

- Fresh RED precedes production behavior write; claim-equivalent GREEN and
  deterministic visual/lifecycle artifacts are recorded.
- Focused/full host tests, clean build, `mb-lint`, diff/static boundary checks
  are run where feasible.
- Handoff is `PASS_FOR_HANDOFF` only for host evidence; runtime/device/audio
  remain explicitly deferred and task lifecycle remains owner-managed.
