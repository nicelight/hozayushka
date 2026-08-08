---
description: Accepted public in-process contracts between the V1 capability slices.
status: active
last_updated: 2026-08-07
source_of_truth: .memory-bank/architecture/system-architecture.md, .memory-bank/prd.md, operator confirmation 2026-08-04 and 2026-08-06
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
  presentation data, submit the accepted countdown cancellation command, and
  submit an any-tap overdue dismissal command.
- Provider authority: Timer & Alert owns configured-duration interpretation,
  one-active-timer enforcement, elapsed arithmetic, recovery and overdue state.
- Allowed interaction: a single tap starts a preset or shows the accepted hint;
  a single tap during countdown is never a cancel command; a double tap during
  countdown cancels; a single or double tap in overdue dismisses the overdue
  state and returns to `idle`. Main Display does not calculate remaining time or
  write timer state.
- Failure/compatibility: timer operation is independent of network; alert audio
  denial cannot remove the visual overdue state or block dismissal; a resumed
  timer already past its duration re-enters the same overdue presentation and
  alert-policy path.
- Verification: timer transition, countdown-gesture, overdue-any-tap and
  resumed-overdue audio-policy probes are mandatory host evidence; target-ROM
  lifecycle evidence remains deferred/non-blocking from
  [Runtime Verification](../testing/runtime-verification.md) and is never
  reported as runtime `PASS` without a target.

### Main Display to Forecast Sessions

- Public surface: request hourly or long-term session entry from the accepted
  card, observe session state, and send the shared exit gestures.
- Ownership split: Weather Context owns normalized forecast data and the
  availability/completeness predicate exposed by its read model. Forecast
  Sessions owns the user-facing session creation decision, rejection of an
  unavailable or incomplete read model, the auto-close/single-tap-hint/
  double-tap/hold-release flow and temporary session state.
- Allowed interaction: Main Display supplies the user intent and renders the
  returned session projection; it does not read Weather Context storage.
- Failure/compatibility: missing required data leaves the main display visible
  and returns the exact accepted availability message.
- Verification: eight-slot/ten-card fixture checks and shared-session gesture
  checks.

### FT-003 Hourly Forecast Session Surface

- Entry command: Main Display requests the hourly session from Today; Forecast
  Sessions accepts it only when Weather Context exposes a complete ordered
  eight-slot projection.
- Returned projection: exactly eight cards in two rows of four, using slot time
  in place of calendar date, the shared temperature/glass/illustration rules,
  and no pressure arrow.
- Failure/compatibility: unavailable or incomplete hourly data leaves Main
  Display visible and returns `Почасовой прогноз еще не подгрузился`; no empty
  or fabricated session is returned.
- Ownership: Forecast Sessions owns the session state, three-second timer and
  gestures and user-facing entry/rejection; Main Display owns composition and
  Weather Context owns normalized hourly data plus the availability/completeness
  predicate in the read model. No direct storage or provider-adapter access is
  allowed.
- Verification: deterministic eight-slot order/timezone/completeness checks and
  shared exit-gesture checks.

### Main Display to Settings and Location

- Public surface: request the Settings screen from city interaction and read
  the selected-city display projection plus the validated presentation
  projection containing alert sound/volume and glass intensity.
- Provider authority: Settings & Location owns settings, selected country/city,
  coordinates, validation, automatic persistence and the persisted glass
  personalization values.
- Allowed interaction: empty-city short tap opens Settings; selected-city short
  tap is a no-op; hold opens Settings. Main Display does not change settings.
  Main Display composes the production Today card and the Settings preview from
  this validated projection, its existing Weather Context read model and the
  shared Weather Card Presentation rules.
- Failure/compatibility: inline owning errors and the last valid value remain
  visible; system Back and the bottom back icon return to Main Display.
- Verification: settings state/validation probes are mandatory host evidence;
  the accepted device/UI evidence route is deferred/non-blocking while no
  target is available and must be recorded as `DEFERRED`, not as runtime
  `PASS`.

### Settings Personalization Surface

- Public surface: Settings exposes the accepted built-in sound set, app alert
  volume and glass-intensity value; Main Display renders the Settings preview
  using the validated projection returned through the existing
  `Main Display → Settings & Location` contract.
