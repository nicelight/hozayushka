---
description: Scheduler-recorded task-local defect preventing TASK-005 closure.
status: active
last_updated: 2026-08-08
source_of_truth: .tasks/TASK-005-T3-FT-003-W4/TASK-005-T3-FT-003-W4-S-RED-VERIFY-final-report-docs-01.md
---
# TASK-005 hourly provider-shape defect

## Observed failure

The required hostile semantic probe supplied a supported two-day provider
payload with 48 hourly records. It contained every accepted city-local slot,
but `WeatherCapability` rejected the response because normalization currently
requires `hourly.size == expected.size` (exactly eight raw records).

Evidence:

- `.tasks/TASK-005-T3-FT-003-W4/ProviderHourlyShapeProbe.java`
- `.tasks/TASK-005-T3-FT-003-W4/TASK-005-T3-FT-003-W4-S-RED-VERIFY-final-report-docs-01.md`
- `.protocols/TASK-005-T3-FT-003-W4/red-verification.md`

Observed probe result: `provider_hourly_count=48`,
`accepted_slots_present=true`, `refresh_result=NULL`,
`hourly_projection=NULL`.

## Required recovery

Route through the normal indexed task-planning/repair owner. The repair must
select the accepted eight slots from a supported full-day provider shape without
weakening completeness, then obtain fresh `/exe`, `/verify`, and `/red-verify`
evidence. Do not claim target runtime PASS; Android target evidence remains
DEFERRED until a device is available.

Scheduler disposition: TASK-005 failed after the configured initial attempt plus
two retries; no fourth retry is permitted in the current run.
