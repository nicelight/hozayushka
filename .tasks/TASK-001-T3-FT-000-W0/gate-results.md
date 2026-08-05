# Gate results — attempt 1

These are executor-owned current-attempt results. They support `/verify`; they
are not independent provenance or a final T3 verdict.

## Android debug build

- claim: `REQ-000` reproducible one-module Android debug build and
  `runtime-verification.md#foundation-minimal-proof` clean-build route.
- command: `./gradlew clean` followed by the required gate `./gradlew assembleDebug`.
- cwd: `/home/serg/Projects/Mobile_APPS/hozayushka`
- input_state_basis immediately before `assembleDebug`:
  - repository revision: `e00238676b0810431ba351a6c2e091898022d8cb`
  - source/config aggregate: `d52e94cbca9407120f492e1d3e7dcdca9201e3c9a86c8f93f6c3068abe31b189`
  - tracked/staged/unstaged/untracked state: task protocol/evidence, Android
    scaffold, Gradle wrapper, `.gitignore` and Foundation documentation were
    present as listed by `git status --short`; no unrelated user changes were
    observed.
  - environment: `JAVA_HOME=/opt/android-studio/jbr`,
    `ANDROID_HOME=/home/serg/Android/Sdk`, `ANDROID_SDK_ROOT=/home/serg/Android/Sdk`;
    Gradle wrapper 8.9, AGP 8.7.0, Kotlin Android plugin 2.0.20.
- exit_code: `0`
- completed_at: `2026-08-04 15:06` local time (Asia/Dushanbe)
- result: `BUILD SUCCESSFUL`; `:app:assembleDebug` completed after a clean.
- artifact: `app/build/outputs/apk/debug/app-debug.apk`
- artifact checksum: `48c4c1322272678015ef587a165b52335c0a18cd4366cdf2e61e92da546f077e`
- receipt status: supporting-only; superseded by Attempt 3 and subject to fresh verifier rerun because
  this is a T3 task.

## Host-side Foundation probes

- claim: `local-data.md#durable-data-rules` owner-local reset/reload and
  `local-secret-handling.md#evidence-and-verification` redacted provider path.
- command: `./gradlew testDebugUnitTest`
- cwd: `/home/serg/Projects/Mobile_APPS/hozayushka`
- input_state_basis immediately before command:
  - repository revision: `e00238676b0810431ba351a6c2e091898022d8cb`
  - source/config aggregate: `d52e94cbca9407120f492e1d3e7dcdca9201e3c9a86c8f93f6c3068abe31b189`
  - generated build output: APK from the current clean `assembleDebug`,
    checksum recorded above; no source/config changes occurred between the
    build and test snapshots.
  - dependency/toolchain qualifiers: same JBR, SDK and Gradle wrapper as the
    build receipt; JUnit `4.13.2` is the only test dependency.
- exit_code: `0`
- completed_at: `2026-08-04 15:08` local time (Asia/Dushanbe)
- result: `BUILD SUCCESSFUL`; `FoundationProbesTest` reports `tests="2"`,
  `failures="0"`, `errors="0"`, `skipped="0"`.
- evidence: `app/build/test-results/testDebugUnitTest/TEST-com.hozayushka.app.FoundationProbesTest.xml`
- evidence checksum: `163b962e0291dcdbc24d308df2bf34dbca66ac650c69a5d3709bb1f2d064f8b9`
- receipt status: supporting-only; superseded by Attempt 3 and subject to fresh verifier rerun because
  this is a T3 task.

## Documentation/lint checks

- `git diff --check` → exit `0`, no whitespace errors.
- `node scripts/mb-lint.mjs` → exit `0`, `mb-lint passed (65 files)`.

## Attempt 2 — correction receipts

These are executor-owned receipts for the retry correction. They remain
supporting evidence for the independent T3 verifier; they are not a final
functional or semantic verdict.

### Android debug build

- claim: corrected `REQ-000` Foundation implementation, including the
  supported installed-app probe route.
- command: `./gradlew clean assembleDebug testDebugUnitTest` (the build portion
  completed before the test portion in the same clean run).
- cwd: `/home/serg/Projects/Mobile_APPS/hozayushka`
- input_state_basis: revision `e00238676b0810431ba351a6c2e091898022d8cb` plus
  Attempt 2 source changes in `DisplayCapability.kt`, `FoundationRuntime.kt`,
  `MainActivity.kt`, `PlatformRuntimeAdapter.kt`, `TimerCapability.kt`, the
  host probe test, and Foundation navigation/protocol evidence; no unrelated
  user changes observed; JBR/SDK/Gradle environment unchanged.
- exit_code: `0`
- completed_at: `2026-08-04 16:12` local time (Asia/Dushanbe)
- result: `BUILD SUCCESSFUL`; `:app:assembleDebug` completed after
  `:app:clean`.
- artifact: `app/build/outputs/apk/debug/app-debug.apk`
- artifact checksum: `0162c8f282334150f6731bc00efebd5e302c084693fc11534552eb1c80ee7188`
- receipt_status: supporting-only; superseded by Attempt 3 and subject to fresh T3
  verification.

### Host-side Foundation probes

- claim: corrected owner-local reset/reload, timer rehydration and redacted
  provider path.
