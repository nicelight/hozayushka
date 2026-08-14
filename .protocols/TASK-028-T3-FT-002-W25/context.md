---
description: Execution context for TASK-028-T3-FT-002-W25.
status: active
---
# Context — TASK-028-T3-FT-002-W25

## Purpose

Apply the bounded Main Display visual correction: shrink the painted bounds of
the six existing weather illustrations, moderately enlarge only the CLEAR sun
disk inside its reduced envelope, and replace Main Display Unicode pressure
glyphs with measured Canvas/Path arrows.

## Execution Attempt

- attempt: 1
- started: 2026-08-12 20:28:15 +0500

## Inputs

- Task record: `.memory-bank/tasks/TASK-028-T3-FT-002-W25.task.json`
- Task index: `.memory-bank/tasks/index.json`
- Specs: `.memory-bank/features/FT-002-weather-cards-context.md#FT-002-AC-009`, `.memory-bank/contracts/weather-card-presentation.md`, `.memory-bank/contracts/capability-interfaces.md#main-display-to-weather-context`, `.memory-bank/contracts/boundary-map.md#dependency-graph`, `.memory-bank/contracts/platform-runtime.md#display-runtime-boundary`, `.memory-bank/contracts/weather-provider.md`, `.memory-bank/states/lifecycle-map.md#ft-002-first-run-and-failure-projection`, `.memory-bank/testing/runtime-verification.md#deterministic-host-side-checks`, `.memory-bank/testing/strategy.md#risk-based-checks`.
- Planning gate: Global Backbone `Planning Revision: 2`; latest W25 review `FINAL_VERDICT: APPROVE`, `REVIEWED_PLANNING_REVISION: 2`.

## Preflight

- Exact indexed task resolved; tier `T3`, feature `FT-002`, wave `W25`.
- Dependency `TASK-027-T3-FT-001-W24` is `done` and is the authoritative
  overlapping Main Display prerequisite.
- Current task is already `in_progress`; no lifecycle/status/checkpoint write
  is made by this attempt.
- Hard production/test write boundary is exactly
  `DisplayCapability.kt` and `DisplayProjectionTest.kt`. Required protocol and
  task-local evidence are workflow-owned bookkeeping; Memory Bank, task cards,
  scheduler checkpoint, lifecycle/RTM state and terminal state remain untouched.
- Forbidden Weather Context/provider/settings/timer/forecast/app/resource/
  asset/device/network/credential scopes remain excluded.

## Loaded context set

- `AGENTS.md`
- `.memory-bank/constitution.md`, `.memory-bank/mbb/index.md`, `.memory-bank/spec-backbone.md`, `.memory-bank/spec-index.md`, `.memory-bank/index.md`, `.memory-bank/roles/implementer.md`
- `.memory-bank/tasks/TASK-028-T3-FT-002-W25.task.json`, `IMPL-FT-002.md`, FT-002 feature and latest W25 review reports
- Direct architecture, boundary, capability, presentation, provider, runtime, lifecycle, invariant and testing specs

## Decisions / assumptions

- Use the existing Android `Canvas`/`Path`/`Paint` route and existing projection
  inputs only; no new state, contract, resource or dependency.
- Target Samsung GT-I9300I Android 11 custom-ROM 1280×720 evidence is
  `DEFERRED`; host/image evidence must not be called runtime PASS.

## Commands run / environment notes

- Read-only preflight inspection completed before prospective probes or writes.
- Emulator/AVD/QEMU, Android Studio virtual device, adb/device, live network,
  provider calls and credentials are prohibited and will not be used.

## Open questions / blockers

- None for the bounded host implementation. Target observation remains deferred.

## Next session

- Start by reading `context.md`, `plan.md`, `progress.md`.
- Next action: capture fresh RED baseline, then implement and verify only the two
  hard-boundary source/test files.
