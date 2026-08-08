---
description: Implementation plan for the FT-002 main weather cards and local context.
status: active
last_updated: 2026-08-06
---
# IMPL-FT-002 — Main weather cards and local context

## Goal

Deliver the accepted Weather Context outcome on top of the existing Android
scaffold and FT-001 shell: four ordered display-ready weather cards with the
accepted day/night, palette, pseudo-glass and pressure presentation, backed by
redacted provider normalization, successful local cache/freshness and
installation-relative history.

## Ordered work

1. `TASK-004-T3-FT-002-W3` — implement and verify the Weather Context outcome
   under the Weather Context capability owner. Use deterministic redacted host
   probes for mapping, presentation, freshness, history and fallback; use the
   accepted target-device route only for visual/runtime results host checks
   cannot establish.

## Primary owner and accepted graph

- Primary owner: `Weather Context`.
- Code root evidenced by the Foundation scaffold:
  `app/src/main/kotlin/com/hozayushka/app/weather`.
- Provider boundary crossed: Weather Context → Yandex Weather Adapter through
  [Weather Provider Boundary](../../contracts/weather-provider.md#weather-provider-boundary).
- Settings boundary crossed: Weather Context → Settings & Location through
  [Weather Context to Settings and Location](../../contracts/capability-interfaces.md#weather-context-to-settings-and-location).
- City-change orchestration uses the existing Settings & Location → Weather
  Context edge through
  [Location Refresh Orchestration](../../contracts/capability-interfaces.md#location-refresh-orchestration);
  Weather Context owns the refresh and resulting cache/history projection.
- Platform signal/wiring uses the existing Android Runtime Adapter boundary:
  Android OS owns device time, lifecycle and network availability; the
  Application Composition Root and Android Runtime Adapter only lift those
  signals through existing contracts. Weather Context owns refresh cadence,
  cache/freshness and failure projection. The direct basis is
  [Platform Runtime Boundary Ownership](../../contracts/platform-runtime.md#boundary-ownership)
  and [Platform Compatibility and Failure Rules](../../contracts/platform-runtime.md#compatibility-and-failure-rules).
  The proof path is the deterministic lifecycle/cache route in
  [Runtime Verification](../../testing/runtime-verification.md#deterministic-host-side-checks)
  plus the accepted target-device route only for residual runtime/readability
  results; no new graph edge is introduced.
- Display boundary crossed: Main Display → Weather Context through
  [Main Display to Weather Context](../../contracts/capability-interfaces.md#main-display-to-weather-context).
  Main Display renders the projection and never writes Weather Context state.
- Composition-root and platform changes, if required, remain signal/wiring
  only; no new Weather Context → Android Runtime graph edge is introduced.

## Scope

### In scope

- Normalized current/daily provider mapping with selected-city API timezone,
  current pressure, day/night temperature and optional moon/condition fields.
- Four ordered card records, Today sizing, filled/empty projection and the
  selected-city date/day-night/illustration rules.
- Explicit 78-entry temperature palette, sign/clamp behavior and shared static
  pseudo-glass for temperature and pressure arrows.
- Launch, valid-city-change and 30-minute network refresh; successful local
  cache through 24 hours offline and all-four-card stale contours after that.
- Installation-relative seven-day history, current/yesterday pressure arrows,
  3-hour/12-hour fallback and first-run dated Yesterday contour.
- Unknown-condition/optional-field fallback, missing-required-data preservation,
  redacted fixtures and the existing Main Display read projection.

### Out of scope

- FT-001 clock/date/fullscreen/colon/city-gesture behavior beyond consuming the
  existing Main Display weather projection seam.
- Hourly and long-term forecast session data or UI, timer/preset/overdue logic,
  Settings catalog/API-key input/validation, and personalization controls.
- Backend, cloud/accounts, Google Services, reboot recovery, live credentials,
  a second provider, a second persistence owner, heavy realtime effects or new
  dependencies/graph edges.

## Expected advisory change surface

- `app/src/main/kotlin/com/hozayushka/app/weather/`
- `app/src/main/kotlin/com/hozayushka/app/adapters/weather/`
- `app/src/main/kotlin/com/hozayushka/app/settings/` (existing public input seam only)
- `app/src/main/kotlin/com/hozayushka/app/display/`
- `app/src/main/kotlin/com/hozayushka/app/app/` (wiring only)
- `app/src/main/kotlin/com/hozayushka/app/adapters/platform/` (accepted signal seam only)
- `app/src/main/res/`
- `app/src/test/kotlin/com/hozayushka/app/`
- `app/src/test/resources/fixtures/`

These paths are advisory and non-exhaustive; no hard write boundary is set.

## Applicable quality gates and UAT

- `./gradlew clean assembleDebug`
- `./gradlew testDebugUnitTest`
- Deterministic redacted provider, freshness, history, palette and boundary
  probes from [Runtime Verification](../../testing/runtime-verification.md).
- Scoped target-device observation for 1280×720 readability and static glass
  only where host checks cannot prove the result. No such evidence is created
  by this planning run.

## Constraints and verification targets

- Preserve the owner/write matrix and accepted dependency graph in
  [Boundary Map](../../contracts/boundary-map.md#dependency-graph).
- Preserve the accepted Android platform signal/wiring ownership and failure
  route in [Platform Runtime](../../contracts/platform-runtime.md#boundary-ownership)
  and [Platform Compatibility and Failure Rules](../../contracts/platform-runtime.md#compatibility-and-failure-rules).
- Preserve the device-time versus selected-city API-timezone split in
  [System Architecture](../../architecture/system-architecture.md#architecture-spine)
  and [Local Data](../../domains/local-data.md#ft-002-weather-context-records).
- Preserve successful-cache-only freshness and seven-day history in
  [Lifecycle Map](../../states/lifecycle-map.md#weather-freshness-contract) and
  [Local Data](../../domains/local-data.md#retention-and-cleanup).
- Keep provider credentials synthetic/redacted throughout the provider and
  evidence path under [Local API-Key Handling](../../contracts/local-secret-handling.md#local-api-key-handling-contract).
- Every FT-002 AC is owned by the task card with a claim-specific RED/GREEN
  comparison; no dependency proof is inherited from FT-001 or Foundation.

## Handoff

The successful single-feature route is `/review-tasks-plan FT-002`. Execution is
not part of this plan.
