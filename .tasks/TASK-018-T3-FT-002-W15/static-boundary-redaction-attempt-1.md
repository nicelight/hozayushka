---
description: Static composition, permission, dependency and secret-redaction evidence for TASK-018-T3-FT-002-W15 attempt 1.
status: supporting-only
---
# Static boundary and redaction — attempt 1

Command: deterministic `rg`/manifest-count scan from `/home/serg/Projects/Mobile_APPS/hozayushka` after host gates. A process-only synthetic token was generated and never emitted or written.

Observed:

```text
GREEN permission: INTERNET is the only added network permission beside ACCESS_NETWORK_STATE
GREEN wiring: production Yandex and isolated fixture providers are composed; refresh dispatch uses a single JDK executor
GREEN request boundary: endpoint, lat/lon, hours=true and header are present in adapter only
f94917b7c354d2d5da997f6d21d7e79b59117ad25a9ee49dbfd10dab5ecb701f  app/build/outputs/apk/debug/app-debug.apk
static boundary/redaction checks: PASS
synthetic credential scan: PASS (no value persisted; value not emitted)
```

Decisive conditions: manifest has exactly two permissions (`ACCESS_NETWORK_STATE`, `INTERNET`); no production dependency/plugin declaration was added; production composition selects `YandexWeatherAdapter`, injects `RedactedWeatherFixtureAdapter` separately and dispatches refresh through `Executors.newSingleThreadExecutor`; the dynamic synthetic token was absent from source, resources, task evidence, protocol evidence and APK. The request-shape test observes header presence and verifies the header value is not in the URL without retaining the value.

No real or user-like credential, live request, screenshot, log or target-device artifact was used.
