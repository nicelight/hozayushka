---
description: Template for .protocols/TASK-NNN-TN-FT-NNN-WN/plan.md (execution plan + MB-SYNC handoff).
status: active
---
# Plan — TASK-033-T3-FT-001-W30

## Goal

Freshly prove the current Main Display baseline, or repair only the accepted
two-file surface, for full unclipped `HH:mm`, four stable weather shells under
NO_DATA/partial/populated fixtures, and three existing preset controls with a
one-color radial shade, wider rim, and exactly three static fading glow layers.

## Non-goals

No WeatherCapability/provider/adapter, Settings, Timer & Alert, lifecycle,
audio, runtime wiring, resource, device/emulator, network, or boundary changes.
No W29/W28/W26 history/status/checkpoint/terminal edits.

## Inputs / source specs
- Task record: `.memory-bank/tasks/TASK-033-T3-FT-001-W30.task.json`
- Task index: `.memory-bank/tasks/index.json`
- Feature/Epic: FT-001 / EP-001
- REQ IDs: REQ-001, REQ-002, REQ-005, REQ-023

## Richer execution inputs (optional)
- Source Artifacts: task-linked FT-001, contracts/boundary-map, runtime-verification, W29 blocked reports
- Normative Inputs: task record, tier policy claim-linked RED/GREEN, display/weather/timer/platform contracts
- Verification Targets: task-scoped clock geometry, four-slot matrix, preset visual receipts, named rubric, host gates

## Fallback basis

Not used; richer task-linked inputs were present and loaded.

## Constraints / invariants (MUST / NEVER)
- MUST: fresh task-specific RED probe first at exactly 2460x1080 and 1280x720 with full bounds/slots/presets.
- MUST: if baseline is already GREEN, use honest RED_NOT_APPLICABLE and no behavior write.
- MUST: if repair is needed, change only DisplayCapability.kt and DisplayProjectionTest.kt.
- NEVER: reuse W26/W28/W29 evidence as W30 RED/GREEN or alter their history/status/protocols/artifacts.
- NEVER: launch device/emulator/runtime, use adb/network/credentials, or touch forbidden owners.

## Scope
### In scope

Fresh W30 host probe/evidence, optional exact two-file behavior correction, GREEN
supporting receipts, required artifact set, gates and handoff to `/verify`.

### Out of scope

Task lifecycle closure, `/verify`, `/red-verify`, `/mb-sync`, device/runtime
evidence, and any write outside task-local evidence/protocol artifacts plus the
two exact behavior files if a correction is necessary.

## Proposed changes
### Touched areas

No production/test behavior change was required after the fresh baseline
probe. The exact two-file surface was inspected and preserved unchanged.

### Preflight-confirmed change surface
- Expected hints kept: DisplayCapability.kt and DisplayProjectionTest.kt
- Additional same-outcome files/areas and rationale: none planned
- Hard `write_boundary` present and satisfied: yes
- `forbidden_scope` / stop-condition check: clear at preflight

## Applicable quality gates
List only evidence-backed project-native checks required by the task record,
linked specs/PRD, or repository configuration.

- [x] Clean Android debug build: `./gradlew clean assembleDebug` — exit 0
- [x] Focused display suite: `./gradlew :app:testDebugUnitTest --tests com.hozayushka.app.DisplayProjectionTest` — exit 0
- [x] Full host unit suite: `./gradlew testDebugUnitTest` — exit 0
- [x] Android debug lint: `./gradlew lintDebug` — exit 0
- [x] Static diff integrity: `git diff --check` — exit 0

## Claim-linked RED / GREEN (T2/T3)
- applicability: applicable for Main Display claim set; accepted not-applicable branches are claim-specific.
- accepted claim locator(s): FT-001-AC-002 / REQ-002 / REQ-023, plus REQ-001/005 read-only boundaries
- planned test/probe and environment: disposable deterministic host-only probe at 2460x1080 and 1280x720
- observable RED: fresh baseline defect, if any, with full clock bounds, slot matrix, and preset receipts
- corresponding GREEN: same W30 host path after correction, or fresh baseline proof when RED_NOT_APPLICABLE
- accepted not-applicable reason and alternative proof: intentionally breaking accepted display would manufacture failure; fresh W30 receipts/rubric/boundary review prove baseline.
- T3 isolation, safe rerun, cleanup, and permission boundary: redacted fixtures; no network/device/runtime; only task artifacts/protocol and exact two-file behavior scope.

## Fan-out plan (if needed)

None; execution remained single-agent and task-local.

## MB-SYNC handoff / owner
Scheduler or explicit standalone owner performs sync after verification/status
decision. `/exe` only records handoff notes.

An `explicit standalone owner` exists only when the user directly asked the
current top-level agent to close the task, or when the top-level
agent/orchestrator explicitly runs a manual workflow for one TASK and records
that it owns closure. Subagent prompts do not silently become closure
owners.

Checklist:
- [x] Owner identified: human / `/verify` (T3 lifecycle owner)
- [x] Explicit standalone owner basis recorded: n/a; user forbids lifecycle edits
- [x] `.memory-bank/` docs needing update: none; task-local protocol/evidence is sufficient
- [x] `.memory-bank/index.md` router update needed: no
- [x] RTM update in `.memory-bank/requirements.md` needed: no
- [x] Task registry/status update owner: lifecycle owner; unchanged by `/exe`
- [x] Changelog update owner: lifecycle owner; unchanged by `/exe`

## Definition of done

All required W30 evidence exists with fresh provenance, applicable host gates
pass, actual scope is recorded, target/device is DEFERRED, and handoff points
to current-attempt receipts without claiming final task closure.
