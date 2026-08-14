# Gate results — TASK-023-T3-FT-002-W20 Attempt 2

| Gate | Exact command | Result |
|---|---|---|
| Clean Android debug build | `./gradlew clean assembleDebug --no-daemon` | PASS; exit `0`; `34 actionable tasks` |
| Full host/unit suite | `./gradlew testDebugUnitTest --rerun-tasks --no-daemon` | PASS; exit `0`; `91/91` tests, `0` failures/errors/skips; 13 XML suites under `app/build/test-results/testDebugUnitTest/` |
| Memory Bank lint | `node scripts/mb-lint.mjs` | PASS; `78 files` |
| Diff integrity | `git diff --check` | PASS; no output |
| Focused Attempt-2 GREEN | `./gradlew testDebugUnitTest --tests 'com.hozayushka.app.SettingsLocationTest.characterByCharacterOpenWeatherInputOnlyValidatesUntilCommitBoundary' --rerun-tasks --no-daemon` | PASS; exit `0` |
| Settings/provider/timer regression group | `./gradlew testDebugUnitTest --tests 'com.hozayushka.app.SettingsLocationTest' --tests 'com.hozayushka.app.WeatherProviderDispatchTest' --tests 'com.hozayushka.app.WeatherContextTest.selectedWeatherActivationLeavesClockAndTimerControlTraceUnchanged' --rerun-tasks --no-daemon` | PASS; exit `0` |
| UI commit-boundary static scan | read-only `awk`/`rg` watcher and boundary scan | PASS; watcher has no `updateOpenWeatherApiKey`; IME/focus/`clearFocus` commit path present |
| Source/evidence redaction and owner scan | read-only `rg` scan over task source/tests/protocol/evidence | PASS; no credential-shaped literal; no Settings adapter/WeatherCapability bypass |
| Synthetic marker scan | computed synthetic marker scan over source/resources/reports/evidence/APK | PASS; marker absent |
| Debug APK credential scan | `unzip -p app/build/outputs/apk/debug/app-debug.apk classes.dex | strings | rg ...` | PASS; no credential-shaped APK string |

Debug APK SHA-256:
`3b1965b0b3e7cefbeeaf7b7cd9eb5228378751e6db494058165bfa25a9f22a22`

## Scope

- Attempt-2 production/test changes: exactly
  `app/src/main/kotlin/com/hozayushka/app/settings/SettingsCapability.kt`
  and `app/src/test/kotlin/com/hozayushka/app/SettingsLocationTest.kt`.
- `WeatherCapability.kt`, `FoundationRuntime.kt`, provider adapters and the
  other task-boundary tests were not edited by Attempt 2; their dirty state is
  pre-existing baseline from earlier waves.
- No forbidden scope, lifecycle/status/checkpoint, historical task evidence,
  `/verify`, `/red-verify` or `/mb-sync` was touched/run.
