# Foundation Gate receipts — TASK-002-T3-FT-000-W1

This is executor-owned Attempt 1 evidence. It supports independent T3
verification and is not a lifecycle verdict. All commands ran from
`/home/serg/Projects/Mobile_APPS/hozayushka`. The repository revision was
`e00238676b0810431ba351a6c2e091898022d8cb`; the checkout already contained
the uncommitted TASK-001 Android baseline and unrelated workflow/Memory Bank
changes. No production source was changed during this task.

## Accepted RED/GREEN path

Meaningful RED is not applicable: this task performs a verification-only final
gate and changes no production behavior. The accepted alternative is fresh
clean/reset execution of the applicable host/device checks with synthetic
fixtures and redacted evidence. Host GREEN is recorded below; device checks
remain unavailable because no ADB target is attached.

## Clean build and host tests

- attempt: `1`
- receipt_status: `supporting-only` (no reuse candidate proposed)
- claim: `REQ-000` Foundation minimal proof; clean APK assembly and
  deterministic owner-local host probes.
- command: `./gradlew clean assembleDebug testDebugUnitTest`
- cwd: `/home/serg/Projects/Mobile_APPS/hozayushka`
- input_state_basis: snapshot `.tasks/TASK-002-T3-FT-000-W1/receipt-A-input.txt`
  (SHA-256 `85cfdf17dd550dfc7f825c89636ee775fdc11bdbe403e01ac57e4b3d35e33b42`),
  repository revision above, existing dirty TASK-001 baseline, JBR/Android
  SDK/Gradle wrapper and JUnit test dependency.
- exit_code: `0`
- completed_at: `2026-08-05 18:40:35 +0500`
- evidence: `receipt-A-clean-build-host-tests.log` (SHA-256
  `6c8373b3f154115a0ff8b11c40483860965e04893be82742a85f6b911a3a4de9`),
  APK `app/build/outputs/apk/debug/app-debug.apk` (SHA-256
  `df20b2e02c5f083c4a740c1ee0e7f1f999d790800668b88b3e556b2b258bde5f`),
  JUnit XML `app/build/test-results/testDebugUnitTest/TEST-com.hozayushka.app.FoundationProbesTest.xml`
  (SHA-256 `8e435e33eb9794f1798a087de768302454f6fc39e4efa3b746ab18cb51ef4053`).
  Result: `BUILD SUCCESSFUL`, 40 actionable tasks, 2 tests, 0 failures, 0
  errors, 0 skipped.

## ADB target and required install/start route

- claim: accepted APK install/start/Foundation probe route and target-device
  compatibility prerequisite.
- command: `adb devices -l`; exit `0`, output `List of devices attached` with
  no device; receipt `receipt-B-adb-devices.log` (SHA-256
  `36f15c2fe32964a6fbe902e914a47739d4e6304b0d3e812bf08096ff0419b7ae`).
- command: `adb install -r app/build/outputs/apk/debug/app-debug.apk`; exit `1`,
  `adb: no devices/emulators found`; receipt `receipt-C-adb-install.log`
  (SHA-256 `edc4912a1aad745078e1f8e44cb6f421c0b66ee22cefd035a6a281f9016baeca`),
  input snapshot `receipt-C-adb-install-input.txt` (SHA-256
  `5b716240c4df1af0f44878519bf6bce54ebe65fa14141780ceae9f6463fc1a15`).
- command: `adb shell am start -n com.hozayushka.app/.app.MainActivity`; exit
  `1`, same no-device output; receipt `receipt-D-adb-launch.log` (SHA-256
  `edc4912a1aad745078e1f8e44cb6f421c0b66ee22cefd035a6a281f9016baeca`).
- command: `adb shell am start -n com.hozayushka.app/.app.MainActivity --ez foundation_probe true`;
  exit `1`, same no-device output; receipt `receipt-E-adb-foundation-probe.log`
  (SHA-256 `edc4912a1aad745078e1f8e44cb6f421c0b66ee22cefd035a6a281f9016baeca`).
- disposition: install/start/installed-app smoke and target compatibility are
  unavailable in this host session. No target-device PASS is inferred or
  claimed, and no unauthorized side effect was taken.

## Secret and artifact check

- claim: `local-secret-handling.md#evidence-and-verification`; no credential-
  like value in source/test/evidence/APK.
- command:
  `if rg -n -i --hidden -e 'x-yandex-weather-key' -e 'bearer[[:space:]]+[A-Za-z0-9._-]{12,}' -e "api([_-]|[[:space:]])key[[:space:]]*[:=][[:space:]]*'[^']+'" app/src/main app/src/test .tasks/TASK-002-T3-FT-000-W1 .protocols/TASK-002-T3-FT-000-W1 app/build/test-results; then echo 'credential-like source/evidence match found'; exit 1; else echo 'no credential-like source/evidence match'; fi; if strings app/build/outputs/apk/debug/app-debug.apk | rg -n -i -e 'x-yandex-weather-key' -e 'bearer[[:space:]]+[A-Za-z0-9._-]{12,}' -e "api([_-]|[[:space:]])key[[:space:]]*[:=][[:space:]]*'[^']+'"; then echo 'credential-like APK match found'; exit 1; else echo 'no credential-like APK match'; fi`
