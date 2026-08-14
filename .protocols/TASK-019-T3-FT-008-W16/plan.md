---
description: Execution plan for TASK-019-T3-FT-008-W16.
status: active
---
# Plan — TASK-019-T3-FT-008-W16

## Goal
Make Open-Meteo the persisted no-key Settings default, make OpenWeather an explicit owner-selected local-key context, keep failures provider-contextual and selection-stable, and place Open-Meteo plus GeoNames attribution before Back.

## Non-goals
- Provider HTTP transport, response mapping, selected-adapter dispatch, normalized cache/history identity, hourly completeness and long-term 8+2.
- Changes to W9 offline catalog/location ownership or FT-009 personalization behavior.
- A third provider, fallback, backend/proxy, account/cloud service, Google Services, registry/DI/event infrastructure, new dependency or live credential.

## Inputs / source specs
- Task record/index: `.memory-bank/tasks/TASK-019-T3-FT-008-W16.task.json`, `.memory-bank/tasks/index.json`
- Feature: `.memory-bank/features/FT-008-weather-location-settings.md`
- REQ IDs: `REQ-017`, `REQ-018`, `REQ-024`, `REQ-027`, `REQ-028`

## Richer execution inputs
- Exact claims: `FT-008-AC-001`, `FT-008-AC-006`, `FT-008-AC-007`, `FT-008-AC-008`
- Canonical rules: AD-006/AD-008; Boundary Map dependency/ownership; Weather Access Settings Surface; Location Refresh Orchestration; Weather Provider selection/credential/attribution; Local Secret Handling; Local Data durable rules; Runtime Verification redacted fixtures/artifact checks.
- Verification targets and evidence requirements are taken unchanged from the selected task card.

## Fallback basis
- Not applicable.

## Constraints / invariants (MUST / NEVER)
- MUST persist exactly `open_meteo|open_weather`, resolve first-run/unrecognized state to Open-Meteo and expose a key only in explicit OpenWeather context.
- MUST auto-save valid owner changes, preserve last valid provider/location/key state on invalid/failure paths and keep owning errors inline without fallback claims.
- MUST keep both attributions before the final Back action and preserve existing location/personalization controls.
- NEVER touch provider transport/dispatch/mapping/cache/history/forecast behavior or introduce a secret-bearing durable artifact.
- NEVER launch an emulator/AVD/QEMU; no device evidence is needed for this host-verifiable task.

## Scope
### In scope
- Settings owner state/persistence/public projection, contextual provider/key view behavior and attribution order.
- Accepted strings and one deterministic isolated host regression probe.
- Task-owned protocol/evidence and lifecycle `ready -> in_progress`.

### Out of scope
- TASK-020 through TASK-022 outcomes and all forbidden scopes from the card.

## Proposed changes
### Touched areas
- `app/src/main/kotlin/com/hozayushka/app/settings/SettingsCapability.kt` — provider state, contextual key projection, persistence, inline context and ordered view.
- `app/src/main/res/values/strings.xml` — accepted provider and Open-Meteo attribution text.
- `app/src/test/kotlin/com/hozayushka/app/SettingsLocationTest.kt` — claim-linked isolated RED/GREEN and W9 regression checks.

### Preflight-confirmed change surface
- Expected hints kept: all three advisory task paths.
- Additional same-outcome files/areas: none expected; task protocol/evidence and task-card lifecycle are workflow-owned bookkeeping.
- Hard `write_boundary` present and satisfied: not set.
- `forbidden_scope` / stop-condition check: clear.

## Applicable quality gates
- [x] Targeted RED/GREEN: `./gradlew testDebugUnitTest --tests "com.hozayushka.app.SettingsLocationTest"` — proves the four exact task-owned claims and W9 location regressions.
- [x] Clean Android debug build: `./gradlew clean assembleDebug` — proves packaged source/resources compile cleanly.
- [x] Host provider-settings and secret-safety probes: `./gradlew testDebugUnitTest` — proves all host regressions without device/network.
- [x] Memory Bank and diff integrity: `node scripts/mb-lint.mjs && git diff --check`.
- [x] Redacted static/package/evidence inspection using only pattern/field presence and no raw synthetic value in output.

## Claim-linked RED / GREEN (T2/T3)
- applicability: applicable
- accepted claim locators: `FT-008-AC-001`, `FT-008-AC-006`, `FT-008-AC-007`, `FT-008-AC-008`
- planned test/probe and environment: one host JUnit class with resettable in-memory Settings state, runtime-generated synthetic secret, provider-context failure matrix and deterministic content projection; no network/device.
- observable RED: no provider/default state or contextual key gating; no selected-provider error context; no Open-Meteo attribution/order entry.
- corresponding GREEN: first-run Open-Meteo/no key, explicit OpenWeather/key persistence/reopen, state-stable contextual failures, dual attribution before Back, existing location/personalization order preserved.
- accepted not-applicable reason and alternative proof: none.
- T3 isolation, safe rerun, cleanup, and permission boundary: owner-local in-memory store is reset in `finally`; raw marker never enters source/resources/log/evidence; only booleans/redacted observations are asserted; no external side effect.

## Fan-out plan
- None; subagents are forbidden for this Implementer assignment.

## MB-SYNC handoff / owner
- [x] Owner identified: scheduler
- [x] Explicit standalone owner basis: n/a
- [x] `.memory-bank/` docs needing update: only selected task status is owned by `/exe`; broader docs remain scheduler/`/mb-sync` owned
- [x] `.memory-bank/index.md` router update needed: no
- [x] RTM update needed: no
- [x] Task registry/status update owner: `/exe` starts; `/verify` and scheduler own forward lifecycle
- [x] Changelog update owner: scheduler/`/mb-sync`, not this run

## Definition of done
- Honest claim-linked RED precedes production edits; claim-equivalent GREEN and all required gates pass; exact files/evidence/scope/secret compliance are recorded; task remains `in_progress` for `/verify`.
