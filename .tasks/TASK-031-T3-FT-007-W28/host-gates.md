---
description: Host/build/schema/diff gate evidence for TASK-031-T3-FT-007-W28.
status: supporting
---
# W28 host gates — attempt 1

All commands ran from `/home/serg/Projects/Mobile_APPS/hozayushka` with
`--offline` where Gradle was used. No runtime target, network or audio runtime
was used.

| Gate | Command | Result | Evidence |
|---|---|---|---|
| Focused projection/regression | `./gradlew --offline --no-daemon :app:testDebugUnitTest --tests com.hozayushka.app.DisplayProjectionTest` | PASS; `22` tests, `0` failures/errors | `app/build/test-results/testDebugUnitTest/TEST-com.hozayushka.app.DisplayProjectionTest.xml` |
| Named W28 probes | `./gradlew --offline --no-daemon :app:testDebugUnitTest --tests com.hozayushka.app.DisplayProjectionTest.w28GreenOverdueSurfaceIsContentFreeAdaptiveAndHierarchyFirst --tests com.hozayushka.app.DisplayProjectionTest.w28GreenKeepsElapsedNumericStableAndPlusBlinkingSeparate --tests com.hozayushka.app.DisplayProjectionTest.w28ReadOnlyOverdueAnyTapStillDismissesThroughTimerContract --info` | PASS; W28 geometry stdout: `idle=188.75`, `active=228.0`, `elapsed=256.0`, `plus=280.0`, `#FF4FA3` | Gradle executor stdout; same XML directory |
| Full host regression | `./gradlew --offline --no-daemon testDebugUnitTest` | PASS; `110` tests, `0` failures/errors | `app/build/test-results/testDebugUnitTest/*.xml` |
| Clean Android debug build | `./gradlew --offline --no-daemon clean assembleDebug` | PASS; `BUILD SUCCESSFUL` | Gradle output; build artifact under `app/build/outputs/apk/debug/` |
| Memory Bank/schema lint | `node scripts/mb-lint.mjs` | PASS; `mb-lint passed (78 files)` | command output |
| Diff integrity | `git diff --check` | PASS; no output | command output |

The Android toolchain emitted its existing SDK XML version warning, and the
clean build emitted one pre-existing MainActivity deprecation warning; neither
caused a failure. Because the worktree is broadly dirty, no execute receipt is
offered for reuse: relevant source, generated and environment inputs are not
conservatively bounded.