- Provider authority: Settings & Location owns validation and persistence of
  these values; the preview must follow the production weather-card
  presentation rules and does not request weather data solely for rendering.
- Allowed interaction: valid changes auto-save; the glass slider updates the
  preview during the gesture. Invalid values retain the previous valid value
  and expose only the owning inline error. No modal error or extra Settings
  control is introduced.
- Failure/compatibility: preview uses Today temperature when available and
  `24 °C` otherwise, with two overlapping pressure arrows; system Back and the
  bottom back icon return to Main Display. Android silent/DND policy remains
  owned by Android Runtime Adapter.
- Boundary rule: Main Display supplies Today temperature from its existing
  Weather Context projection, or the accepted `24 °C` fallback. This is a
  presentation composition inside the existing Main Display → Settings &
  Location route; it does not create a Settings & Location → Weather Context
  edge or permit a private-storage bypass. The production Today card and the
  Settings preview use the same saved glass-personalization projection and
  shared Weather Card Presentation rules.
- Verification: deterministic Settings validation/auto-save probes and the
  shared weather-card presentation probe, with target-device readability only
  where host checks cannot establish the static pseudo-glass result.

### Forecast Sessions to Weather Context

- Public surface: request normalized complete hourly slots or ordered daily
  forecast data for the selected city and its API timezone.
- Ownership split: Weather Context owns forecast acquisition, normalization,
  storage and the availability/completeness predicate in the read model;
  Forecast Sessions owns only the user-facing session creation/rejection,
  transient display session and gestures.
- Allowed interaction: Forecast Sessions consumes the predicate and may reject
  an unavailable or incomplete read model and render only the accepted eight or
  ten cards. It may not invent missing slots or mutate the forecast cache.
- Failure/compatibility: missing hourly/long-term data returns the accepted
  message and no session is created.
- Verification: deterministic timezone, field-completeness and sequence checks.

### FT-003 Forecast Data Contract

- Public surface: request a complete hourly projection for the selected city,
  including its API timezone and the eight accepted slots.
- Ownership split: Weather Context normalizes provider values and publishes the
  availability/completeness predicate; Forecast Sessions owns the user-facing
  creation/rejection decision and consumes that read model.
- Allowed interaction: Forecast Sessions may reject an unavailable or
  incomplete read model and render the accepted cards, but may not invent
  missing slots, read provider fields or mutate Weather Context state.

### FT-004 Long-Term Forecast Session Surface

- Entry command: Main Display requests the long-term session from Tomorrow or
  Day-after; Forecast Sessions accepts it only when Weather Context exposes a
  complete ordered ten-day projection.
- Returned projection: exactly ten cards in two rows of five, beginning with
  today in the selected-city API timezone and continuing through the next nine
  calendar days. Cards use the shared temperature/glass/illustration rules,
  show `dd`, and omit pressure arrows.
- Failure/compatibility: unavailable or incomplete daily data leaves Main
  Display visible and returns `Долгосрочный прогноз еще не подгрузился`; no
  empty, partial or fabricated session is returned.
- Ownership: Weather Context owns normalized daily forecast data and its
  availability/completeness predicate. Forecast Sessions owns user-facing
  creation/rejection, transient state and the shared exit flow. Main Display
  supplies intent and renders the returned projection only.
- Verification: deterministic ten-day order/completeness/timezone checks and
  shared exit-gesture checks.

### Timer and Alert to Settings and Location

- Public surface: read the three configured durations, selected built-in sound
  and app alert volume as a validated settings projection.
- Provider authority: Settings & Location owns persistence and validation;
  Timer & Alert owns runtime interpretation and active state.
- Allowed interaction: Timer & Alert reads the projection at start and at the
  accepted settings-change boundary; it never writes settings directly.
- Failure/compatibility: invalid or zero duration cannot become an active
  preset; the previous valid value remains available.
- App alert volume is a validated integer in `0…100` percent with default
  `70`; `0` suppresses only app-alert sound and never removes visual overdue
  state. Android silent/DND policy remains platform-owned.
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
