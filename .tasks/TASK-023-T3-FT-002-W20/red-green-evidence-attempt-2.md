# Claim-linked execution evidence — TASK-023-T3-FT-002-W20 Attempt 2

## Applicability and retry basis

- attempt: `2`
- applicable claims: `FT-002-AC-004 / REQ-007, REQ-025` and
  `FT-002-AC-008 / REQ-007, REQ-029`
- `FT-002-AC-007 / REQ-024`: accepted synthetic/redacted alternative proof;
  real-key observation remains forbidden.
- retry basis: fresh Debug diagnosis in
  `.protocols/AUTONOMOUS-RUN/status.md`; Settings UI called
  `updateOpenWeatherApiKey` from every non-empty `onTextChanged` prefix.
- Attempt-1 receipts remain supporting-only; no historical evidence was
  backfilled or replaced.

## Attempt 2 RED

- Setup-only first probe: the initial source-path variant exited `1` with
  `FileNotFoundException` because the Gradle host test working directory is
  `app/`; it is not claim evidence.
- Corrected command:
  `./gradlew testDebugUnitTest --tests 'com.hozayushka.app.SettingsLocationTest.characterByCharacterOpenWeatherInputOnlyValidatesUntilCommitBoundary' --rerun-tasks --no-daemon`
- Pre-correction basis: the Attempt-1 Settings UI implementation was restored
  only for this disposable pre-change probe, then the Attempt-2 correction was
  reapplied. No external side effect, network, provider, device or credential
  was used.
- Result: exit `1`; one expected `AssertionError` at the watcher-boundary
  assertion. The focused probe found that the old watcher did not have local
  rendering plus a separate IME/focus/leave commit boundary.
- Observable defect: the old `onTextChanged` body directly called
  `updateOpenWeatherApiKey`, so a non-empty typed prefix could persist and
  trigger the existing callback before the complete value was committed.

## Attempt 2 correction

- `SettingsCapability.kt`: extracted the existing validation expression into a
  Settings-local helper; `onTextChanged` now only validates/renders. Existing
  persistence/callback remains in `updateOpenWeatherApiKey` and is reached by
  the focus-loss commit boundary. IME-DONE and the existing leave-Settings
  button use `clearFocus()` to enter that boundary once without a new stateful
  deduplication mechanism.
- No visible Save button, debounce, deduplication state, new regex/length
  contract, event/message boundary, provider dispatch, adapter transport or
  secret transport was added.

## Attempt 2 GREEN

- Focused command (fresh after final `clearFocus()` correction): same command
  as RED above.
- Result: exit `0`; character-by-character watcher/static contract passed and
  synthetic complete-key commit produced exactly one save callback, with no
  persisted value observed during the per-character loop.
- Regression group:
  `./gradlew testDebugUnitTest --tests 'com.hozayushka.app.SettingsLocationTest' --tests 'com.hozayushka.app.WeatherProviderDispatchTest' --tests 'com.hozayushka.app.WeatherContextTest.selectedWeatherActivationLeavesClockAndTimerControlTraceUnchanged' --rerun-tasks --no-daemon`
- Result: exit `0`; selected OpenWeather one-call behavior, Open-Meteo zero
  calls, invalid/blank/Open-Meteo inert paths, selected failure isolation and
  timer-independence controls passed.

## Safety and provenance

- Synthetic/resettable host fixtures only; no real credential, live provider,
  live network, emulator/AVD/QEMU, Android Studio virtual device, `adb`,
  physical device or runtime/device claim.
- Computed synthetic marker was absent from source/resources/reports/evidence
  and the debug APK. Evidence records redacted/presence-only request checks.
