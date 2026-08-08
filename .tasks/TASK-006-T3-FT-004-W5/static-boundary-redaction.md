---
description: Static, boundary and redaction evidence for TASK-006-T3-FT-004-W5.
status: final
---
# Static/boundary/redaction gates — TASK-006-T3-FT-004-W5

Command shape, from the project root:

```text
set -e
if rg -n 'Provider(Current|Daily|Hourly)Weather|ProviderWeatherData|ProviderWeather|SharedPreferences|WeatherCacheStore|KEY_DAILY|KEY_HOURLY|adapters\.weather' app/src/main/kotlin/com/hozayushka/app/forecast app/src/main/kotlin/com/hozayushka/app/display; then exit 1; fi
if sed -n '/private fun forecastCard/,/private fun presetButton/p' app/src/main/kotlin/com/hozayushka/app/display/DisplayCapability.kt | rg -n 'pressureArrow|Pressure trend arrow'; then exit 1; fi
if rg -n 'X-Yandex-Weather-Key|Bearer |BEGIN (RSA|OPENSSH|PRIVATE) KEY|apiKey\s*=|api_key\s*=' app/src/main app/src/test .tasks/TASK-006-T3-FT-004-W5; then exit 1; fi
```

Final bounded scans exited `0`:

- Forecast Sessions and Main Display contain no raw provider DTO,
  `WeatherProvider`, `SharedPreferences`, `WeatherCacheStore`, daily/hourly
  storage key or provider-adapter access.
- The shared `forecastCard` renderer contains no pressure-arrow branch; the
  existing pressure rendering remains confined to Main Display's accepted
  weather-card path.
- Source, test and task-local evidence scans contain no Yandex key header,
  bearer value, API-key assignment or private-key marker.
- No FT-005–FT-009 or TASK-005 reference was introduced in the FT-004
  implementation/test surface.

The accepted path is `Main Display → Forecast Sessions → Weather Context`; the
Weather Context owner remains responsible for normalization, completeness and
cache writes, and Forecast Sessions consumes only `WeatherReadPort`.
