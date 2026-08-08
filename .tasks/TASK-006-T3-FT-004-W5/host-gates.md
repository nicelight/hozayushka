---
description: Mandatory host gates for TASK-006-T3-FT-004-W5.
status: final
---
# Host gates — TASK-006-T3-FT-004-W5

Source basis before the final gate sequence:
`a93e46118f0f0b90e311b6174e3f5a8ed7d89fef`. The workspace retained broad
pre-existing tracked/untracked changes; no unrelated cleanup or reset was run.

## Required commands

- `./gradlew clean assembleDebug` → exit `0`, `BUILD SUCCESSFUL`; generated
  APK: `app/build/outputs/apk/debug/app-debug.apk`; SHA-256:
  `8c804c90cebaca635b71439852847eabe204a78551bf3ccd3b2b91b89d2911be`.
  The existing unrelated `MainActivity.onBackPressed` deprecation warning was
  observed; it did not fail the build.
- `./gradlew testDebugUnitTest` → exit `0`, `BUILD SUCCESSFUL`; 27 tests,
  zero skipped/failures/errors. XML reports are under
  `app/build/test-results/testDebugUnitTest/`.
- `node scripts/mb-lint.mjs` → exit `0`; `mb-lint passed (77 files)`.
- `git diff --check` → exit `0`.

## Gate interpretation

The unit suite includes the deterministic redacted daily mapping,
save/reload, selected-city day/night, ten-card 2×5 projection, incomplete-data
fallback, shared long-term timing/gesture and Tomorrow/Day-after intent tests.
No live provider, API key, ADB install or target runtime was used.