- command: same clean run, `./gradlew clean assembleDebug testDebugUnitTest`.
- cwd: `/home/serg/Projects/Mobile_APPS/hozayushka`
- input_state_basis: same clean Attempt 2 source/config basis as the build
  receipt; no source/config changes occurred between build and test.
- exit_code: `0`
- completed_at: `2026-08-04 16:12` local time (Asia/Dushanbe)
- result: `FoundationProbesTest` reports `tests="2"`, `failures="0"`,
  `errors="0"`, `skipped="0"`.
- evidence: `app/build/test-results/testDebugUnitTest/TEST-com.hozayushka.app.FoundationProbesTest.xml`
- evidence checksum: `cd8d5cec623a56829b9f8dd9f3e2f31edd0b53f6e82c237c4174875e9986fda7`
- receipt_status: supporting-only; superseded by Attempt 3 and subject to fresh T3
  verification.

### Supported installed-app route inspection

- command: `/home/serg/Android/Sdk/build-tools/34.0.0/aapt dump badging app/build/outputs/apk/debug/app-debug.apk`; source call-site and interaction scans; `adb devices`.
- result: APK exposes launchable `com.hozayushka.app.app.MainActivity` with
  landscape configuration. Explicit route:
  `adb shell am start -n com.hozayushka.app/.app.MainActivity --ez foundation_probe true`.
  Production sources now contain Settings seed/reset, weather refresh, timer
  start/cancel/rehydrate, Activity pause/resume and AudioManager/ToneGenerator
  call-sites. `adb devices` still reports no attached target, so no device
  observation is claimed.

### Repository and safety checks

- `node scripts/mb-lint.mjs` → exit `0`, `mb-lint passed (65 files)`.
- `git diff --check` → exit `0`.
- Boundary/import review → exit `0`; updated report in
  `boundary-review.md#attempt-2-correction-review`.
- Secret/artifact scan → exit `0`; updated report in
  `secret-scan.md#attempt-2-correction-scan`.

## Attempt 3 — boundary correction receipts

These are executor-owned receipts for the current retry. They support
independent T3 verification and are not an independent lifecycle verdict.

### Android debug build and host-side Foundation probes

- attempt: `3`
- receipt_status: `current`
- claim: corrected `REQ-000` Foundation implementation preserves the accepted
  `Main Display → Weather Context → Yandex Weather Adapter` graph while keeping
  the installed-app probe reachable.
- command: `./gradlew clean assembleDebug testDebugUnitTest`
- cwd: `/home/serg/Projects/Mobile_APPS/hozayushka`
- input_state_basis immediately before command: repository revision
  `e00238676b0810431ba351a6c2e091898022d8cb`; existing Foundation scaffold,
  protocol/evidence changes and Attempt 3 source edits were present; changed
  source hashes were `40a9251da80eda91b8d2e8b0976e591bf62a75981ef5e74a2277c3200dd908c7`
  (`DisplayCapability.kt`),
  `3b41173bc1585254d7e63147e17bbda7fda02e62adb359d6440f3543bd973ea0`
  (`WeatherCapability.kt`) and
  `5fb8e3f07ce3b54f37e6372077600bc302ec671a4faa1e2a7f20cadab3a24df5`
  (`FoundationProbesTest.kt`); local JBR/SDK/Gradle inputs unchanged; no
  unrelated user changes observed.
- exit_code: `0`
- completed_at: `2026-08-05 13:42 +0500` local time.
- result: `BUILD SUCCESSFUL`, 40 actionable tasks; APK
  `app/build/outputs/apk/debug/app-debug.apk`, SHA-256
  `df20b2e02c5f083c4a740c1ee0e7f1f999d790800668b88b3e556b2b258bde5f`;
  XML `app/build/test-results/testDebugUnitTest/TEST-com.hozayushka.app.FoundationProbesTest.xml`,
  SHA-256 `f2ca3528c14e7b6aeb22da3fd99638ec660d875019bdc23bd55305f0d015896e`,
  reports `tests="2"`, `failures="0"`, `errors="0"`, `skipped="0"`.

### Boundary, secret and package checks

- attempt: `3`
- receipt_status: `current`
- claim: Display and other non-owner capabilities do not bypass Weather
  Context, and no credential-like value reaches source, evidence or APK.
- commands: the Display/non-owner adapter scan; accepted-root boundary scan;
  secret/artifact scan; `node scripts/mb-lint.mjs`; `git diff --check`; and
  `/home/serg/Android/Sdk/build-tools/34.0.0/aapt dump badging
  app/build/outputs/apk/debug/app-debug.apk`, as recorded in the current
  protocol and the Attempt 3 sections of `boundary-review.md` and
  `secret-scan.md`.
- cwd: `/home/serg/Projects/Mobile_APPS/hozayushka`
- exit_code: `0` for each check.
- input_state_basis: same Attempt 3 source/config basis as the build receipt;
  freshly produced APK/XML artifacts.
- completed_at: `2026-08-05 13:43 +0500` local time.
- result: Display/non-owner adapter import scan clean; `mb-lint passed (65
  files)`; no whitespace errors; package exposes launchable
  `com.hozayushka.app.app.MainActivity`; source/evidence and APK scans report
  no credential-like match. `adb devices` had no attached target, so no device
  observation is claimed.
