# Attempt 1 — claim-linked RED baseline

Claim locators: `FT-003-AC-001 / REQ-009` and `FT-003-AC-005 / REQ-009, REQ-026`.

The pre-change task-owned proof surface contains one generic complete hourly
session test and one `dropLast(1)` incomplete case in `ForecastSessionTest`,
plus three single-provider/missing-field cases in `WeatherContextTest`. It has
no selected Open-Meteo/OpenWeather complete-entry matrix, no eight-position
per-provider matrix, and no elapsed current-day OpenWeather case. The current
implementation has selected-provider matching and exact-slot checks, but this
task's accepted two-provider/no-borrowing claim is not yet evidenced.

This is the task-card RED: absence of the required claim-specific proof, not a
setup or artificial failure. No production behavior was changed before this
observation. Target-device/live-provider proof remains prohibited/deferred.

Observed command:

```text
rg -n 'fun .*hourly|fun .*Hourly|OpenWeather|OPEN_WEATHER|missing.*slot|dropLast|filterNot' app/src/test/kotlin/com/hozayushka/app/WeatherContextTest.kt app/src/test/kotlin/com/hozayushka/app/ForecastSessionTest.kt
```

Decisive observations: `ForecastSessionTest` had only the generic complete
case and `dropLast(1)` rejection; the task-owned provider-specific entries and
all-eight missing-slot matrix were absent. Production `WeatherCapability.kt`
had `matchingRecord`, `hourlyProjection`, `hasCompleteHourly` and
`retainCompleteHourlySubset`, but no corresponding two-provider proof.
