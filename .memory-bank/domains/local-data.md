---
description: Canonical V1 local domain, write ownership and persistence invariants.
status: active
last_updated: 2026-08-04
source_of_truth: .memory-bank/prd.md, .memory-bank/invariants.md, operator confirmation 2026-08-04
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
| User Settings, selected country/city, coordinates and validated API key | Settings & Location | Settings & Location | Main Display, Weather Context, Timer & Alert |
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
