W13 required gates — Attempt 1

1. `./gradlew clean assembleDebug`
   - cwd: `/home/serg/Projects/Mobile_APPS/hozayushka`
   - exit_code: 0
   - result: `BUILD SUCCESSFUL in 9s`; `:app:assembleDebug` completed.
   - note: pre-existing non-blocking `MainActivity.onBackPressed` deprecation warning.

2. `./gradlew testDebugUnitTest`
   - cwd: `/home/serg/Projects/Mobile_APPS/hozayushka`
   - exit_code: 0
   - result: `BUILD SUCCESSFUL in 3s`; `:app:testDebugUnitTest` completed.

3. `git diff --check`
   - cwd: `/home/serg/Projects/Mobile_APPS/hozayushka`
   - exit_code: 0
   - result: no output; static diff integrity passed.

Relevant source basis:
- git revision: `cea5db2d45c06bb7585d071856d96b77079c8284`
- changed task code files: exactly `DisplayCapability.kt`, `MainActivity.kt`,
  `DisplayProjectionTest.kt`
- task status: `in_progress`; no scheduler checkpoint or terminal-state write

Forbidden-scope audit: no W2/W11/W12 historical task record, Weather Context,
Timer & Alert, Forecast, PlatformRuntimeAdapter, FoundationRuntime,
scheduler/status, provider, Settings, credentials or target-device artifact was
modified by this task execution.
