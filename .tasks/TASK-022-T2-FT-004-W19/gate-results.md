---
description: Attempt-1 executor gate results for TASK-022-T2-FT-004-W19.
status: supporting
---
# Gate results — TASK-022-T2-FT-004-W19 Attempt 1

| Gate | Exact command | Result |
|---|---|---|
| Clean Android debug build | `./gradlew clean assembleDebug --no-daemon` | exit `0`; `BUILD SUCCESSFUL`; 34 actionable tasks; APK SHA-256 `20d4c84d2ea43a3758bbc58b5e7e6bcfb67e4aaa2db41dd1b4879e1f97716b65` |
| Full host/unit suite | `./gradlew testDebugUnitTest --no-daemon` | final post-assertion rerun exit `0`; `BUILD SUCCESSFUL`; 95 tests, 0 failures, 0 errors, 0 skipped; XML under `app/build/test-results/testDebugUnitTest/` |
| Memory Bank and diff integrity | `node scripts/mb-lint.mjs && git diff --check` | exit `0`; `mb-lint passed (78 files)`; no diff-check findings |
| Claim-equivalent W19 GREEN | `./gradlew testDebugUnitTest --tests com.hozayushka.app.ForecastSessionTest.selectedProvidersKeepTenDayHorizonAndOpenWeatherUsesHonestEightPlusTwoProjection --tests com.hozayushka.app.ForecastSessionTest.selectedProviderChangeDoesNotBorrowAnotherProviderLongTermCache --no-daemon` | exit `0`; `BUILD SUCCESSFUL` |
| Transport/ownership static scan | fixed-string scan for `HttpURLConnection`, `HttpUrlConnection`, `WeatherTransport`, `URL(` and direct selected-provider `.fetch` over W19 path | exit `0`; no matches; provider threshold/record identity/date projection/exact message anchors present |
| Source/evidence redaction scan | fixed-string scan for `fromUserInput("` and `appid=` over W19 source/evidence | exit `0`; no raw credential-shaped constructor or appid literal |
| W19 whitespace scan | `rg -n '[[:blank:]]+$' <W19 source/protocol/evidence paths>` in fail-closed wrapper | exit `0`; no matches |
| Debug APK credential scan | `unzip -p app/build/outputs/apk/debug/app-debug.apk classes.dex | strings | rg -n -F -e 'api_key=' -e 'api-key=' -e 'appid=' -e 'sk-'` in fail-closed wrapper | exit `0`; no credential-shaped APK string |

## Boundary

- All fixtures and evidence are synthetic/redacted. No real credentials were
  read or introduced.
- No Android Studio, emulator/AVD, QEMU, adb, physical device, live provider,
  network call or runtime PASS activity was used.
- Existing unrelated upstream dirty changes were preserved; lifecycle/status/
  checkpoint and historical W18/W20 evidence were not edited.
