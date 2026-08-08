---
description: Current attempt-2 boundary evidence for TASK-004-T3-FT-002-W3.
status: active
---
# Boundary Review — TASK-004-T3-FT-002-W3 — Attempt 2

- attempt: 2
- receipt_status: current
- claim: correction remains inside the accepted capability graph and owner
  boundaries

Weather Context remains the owner of refresh cadence, provider access,
normalization, cache/history, freshness and failure projection. Settings &
Location persists the validated location and emits the existing
location-refresh request only after a changed value is persisted. The
Composition Root lifts lifecycle timing and network availability into the
existing Weather Context refresh contract; it does not write Weather Context
state or decide freshness. Main Display only consumes the projection and uses
the existing shared presentation material.

Exact static checks, from the project root, passed with exit `0`:

```text
if rg -n 'SharedPreferencesWeatherCacheStore|InMemoryWeatherCacheStore|WeatherProvider|ProviderWeatherData|WeatherCacheStore|cacheStore' app/src/main/kotlin/com/hozayushka/app/display; then exit 1; else rc=$?; test "$rc" -eq 1; fi
if rg -n 'TimerCapability|SettingsStateStore|SharedPreferencesTimerStateStore' app/src/main/kotlin/com/hozayushka/app/weather; then exit 1; else rc=$?; test "$rc" -eq 1; fi
rg -q 'trigger = WeatherRefreshTrigger.LOCATION_CHANGE' app/src/main/kotlin/com/hozayushka/app/app/FoundationRuntime.kt
rg -q 'trigger = WeatherRefreshTrigger.SCHEDULED' app/src/main/kotlin/com/hozayushka/app/app/FoundationRuntime.kt
rg -q 'postDelayed\(scheduledWeatherRefresh, WEATHER_REFRESH_CADENCE_MILLIS\)' app/src/main/kotlin/com/hozayushka/app/app/FoundationRuntime.kt
test "$(rg -n 'alpha = \(0\.65f \+ material\.fillAlpha / 255f\)\.coerceAtMost\(1f\)' app/src/main/kotlin/com/hozayushka/app/display/DisplayCapability.kt | wc -l)" -eq 2
if rg -n 'alpha = 0\.32f' app/src/main/kotlin/com/hozayushka/app/display/DisplayCapability.kt; then exit 1; else rc=$?; test "$rc" -eq 1; fi
git diff --check
```

No module, dependency, permission, public graph edge, private-storage bypass,
composition-root business owner or forbidden FT-003..FT-009 scope was added.
