---
description: Execution plan for TASK-020-T3-FT-002-W17 Attempt 3.
status: active
---
# Plan — TASK-020-T3-FT-002-W17

## Goal

Replace Yandex production transport with Open-Meteo and OpenWeather One Call 3.0 adapters, then make Weather Context own selected-only dispatch, provider-neutral normalization and provider+location state while keeping the OpenWeather key ephemeral and redacted.

Attempt 3 is the final bounded retry. It corrects only the independently
reproduced pre-request identity split: one Settings-owned snapshot must
authorize cadence, adapter, request and request identity, and the response must
be rejected before every side effect when one coherent current Settings
projection no longer matches that original identity.

## Non-goals

- Strict hourly-session completeness (`TASK-021`) and long-term 10/8+2 projection (`TASK-022`).
- UI/timer/settings catalog/personalization changes, new dependencies, plugin/DI/registry/event mechanisms, fallback, parallel requests, live calls or device evidence.

## Inputs / source specs
- Task record/index: `.memory-bank/tasks/TASK-020-T3-FT-002-W17.task.json`, `.memory-bank/tasks/index.json`
- Feature: `.memory-bank/features/FT-002-weather-cards-context.md`
- REQ IDs: `REQ-005`, `REQ-007`, `REQ-008`, `REQ-022`, `REQ-024`, `REQ-025`, `REQ-026`, `REQ-029`

## Richer execution inputs
- Weather Provider, Local Secret Handling, Local Data, Capability Interfaces, Boundary Map, System Architecture AD-006/AD-008 and Runtime Verification.
- Verification targets and evidence mappings are taken verbatim from the indexed task card.

## Constraints / invariants (MUST / NEVER)
- MUST: Open-Meteo is default/keyless; OpenWeather is explicit and may receive the owner key only transiently in selected outbound `appid` construction.
- MUST: Weather Context alone dispatches, normalizes and owns provider+location cache/history/freshness.
- NEVER: Yandex production path, third provider, automatic fallback, second-provider request, cross-provider substitution/mixing, secret-bearing durable output or live/device proof.

## Scope

### In scope

- Provider boundary DTO/request identity and credential optionality.
- Two transport/decoder adapters and production composition wiring.
- Selected-provider Weather Context dispatch, error attribution, normalization and provider+location cache/history identity.
- Atomic selected-OpenWeather key-access authorization in the existing Settings seam.
- Deterministic redacted fixtures/tests and static secret/provider inventory scans.

### Out of scope

- Forecast-session completeness/projection owned by W18/W19, unrelated UI/timer/settings/catalog behavior and any runtime/live environment.

## Proposed changes

### Attempt-3 bounded correction

- `SettingsCapability.kt`: expose one immutable provider+location refresh
  snapshot from one Settings load and nested selected-OpenWeather key access
  without putting a raw key in the projection/identity.
- `WeatherCapability.kt`: resolve the complete request attempt from that
  snapshot, then perform the post-fetch stale comparison before result
  inspection or success/failure state mutation.
- `WeatherProviderDispatchTest.kt`: preserve the 8 in-fetch scenarios and add
  both request-capture-window providers plus cadence/freshness/key-read counts.
- Task protocol/evidence: bind fresh GREEN and all gates to Attempt-2 `/verify`,
  confirmed `/debug` and scheduler premortem; retain original RED and both
  failed attempts as supporting-only.

### Touched areas
- `app/src/main/kotlin/com/hozayushka/app/settings/SettingsCapability.kt`
- `app/src/main/kotlin/com/hozayushka/app/weather/WeatherCapability.kt`
- `app/src/test/kotlin/com/hozayushka/app/WeatherProviderDispatchTest.kt`
- Task-owned protocol/report/evidence files only.

### Preflight-confirmed change surface
- Final retry app write surface is exactly the three diagnosed/operator-listed
  files above; needing another app/source/test file is a stop condition.
- Hard `write_boundary` present and satisfied: not set.
- `forbidden_scope` / stop-condition check: clear.

## Applicable quality gates
- [x] Claim-linked focused provider/Weather Context host probe — proves all six owned AC mappings.
- [x] `./gradlew clean assembleDebug` — required clean Android debug build without launch.
- [x] `./gradlew testDebugUnitTest` — required deterministic host suite.
- [x] `node scripts/mb-lint.mjs && git diff --check` — required Memory Bank/diff integrity.
- [x] Redacted source/evidence/APK/provider-inventory scans — required secret and no-Yandex/no-third-provider proof.

## Claim-linked RED / GREEN (T2/T3)
- applicability: applicable
- accepted claim locators: `FT-002-AC-002`, `FT-002-AC-004`, `FT-002-AC-005`, `FT-002-AC-006`, `FT-002-AC-007`, `FT-002-AC-008`
- planned probe/environment: deterministic JUnit fake transports, synthetic in-memory credential, fixed clocks/locations and disposable in-memory stores; no network or device.
- observable RED: current Yandex-only/must-have-key/provider-less production model fails target adapter inventory, selected dispatch, request shape, provider+location identity and provider-attributed failure assertions.
- corresponding GREEN: both target fixtures normalize equivalently; only selected adapter is invoked; identity/freshness/history/failure matrices remain partitioned; Open-Meteo has no credential and only selected OpenWeather transiently uses a synthetic `appid` with redacted capture.
- T3 isolation/safe rerun/cleanup: host-only fake transport and disposable state, with no real key, subscription, endpoint call, emulator or `adb`.

## Retry Attempt 3

- final retry: scheduler-authorized Attempt 3; no fourth `/exe` is permitted
- failed-gate binding: Attempt-2 `/verify` `94/102`, confirmed `/debug` report,
  and scheduler `GO_WITH_CONDITIONS` premortem
- retained RED: Attempt-1 claim RED and Attempt-2 focused RED remain durable;
  this retry requires fresh claim-equivalent GREEN for the corrected identity
  claims and every required gate
- distinguishing target: 10 scenarios, `102/102`; stale success/failure no
  side effects; exact key-read/cadence/freshness boundaries; full clean host,
  MB/diff, secret/APK/provider inventory

## MB-SYNC handoff / owner
- [x] Owner identified: scheduler.
- [x] Explicit standalone owner basis: n/a.
- [x] `.memory-bank/` docs needing update: task status only during `/exe`; lifecycle/RTM/changelog remain scheduler `/mb-sync` ownership.
- [x] `.memory-bank/index.md` router update needed: no.
- [x] RTM update needed: no during `/exe`.
- [x] Task registry/status update owner: `/exe` owns `ready -> in_progress`; verifier/scheduler owns closure.
- [x] Changelog update owner: scheduler `/mb-sync` after verification.

## Definition of done

All task-owned claims have honest RED and claim-equivalent GREEN; required build/test/integrity/security scans pass; changed files/evidence are recorded; task remains `in_progress` and routes to fresh `/verify`, then T3 `/red-verify` only after functional PASS.

Attempt-3 execution result: all executor-owned items above are complete and the
handoff is `PASS_FOR_HANDOFF`; independent `/verify` remains due.
