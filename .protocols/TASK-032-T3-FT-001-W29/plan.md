---
description: Execution plan for TASK-032-T3-FT-001-W29.
status: active
---
# Plan — TASK-032-T3-FT-001-W29

## Goal

Repair task-local execution provenance for the bounded W29 Main Display
outcome: exact-size host geometry, four-slot state receipts, preset
radial/rim/glow receipts, named visual rubric, boundary review, host gates and
target-device deferral. Preserve the already-present production behavior.

## Non-goals

- No production or test behavior write in this recovery.
- No changes to task card, task index, status, checkpoint, lifecycle or terminal
  state.
- No W26/W28 history rewrite and no `/verify`, `/red-verify` or `/mb-sync`.
- No emulator/device/adb/network/provider/audio/runtime action.

## Inputs / source specs

- Task record: `.memory-bank/tasks/TASK-032-T3-FT-001-W29.task.json`
- Feature/REQ: `FT-001-AC-002`, `REQ-001`, `REQ-002`, `REQ-005`, `REQ-023`.
- Direct contracts: Boundary Map, Capability Interfaces, Platform Runtime,
  Weather Card Presentation, Lifecycle Map and Runtime Verification.
- Execution rule: `.memory-bank/workflows/tier-policy.md#claim-linked-red--green-for-t2t3`.

## Constraints / invariants (MUST / NEVER)

- MUST keep the exact implementation/test boundary limited to the two task
  files; task-local `.tasks/` and `.protocols/` bookkeeping is workflow output.
- MUST distinguish executor evidence from independent verification and host
  evidence from target/device evidence.
- NEVER backfill a pre-write RED from W26/W28 history, an artificial break or
  the absence of artifacts.
- NEVER change neighbor capability/provider/timer/platform ownership or
  lifecycle/task state.

## Scope

### In scope

- Task-local evidence and T3 execution protocol files.
- Read-only host/static observations of the current two-file source/test state.

### Out of scope

- `DisplayCapability.kt` and `DisplayProjectionTest.kt` production/test writes
  during recovery; no source change is justified by the current receipts.
- All forbidden runtime, device, network, credential and external-provider
  paths.

## Preflight-confirmed change surface

- Expected implementation hints: the exact two task boundary files; already
  dirty before recovery.
- Recovery artifact surface: `.protocols/TASK-032-T3-FT-001-W29/` and
  `.tasks/TASK-032-T3-FT-001-W29/`.
- Hard `write_boundary`: present and respected for production/test behavior;
  only workflow-owned evidence files are added/updated.
- `forbidden_scope` / stop-condition check: clear for host/static work;
  lifecycle/status/checkpoint/terminal writes remain forbidden.

## Applicable quality gates

- [x] Focused display suite: `./gradlew --offline --no-daemon
  :app:testDebugUnitTest --tests com.hozayushka.app.DisplayProjectionTest`.
- [x] Full host suite: `./gradlew --offline --no-daemon testDebugUnitTest`.
- [x] Clean debug build: `./gradlew --offline --no-daemon clean assembleDebug`.
- [x] Android debug lint: `./gradlew --offline --no-daemon lintDebug`.
- [x] Static diff integrity: `git diff --check`.

## Claim-linked RED / GREEN (T2/T3)

- applicability: applicable to the W29 owned visual/composition claims.
- accepted claim locators: `FT-001-AC-002 / REQ-002` for complete clock,
  four-slot stability and preset treatment; `REQ-023` for visual rubric/NFR;
  `REQ-005` and Timer & Alert read-only regression alternatives; `REQ-001`
  target/display-policy alternative proof.
- planned/current host probes: deterministic MainDisplayGeometry,
  MainClockGeometry, orderedDisplayWeatherSlots and PresetVisualGeometry
  observations at exactly `2460x1080` and `1280x720`, plus source/boundary
  review and required host gates.
- observable RED: unavailable as a valid pre-write executor receipt. Reviewer
  missing-artifact findings are not promoted to RED, and historical W26/W28
  evidence is not reused.
- corresponding GREEN: current host/static observations are recorded as
  supporting-only recovery evidence in the task-local artifacts.
- accepted not-applicable alternatives: target/device, fullscreen,
  keep-screen-on and physical rendering remain `DEFERRED`; Weather and Timer
  semantic break probes are not run because they violate the hard boundary.
- T3 isolation/cleanup: offline host-only, redacted deterministic fixtures,
  no external state; task/status/checkpoint/terminal state unchanged.

## MB-SYNC handoff / owner

- Owner: `/verify TASK-032-T3-FT-001-W29`, then required T3
  `/red-verify`; lifecycle owner remains outside this recovery.
- `.memory-bank/` docs needing update: none; this is task-local provenance
  repair and `/mb-sync` is not authorized.
- Task registry/status update owner: lifecycle owner; unchanged here.

## Definition of done

- All requested task-local artifacts exist and link current host/static
  evidence, exact two-size geometry, four-slot cases, preset visual receipts,
  rubric, boundary/deferral notes and host gates.
- The handoff states `PASS_FOR_HANDOFF` only if a valid pre-write RED is
  available; otherwise it returns the exact provenance blocker without
  changing task state.
