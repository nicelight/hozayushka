---
description: Canonical Yandex Weather API boundary and normalized provider obligations for V1.
status: active
last_updated: 2026-08-06
source_of_truth: .memory-bank/prd.md, .memory-bank/architecture/system-architecture.md
---
# Weather Provider Contract

## Boundary

The application has one external weather source: the Yandex Weather API. The
provider is the source of fetched weather/forecast values; Weather Context is
the source of normalized local cache, freshness, history and fallback behavior.
The accepted request shape is the REST forecast endpoint with selected-city
coordinates, `hours=true` for hourly data, and the user's key in the
`X-Yandex-Weather-Key` header, as recorded in the clarified PRD.

### Weather Provider Boundary

- Consumer: Weather Context.
- Provider: Yandex Weather API through the application adapter.
- Request authority: Settings & Location supplies the validated location and
  ephemeral credential; Weather Context decides when the accepted refresh is
  needed.
- Response authority: raw response fields remain external until normalized into
  the application-owned Weather Snapshot, Weather History and Forecast Day
  model described in [Local Data](../domains/local-data.md).

## Required Semantic Data

- Current weather and pressure sufficient for the current card and local trend.
- Daily forecast data sufficient for the selected-city ten-day view and its
  day/night selection.
- Hourly values sufficient for the accepted 06:00–03:00 eight-slot view when
  `hours=true` is available.
- Selected-city API timezone for weather dates, day boundaries, day/night
  selection and hourly labels.
- Optional moon-phase data is consumed only when present; absent optional fields
  use the accepted neutral fallback.

The exact provider field-to-domain mapping and redacted fixture shape are
feature-level contract work for FT-002–FT-004 and FT-008. That work may refine
serialization details but may not change the source, horizon, timezone split,
freshness rule or missing-data behavior below.

### FT-002 Current and Daily Mapping

- A successful FT-002 fixture MUST normalize selected-city timezone, current
  temperature/pressure, daily dates, day/night temperature and condition, and
  optional moon phase into the Weather Context model before persistence.
- Missing optional condition or moon phase MUST produce the neutral cloud and
  regular-moon fallbacks respectively; no textual condition is synthesized.
- A missing required current pressure or daily day/date record MUST keep the
  affected projection unavailable and MUST NOT replace the last successful
  normalized cache with a partial result.
- The fixture and mapping proof MUST be redacted and deterministic; it MUST NOT
  contain a user API key.

### FT-003 Hourly Mapping

- A successful hourly fixture MUST normalize the selected-city API timezone and
  the eight accepted slots `06:00`, `09:00`, `12:00`, `15:00`, `18:00`, `21:00`,
  `00:00`, and `03:00`; the last two belong to the following city-local day.
- Each accepted slot MUST contain the required time, temperature and weather
  illustration inputs needed by the shared forecast card presentation. Pressure
  arrows and calendar dates are not part of the hourly card projection.
- Missing or incomplete required hourly data MUST produce unavailable hourly
  data and MUST NOT synthesize a slot, open a session, or replace a successful
  normalized forecast result with a partial result.
- Hourly labels and day boundaries MUST use the selected-city API timezone;
  device timezone is not a source for hourly labels.
- The fixture and mapping proof MUST be deterministic and redacted; it MUST NOT
  contain a user API key.

### FT-004 Long-Term Mapping

- A successful daily fixture MUST normalize the selected-city API timezone and
  exactly ten ordered daily records: today plus the next nine calendar days.
- Each required daily record MUST contain its city-local date, day/night
  temperature and condition illustration inputs needed by the shared forecast
  card presentation. Pressure arrows are not part of the long-term card
  projection.
- Missing or incomplete required daily data MUST produce unavailable long-term
  data and MUST NOT synthesize a day, open a session, or replace a successful
  normalized forecast result with a partial result.
- Daily dates, day boundaries and day/night selection MUST use the selected-city
  API timezone; device timezone is not a source for long-term date composition.
- The fixture and mapping proof MUST be deterministic and redacted; it MUST NOT
  contain a user API key.

## Refresh, Cache and Failure Rules

- Attempt refresh after launch, after a valid city change and every 30 minutes
  when network access is available.
- Persist only successful normalized results in the local Weather Context cache.
- Cache age up to 24 hours remains usable offline; older cache renders the four
  accepted empty contours.
- A network/provider failure never invalidates the clock, timer lifecycle,
  timer cancellation or overdue dismissal.
- Missing optional fields never create invented text or crash. Missing required
  hourly/daily data prevents the corresponding forecast session.
- No backend, proxy, second weather provider or shared key is permitted in V1.

## Credential and Evidence Rules

The provider adapter receives the user key only for the request path described
above. It must not put the key in a URL, source literal, APK resource, logs,
crash output, screenshots, fixtures or verification evidence. See [Local Secret
Handling](local-secret-handling.md).

## Verification Route

Use redacted provider fixtures for successful current/daily/hourly responses,
missing optional fields, incomplete required fields, stale cache and provider
failure. Live credentials are not part of a test or evidence path. The concrete
fixture and mapping checks are owned by [Runtime Verification](../testing/runtime-verification.md)
and later feature task cards.

## Sources

- `PRD-FR-013`–`PRD-FR-022`, `PRD-FR-033`–`PRD-FR-039` and `PRD-AC-007`–`PRD-AC-010` in [PRD](../prd.md).
- [Constitution](../constitution.md), Product Non-negotiables.
