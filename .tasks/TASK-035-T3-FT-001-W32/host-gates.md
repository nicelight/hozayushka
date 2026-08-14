---
description: Host gate evidence for TASK-035-T3-FT-001-W32.
status: evidence
---
# W32 host gates

Attempt 1, host-only. All commands ran in `/home/serg/Projects/Mobile_APPS/hozayushka`.

| Gate | Command | Result | Receipt |
|---|---|---|---|
| Fresh RED | `./gradlew :app:testDebugUnitTest --tests com.hozayushka.app.DisplayProjectionTest --tests 'com.hozayushka.app.DisplayProjectionTest.w32CompositionContractFitsBandAndClockZoneAtBothHostSizes' --console=plain` | Expected claim-specific failure before production correction; raw RED at both sizes recorded. | [`red-focused.log`](red-focused.log), JUnit report locator in `geometry.json` |
| Focused GREEN | `./gradlew :app:testDebugUnitTest --tests com.hozayushka.app.DisplayProjectionTest --console=plain` | PASS, 30/30. | [`focused-final.log`](focused-final.log), `app/build/test-results/testDebugUnitTest/TEST-com.hozayushka.app.DisplayProjectionTest.xml` |
| Clean Android debug build | `./gradlew clean assembleDebug --console=plain` | PASS; existing deprecation warning in `MainActivity.kt` only. | [`clean-build.log`](clean-build.log) |
| Full host unit suite | `./gradlew testDebugUnitTest --console=plain` | PASS, 118/118 across 13 suites. | [`full-host-tests-final.log`](full-host-tests-final.log), `app/build/test-results/testDebugUnitTest/` |
| Android debug lint | `./gradlew lintDebug --console=plain` | PASS. | [`lint.log`](lint.log), `app/build/reports/lint-results-debug.html` |
| Static diff integrity | `git diff --check` | PASS, no whitespace errors. | [`diff-check.log`](diff-check.log) |

No adb, install/upload, emulator/AVD/QEMU, device launch, network or
credentials were used. The local debug APK is build output only; physical and
runtime evidence remains DEFERRED.
