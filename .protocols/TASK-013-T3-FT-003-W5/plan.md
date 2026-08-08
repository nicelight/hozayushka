---
description: Execution plan for TASK-013-T3-FT-003-W5.
status: active
---
# Plan — TASK-013-T3-FT-003-W5

## Goal

Make Today open hourly forecast only from the complete TASK-012 normalized
read model, preserve Main Display with the exact fallback otherwise, and keep
the shared 3000 ms/tap/double-tap/hold-release session lifecycle. Add only the
minimum AC-003/AC-005 consumer regression needed to prove the eight-slot public
projection and fallback reach the display/session path.

## Non-goals

- No provider normalization or adapter changes.
- No changes to TASK-005/TASK-012 records or protocols, scheduler state,
  planning/spec artifacts, lifecycle final statuses, or verification routes.
- No new module, dependency, public contract, storage owner, event boundary,
  live key, backend, or unrelated feature behavior.

## Inputs / source specs

- Task record: `.memory-bank/tasks/TASK-013-T3-FT-003-W5.task.json`
- Task index: `.memory-bank/tasks/index.json`
- Feature/Epic: `.memory-bank/features/FT-003-hourly-forecast.md`, `.memory-bank/epics/EP-002-weather-context.md`
- REQ IDs: `REQ-009`, `REQ-022`, `REQ-026`
- Direct contracts/state/runtime/testing inputs are recorded in `context.md`.

## Constraints / invariants (MUST / NEVER)

- MUST use the Forecast Sessions → Weather Context public read boundary.
- MUST use selected-city API timezone for hourly labels and day boundaries.
- MUST render exactly eight cards in two rows of four with shared
  temperature/glass/illustration inputs and no pressure arrows.
- MUST keep the exact fallback `Почасовой прогноз еще не подгрузился`.
- NEVER read Weather Context private storage or raw provider fields from
  Forecast Sessions/Main Display.
- NEVER modify provider normalization ownership or forbidden historical,
  scheduler, lifecycle and planning/spec artifacts.

## Scope

### In scope

- `ForecastSessionCapability` and Main Display hourly-entry/session surface if
  the claim-specific baseline identifies an accepted defect.
- Fresh deterministic tests/fixtures for AC-001 and AC-004 and regression-only
  AC-003/AC-005 integration behavior.
- `.protocols/TASK-013-T3-FT-003-W5/` and `.tasks/TASK-013-T3-FT-003-W5/` evidence.

### Out of scope

- `app/src/main/kotlin/com/hozayushka/app/adapters/weather/` and all forbidden
  task/history/scheduler/spec paths.

## Preflight-confirmed change surface

- Expected hints kept: forecast/display capability and existing forecast tests.
- Additional same-outcome files/areas: task-local evidence/protocol only; no
  unrelated source area is authorized.
- Hard `write_boundary` present and satisfied: not set.
- `forbidden_scope` / stop-condition check: clear.

## Applicable quality gates

- [ ] `./gradlew clean assembleDebug` — clean Android build.
- [ ] `./gradlew testDebugUnitTest` — deterministic unit/fixture tests.
- [ ] `node scripts/mb-lint.mjs` — project Memory Bank/document lint required by the current repository workflow.
- [ ] boundary/static/secret-redaction checks — owner/bypass and no-secret risk.
- [ ] `git diff --check` — patch integrity.
- Target-device route — `DEFERRED` if no authorized target; never claim runtime `PASS`.

## Claim-linked RED / GREEN (T2/T3)

- applicability: applicable
- accepted claim locators: `FT-003-AC-001 / REQ-009`; `FT-003-AC-004 / REQ-009`; regression-only `FT-003-AC-003 / REQ-009 / REQ-022`; regression-only `FT-003-AC-005 / REQ-009 / REQ-026`.
- planned test/probe and environment: deterministic host tests over in-memory
  synthetic/redacted Weather Context and a fake platform clock; static scans
  over registered module edges and forbidden provider access.
- observable RED: to be recorded in `progress.md` before source changes.
- corresponding GREEN: fresh claim-equivalent focused tests plus all mandatory
  host/static/boundary gates, recorded after implementation.
- accepted not-applicable reason and alternative proof: none for host claims;
  target-only readability/gesture/timing observation is deferred as documented.
- T3 isolation, safe rerun, cleanup, and permission boundary: fresh in-memory
  state per test, synthetic credential only, no network/ADB/live key, task-local
  artifacts only, no forbidden paths.

## MB-SYNC handoff / owner

- Owner identified: none in `/exe`; T3 closure remains with `/verify`,
  `/red-verify`, and the lifecycle owner.
- `.memory-bank/` docs needing update: none authorized by this task; planning,
  spec, feature/epic lifecycle and scheduler artifacts remain unchanged.
- Task registry/status update owner: `/exe` may set `ready -> in_progress` only;
  final lifecycle remains outside this command.

## Definition of done

- Current-attempt RED/GREEN and all mandatory host/static/boundary evidence are
  recorded, exact changed files are listed, target risk is `DEFERRED` when no
  target exists, and handoff points to `/verify TASK-013-T3-FT-003-W5`.
