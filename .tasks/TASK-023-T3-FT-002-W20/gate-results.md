# Gate results — TASK-023-T3-FT-002-W20 Attempt 1

## Required gates

| Gate | Exact command | Result |
|---|---|---|
| Clean Android debug build | `./gradlew clean assembleDebug` | exit `0`; `34 actionable tasks`; APK `app/build/outputs/apk/debug/app-debug.apk`; SHA-256 `98c51cfec1bf9ef0a12b2686ed2df9dc9a1a51cd06d8760cab91c63031a71518` |
| Host/unit suite | `./gradlew testDebugUnitTest --rerun-tasks` | exit `0`; XML aggregate `90` tests, `0` failures, `0` errors, `0` skipped |
| Memory Bank/diff integrity | `node scripts/mb-lint.mjs && git diff --check` | exit `0`; `mb-lint passed (78 files)` |

## Task-scoped probes

- RED focused activation test: exit `1`, one expected behavioral failure before
  production correction; see `red-green-evidence.md`.
- Focused Settings/provider GREEN: exit `0`.
- Clock/timer control-treatment GREEN: exit `0`; see
  `weather-refresh-timer-independence.json`.

## Static, redaction and boundary checks

- Final scan over task source/tests, task protocol/evidence, resources and debug
  APK: exit `0`; no prohibited synthetic marker, credential-shaped literal or
  unredacted `appid` group.
- Settings ownership scan: exit `0`; no adapter/request/cache/weather-context
  access from `SettingsCapability.kt`.
- Task-owned production/test edits are limited to the exact hard-boundary paths;
  `WeatherCapability.kt` was not edited by this attempt. Pre-existing dirty W16/
  W17 paths were preserved and are not W20 changes.
- No live network/provider call, real credential, emulator/AVD/QEMU, Android
  Studio virtual device, `adb` or physical device was used.
