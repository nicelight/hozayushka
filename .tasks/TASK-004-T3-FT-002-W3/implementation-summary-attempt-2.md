---
description: Compact current attempt-2 implementation handoff for TASK-004-T3-FT-002-W3.
status: active
---
# Implementation Summary — TASK-004-T3-FT-002-W3 — Attempt 2

## Correction surface

- `app/src/main/kotlin/com/hozayushka/app/display/DisplayCapability.kt` —
  pressure-arrow TextView now uses the existing card `PseudoGlassMaterial`
  result, matching the temperature text application.
- `app/src/main/kotlin/com/hozayushka/app/settings/SettingsCapability.kt` —
  changed persisted valid locations invoke the existing refresh request seam.
- `app/src/main/kotlin/com/hozayushka/app/app/FoundationRuntime.kt` —
  lifecycle wiring routes location-change and 30-minute signals to
  `WeatherCapability.refreshIfNeeded`; cadence/freshness remains Weather
  Context-owned and the schedule is stopped on pause.
- `app/src/test/kotlin/com/hozayushka/app/FoundationProbesTest.kt` and
  `WeatherContextTest.kt` — callback and trigger semantics are covered by
  deterministic host tests.

No task identity, tier, dependency, scheduler checkpoint/run status, lifecycle
status, new dependency, graph edge or FT-003..FT-009 source was changed.
