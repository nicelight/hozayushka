---
description: Host build and regression evidence for TASK-018-T3-FT-002-W15 attempt 2.
status: supporting-only
---
# Host gates — attempt 2

Input basis: repository revision `cea5db2d45c06bb7585d071856d96b77079c8284`;
the worktree contains pre-existing unrelated dirty and untracked files, which
were preserved. The attempt-2 production correction changed only the listed
Weather Context/test lines in `changed-files-attempt-2.md`.

- `./gradlew testDebugUnitTest --tests com.hozayushka.app.WeatherContextTest.incompleteFullDailyConditionDataDoesNotReplaceSuccessfulCache --tests com.hozayushka.app.WeatherContextTest.emptyHourlyPayloadDoesNotReplaceSuccessfulHourlyCache`
  → exit `0`; both correction regressions passed, including empty and
  incomplete hourly variants.
- `./gradlew testDebugUnitTest` → exit `0`; 65 host tests passed, with zero
  failures/errors in the generated XML results.
- `./gradlew clean assembleDebug` → exit `0`; debug APK assembled. The known
  pre-existing `MainActivity.kt` deprecation warning remained non-fatal.
- `node scripts/mb-lint.mjs` → exit `0`; `mb-lint passed (78 files)`.
- `git diff --check -- app/src/main/kotlin/com/hozayushka/app/weather/WeatherCapability.kt app/src/test/kotlin/com/hozayushka/app/WeatherContextTest.kt`
  → exit `0`.
- Repository-wide `git diff --check` was not clean because of pre-existing
  trailing whitespace in
  `.tasks/TASK-MB-REVIEW-TASKS-PLAN/TASK-MB-REVIEW-TASKS-PLAN-S-TASKS-FT-002-final-report-docs-01.md`;
  this file is outside W15 correction scope and was not edited.

Clean-build APK artifact: `app/build/outputs/apk/debug/app-debug.apk`;
SHA-256 `5bf3c4588545996050d6f229fc59ba61613136bccc185d5a78f6283a05275d22`.

No emulator, ADB, connected/device Gradle task, target-device process, live
network request or live credential was used.
