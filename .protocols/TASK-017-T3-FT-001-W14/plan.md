---
description: Execution plan for TASK-017-T3-FT-001-W14.
status: active
---
# Plan — TASK-017-T3-FT-001-W14

## Goal

Reuse the existing capability-owned `WeatherProjection` for repeated Main Display
reads while preserving accepted successful-refresh, location, selected-city time,
pressure-trend and 24-hour freshness behavior.

## Non-goals

- No Main Display ticker/W13, renderer, Activity lifecycle or public read-contract change.
- No provider/Yandex wiring, Forecast, Timer/audio, gestures, Settings, dependency,
  module, event/message boundary, target-device claim or scheduler/terminal-state change.

## Inputs / source specs

- Task record: `.memory-bank/tasks/TASK-017-T3-FT-001-W14.task.json`
- Task index: `.memory-bank/tasks/index.json`
- Feature: `.memory-bank/features/FT-001-main-clock-display.md#FT-001-AC-002`
- REQ IDs: `REQ-002`, `REQ-007`, `REQ-022`, `REQ-025`
- Direct SDD: task `normative_inputs` and `source_artifacts` (read in full before execution).

## Constraints / invariants (MUST / NEVER)

- MUST keep Weather Context as the owner of cache/history/freshness/projection semantics.
- MUST observe validated location through the existing reader and preserve selected-city
  date/day-night and pressure-trend boundary semantics.
- MUST preserve failed-refresh last-success behavior and four-card empty/stale semantics.
- NEVER bypass the private cache owner, add public invalidation, or change a neighbor module.
- NEVER write outside the two task hard-boundary source/test files (workflow artifacts exempt).

## Scope

### In scope

- `WeatherCapability.kt`: private projection snapshot and invalidation/validity logic.
- `WeatherContextTest.kt`: focused counting/reuse/invalidation and existing regression coverage.

### Out of scope

Everything listed in the task card `anti_goals`, `runtime_context.forbidden_scope` and
`stop_conditions`, including W13/Main Display, provider, Forecast, Timer/audio, gestures,
public edges, dependencies and target-device evidence.

## Proposed changes

### Touched areas (hypotheses OK)

- `app/src/main/kotlin/com/hozayushka/app/weather/WeatherCapability.kt` — memoized display
  projection and private boundary checks.
- `app/src/test/kotlin/com/hozayushka/app/WeatherContextTest.kt` — isolated counting fixture
  and claim-equivalent host tests.

### Preflight-confirmed change surface

- Expected hints kept: yes; exact two paths.
- Additional same-outcome files/areas and rationale: none.
- Hard `write_boundary` present and satisfied: yes, once production/test writes begin.
- `forbidden_scope` / stop-condition check: clear.

## Applicable quality gates

- [x] `./gradlew clean assembleDebug` — clean Android debug build; executor receipt recorded.
- [x] `./gradlew testDebugUnitTest` — full host weather/display regression suite; 59/59.
- [x] `git diff --check` — static diff integrity; executor receipt recorded.

## Claim-linked RED / GREEN (T2/T3)

- applicability: `FT-001-AC-002 / REQ-002` applicable; weather/time/failure claims use
  the accepted deterministic regression alternative in the task card.
- accepted claim locator(s): `FT-001-AC-002 / REQ-002`; regression `REQ-007 / REQ-022 / REQ-025`.
- planned test/probe and environment: isolated in-memory/counting Weather Context fixture.
- observable RED: pre-change repeated `projection(now)` calls failed `assertSame` and
  exercised the current cache-load/projection-build path.
- corresponding GREEN: same valid input returns the same snapshot with zero repeated cache
  loads; accepted refresh/location/freshness/time boundaries rebuild once and preserve exact
  card semantics.
- accepted not-applicable reason and alternative proof: no intentional break of already
  accepted weather/time/failure behavior; fresh deterministic regression output instead.
- T3 isolation, safe rerun, cleanup, and permission boundary: in-memory settings/cache/
  provider fixtures, reset per case, no credentials/persistent production state/device.

## MB-SYNC handoff / owner

Scheduler or explicit lifecycle owner performs sync after verification/status decision.
`/exe` does not run `/verify`, `/red-verify`, `/mb-sync` or closure.

- [x] Owner identified: lifecycle owner after `/verify` and `/red-verify`
- [ ] `.memory-bank/` docs needing update: no task-owned WHY/WHERE doc change required;
  wave sync owner decides at boundary
- [ ] `.memory-bank/index.md` router update needed: no
- [ ] RTM update needed: no
- [ ] Task registry/status update owner: lifecycle owner; executor leaves task open
- [ ] Changelog update owner: wave sync owner

## Definition of done

Fresh RED/GREEN, all three executor gates, actual changed-file boundary evidence and a
compact PASS_FOR_HANDOFF are recorded; current lifecycle remains open for `/verify`.
