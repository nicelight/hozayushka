---
description: Execution context for TASK-001-T3-FT-000-W0.
status: active
---
# Context — TASK-001-T3-FT-000-W0

## Purpose

Create the smallest executable Android walking skeleton required by REQ-000:
one deployable Kotlin Android app, one composition root, accepted capability
discovery roots, owner-local disposable state, a redacted provider fixture and
reproducible host/device proof commands.

## Execution Attempt

- attempt: 3
- started: 2026-08-05 13:35:12 +0500

This is the current neutral retry attempt for the selected `in_progress` task.
It supersedes Attempt 2 for same-claim execution receipts after the
adversarial boundary failure.

## Retry reconciliation

- Retry basis: `.protocols/TASK-001-T3-FT-000-W0/red-verification.md` and
  `.tasks/TASK-001-T3-FT-000-W0/TASK-001-T3-FT-000-W0-S-RED-VERIFY-final-report-docs-01.md`
  identified a forbidden `DisplayCapability → Yandex Weather Adapter` edge
  caused by direct `WeatherProviderRequest` construction in Display.
- Original claim-linked RED remains valid and is not recreated. Attempt 3 must
  produce fresh claim-equivalent GREEN for the corrected boundary path.
- Attempt 1 and Attempt 2 same-claim receipts are retained as
  `supporting-only`; they do not represent the corrected implementation.
- Correction boundary: move synthetic Foundation request construction behind
  the existing Weather Context owner and make Display call only that owner
  method. No product feature UX, reboot recovery, live network/key, new
  dependency or architecture edge is authorized.

## Inputs (what drives this task)

- Task record: `.memory-bank/tasks/TASK-001-T3-FT-000-W0.task.json`
- Task index: `.memory-bank/tasks/index.json`
- Foundation: `.memory-bank/foundation.md`
- Foundation feature: `.memory-bank/features/FT-000-foundation.md`
- Requirement: `REQ-000` in `.memory-bank/requirements.md`
- Architecture: `.memory-bank/architecture/system-architecture.md`
- Boundary graph: `.memory-bank/contracts/boundary-map.md`
- Capability contracts: `.memory-bank/contracts/capability-interfaces.md`
- Platform boundary: `.memory-bank/contracts/platform-runtime.md`
- Provider boundary: `.memory-bank/contracts/weather-provider.md`
- Secret handling: `.memory-bank/contracts/local-secret-handling.md`
- Local data: `.memory-bank/domains/local-data.md`
- Runtime proof: `.memory-bank/testing/runtime-verification.md`
- Tier policy: `.memory-bank/workflows/tier-policy.md`

## Decisions / assumptions

- Use a single `app` Android module with Kotlin DSL and the platform Android
  `Activity`/`View` APIs; no Compose, AndroidX, network client, database or
  other runtime library is needed for the walking skeleton.
- Use a provisional internal package `com.hozayushka.app` only to materialize
  the planned `<app-package>` roots. This does not create a public in-process
  contract or alter the accepted architecture; final packaging remains outside
  the Foundation behavior scope.
- Use owner-local Android `SharedPreferences` adapters for the runtime baseline
  and per-owner in-memory stores in host probes. No shared storage owner is
  introduced.
- Use a test-only generated synthetic credential that is never persisted or
  emitted; durable fixture/provider output is always `[REDACTED]`.

## Loaded context set

- `AGENTS.md`
- `.memory-bank/constitution.md`
- `.memory-bank/mbb/index.md`
- `.memory-bank/spec-backbone.md`
- `.memory-bank/spec-index.md`
- `.memory-bank/index.md`
- `.memory-bank/roles/general.md`
- `.memory-bank/tasks/TASK-001-T3-FT-000-W0.task.json`
- `.memory-bank/workflows/tier-policy.md`
- Direct Foundation, architecture, boundary, data, platform, provider, secret
  and runtime-verification specs listed above.

## Preflight result

- Indexed task resolves exactly to this file and `T3 / FT-000 / W0`.
- Status was `ready`; dependencies are empty; no blocker or required gate is
  recorded in the resolved Foundation context.
- Global Backbone is complete at Planning Revision 1. Product task-plan review
  is not required for `FT-000`.
- Existing checkout has no Gradle project, `app/src`, executable Android
  entrypoint, build/test/smoke path or task protocol for this task.
- `runtime_context.write_boundary` is omitted; semantic task scope and
  `forbidden_scope` remain binding.

## Commands run / environment notes

- `git status --short` → clean before task writes.
- `java -version` → OpenJDK 21.0.10 from Android Studio JBR.
- Android SDK → `/home/serg/Android/Sdk`, platforms 34/35 and build-tools 34.0.0
  available; ADB is installed.
