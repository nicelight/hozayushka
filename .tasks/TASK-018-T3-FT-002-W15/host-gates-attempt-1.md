---
description: Host build and unit evidence for TASK-018-T3-FT-002-W15 attempt 1.
status: supporting-only
---
# Host gates — attempt 1

Input basis: repository revision `cea5db2d45c06bb7585d071856d96b77079c8284`; worktree had pre-existing unrelated Memory Bank, W14 source/test and operational artifacts, preserved. No emulator, ADB, connected/device Gradle task, target-device process or live request was run.

- `./gradlew clean assembleDebug` → exit `0`; `:app:assembleDebug` completed. One pre-existing `MainActivity.kt` deprecation warning; no build failure.
- `./gradlew testDebugUnitTest` → exit `0`; all `63 tests` completed successfully.
- `node scripts/mb-lint.mjs` → exit `0`; `mb-lint passed (78 files)`.
- targeted `git diff --check` over W15 source/test files → exit `0`; `targeted diff check: PASS`.

The test suite includes W15 fake-transport request shape, Yandex-shaped current/daily/hourly mapping, status/timeout/I/O/malformed cache preservation, optional condition/moon fallback and fixture-provider isolation. Existing hourly/long-term and Settings boundary tests remain compatibility regressions only; no foreign feature acceptance is claimed.

APK artifact from the clean build: `app/build/outputs/apk/debug/app-debug.apk`; SHA-256 `f94917b7c354d2d5da997f6d21d7e79b59117ad25a9ee49dbfd10dab5ecb701f`.
