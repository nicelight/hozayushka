---
description: Execution plan for TASK-012-T3-FT-003-W4.
status: active
---
# Plan — TASK-012-T3-FT-003-W4

## Goal

Accept a supported 48-record, two-city-local-day provider response and
normalize it to exactly `06:00, 09:00, 12:00, 15:00, 18:00, 21:00, 00:00,
03:00`, with the final two slots on the following city-local day and with
selected-slot required-field validation remaining all-or-nothing.

## Non-goals

- No change to the public eight-slot projection, Forecast Sessions, lifecycle,
  task planning, scheduler state, or `TASK-005` historical evidence/status.
- No live request, credential, provider/dependency, schema, graph edge, or
  unrelated feature behavior.

## Inputs / source specs

- Task record: `.memory-bank/tasks/TASK-012-T3-FT-003-W4.task.json`
- Feature: `.memory-bank/features/FT-003-hourly-forecast.md`
- REQs: `REQ-009`, `REQ-022`, `REQ-026`
- Provider/boundary/read-model specs listed in `context.md`.

## Scope

### In scope

- Weather Context validation and normalization of supported full-day hourly
  provider data.
- Synthetic/redacted fixture-based host tests for exact slots, timezone/day
  boundary, and selected-slot completeness.
- Task protocol and evidence under `.protocols/TASK-012-T3-FT-003-W4/` and
  `.tasks/TASK-012-T3-FT-003-W4/`.

### Out of scope

- All forbidden scope in the task card, including `TASK-005` files/protocol,
  lifecycle/status terminal records, scheduler checkpoint and secrets.

## Proposed changes

### Preflight-confirmed change surface

- `app/src/main/kotlin/com/hozayushka/app/weather/WeatherCapability.kt` —
  owner-side selected-slot validation and normalization.
- `app/src/test/kotlin/com/hozayushka/app/WeatherContextTest.kt` — deterministic
  regression fixtures/probes for the task-owned claims.
- `.protocols/TASK-012-T3-FT-003-W4/` and `.tasks/TASK-012-T3-FT-003-W4/` —
  execution evidence only.
- Advisory touched-file hints remain satisfied; no hard write boundary is set.

## Applicable quality gates

- [x] `./gradlew clean assembleDebug` — clean Android debug build.
- [x] `./gradlew testDebugUnitTest` — full host unit suite.
- [x] `./gradlew testDebugUnitTest --tests 'com.hozayushka.app.WeatherContextTest'` —
  focused fixture proof.
- [x] `node scripts/mb-lint.mjs` and `git diff --check` — static/document
  hygiene checks.
- [x] boundary/static and synthetic/redacted secret scans — registered edge,
  no bypass, no secret-bearing fixture/evidence.

## Claim-linked RED / GREEN (T2/T3)

- applicability: applicable
- accepted claim locators: `FT-003-AC-002 / REQ-009`,
  `FT-003-AC-005 / REQ-009 / REQ-026`, `FT-003-AC-003 / REQ-022`
- planned probe: compiled host unit tests with in-memory Weather Context,
  deterministic `Asia/Dushanbe` selected-city timezone and synthetic 48-hour
  provider data.
- RED: current exact raw-cardinality gate rejects the valid 48-record shape;
  selected-slot missing temperature/illustration remains unavailable.
- GREEN: exactly eight ordered cards are normalized with next-day `00:00` and
  `03:00`; any selected required-field absence returns no projection.
- T3 isolation: in-memory cache, synthetic provider/request, deterministic time,
  safe rerun with fresh state per test, no external request or credential.

## MB-SYNC handoff / owner

`/exe` records execution only. `/verify` and then T3 `/red-verify` are the
next required routes; lifecycle and sync owners remain outside this task.

- Owner identified: human/lifecycle owner after required verification.
- `.memory-bank/` planning/registry/status artifacts: unchanged by `/exe`.
- `TASK-005` protocol/status/artifacts: unchanged.

## Definition of done for this execution handoff

- Production repair and regression fixture are implemented within scope.
- RED/GREEN, host/build/unit/static/fixture results and deferred target risk
  are recorded with exact artifact paths.
- Task remains open for `/verify` and T3 `/red-verify`.
