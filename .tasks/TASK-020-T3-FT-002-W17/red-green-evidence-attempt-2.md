---
description: Claim-linked retry RED/GREEN evidence for TASK-020-T3-FT-002-W17 Attempt 2.
status: active
task_id: TASK-020-T3-FT-002-W17
attempt: 2
---
# Claim-linked retry evidence — Attempt 2

## Correction basis

- Failed gate: independent Attempt-1 `/verify` response-identity probe.
- Claims: `FT-002-AC-004`, `FT-002-AC-005`, `FT-002-AC-008` / `REQ-007`,
  `REQ-008`, `REQ-029`.
- Correction: bind response acceptance to the immutable provider/location
  identity captured before selected-provider `fetch`; reject a response when
  the current selected identity changed, before projection/cache/history write.
- Isolation: deterministic host unit test, fake provider, mutable in-memory
  Settings, disposable in-memory cache, fixed time and locations; no network,
  credential, device, emulator or external side effect.

## RED

- Production state: unchanged from failed Attempt 1.
- Probe: `WeatherProviderDispatchTest.locationChangeDuringFetchRejectsResponseBeforeProjectionOrHistoryAcceptance`.
- Command: `./gradlew testDebugUnitTest --tests 'com.hozayushka.app.WeatherProviderDispatchTest.locationChangeDuringFetchRejectsResponseBeforeProjectionOrHistoryAcceptance' --rerun-tasks --no-daemon`
- Result: exit `1`; `1` test completed, `1` failed.
- Decisive observation: JUnit XML reported
  `staleProjectionAccepted=true; stalePressureStored=true`.
- Artifact at RED time:
  `app/build/test-results/testDebugUnitTest/TEST-com.hozayushka.app.WeatherProviderDispatchTest.xml`.

## GREEN

- Production correction: `WeatherCapability` captures provider, immutable
  `LocationContext` and canonical location identity before `fetch`; response
  acceptance compares the current selected provider/location to that request
  identity and rejects a changed-selection response before normalization,
  projection rebuild, cache save or history append.
- Focused command: `./gradlew testDebugUnitTest --tests 'com.hozayushka.app.WeatherProviderDispatchTest.locationChangeDuringFetchRejectsResponseBeforeProjectionOrHistoryAcceptance' --rerun-tasks --no-daemon`
- Focused result: exit `0`; `1/1` passed.
- Original verifier reproducer: compiled and ran
  `.tasks/TASK-020-T3-FT-002-W17/VerifierResponseIdentityProbe.java`; exit `0`
  with `selected_projection_fresh=false` and
  `old_pressure_labeled_as_new_history=false`.
- Claim-equivalent eight-class provider/context/settings regression: exit `0`;
  `56/56` passed, zero failures/errors/skips.
- Decisive comparison: both RED observations changed from `true` to rejected;
  the new selected city receives `NO_DATA`, the stale refresh returns no result,
  and disposable cache/history remains empty.
- Probe-strength change: none after RED. The same test method and assertions
  produced GREEN after the production correction.

## Required and supplemental gates

- Clean debug build: `./gradlew clean assembleDebug --no-daemon` -> exit `0`,
  `34/34` actionable tasks.
- Full host suite: `./gradlew testDebugUnitTest --rerun-tasks --no-daemon` ->
  exit `0`, `84/84` across `13` reports, zero failures/errors/skips.
- Memory Bank/diff: `node scripts/mb-lint.mjs` -> `78` files; `git diff --check`
  -> exit `0`, no output.
- Security/APK script: exit `0`; zero marker/credential/Yandex findings and
  exactly Open-Meteo plus OpenWeather production implementations.
- Static/APK endpoint inventory: one Open-Meteo and one OpenWeather endpoint
  occurrence/entry; no third production provider.
- Debug APK SHA-256:
  `19ddca31aabddc69fa889537ab09d24699e573aad55a6516d4d7532837b2d697`.
- `WeatherProviderDispatchTest` XML SHA-256:
  `b03e8e737b85ee67dc8980794252ea3f90277c3bed6343fa52f986151d9124ca`.
- Device/live evidence remains `DEFERRED`; no runtime or live-provider PASS is
  claimed.
