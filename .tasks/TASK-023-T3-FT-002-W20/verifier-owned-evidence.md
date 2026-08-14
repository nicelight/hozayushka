---
description: Fresh verifier-owned host, boundary, timer and redaction evidence for TASK-023-T3-FT-002-W20.
status: final
task_id: TASK-023-T3-FT-002-W20
stage: S-VERIFY
---
# Verifier-owned evidence — TASK-023-T3-FT-002-W20

## Fresh execution

All observations below were produced in this verification cycle. No executor
receipt was reused as functional proof. The disposable probe is
`VerifierOwnedW20Probe.java`; its generated synthetic key is never printed.

| Check | Command/result |
|---|---|
| Focused W20 claims | `./gradlew testDebugUnitTest --tests 'com.hozayushka.app.SettingsLocationTest.characterByCharacterOpenWeatherInputOnlyValidatesUntilCommitBoundary' --tests 'com.hozayushka.app.SettingsLocationTest.validOpenWeatherKeySaveRequestsSelectedRefreshAfterMissingKeyState' --tests 'com.hozayushka.app.SettingsLocationTest.invalidBlankAndOpenMeteoKeySavesAreInertWhileRepeatedValidSavesNotify' --tests 'com.hozayushka.app.WeatherProviderDispatchTest.validKeySaveRefreshesSelectedOpenWeatherAndKeepsRepeatedFailureIsolated' --tests 'com.hozayushka.app.WeatherContextTest.selectedWeatherActivationLeavesClockAndTimerControlTraceUnchanged' --rerun-tasks --no-daemon` → exit `0` |
| Full host suite | `./gradlew testDebugUnitTest --rerun-tasks --no-daemon` → exit `0`; `91` tests, `13` XML suites, `0` skipped/failures/errors |
| Clean build | `./gradlew clean assembleDebug --no-daemon` → exit `0`; `34` actionable tasks |
| Fresh host after clean | `./gradlew testDebugUnitTest --rerun-tasks --no-daemon` → exit `0`; XML aggregate `91/91` |
| Verifier probe | Java compile/run against the current debug classes and Android API-34 stub → exit `0`; `probe=PASS` |
| Static/redaction/MB/diff | watcher/boundary scan, computed marker scan, APK scan, `node scripts/mb-lint.mjs`, `git diff --check` → all exit `0` |

Debug APK SHA-256 from this cycle:
`3b1965b0b3e7cefbeeaf7b7cd9eb5228378751e6db494058165bfa25a9f22a22`.

## Probe observations

The probe started with selected OpenWeather, valid location and no key. The
initial selected-provider refresh recorded missing-key state with zero calls.
For every generated character prefix, Settings state, save callback count and
both provider call counts stayed at zero. The committed complete value then
produced one callback, one OpenWeather call, zero Open-Meteo calls, fresh
matching projection and missing-key clearance.

The same resettable host fixture proved blank, invalid and Open-Meteo-inapplicable
updates inert. A later selected OpenWeather failure made no Open-Meteo call,
kept the selected-provider error, and preserved the pre-failure matching record,
provider and location identity. The request observation was presence-only with
`[REDACTED]`; the raw synthetic value was not recorded.

The control/treatment clock and timer traces are recorded in
[`verifier-owned-weather-refresh-timer-independence.json`](verifier-owned-weather-refresh-timer-independence.json).
Both traces are exactly:

`COUNTDOWN:0:60000, COUNTDOWN:1000:59000, COUNTDOWN:5000:55000, OVERDUE:60000:0, OVERDUE:60001:0`.

Double-tap countdown cancellation and overdue single-tap dismissal matched.

## Static and secret-safety observations

- `SettingsCapability.kt:665-671` watcher calls only local `renderKeyValidation`;
  it contains no save/callback/storage call.
- The existing commit path is present at `:659-664`; IME-DONE calls
  `clearFocus()` at `:696-702`, focus loss commits at `:704-706`, and the
  existing Settings leave button calls `clearFocus()` at `:949-955`.
- Settings has zero adapter/WeatherCapability imports. The Foundation callback
  has zero raw-key references and queues the existing Weather Context
  `PROVIDER_CHANGE` refresh. Weather Context retains selected adapter dispatch.
- Computed synthetic markers: `0` hits across current source, W20 protocol/
  evidence, task artifacts and the debug APK.
- Credential-shaped literal candidates: `0`; APK secret-pattern hits: `0`.
- Open-Meteo never receives a credential in the observed inert/dispatch paths.

## Provenance and boundaries

Attempt-2 executor handoff/evidence, the pre-existing W20 timer JSON labelled
`attempt: 1`, and the prior W20 red-verification report remain supporting or
historical context only. This fresh artifact is the independent functional
basis. No production source, task card, lifecycle/status, scheduler checkpoint,
executor evidence or red-verification evidence was modified. The only new task
artifacts are verifier-owned.

No Android Studio, emulator/AVD, QEMU, adb, physical device, live provider,
network call or real credential was used.
