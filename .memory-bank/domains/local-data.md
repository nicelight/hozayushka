---
description: Canonical V1 local domain, write ownership and persistence invariants.
status: active
last_updated: 2026-08-10
source_of_truth: .memory-bank/prd.md, .memory-bank/invariants.md, operator confirmation 2026-08-04, 2026-08-06 and 2026-08-10
---
# Local Data

## Scope and Source of Truth

The application owns normalized product data and all user-provided settings.
Remote Open-Meteo/OpenWeather responses remain external input; Android OS
signals remain platform input. No capability may treat an adapter response or
another capability's private storage as its own source of truth.

## Ownership Matrix

| Data subject | Owning capability | Mutable authority | Consumers |
|---|---|---|---|
| User Settings, selected provider, selected country/city, coordinates, optional validated OpenWeather key, alert sound/volume and glass intensity | Settings & Location | Settings & Location | Main Display, Weather Context, Timer & Alert |
| Offline GeoNames catalog and aliases | Bundled Location Catalog | Immutable packaged data | Settings & Location |
| Weather Snapshot, freshness and normalized current/forecast data | Weather Context | Weather Context | Main Display, Forecast Sessions |
| Weather History and pressure-trend inputs | Weather Context | Weather Context | Main Display |
| Configured preset duration projection | Settings & Location | Settings & Location | Timer & Alert, Main Display |
| Active Timer and `idle|countdown|overdue` transitions | Timer & Alert | Timer & Alert | Main Display, Android runtime adapter |
| Forecast session state and exit timer | Forecast Sessions | Forecast Sessions | Main Display |
| Display composition state | Main Display | Main Display | Android runtime adapter |

Shared physical storage, if selected by the Foundation implementation, does
not create shared business ownership. Every mutable invariant keeps the owner
shown above; a reader cannot issue the owner's commands or duplicate its rules.

## Durable Data Rules

- Settings auto-save only valid values. Invalid values remain unsaved with the
  owning inline error and preserve the previous valid value.
- Alert sound is one of the accepted built-in values, app alert volume is a
  validated integer in `0…100` percent with default `70` (`0` suppresses only
  app-alert sound), and glass intensity remains in `0…1` with default `0.45`;
  all valid changes auto-save and are exposed to Main Display and other
  consumers only as a validated Settings presentation projection. Visual
  overdue state is independent of the volume value.
- Active timer recovery is based on persisted start/duration information and
  recalculates the product state after a temporary process/lifecycle stop.
  Reboot recovery is not stored as a requirement.
- Weather cache records the provider/location identity, last successful
  normalized update and its age. Weather history records the same identity,
  starts at installation and retains the accepted seven-day window;
  pre-install history is never inferred.
- Forecast dates and hourly slots carry the selected-city API timezone. Main
  clock/date values carry the device timezone.
- Location records preserve country-first selection, scoped city search,
  coordinates and Russian/canonical/ASCII aliases needed by the accepted
  offline flow.
- Provider selection defaults to Open-Meteo. The OpenWeather key is a separate
  local secret applicable only to explicit OpenWeather selection and follows
  [Local Secret Handling](../contracts/local-secret-handling.md).

### FT-002 Weather Context Records

The Weather Context owner keys normalized cache/history by provider identity
plus selected location identity and exposes only the partition matching the
current selection. A failed request cannot mutate that partition with partial
or differently identified data. Provider-less or Yandex-identified legacy
records do not match either target provider and are never relabelled.

The owner exposes a display-ready projection composed of four
ordered card records (`yesterday`, `today`, `tomorrow`, `day_after`) and a
freshness state. Each filled record carries the selected-city date, resolved
day/night temperature and condition illustration input; current and yesterday
records may also carry a pressure-arrow count. The persisted successful cache
also carries its provider identity, location identity, normalized update
timestamp and selected-city timezone.

History records carry installation-relative timestamps, pressure, provider and
selected-city identity. Retention removes entries older than seven days. Trend
evaluation filters the exact provider/location identity and uses the accepted
3-hour comparison, the 12-hour fallback when the 3-hour delta is zero, and the
0/1/2-arrow thresholds; absent comparison data yields zero arrows. No consumer
writes these records directly.

### FT-003 Hourly Forecast Records

Weather Context exposes an ordered complete selected-provider hourly projection
for Forecast Sessions. The projection contains exactly eight
selected-city-timezone slots:
06:00, 09:00, 12:00, 15:00, 18:00, 21:00, 00:00 and 03:00, with the final two
slots on the following city-local day. Each slot carries only the normalized
time, temperature and illustration inputs required by the shared forecast-card
rules; pressure arrows and device-time dates are excluded.

Forecast Sessions owns only the transient session projection and exit timer. It
does not write or mutate the Weather Context forecast data. An incomplete
projection is unavailable rather than a partial, interpolated, historical or
cross-provider sequence. A partial response cannot replace a matching complete
hourly subset.

### FT-004 Long-Term Forecast Records

Weather Context exposes one ordered ten-position daily projection for Forecast
Sessions. Open-Meteo completeness requires 10 selected-city-timezone records
from today through the following nine calendar days and fills every position.
OpenWeather completeness requires its supported 8 ordered records and fills the
first eight positions; positions nine and ten carry their expected city-local
dates plus explicit unavailable state, without temperature or illustration.
Each available position carries the normalized day/night temperature and
illustration inputs required by the shared forecast-card rules; pressure arrows
and device-time dates are excluded.

Forecast Sessions consumes this read model and owns only its transient
ten-position session and exit timer. Fewer than 10 Open-Meteo or 8 OpenWeather
records makes the projection unavailable. The two expected OpenWeather empty
positions do not. No position is synthesized or borrowed from the other
provider, and no consumer writes Weather Context forecast data directly.

## Validation and Serialization Boundaries

Each owner validates data before exposing it through a public contract. Missing
optional weather fields become explicit neutral fallbacks; missing required
forecast fields produce unavailable data rather than invented records. Weather
Context owns product-semantic normalization and unit/timezone conversion;
adapters decode only their provider schema. Concrete storage schema and
serialization types are feature detail and must preserve this ownership model.

## Retention and Cleanup

- Keep only the accepted weather-cache freshness window and seven-day local
  history needed by the product.
- Do not add cloud synchronization, accounts, pre-install history or a second
  persistence owner.
- Foundation establishes a safe reset/fixture path for verification without
  changing product retention rules.

## Sources

- [PRD Data / Domain Model](../prd.md), `PRD-FR-013`–`PRD-FR-018`, `PRD-FR-032`–`PRD-FR-039`.
- [Invariants](../invariants.md).
- [Boundary Map](../contracts/boundary-map.md).
