# Attempt 1 host gate evidence

Input source basis for the final gates: repository revision
`a93e46118f0f0b90e311b6174e3f5a8ed7d89fef`; the worktree contained pre-existing
user changes, preserved by this execution. Synthetic/redacted fixture path only.

## Required build and unit gates

- command: `./gradlew clean assembleDebug`
- cwd: `/home/serg/Projects/Mobile_APPS/hozayushka`
- result: exit `0`, `BUILD SUCCESSFUL`; debug APK checksum
  `c609347b958ca4d012f2507eb19aa486459eaeef2a71902711b847e812737a5d`.

- command: `./gradlew testDebugUnitTest`
- cwd: `/home/serg/Projects/Mobile_APPS/hozayushka`
- result: exit `0`, `BUILD SUCCESSFUL`; 18 tests, 0 failures, 0 errors:
  `ForecastSessionTest` 3, `WeatherContextTest` 8,
  `FoundationProbesTest` 3, `DisplayProjectionTest` 4.
- report paths: `app/build/test-results/testDebugUnitTest/TEST-*.xml`.

## Static/boundary gate

- command: `rg` forbidden storage/provider symbols in
  `app/src/main/kotlin/com/hozayushka/app/forecast` and
  `app/src/main/kotlin/com/hozayushka/app/display` and
  `app/src/main/kotlin/com/hozayushka/app/app/MainActivity.kt`.
- result: exit `0`; MainActivity is navigation-only and no Forecast
  Sessions/Main Display direct storage or raw provider-adapter bypass detected.
- inspected wiring: Main Display → Forecast Sessions,
  Forecast Sessions → Weather Context, Weather Context → provider adapter;
  composition root only constructs/wires the capabilities.

## Secret gate

- command: source/test credential-like literal scan plus `strings` scan of
  `app/build/outputs/apk/debug/app-debug.apk` using the accepted redaction
  patterns.
- result: exit `0`; no credential-like source/fixture literal and no
  credential-like APK match. Tests use `WeatherProviderRequest.fromSyntheticProbe()`.

## Claim-equivalent GREEN

- AC-001/005: complete hourly data opens; missing/incomplete input returns
  closed state with the exact accepted message and no rows.
- AC-002/003: the redacted fixture produces the exact eight time labels in
  rows `[4, 4]`, with `00:00`/`03:00` on the following city-local day,
  temperature palette/background, illustration input and zero pressure arrows.
- AC-004: deterministic session probe covers 3-second close, single-tap hint
  cancellation, double-tap close and hold/release close.
- artifact: `app/build/test-results/testDebugUnitTest/TEST-com.hozayushka.app.ForecastSessionTest.xml`.

## Target evidence

- status: `DEFERRED` (non-blocking)
- unavailable condition: no authorized Android emulator/device target was
  available for this execution; `adb devices` returned only `List of devices attached`.
- residual risk: 1280×720 card readability/static pseudo-glass and actual
  Android gesture/timing behavior remain unobserved on the target custom ROM.
- no runtime PASS claim is made.
