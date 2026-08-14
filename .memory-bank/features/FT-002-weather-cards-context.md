---
description: L3 feature for main weather cards, freshness, local history and pressure context.
status: draft
id: FT-002
epic: EP-002
lifecycle: planned
spec_design_status: complete
spec_design_links:
  - .memory-bank/contracts/weather-provider.md
  - .memory-bank/contracts/weather-card-presentation.md
  - .memory-bank/contracts/local-secret-handling.md
  - .memory-bank/contracts/platform-runtime.md
  - .memory-bank/contracts/boundary-map.md
  - .memory-bank/contracts/capability-interfaces.md
  - .memory-bank/domains/local-data.md
  - .memory-bank/states/lifecycle-map.md
  - .memory-bank/testing/runtime-verification.md
last_updated: 2026-08-12
---
# FT-002 — Main weather cards and local context

## Product outcome

Владелец видит четыре различимые карточки вчера/сегодня/завтра/послезавтра с
актуальной погодой выбранного provider, температурным цветом, deliberately
designed condition illustration и локально вычисленным provider-consistent
pressure trend, а при offline, provider failure или stale data получает честное
стабильное состояние без automatic fallback.

## Requirements

- Direct FT-002 outcome: REQ-005, REQ-006, REQ-007, REQ-008, REQ-026,
  REQ-029.
- FT-002 integration claims: REQ-022, REQ-023, REQ-024, REQ-025.

The integration claims are limited to the Weather Context delta proved by this
feature: selected-city timezone for weather projection, card-specific visual
constraints, redacted provider/evidence handling, and preserving the existing
clock/timer path during weather failure. RTM primary ownership remains with
FT-001 for device clock/date and display-shell constraints, FT-008 for user
API-key settings and validation, and FT-006 for timer lifecycle behavior.

## Use cases

1. Владелец быстро считывает четыре карточки в фиксированном порядке.
2. Владелец видит актуальный cache без сети и понимает empty state после
   истечения 24 часов.
3. Владелец получает yesterday card и pressure arrows после накопления local
   history.
4. Владелец видит neutral cloud fallback для неизвестного condition без crash.
5. После явной смены provider владелец видит только его matching cache/history;
   ошибка не подставляет данные другого provider.

## Acceptance Criteria

### FT-002-AC-001 — Ordered card projection

- REQ: REQ-005

Cards always appear in yesterday/today/tomorrow/day-after order; Today is
  slightly larger and the other three have equal smaller size.

- Verification: deterministic four-position projection check.

### FT-002-AC-002 — Filled card and day/night presentation

- REQ: REQ-005, REQ-022

Filled card contains illustration, temperature, calendar date and
  temperature-dependent background without textual day/weather labels.
Day/night data follows the selected-city day/night rule; moon phase is used
  only when supplied, otherwise a regular moon is used.

- Verification: deterministic filled-card and city-timezone projection examples.

### FT-002-AC-003 — Temperature palette and glass

- REQ: REQ-006, REQ-023

Temperature sign is shown only from −4 through +4 °C inclusive; all 78
  accepted compile-time HEX values are used with endpoint clamp, and accepted
  static pseudo-glass is shared by temperature and pressure arrows.

- Verification: palette boundary lookup and static presentation inspection.

### FT-002-AC-004 — Refresh and freshness

- REQ: REQ-007, REQ-025

Weather/current pressure refreshes after launch, city change and every 30
  minutes when network is available; last successful data is cached.
Cache age up to 24 hours remains available offline. Older data renders all
  four cards as transparent contours without values, illustrations or arrows.

- Verification: deterministic launch/city/cadence and freshness-boundary scenarios.

### FT-002-AC-005 — Local history and pressure trends

- REQ: REQ-008

Local history begins at installation and retains the accepted seven-day
  window. Current and yesterday trends use the accepted 3-hour/12-hour fallback
  and thresholds; absent history produces no arrow and first-run Yesterday is a
  dated empty contour without layout shift.

- Verification: deterministic seven-day history and pressure-threshold scenarios.

### FT-002-AC-006 — Unknown-condition and optional-field fallback

- REQ: REQ-026

Unknown conditions or missing optional fields use the neutral cloud and regular
moon fallbacks, preserve available temperature/color, do not invent textual
weather labels, and do not crash.

