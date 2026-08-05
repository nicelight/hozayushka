# TASK-001-T3-FT-000-W0 — execution report

## Execution result

Retry Attempt 2 repaired the adversarially identified missing installed-app
Foundation probe surface within the existing TASK-001 boundary. The normal
launch remains the static Foundation shell; the explicit probe route is:

```text
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.hozayushka.app/.app.MainActivity --ez foundation_probe true
```

The probe mode exposes owner-routed controls for Settings seed/reset, redacted
weather fixture refresh, timer start/rehydration/cancel and audio policy probe.
Activity pause/resume is wired through the composition root, persisted timer
state is recalculated on resume, and the Android adapter checks ringer/DND
policy before issuing a bounded ToneGenerator probe.

## Attempt and change surface

- Execution Attempt: `2`, started `2026-08-04 16:08:43 +0500`.
- Retry basis: the prior semantic-fail evidence in
  `.protocols/TASK-001-T3-FT-000-W0/red-verification.md` and its task report.
- Production files changed in this retry:
  `DisplayCapability.kt`, `MainActivity.kt`, `FoundationRuntime.kt`,
  `TimerCapability.kt` and `PlatformRuntimeAdapter.kt`.
- Supporting host probe change: `FoundationProbesTest.kt` now exercises
  `rehydrateAt`.
- Durable navigation/protocol evidence changed in `foundation.md`,
  `runtime-verification.md`, `FT-000/plan.md`, and the TASK-001 protocol/task
  evidence files.
- No new dependency, permission, backend, event bus, product feature behavior,
  reboot recovery, real credential or unaccepted architecture edge was added.
- Task status remains `in_progress`; `/exe` did not make a final lifecycle
  decision.

## Execution checks

- `./gradlew clean assembleDebug testDebugUnitTest` → exit `0`.
- APK: `app/build/outputs/apk/debug/app-debug.apk`, SHA-256
  `0162c8f282334150f6731bc00efebd5e302c084693fc11534552eb1c80ee7188`.
- Host XML: `tests="2"`, `failures="0"`, `errors="0"`, `skipped="0"`; SHA-256
  `cd8d5cec623a56829b9f8dd9f3e2f31edd0b53f6e82c237c4174875e9986fda7`.
- `node scripts/mb-lint.mjs` → exit `0`.
- `git diff --check` → exit `0`.
- SDK `aapt dump badging` → exit `0`; launchable Activity remains
  `com.hozayushka.app.app.MainActivity`.
- Production reachability scan finds Settings/weather/timer/lifecycle/audio
  call-sites under `app/src/main`.
- Boundary review and secret/artifact scan pass for the corrected surface; see
  `boundary-review.md#attempt-2-correction-review` and
  `secret-scan.md#attempt-2-correction-scan`.
- `adb devices` has no attached target. No physical fullscreen, interruption,
  rehydration or audio observation is claimed.

## Handoff

Current evidence is ready for independent T3 functional verification. The next
workflow owner is `/verify TASK-001-T3-FT-000-W0`; `/exe` does not run it, does
not run `/red-verify`, and does not promote `TASK-002`.
