---
description: Fresh adversarial semantic evidence for TASK-018-T3-FT-002-W15 attempt 2.
status: supporting-only
task_id: TASK-018-T3-FT-002-W15
attempt: 2
role: Reviewer
---

# Red-verifier evidence — attempt 2

## Prior finding 1: full-daily completeness

- Current `WeatherCapability.kt:568-583` loads the previous record, evaluates
  required structured-data predicates, and returns `null` before
  `normalize()` at line 584 and `saveRecord()` at line 601.
- `hasCompleteDaily` at lines 753-761 requires exactly ten ordered city-local
  dates, both temperatures and both non-blank day/night conditions for every
  day.
- Fresh `WeatherContextTest.incompleteFullDailyConditionDataDoesNotReplaceSuccessfulCache`
  passed. It removes one required night condition after a valid cache, observes
  rejected refresh, unchanged long-term projection and unchanged snapshot.
- Result: prior finding is fixed on the supported provider-response path.

## Prior finding 2: hourly cache preservation

- Current `WeatherCapability.kt:579-580` rejects non-empty incomplete hourly
  data through `hasCompleteHourly` and rejects empty hourly data when a prior
  hourly cache exists, before normalization/cache replacement.
- `hasCompleteHourly` at lines 744-750 requires every accepted slot to have
  temperature and a non-blank condition.
- Fresh `WeatherContextTest.emptyHourlyPayloadDoesNotReplaceSuccessfulHourlyCache`
  passed for both an empty payload and a payload missing one required condition;
  each retained the prior hourly projection and snapshot.
- Result: prior finding is fixed on both tested incomplete variants.

## Hostile boundary review

- Existing provider/request/result/read-port symbols and accepted graph remain;
  no public contract or second source of truth appeared.
- Production composition selects `YandexWeatherAdapter`, injects a separate
  `RedactedWeatherFixtureAdapter`, and submits launch/location/scheduled refresh
  work to one JDK executor. Composition root contains wiring only; Weather
  Context owns normalization/cache/failure behavior.
- Manifest scan found exactly `ACCESS_NETWORK_STATE` and `INTERNET`; no Gradle
  dependency/plugin/property delta exists.
- Fake-transport tests observe the accepted endpoint/query/header without
  retaining a credential value. Fixture route test proves zero production
  transport calls. Source/test/APK/W15 evidence redaction scans found no review
  token or literal user credential.
- W15 production surface contains no FT-003/FT-004/FT-008 business ownership
  markers; downstream checks remain compatibility regressions only.

No material semantic break or operator-owned question was evidenced.

