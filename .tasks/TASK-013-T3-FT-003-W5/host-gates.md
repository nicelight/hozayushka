---
description: Host build, unit and focused gate evidence for TASK-013-T3-FT-003-W5.
status: active
---
# Host gates — TASK-013-T3-FT-003-W5

## Attempt 1 source basis

- The worktree was already broadly dirty before this attempt. The only
  implementation delta for TASK-013 is the task-local AC-003 regression test;
  no production Kotlin or provider normalization file was changed.
- AC-001/AC-004/AC-005 were pre-implementation GREEN and received no artificial
  production change.

## Claim-linked fixture gate

- focused command: `./gradlew testDebugUnitTest --tests 'com.hozayushka.app.ForecastSessionTest.completeReadModelIsConsumedByHourlySessionWithSharedCardInputs' --tests 'com.hozayushka.app.ForecastSessionTest.incompleteHourlyDataStaysUnavailableAndDoesNotCreateSession' --tests 'com.hozayushka.app.ForecastSessionTest.sharedSessionTimingAndGesturesFollowAcceptedTransitions' --tests 'com.hozayushka.app.ForecastSessionTest.holdKeepsSessionOpenBeyondOriginalDeadlineAndReleaseClosesImmediately'`
- exit `0`, `BUILD SUCCESSFUL`; 4 selected tests passed.
- evidence: `green-fixture.md` and
  `app/build/test-results/testDebugUnitTest/TEST-com.hozayushka.app.ForecastSessionTest.xml`.

## Mandatory host gates

- `./gradlew clean assembleDebug` — exit `0`, `BUILD SUCCESSFUL`; APK
  `app/build/outputs/apk/debug/app-debug.apk`; SHA-256
  `6e5f042862ff829a35630a6319b3da96a993b118ac528d8a4c9c82e2b8a92de7`.
  Existing non-blocking SDK compatibility and unrelated MainActivity
  deprecation warning were observed.
- `./gradlew testDebugUnitTest` — exit `0`, `BUILD SUCCESSFUL`; `23` tests,
  `0` skipped, `0` failures, `0` errors. Reports:
  `app/build/test-results/testDebugUnitTest/`.
- `node scripts/mb-lint.mjs` — exit `0`; `mb-lint passed (77 files)`.
- `git diff --check` — exit `0`.
- boundary/static/forbidden-diff/redaction bundle — exit `0`; details in
  `static-boundary-redaction.md`.

No live request, API key, production secret, ADB install or target runtime was
used.
