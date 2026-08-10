---
description: Actual change surface for TASK-018-T3-FT-002-W15 attempt 2.
status: supporting-only
---
# Change surface — attempt 2

Production correction:

- `app/src/main/kotlin/com/hozayushka/app/weather/WeatherCapability.kt` —
  validate full ten-day day/night conditions before normalization; reject
  non-empty incomplete hourly data and reject an empty hourly result when it
  would replace an existing successful hourly cache. Weather Context remains
  the sole normalization/cache owner.

Host regression tests:

- `app/src/test/kotlin/com/hozayushka/app/WeatherContextTest.kt` — add the two
  deterministic cache-preservation tests and a ten-day fixture helper. Existing
  W14/W15 test content was preserved; no resource fixture was needed.

Workflow-owned durable evidence:

- `.protocols/TASK-018-T3-FT-002-W15/context.md`
- `.protocols/TASK-018-T3-FT-002-W15/progress.md`
- `.protocols/TASK-018-T3-FT-002-W15/handoff.md`
- `.tasks/TASK-018-T3-FT-002-W15/red-correction-attempt-2.md`
- `.tasks/TASK-018-T3-FT-002-W15/host-gates-attempt-2.md`
- `.tasks/TASK-018-T3-FT-002-W15/static-boundary-redaction-attempt-2.md`
- `.tasks/TASK-018-T3-FT-002-W15/changed-files-attempt-2.md`
- `.tasks/TASK-018-T3-FT-002-W15/TASK-018-T3-FT-002-W15-S-EXE-final-report-code-02.md`

Hard-boundary result: compliant. No feature/lifecycle/acceptance record for
FT-003, FT-004 or FT-008, no scheduler checkpoint or terminal-state artifact,
no public provider contract, dependency, permission, secret or live-I/O path
was changed.
