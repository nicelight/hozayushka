---
description: Execution plan for TASK-003-T3-FT-001-W2.
status: active
---
# Plan — TASK-003-T3-FT-001-W2

## Goal

Turn the Foundation display into the accepted stable FT-001 Main Display
outcome while preserving registered capability ownership and edges.

## Non-goals

- No FT-002 weather data/content/freshness/palette or forecast sessions.
- No preset configuration, countdown lifecycle, overdue or alert behavior.
- No Settings catalog, API-key handling/validation, offline location search or personalization.
- No new dependency, graph edge, public contract, storage bypass, backend, reboot recovery or live credential.

## Scope

### In scope

- Main Display composition: dominant `HH:mm`, device-time Russian date, stable four-card lower-left shell and three preset positions.
- Online/offline/countdown colon projection and city short/long-hold routing.
- Platform display policy wiring and minimal Settings destination/return seam.
- Deterministic host tests and required Android build/test gates.

### Out of scope

Everything listed under task `anti_goals` and `runtime_context.forbidden_scope`.

## Preflight-confirmed change surface

- Expected advisory paths kept: display, MainActivity, FoundationRuntime only if wiring is needed, platform adapter, settings, resources and host tests.
- Additional same-outcome files: none planned.
- Hard `write_boundary`: not set.
- Forbidden scope / stop conditions: clear at start.

## Applicable quality gates

- [ ] `./gradlew clean assembleDebug` — clean Android debug build.
- [ ] `./gradlew testDebugUnitTest` — deterministic display/date/colon/layout/gesture checks.
- [ ] Target-device route from `runtime-verification.md#target-device-evidence` — fullscreen, hidden panels, keep-screen-on, readability and Settings return interaction.

## Claim-linked RED / GREEN

- applicability: applicable to all five FT-001 AC claims.
- claim mapping: AC-001 runtime policy; AC-002 shell composition; AC-003 device date; AC-004 colon states; AC-005 city routing.
- initial RED: run claim-specific baseline/static probes after `in_progress` and before production changes.
- GREEN: deterministic tests plus clean build; target-only observations remain separately recorded and are not inferred.
- T3 isolation: synthetic/local state only, no live key/network request, no secret-bearing evidence, disposable target state with cleanup if a device is available.

## MB-SYNC handoff / owner

`/exe` leaves T3 open. `/verify TASK-003-T3-FT-001-W2`, then `/red-verify` are required before lifecycle closure.

- Owner: verifier / T3 semantic reviewer / scheduler lifecycle owner.
- Memory Bank/index/RTM/changelog updates: none authorized by `/exe`; only task protocol/evidence and task lifecycle start are owned here.

## Definition of done

- Production outcome is implemented within scope.
- Current-attempt RED/GREEN evidence, exact changed files, gates and blockers are linked from `progress.md` and `handoff.md`.
- Task remains `in_progress`; recommended next action is `/verify TASK-003-T3-FT-001-W2`.
