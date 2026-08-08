---
description: Host build, unit and fixture gate evidence for TASK-012-T3-FT-003-W4.
status: active
---
# Host gates — TASK-012-T3-FT-003-W4

## Attempt 1 final host gates

- source basis: repository revision
  `a93e46118f0f0b90e311b6174e3f5a8ed89fef` plus the pre-existing dirty
  worktree and task repair delta.
- `git diff --check` — exit `0` before the clean build.
- `./gradlew clean assembleDebug` — exit `0`, `BUILD SUCCESSFUL`; APK
  `app/build/outputs/apk/debug/app-debug.apk`; SHA-256
  `6e5f042862ff829a35630a6319b3da96a993b118ac528d8a4c9c82e2b8a92de7`.
  Existing non-blocking SDK XML compatibility and unrelated MainActivity
  deprecation warnings were observed.
- `./gradlew testDebugUnitTest` — exit `0`, `BUILD SUCCESSFUL`; `22` tests,
  `0` skipped, `0` failures, `0` errors. XML reports are under
  `app/build/test-results/testDebugUnitTest/`.
- focused fixture command:
  `./gradlew testDebugUnitTest --tests 'com.hozayushka.app.WeatherContextTest.supportedFullDayHourlyPayloadNormalizesAcceptedCityLocalSlots' --tests 'com.hozayushka.app.WeatherContextTest.selectedHourlyRequiredFieldMissingKeepsProjectionUnavailable'`
  — exit `0`, `BUILD SUCCESSFUL`; both task-owned fixture tests passed.
  Report: `app/build/test-results/testDebugUnitTest/TEST-com.hozayushka.app.WeatherContextTest.xml`.

No live request, API key, production secret or device runtime was used.
