---
description: Execution plan for TASK-023-T3-FT-002-W20.
status: active
---
# Plan — TASK-023-T3-FT-002-W20

## Goal

Make a valid OpenWeather key save while OpenWeather is explicitly selected
request exactly one immediate existing Weather Context refresh, so successful
matching data clears the obsolete missing-key state without changing provider or
location identity.

## Non-goals

- No adapter transport/schema, forecast, presentation, timer/alert or lifecycle
  behavior changes.
- No direct Settings-to-adapter call, raw-key callback, storage bypass, fallback,
  mixing, new event boundary, dependency or runtime/device route.
- No changes to historical task status/evidence, scheduler checkpoint or task
  lifecycle.

## Inputs / source specs

- Task: `.memory-bank/tasks/TASK-023-T3-FT-002-W20.task.json`
- Feature/Epic: FT-002 / EP-002
- REQ IDs: `REQ-007`, `REQ-024`, `REQ-025`, `REQ-029`
- Direct canonical inputs: System Architecture, Boundary Map, Capability
  Interfaces, Weather Provider, Local Secret Handling, Local Data, Lifecycle
  Map and Runtime Verification.

## Constraints / invariants (MUST / NEVER)

- MUST keep Settings validation/persistence/secret ownership and Weather Context
  refresh/dispatch/error/cache ownership.
- MUST carry no raw key through the callback; Weather Context obtains it only in
  its existing coherent selected-OpenWeather access snapshot.
- MUST preserve selected provider and location on success/failure and invoke no
  non-selected adapter.
- NEVER refresh for invalid/blank input or an Open-Meteo key-inapplicable save.
- NEVER add a live network/device/ADB/emulator path or secret-bearing artifact.

## Scope

### In scope

- `SettingsCapability.kt`: accepted-key-save callback hook.
- `FoundationRuntime.kt`: wire the hook to the existing asynchronous Weather
  Context refresh executor with the selected-provider trigger.
- `WeatherCapability.kt` and the three listed test files: only if needed for
  the bounded activation proof and regression coverage.

### Out of scope

- All paths in `forbidden_scope` from the task card, including adapters,
  forecast/timer/presentation and historical workflow artifacts.

## Proposed changes

### Touched areas (hypotheses OK)

- `SettingsCapability.kt` — notify only after a valid persisted OpenWeather key
  update; invalid/inapplicable paths return before notification.
- `FoundationRuntime.kt` — enqueue one `WeatherRefreshTrigger.LOCATION_CHANGE`
  (the existing valid-settings refresh boundary) without reading/passing key.
- Focused tests — prove RED/GREEN call counts, errors, identity/isolation,
  failure, repeated-save, redaction and timer/clock independence.

### Preflight-confirmed change surface

- Expected hints kept: all six task `write_boundary` paths are available.
- Additional files/areas: none planned; protocol/evidence files are required
  workflow artifacts, not production outcome expansion.
- Hard `write_boundary` present and satisfied: yes, production/test changes only
  under the six exact task paths.
- `forbidden_scope` / stop-condition check: clear.

## Applicable quality gates

- [ ] `./gradlew clean assembleDebug` — clean Android debug build.
- [ ] `./gradlew testDebugUnitTest` — host selected-key refresh/isolation probes.
- [ ] `node scripts/mb-lint.mjs && git diff --check` — Memory Bank/diff integrity.
- [ ] task-local synthetic/redaction and boundary scans — no raw marker in
  durable source/resource/evidence and no forbidden file changes.

## Claim-linked RED / GREEN (T2/T3)

- applicability: applicable for AC-004 and AC-008; AC-007 accepted not-applicable
  for real-key observation with synthetic/redacted alternative proof.
- accepted claim locators: `FT-002-AC-004 / REQ-007, REQ-025`;
  `FT-002-AC-008 / REQ-007, REQ-029`; `FT-002-AC-007 / REQ-024`.
- planned test/probe: resettable in-memory Settings/Weather fixture, selected
  OpenWeather missing-key refresh, valid/invalid/blank/Open-Meteo saves,
  selected success/failure, repeat save and matched clock/timer trace.
- observable RED: valid save persists but invokes zero Weather Context/provider
  calls and leaves `OpenWeather: API-ключ не указан` current.
- corresponding GREEN: valid save invokes one selected OpenWeather refresh,
  clears the error on success, preserves identity and keeps Open-Meteo at zero;
  selected failure preserves matching state without fallback.
- accepted alternative proof: synthetic marker appears only as in-memory request
  presence/redacted observation; scans prove absence from durable artifacts.
- T3 isolation, safe rerun, cleanup and permission boundary: fresh in-memory
  state per case, synthetic marker never emitted, reset in `finally`, no network,
  device, emulator, ADB or real credentials.

## MB-SYNC handoff / owner

- [x] Owner identified: scheduler/lifecycle owner for later closure.
- [x] Explicit closure ownership: not granted to this `/exe` execution.
- [ ] `.memory-bank/` docs needing update: scheduler/MB-SYNC owner decides after
  verification; `/exe` will not sync broad Memory Bank state.
- [ ] Task registry/status update owner: scheduler; unchanged by `/exe` here.
- [ ] Changelog update owner: scheduler/MB-SYNC owner if required.

## Definition of done

Current executor leaves a reproducible PASS_FOR_HANDOFF with exact changed
files, claim-linked RED/GREEN artifacts, all required gates and residual risk;
functional and T3 semantic closure remain with `/verify` and `/red-verify`.
