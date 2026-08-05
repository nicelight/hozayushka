# Boundary review — attempt 1

## Claims

- `system-architecture.md#AD-001`: one deployable app and one composition
  root with accepted capability discovery roots.
- `system-architecture.md#AD-002` and
  `boundary-map.md#dependency-graph`: mutable state stays behind its owner and
  only accepted in-process imports are present.
- `system-architecture.md#AD-003`: composition root only wires/lifecycles;
  no screen/helper/event-bus business orchestration was added.

## Exact inspection command

```text
find app/src/main/kotlin/com/hozayushka/app -mindepth 1 -maxdepth 1 -type d -printf '%f\n' | sort; rg -n '^import com\.hozayushka\.app' app/src/main/kotlin/com/hozayushka/app --glob '*.kt' | sort; if find app/src/main/kotlin/com/hozayushka/app -type d \( -name core -o -name common -o -name repository -o -name repositories -o -name services -o -name eventbus \) -print | rg .; then printf '%s\n' 'forbidden technical/shared root found'; exit 1; else printf '%s\n' 'no shared technical/event root'; fi; if rg -n -i 'eventbus|event bus|retrofit|ktor|room|workmanager|backend|google services|broadcastreceiver' app/src/main/kotlin app/src/main/AndroidManifest.xml app/build.gradle.kts; then printf '%s\n' 'forbidden boundary token found'; exit 1; else printf '%s\n' 'no event/backend/extra-runtime boundary token'; fi
```

## Observed result

```text
adapters
app
display
forecast
settings
timer
weather
app/src/main/kotlin/com/hozayushka/app/app/FoundationRuntime.kt:4:import com.hozayushka.app.adapters.platform.PlatformRuntimeAdapter
app/src/main/kotlin/com/hozayushka/app/app/FoundationRuntime.kt:5:import com.hozayushka.app.adapters.weather.RedactedWeatherFixtureAdapter
app/src/main/kotlin/com/hozayushka/app/app/FoundationRuntime.kt:6:import com.hozayushka.app.display.DisplayCapability
app/src/main/kotlin/com/hozayushka/app/app/FoundationRuntime.kt:7:import com.hozayushka.app.forecast.ForecastSessionCapability
app/src/main/kotlin/com/hozayushka/app/app/FoundationRuntime.kt:8:import com.hozayushka.app.settings.SettingsCapability
app/src/main/kotlin/com/hozayushka/app/app/FoundationRuntime.kt:9:import com.hozayushka.app.settings.SharedPreferencesSettingsStateStore
app/src/main/kotlin/com/hozayushka/app/app/FoundationRuntime.kt:10:import com.hozayushka.app.timer.SharedPreferencesTimerStateStore
app/src/main/kotlin/com/hozayushka/app/app/FoundationRuntime.kt:11:import com.hozayushka.app.timer.TimerCapability
app/src/main/kotlin/com/hozayushka/app/app/FoundationRuntime.kt:12:import com.hozayushka.app.weather.SharedPreferencesWeatherCacheStore
app/src/main/kotlin/com/hozayushka/app/app/FoundationRuntime.kt:13:import com.hozayushka.app.weather.WeatherCapability
app/src/main/kotlin/com/hozayushka/app/display/DisplayCapability.kt:8:import com.hozayushka.app.R
app/src/main/kotlin/com/hozayushka/app/display/DisplayCapability.kt:9:import com.hozayushka.app.adapters.platform.PlatformRuntime
app/src/main/kotlin/com/hozayushka/app/forecast/ForecastSessionCapability.kt:3:import com.hozayushka.app.adapters.platform.PlatformRuntime
app/src/main/kotlin/com/hozayushka/app/forecast/ForecastSessionCapability.kt:4:import com.hozayushka.app.weather.WeatherReadPort
app/src/main/kotlin/com/hozayushka/app/weather/WeatherCapability.kt:4:import com.hozayushka.app.adapters.weather.WeatherProvider
app/src/main/kotlin/com/hozayushka/app/weather/WeatherCapability.kt:5:import com.hozayushka.app.adapters.weather.WeatherProviderRequest
app/src/main/kotlin/com/hozayushka/app/weather/WeatherCapability.kt:6:import com.hozayushka.app.settings.LocationReader
no shared technical/event root
no event/backend/extra-runtime boundary token
```

## Review conclusion

- `FoundationRuntime` is the only composition root and creates one Android
  `app` deployment.
- `display`, `weather`, `forecast`, `timer`, `settings`,
  `adapters/platform` and `adapters/weather` are the only main code roots.
- Settings, weather and timer each own a private persistence adapter; no shared
  storage module exists.
- Weather Context depends on Settings and the weather adapter; Forecast depends
  on Weather and the platform adapter; Display depends on the platform adapter;
  the composition root wires the graph. No absent event/backend edge was added.
- The visible shell is Foundation-only and does not implement FT-001–FT-009
  acceptance behavior.

## Attempt 2 correction review

The retry adds only the supported Foundation probe route inside the existing
accepted graph:

- `Main Display` now presents explicit Foundation-only probe controls and
  calls the public Settings, Weather and Timer owners; the dependency rows are
  already registered in `boundary-map.md`.
- `Timer & Alert` calls the existing Android Runtime Adapter boundary for
  lifecycle rehydration and policy-aware audio probing; no second audio or
  storage owner exists.
- `FoundationRuntime` remains the only composition root and only wires the
  owners plus Activity lifecycle callbacks. It does not store product state or
  orchestrate a new event/message path.
- No technical/shared root, backend, event bus, new dependency, manifest
  permission or real credential was added.

The corrected production call-site scan found Settings seed/reset, Weather
fixture refresh, Timer start/cancel/rehydrate and AudioManager/ToneGenerator
hooks under `app/src/main`; the same operations remain covered by the host
probe test. This review is executor-owned supporting evidence for Attempt 2.

## Attempt 3 boundary correction review

The current correction removes the material adversarial finding without
changing the registered graph:

- `DisplayCapability.kt` imports `WeatherCapability` but does not import
  `com.hozayushka.app.adapters.weather`, `WeatherProviderRequest`, or
  `fromSyntheticProbe`.
- The installed-app weather button calls
  `WeatherCapability.refreshFoundationFixture()` through the registered
  `Main Display → Weather Context` boundary.
- `WeatherCapability.kt` is the owner that constructs the synthetic request and
  invokes the existing provider-backed refresh, preserving
  `Weather Context → Yandex Weather Adapter` as the only provider edge.
- `FoundationProbesTest` exercises the same owner method for the Foundation
  weather action; its separate provider fixture test still enters the adapter
  only through `WeatherCapability`.

Exact targeted checks:

```text
if rg -n '^import com\.hozayushka\.app\.adapters\.weather|WeatherProviderRequest|fromSyntheticProbe|weather\.refresh\(' app/src/main/kotlin/com/hozayushka/app/display/DisplayCapability.kt; then exit 1; else echo 'Display → weather adapter scan: clean'; fi
if rg -n '^import com\.hozayushka\.app\.adapters\.weather' app/src/main/kotlin/com/hozayushka/app/display app/src/main/kotlin/com/hozayushka/app/forecast app/src/main/kotlin/com/hozayushka/app/settings app/src/main/kotlin/com/hozayushka/app/timer; then exit 1; else echo 'non-owner weather-adapter import scan: clean'; fi
```

Observed result: both scans exited `0`; only Weather Context retains the
provider request import/construction. The correction introduces no new module,
dependency, event path, storage owner or provider contract.