- Verification: unknown-condition and absent-optional-field fixture scenarios.

### FT-002-AC-007 — Redacted provider and evidence path

- REQ: REQ-024

The FT-002 provider and verification path uses synthetic OpenWeather
credentials only; the owner's key does not appear in source, packaged
resources, logs, fixtures, screenshots, or verification evidence. Open-Meteo
uses no credential in the accepted default path.

- Verification: credential-absence/redaction inspection for both provider paths.

### FT-002-AC-008 — Selected-provider isolation

- REQ: REQ-007, REQ-008, REQ-029

Refresh targets only the selected provider, including after an explicit
provider change. Cache and seven-day history retain provider identity; only a
matching cache may remain visible, trend comparison never mixes providers, and
failure neither changes selection nor triggers a request or data substitution
from the other provider.

- Verification: deterministic provider-change, failure, cache-identity and history-isolation scenarios.

### FT-002-AC-009 — Designed condition illustration composition

- REQ: REQ-005, REQ-022, REQ-023, REQ-026

Every fresh filled Main Display card renders one deliberately drawn, legible,
non-text illustration from the existing `WeatherIllustration` projection:
`CLEAR` is a sun, `CLOUD`/`NEUTRAL_CLOUD` is a cloud, `RAIN` is a cloud with
distinct rain marks, `SNOW` is a cloud with distinct snow marks, and `MOON` is
the selected-city night moon state. A supplied `moonPhase` may refine that
existing moon input; absent phase uses the regular moon fallback. The
illustration is layered inside a dedicated card area and its measured painted
bounds do not obscure the temperature, calendar date or pressure-arrow
content. The illustration is not a Unicode/condition text label. Existing
yesterday/today/tomorrow/day-after order, Today sizing, temperature palette,
pseudo-glass, selected-provider identity, city-timezone day/night mapping and
stale/first-run empty-card rules remain unchanged.

- Verification: deterministic projection cases plus a host/static rendered
  image and bounds review for sun, cloud, rain, snow, neutral-cloud and day/night
  moon states at the four-card row geometry.

## Edge / failure behavior

- No city, missing OpenWeather key, selected-provider failure or no network
  leaves clock and timers usable; cards show only matching accepted
  available/empty state and no fallback provider is requested. Coverage:
  FT-002-AC-004, FT-002-AC-007, FT-002-AC-008.
- Unknown condition or missing optional field uses the neutral cloud/available
  data fallback and never invents a textual condition or crashes. Coverage:
  FT-002-AC-006, FT-002-AC-009.
- A stale or first-run empty card keeps its existing transparent contour and
  date-only geometry; it renders no illustration. Coverage: FT-002-AC-004,
  FT-002-AC-009.
- Insufficient pressure history suppresses arrows rather than fabricating a
  trend. Weather data before installation is never assumed, and records from
  another provider are not included. Coverage: FT-002-AC-005,
  FT-002-AC-008.

## Sources and normative inputs

- [.memory-bank/prd.md](../prd.md): `PRD-FR-007`–`PRD-FR-018`, `PRD-NFR-004`–
  `PRD-NFR-005`, `PRD-AC-002`–`PRD-AC-003`, `PRD-AC-008`, `PRD-AC-010`.
- [.memory-bank/glossary.md](../glossary.md): weather, freshness, palette and
  pseudo-glass vocabulary.
- [.memory-bank/invariants.md](../invariants.md): cache, palette and visual
  constraints.
- [.memory-bank/contracts/local-secret-handling.md](../contracts/local-secret-handling.md): local
  credential and evidence boundary.
- [.memory-bank/contracts/platform-runtime.md](../contracts/platform-runtime.md): platform signal
  ownership and network-failure compatibility.
- [.memory-bank/states/lifecycle-map.md](../states/lifecycle-map.md): weather
  freshness lifecycle.

## Verification targets

- `PRD-AC-002`, `PRD-AC-003`, `PRD-AC-008`, `PRD-AC-010`,
  `PRD-FR-013`–`PRD-FR-018`, `REQ-022`, `REQ-023`, `REQ-024`, `REQ-025`,
  `REQ-026`, `FT-002-AC-008`, `FT-002-AC-009`.

## W3 implementation evidence

