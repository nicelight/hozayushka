---
description: Correction-specific RED evidence for TASK-018-T3-FT-002-W15 attempt 2.
status: supporting-only
---
# Correction RED — attempt 2

Basis: scheduler-authorized same-task correction from
`TASK-018-T3-FT-002-W15-S-RED-VERIFY-final-report-docs-01.md` and
`.protocols/TASK-018-T3-FT-002-W15/red-verification.md`. The original attempt-1
RED remains retained as supporting-only evidence; it was not replayed.

Mapped claims:

- `FT-002-AC-004 / REQ-007 / REQ-025`: a failed required payload must not
  replace a successful Weather Context cache.
- `FT-002-AC-006 / REQ-026`: missing required full-daily condition inputs must
  remain unavailable; optional fallback must not fabricate them.
- Downstream hourly/long-term checks are compatibility regressions only.

Correction-specific probe added before the production fix:

```text
./gradlew testDebugUnitTest --tests com.hozayushka.app.WeatherContextTest.incompleteFullDailyConditionDataDoesNotReplaceSuccessfulCache --tests com.hozayushka.app.WeatherContextTest.emptyHourlyPayloadDoesNotReplaceSuccessfulHourlyCache
```

Observed exit `1`: both new tests failed against the pre-correction behavior
(`WeatherContextTest.kt:354` and `WeatherContextTest.kt:370`). The failures
were claim-specific: missing full-daily condition data was normalized through
the cache path, and an empty hourly payload was accepted after a successful
hourly cache.

No live network, credentials, emulator, ADB, connected-device Gradle task or
target-device process was used.
