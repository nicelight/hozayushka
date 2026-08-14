# Fresh RED baseline — attempt 1

## Claim

`FT-006-AC-001 / REQ-012` and material visual NFR `REQ-023`: active countdown
must be a dedicated no-weather/no-city/no-date/no-card surface with a stronger
countdown hierarchy and activating-preset color identity.

## Probe

- Command: `./gradlew --offline --no-daemon :app:testDebugUnitTest --tests com.hozayushka.app.DisplayProjectionTest.reachableMainDisplayRefreshKeepsIdleClock176AndCountdown32`
- Result: exit `0`; existing baseline projection test ran successfully.
- Source probe: read-only Node probe over the current host presentation path,
  executed immediately after the focused baseline test and before any W27
  production/test behavior write.
- Source basis:
  - `DisplayCapability.kt` SHA-256:
    `32b32c1056f1ad590827b747d23b6444c15fa8a2c8ab2370578ba62670d0e934`
  - `DisplayProjectionTest.kt` SHA-256:
    `8bf0487f90084a7d8b95941d6b3a917ad699482b120f2292f91c78fe09882005`

## Observed RED

| Condition | Current observation | W27 comparison |
|---|---|---|
| Dedicated active surface | No active-countdown surface symbol or render branch; countdown is a child of the standard header | FAIL |
| Weather/card exclusion | `renderWeatherCardsIfChanged(now)` runs during every non-overdue refresh, including countdown | FAIL |
| City/date exclusion | `date.text = ...` and `city.text = ...` run during every refresh | FAIL |
| Standard shell exclusion | Countdown branch keeps `mainShell.visibility = View.VISIBLE` | FAIL |
| Countdown hierarchy | countdown `textSize=132`, final idle `textSize=196`; ratio `0.673469387755102` | FAIL |
| Transparent preset-colored circular backdrop | No active countdown backdrop; existing `drawCircle` helpers belong to weather/preset code, not countdown | FAIL |

This is an honest current-state failure, not an artificial failing test: the
existing host test remains green while the source/render projection still
violates every W27 presentation condition above.

## Scope gate

No production or test behavior file was written before this RED observation.
Only required `/exe` protocol/papercut bookkeeping had been initialized.
