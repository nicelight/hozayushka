---
description: Host fake scheduler trace for TASK-026-T3-FT-007-W23 AC-004.
status: supporting
---
# AC-004-SCHEDULER-TRACE

## Basis

- Task: `TASK-026-T3-FT-007-W23`, attempt `1`.
- Host-only deterministic `MainDisplayTickerOwner` driver with synthetic
  timestamps, disposable `InMemoryTimerStateStore` and recording fake
  `PlatformRuntime`/audio scheduler.
- No Android emulator/AVD/QEMU/Android Studio virtual device, adb/device, live
  audio, network or credentials used.

## ZERO_TRANSITION

| timer_state | timestamp_ms | event | visual_overdue |
|---|---:|---|---|
| COUNTDOWN | 999 | display tick before duration | false |
| OVERDUE | 1000 | first display tick at duration; AlertAudioRequest emitted | true |

## FIRST_ALERT_REQUEST_START

- `AlertAudioRequest`: `signal=classic`, `volume=70`, `ramp=10`,
  `overdueElapsedMillis=0`.
- Fake platform: `request_count=1`, `start_result=STARTED`,
  `start_calls=1`, `audio_active=true`.
- Comparison: first tick at/after configured duration emitted the selected
  default request and fake-started it. PASS.

## REPEAT_BOUNDARY

- At `timestamp_ms=5999` (`repeat_interval - 1`): `request_count=1`.
- At `timestamp_ms=6000`: second request emitted; `ramp=100`,
  `request_count=2`, fake start recorded.
- Comparison: no early repeat; one request at accepted five-second boundary.
  PASS.

## DISMISSAL_STOP

- Single overdue tap at `timestamp_ms=6000`: timer `OVERDUE -> IDLE`,
  `stop_result=STOPPED`, `audio_active=false`.
- After another repeat interval: `post_dismissal_requests=0`.
- Comparison: dismissal stops audio and prevents later requests while returning
  to the existing idle visual lifecycle. PASS.

## AUDIO_CAP_30M

- Disposable second timer reaches `timestamp_ms=18001000` (duration `1000` plus
  `AUDIO_CAP_MILLIS`). Timer remains `OVERDUE` and `visual_overdue=true`.
- `stop_result=STOPPED`, `audio_active=false`; later tick at cap plus repeat
  interval leaves `request_count` unchanged.
- Comparison: audio stops at 30-minute cap while visual overdue is preserved.
  PASS.

## HOST_FAKE_RESULT

`PASS` — the deterministic scheduler trace proves tick-driven request emission,
fake start, repeat boundary, dismissal stop/no later request and 30-minute
audio cap. It is not physical audibility evidence.
