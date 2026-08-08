---
description: Fresh verifier-owned outcome probes for TASK-009-T3-FT-007-W8.
status: supporting
---
# Verifier-owned probes — TASK-009-T3-FT-007-W8

## Run

- Reviewer session: `2026-08-08T07:33:17+05:00`.
- Worktree was already broadly dirty; no execute receipt was reused.
- No production or test source was changed by this verification session.

## Fresh gate observations

Commands were run from `/home/serg/Projects/Mobile_APPS/hozayushka`:

- `./gradlew clean assembleDebug` — exit `0`, `BUILD SUCCESSFUL`.
- `./gradlew testDebugUnitTest --tests com.hozayushka.app.OverdueAlertTest` — exit `0`, `BUILD SUCCESSFUL`.
- `./gradlew testDebugUnitTest` — exit `0`, `BUILD SUCCESSFUL`.
- `node scripts/mb-lint.mjs` — exit `0`, `mb-lint passed (77 files)`.
- `git diff --check -- app/src/main/kotlin/com/hozayushka/app/timer ...` — exit `0`.
- Secret scan over FT-007 source/tests/evidence — no credential-shaped match.
- `adb devices -l` — exit `0`, no attached target; target-only evidence is `DEFERRED`.

## Claim-level independent inspection

- AC-001/REQ-015: current source exposes active preset colour, fullscreen overlay,
  blinking plus and stable elapsed text; targeted host tests pass. Fullscreen and
  readability remain target-only residual risk.
- AC-002/REQ-015: `TimerCapability.snapshotAt()` derives full elapsed and the
  overlay is retained while state is `OVERDUE`; host test covers tap boundary.
- AC-003/REQ-015: `TimerCapability.handleGesture()` dismisses both gesture
  forms through the overdue branch and stops audio; targeted host tests pass.
- AC-004/REQ-016: policy inspection and host tests cover all three signal IDs,
  classic default, 5-second ramp endpoints, 5-second repeat and 30-minute cap.
  Actual device ramp remains deferred.
- AC-005/REQ-016: synthetic silent/DND/route cases preserve `visualOverdue`
  and remain dismissible; host tests pass. Custom-ROM behavior remains deferred.

## Supported temporary-resume probe result

The accepted same-runtime path is not satisfied by the available implementation:

1. `PlatformRuntimeAdapter.onActivityPaused()` releases `toneGenerator` and
   clears the platform audio instance (`PlatformRuntimeAdapter.kt:107-110`).
2. `FoundationRuntime.onActivityResumed()` calls `timer.rehydrateAt()` but does
   not reset or re-request Timer & Alert audio (`FoundationRuntime.kt:96-110`).
3. `TimerCapability` retains `lastAlertRequestAtMillis`; `advanceAt()` suppresses
   a new request until `REPEAT_INTERVAL_MILLIS` elapses (`TimerCapability.kt:130,
   254-275`).

Therefore, when an overdue alert was requested, the Activity pauses, and resumes
before five seconds have elapsed, the platform tone has been released while the
next display refresh does not request it again. This contradicts the direct
lifecycle/capability contract requiring a resumed overdue timer to re-establish
the same alert-policy path. The executor resume test creates a new
`TimerCapability` and does not exercise this same-instance pause/release path.

## Scope / boundary inspection

- Current task source/test additions are within the advisory timer, display,
  settings, platform, app wiring, resources and test roots.
- No new audio permission, event/message boundary, private cross-owner storage
  access, composition-root business workflow, reboot recovery or live secret was
  observed.
- Registered edges remain the inspected Main Display → Timer & Alert, Timer &
  Alert → Settings & Location, Timer & Alert → Android Runtime Adapter and Main
  Display → Android Runtime Adapter paths.

## Fresh retry verifier-owned observations — attempt 2 re-verification

- Reviewer run: `2026-08-08T07:49:03+05:00`.
- No production or test source was changed by this verification session.
- Attempt-1 RED lineage remains the supported same-runtime pause/release defect
  documented above; the current source was inspected after the bounded retry
  correction before assigning the new verdict.

### Current outcome probes

- `./gradlew testDebugUnitTest --tests com.hozayushka.app.OverdueAlertTest` —
  exit `0`; the generated XML recorded all five FT-007 tests with
  `failures="0"` and `errors="0"`:
  `overdueProjectionUsesActivePresetColorBlinkSplitAndFullElapsedCounter`,
  `selectedAndDefaultSignalsRampRepeatUntilDismissalAndStopAtAudioCap`,
  `silentDndAndUnavailableRouteSuppressAudioOnlyAndAnyTapStopsAlert`,
  `persistedOverdueTimerReestablishesVisualAndPermittedAlertPathAfterResume`,
  and `sameRuntimeTemporaryResumeReRequestsReleasedAlertBeforeNormalRepeatInterval`.
- The same-instance method was then run independently with
  `./gradlew testDebugUnitTest --tests
  com.hozayushka.app.OverdueAlertTest.sameRuntimeTemporaryResumeReRequestsReleasedAlertBeforeNormalRepeatInterval`;
  exit `0`. Its assertions observe one initial request, platform release on
  pause, immediate re-request after resume at `+1 ms`, active audio again, and
  no second request before the normal five-second repeat interval.
- The policy test observes all three built-in signals, Classic default,
  10%-to-100% ramp, repeat at the five-second boundary, no request before the
  boundary, and audio stop at the 30-minute cap while `visualOverdue` remains
  true.
- The suppression test observes `silent`, `dnd`, and unavailable `route` as
  audio-only denials with `visualOverdue=true`; double-tap reaches `IDLE` and
  stops audio in every case. The projection test covers active-preset color,
  blinking-plus/stable counter, full elapsed value, and single-tap dismissal.

### Current source/contract comparison

- `TimerCapability.rehydrateAt()` clears only the in-memory request cadence for
  a persisted `OVERDUE` state and reuses `advanceAt()`; the cap terminal flag is
  retained. `FoundationRuntime` and `MainActivity` still use the accepted
  lifecycle path.
- Current source inspection found no new graph edge, private-store bypass,
  composition-root business orchestration, permission, event boundary,
  reboot recovery, or secret-bearing literal in the FT-007 change surface.
- `./gradlew clean assembleDebug`, `./gradlew testDebugUnitTest`,
  `node scripts/mb-lint.mjs`, scoped `git diff --check`, and the refined
  boundary/secret scan all exited `0`.
- `adb devices -l` returned no target. Fullscreen/readability, actual ramp and
  custom-ROM audio-policy observations remain `DEFERRED`/non-blocking; no
  runtime `PASS` is claimed.
