---
description: Execution plan for TASK-007-T3-FT-005-W6.
status: active
---
# Plan — TASK-007-T3-FT-005-W6

## Goal

Deliver three independently configurable preset definitions with defaults
`3m/10m/30m`, accepted field validation, positive-total and last-valid-value
rules, owner-local persistence/reload, floor-rounded labels, fixed
orange/pink/purple neon outlines and a selected/active projection consumable by
Timer & Alert without creating parallel active timer state.

## Non-goals

- FT-006 countdown start/cancellation/recovery/arithmetic behavior.
- FT-007/FT-009 overdue rendering, audio policy, alert ramp/cap and personalization.
- API-key, location, catalog, sound-volume, glass settings, backend/cloud, reboot recovery, new dependencies or new graph edges.

## Inputs / source specs

- Task: `.memory-bank/tasks/TASK-007-T3-FT-005-W6.task.json`
- Feature/Epic: `.memory-bank/features/FT-005-timer-presets.md`, `.memory-bank/epics/EP-003-timers-alert.md`
- REQ: `REQ-011`
- Canonical: system architecture, boundary map, capability interfaces, local data, lifecycle map, platform runtime, runtime verification, invariants and tier policy.

## Constraints / invariants (MUST / NEVER)

- MUST keep validation and persistence in Settings & Location.
- MUST keep active timer state and lifecycle ownership in Timer & Alert.
- MUST keep Main Display composition-only and use public capability surfaces.
- MUST preserve exactly one active timer and last valid value on invalid input.
- NEVER access a neighbor's private store/raw provider or put business orchestration in MainActivity/composition root.
- NEVER implement FT-006/FT-007 behavior or add secrets/dependencies/modules/edges.

## Scope

### In scope

- Existing SettingsCapability, TimerCapability, DisplayCapability, FoundationRuntime/resource values and task-local host tests as needed for the same outcome.

### Out of scope

- All forbidden/anti-goal areas in the task card and unrelated brownfield cleanup.

## Proposed changes

### Touched areas (hypotheses)

- `settings/SettingsCapability.kt` — validated preset model, owner-local store and projection.
- `timer/TimerCapability.kt` — public read/selected-active projection and preset start boundary only as required by AC.
- `display/DisplayCapability.kt` — labels, fixed outlines, selected/active presentation and bounded preset interaction.
- `app/FoundationRuntime.kt` — wiring only if required to pass the existing capability contracts.
- `res/values/` — only FT-005 strings/colors if required.
- `app/src/test/kotlin/com/hozayushka/app/` — deterministic isolated claim probes and boundary/static checks.

### Preflight-confirmed change surface

- Expected advisory hints retained; exact final files will be recorded after implementation.
- Additional files are allowed only for the same FT-005 outcome and will be justified.
- Hard `write_boundary`: not set.
- Forbidden scope / stop conditions: clear at preflight.

## Applicable quality gates

- [ ] `./gradlew clean assembleDebug` — clean Android debug build.
- [ ] `./gradlew testDebugUnitTest` — host preset validation/persistence/presentation/integration tests.
- [ ] bounded static/boundary/redaction scans — public-edge ownership and synthetic/redacted evidence.
- [ ] `git diff --check` — changed-file whitespace integrity.

## Claim-linked RED / GREEN (T2/T3)

- applicability: applicable
- accepted claim locators: `FT-005-AC-001 / REQ-011`; `FT-005-AC-002 / REQ-011`; `FT-005-AC-003 / REQ-011`; `FT-005-AC-004 / REQ-011`.
- planned probe: deterministic host tests over isolated in-memory/resettable Settings owner state, Timer read projection, synthetic presentation values and boundary/static scans.
- RED: record the pre-implementation absence of each accepted capability before production changes in `progress.md` and `.tasks/TASK-007-T3-FT-005-W6/red-baseline.md`.
- GREEN: claim-equivalent host tests and required gates after production changes; verifier-owned proof remains due.
- T3 isolation/cleanup/permission: no live credentials or external side effects; each probe creates fresh stores and resets/disposes them in cleanup.

## MB-SYNC handoff / owner

- Owner: `/verify TASK-007-T3-FT-005-W6`, then per-task `/red-verify`; `/exe` does not run them.
- `.memory-bank/` planning/spec/index/task lifecycle files remain outside implementation handoff except the task card's required `ready → in_progress` transition.

## Definition of done

- Current attempt has honest RED/GREEN evidence for every owned AC, mandatory host/build/static/boundary checks are recorded, target is `DEFERRED` if unavailable with residual risk, exact changed files are listed, and handoff recommends `/verify TASK-007-T3-FT-005-W6` while task remains `in_progress`.
