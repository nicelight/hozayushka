# TASK-019 claim-linked RED / GREEN evidence

## Attempt 1 — supporting-only execution evidence

- Evidence status: `supporting-only` for Attempt 3.
- The original honest pre-production RED remains the historical claim basis; it is not replayed or rewritten as a retry RED.

### RED — pre-production

- Attempt: `1`
- Production behavior basis: unchanged at revision `4ab1e1fd538f92ab3e705193a4b236777b6616bf`; only the task-owned test probe and workflow protocol/status had changed.
- Command: `./gradlew testDebugUnitTest --tests "com.hozayushka.app.SettingsLocationTest"`
- CWD: project root
- Exit code: `1`
- Result: `9 tests completed, 3 failed`; Kotlin/test compilation and test execution succeeded, so this was not a setup, syntax or import failure.
- Ephemeral generated report before later clean: `app/build/test-results/testDebugUnitTest/TEST-com.hozayushka.app.SettingsLocationTest.xml`, SHA-256 `9c4cfae77a957b8c55ad16d8403a0e8a75f9ca4c70187b910866b5e74fc59487`.

#### Claim observations

- `FT-008-AC-001` / `REQ-024`: first-run Settings accepted an unconditional runtime-generated key and retained it; expected Open-Meteo no-key context was absent.
- `FT-008-AC-007` / `REQ-027`: no first-run persisted provider selection/default prevented the key acceptance; explicit OpenWeather selection could not be established.
- `FT-008-AC-006` / `REQ-017, REQ-018, REQ-024, REQ-027`: an exercised network failure exposed only `Нет подключения`, not the selected-provider context.
- `FT-008-AC-008` / `REQ-028`: Settings resources lacked `Open-Meteo`, `OpenWeather` and the required Open-Meteo credit URL.

#### T3 safety

- The key probe generated its synthetic value only in memory at runtime.
- No raw value was asserted, printed, persisted to a fixture/resource/source file, captured in the original RED evidence or sent to a provider.
- The disposable Settings store was reset in `finally`; no network, emulator or device was used.

### GREEN — supporting-only

- Attempt: `1`
- Claim-equivalent targeted command: `./gradlew testDebugUnitTest --tests "com.hozayushka.app.SettingsLocationTest"`
- Exit code: `0`
- Result: `10` Settings/location tests, `0` skipped, `0` failures, `0` errors.
- Probe change from RED: the two initial behavioral/resource probes were converted to typed provider state/reopen/context/order assertions and a stronger runtime-only marker scan was added. This preserves the same claims while proving persisted selection, active-key gating, stable error context, ordering and durable absence directly.

#### Claim results

- `FT-008-AC-001` / `REQ-024`: Open-Meteo exposes no active key; only explicit OpenWeather accepts/retains the owner-local value; reopen preserves it; switching back to Open-Meteo keeps it inactive. The runtime-generated synthetic value is never recorded in durable evidence.
- `FT-008-AC-006` / `REQ-017, REQ-018, REQ-024, REQ-027`: missing/invalid key is OpenWeather-only; network and unknown-city text is prefixed by selected provider; provider/location/key state is unchanged and no fallback text appears.
- `FT-008-AC-007` / `REQ-027`: first run is Open-Meteo/no-key; explicit OpenWeather selection auto-saves, requests the existing refresh callback and reopens; failed key updates preserve the valid value/selection.
- `FT-008-AC-008` / `REQ-028`: deterministic Settings projection contains Open-Meteo and GeoNames attribution before Back, retains location/personalization/timer sections and exposes the OpenWeather key section only in that provider context.

#### Safety and artifact observations

- Runtime marker scan passed across `app/src`, task protocol/evidence, generated test reports and the existing assembled APK; only boolean/path observations were emitted.
- Packaged resources: SDK `aapt2 dump resources` found `settings_open_meteo_attribution` with the Open-Meteo URL and visible CC BY 4.0 licence URL, plus OpenWeather contextual labels.
- Source/evidence scan found no former synthetic-key fixture or legacy provider-key label in task source/resources/evidence; no captured task `.log` exists.
- Cleanup: every credential-bearing test resets its in-memory owner store in `finally`; no live key, request, network, emulator or device is used.

### Attempt 1 required gates

- `./gradlew clean assembleDebug` → exit `0`, `BUILD SUCCESSFUL`; APK `app/build/outputs/apk/debug/app-debug.apk`.
- `./gradlew testDebugUnitTest` → exit `0`, `69` tests, `0` skipped/failures/errors.
- `node scripts/mb-lint.mjs && git diff --check` → exit `0`; `mb-lint passed (78 files)` and no whitespace error.
- `/home/serg/Android/Sdk/build-tools/34.0.0/aapt2 dump resources app/build/outputs/apk/debug/app-debug.apk | rg ...` → exit `0`; accepted packaged attribution/provider strings present.

Final Attempt 1 artifact checksums remain recorded in the executor report.

## Attempt 2 — evidence-security correction

- Evidence status: `supporting-only` for Attempt 3.
- Attempt: `2`
- Correction basis: fresh independent `/verify` found one raw runtime-generated synthetic-key marker in this task-owned evidence file, contradicting `FT-008-AC-001 / REQ-024` evidence redaction.
- Correction: removed the raw value and reconciled the affected claim wording without changing the original honest RED, the claim probe, production behavior, tests, resources, task identity, tier, dependencies or lifecycle.

### Fresh claim-equivalent GREEN

