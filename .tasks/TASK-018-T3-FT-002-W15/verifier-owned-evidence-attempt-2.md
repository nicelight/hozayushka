---
description: Fresh verifier-owned host evidence for TASK-018-T3-FT-002-W15 attempt 2.
status: supporting-only
task_id: TASK-018-T3-FT-002-W15
attempt: 2
role: Reviewer
---

# Verifier-owned evidence — attempt 2

## Functional probes

- `./gradlew testDebugUnitTest --tests com.hozayushka.app.WeatherContextTest.incompleteFullDailyConditionDataDoesNotReplaceSuccessfulCache --tests com.hozayushka.app.WeatherContextTest.emptyHourlyPayloadDoesNotReplaceSuccessfulHourlyCache` — exit `0`, `BUILD SUCCESSFUL`.
- `./gradlew testDebugUnitTest` — exit `0`, `BUILD SUCCESSFUL`; fresh XML totals 65 tests, 0 skipped, 0 failures, 0 errors. This includes `YandexWeatherAdapterTest` (4), `WeatherContextTest` (15), and existing Settings/Forecast compatibility suites.
- `./gradlew testDebugUnitTest --tests com.hozayushka.app.YandexWeatherAdapterTest` — exit `0`, `BUILD SUCCESSFUL`.
- `./gradlew clean assembleDebug` — exit `0`, `BUILD SUCCESSFUL`; only the known pre-existing `MainActivity.kt` deprecation warning appeared.
- `node scripts/mb-lint.mjs` — exit `0`, `mb-lint passed (78 files)`.

## Static/boundary/redaction probes

- Manifest scan observed exactly `ACCESS_NETWORK_STATE` and `INTERNET`.
- Source scan observed the accepted Yandex endpoint, `lat/lon`, `hours=true`, header-only credential path, separate production/fixture composition and `Executors.newSingleThreadExecutor` dispatch.
- Fixture/test isolation scan found no URL/connection transport primitive in the fixture adapter or unit-test source; the fixture test uses a throwing synthetic transport to prove zero calls.
- Dependency scan found no diff in `app/build.gradle.kts`, `build.gradle.kts` or `gradle.properties`; targeted `git diff --check` was clean.
- Review-only synthetic token was never written and was absent from source, tests, APK and W15 protocol/evidence paths; no literal user-credential constructor was found in those surfaces.
- Ownership/public-contract scan found the existing `WeatherProvider`, `WeatherProviderRequest`, `WeatherProviderResult` and `WeatherReadPort` boundary symbols intact, no foreign feature business markers in the W15 production surface, and Weather Context still owns refresh/cache normalization.

## Claim mapping and limits

- AC-002 / REQ-005 / REQ-022: fake transport and redacted fixture cover request/DTO current-daily-hourly mapping and selected-city timezone.
- AC-004 / REQ-007 / REQ-025: provider failure sequence plus fresh attempt-2 cache-preservation regressions cover status/timeout/I/O/malformed and incomplete payload behavior.
- AC-006 / REQ-026: optional-field fallback and required completeness checks pass without fabricated values or crash.
- AC-007 / REQ-024: synthetic-only header observation and redacted scans pass.
- Target Android 11 custom-ROM/network readiness and live-provider compatibility were not probed; they remain `DEFERRED`, and no runtime `PASS` is claimed.