The indexed task `TASK-004-T3-FT-002-W3` is `done` after current attempt-2
functional `PASS` and required T3 semantic `semantic-pass`. Current evidence is
recorded in the [functional verification report](../../.tasks/TASK-004-T3-FT-002-W3/TASK-004-T3-FT-002-W3-S-VERIFY-final-report-docs-02.md),
[semantic verification report](../../.tasks/TASK-004-T3-FT-002-W3/TASK-004-T3-FT-002-W3-S-RED-VERIFY-final-report-docs-01.md),
[attempt-2 gates](../../.tasks/TASK-004-T3-FT-002-W3/gate-results-attempt-2.md),
[boundary receipt](../../.tasks/TASK-004-T3-FT-002-W3/boundary-review-attempt-2.md),
[secret scan](../../.tasks/TASK-004-T3-FT-002-W3/secret-scan-attempt-2.md) and
[implementation summary](../../.tasks/TASK-004-T3-FT-002-W3/implementation-summary-attempt-2.md).
The [target-device receipt](../../.tasks/TASK-004-T3-FT-002-W3/target-device-attempt-2.md)
records no attached target and the resulting deferred scope.
Attempt 1 remains supporting-only. Target card readability, static pseudo-glass
and Android runtime/lifecycle observation remain `DEFERRED` and non-blocking
with residual risk under the accepted policy; no runtime `PASS` is claimed.

## W14 Weather Context projection/decode follow-up

The indexed [`TASK-017-T3-FT-001-W14`](../tasks/TASK-017-T3-FT-001-W14.task.json)
is `done` after executor `PASS_FOR_HANDOFF`, fresh functional `PASS` and
independent durable semantic `semantic-pass`. W14 proves the bounded upstream
Weather Context optimization from `FT-001-AC-002 / REQ-002`: unchanged scalar
reads reuse one capability-owned display-ready projection, while accepted
successful refresh, validated location and existing date/day-night,
pressure-trend and 24-hour freshness boundaries rebuild it. Failed refresh
preserves the last successful snapshot; the four-card, timezone, day/night,
pressure, fresh/stale and empty-contour semantics remain unchanged.

See the [executor handoff](../../.protocols/TASK-017-T3-FT-001-W14/handoff.md),
[functional verification](../../.protocols/TASK-017-T3-FT-001-W14/verification.md),
[verifier-owned functional evidence](../../.tasks/TASK-017-T3-FT-001-W14/verifier-owned-evidence.md),
[durable semantic verification](../../.protocols/TASK-017-T3-FT-001-W14/red-verification.md)
and [semantic report](../../.tasks/TASK-017-T3-FT-001-W14/TASK-017-T3-FT-001-W14-S-RED-VERIFY-final-report-docs-01.md).

Weather Context remains the owner of cache/history, refresh, freshness and
projection semantics; this cross-feature W14 evidence does not add an FT-002
acceptance criterion or, at that historical boundary, change FT-002's
then-`implemented` lifecycle/direct RTM values. W14 is host/static proof only.
Existing Samsung/custom-ROM/1280x720
physical target evidence remains `DEFERRED` and non-blocking, with no runtime
`PASS` claim. No provider, public contract, new edge, dependency, forecast,
timer/audio or target-device behavior changed.

## Historical W15 Yandex adapter boundary

This section records brownfield evidence for the superseded Yandex-only target.
It does not define or prove the accepted Open-Meteo/OpenWeather migration.

The indexed [`TASK-018-T3-FT-002-W15`](../tasks/TASK-018-T3-FT-002-W15.task.json)
is `done` after executor `PASS_FOR_HANDOFF`, fresh functional `PASS` and
independent semantic `semantic-pass`. It replaces the composition root's
production fixture-only provider with an actual Yandex REST adapter behind the
existing `WeatherProvider` boundary, using the accepted endpoint/query/header,
local Settings key retrieval/redaction, bounded transport failure mapping, the
minimum `INTERNET` permission, off-UI refresh wiring and deterministic
host/redacted tests. The existing redacted fixture path remains an isolated
test/probe route and never performs live I/O.

