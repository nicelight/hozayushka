---
description: Canonical V1 local domain, write ownership and persistence invariants.
status: active
last_updated: 2026-08-06
source_of_truth: .memory-bank/prd.md, .memory-bank/invariants.md, operator confirmation 2026-08-04 and 2026-08-06
---
# Local Data

## Scope and Source of Truth

The application owns normalized product data and all user-provided settings.
Remote Yandex responses remain external input; Android OS signals remain
platform input. No capability may treat another capability's private storage as
its own source of truth.

## Ownership Matrix

| Data subject | Owning capability | Mutable authority | Consumers |
|---|---|---|---|
| User Settings, selected country/city, coordinates, validated API key, alert sound/volume and glass intensity | Settings & Location | Settings & Location | Main Display, Weather Context, Timer & Alert |
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
- Weather cache records the last successful normalized update and its age.
  Weather history starts at installation and retains the accepted seven-day
  window; pre-install history is never inferred.
- Forecast dates and hourly slots carry the selected-city API timezone. Main
  clock/date values carry the device timezone.
- Location records preserve country-first selection, scoped city search,
  coordinates and Russian/canonical/ASCII aliases needed by the accepted
  offline flow.
- The API key remains local and follows [Local Secret Handling](../contracts/local-secret-handling.md).

### FT-002 Weather Context Records

The Weather Context owner exposes a display-ready projection composed of four
ordered card records (`yesterday`, `today`, `tomorrow`, `day_after`) and a
freshness state. Each filled record carries the selected-city date, resolved
day/night temperature and condition illustration input; current and yesterday
records may also carry a pressure-arrow count. The persisted successful cache
also carries its normalized update timestamp and selected-city timezone.

History records carry installation-relative timestamps, pressure and selected
city identity. Retention removes entries older than seven days. Trend evaluation
uses the accepted 3-hour comparison, the 12-hour fallback when the 3-hour delta
is zero, and the 0/1/2-arrow thresholds; absent comparison data yields zero
arrows. No consumer writes these records directly.

### FT-003 Hourly Forecast Records

Weather Context exposes an ordered complete hourly projection for Forecast
Sessions. The projection contains exactly eight selected-city-timezone slots:
06:00, 09:00, 12:00, 15:00, 18:00, 21:00, 00:00 and 03:00, with the final two
slots on the following city-local day. Each slot carries only the normalized
time, temperature and illustration inputs required by the shared forecast-card
rules; pressure arrows and device-time dates are excluded.

Forecast Sessions owns only the transient session projection and exit timer. It
does not write or mutate the Weather Context forecast data. An incomplete
projection is unavailable rather than a partial or invented sequence.

### FT-004 Long-Term Forecast Records

Weather Context exposes an ordered complete daily projection for Forecast
Sessions. The projection contains exactly ten selected-city-timezone records:
today and the following nine calendar days. Each record carries the normalized
city-local date, day/night temperature and illustration inputs required by the
shared forecast-card rules; pressure arrows and device-time dates are excluded.

Forecast Sessions consumes this read model and owns only its transient ten-card
session and exit timer. An incomplete projection is unavailable rather than a
partial or invented sequence, and no consumer writes Weather Context forecast
data directly.

## Validation and Serialization Boundaries

Each owner validates data before exposing it through a public contract. Missing
optional weather fields become explicit neutral fallbacks; missing required
forecast fields produce unavailable data rather than invented records. Concrete
provider field mappings, storage schema and serialization types are
feature/foundation detail and must preserve this ownership model.

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
