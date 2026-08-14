# Host gates — attempt 1

All commands were run offline on the host. No emulator/AVD/QEMU, adb/device,
network/provider, credentials or real audio runtime was used.

| Gate | Command | Result | Evidence |
|---|---|---|---|
| Fresh RED baseline | `./gradlew --offline --no-daemon :app:testDebugUnitTest --tests com.hozayushka.app.DisplayProjectionTest.reachableMainDisplayRefreshKeepsIdleClock176AndCountdown32` plus read-only source probe | PASS as baseline execution; source probe is honest W27 RED | `red-baseline.md` |
| Focused display GREEN | `./gradlew --offline --no-daemon :app:testDebugUnitTest --tests com.hozayushka.app.DisplayProjectionTest --rerun-tasks` | 19/19, 0 failures/errors | `app/build/test-results/testDebugUnitTest/TEST-com.hozayushka.app.DisplayProjectionTest.xml` |
| Named W27 geometry | `./gradlew --offline --no-daemon :app:testDebugUnitTest --tests com.hozayushka.app.DisplayProjectionTest.w27GreenCountdownSurfaceIsDedicatedLargerAndPresetIdentified --info` | PASS; deterministic geometry output in executor log | `geometry.json`, `red-green-contact-sheet.svg` |
| Timer/lifecycle/audio regression | `./gradlew --offline --no-daemon testDebugUnitTest --tests com.hozayushka.app.TimerLifecycleTest` | 5/5, 0 failures/errors | `app/build/test-results/testDebugUnitTest/TEST-com.hozayushka.app.TimerLifecycleTest.xml` |
| W23/temporary host regression | `./gradlew --offline --no-daemon testDebugUnitTest --tests com.hozayushka.app.FoundationProbesTest --tests com.hozayushka.app.OverdueAlertTest` | Foundation 3/3 and Overdue 7/7, 0 failures/errors | corresponding XML files under `app/build/test-results/testDebugUnitTest/` |
| Full host regression | `./gradlew --offline --no-daemon testDebugUnitTest` | 107/107, 0 failures/errors/skips | `app/build/reports/tests/testDebugUnitTest/index.html` |
| Clean build | `./gradlew --offline --no-daemon clean assembleDebug` | `BUILD SUCCESSFUL` | Gradle build output |
| Memory Bank/diff integrity | `node scripts/mb-lint.mjs && git diff --check -- <two target files>` | mb-lint passed (78 files); diff check passed | command output |
| Protocol readiness | `node scripts/mb-doctor.mjs --strict --json` | `status: pass`, 0 errors/warnings | command output |
| Evidence syntax | `jq empty geometry.json`; `xmllint --noout red-green-contact-sheet.svg` | PASS | task-local artifacts |

The Android deprecation warning for `MainActivity.onBackPressed` is
pre-existing and unrelated to W27; no warning was introduced in the touched
production/test delta.
