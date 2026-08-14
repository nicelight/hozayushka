# TASK-027-T3-FT-001-W24 — Attempt 2 fresh RED baseline

## Claim

`FT-001-AC-002 / REQ-002`: the current reachable Main Display refresh path
still violates the idle clock-size claim before the Attempt 2 correction.

## Probe

- Attempt: 2
- Command: fresh source reachability probe over the supported
  `MainActivity.onResume -> MainDisplayTickerOwner -> refresh` path.
- Result: exit `1`; the current idle branch assigns `132f` to hour, colon and
  minute, while the countdown branch assigns `32f`. This is claim-specific
  RED and is not reused from W21/W24 GREEN evidence.
- Environment: host-only deterministic `1280×720`; no emulator, AVD, QEMU,
  Android Studio virtual device, adb, device, network or credentials.

## Reachable refresh finding

- Idle hour/colon/minute after `refresh()`: `132f` (RED; expected
  `layoutSpec.idleClockTextSize = 176f`).
- Countdown clock text size after `refresh()`: `32f` (preserved).
- The attached/resumed ticker is the supported caller of `refresh()`; the
  static geometry model and earlier W24 GREEN do not prove this live branch.

## Measured current source baseline

- Idle `HH:mm` central/upper region: `[271,24,1028,252]`, width `757`, height
  `228`. The current clock text style is `132f`; this region is the W21 model
  bound used by the existing Main Display geometry support.
- Right preset 1: `[1028,24,1248,248]`, width `220`, height `224`, effective
  corner radius `18f`.
- Right preset 2: `[1028,248,1248,472]`, width `220`, height `224`, effective
  corner radius `18f`.
- Right preset 3: `[1028,472,1248,696]`, width `220`, height `224`, effective
  corner radius `18f`.

The controls are not square (`220 != 224`) and radius `18f` is below half of
the measured side (`112`), so the circular-control claim is RED. The baseline
clock region is retained for same-size RED/GREEN comparison; W21/W22/W23
historical evidence is not reused as W24 RED.

## Evidence

- Current production/test scope before Attempt 2 correction:
  `DisplayCapability.kt` and `DisplayProjectionTest.kt` only.
