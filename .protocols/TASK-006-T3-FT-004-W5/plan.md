---
description: Execution plan for TASK-006-T3-FT-004-W5.
status: active
---
# Plan — TASK-006-T3-FT-004-W5

## Goal

Implement the accepted selected-city ten-day forecast: complete ten-record
Weather Context read model, Tomorrow/Day-after entry, two rows of five shared
cards without pressure arrows, exact missing-data fallback and the shared
forecast-session exit flow.

## Non-goals

- FT-001 clock/date/fullscreen, FT-002 current/daily ownership, or FT-003 hourly content.
- FT-005–FT-009 timer, alert, Settings, location, API-key UI or personalization behavior.
- New providers, dependencies, graph edges, storage owners, event infrastructure, backend, live credentials or target-runtime claims.

## Inputs / source specs

- Task: `.memory-bank/tasks/TASK-006-T3-FT-004-W5.task.json`
- Feature / requirements: `FT-004`, `REQ-010`, `REQ-022`, `REQ-026`
- Canonical interfaces and ownership: `capability-interfaces.md`, `boundary-map.md`, `system-architecture.md`
- Forecast mapping/presentation/data/lifecycle/runtime: `weather-provider.md`, `weather-card-presentation.md`, `local-data.md`, `lifecycle-map.md`, `platform-runtime.md`
- Proof route: `runtime-verification.md`, `tier-policy.md`

## Constraints / invariants (MUST / NEVER)

- MUST keep Weather Context normalization, completeness and persistence behind its public read model.
- MUST use selected-city API timezone for daily dates/day-night selection; device timezone remains Main Display clock/date.
- MUST reject partial daily data atomically; no invented day or field.
- MUST reuse shared card presentation and shared session lifecycle; omit pressure arrows.
- NEVER access private storage/raw provider data from Main Display or Forecast Sessions.
- NEVER add unrelated feature behavior, new graph edges, credentials or device/runtime PASS claims.

## Scope

### In scope

Daily redacted provider mapping, complete public ten-day read model/save-reload proof, long-term Forecast Sessions entry/rejection/projection, Main Display Tomorrow/Day-after wiring and deterministic shared-session behavior.

### Out of scope

All `runtime_context.forbidden_scope` and stop-condition areas from the task card.

## Proposed changes

### Touched areas (hypotheses OK)

- `app/src/main/kotlin/com/hozayushka/app/weather/` — owned normalized daily read model and public boundary.
- `app/src/main/kotlin/com/hozayushka/app/forecast/` — long-term session creation, projection and shared exit behavior.
- `app/src/main/kotlin/com/hozayushka/app/display/` — accepted entry intent and returned projection rendering only.
- `app/src/main/kotlin/com/hozayushka/app/adapters/weather/` — synthetic/redacted provider shape only where existing boundary requires it.
- `app/src/test/kotlin/com/hozayushka/app/`, `app/src/test/resources/fixtures/` — deterministic redacted claims and boundary/static checks.

### Preflight-confirmed change surface

- Expected hints kept: yes; actual implementation stayed in Weather Context, Forecast Sessions, Main Display/MainActivity and task tests.
- Additional same-outcome files/areas and rationale: `MainActivity.kt` wiring was required to select the accepted public hourly/long-term view; no adapter/platform/resource file was required.
- Hard `write_boundary` present and satisfied: not set.
- `forbidden_scope` / stop-condition check: clear.

## Applicable quality gates

- [x] `./gradlew clean assembleDebug` — clean Android debug build.
- [x] `./gradlew testDebugUnitTest` — host ten-day behavior and regression suite.
- [x] Project-native static/boundary/fixture/redaction checks identified from existing FT-003 evidence and task outcome.
- [x] Target-device route — deferred/non-blocking because no target is available; no runtime PASS claim.

Final gate artifacts: `.tasks/TASK-006-T3-FT-004-W5/host-gates.md`,
`static-boundary-redaction.md` and `target-device.md`.

## Claim-linked RED / GREEN (T2/T3)

- applicability: applicable
- accepted claim locator(s): FT-004-AC-001 through FT-004-AC-005; REQ-010, REQ-022, REQ-026; direct FT-004 contracts.
- planned test/probe and environment: smallest deterministic host fixture/read-port probe, isolated state, synthetic/redacted data, deterministic timing source.
- observable RED: record honest baseline result for every owned claim before production behavior changes; pre-existing GREEN is retained.
- corresponding GREEN: fresh claim-equivalent host result after implementation plus mandatory build/static/boundary/redaction gates.
- T3 isolation, safe rerun, cleanup, and permission boundary: disposable/in-memory or isolated Weather Context state; reset before and cleanup after; only public capability interfaces; no secrets or target side effects.

## MB-SYNC handoff / owner

Scheduler or explicit lifecycle owner performs sync after `/verify` and T3
`/red-verify`. `/exe` does not run either verifier or `/mb-sync`.

- [ ] Owner identified: lifecycle owner / scheduler
- [ ] `.memory-bank/` docs needing update: none selected by implementation; user forbids planning/spec/index changes
- [ ] `.memory-bank/index.md` router update needed: no
- [ ] RTM update in `.memory-bank/requirements.md` needed: no
- [ ] Task registry/status update owner: `/exe` owns only start to `in_progress`; closure remains owner
- [ ] Changelog update owner: no change in this execution

## Definition of done

Implementation and current-attempt evidence satisfy the selected card outcome,
all required host/build/unit/static/boundary/redaction checks are recorded,
target evidence is explicitly deferred if unavailable, and handoff points to
`/verify TASK-006-T3-FT-004-W5` without changing final lifecycle.
