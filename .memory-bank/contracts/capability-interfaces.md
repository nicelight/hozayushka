---
description: Accepted public in-process contracts between the V1 capability slices.
status: active
last_updated: 2026-08-04
source_of_truth: .memory-bank/architecture/system-architecture.md, .memory-bank/prd.md, operator confirmation 2026-08-04
---
# Capability Interfaces

## Scope

This document defines the narrow interactions that are allowed inside the one
Android runtime. It does not prescribe package classes, UI framework APIs or a
shared persistence layer. A slice may use private implementation details and
private storage behind its own contract; another slice may not bypass that
contract.

## Common Contract Rules

- The provider owns mutable business state; consumers receive read models or
  commands and never write a provider's storage directly.
- Commands return an accepted result or an owning failure state. Invalid
  Settings values do not replace the last valid value.
- Network failure preserves clock, timer, cancellation and overdue dismissal;
  weather consumers receive the accepted cache/freshness state.
- Contracts carry no API-key value into logs, screenshots or verification
  evidence. Secret handling is owned by [Local Secret Handling](local-secret-handling.md).
- No internal event/message envelope is introduced. Interactions are direct
  in-process calls or state projections through these contracts.

### Main Display to Weather Context

- Public surface: read the display-ready four-card weather projection, freshness
  state, selected-city label and forecast availability indicators.
- Provider authority: Weather Context owns provider refresh, normalization,
  cache/history, freshness, pressure trends and unknown-condition fallback.
- Allowed interaction: Main Display may render the projection and request a
  forecast entry through Forecast Sessions; it may not request raw provider
  fields or write weather data.
- Failure/compatibility: no city, missing key, provider failure or stale cache
  keeps the stable shell and returns the accepted empty/inline state.
- Verification: deterministic freshness/palette/trend checks and redacted
  provider fixtures from [Runtime Verification](../testing/runtime-verification.md).

### Main Display to Timer and Alert

- Public surface: start one selected preset, observe `idle|countdown|overdue`
  presentation data, and submit the accepted double-tap cancellation command.
- Provider authority: Timer & Alert owns configured-duration interpretation,
  one-active-timer enforcement, elapsed arithmetic, recovery and overdue state.
- Allowed interaction: a single tap starts a preset or shows the accepted hint;
  a single tap during countdown is never a cancel command. Main Display does
  not calculate remaining time or write timer state.
- Failure/compatibility: timer operation is independent of network; alert audio
  denial cannot remove the visual overdue state.
- Verification: timer transition and gesture probes plus target-ROM lifecycle
  evidence from [Runtime Verification](../testing/runtime-verification.md).

### Main Display to Forecast Sessions

- Public surface: request hourly or long-term session entry from the accepted
  card, observe session state, and send the shared exit gestures.
- Provider authority: Forecast Sessions owns data-completeness gating, the
  auto-close/single-tap-hint/double-tap/hold-release flow and temporary session
  state.
- Allowed interaction: Main Display supplies the user intent and renders the
  returned session projection; it does not read Weather Context storage.
- Failure/compatibility: missing required data leaves the main display visible
  and returns the exact accepted availability message.
- Verification: eight-slot/ten-card fixture checks and shared-session gesture
  checks.

### Main Display to Settings and Location

- Public surface: request the Settings screen from city interaction and read
  the selected-city display projection.
- Provider authority: Settings & Location owns settings, selected country/city,
  coordinates, validation and automatic persistence.
- Allowed interaction: empty-city short tap opens Settings; selected-city short
  tap is a no-op; hold opens Settings. Main Display does not change settings.
- Failure/compatibility: inline owning errors and the last valid value remain
  visible; system Back and the bottom back icon return to Main Display.
- Verification: settings state/validation probes and the accepted device/UI
  evidence route.

### Forecast Sessions to Weather Context

- Public surface: request normalized complete hourly slots or ordered daily
  forecast data for the selected city and its API timezone.
- Provider authority: Weather Context owns forecast acquisition and storage;
  Forecast Sessions owns only the transient display session.
- Allowed interaction: Forecast Sessions may reject incomplete data and render
  only the accepted eight or ten cards. It may not invent missing slots or
  mutate the forecast cache.
- Failure/compatibility: missing hourly/long-term data returns the accepted
  message and no session is created.
- Verification: deterministic timezone, field-completeness and sequence checks.

### Timer and Alert to Settings and Location

- Public surface: read the three configured durations, selected built-in sound
  and app alert volume as a validated settings projection.
- Provider authority: Settings & Location owns persistence and validation;
  Timer & Alert owns runtime interpretation and active state.
- Allowed interaction: Timer & Alert reads the projection at start and at the
  accepted settings-change boundary; it never writes settings directly.
- Failure/compatibility: invalid or zero duration cannot become an active
  preset; the previous valid value remains available.
- Verification: preset defaults/ranges/labels and auto-save probes.

### Weather Context to Settings and Location

- Public surface: read the validated selected location, provider coordinates,
  selected-city API timezone and an ephemeral request credential when a refresh
  is authorized.
- Provider authority: Settings & Location owns the stored value and its
  validation; Weather Context owns the refresh and normalized weather state.
- Allowed interaction: the credential may travel only to the provider request
  path and must be redacted before any diagnostic/evidence boundary.
- Failure/compatibility: missing/invalid key or location preserves the clock,
  timer path and last valid settings; weather becomes unavailable by the
  accepted freshness rules.
- Verification: redacted request fixtures and local-secret checks.

### Location Refresh Orchestration

- Public surface: after a valid location change, request a Weather Context
  refresh using the newly validated location and the authorized ephemeral
  provider context.
- Provider authority: Weather Context owns refresh cadence, provider mapping,
  cache/history writes and the resulting freshness projection; Settings &
  Location owns the location write and validation.
- Allowed interaction: the request is made only after the new Settings value is
  valid and persisted. No caller may write Weather Context storage directly.
- Failure/compatibility: failed refresh preserves the valid location and
  applies the accepted cache/freshness/error behavior; clock and timer paths
  remain available.
- Verification: city-change refresh and cache-preservation probe with a
  redacted provider fixture.

### Settings and Location to Bundled Location Catalog

- Public surface: read-only country search followed by city search scoped to the
  selected country, returning Russian/canonical/ASCII aliases and coordinates.
- Provider authority: the bundled catalog is immutable application data;
  Settings & Location owns the selected result and persistence.
- Allowed interaction: no network or Google Services; catalog search is
  case-insensitive and city search cannot escape the selected country.
- Failure/compatibility: missing city keeps the previous valid location and
  shows `Город не найден` inline; required GeoNames attribution is rendered in
  Settings.
- Verification: fixture-based offline catalog search and attribution checks.

## Orchestration Ownership

- Main Display owns the user-facing composition of the glanceable screen and
  delegates timer/forecast commands; it does not own timer or weather state.
- Weather Context owns weather refresh after a validated location change and
  the refresh cadence; Settings & Location owns the location write itself.
- Timer & Alert owns the timer start/cancel/complete workflow and the permitted
  audio request; Main Display only supplies gestures and renders state.
- Forecast Sessions owns forecast-screen entry/exit and its data-completeness
  decision; Weather Context remains the data owner.
- The composition root may route lifecycle and navigation wiring, but it may
  not become the owner of any of these business workflows.