- Command: `./gradlew testDebugUnitTest --tests "com.hozayushka.app.SettingsLocationTest" --rerun-tasks`
- Result: exit `0`; targeted XML reports `10` tests, `0` failures, `0` errors, `0` skipped.
- Corrected claim: `FT-008-AC-001 / REQ-024` now has both passing behavior and redacted durable evidence. The unchanged probe regenerates its synthetic value only in memory and scans the corrected task evidence surface without emitting that value.
- Other owned claims remain GREEN: `FT-008-AC-006`, `FT-008-AC-007`, `FT-008-AC-008` all execute in the same targeted class.
- Probe change from Attempt 1: none. Attempt 2 changes only durable evidence text and adds a reproducible task-owned security/static scan wrapper.

### Fresh task gates

- `./gradlew clean assembleDebug` → exit `0`; `BUILD SUCCESSFUL`; `34` actionable tasks executed.
- `./gradlew testDebugUnitTest --rerun-tasks` → exit `0`; XML aggregate `69` tests, `0` failures, `0` errors, `0` skipped; the corrected Settings class remains `10/10`.
- `bash .tasks/TASK-019-T3-FT-008-W16/evidence-security-scan.sh` → exit `0`; exact runtime marker has `0` workspace hits and `0` decompressed APK-entry hits, credential-literal scan has `0` candidate groups, and required packaged attribution/key/back resources are present.
- `node scripts/mb-lint.mjs && git diff --check` → exit `0`; integrity remains clean after the Attempt 2 evidence/handoff writes.

### Fresh artifacts and safety

- Debug APK SHA-256: `b2399d0c27d43949fe7bf58909de89cb958eef7b75c313b92c838707c0d91eeb`.
- Final full-suite `SettingsLocationTest` XML SHA-256: `5741682ce9ec039b1ab8c5d6ceebc49dfa4e7988a0e2f6c50b520ea4b40b1c9a`.
- No live credential, provider request, network call, emulator/AVD/QEMU, `adb` or physical-device action was used.
- Forbidden provider transport/dispatch/cache/history/hourly/long-term scope was untouched.

## Attempt 3 — final bounded provider-isolation correction

- Attempt: `3`
- Retry basis: fresh adversarial verification proved that the selected OpenWeather owner key could pass through the provider-unidentified generic callback to the still-wired legacy provider, while an untagged legacy error could be prefixed from Settings selection alone.
- Correction boundary: only `SettingsCapability.kt` and `SettingsLocationTest.kt`; `strings.xml`, Weather Context, composition, adapters, outbound requests, cache/history, forecast and dependencies remained read-only.

### Focused correction RED

- Production basis: Attempt 2 production behavior was unchanged; only the focused test expectations and Attempt 3 protocol had changed.
- Command: `./gradlew testDebugUnitTest --tests "com.hozayushka.app.SettingsLocationTest" --rerun-tasks`
- Result: exit `1`; `10 tests completed, 8 failed`; compilation and test execution succeeded.
- `FT-008-AC-001 / REQ-024`: the generic callback was invoked after OpenWeather selection/key save, exposing the owner key beyond local Settings state.
- `FT-008-AC-006 / REQ-027`: untagged network/unknown-city/key errors were attributed as OpenWeather or Open-Meteo solely from current selection.
- `FT-008-AC-007 / REQ-027`: supported `LAUNCH` and `LOCATION_CHANGE` refresh triggers reached the injected legacy provider after selection/key save.
- RED source: the current adversarial `semantic-fail` in `.protocols/TASK-019-T3-FT-008-W16/red-verification.md`; this focused executable RED is claim-equivalent correction evidence, not a replacement for the original Attempt 1 RED.

### Focused correction GREEN

- Production correction: the provider-unidentified generic key callback now denies access; stored OpenWeather key state, selection, persistence and local validation remain unchanged. Untagged transport errors remain provider-neutral, while local Settings key validation continues to render the accepted OpenWeather missing/invalid messages.
- Command: `./gradlew testDebugUnitTest --tests "com.hozayushka.app.SettingsLocationTest" --rerun-tasks`
- Result: exit `0`; targeted XML `10` tests, `0` failures, `0` errors, `0` skipped.
- Observable result: key callback count `0`; provider invocation count `0` for both supported refresh triggers; key remains stored/reopenable and applicable in Settings; untagged errors contain neither provider label; provider/location/key state remains unchanged.
- Probe change: existing Settings persistence/failure tests were strengthened with callback/provider counters and explicit launch/location refresh checks. No network, device, live key or provider transport was used.

### Attempt 3 host and safety gates

- `./gradlew clean assembleDebug` → exit `0`; clean debug build, `34` actionable tasks executed.
- `./gradlew testDebugUnitTest --rerun-tasks` → exit `0`; XML aggregate `69` tests, `0` failures, `0` errors, `0` skipped; Settings class `10/10`.
- `bash .tasks/TASK-019-T3-FT-008-W16/evidence-security-scan.sh` → exit `0`; `0` known-marker workspace hits, `0` decompressed-APK entry hits, `0` credential-literal candidate groups; packaged-resource static scan PASS.
- `node scripts/mb-lint.mjs && git diff --check` → exit `0`; `mb-lint passed (78 files)` and no whitespace errors.
- No emulator/AVD/QEMU, Android Studio virtual device, `adb`, physical phone, network, live provider or real credential was used. Device/live-provider evidence remains deferred without runtime PASS.
