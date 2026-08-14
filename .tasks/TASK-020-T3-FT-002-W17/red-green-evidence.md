---
description: Claim-linked RED/GREEN evidence for TASK-020-T3-FT-002-W17 Attempt 1.
status: active
task_id: TASK-020-T3-FT-002-W17
attempt: 1
---
# Claim-linked evidence — Attempt 1

## Honest pre-production RED

- Production source state: unchanged W16 baseline; Yandex is the only production weather adapter, cache/history are provider-less, Weather Context has one production provider field, and the Settings key callback remains blanket-denied.
- Probe: `app/src/test/kotlin/com/hozayushka/app/ProviderMigrationClaimProbeTest.kt` uses only compilable current public/reflection/file-inventory surfaces. It introduces no missing target imports and no artificial production break.
- Command: `./gradlew testDebugUnitTest --tests com.hozayushka.app.ProviderMigrationClaimProbeTest`
- Working directory: project root.
- Result: exit `1`; `6 tests completed, 6 failed`.
- Claim observations:
  - `FT-002-AC-002`: Open-Meteo/OpenWeather target adapters and provider-identified envelope are absent; Yandex remains.
  - `FT-002-AC-004`: `WeatherCacheRecord` lacks provider plus location identity.
  - `FT-002-AC-005`: `PressureHistoryEntry` lacks provider plus location identity.
  - `FT-002-AC-006`: neither target decoder exists to prove two-provider optional/unknown mapping.
  - `FT-002-AC-007`: selected OpenWeather cannot obtain the stored synthetic key because W16's temporary deny is still active.
  - `FT-002-AC-008`: Weather Context owns one generic production provider rather than two explicit selected leaves.
- Gradle report: `app/build/reports/tests/testDebugUnitTest/index.html` (generated, local).
- Excluded setup run: the immediately preceding command stopped in test compilation because one generic lambda inferred `Any`; it was corrected in test-only code and is not counted as RED.
- Isolation: deterministic host JVM only; no network/live endpoint, real credential, subscription, emulator/AVD/QEMU, Android Studio virtual device, `adb` or physical device.

## GREEN

- Production change: removed Yandex implementation/wiring/fixture; installed only Open-Meteo and OpenWeather production `WeatherProvider` implementations; separated the Foundation fake behind non-provider `WeatherFixture`.
- Claim-equivalent command: `./gradlew testDebugUnitTest --tests com.hozayushka.app.ProviderMigrationClaimProbeTest --tests com.hozayushka.app.OpenMeteoWeatherAdapterTest --tests com.hozayushka.app.OpenWeatherWeatherAdapterTest --tests com.hozayushka.app.WeatherProviderDispatchTest --tests com.hozayushka.app.WeatherContextTest --tests com.hozayushka.app.SettingsLocationTest --tests com.hozayushka.app.FoundationProbesTest --tests com.hozayushka.app.ForecastSessionTest`
- Result: exit `0`, `55/55` tests, zero failures/errors/skips.
- `FT-002-AC-002`: both deterministic provider fixtures pass through their real decoder and the same Weather Context normalization into equivalent current/four-card fields, provider source and selected-city timezone; capability metadata remains provider-specific.
- `FT-002-AC-004`: launch, 30-minute cadence, provider change and city change invoke only the resolved adapter; matching cache remains fresh through the existing window, while mismatched provider/location state is invisible.
- `FT-002-AC-005`: every history entry carries provider plus location identity; trend assertions prove provider and location switches start an isolated comparison partition while seven-day retention remains owner-local.
- `FT-002-AC-006`: WMO/OpenWeather unknown values normalize to neutral cloud; missing condition/moon/array values remain null/fallback inputs without crashes, text invention or positional shifts.
- `FT-002-AC-007`: Open-Meteo request shape is credential-free and rejects credential-bearing requests before transport; selected OpenWeather alone receives a runtime-only synthetic key in transient `appid`, while result/evidence stay redacted. Mismatched/unknown adapter identity is rejected before Settings key access.
- `FT-002-AC-008`: source inventory has exactly two production provider implementations, explicit selected-only dispatch, provider-attributed failures, no second-provider invocation/substitution and provider+location record identity.
- Probe change from RED: the original six target-state assertions were retained and turned GREEN; direct fake-transport fixture decode, dispatch/failure/identity matrices and optional-field cases were added. This strengthens the same claim mapping without changing acceptance meaning.
- Isolation: fake transports, runtime-generated synthetic values, fixed clocks/locations and disposable in-memory stores only. No network/live endpoint, owner credential, subscription, emulator/AVD/QEMU, Android Studio virtual device, `adb` or physical device.

## Required executor gates

- Clean debug build: `./gradlew clean assembleDebug --quiet` -> exit `0`.
- Full host suite: `./gradlew testDebugUnitTest --quiet` -> exit `0`; JUnit XML aggregate `83/83`, zero failures, errors or skips.
- Memory Bank integrity: `node scripts/mb-lint.mjs` -> exit `0`, `78 files`.
- Patch integrity: `git diff --check` -> exit `0`, no output.
- Security/APK evidence: `bash .tasks/TASK-020-T3-FT-002-W17/evidence-security-scan.sh` -> exit `0`:
  - known synthetic marker: `0` workspace matches and `0` decompressed APK entries;
  - credential literals/candidates: `0` workspace groups and `0` APK entries;
  - production provider inventory: exactly Open-Meteo plus OpenWeather;
  - Yandex/legacy provider: `0` production-source matches and `0` APK entries.
- Debug APK SHA-256: `df238a244bba050effdfbb9691e35b35821b664120b7125db2ed3d632a9d6bd1`.
- Evidence class: host build/unit/static/redacted only. Device/live-provider evidence remains `DEFERRED`; these gates do not assert Android runtime or live-provider PASS.
