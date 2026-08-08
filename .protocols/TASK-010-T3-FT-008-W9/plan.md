---
description: Execution plan for TASK-010-T3-FT-008-W9.
status: active
---
# Plan — TASK-010-T3-FT-008-W9

## Goal

Deliver one bounded Settings & Location outcome for local weather access and
offline location selection, while preserving the accepted Settings owner,
Weather Context refresh owner, immutable catalog and secret-redaction rules.

## Non-goals

- FT-002 weather normalization/cache/history/freshness or weather-card behavior.
- FT-001 display, FT-003/FT-004 forecast sessions and FT-005–FT-007 timer/alert
  behavior.
- FT-009 alert/glass personalization.
- Backend/cloud/accounts, Google Services, a second location source, event
  infrastructure, new dependencies, shared/embedded credentials or private
  storage bypasses.

## Inputs / source specs

- Task: `.memory-bank/tasks/TASK-010-T3-FT-008-W9.task.json`
- Feature/Epic: `FT-008-weather-location-settings.md`, `EP-004-settings-location.md`
- REQ IDs: `REQ-017`, `REQ-018`, `REQ-024`
- Direct canonical specs: capability interfaces, boundary map, weather
  provider, local-data, local-secret-handling, platform-runtime and runtime
  verification.

## Constraints / invariants (MUST / NEVER)

- MUST keep Settings & Location as the mutable owner of key and selected
  country/city/coordinates.
- MUST request Weather Context refresh only after a valid persisted location
  write; Weather Context remains owner of provider refresh/cache/freshness.
- MUST keep the key ephemeral at the provider path and redacted from source,
  APK, logs, fixtures and evidence.
- MUST keep country-first offline search and selected-country city scoping.
- NEVER add a dependency, event boundary, backend, Google Services, direct
  adapter/private-store access or composition-root business orchestration.

## Scope

### In scope

- `settings/`: key/location models, validation, local persistence, bundled
  catalog query and Settings screen inline errors/attribution/navigation.
- `weather/`, `adapters/weather/`: authorized coordinate/key request seam and
  redacted provider success/failure result handling.
- `app/`, `display/`, manifest/resources: composition/navigation wiring only.
- `assets/geonames/` and task-scoped tests/evidence: deterministic catalog and
  redacted provider fixtures.

### Out of scope

- All task-card `forbidden_scope` and stop-condition items.

## Preflight-confirmed change surface

- Expected hints kept: Settings, Weather, weather adapter, Main Activity or
  composition wiring, bundled catalog asset, tests and redacted evidence.
- Additional same-outcome files/areas: manifest only if the provider boundary
  needs network permission; no unrelated module changes are authorized.
- Hard `write_boundary`: not set.
- `forbidden_scope` / stop-condition check: clear at start.

## Applicable quality gates

- [ ] `./gradlew clean assembleDebug` — clean Android debug build.
- [ ] `./gradlew testDebugUnitTest` — deterministic FT-008 host/unit probes.
- [ ] scoped static boundary/secret/redaction and `git diff --check` checks.
- [ ] `adb devices -l` — classify target-only Settings readability/navigation
  evidence; unavailable target is `DEFERRED`, non-blocking.

## Claim-linked RED / GREEN (T2/T3)

- applicability: applicable
- accepted claim locators: FT-008-AC-001 through FT-008-AC-006; REQ-017,
  REQ-018, REQ-024; local-secret and Settings/Weather/Catalog contract anchors.
- planned probe: isolated in-memory Settings, deterministic bundled catalog,
  synthetic credential, redacted provider fixture and resettable state.
- RED: current baseline lacks task-owned key persistence/secret proof, Khujand
  selected-location/catalog/alias/attribution surface and valid-coordinate
  refresh/failure-preservation path.
- GREEN: claim-equivalent host probes plus clean build, full unit suite and
  static/redaction evidence after implementation.
- T3 isolation: no live credentials; synthetic credential exists only in
  process memory and is never written to logs/evidence; tests reset disposable
  stores.

## MB-SYNC handoff / owner

- Owner identified: `/verify` then explicit lifecycle owner.
- `.memory-bank/` docs needing update: none expected for this bounded outcome;
  protocol/task evidence is the execution handoff.
- Task registry/status owner: `/exe` owns only selected `ready → in_progress`;
  closure remains outside this command.

## Definition of done

- Accepted FT-008 outcome is implemented inside the registered boundaries,
  mandatory host/build/unit/static/redacted evidence is recorded, target
  status is `DEFERRED` if unavailable, and handoff routes to `/verify`.
