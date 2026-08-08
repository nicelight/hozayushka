# TASK-003-T3-FT-001-W2 — Attempt 1 execution receipts

These are executor-owned supporting receipts, not independent `/verify` or
`/red-verify` evidence.

## Claim-linked RED

- Artifact: `red-baseline.md`.
- AC-001: static Foundation window policy already GREEN; target-only proof remains open.
- AC-002: RED — Foundation had only three generic TextViews and no four-card/three-preset shell.
- AC-003: RED — Foundation had only `HH:mm`, with no accepted date projection.
- AC-004: RED — no online/offline/countdown colon projection existed.
- AC-005: RED — no city gesture, Settings destination or Back seam existed.

## Claim-linked GREEN

- AC-001 / AC-002 / AC-003 / AC-004 / AC-005: `app/src/test/kotlin/com/hozayushka/app/DisplayProjectionTest.kt` — deterministic date/timezone, stable counts, colon timing/brightness and city routing checks.
- AC-001 / REQ-001: `AndroidManifest.xml` landscape declaration and Platform Runtime fullscreen/keep-screen-on flags remain present.
- AC-005: `SettingsCapability.createDestinationView` owns the minimal destination; MainActivity only wires the callback and Back route.

## Required gates

### Host unit tests

- command: `./gradlew testDebugUnitTest`
- cwd: `/home/serg/Projects/Mobile_APPS/hozayushka`
- result: exit `0`, `BUILD SUCCESSFUL`.
- test artifacts: `app/build/test-results/testDebugUnitTest/TEST-com.hozayushka.app.DisplayProjectionTest.xml` (4 test cases) and `TEST-com.hozayushka.app.FoundationProbesTest.xml` (2 test cases); no failure/error/skip match.
- final run: 2026-08-07 after all production changes.

### Clean Android debug build

- command: `./gradlew clean assembleDebug`
- cwd: `/home/serg/Projects/Mobile_APPS/hozayushka`
- result: exit `0`, `BUILD SUCCESSFUL`, 34 actionable tasks.
- artifact: `app/build/outputs/apk/debug/app-debug.apk`
- SHA-256: `f84341b54fbea3142dafe3c86bd4541589168908bd86c5f6cd77cd9b43f90647`

## Boundary and scope checks

- `git diff --check` on task production paths: exit `0`.
- `node scripts/mb-lint.mjs` → exit `0`, `mb-lint passed (76 files)`.
- Product Main Display/composition-root scan: no provider/private-storage access and no neighbor writes; existing Foundation probe remains the only probe-only owner route.
- No new module, dependency, public contract or graph edge was added.
- No API key, provider request, backend, Google Services, reboot recovery or secret-bearing evidence was used.

## Target-device gate

- command: `adb devices -l`
- result: exit `0`; no devices/emulators listed. Install, launch, 1280×720 fullscreen/readability, Settings Back interaction and cleanup are unavailable in this session.
- disposition: blocker for independent target-device evidence only; no target PASS is claimed and no install/launch side effect was attempted.

## Reuse candidate

None proposed. Gradle inputs are the dirty shared workspace and target state is external/unavailable; receipts are supporting-only.
