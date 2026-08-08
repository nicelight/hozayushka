---
description: Static boundary and redaction evidence for TASK-007-T3-FT-005-W6.
status: final
---
# Static / boundary / redaction gates — TASK-007-T3-FT-005-W6

## Command

```text
set -e
if rg -n 'SharedPreferences|WeatherCacheStore|Provider(Current|Daily|Hourly)Weather|ProviderWeatherData|adapters\.weather|WeatherProvider' app/src/main/kotlin/com/hozayushka/app/display; then exit 1; fi
if rg -n 'SettingsStateStore|SharedPreferencesSettingsStateStore|WeatherCapability|WeatherProvider|adapters\.weather|WeatherCacheStore' app/src/main/kotlin/com/hozayushka/app/timer; then exit 1; fi
if rg -n 'X-Yandex-Weather-Key|Bearer |BEGIN (RSA|OPENSSH|PRIVATE) KEY|apiKey\s*=|api_key\s*=' app/src/main app/src/test .tasks/TASK-007-T3-FT-005-W6; then exit 1; fi
if git diff --unified=0 -- app/src/main/kotlin/com/hozayushka/app/settings/SettingsCapability.kt app/src/main/kotlin/com/hozayushka/app/timer/TimerCapability.kt app/src/main/kotlin/com/hozayushka/app/display/DisplayCapability.kt app/src/main/kotlin/com/hozayushka/app/app/FoundationRuntime.kt app/src/main/res/values/colors.xml app/src/main/res/values/strings.xml app/src/test/kotlin/com/hozayushka/app/TimerPresetTest.kt | rg '^\\+.*(FT-006|FT-007|overdue|audio|X-Yandex|apiKey|api_key)'; then exit 1; fi
```

## Result

Exit `0`, `static_boundary_redaction=PASS`.

- Main Display consumes Settings and Timer projections only; it does not read
  private stores, raw weather DTOs or provider adapters.
- Timer's only persistence access remains its own private Timer & Alert store;
  its Settings interaction is `TimerPresetReader`.
- Composition-root wiring passes `presetReader = settings`; no business rule
  moved into `FoundationRuntime`.
- No new graph edge, dependency, event boundary or forbidden provider/raw-store
  access was introduced.
- No live key or secret-shaped value exists in implementation, tests or
  task-local evidence.
