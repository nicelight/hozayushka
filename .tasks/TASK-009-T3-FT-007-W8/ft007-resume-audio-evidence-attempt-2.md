---
description: Claim-equivalent retry evidence for TASK-009-T3-FT-007-W8 attempt 2.
status: supporting
---
# FT-007 temporary-resume/audio evidence — attempt 2

## Execution basis

- Task: `TASK-009-T3-FT-007-W8`, tier `T3`, retry attempt `2`.
- CWD: `/home/serg/Projects/Mobile_APPS/hozayushka`.
- Repository source basis: `HEAD=a93e46118f0f0b90e311b6174e3f5a8ed7d89fef`;
  the worktree has a broad pre-existing tracked/untracked baseline, so this
  receipt is supporting-only and is not offered as a `/verify` reuse candidate.
- Changed task files in this retry: `app/src/main/kotlin/com/hozayushka/app/timer/TimerCapability.kt`
  and `app/src/test/kotlin/com/hozayushka/app/OverdueAlertTest.kt`.
- Isolation: disposable `InMemoryTimerStateStore`, synthetic timestamps and a
  recording `PlatformRuntime`; the test simulates platform tone release on
  pause and does not use credentials, permissions or a target device.

## Retry correction basis

Fresh independent verification found that the same `TimerCapability` retained
`lastAlertRequestAtMillis` after `PlatformRuntimeAdapter` released its tone on
pause. Resume called `rehydrateAt()` but the next `advanceAt()` was suppressed
until the five-second repeat interval. The correction keeps the existing
`FoundationRuntime -> TimerCapability.rehydrateAt()` path, clears only the
in-memory request cadence for an overdue resumed runtime, and reuses
`advanceAt()`; `audioCapStopIssued`, the repeat interval, cap and platform
suppression decisions are unchanged.

## Fresh claim-equivalent GREEN

`OverdueAlertTest.sameRuntimeTemporaryResumeReRequestsReleasedAlertBeforeNormalRepeatInterval`
passed and proves:

- the same Timer & Alert instance requests the overdue alert once;
- simulated temporary pause releases the active tone;
- resume before five seconds re-requests the alert immediately with the current
  overdue elapsed value;
- the next request remains suppressed until the normal five-second repeat
  interval.

The existing `OverdueAlertTest.selectedAndDefaultSignalsRampRepeatUntilDismissalAndStopAtAudioCap`
also passed, preserving all three signals, Classic default, ramp endpoints,
normal repeat and 30-minute audio cap. Existing suppression tests passed for
silent mode, DND and unavailable route, preserving the visual overdue state
and any-tap dismissal.

## Commands and results

- `./gradlew testDebugUnitTest --tests com.hozayushka.app.OverdueAlertTest` —
  exit `0`; 22 actionable tasks, build successful.
- `./gradlew clean assembleDebug` — exit `0`; clean debug APK produced at
  `app/build/outputs/apk/debug/app-debug.apk`, SHA-256
  `2a6152cfb18773fff48a84b90ce60786488cf6481f415c0e36125c3161090308`.
- `./gradlew testDebugUnitTest` — exit `0`; full host/unit suite successful;
  report at `app/build/reports/tests/testDebugUnitTest/index.html`.
- `node scripts/mb-lint.mjs` — exit `0`; `mb-lint passed (77 files)`.
- `! rg -n '[[:blank:]]+$' app/src/main/kotlin/com/hozayushka/app/timer/TimerCapability.kt app/src/test/kotlin/com/hozayushka/app/OverdueAlertTest.kt .protocols/TASK-009-T3-FT-007-W8/context.md .protocols/TASK-009-T3-FT-007-W8/progress.md` — exit `0`; no trailing whitespace.
- `git diff --check -- app/src/main/kotlin/com/hozayushka/app/timer/TimerCapability.kt` — exit `0`.
- `! rg -n -i 'getSharedPreferences|MainActivity|EventBus|LocalBroadcast|ACCESS_NOTIFICATION_POLICY|RECORD_AUDIO|api[_-]?key|Bearer|token' app/src/main/kotlin/com/hozayushka/app/timer/TimerCapability.kt app/src/test/kotlin/com/hozayushka/app/OverdueAlertTest.kt` — exit `0`; no forbidden bypass or secret-shaped literal in retry files.
- `adb devices -l` — exit `0`, no attached target; device-only fullscreen/readability,
  actual ramp and custom-ROM audio-policy evidence is `DEFERRED`/non-blocking.

## Boundary and residual risk

The correction remains inside Timer & Alert's existing lifecycle/alert owner
and public rehydration seam. No new graph edge, permission, storage bypass,
composition-root business orchestration, reboot recovery, dependency or
product behavior was introduced. Target-device behavior remains unproven and
must not be reported as runtime PASS.
