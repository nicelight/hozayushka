# Claim-linked execution evidence — TASK-023-T3-FT-002-W20

## Attempt 1 RED — pre-repair

- Claims: `FT-002-AC-004 / REQ-007, REQ-025` and
  `FT-002-AC-008 / REQ-007, REQ-029`.
- Command: `./gradlew testDebugUnitTest --tests 'com.hozayushka.app.SettingsLocationTest.validOpenWeatherKeySaveRequestsSelectedRefreshAfterMissingKeyState' --rerun-tasks`
- Result: exit `1`; one test completed and failed at the expected post-save
  activation assertion.
- Observation: after explicit OpenWeather selection without a key, the initial
  selected-provider refresh recorded `OpenWeather: API-ключ не указан` with
  zero provider calls; valid key persistence did not issue the expected second
  refresh. No credential value was printed or stored by the probe.
- Production basis: the RED ran before the Settings key-save callback and
  composition-root wiring were added. The only pre-RED change was this
  claim-specific executable test.

## Attempt 1 GREEN — bounded repair

- Claims: same AC/REQ locators; `FT-002-AC-007 / REQ-024` uses the accepted
  synthetic/redacted alternative proof because a real owner key is forbidden.
- Production correction: `SettingsCapability` invokes a dedicated callback
  only after a valid persisted OpenWeather key save; `FoundationRuntime` wires
  that callback to the existing asynchronous Weather Context refresh command.
  The callback carries no key and uses `PROVIDER_CHANGE` as the existing
  non-scheduled selected-access refresh trigger.
- Command: `./gradlew testDebugUnitTest --tests 'com.hozayushka.app.SettingsLocationTest.validOpenWeatherKeySaveRequestsSelectedRefreshAfterMissingKeyState' --rerun-tasks`
- Result: exit `0`; focused test passed.
- Observable result: one selected OpenWeather refresh after valid save, zero
  Open-Meteo calls, matching provider/location state, fresh successful data and
  no obsolete missing-key error.
- Additional claim probes:
  - `./gradlew testDebugUnitTest --tests 'com.hozayushka.app.SettingsLocationTest' --tests 'com.hozayushka.app.WeatherProviderDispatchTest.validKeySaveRefreshesSelectedOpenWeatherAndKeepsRepeatedFailureIsolated' --rerun-tasks` → exit `0`.
  - `./gradlew testDebugUnitTest --tests 'com.hozayushka.app.WeatherContextTest.selectedWeatherActivationLeavesClockAndTimerControlTraceUnchanged' --rerun-tasks` → exit `0`.
- Probe strengthening: the RED used the existing provider-change callback to
  expose the missing-key state before the new hook existed. The GREEN uses the
  dedicated key-save callback and adds direct invalid/blank/Open-Meteo inert,
  repeated-save failure-isolation and timer/control treatment assertions; the
  accepted claim meaning is unchanged.

## Safety and cleanup

- All fixtures use resettable in-memory Settings/Weather/Timer stores.
- Credential observation is presence/redacted only; generated synthetic values
  are not emitted into output, fixtures or durable evidence.
- No network, provider endpoint, emulator, AVD, QEMU, Android Studio virtual
  device, `adb`, physical device or real credential was used.