See the [task handoff](../../.protocols/TASK-018-T3-FT-002-W15/handoff.md),
[functional verification](../../.protocols/TASK-018-T3-FT-002-W15/verification.md),
[verifier-owned evidence](../../.tasks/TASK-018-T3-FT-002-W15/verifier-owned-evidence-attempt-2.md),
[semantic verification](../../.protocols/TASK-018-T3-FT-002-W15/red-verification.md)
and [semantic evidence](../../.tasks/TASK-018-T3-FT-002-W15/red-verifier-owned-evidence-attempt-2.md).

W15 owns only the production transport/mapping/wiring delta and does not re-own
the completed W3 card/cache/history acceptance. FT-003, FT-004 and FT-008 are
checked only as compatible consumers/inputs through their accepted boundaries;
their acceptance and historical task records remain unchanged. `REQ-024`
remains `planned` under its FT-008 primary ownership; W15's synthetic-only
redaction proof does not promote it. Existing provider/public contracts,
ownership edges, Planning Revision `1`, scheduler state and terminal history
remain unchanged.

W15 is host/build/static/redacted proof only. Samsung/custom-ROM/1280x720
target-device readiness and live-provider/network compatibility remain
`DEFERRED`; no target-device runtime `PASS` is claimed.

## SDD Design Gate

The Global Backbone is `complete` at Planning Revision `2`, Foundation
revalidation is successful and the Gate anchors remain closed. The two fixed
adapters, Weather Context normalization/cache ownership, provider/location
identity and no-fallback rules remain represented by W17's accepted migration
implementation facts, while the activation repair is closed by W20;
feature-level design is `complete`. Existing W3/W15/W17 terminal records,
W20 closure evidence, scheduler state and historical evidence remain
traceable.

Current Revision-2 ownership is exact: W3 retains unchanged AC-001/AC-003;
W17 remains historical for migration facts covering AC-002/AC-005/AC-006;
W20 owns the AC-004/AC-007/AC-008 activation repair delta; completed W22 is
the historical AC-009 illustration baseline, while new W25 owns only the
operator-requested bounds/sun/pressure-rendering adjustment under the same
AC-009 locator. W15 Yandex evidence is historical and is not target-provider
proof.

Applicable global specs: [System Architecture](../architecture/system-architecture.md),
[Boundary Map](../contracts/boundary-map.md), [Capability Interfaces](../contracts/capability-interfaces.md),
[Weather Provider](../contracts/weather-provider.md), [Weather Card Presentation](../contracts/weather-card-presentation.md),
[Local Secret Handling](../contracts/local-secret-handling.md), [Platform Runtime](../contracts/platform-runtime.md),
[Local Data](../domains/local-data.md), [Lifecycle Map](../states/lifecycle-map.md) and
[Runtime Verification](../testing/runtime-verification.md).

## W16 Settings prerequisite and transition safeguard

The indexed [`TASK-019-T3-FT-008-W16`](../tasks/TASK-019-T3-FT-008-W16.task.json)
is `done` after final Attempt-3 functional `PASS` and semantic `semantic-pass`.
Its [functional verification](../../.protocols/TASK-019-T3-FT-008-W16/verification.md)
and [semantic verification](../../.protocols/TASK-019-T3-FT-008-W16/red-verification.md)
establish the accepted Settings-owned provider/key projection while keeping
both earlier unsuccessful attempts traceable.

At the W16 boundary, the closing safeguard intentionally denied
provider-unidentified legacy key access/refresh so the owner key could not reach
the superseded provider path. W17 subsequently installed the selected-provider
migration and W20 now owns the remaining valid-key-save activation repair. This
historical handoff does not promote W16/W17 or alter FT-002 lifecycle.
Physical-device/live-provider evidence remains `DEFERRED`, with no runtime
`PASS` claim.

## W17 terminal provider-migration boundary

The authoritative
[`TASK-020-T3-FT-002-W17`](../tasks/TASK-020-T3-FT-002-W17.task.json) is
`failed` after all `3/3` unsuccessful attempts. Final Attempt 3 retained an
executor `PASS_FOR_HANDOFF` and fresh functional `PASS`, but the required
[semantic verification](../../.protocols/TASK-020-T3-FT-002-W17/red-verification.md)
returned `semantic-fail`. Attempt 1, Attempt 2 and Attempt 3 history remains
durable in the indexed task record and task-owned evidence; no fourth `/exe`
is permitted. The durable reconciliation result is recorded in the
[W17 sync report](../../.tasks/TASK-020-T3-FT-002-W17/TASK-020-T3-FT-002-W17-S-MB-SYNC-final-report-docs-01.md).

