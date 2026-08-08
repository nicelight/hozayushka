---
description: Claim-linked RED/GREEN execution evidence for TASK-004-T3-FT-002-W3.
status: active
---
# RED/GREEN Baseline — TASK-004-T3-FT-002-W3

## Attempt 1

- receipt_status: supporting-only after the fresh Reviewer FAIL; attempt 2 is
  the current retry basis.

The following honest RED observations were recorded in `context.md` before
production changes. They describe the Foundation baseline, not an artificial
test failure.

| Claim | RED before change | GREEN after change | Evidence |
|---|---|---|---|
| FT-002-AC-001 / REQ-005 | Four placeholder Views had no order/projection or Today size. | `WeatherContextTest.projectionKeepsAcceptedOrderSizingAndCardFields`: four fixed slots and Today-only size variant. | `WeatherContextTest.kt` |
| FT-002-AC-002 / REQ-005, REQ-022 | Snapshot had no normalized daily/date/day-night/moon data. | Redacted provider DTO normalization uses selected-city zone, day/night selection and regular-moon fallback. | `WeatherContextTest.kt` |
| FT-002-AC-003 / REQ-006, REQ-023 | No palette/material source existed. | Explicit 78-entry table, endpoint clamp, sign projection and static pseudo-glass pass host assertions. | `WeatherPresentation.kt`, `WeatherContextTest.kt` |
| FT-002-AC-004 / REQ-007, REQ-025 | Timeless single snapshot had no refresh interval, age or stale contour state. | Launch/scheduled trigger, successful-only cache, 24-hour freshness and four empty contours are asserted; incomplete response preserves prior cache. | `WeatherContextTest.kt` |
| FT-002-AC-005 / REQ-008 | No history, retention or pressure-trend owner existed. | Weather Context stores installation-relative history, prunes to seven days and applies 3-hour/12-hour rules. | `WeatherContextTest.kt` |
| FT-002-AC-006 / REQ-026 | Provider condition was passed through without neutral fallback. | Unknown/missing optional values normalize to neutral cloud/regular moon without invented text. | `WeatherContextTest.kt` |
| FT-002-AC-007 / REQ-024 | RED not applicable: meaningful RED would require a forbidden real/user-like key. | Accepted alternative: synthetic request only plus redacted scans with no credential-like literal. | `secret-scan.md` |

GREEN is executor evidence only; independent `/verify` remains due.