- Local Gradle distribution → Gradle 8.9 available; AGP 8.7.0 and Kotlin
  Android plugin 2.0.20 are cached.
- `./gradlew clean` → OK.
- `./gradlew assembleDebug` → OK; APK checksum is recorded in
  `.tasks/TASK-001-T3-FT-000-W0/gate-results.md`.
- `./gradlew testDebugUnitTest` → OK; 2 tests, 0 failures, 0 errors; XML
  checksum is recorded in the same gate report.
- `node scripts/mb-lint.mjs` and `git diff --check` → OK.
- `adb devices` → no attached target device; install/start route is documented
  but target-device execution is unavailable in this host session.

## Attempt 2 correction surface (historical)

- `app/src/main/kotlin/com/hozayushka/app/display/DisplayCapability.kt` now
  provides an explicit Foundation probe mode with owner-routed Settings
  seed/reset, redacted weather refresh, timer start/rehydrate/cancel and audio
  probe controls.
- `app/src/main/kotlin/com/hozayushka/app/app/MainActivity.kt` accepts the
  internal `foundation_probe` launch extra and routes Activity pause/resume to
  the existing composition root.
- `app/src/main/kotlin/com/hozayushka/app/app/FoundationRuntime.kt` wires the
  lifecycle callback and existing owners without taking business ownership.
- `app/src/main/kotlin/com/hozayushka/app/timer/TimerCapability.kt` exposes
  persisted rehydration and the Timer → Android Runtime Adapter audio probe.
- `app/src/main/kotlin/com/hozayushka/app/adapters/platform/PlatformRuntimeAdapter.kt`
  observes ringer/DND policy and emits a bounded ToneGenerator probe without a
  new runtime dependency or permission.
- `app/src/test/kotlin/com/hozayushka/app/FoundationProbesTest.kt` now covers
  the public rehydration method in the durable owner-local probe.

The correction stays within the existing Main Display, Timer & Alert, Settings
& Location, Weather Context and Android Runtime Adapter graph. No forbidden
scope was touched.

## Attempt 2 commands / results (historical)

- `./gradlew clean assembleDebug testDebugUnitTest` → exit `0`; fresh clean APK
  and host-probe evidence are recorded in
  `.tasks/TASK-001-T3-FT-000-W0/gate-results.md#attempt-2--correction-receipts`.
- `node scripts/mb-lint.mjs` → exit `0`, `mb-lint passed (65 files)`.
- `git diff --check` → exit `0`.
- SDK `aapt dump badging` → exit `0`; launchable Activity is
  `com.hozayushka.app.app.MainActivity`.
- `adb devices` → no attached target; explicit probe launch route is
  `adb shell am start -n com.hozayushka.app/.app.MainActivity --ez
  foundation_probe true`, with no device execution claimed.
- Production reachability scan → Settings/weather/timer/lifecycle/audio hooks
  present under `app/src/main`; boundary and secret reports updated for
  Attempt 2.

## Attempt 3 correction surface

- `app/src/main/kotlin/com/hozayushka/app/weather/WeatherCapability.kt` keeps
  synthetic Foundation request construction inside the Weather Context owner.
- `app/src/main/kotlin/com/hozayushka/app/display/DisplayCapability.kt` calls
  the owner method and no longer imports `adapters.weather` or constructs a
  provider request.
- `app/src/test/kotlin/com/hozayushka/app/FoundationProbesTest.kt` exercises
  the Foundation weather action through the Weather Context owner boundary.

The correction removes the observed forbidden edge without changing the
registered graph: `Main Display → Weather Context → Yandex Weather Adapter`.

## Attempt 3 commands / results

- `./gradlew clean assembleDebug testDebugUnitTest` → exit `0`; fresh APK and
  host-probe evidence are recorded in
  `.tasks/TASK-001-T3-FT-000-W0/gate-results.md#attempt-3-correction-receipts`.
- `node scripts/mb-lint.mjs` → exit `0`, `mb-lint passed (65 files)`.
- `git diff --check` → exit `0`.
- Display/non-owner adapter scan → exit `0`; no direct Display or other
  non-owner `adapters.weather` import/request construction remains.
- SDK `aapt dump badging` → exit `0`; launchable Activity is
  `com.hozayushka.app.app.MainActivity`.
- `adb devices` → no attached target; no physical runtime result claimed.

## Open questions / blockers

- No material unresolved branch is open. Final target-device behavior remains
  a Foundation Gate probe and must not be claimed as PASS by this task.

## Next session

- Start by reading: `context.md`, `plan.md`, `progress.md`.
- Next action: run `/verify TASK-001-T3-FT-000-W0` with fresh T3 evidence; do not
  replay an unsafe external side effect, and do not claim device PASS without
  an attached target.
