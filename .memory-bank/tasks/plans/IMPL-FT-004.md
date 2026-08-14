---
description: Revision-2 implementation plan for Open-Meteo 10 and OpenWeather 8+2 long-term projection.
status: active
last_updated: 2026-08-12
---
# IMPL-FT-004 — Provider-capability long-term forecast

## Outcome

Use one shared ten-position long-term session with provider-specific
completeness: Open-Meteo supplies 10 filled positions; OpenWeather supplies 8
filled positions and leaves positions 9–10 dated but unavailable/empty.

## Ordered work

1. Preserve terminal `TASK-006-T3-FT-004-W5` and its evidence unchanged.
2. Preserve W17's implemented selected-provider data facts separately from the
   failed activation outcome; W20's completed activation repair and W18's
   completed hourly completeness boundary remain upstream prerequisites.
3. Reconcile completed `TASK-022-T2-FT-004-W19` and its feature-level
   semantic-pass evidence without changing W5 history or the W17 failure.

## Ownership and execution path

Weather Context owns normalized selected-provider daily records and capability
metadata. Forecast Sessions owns entry and the ten-position session projection.
The bounded path is selected-city today + provider identity + daily records →
provider-specific completeness gate → one shared ten-position session or the
accepted unavailable result.

## Acceptance map

| Current claim | Owner | Proof |
|---|---|---|
| `AC-001 / REQ-010, REQ-026` | W19 | Tomorrow/Day-after require 10 Open-Meteo or 8 OpenWeather |
| `AC-002 / REQ-010, REQ-022` | W19 | Both providers retain ten dates from selected-city today |
| `AC-005 / REQ-010, REQ-026` | W19 | One-short sets remain on Main Display with exact message |
| `AC-006 / REQ-010, REQ-026` | W19 | Open-Meteo 10 filled; OpenWeather 8 filled + 2 empty |
| `AC-003 / REQ-010, REQ-022, REQ-026` | W5 done | Existing available-card presentation |
| `AC-004 / REQ-010` | W5 done | Existing shared exit flow |

## Advisory surface and proof

- `weather/WeatherCapability.kt` — capability-aware daily projection
- `forecast/ForecastSessionCapability.kt` — entry and ten positions
- `WeatherContextTest.kt`, `ForecastSessionTest.kt` and provider fixtures

RED is the current single ten-record threshold. GREEN is a deterministic
provider/entry-card matrix: 10/9 Open-Meteo, 8/7 OpenWeather, ten ordered dates,
and exact 10-filled versus 8-filled-plus-2-empty projection. Every empty or
missing record retains selected-provider identity and cannot be filled from
another provider or cache partition.

No hard `write_boundary` is selected. Project-native clean build and host
tests remain execution gates; this planning run performs no Gradle,
emulator/device or runtime evidence.

## Constraints

One shared screen, Open-Meteo 10, OpenWeather 8+2. Do not require ten
OpenWeather records, fabricate two records, hide positions 9–10, create
provider-specific screens, change existing presentation/gestures, or add a
provider/plugin abstraction.

## Direct normative inputs

- [.memory-bank/features/FT-004-ten-day-forecast.md](../../features/FT-004-ten-day-forecast.md)
- [.memory-bank/contracts/capability-interfaces.md](../../contracts/capability-interfaces.md)
- [.memory-bank/contracts/weather-provider.md](../../contracts/weather-provider.md)
- [.memory-bank/domains/local-data.md](../../domains/local-data.md)
- [.memory-bank/testing/runtime-verification.md](../../testing/runtime-verification.md)
- [.memory-bank/tasks/TASK-022-T2-FT-004-W19.task.json](../TASK-022-T2-FT-004-W19.task.json)

## W19 completion evidence

`TASK-022-T2-FT-004-W19` is `done` on the authorized host/build/static/redacted
route after fresh `/verify PASS` and feature-level FT-004 `semantic-pass`.
Closure metadata and claim-linked evidence are retained in the indexed task
record, including the [W19 handoff](../../../.protocols/TASK-022-T2-FT-004-W19/handoff.md),
[verification](../../../.protocols/TASK-022-T2-FT-004-W19/verification.md),
[RED baseline](../../../.tasks/TASK-022-T2-FT-004-W19/red-baseline.md),
[completeness matrix](../../../.tasks/TASK-022-T2-FT-004-W19/long-term-completeness-matrix.json)
and [FT-004 semantic report](../../../.tasks/FT-004/FT-004-S-RED-VERIFY-final-report-docs-01.md).

Target Android/custom-ROM rendering and live-provider/network compatibility
remain `DEFERRED`; no runtime `PASS` is claimed. Scheduler post-sync lint,
strict doctor, promotion and terminal-state handling remain caller-owned.
