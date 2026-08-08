---
description: Execution plan for TASK-005-T3-FT-003-W4.
status: active
---
# Plan — TASK-005-T3-FT-003-W4

## Goal

Deliver complete eight-slot hourly entry, city-timezone projection, shared
card presentation, missing-data fallback and shared forecast exit flow.

## Non-goals

FT-001 clock/fullscreen, FT-002 current/daily behavior, FT-004 long-term
forecast, timer/preset/alert, Settings/location, new providers/dependencies,
backend, Google Services, reboot recovery and live credentials.

## Preflight-confirmed change surface

- Existing advisory areas confirmed: forecast, weather, provider adapter,
  display, composition wiring and host tests.
- Additional same-outcome files may include the existing fixture JSON only if
  needed; no hard `write_boundary` is set.
- `forbidden_scope` / stop-condition check: clear.

## Applicable quality gates

- [x] `./gradlew clean assembleDebug` — clean Android debug build.
- [x] `./gradlew testDebugUnitTest` — deterministic hourly mapping, completeness,
  timezone, projection and shared-session behavior.
- [x] source/evidence secret scan — synthetic/redacted-only path.
- [x] boundary inspection — registered edges and no direct storage/provider bypass.

## Claim-linked RED / GREEN

- applicability: applicable for AC-001 through AC-005.
- probe: host tests with deterministic redacted hourly fixture and
  fake `PlatformRuntime` clock; isolated in-memory Weather Context and session
  state, reset per test.
- target-device route: accepted alternative proof is not applicable while no
  authorized Android target is available; record `DEFERRED` plus residual risk.

## MB-SYNC handoff / owner

Scheduler or explicit lifecycle owner performs sync and closure after `/verify`
and required T3 `/red-verify`; `/exe` does not run those workflows.

## Definition of done

Implementation, mandatory host gates, static/secret/boundary evidence and
current-attempt handoff are recorded; task remains open for `/verify` and
`/red-verify`.
