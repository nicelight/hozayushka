---
description: Execution plan for TASK-011-T3-FT-009-W10.
status: active
---
# Plan — TASK-011-T3-FT-009-W10

## Goal

Deliver the one bounded FT-009 personalization outcome: Settings & Location
validates and auto-saves alert sound/volume and glass intensity, Main Display
renders a production-card live preview from the validated projection, and Timer
& Alert consumes sound/volume without writing Settings.

## Non-goals

- FT-007 completion-time overdue presentation, scheduling, ramp or dismissal.
- Weather normalization/cache/history, Weather Context private storage access
  or a Settings → Weather Context edge.
- Extra Settings controls, modal errors, realtime blur/refraction/heavy effects,
  backend/cloud/accounts, Google Services, events, new dependencies or reboot
  recovery.
- API-key changes, secret-bearing artifacts or target-runtime PASS claims.

## Inputs / source specs

- Task: `.memory-bank/tasks/TASK-011-T3-FT-009-W10.task.json`
- Feature/Epic: `FT-009-personalization-settings.md`, `EP-004-settings-location.md`
- REQ IDs: `REQ-019`, `REQ-020`, `REQ-021`
- Direct canonical specs: system architecture, Boundary Map, Capability
  Interfaces, Weather Card Presentation, Local Data, Platform Runtime and
  Runtime Verification.

## Constraints / invariants (MUST / NEVER)

- MUST keep Settings & Location as the sole mutable owner of validated
  sound/volume/glass values.
- MUST preserve last valid values on invalid input and show accepted errors
  inline without a modal.
- MUST make Main Display compose both production Today and preview from the
  same saved projection; preview reads existing Weather Context projection or
  `24 °C` fallback and makes no network request.
- MUST keep Timer & Alert read-only for the Settings projection; volume `0`
  suppresses only app-alert audio and visual overdue remains.
- NEVER add a module, dependency, graph edge, private-store bypass, direct
  platform-policy bypass, event boundary or unaccepted product behavior.

## Scope

### In scope

- `settings/`: validated personalization models, private persistence, UI and
  owning inline errors.
- `display/`: shared production-card material/intensity and Settings preview
  composition through existing Settings/Weather read contracts.
- `timer/`: only the existing read projection behavior needed to prove volume
  zero and platform-policy preservation.
- `app/`/resources: existing navigation/wiring and accepted strings only.
- task-scoped deterministic host tests and redacted evidence.

### Out of scope

- All task-card `forbidden_scope` and stop-condition items.

## Proposed changes

### Touched areas (hypotheses OK)

- `app/src/main/kotlin/com/hozayushka/app/settings/SettingsCapability.kt` —
  owner validation, persistence, projection and personalization controls.
- `app/src/main/kotlin/com/hozayushka/app/display/DisplayCapability.kt` —
  production material and Main Display-owned preview composition.
- `app/src/main/kotlin/com/hozayushka/app/app/MainActivity.kt` — route Settings
  through Main Display composition owner.
- `app/src/main/res/values/strings.xml` — accepted Russian labels/errors only.
- `app/src/test/kotlin/com/hozayushka/app/FT009PersonalizationTest.kt` —
  isolated claim-equivalent host probes.
- `.tasks/TASK-011-T3-FT-009-W10/` — RED/GREEN and gate evidence.

### Preflight-confirmed change surface

- Expected hints kept: Settings, Display, Timer, app/resources, tests and
  fixtures/evidence; only same-outcome files may be added.
- Additional same-outcome files/areas: `weather/` only if a shared
  presentation pure model must remain owned by the existing Weather Card
  Presentation boundary; no weather-state or provider logic is authorized.
- Hard `write_boundary`: not set.
- `forbidden_scope` / stop-condition check: clear at start.

## Applicable quality gates

- [ ] `./gradlew clean assembleDebug` — clean Android debug build.
- [ ] `./gradlew testDebugUnitTest` — deterministic host/unit and presentation
  probes.
- [ ] `node scripts/mb-lint.mjs` — durable documentation integrity.
- [ ] scoped static/boundary/redaction checks and `git diff --check` — no
  forbidden dependency/edge/private-store/policy bypass or secret artifact.
- [ ] `adb devices -l` and `emulator -list-avds` — classify target-only
  readability/static pseudo-glass evidence; unavailable target is
  `DEFERRED`/non-blocking.

## Claim-linked RED / GREEN (T2/T3)

- applicability: applicable
- accepted claim locators: `FT-009-AC-001`, `REQ-019`, `REQ-020`, `REQ-021`,
  Weather Card Presentation `#personalization-preview`, Timer and Alert →
  Settings and Location, and platform audio boundary.
- planned probe: isolated in-memory/SharedPreferences-like Settings state,
  deterministic Today/fallback presentation fixtures, no-network presentation
  factory, read-only Timer consumer double and resettable platform fixture.
- RED: current baseline has no task-owned validated glass projection/UI,
  persistence/reload, live preview or complete FT-009 claim evidence; existing
  FT-008/FT-007 behavior is preserved but not adopted as proof.
- GREEN: claim-equivalent tests plus clean build, full unit suite and scoped
  static/presentation evidence after implementation.
- T3 isolation: no live credential/network/device side effect; synthetic or
  in-memory state is reset between probes and evidence contains no secrets.

## MB-SYNC handoff / owner

- Owner identified: `/verify` then explicit lifecycle owner.
- `.memory-bank/` docs needing update: none expected for this bounded outcome;
  protocol/task evidence is the execution handoff.
- Task registry/status owner: `/exe` owns only selected `ready → in_progress`;
  closure remains outside this command.

## Definition of done

- Accepted FT-009 behavior is implemented inside the registered boundaries,
  mandatory host/build/unit/static/presentation evidence is recorded, target
  status is `DEFERRED` if unavailable, and handoff routes to `/verify`.
