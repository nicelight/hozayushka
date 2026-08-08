# Attempt 2 — host and mandatory gate evidence

Run from repository root on `2026-08-08`, after the retry correction.

| Gate | Result |
|---|---|
| `./gradlew testDebugUnitTest --tests com.hozayushka.app.DisplayProjectionTest.activeCountdownKeepsCityHoldAlongsideProtectedTimerTaps` | exit `0`, `BUILD SUCCESSFUL`; focused retry regression passed |
| `./gradlew clean assembleDebug` | exit `0`, `BUILD SUCCESSFUL`; 34 tasks executed |
| `./gradlew testDebugUnitTest` | exit `0`, `BUILD SUCCESSFUL`; 53/53 tests, 0 failures/errors/skips; `DisplayProjectionTest` 6/6 |
| `git diff --check` | exit `0`, no output |
| `/home/serg/Android/Sdk/platform-tools/adb -s emulator-5554 shell dumpsys activity top` | exit `0`; exact required supplementary gate run against the final awake idle Main Display |

The clean build emitted only the pre-existing `MainActivity.onBackPressed` deprecation warning. It did not fail the gate and is outside TASK-014.

No reuse candidate is offered: the repository has broad shared dirty/untracked inputs and the emulator checks depend on external runtime state. All attempt-2 executor results are supporting-only for fresh independent `/verify`.
