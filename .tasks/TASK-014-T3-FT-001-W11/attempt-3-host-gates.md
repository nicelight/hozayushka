# Attempt 3 — host and mandatory gate evidence

Run from the repository root on `2026-08-08`, after the final retry correction.

| Gate | Result |
|---|---|
| `./gradlew testDebugUnitTest --tests com.hozayushka.app.DisplayProjectionTest.cityDoubleTapCannotLeaveDelayedSettingsAfterLongPressTimeout` | exit `0`, `BUILD SUCCESSFUL`; focused delayed-navigation regression passed |
| `./gradlew clean assembleDebug` | exit `0`, `BUILD SUCCESSFUL`; 34 tasks executed; APK SHA-256 `5cfb17a4c3d192b44583dce678b342588361bac35fb3bfd5ddf97e84820a7b80` |
| `./gradlew testDebugUnitTest` | exit `0`, `BUILD SUCCESSFUL`; 54/54 tests, 0 failures/errors/skips |
| `git diff --check` | exit `0`, no output |
| `/home/serg/Android/Sdk/platform-tools/adb -s emulator-5554 shell dumpsys activity top` | exit `0`; repeated against fresh current-APK Main Display, Settings hold route, cancellation states and final awake idle Main Display |
| current APK install/hash | `adb install -r` returned `Success`; installed/local SHA-256 matched exactly |

The clean build emitted only the pre-existing `MainActivity.onBackPressed` deprecation warning. It did not fail the gate and is outside TASK-014.

No reuse candidate is offered: the repository has broad shared dirty/untracked inputs and the emulator checks depend on external runtime state. All attempt-3 executor evidence is supporting-only for fresh independent `/verify`.
