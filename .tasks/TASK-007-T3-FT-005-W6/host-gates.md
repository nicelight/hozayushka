---
description: Host build and unit gate evidence for TASK-007-T3-FT-005-W6.
status: final
---
# Host gates — TASK-007-T3-FT-005-W6

## Current attempt

- attempt: 1
- source basis: repository `HEAD` `a93e46118f0f0b90e311b6174e3f5a8ed7d89fef` with the pre-existing dirty/untracked workspace declared in `context.md`; task-owned FT-005 files are listed in `progress.md`.
- target: no Android device/emulator attached.

## Required commands

- `./gradlew clean assembleDebug` — exit `0`; `BUILD SUCCESSFUL`; debug APK
  SHA-256: `1ab29ce24ff60a593b059e85654897a6907663563eca5fcbc85a86a72c80b9b6`.
- `./gradlew testDebugUnitTest` — exit `0`; test result XML reports
  `DisplayProjectionTest=5`, `ForecastSessionTest=9`,
  `FoundationProbesTest=3`, `TimerPresetTest=4`, `WeatherContextTest=10`;
  total `31/31`, failures `0`, errors `0`. Artifact directory:
  `app/build/test-results/testDebugUnitTest/`.
- `node scripts/mb-lint.mjs` — exit `0`; `mb-lint passed (77 files)`.
- `git diff --check` — exit `0`.

## Boundary/static/redaction command

The bounded scan passed with exit `0`: Main Display contains no private store,
raw provider or adapter access; Timer contains no Settings private-store,
weather or provider bypass; source/tests/task-local evidence contain no
Yandex-key header, bearer token, private-key marker or API-key assignment; the
added diff contains no FT-006/FT-007 behavior or secret marker.

## Target route

`adb devices` returned only `List of devices attached` with no target. Target
visual/readability/runtime evidence is `DEFERRED`, non-blocking, with residual
risk that actual Android rendering and custom-ROM behavior remain unobserved.
No runtime `PASS` is claimed.
