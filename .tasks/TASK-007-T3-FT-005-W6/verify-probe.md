---
description: Verifier-owned isolated probes for TASK-007-T3-FT-005-W6.
status: final
---
# Verifier-owned targeted probes — TASK-007-T3-FT-005-W6

## Isolation and cleanup

- Two temporary JUnit probe classes were added under `app/src/test` only for
  these checks and deleted immediately after each run; no production source,
  task card, lifecycle or planning artifact was changed.
- Probe stores were synthetic and in-memory, or a proxy-backed
  `SharedPreferences` map. No Android device, network, credential or external
  state was used.
- Temporary sources are absent after cleanup; the observed results below are
  retained as the verifier-owned evidence receipt.

## Probe 1 — capability, active identity and presentation

Command:

```text
./gradlew testDebugUnitTest --tests com.hozayushka.app.VerifierOwnedTimerPresetProbeTest --rerun-tasks
```

Exit `0` / `BUILD SUCCESSFUL`.

Observed assertions:

- valid `2:04:06` persisted; invalid `2:60:06` returned
  `MINUTES_OUT_OF_RANGE`, did not call the backing store's `save`, and returned
  the previous valid duration;
- reload from a new `SettingsCapability` retained slot 1 `2:04:06`, slot 3
  `0:0:7`, and slot 2 default `0:10:0` independently;
- Timer projection exposed defaults, exact orange/pink/purple tokens, one
  active slot after `startPreset`, and the same active identity after Settings
  slot 3 was edited; the timer store still held one record;
- labels for `1:59:59`, `0:59:59`, and `0:0:59` were `1 ч`, `59 м`, and `59 с`.

## Probe 2 — owner-local SharedPreferences persistence

Command:

```text
./gradlew testDebugUnitTest --tests com.hozayushka.app.VerifierOwnedSharedPreferencesPresetProbeTest --rerun-tasks
```

Exit `0` / `BUILD SUCCESSFUL`.

Observed assertions:

- `SharedPreferencesSettingsStateStore` reloaded valid slot 2 `4:05:06`;
- invalid `4:60:06` left the proxy-backed store map byte-for-byte unchanged;
- slots 1 and 3 reloaded as their accepted defaults.

## Independent gates after probe cleanup

- `./gradlew clean testDebugUnitTest`: exit `0`; XML totals `31` tests,
  `0` failures, `0` errors.
- `./gradlew assembleDebug`: exit `0`; APK SHA-256
  `1ab29ce24ff60a593b059e85654897a6907663563eca5fcbc85a86a72c80b9b6`.
- `node scripts/mb-lint.mjs`: exit `0`; 77 files.
- `git diff --check`: exit `0`.
- Corrected source/test-only boundary and redaction scan: exit `0`.
- `adb devices`: no target listed; runtime/device evidence remains
  `DEFERRED`, non-blocking, with no runtime `PASS` claim.
