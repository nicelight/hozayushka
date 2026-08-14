---
description: Execution context for TASK-020-T3-FT-002-W17 Attempt 3.
status: active
---
# Context — TASK-020-T3-FT-002-W17

## Purpose

Execute the accepted Revision-2 migration from the historical Yandex production path to exactly two selected weather providers while preserving Weather Context ownership and secret isolation.

## Execution Attempt
- attempt: 3
- started: 2026-08-11T05:30:45+05:00

## Inputs (what drives this task)
- Task record: `.memory-bank/tasks/TASK-020-T3-FT-002-W17.task.json`
- Task index: `.memory-bank/tasks/index.json`
- Feature acceptance: `.memory-bank/features/FT-002-weather-cards-context.md#acceptance-criteria`
- Requirements: `REQ-005`, `REQ-007`, `REQ-008`, `REQ-022`, `REQ-024`, `REQ-025`, `REQ-026`, `REQ-029`
- Planning gate: Global Backbone `complete`, Planning Revision `2`; latest FT-002 task-plan review `FINAL_VERDICT: APPROVE`, `REVIEWED_PLANNING_REVISION: 2`
- Dependency: `TASK-019-T3-FT-008-W16` is `done`; its Attempt-3 semantic handoff requires atomic replacement of the temporary provider-unidentified key deny.

## Richer inputs
- Source artifacts: task-owned `FT-002-AC-002`, `FT-002-AC-004` through `FT-002-AC-008`.
- Normative inputs: System Architecture AD-006/AD-008; Boundary Map graph/ownership; Capability Interfaces; complete Weather Provider contract; Local Secret Handling; Local Data FT-002 records; Runtime Verification redacted-fixture and artifact checks; applicable Tier Policy sections.
- Constraints: exactly two adapters; Open-Meteo default/no-key; explicit OpenWeather/local-key; selected-only dispatch; provider+location cache/history; no fallback, mixing, live provider, real credential, device or emulator evidence.

## Loaded context set (what was read)
- `AGENTS.md`, `.memory-bank/roles/implementer.md`, `.agents/skills/exe/SKILL.md`
- `.memory-bank/tasks/TASK-020-T3-FT-002-W17.task.json`, task index, dependency task and W16 final handoff/semantic evidence
- `.memory-bank/spec-backbone.md`, `.memory-bank/spec-index.md`, current FT-002 review report
- `.memory-bank/contracts/weather-provider.md`, linked architecture/boundary/capability/secret/data/testing specs
- `.memory-bank/features/FT-002-weather-cards-context.md`, linked requirements, invariants and provider-migration plans
- Current adapter, Weather Context, Settings, composition and host-test source

## Decisions / assumptions
- Decision: use the existing provider interface and explicit Weather Context branch; no registry, DI mechanism or new module is needed.
- Decision: replace the W16 blanket deny only with selected-OpenWeather-scoped callback access; Open-Meteo request construction has no credential object.
- Decision: provider identity is a typed value carried by provider envelopes and all Weather Context cache/history records.
- Assumption to verify by GREEN: existing unrelated UI/timer/settings/catalog behavior remains unchanged under the full host suite.
- Final-retry correction basis: Attempt-2 `/verify` passed the original race and
  all eight in-fetch stale scenarios, but its request-capture-window cases
  passed only `94/102`: request construction used location A before the later
  identity capture rebound the attempt to location B.
- Confirmed diagnosis: capture one validated immutable Settings-owned access
  snapshot at the start of `refreshIfNeeded`, derive cadence/adapter/request and
  request identity from it, then compare one coherent current provider+location
  projection with that original identity immediately after fetch and before any
  result inspection or side effect.
- Premortem: `GO_WITH_CONDITIONS`; exactly the three operator-authorized app
  files may change, raw key access stays nested/ephemeral after pre-request
  checks, the 10-scenario identity matrix must reach `102/102`, and no fourth
  execution attempt exists.

## Commands run / environment notes
- Read-only preflight resolved one exact indexed task, valid `T3 / FT-002 / W17` identity, `ready` lifecycle, done dependency, current planning approval and complete prospective proof mapping.
- Git baseline is intentionally dirty with Revision-2 planning and completed W16 changes. The only app overlap is the accepted W16 Settings/key transition surface; unrelated changes will be preserved.
- No network/live provider, credential, emulator/AVD/QEMU, Android Studio virtual device, `adb` or physical device is authorized or used.
- Attempt 3 reconciles the completed Attempt-2 handoff with its independent
  functional `FAIL`; the indexed task remains `in_progress`, and the final
  retry is bounded to the diagnosed three-file correction and evidence.

## Open questions / blockers
- None.

## Next session
- No replay is authorized. Resume only this active Attempt-3 correction from
  the latest progress entry and keep all Attempt-1/2 evidence supporting-only.