The [final functional verification](../../.protocols/TASK-020-T3-FT-002-W17/verification.md)
still records implemented migration facts separately from acceptance: Yandex
is removed from production, exactly Open-Meteo and OpenWeather are wired,
ordinary refresh dispatches only the selected adapter, provider/location
cache-history identity is present, mapping/fallback behavior is
provider-neutral, and credential evidence remains redacted. Those facts do not
close FT-002 or convert the failed accepted activation outcome into a semantic
pass.

The admitted activation defect is current: first-time OpenWeather selection
queues its provider-change refresh before a key exists; accepting a valid key
later triggers no refresh, leaves the obsolete missing-key error current and
makes zero provider calls. This fails the accepted FT-002-AC-004/008 activation
outcome and REQ-007/REQ-029. FT-002 lifecycle therefore remains `planned`.
Repair ownership is now `TASK-023-T3-FT-002-W20`, followed by fresh plan review,
strict readiness, execution and both verification gates. Device/live-provider
evidence remains `DEFERRED`; no runtime `PASS` is claimed.

## W20 activation repair boundary

The new indexed [`TASK-023-T3-FT-002-W20`](../tasks/TASK-023-T3-FT-002-W20.task.json)
is `done` after executor `PASS_FOR_HANDOFF`, fresh functional `PASS` and final
independent T3 `semantic-pass`. It owns only the confirmed activation delta:
after the initial missing-key result, a valid OpenWeather key save requests the
existing Weather Context refresh, invokes only selected OpenWeather, clears the
obsolete missing-key error on successful matching data, and preserves
provider/location isolation and redacted key handling. Settings & Location
remains the validation/persistence/secret owner; Weather Context owns
refresh/error state; the composition root only wires the existing executor.

Current evidence is linked from the [executor handoff](../../.protocols/TASK-023-T3-FT-002-W20/handoff.md),
[fresh functional verification](../../.protocols/TASK-023-T3-FT-002-W20/verification.md),
[verifier-owned evidence](../../.tasks/TASK-023-T3-FT-002-W20/verifier-owned-evidence.md),
[fresh timer-independence receipt](../../.tasks/TASK-023-T3-FT-002-W20/verifier-owned-weather-refresh-timer-independence.json),
[final semantic verification](../../.protocols/TASK-023-T3-FT-002-W20/red-verification.md),
and [semantic report](../../.tasks/TASK-023-T3-FT-002-W20/TASK-023-T3-FT-002-W20-S-RED-VERIFY-final-report-docs-01.md).
Attempt 1 remains supporting-only; the fresh verifier-owned artifacts repair
the prior provenance gap. W20 is host/build/static/redacted proof only.
Target-device, custom-ROM and live-provider evidence remains `DEFERRED`, with
no runtime `PASS` claim.

W20 depends on completed `TASK-019-T3-FT-008-W16`, not failed W17. The direct
downstream W18 dependency remains W20, and W18 is now complete; W19 remains
blocked with its historical status/evidence unchanged. No new canonical spec,
graph edge, provider, event boundary or Planning Revision is required. FT-002
lifecycle remains `planned` because the broader feature and downstream forecast
claims remain open; scheduler recovery, promotion and W19 lifecycle changes
remain external.

## W22 operator-requested illustration delta

The indexed `TASK-025-T3-FT-002-W22` was the bounded Main Display visual
follow-up after completed `TASK-024-T3-FT-001-W21`. Current code now carries
the normalized `WeatherIllustration` enum and day/night projection input, but
the four Main Display cards do not render that input; the existing Unicode
`illustrationText` path is used only by forecast-card composition. The accepted
route keeps the Weather Context/provider boundary unchanged and adds a
display-owned Canvas primitive layer in the existing card composition, with
focused host/static/image/resource evidence for legibility and non-overlap.

The task does not add drawable assets, an image pipeline, a dependency, a new
resource, a Weather Context/provider change, a new graph edge or a public
contract. It preserves the historical W3/W15/W17/W20 records, planned W21
history, prior terminal `SUCCESS`, selected-provider identity, city-timezone
day/night mapping, four-card order, Today sizing and no-label/stale-card rules.
Target 1280×720 Samsung/custom-ROM readability remains `DEFERRED` with
residual risk when no authorized target observation exists.

