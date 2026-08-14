---
description: Canonical selectable weather-provider boundary, endpoints and normalization obligations for V1.
status: active
last_updated: 2026-08-10
source_of_truth: .memory-bank/prd.md, .memory-bank/architecture/system-architecture.md, operator decision 2026-08-10
---
# Weather Provider Contract

## Provider-Neutral Boundary

The accepted target has exactly two external weather adapters behind one
provider-neutral application boundary: Open-Meteo and OpenWeather. Weather
Context is the sole owner of provider dispatch, product-semantic normalization,
cache/history, freshness and provider-capability projection. An adapter owns
only HTTPS transport and provider-schema decoding; it never persists or exposes
application-owned normalized state.

### Weather Provider Boundary

- Consumer: Weather Context.
- Providers: Open-Meteo Forecast API through the Open-Meteo Weather Adapter and
  OpenWeather One Call 3.0 through the OpenWeather Weather Adapter.
- Request authority: Settings & Location owns the persisted provider selection,
  selected location and local OpenWeather key. Weather Context decides when a
  refresh is due and dispatches exactly one request to the selected adapter.
- Response authority: raw/transport DTOs remain external input until Weather
  Context validates them and creates provider-identified Weather Snapshot,
  Weather History and forecast records described in
  [Local Data](../domains/local-data.md).
- Forbidden behavior: neither adapter may call the other adapter, select a
  provider, read or write cache/history, or combine provider responses.

## Provider Selection and Dispatch

- Persisted selection is `open_meteo|open_weather`; absence of a recognized
  value resolves to `open_meteo`.
- Open-Meteo is the default and requires no credential. A missing OpenWeather
  key must not block the Open-Meteo request or any default Settings path.
- OpenWeather is used only after explicit owner selection and requires that
  owner's local key plus an OpenWeather account and active One Call 3.0
  `One Call by Call` subscription.
- Launch, valid city change, valid provider change and the 30-minute cadence
  ask Weather Context to refresh. Each trigger resolves the current selection
  once and invokes only its matching adapter; there is no second attempt against
  the other provider.
- Provider capabilities are fixed contract data below, not runtime discovery or
  a plugin registry.

### Open-Meteo Endpoint

- Adapter: Open-Meteo Weather Adapter.
- Request: HTTPS `GET https://api.open-meteo.com/v1/forecast`.
- Required request context: selected latitude/longitude, `timezone=auto`,
  `forecast_days=10`, and only the current/hourly/daily variables needed for
  current temperature and surface pressure, weather code, hourly temperature
  and weather code, daily weather code, daily maximum/minimum temperature and
  sunrise/sunset mapping.
- Credential rule: the accepted personal non-commercial Free API path sends no
  API key, `apikey`, `appid`, authorization header or other credential.
- Response timezone: the returned IANA `timezone` and city-local timestamps are
  mandatory normalization inputs.

### OpenWeather One Call 3.0 Endpoint

- Adapter: OpenWeather Weather Adapter.
- Request: HTTPS `GET https://api.openweathermap.org/data/3.0/onecall`.
- Required request context: selected `lat`, `lon`, `units=metric`, current,
  hourly and daily sections, and required query parameter `appid=<owner key>`;
  unused minutely/alerts sections may be excluded without adding an endpoint.
- Access prerequisite: the owner supplies a local OpenWeather key from an
  account with an active One Call 3.0 `One Call by Call` subscription. The app
  does not create or manage that account/subscription.
- Response capability: current weather, up to 48 hours of future hourly data
  and eight daily records. Returned `timezone`/`timezone_offset` are mandatory
  city-time inputs; a valid IANA timezone name is preferred for calendar
  boundaries.
- Credential rule: because the official API requires `appid` in the query, the
  raw key may occur in a URL only transiently while constructing/sending this
  explicit outbound HTTPS request. The URL and key must be redacted before any
  diagnostic, error, persistence or evidence boundary.

No geocoding, history, time-machine, day-summary, proxy or fallback endpoint is
part of the target weather boundary. Offline location remains the bundled
GeoNames boundary.

## Provider Capability Matrix

| Capability | Open-Meteo | OpenWeather |
|---|---|---|
| Selection | Default | Explicit owner selection |
| Credential | None for accepted Free API use | Local owner key in `appid` query parameter |
| Account/subscription | None for accepted Free API use | OpenWeather account plus active One Call 3.0 subscription |
| Current weather/pressure | Required | Required |
| Hourly input | City-local hourly series including every fixed slot when available | Future 48-hour hourly series; elapsed fixed slots may be absent |
| Long-term supported set | 10 ordered daily records | 8 ordered daily records |
| Ten-position projection | 10 available positions | 8 available plus positions 9–10 explicitly unavailable |
| Moon phase | Optional/absent; regular-moon fallback | Optional provider value |

The matrix is selection metadata owned by the application contract. It does
not authorize probing the non-selected provider or filling a capability gap
from another provider.

## Provider-Neutral Response Contract

Each adapter returns a typed envelope that identifies its provider and contains
only decoded current, daily, hourly and timezone values plus a bounded failure.
Weather Context must reject an envelope whose provider identity does not match
the selected request. The envelope is not a cache model and grants no adapter
write authority.

Weather Context alone:

- converts provider values into canonical temperature, pressure, condition,
  city-local date/time and availability semantics;
- creates the selected provider/location cache identity;
- evaluates current-card, strict hourly and provider-supported daily
  completeness independently, so an incomplete hourly subset cannot be
  synthesized and does not require discarding otherwise valid current/daily
  data;
- atomically replaces only a successfully normalized data subset; a partial
  hourly or daily sequence never replaces the matching last valid complete
  subset; and
- exposes only selected-provider read models to Main Display and Forecast
  Sessions.

