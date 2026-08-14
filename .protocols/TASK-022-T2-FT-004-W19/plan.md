---
description: Execution plan for TASK-022-T2-FT-004-W19.
status: active
---
# Plan — TASK-022-T2-FT-004-W19

## Goal

Make the existing shared long-term forecast session capability-aware: ten
selected-city dates from API-timezone today for both providers; ten filled
Open-Meteo cards; eight filled plus two dated empty OpenWeather cards; exact
unavailable result for one-short sets.

## Non-goals

- No provider HTTP adapter, key/Settings, cache/history ownership or selected-provider dispatch changes.
- No hourly behavior, provider-specific screen, new module/dependency, plugin/DI/event layer, layout redesign, or shared exit-flow changes.
- No live provider/network, credential, emulator, Android Studio, AVD, QEMU, adb, physical-device or runtime-PASS activity.

## Inputs / source specs

- Task: `.memory-bank/tasks/TASK-022-T2-FT-004-W19.task.json`
- Feature/REQ: FT-004 AC-001/002/005/006; REQ-010, REQ-022, REQ-026.
- Canonical: Boundary Map; Capability Interfaces; Weather Provider; Local Data; Lifecycle Map; Runtime Verification; Invariants; Tier Policy.
- Current implementation baseline: `WeatherCapability.kt`, `ForecastSessionCapability.kt`, `DisplayCapability.kt` and existing forecast tests.

## Constraints / invariants

- MUST use selected provider and selected-city API timezone as the sole source of identity and date horizon.
- MUST require exactly ten Open-Meteo or eight OpenWeather complete daily records before entry.
- MUST return ten ordered positions; OpenWeather positions nine and ten are dated and empty with no temperature or illustration.
- MUST preserve exact `Долгосрочный прогноз еще не подгрузился` behavior for one-short sets.
- NEVER synthesize, duplicate, borrow, mix providers, read another cache partition, alter hourly behavior, or change task lifecycle/checkpoint.

## Scope

### In scope

- Provider-aware long-term projection shape and mapping in Weather Context.
- Existing forecast-card rendering's nullable empty-cell handling, only as required to show the accepted 8+2 projection without temperature/illustration.
- Deterministic synthetic/redacted unit fixtures and claim matrix in ForecastSession/WeatherContext tests.
- T2 protocol and task-owned evidence artifacts.

### Out of scope

- All paths listed in task `runtime_context.forbidden_scope` and all upstream dirty changes unrelated to W19.

## Proposed changes

### Touched areas

- `app/src/main/kotlin/com/hozayushka/app/weather/WeatherCapability.kt` — provider-specific threshold, ten-date projection, explicit empty tail cards.
- `app/src/main/kotlin/com/hozayushka/app/display/DisplayCapability.kt` — render nullable tail fields without inventing temperature/illustration; same forecast view and exit flow.
- `app/src/test/kotlin/com/hozayushka/app/ForecastSessionTest.kt` — selected-provider complete/one-short, date horizon, 10 vs 8+2, Tomorrow/Day-after-equivalent entry and isolation matrix.
- `app/src/test/kotlin/com/hozayushka/app/WeatherContextTest.kt` — provider capability projection/cache identity regression where needed.
- `app/src/test/resources/fixtures/` — only if a deterministic redacted fixture is necessary; prefer existing in-memory fixtures.
- `.tasks/TASK-022-T2-FT-004-W19/` and this protocol — durable evidence.

### Preflight-confirmed change surface

- Advisory task hints are retained; `DisplayCapability.kt` is an additional same-outcome file because nullable projection fields otherwise cannot reach the existing display surface honestly.
- No hard `write_boundary` is set; forbidden scope remains untouched.

## Applicable quality gates

- [ ] `./gradlew clean assembleDebug` — clean Android debug build.
- [ ] `./gradlew testDebugUnitTest` — host provider-capability and long-term session probes.
- [ ] `node scripts/mb-lint.mjs && git diff --check` — Memory Bank and diff integrity.
- [ ] static provider-identity/no-fallback/no-live-call scan and synthetic/redacted secret scan — task redaction and boundary evidence.

## Claim-linked RED / GREEN (T2)

- applicability: applicable
- accepted claim locators: FT-004-AC-001/REQ-010, REQ-026; FT-004-AC-002/REQ-010, REQ-022; FT-004-AC-005/REQ-010, REQ-026; FT-004-AC-006/REQ-010, REQ-026.
- planned probe: deterministic selected-provider fixtures with 10 Open-Meteo, 8 OpenWeather, one-short variants, selected-city timezone crossing and a non-selected provider with distinct data; session begins CLOSED and resets between cases.
- observable RED: current long-term projection returns unavailable unless `daily.size == 10` and all ten records are filled, so OpenWeather 8 cannot open an 8+2 session; no claim matrix proves both entry cards/providers.
- corresponding GREEN: provider thresholds gate entry; projection has exact ten dates, 10 filled Open-Meteo or 8 filled + 2 nullable OpenWeather; one-short remains CLOSED with exact message and non-selected data/calls are never used.
- T3 isolation/cleanup: not applicable; T2 host-only synthetic fixture state is reset per case.

## MB-SYNC handoff / owner

- Owner identified: scheduler/lifecycle owner; `/exe` does not run `/verify`, `/red-verify` or `/mb-sync`.
- `.memory-bank/` docs needing update: none in this execution; current canonical docs already describe the W19 outcome. Existing pre-task Memory Bank edits are preserved.
- Task registry/status/checkpoint update owner: scheduler; unchanged here.
- Changelog update owner: workflow owner if required at wave boundary.

## Definition of done

- Code and deterministic tests satisfy all four W19 claims, task gates pass, artifacts link exact RED/GREEN and gate evidence, deferred runtime risks are recorded, and handoff returns `PASS_FOR_HANDOFF` without changing lifecycle/status/checkpoint.
