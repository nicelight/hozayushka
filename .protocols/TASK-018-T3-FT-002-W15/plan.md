---
description: Execution plan for TASK-018-T3-FT-002-W15.
status: active
---
# Plan — TASK-018-T3-FT-002-W15

## Goal
Wire a production Yandex REST adapter behind the accepted provider boundary with deterministic host/redacted proof and no live credentials or device claims.

## Non-goals
- No WeatherProvider/WeatherProviderRequest/WeatherReadPort public-shape change.
- No cache/history/freshness/fallback ownership move.
- No FT-003/FT-004/FT-008 feature ownership, Settings product surface, forecast-session, timer or display behavior.
- No dependency, backend, extra permission, live request, emulator or target-device process.

## Inputs / source specs
- Task record: `.memory-bank/tasks/TASK-018-T3-FT-002-W15.task.json`
- Task index: `.memory-bank/tasks/index.json`
- Feature/REQ: `.memory-bank/features/FT-002-weather-cards-context.md`, `.memory-bank/requirements.md`
- Canonical: provider, local-secret, platform-runtime, boundary-map, capability-interfaces, system-architecture, runtime-verification.
- Planning approval: `.tasks/TASK-MB-REVIEW-TASKS-PLAN/TASK-MB-REVIEW-TASKS-PLAN-S-TASKS-FT-002-final-report-docs-01.md`

## Constraints / invariants
- MUST use `https://api.weather.yandex.ru/v2/forecast`, lat/lon, `hours=true`, and `X-Yandex-Weather-Key` only in the request header.
- MUST preserve selected-city API timezone and existing Weather Context normalization/cache/fallback semantics.
- MUST map finite timeout/I/O/status/malformed failures to existing failure categories without replacing the last successful cache.
- MUST keep production refresh off the UI thread while preserving synchronous provider calls for deterministic host tests.
- NEVER write credentials to URLs, source/resources, logs, fixtures, screenshots or evidence.

## Scope
### In scope
- Manifest minimum `INTERNET` beside existing `ACCESS_NETWORK_STATE`.
- Adapter transport/parser and fake transport seam in `adapters/weather/`.
- Weather Context fixture-provider isolation seam and existing DTO compatibility checks.
- Composition-root Yandex/fixture wiring and off-main refresh dispatch.
- Redacted deterministic tests/fixtures/evidence under allowed test roots and `.tasks/`.

### Out of scope
- All forbidden task scope and all historical task/scheduler/terminal artifacts.

## Proposed changes
### Touched areas
- `app/src/main/kotlin/com/hozayushka/app/adapters/weather/` — Yandex transport and response mapping.
- `app/src/main/kotlin/com/hozayushka/app/weather/WeatherCapability.kt` — isolate foundation fixture provider without moving owner logic.
- `app/src/main/kotlin/com/hozayushka/app/app/` — production/fixture wiring and off-main executor dispatch only.
- `app/src/main/AndroidManifest.xml` — minimum network permission.
- `app/src/test/kotlin/com/hozayushka/app/`, `app/src/test/resources/fixtures/` — deterministic redacted request/mapping/failure/wiring probes.

### Preflight-confirmed change surface
- Expected hints kept: yes; extra files, if any, remain under the same adapter/test outcome.
- Additional same-outcome files/areas: none currently.
- Hard `write_boundary` present and satisfied: yes.
- `forbidden_scope` / stop-condition check: clear.
- Existing dirty overlap: W14 changes in `WeatherCapability.kt` and tests are preserved and not reverted.

## Applicable quality gates
- [ ] `./gradlew clean assembleDebug` — clean Android debug assembly.
- [ ] `./gradlew testDebugUnitTest` — host adapter/weather-context and regression tests.
- [ ] `node scripts/mb-lint.mjs` — Memory Bank/task integrity.
- [ ] deterministic redacted/static scan — request, permission, fixture isolation and secret artifact proof.

## Claim-linked RED / GREEN (T2/T3)
- applicability: applicable for transport, mapping, failure/cache, optional fallback and wiring; accepted not-applicable route for secret claim.
- accepted claim locators: `FT-002-AC-002`, `FT-002-AC-004`, `FT-002-AC-006`, `FT-002-AC-007`, Weather Provider Boundary, Platform Runtime Boundary Ownership.
- planned probe: fake transport captures only redacted request metadata; redacted Yandex-shaped fixture parses to existing DTOs; failure sequence compares cache before/after; source/resource/APK/evidence scan uses synthetic credential only; static executor/manifest/fixture route proof.
- RED: current source has fixture-only composition, no `INTERNET`, no Yandex transport/request parser and no production-shaped integration proof.
- accepted not-applicable reason: meaningful pre-change secret RED would require a real/user-like credential, forbidden by Local Secret Handling; alternative proof is synthetic in-memory header observation plus redacted durable scans.
- T3 isolation/safe rerun: fake transport and in-memory caches only; no live URL execution; synthetic credential never serialized; fixture provider cannot access production transport.

## MB-SYNC handoff / owner
- Owner identified: none for this `/exe` handoff; user explicitly forbids `/mb-sync` and lifecycle closure.
- `.memory-bank/` docs needing update: no product/spec change; task protocol/evidence only.
- Task registry/status update owner: `/exe` only for `ready -> in_progress`; lifecycle owner remains downstream.
- Changelog update owner: none.

## Definition of done
- Production adapter, wiring, permission and deterministic host/redacted evidence are complete within boundary.
- Required host/build/lint gates are recorded, target-device evidence is `DEFERRED`, and handoff routes independently to `/verify TASK-018-T3-FT-002-W15`, then `/red-verify` after functional PASS.
