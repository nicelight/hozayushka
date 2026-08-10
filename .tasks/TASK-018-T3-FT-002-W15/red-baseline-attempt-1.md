---
description: Claim-specific pre-implementation RED baseline for TASK-018-T3-FT-002-W15 attempt 1.
status: supporting-only
---
# RED baseline — attempt 1

Command:

```text
set -e
app_root='app/src/main'
if rg -q 'api\.weather\.yandex\.ru|YandexWeatherAdapter|X-Yandex-Weather-Key' "$app_root"; then exit 1; else echo 'RED transport: no Yandex endpoint/adapter/header path in production source'; fi
if rg -q 'android\.permission\.INTERNET' app/src/main/AndroidManifest.xml; then exit 1; else echo 'RED wiring: manifest has no INTERNET permission'; fi
if rg -q 'provider = RedactedWeatherFixtureAdapter\(\)' app/src/main/kotlin/com/hozayushka/app/app/FoundationRuntime.kt; then echo 'RED wiring: FoundationRuntime selects redacted fixture provider'; else exit 1; fi
if rg -q 'weatherRefreshHandler\.postDelayed|weather\.refreshIfNeeded' app/src/main/kotlin/com/hozayushka/app/app/FoundationRuntime.kt && ! rg -q 'ExecutorService|Executors\.' app/src/main/kotlin/com/hozayushka/app/app/FoundationRuntime.kt; then echo 'RED runtime: composition refresh path has no executor dispatch'; else exit 1; fi
if rg -q 'forecast|fact|tzinfo|parts|hours' app/src/test/resources/fixtures/redacted-weather.json; then exit 1; else echo 'RED mapping/fallback: no redacted Yandex-shaped production response fixture'; fi
if rg -q 'HttpURLConnection|URLConnection|Socket' app/src/main/kotlin/com/hozayushka/app/adapters/weather; then exit 1; else echo 'RED failure/cache: no production transport exists to map bounded failures'; fi
if rg -q 'source = "redacted-provider"' app/src/main/kotlin/com/hozayushka/app/weather/WeatherCapability.kt; then echo 'RED integration: Weather Context source remains fixture-oriented'; else exit 1; fi
```

Working directory: `/home/serg/Projects/Mobile_APPS/hozayushka`

Observed output:

```text
RED transport: no Yandex endpoint/adapter/header path in production source
RED wiring: manifest has no INTERNET permission
RED wiring: FoundationRuntime selects redacted fixture provider
RED runtime: composition refresh path has no executor dispatch
RED mapping/fallback: no redacted Yandex-shaped production response fixture
RED failure/cache: no production transport exists to map bounded failures
RED integration: Weather Context source remains fixture-oriented
```

Interpretation: the current production source is fixture-only and has no Yandex request/transport/parser, bounded production failure path, production-shaped optional-field proof, minimum permission or off-main refresh wiring. This is claim-specific baseline evidence, not a final verdict. The secret claim uses the accepted `RED_NOT_APPLICABLE` route because no real or user-like credential may be introduced.
