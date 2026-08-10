---
description: Actual W15 change surface and hard-boundary audit for TASK-018-T3-FT-002-W15 attempt 1.
status: supporting-only
---
# Change surface — attempt 1

Production/test outcome files changed or created inside the hard boundary:

- `app/src/main/AndroidManifest.xml` — add minimum `INTERNET` permission.
- `app/src/main/kotlin/com/hozayushka/app/adapters/weather/WeatherProviderAdapter.kt` — preserve existing provider shapes; make the internal credential callback value-returning for adapter transport use.
- `app/src/main/kotlin/com/hozayushka/app/adapters/weather/YandexWeatherAdapter.kt` — Android/JDK transport, bounded timeout, status mapping, redacted request path and Yandex-shaped parser.
- `app/src/main/kotlin/com/hozayushka/app/app/FoundationRuntime.kt` — production/fixture composition and off-main refresh executor wiring only.
- `app/src/main/kotlin/com/hozayushka/app/weather/WeatherCapability.kt` — injected fixture-provider seam plus required daily/optional condition compatibility; cache/history/freshness ownership remains here.
- `app/src/test/kotlin/com/hozayushka/app/YandexWeatherAdapterTest.kt` — deterministic fake-transport/redacted integration tests.
- `app/src/test/resources/fixtures/yandex-redacted-weather.json` — redacted Yandex-shaped response fixture.

Workflow-owned outputs:

- `.memory-bank/tasks/TASK-018-T3-FT-002-W15.task.json` (`ready -> in_progress` only).
- `.protocols/TASK-018-T3-FT-002-W15/{context,plan,progress,verification,handoff}.md`.
- `.tasks/TASK-018-T3-FT-002-W15/*`.
- `PAPERCUTS/GPT-5 __ 08-10-2026 14.47.md` for the corrected task-index lookup.

Hard-boundary result: compliant. Forbidden historical task records, scheduler checkpoint, terminal state, build dependency files, Settings product surface, FT-003/FT-004/FT-008 behavior, timer/display behavior, credentials and live-I/O paths were not changed by W15. Pre-existing dirty files outside this list were preserved.