W22 is now `done` after fresh executor `PASS_FOR_HANDOFF`, functional `PASS`
and independent T3 semantic `semantic-pass`. The host/image evidence confirms
recognizable CLEAR, CLOUD, NEUTRAL_CLOUD, RAIN, SNOW and MOON Canvas states,
including the existing moonPhase fallback, no text/emoji, no clipping and
non-overlap with temperature/date/pressure. The four-card order, Today sizing,
stale/empty behavior, day/night input and Weather Context/provider boundaries
remain unchanged. See the [executor report](../../.tasks/TASK-025-T3-FT-002-W22/TASK-025-T3-FT-002-W22-S-EXE-final-report-code-01.md),
[functional verification](../../.tasks/TASK-025-T3-FT-002-W22/TASK-025-T3-FT-002-W22-S-VERIFY-final-report-docs-01.md),
[semantic verification](../../.tasks/TASK-025-T3-FT-002-W22/TASK-025-T3-FT-002-W22-S-RED-VERIFY-final-report-docs-01.md),
[contact sheet](../../.tasks/TASK-025-T3-FT-002-W22/illustration-contact-sheet.png)
and [bounds](../../.tasks/TASK-025-T3-FT-002-W22/illustration-bounds.json).

## W25 operator-feedback visual adjustment

The new indexed `TASK-028-T3-FT-002-W25` is a smallest cohesive T3 follow-up
after `TASK-027-T3-FT-001-W24`, because both the prerequisite and this task
write the same Main Display composition surface. It does not create a new
feature AC: W25 owns the bounded adjustment under `FT-002-AC-009`, while W22's
done record and evidence remain the historical six-state illustration baseline.

The accepted adjustment reduces the measured painted bounds of all six
existing `CLEAR`, `CLOUD`, `NEUTRAL_CLOUD`, `RAIN`, `SNOW` and `MOON` graphics,
moderately enlarges only the CLEAR sun disk inside its reduced overall
composition, and replaces the ineffective Main Display Unicode pressure glyphs
with measured Canvas/Path arrows. `WeatherCapability` remains the sole owner of
`pressureDirection`/`pressureArrowCount` calculation and history semantics;
there is no partly-cloudy state or pressure calculation change.

The hard production/test boundary is exactly
`DisplayCapability.kt` plus `DisplayProjectionTest.kt`. W25 preserves the four
card order, Today sizing, temperature/date/pressure non-overlap, temperature
palette, pseudo-glass, city-timezone/day-night and moon fallback,
provider/cache/freshness/stale semantics, and clock/timer/audio/network paths.
Fresh RED/GREEN evidence must include measured icon and sun bounds, a
UP/DOWN/zero-arrow contact sheet with explicit stroke-width/visibility proof,
an independent visual rubric, build/unit/static gates and target-device
`DEFERRED` handling. No emulator, adb, device, network or provider action is
part of planning.

W25 is now `done` after executor `PASS_FOR_HANDOFF`, fresh functional `PASS`
and independent T3 `semantic-pass`. The six existing illustration states retain
their semantics while painted envelopes measure approximately 69.5–70.2% of
the prior bounds without clipping or overlap; the CLEAR sun disk is
`1.1789474x`; and visible pressure arrows use Canvas/Path geometry with `5 px`
stroke, round caps/joins and no arrow pixels for zero/steady. Focused, full
host, build, static and boundary regression gates pass. `WeatherCapability`
remains the owner of pressure direction/count semantics. Samsung/custom-ROM
target readability and runtime Canvas compatibility remain `DEFERRED`, with no
device/runtime `PASS` claim. See the [W25 sync report](../../.tasks/TASK-028-T3-FT-002-W25/TASK-028-T3-FT-002-W25-S-MB-SYNC-final-report-docs-01.md),
[functional verification](../../.tasks/TASK-028-T3-FT-002-W25/TASK-028-T3-FT-002-W25-S-VERIFY-final-report-docs-01.md)
and [semantic verification](../../.tasks/TASK-028-T3-FT-002-W25/TASK-028-T3-FT-002-W25-S-RED-VERIFY-final-report-docs-01.md).