- input_state_basis: `receipt-F-secret-scan-input.txt` (SHA-256
  `f411af8a254baeb576adb60d692970c8fcba8b50ea0ac30183ada2d8ae275db9`).
- exit_code: `0`
- completed_at: `2026-08-05 18:42 +0500`
- evidence: `receipt-F-secret-scan.log` (SHA-256
  `a85dc9fb5fc5de375ac3f0b6fd2368c5088834ae00559a5390754479cd96c74c`),
  output `no credential-like source/evidence match` and `no credential-like
  APK match`; this first scan is supporting-only because the evidence report
  below was created afterward.
- current receipt: command repeated after adding this report, excluding only
  `gate-results.md` because it documents the literal scan regex:
  `if rg -n -i --hidden --glob '!gate-results.md' -e 'x-yandex-weather-key' -e 'bearer[[:space:]]+[A-Za-z0-9._-]{12,}' -e "api([_-]|[[:space:]])key[[:space:]]*[:=][[:space:]]*'[^']+'" app/src/main app/src/test .tasks/TASK-002-T3-FT-000-W1 .protocols/TASK-002-T3-FT-000-W1 app/build/test-results; then echo 'credential-like source/evidence match found'; exit 1; else echo 'no credential-like source/evidence match'; fi; if strings app/build/outputs/apk/debug/app-debug.apk | rg -n -i -e 'x-yandex-weather-key' -e 'bearer[[:space:]]+[A-Za-z0-9._-]{12,}' -e "api([_-]|[[:space:]])key[[:space:]]*[:=][[:space:]]*'[^']+'"; then echo 'credential-like APK match found'; exit 1; else echo 'no credential-like APK match'; fi`
- current receipt_status: `supporting-only`; receipt
  `receipt-J-final-secret-scan.log` (SHA-256
  `a85dc9fb5fc5de375ac3f0b6fd2368c5088834ae00559a5390754479cd96c74c`),
  input snapshot `receipt-J-final-secret-scan-input.txt` (SHA-256
  `28d88671a9fe98c3a063f232b560b48800ab0a2176d10d8c90b3df5c0bb64b62`),
  completed `2026-08-05 18:48 +0500`, exit `0`.

## Boundary, package and runtime-route checks

- claim: direct task-linked architecture/boundary rules, accepted package
  entrypoint and static runtime route remain intact; no new permission.
- command: targeted non-owner weather import/request scan, accepted-root and
  forbidden technical-root scan, manifest permission scan, SDK `aapt dump
  badging`, and `git diff --check`; exact output is in
  `receipt-G-static-checks.log`.
- input_state_basis: `receipt-G-static-checks-input.txt` (SHA-256
  `908d72172af2638e73dad2750f0776a617e539b3344056f783e08c76bbf94c11`).
- exit_code: `0`
- completed_at: `2026-08-05 18:42 +0500`
- evidence: `receipt-G-static-checks.log` (SHA-256
  `23ef457379a50aa6b8d440c66fbd480ef380e8061536311eb59f336cd40cd113`);
  Display/non-owner provider scans clean, no shared technical/event root,
  no event/backend/extra-runtime token, no extra permission, package and
  `com.hozayushka.app.app.MainActivity` metadata present, diff check clean.
- static runtime route command: `rg -n 'screenOrientation="landscape"|FLAG_KEEP_SCREEN_ON|SYSTEM_UI_FLAG_FULLSCREEN|IMMERSIVE_STICKY|WindowInsets.Type.systemBars|setRequestedOrientation|EXTRA_FOUNDATION_PROBE|onPause|onResume|requestAudioProbeAt|refreshFoundationFixture|rehydrateAt' app/src/main/AndroidManifest.xml app/src/main/kotlin/com/hozayushka/app`
- static runtime route evidence: `receipt-H-runtime-route.log` (SHA-256
  `eb0020f5b752f01d8c0ad76948006d8f704bb81c063d060b4b29569b00f902ab`).
  This is implementation route inspection, not device behavior evidence.

## Memory Bank lint

- claim: project-native durable-doc structure remains valid after task
  bookkeeping.
- command: `node scripts/mb-lint.mjs`
- input_state_basis: `receipt-I-mb-lint-input.txt` (SHA-256
  `3e6efa5b6a5925b993dce4918263b2d91df6d4465412e70e36593513e8f25766`).
- exit_code: `0`
- completed_at: `2026-08-05 18:42 +0500`
- evidence: `receipt-I-mb-lint.log` (SHA-256
  `e406fdb56985969b090fa4636c3d0b97234c668a9104f2dc3093df8e3b2d405f`),
  `mb-lint passed (66 files)`.

## Actual change surface and scope

- Changed by this execution: `.memory-bank/tasks/TASK-002-T3-FT-000-W1.task.json`
  for required lifecycle transitions; `.protocols/TASK-002-T3-FT-000-W1/`;
  `.tasks/TASK-002-T3-FT-000-W1/`; generated `app/build/` outputs.
- No `app/src` production file, Gradle source/config, public boundary,
  dependency graph, permission, credential or forbidden-scope path was
  changed by this task. The existing untracked `app/` baseline was present in
  the Attempt 1 input snapshot.
- `runtime_context.write_boundary`: not set. Forbidden scope was not touched.
- No tier escalation or material design decision surfaced.
