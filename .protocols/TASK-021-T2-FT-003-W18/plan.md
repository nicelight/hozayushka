---
description: Execution plan for TASK-021-T2-FT-003-W18.
status: active
---
# Plan — TASK-021-T2-FT-003-W18

## Goal
Make hourly entry available only for all eight fixed selected-provider city-local slots, and keep Main Display with `Почасовой прогноз еще не подгрузился` for every missing slot without synthesis, borrowing, fallback or mixing.

## Non-goals
- Provider HTTP adapters, selection persistence, key handling, cache/history ownership or fallback policy.
- 2×4 layout, presentation, auto-close and gesture redesign.
- Long-term forecast, new modules/dependencies/events, live calls or device evidence.

## Inputs / source specs
- Task record: `.memory-bank/tasks/TASK-021-T2-FT-003-W18.task.json`
- Task index: `.memory-bank/tasks/index.json`
- Feature: `.memory-bank/features/FT-003-hourly-forecast.md`
- REQ IDs: `REQ-009`, `REQ-026`

## Richer execution inputs
- Source Artifacts: FT-003 AC-001 and AC-005.
- Normative Inputs: capability interfaces, weather provider, local data, runtime verification, boundary map and tier policy listed in `context.md`.
- Verification Targets: selected-city timezone projection, complete two-provider entry, all 16 one-missing-slot rejection cases, no cross-provider/cache/history borrowing.

## Constraints / invariants (MUST / NEVER)
- MUST use exactly `06:00, 09:00, 12:00, 15:00, 18:00, 21:00` for current city-local day and `00:00, 03:00` for the following day.
- MUST preserve selected provider and location identity through Weather Context → Forecast Sessions.
- NEVER synthesize, nearest-match, interpolate, read another provider partition, mix providers, open a partial session or change W4/W5 session behavior.

## Scope
### In scope
- Weather Context completeness/projection behavior and Forecast Sessions entry rejection only as needed for W18.
- `WeatherContextTest.kt`, `ForecastSessionTest.kt`, and redacted fixture/test support needed for deterministic provider-separated proof.

### Out of scope
- All task `forbidden_scope` entries and scheduler/lifecycle/protocol state outside this task protocol/evidence.

## Proposed changes
### Touched areas (hypotheses OK)
- `app/src/main/kotlin/com/hozayushka/app/weather/WeatherCapability.kt` — selected-provider eight-slot completeness owner.
- `app/src/main/kotlin/com/hozayushka/app/forecast/ForecastSessionCapability.kt` — existing entry/rejection surface only if required.
- `app/src/test/kotlin/com/hozayushka/app/WeatherContextTest.kt` and `ForecastSessionTest.kt` — deterministic provider/date/timezone matrix.
- `app/src/test/resources/fixtures/` — synthetic/redacted provider fixture support only.

### Preflight-confirmed change surface
- Expected hints kept: tests/proof surface kept; production Weather Context/Forecast Sessions behavior already satisfies the selected-provider exact-slot path on pre-implementation probe.
- Additional same-outcome files/areas and rationale: `.tasks/TASK-021-T2-FT-003-W18/hourly-completeness-matrix.json` and protocol files are required durable execution evidence; no production file or fixture resource was added.
- Hard `write_boundary` present and satisfied: not set.
- `forbidden_scope` / stop-condition check: clear.

## Applicable quality gates
- [ ] `./gradlew clean assembleDebug` — required clean Android debug build.
- [ ] `./gradlew testDebugUnitTest` — required host hourly completeness/session probes.
- [ ] `node scripts/mb-lint.mjs && git diff --check` — required Memory Bank/diff integrity.
- No Android Studio, emulator, AVD, QEMU, virtual device, adb, physical device, live network/provider call or real credential.

## Claim-linked RED / GREEN (T2/T3)
- applicability: applicable
- accepted claim locator(s): `FT-003-AC-001 / REQ-009`; `FT-003-AC-005 / REQ-009, REQ-026`; `weather-provider.md#mapping-and-timezone-obligations`; `weather-provider.md#failure-rules`
- planned test/probe and environment: resettable host fixtures with selected Open-Meteo/OpenWeather identity, fixed `Asia/Dushanbe` city-local date and session state.
- observable RED: current code lacks the required two-provider complete-entry and 16-case missing-slot matrix; exact current observation to be recorded before production change.
- corresponding GREEN: complete Open-Meteo/OpenWeather entry plus every one-missing-slot rejection, elapsed OpenWeather absence, exact message, no session and no cross-provider value.
- accepted not-applicable reason and alternative proof: target runtime/live provider is deferred by task constraints; deterministic host proof is the accepted alternative.

## MB-SYNC handoff / owner
Scheduler/lifecycle owner performs status/checkpoint/verification/sync after this handoff. `/exe` records evidence only and does not run `/verify`, `/red-verify` or `/mb-sync`.
- Owner identified: scheduler
- `.memory-bank/` docs needing update: none expected; task-local protocol/evidence only.
- `.memory-bank/index.md` router update needed: no.
- RTM update in `.memory-bank/requirements.md` needed: no.
- Task registry/status update owner: scheduler; unchanged by this execution.
- Changelog update owner: scheduler/wave boundary.

## Definition of done
- Required claim-specific regression proof and all task gates pass; current Attempt 1 claim-linked RED/GREEN and artifacts are linked from `progress.md`/`handoff.md`; no runtime PASS or lifecycle mutation is made.
