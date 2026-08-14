---
description: Fresh verifier rerun of TASK-034-T3-FT-001-W31 host gates.
status: final
task_id: TASK-034-T3-FT-001-W31
---
# W31 host gates — independent rerun

All commands ran from `/home/serg/Projects/Mobile_APPS/hozayushka` after
`./gradlew clean assembleDebug`. No emulator or device was used by these host
commands.

| Gate | Result | Evidence |
|---|---|---|
| `./gradlew clean assembleDebug` | exit 0 | APK SHA-256 `60121bf8e5d4edc2da807efe7af968550c36dc06b63431f7fd05b76a95520064` |
| `./gradlew :app:testDebugUnitTest --tests com.hozayushka.app.DisplayProjectionTest` | exit 0 | 26 tests, 0 failures, 0 errors; focused XML |
| `./gradlew testDebugUnitTest` | exit 0 | `BUILD SUCCESSFUL` |
| `./gradlew lintDebug` | exit 0 | `app/build/reports/lint-results-debug.html` |
| `git diff --check` | exit 0 | no output |

Fresh geometry and fixture receipts are in the focused test XML at
`app/build/test-results/testDebugUnitTest/TEST-com.hozayushka.app.DisplayProjectionTest.xml`.
Host output is supporting evidence only and is not used as a substitute for
the physical visual observation.
