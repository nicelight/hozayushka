---
description: Claim-equivalent host GREEN evidence for TASK-007-T3-FT-005-W6.
status: final
---
# Claim-linked GREEN fixture — TASK-007-T3-FT-005-W6

## Attempt and isolation

- attempt: 1
- environment: JVM host unit tests, synthetic values only
- isolation: every test creates a fresh `InMemorySettingsStateStore` and
  `InMemoryTimerStateStore`; no live Android storage, provider, credential or
  network is used. The test-owned stores are discarded after each test.

## Claim results

- `FT-005-AC-001 / REQ-011` — GREEN: exactly three ordered preset definitions
  exist; changing slot 2 persists after a new Settings capability reload and
  leaves slots 1 and 3 at their independent values. Timer configuration while
  slot 1 is active leaves one countdown/active slot; selecting slot 3 replaces
  the single Timer & Alert record rather than creating parallel state.
- `FT-005-AC-002 / REQ-011` — GREEN: `99:59:59`, hours `100`, minutes `60`,
  seconds `60` and zero total are exercised. Valid values save; every invalid
  result reports its owning validation error and preserves the last valid
  persisted duration.
- `FT-005-AC-003 / REQ-011` — GREEN: defaults are `3 м`, `10 м`, `30 м`; mixed
  values expose only the highest non-zero unit with floor behavior (`1 ч`,
  `59 м`, `59 с`).
- `FT-005-AC-004 / REQ-011` — GREEN: ordered outline tokens are orange
  `#FF7A00`, pink `#FF4FA3` and purple `#A855F7`; Timer presentation exposes
  selected/active state through the Settings-backed Timer read path.

## Artifact

`app/src/test/kotlin/com/hozayushka/app/TimerPresetTest.kt` contains the four
claim-equivalent tests. Final suite result is recorded in `host-gates.md`.
