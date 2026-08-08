---
description: Claim-linked host evidence for TASK-009 attempt 1.
status: supporting
---
# FT-007 host evidence — attempt 1

## Execution basis

- Task: `TASK-009-T3-FT-007-W8`, attempt `1`.
- CWD: `/home/serg/Projects/Mobile_APPS/hozayushka`.
- State isolation: `InMemoryTimerStateStore`, synthetic millisecond clocks,
  `InMemorySettingsStateStore` and a recording synthetic `PlatformRuntime`;
  every test creates disposable state and does not use credentials or device
  permissions.
- Target device/emulator: unavailable; no runtime/device observation made.

## Claim-equivalent GREEN observations

- `FT-007-AC-001 / REQ-015`: `OverdueAlertTest.overdueProjectionUsesActivePresetColorBlinkSplitAndFullElapsedCounter`
  proves `OVERDUE`, active SECOND preset `#FF4FA3`, the blinking-plus phase
  split (`0 ms` visible, `382 ms` hidden, `764 ms` visible) and the stable
  `00:10:01` full elapsed projection.
- `FT-007-AC-002 / REQ-015`: the same test proves elapsed includes the
  configured 10-minute duration, overdue elapsed is separately projected, the
  visual-overdue decision remains true, and the state changes only after the
  accepted tap dismissal.
- `FT-007-AC-003 / REQ-015`: the same test proves single-tap dismissal;
  `silentDndAndUnavailableRouteSuppressAudioOnlyAndAnyTapStopsAlert` proves
  double-tap dismissal also returns to `IDLE` and invokes audio stop.
- `FT-007-AC-004 / REQ-016`:
  `selectedAndDefaultSignalsRampRepeatUntilDismissalAndStopAtAudioCap` proves
  all three built-in IDs, default `classic`, 10% initial/100% at the selected
  5-second ramp boundary, repeat at the 5-second interval and no request at
  the 30-minute audio cap.
- `FT-007-AC-005 / REQ-016`:
  `silentDndAndUnavailableRouteSuppressAudioOnlyAndAnyTapStopsAlert` runs
  synthetic `silent`, `dnd` and `route` suppression cases. Each retains
  `visualOverdue=true`, reports `permitted=false`, and remains dismissible.
- Temporary resume route:
  `persistedOverdueTimerReestablishesVisualAndPermittedAlertPathAfterResume`
  rehydrates persisted FT-006 timer data and requests the same policy path;
  no reboot recovery is added or tested.

## Required commands and results

- `./gradlew testDebugUnitTest --tests com.hozayushka.app.OverdueAlertTest`
  → exit `0`, targeted FT-007 probes passed.
- `./gradlew clean assembleDebug` → exit `0`, clean Android debug APK built.
- `./gradlew testDebugUnitTest` → exit `0`, full host/unit suite passed.
- `node scripts/mb-lint.mjs` → exit `0`, `mb-lint passed (77 files)`.
- `git diff --check -- <task changed paths>` → exit `0`, no whitespace errors.
- Boundary/secret inspection with `rg` over FT-007 source/tests/evidence →
  exit `0` with no direct storage/composition-root/event/permission bypasses
  and no live-key/token-shaped literals found.

Build artifact: `app/build/outputs/apk/debug/app-debug.apk`.
SHA-256: `853176c2d57471f03ef4cb891a11365209584d7afbc4fd8b15cd6ce57b9b2ac6`.
Unit report: `app/build/reports/tests/testDebugUnitTest/index.html`.

## Boundary and safety comparison

- Timer & Alert owns `TimerAlertPolicy`, overdue elapsed projection, repeat/cap
  decision, alert request and dismissal stop.
- Main Display consumes the public timer projection, composes the fullscreen
  overlay and sends single/double tap commands; it does not read timer storage
  or calculate elapsed time.
- Settings & Location exposes only the validated alert read projection/default
  seam; no FT-009 user-facing controls were added.
- Android Runtime Adapter owns ringer/DND/output-route checks and ToneGenerator
  request/stop behavior; visual state is independent of its result.
- No new graph edge, permission, event boundary, reboot recovery, private-store
  bypass, live credential or secret-bearing evidence was introduced.

## Target evidence

`DEFERRED` / non-blocking: no target device or emulator is available. Residual
risk remains for 1280×720 fullscreen/readability, actual 5-second ramp and
custom-ROM silent/DND/output-route behavior. This artifact makes no runtime
`PASS` claim.