## Mapping and Timezone Obligations

- Provider identity comes from the selected adapter/request, never from display
  text or an untrusted response field.
- Open-Meteo `surface_pressure` and OpenWeather `pressure` are hPa inputs.
  Weather Context converts them to the canonical mmHg value with one
  deterministic conversion (`hPa × 0.75006157584566`) before history/trend
  comparison. Their provider-specific pressure basis remains provenance; trend
  comparisons never cross provider identity.
- Open-Meteo WMO weather codes and OpenWeather weather condition IDs map into
  the same application condition vocabulary. An unknown code maps to neutral
  cloud without invented text or a crash.
- Open-Meteo daily maximum/minimum temperature map to day/night temperature;
  its daily weather code supplies the condition input for both presentation
  phases when no separate condition exists. OpenWeather `daily.temp.day` and
  `daily.temp.night` map to day/night temperature; its daily weather condition
  supplies the shared condition input. Missing optional moon phase uses the
  regular-moon fallback.
- Weather Context converts timestamps in the selected provider's returned
  city timezone. Device timezone remains reserved for the main clock/date.
- Hourly completeness requires exact city-local slots `06:00`, `09:00`,
  `12:00`, `15:00`, `18:00`, `21:00`, `00:00` and `03:00`, with the last two on
  the following city-local day. No nearest-hour selection, interpolation,
  synthetic slot or cross-provider borrowing is allowed. If any slot is not
  available from the selected provider's matching valid data, the existing
  hourly-unavailable path applies.
- Daily completeness is 10 ordered records for Open-Meteo and 8 ordered records
  for OpenWeather, starting at selected-city today. Weather Context emits one
  ten-position projection; OpenWeather positions 9 and 10 are explicit
  unavailable values, not missing required provider records.

## Cache, History and Refresh Rules

- Cache and history identity is the tuple of provider identity and selected
  location identity. A record with a different provider or location is never
  eligible for display, trend comparison or forecast completion.
- Every retained record carries the identity tuple; storage layout and count
  remain an owner-local implementation detail. Weather Context exposes and
  updates only the current selection. This identity filtering is not fallback.
- A matching successful normalized cache remains usable through 24 hours;
  older data follows the four-card stale/empty path. History retains the
  accepted seven-day window and trend queries filter the same provider/location
  identity.
- Refresh is attempted after launch, valid city change, valid provider change
  and every 30 minutes when network is available. A scheduled freshness check
  uses the selected identity's last successful update.
- A provider/access/network/malformed-core failure preserves the matching last
  valid cache and history unchanged. It never changes selection, invokes the
  other adapter or substitutes another provider's state.

## Failure Rules

- OpenWeather missing/invalid key or inactive subscription/access maps to an
  OpenWeather-identified access failure and the accepted owning inline path.
  It does not invoke Open-Meteo.
- Open-Meteo has no missing-key failure. Its network/provider errors identify
  Open-Meteo and do not surface or require a key UI.
- Missing optional fields use the accepted fallback. Missing required current
  data leaves that subset unchanged/unavailable; incomplete required hourly or
  daily data follows its existing unavailable path.
- Provider failure never invalidates the clock, timer lifecycle, timer
  cancellation or overdue dismissal.

## Credential and Evidence Rules

The owner key follows [Local Secret Handling](local-secret-handling.md). It may
be read only for an explicit selected OpenWeather request and may occur in that
outbound HTTPS query construction because `appid` is mandatory. It must be
absent or redacted in source literals, persisted non-secret weather/settings
data, logs, crash output, captured URLs, screenshots, fixtures, APK resources
and verification evidence. Open-Meteo fixtures and requests contain no
credential field.

## Attribution and Terms Boundary

- The Open-Meteo path is accepted only for this personal non-commercial app
  under the Free API limits and no-warranty boundary documented by the
  [official terms](https://open-meteo.com/en/terms).
- Settings must display credit to Open-Meteo with a link to
  `https://open-meteo.com/` and a visible/linkable `CC BY 4.0` licence reference
  alongside the existing GeoNames attribution before the final back action.
- A future commercial/distributed use or different terms is outside this
  contract and requires a new accepted product/security decision; it must not
  silently add a credential to the default endpoint.

## Current-State Migration Evidence

Production code currently wires a credential-required Yandex adapter and stores
provider-less normalized cache/history. That is brownfield migration evidence,
not target authority. The target must not register Yandex as a third adapter,
relabel legacy Yandex/provider-less cache or history as Open-Meteo/OpenWeather,
or repurpose the legacy Yandex key as an OpenWeather key. Unrecognized legacy
selection resolves to default Open-Meteo; cleanup of inert legacy owner-local
data remains a bounded implementation/migration detail under these rules.

## Verification Route

Use separate deterministic Open-Meteo no-key and OpenWeather synthetic-key
fixtures. Prove endpoint/request shape, provider identity, timezone and unit
mapping, unknown/optional fields, 10-versus-8+2 daily projection, every
one-missing-hour position, cache/history isolation, provider-change refresh and
failure without a second adapter call. Request-shape evidence must redact the
synthetic `appid` value and never use a live owner key. Concrete evidence routes
are owned by [Runtime Verification](../testing/runtime-verification.md) and
later feature task cards.

## Sources

- `PRD-FR-013`–`PRD-FR-022`, `PRD-FR-032`–`PRD-FR-039` and
  `PRD-AC-002`, `PRD-AC-006`–`PRD-AC-010` in [PRD](../prd.md).
- [Constitution](../constitution.md), Product Non-negotiables.
- [Open-Meteo Forecast API](https://open-meteo.com/en/docs),
  [Open-Meteo Terms](https://open-meteo.com/en/terms) and
  [OpenWeather One Call 3.0](https://openweathermap.org/api/one-call-3).
