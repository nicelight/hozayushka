---
description: Deferred target-device evidence for TASK-013-T3-FT-003-W5.
status: active
---
# Target-device evidence — TASK-013-T3-FT-003-W5

- command: `adb devices`
- result: only `List of devices attached`; no authorized device or emulator.
- status: `DEFERRED` and non-blocking under the runtime-verification contract.
- no runtime `PASS` is claimed.
- deferred observation: on the accepted Android target, start from a closed
  session with the synthetic complete fixture, open Today, observe the 2×4
  hourly surface, single-tap hint/cancellation, double-tap close,
  hold-beyond-3-second and release-close, then repeat with unavailable data to
  observe Main Display and the exact Russian fallback.
- residual risk: custom-ROM gesture dispatch/timing, Android rendering and
  1280×720 readability were not observed on a target device.
