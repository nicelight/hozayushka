---
description: L3 feature for main weather cards, freshness, local history and pressure context.
status: draft
id: FT-002
epic: EP-002
lifecycle: implemented
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
last_updated: 2026-08-10
---
# FT-002 — Main weather cards and local context

## Product outcome

Владелец видит четыре различимые карточки вчера/сегодня/завтра/послезавтра с
актуальной погодой, температурным цветом и локально вычисленным pressure trend,
а при offline или stale data получает честное стабильное состояние.

## Requirements

- Direct FT-002 outcome: REQ-005, REQ-006, REQ-007, REQ-008, REQ-026.
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

## Acceptance criteria

### FT-002-AC-001 — Ordered card projection

- REQ: REQ-005

Cards always appear in yesterday/today/tomorrow/day-after order; Today is
  slightly larger and the other three have equal smaller size.

### FT-002-AC-002 — Filled card and day/night presentation

- REQ: REQ-005, REQ-022

Filled card contains illustration, temperature, calendar date and
  temperature-dependent background without textual day/weather labels.
Day/night data follows the selected-city day/night rule; moon phase is used
  only when supplied, otherwise a regular moon is used.

### FT-002-AC-003 — Temperature palette and glass

- REQ: REQ-006, REQ-023

Temperature sign is shown only from −4 through +4 °C inclusive; all 78
  accepted compile-time HEX values are used with endpoint clamp, and accepted
  static pseudo-glass is shared by temperature and pressure arrows.

### FT-002-AC-004 — Refresh and freshness

- REQ: REQ-007, REQ-025

Weather/current pressure refreshes after launch, city change and every 30
  minutes when network is available; last successful data is cached.
Cache age up to 24 hours remains available offline. Older data renders all
  four cards as transparent contours without values, illustrations or arrows.

### FT-002-AC-005 — Local history and pressure trends

- REQ: REQ-008

Local history begins at installation and retains the accepted seven-day
  window. Current and yesterday trends use the accepted 3-hour/12-hour fallback
  and thresholds; absent history produces no arrow and first-run Yesterday is a
  dated empty contour without layout shift.

### FT-002-AC-006 — Unknown-condition and optional-field fallback

- REQ: REQ-026

Unknown conditions or missing optional fields use the neutral cloud and regular
moon fallbacks, preserve available temperature/color, do not invent textual
weather labels, and do not crash.

### FT-002-AC-007 — Redacted provider and evidence path

- REQ: REQ-024

The FT-002 provider and verification path uses synthetic credentials only; the
user-provided key does not appear in source, packaged resources, logs, fixtures,
screenshots, or verification evidence.

## Edge / failure behavior

- No city, missing key, provider failure or no network leaves clock and timers
  usable; cards show only the accepted available/empty state.
- Unknown condition or missing optional field uses the neutral cloud/available
  data fallback and never invents a textual condition or crashes.
- Insufficient pressure history suppresses arrows rather than fabricating a
  trend. Weather data before installation is never assumed.

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
  `REQ-026`.

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
acceptance criterion or change FT-002's implemented lifecycle/direct RTM
values. W14 is host/static proof only. Existing Samsung/custom-ROM/1280x720
physical target evidence remains `DEFERRED` and non-blocking, with no runtime
`PASS` claim. No provider, public contract, new edge, dependency, forecast,
timer/audio or target-device behavior changed.

## W15 Production Yandex adapter boundary

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

Global backbone is complete at Planning Revision `1` and the Foundation Gate
is closed; feature-level design is complete for task planning.

Applicable global specs: [System Architecture](../architecture/system-architecture.md),
[Boundary Map](../contracts/boundary-map.md), [Capability Interfaces](../contracts/capability-interfaces.md),
[Weather Provider](../contracts/weather-provider.md), [Weather Card Presentation](../contracts/weather-card-presentation.md),
[Local Secret Handling](../contracts/local-secret-handling.md), [Platform Runtime](../contracts/platform-runtime.md),
[Local Data](../domains/local-data.md), [Lifecycle Map](../states/lifecycle-map.md) and
[Runtime Verification](../testing/runtime-verification.md).
