---
description: Требования (REQ-IDs) + traceability matrix (RTM).
status: draft
last_updated: 2026-08-14
---
# Requirements: V1 product contract

## Status model
- Document `status`: `draft|active|deprecated|archived`
- RTM `Lifecycle`: `planned|implemented|verified`

## REQ list

Each requirement is derived from the clarified PRD. `Test` in the RTM points to
the corresponding product acceptance/verification target; it does not select a
test level or create a test artifact.

### Foundation executable baseline

- **REQ-000** — Before product-feature implementation, the project MUST provide
  a reproducible single-deployable Kotlin Android baseline with the accepted
  composition root and capability-slice discovery roots, a deterministic local
  state reset/fixture path, a redacted provider-fixture path, and recorded
  host build/test/smoke evidence. Target-device/emulator compatibility evidence
  is deferred until the application is ready for runtime/readiness validation;
  it is not a Foundation Gate prerequisite or a blocking T3 queue gate while a
  target is unavailable. Foundation MUST NOT introduce
  product behavior beyond the walking skeleton or any live user API key.
  Sources: `.memory-bank/foundation.md`, `.memory-bank/architecture/system-architecture.md`,
  `.memory-bank/testing/runtime-verification.md`.

### Main display and clock

- **REQ-001** — Main display MUST run in landscape fullscreen, hide system
  panels and keep the screen on while the app is open. Source: `PRD-FR-001`.
 city/date on the left, four weather cards along the lower-left area and three
 preset buttons on the right; date uses `dd` plus Russian genitive month and no
 year/weekday. Sources: `PRD-FR-002`, `PRD-FR-005`, `PRD-AC-001`.
- **REQ-002** — Main display MUST show dominant `HH:mm` clock without seconds,
  city/date on the left, a 25–30% total-height bottom weather-card band with
  four equal-height bottom-aligned cards, and three separate preset buttons on
  the right; the remaining 70–75% clock zone MUST contain the complete clock
  without clipping or overlap. Date uses `dd` plus Russian genitive month and
  no year/weekday. Sources: `PRD-FR-002`, `PRD-FR-005`, `PRD-FR-007`,
  `PRD-NFR-002`, `PRD-AC-001`.
- **REQ-003** — Clock colon MUST use the accepted online pulse, offline fixed
  brightness and active-timer blink behaviors. Sources: `PRD-FR-003`–`PRD-FR-004`.
- **REQ-004** — City interaction MUST follow the accepted hold/open-Settings,
  no-city tap/open-Settings and selected-city short-tap/no-op rules. Source:
  `PRD-FR-006`.

### Weather context and forecast

 tomorrow/day-after order, accepted relative sizing and card content/day-night
 presentation without textual day/weather labels. Sources: `PRD-FR-007`,
- **REQ-005** — Main weather cards MUST preserve the fixed yesterday/today/
  tomorrow/day-after order, equal shell height and bottom alignment, accepted
  width/content variation, and card content/day-night presentation without
  textual day/weather labels. Sources: `PRD-FR-007`,
  `PRD-FR-008`, `PRD-FR-009`, `PRD-FR-010`, `PRD-AC-002`.
- **REQ-006** — Temperature presentation MUST apply the −4…+4 sign rule, all
  78 explicit BR-001 HEX values with endpoint clamp, and the accepted static
  pseudo-glass material. Sources: `PRD-FR-009`, `PRD-FR-011`–`PRD-FR-012`,
  `PRD-AC-003`.
- **REQ-007** — Weather/current pressure MUST refresh only through the selected
  provider after launch, valid city change, explicit provider change and every
  30 minutes when network is available. The last successful normalized result
  MUST be cached with provider identity, usable offline through 24 hours only
  when it matches the current selection, then render all four cards as empty
  transparent contours. Sources: `PRD-FR-013`–`PRD-FR-014`, `PRD-AC-002`,
  `PRD-AC-008`.
- **REQ-008** — Local weather history MUST start at installation, retain the
  accepted seven-day window, calculate current and yesterday pressure trends by
  the accepted thresholds without mixing records from different providers, and
  keep first-run yesterday stable as a dated empty contour. Sources:
  `PRD-FR-015`, `PRD-FR-016`, `PRD-FR-017`, `PRD-FR-018`, `PRD-AC-002`.
- **REQ-009** — Hourly forecast MUST open from Today only when the selected
  provider supplies all eight fixed city-local slots and MUST show exactly
  those slots in a 2×4 layout with the shared forecast exit flow. For
  OpenWeather, an absent slot, including an already elapsed current-day slot,
  MUST keep the main display and show the accepted unavailable message; no slot
  may be synthesized or borrowed from Open-Meteo. Sources: `PRD-FR-019A`,
  `PRD-FR-019B`, `PRD-FR-019C`, `PRD-FR-022`, `PRD-AC-007A`.
- **REQ-010** — Long-term forecast MUST open from Tomorrow or Day-after only
  when the selected provider supplies its complete supported daily set: 10
  records for Open-Meteo or 8 for OpenWeather. The shared 2×5 layout MUST retain
  ten dated positions from selected-city today; Open-Meteo fills all ten,
  OpenWeather fills the first eight and leaves the last two unavailable/empty
  without synthesis or cross-provider data. Otherwise the main display remains
  with the accepted message; the shared exit flow is unchanged. Sources:
  `PRD-FR-019`, `PRD-FR-020`, `PRD-FR-021`, `PRD-FR-022`, `PRD-AC-007`.

### Timers and alert

- **REQ-011** — Product MUST provide three separately configurable presets,
  one active timer at a time, defaults of 3/10/30 minutes, accepted labels using
  the highest non-zero unit and fixed button colors. Sources: `PRD-FR-023`,
  `PRD-FR-024`, `PRD-FR-025`.
- **REQ-012** — A short tap MUST start the selected preset immediately; countdown
  replaces the large clock, current time moves aside and the active button is
  highlighted. Source: `PRD-FR-026`.
- **REQ-013** — During countdown a single tap MUST not cancel the timer and MUST
  show the accepted double-tap hint; a double tap anywhere MUST cancel and return
  to main display. Source: `PRD-FR-028`.
- **REQ-014** — Timer state MUST retain correct elapsed/remaining behavior across
  Activity changes, foreground loss, screen-off and temporary process stop by
  recalculating the accepted lifecycle state; reboot recovery is excluded.
  Sources: `PRD-FR-027`, `PRD-AC-005`.
- **REQ-015** — On completion the product MUST show the accepted fullscreen
  neon overdue state with blinking `+`, non-blinking full elapsed counter and
  tap dismissal behavior. Sources: `PRD-FR-029`–`PRD-FR-030`.
- **REQ-016** — Completion MUST use the accepted repeatable built-in sound set,
  default, 5–10 second ramp, system silent/DND policy and maximum 30-minute
  audio duration; visual overdue state remains regardless of audio permission.
  Source: `PRD-FR-031`, `PRD-AC-005`.

### Settings, location and personalization

- **REQ-017** — Settings MUST support default Khujand location, selected
  country/city coordinates and refresh behavior after a valid city change.
  Sources: `PRD-FR-032`, `PRD-FR-034`.
- **REQ-018** — Location selection MUST work offline through country-first then
  scoped city search over the accepted GeoNames `cities15000` subset, case-
  insensitive matching, Russian/canonical/ASCII aliases and required attribution.
  Source: `PRD-FR-034`, `PRD-AC-006B`.
- **REQ-019** — Settings MUST support the accepted alert sound set and app alert
  volume with automatic persistence; timer duration configuration is covered by
  REQ-011. Sources: `PRD-FR-031`–`PRD-FR-032`, `PRD-FR-037`–`PRD-FR-038`.
- **REQ-020** — Glass intensity MUST range from 0 to 1 with default 0.45, update
  the production weather-card preview live using the accepted fallback/two-arrow
  content, and persist after the gesture. Sources: `PRD-FR-035`–`PRD-FR-036`,
  `PRD-AC-003`, `PRD-AC-006`.
- **REQ-021** — Settings MUST show each accepted validation/network error inline
  without modal dialogs, preserve the last valid value on invalid input, auto-save
  valid changes, and return through the bottom back button or system Back. Sources:
  `PRD-FR-037`–`PRD-FR-038`, `PRD-AC-006A`, `PRD-AC-006C`.

### Cross-cutting constraints and resilience

- **REQ-022** — Device clock/date MUST use device timezone; weather dates, day/
  night boundaries and hourly labels MUST use the selected-city API timezone.
  Source: `PRD-FR-039`, `PRD-AC-009`.
- **REQ-023** — Product MUST meet the accepted target-device, Russian UI,
  readability, static-UI and lightweight-visual constraints; clock dominance
  takes precedence over effects, and outcomes that cannot be reliably proven
  host-side require the accepted deferred device verification route; unavailable
  device evidence is recorded as `DEFERRED` with residual risk and does not
  block the current T3 product queue. Sources:
  `PRD-NFR-001`, `PRD-NFR-002`, `PRD-NFR-003`, `PRD-NFR-004`, `PRD-NFR-005`,
  `PRD-NFR-006`, `PRD-AC-001`, `PRD-AC-003`.
- **REQ-024** — The owner's OpenWeather API key MUST remain local and MUST NOT
  appear in APK contents, source, logs or verification evidence; Open-Meteo MUST
  neither require nor use that key. Sources: `PRD-FR-033`, `PRD-AC-006`,
  Constitution Product Non-Negotiables.
- **REQ-025** — Network or weather-service unavailability MUST not break the
  clock, timer lifecycle, cancellation or overdue dismissal; fresh cached
  weather remains available offline under REQ-007. Sources: `PRD-NFR-004`,
  `PRD-AC-008`, `Edge Cases / Failure Handling`.
- **REQ-026** — Unknown weather conditions or missing optional fields MUST not
  crash or invent text; use the neutral cloud fallback while preserving available
  temperature/color, and do not open forecast views without required data.
  Sources: `PRD-FR-010`, `PRD-FR-022`, `Edge Cases / Failure Handling`,
  `PRD-AC-007`, `PRD-AC-010`.

- **REQ-027** — Settings MUST default to Open-Meteo without a user API key and
  MUST activate OpenWeather only after explicit owner selection with that
  provider's personal local key. Valid selection changes auto-save; provider or
  key failures MUST identify the selected provider and MUST NOT change the
  selection or report fallback. Sources: `PRD-FR-032`, `PRD-FR-037`,
  `PRD-AC-006`, `PRD-AC-006A`.
- **REQ-028** — Settings MUST show the required Open-Meteo attribution for the
  accepted personal non-commercial use alongside the independently required
  GeoNames attribution. Sources: `PRD-FR-032`, `Integrations / Dependencies`,
  `PRD-AC-006`.
- **REQ-029** — A selected-provider request or failure MUST NOT automatically
  request the other provider, change selection, substitute its cache/forecast,
  or mix provider records. Only the matching last valid normalized cache may
  remain visible within REQ-007 freshness. Sources: `PRD-FR-013`,
  `PRD-FR-032`, `PRD-FR-037`, `PRD-AC-002`, `PRD-AC-008`.

## Out of scope

- Publishing or distributing the APK in V1.
- Backend, cloud sync, accounts, multi-user mode or a shared embedded API key.
- Google Services, autostart/recovery after reboot and AMOLED pixel shifting.
- Weather history from before installation.
- Telegram bot and Android TTS before V2.
- Realtime blur, refraction/lensing, background capture, dynamic specular
  highlights, morphing animation and other heavy visual effects.
- Any additional functions, settings or left-side UI without a new accepted
  operator decision.
- Automatic cross-provider fallback, hidden provider switching, mixed-provider
  cache/history/forecast, or synthesized unavailable hourly/daily records.

## Traceability (RTM)
| REQ | Epic | Feature | Test | Lifecycle |
|---|---|---|---|---|
| REQ-000 | Foundation | FT-000 | Foundation Exit Criteria | verified |
| REQ-001 | EP-001 | FT-001 | PRD-AC-001 | implemented |
| REQ-002 | EP-001 | FT-001 | PRD-AC-001 | implemented |
| REQ-003 | EP-001 | FT-001 | PRD-FR-003/004 | implemented |
| REQ-004 | EP-001 | FT-001 | PRD-FR-006 | implemented |
| REQ-005 | EP-002 | FT-002 | PRD-AC-002 | implemented |
| REQ-006 | EP-002 | FT-002 | PRD-AC-003 | implemented |
| REQ-007 | EP-002 | FT-002 | PRD-FR-013/014; PRD-AC-008 | implemented |
| REQ-008 | EP-002 | FT-002 | PRD-FR-015/018; PRD-AC-002 | planned |
| REQ-009 | EP-002 | FT-003 | PRD-AC-007A | implemented |
| REQ-010 | EP-002 | FT-004 | PRD-AC-007 | implemented |
| REQ-011 | EP-003 | FT-005 | PRD-FR-023/024/025 | planned |
| REQ-012 | EP-003 | FT-006 | PRD-AC-004 | implemented |
| REQ-013 | EP-003 | FT-006 | PRD-FR-028 | implemented |
| REQ-014 | EP-003 | FT-006 | PRD-AC-005 | implemented |
| REQ-015 | EP-003 | FT-007 | PRD-FR-029/030 | implemented |
| REQ-016 | EP-003 | FT-007 | PRD-AC-005 | implemented |
| REQ-017 | EP-004 | FT-008 | PRD-FR-032/034 | implemented |
| REQ-018 | EP-004 | FT-008 | PRD-AC-006B | implemented |
| REQ-019 | EP-004 | FT-009 | PRD-AC-006/006C | planned |
| REQ-020 | EP-004 | FT-009 | PRD-AC-003/006 | planned |
| REQ-021 | EP-004 | FT-009 | PRD-AC-006A/006C | planned |
| REQ-022 | EP-001 | FT-001 | PRD-AC-009 | implemented |
| REQ-023 | EP-001 | FT-001 | PRD-AC-001/003 | implemented |
| REQ-024 | EP-004 | FT-008 | PRD-AC-006 | implemented |
| REQ-025 | EP-003 | FT-006 | PRD-AC-008 | implemented |
| REQ-026 | EP-002 | FT-002 | PRD-AC-007/010 | implemented |
| REQ-027 | EP-004 | FT-008 | PRD-AC-006/006A | implemented |
| REQ-028 | EP-004 | FT-008 | PRD-AC-006 | implemented |
| REQ-029 | EP-002 | FT-002 | PRD-AC-002/008 | implemented |

## W17 terminal traceability note

The final W17 functional `PASS` preserves implemented migration facts without
promoting the failed accepted outcome. Existing independently supported rows
REQ-005, REQ-022, REQ-024, REQ-025 and REQ-026 remain `implemented`; the exact
two-provider inventory, Yandex removal, provider-neutral mapping, ordinary
selected-only dispatch, provider/location state and redaction evidence are
linked from FT-002 and the authoritative TASK-020 record.

TASK-020 nevertheless remains `failed` after required semantic
`semantic-fail`: first-time OpenWeather selection refreshes before key entry,
and later valid-key save triggers no refresh while leaving the obsolete
missing-key error current. W17-owned unclosed REQ-007, REQ-008 and REQ-029
therefore remain `planned`. Directly blocked TASK-021 leaves REQ-009 `planned`;
transitively blocked TASK-022 leaves REQ-010 `planned`. No row is promoted to
`verified`, and device/live-provider evidence remains `DEFERRED` without a
runtime `PASS` claim.

## W20 activation-repair traceability note

`TASK-023-T3-FT-002-W20` is the completed repair owner for the activation delta
under REQ-007, REQ-024, REQ-025 and REQ-029: after the initial selected-
OpenWeather missing-key result, a valid local key save triggers one selected
OpenWeather refresh, clears the obsolete missing-key state on successful
matching data, and preserves no-fallback/provider-location isolation plus
secret redaction. Fresh W20 evidence supports the `implemented` RTM lifecycle
for REQ-007 and REQ-029; primary ownership of REQ-024 and REQ-025 remains
unchanged. It does not reopen or replace failed
`TASK-020-T3-FT-002-W17`; its accepted migration implementation facts remain
traceable there. `TASK-021-T2-FT-003-W18` now records the completed hourly
completeness recovery behind W20, while transitively blocked
`TASK-022-T2-FT-004-W19` remains behind W18. No REQ row is promoted to
`verified`; device/live-provider evidence remains `DEFERRED` without a runtime
`PASS` claim.

## W18 hourly-completeness traceability note

`TASK-021-T2-FT-003-W18` is the completed current-provider completeness owner
for REQ-009's AC-001/AC-005 delta: fresh deterministic evidence proves that
Open-Meteo and OpenWeather each open only with all eight fixed city-local slots,
and every one of the sixteen one-missing-slot cases, including elapsed
OpenWeather current-day slots, remains on Main Display with the exact
unavailable message and no synthesis, borrowing or cross-provider fallback.
The task card retains claim-linked executor, functional-verification and final
semantic evidence under W18 only. W4/W5 presentation and exit evidence remains
separate historical ownership; TASK-020 remains failed after 3/3 and TASK-022/
W19 remains blocked without execution or acceptance evidence. FT-003 and EP-002
lifecycles remain `planned`; no row is promoted to `verified`, and target
device/emulator rendering, live provider/subscription behavior and runtime
network compatibility remain `DEFERRED` by the explicit boundary with no
runtime `PASS` claim.

## W19 long-term-completeness traceability note

`TASK-022-T2-FT-004-W19` is the completed current-provider long-term owner for
REQ-010's FT-004-AC-001/AC-002/AC-005/AC-006 delta. Fresh host evidence proves
Tomorrow/Day-after entry for 10 Open-Meteo or 8 OpenWeather records, ten
selected-city dates, Open-Meteo `10/10`, OpenWeather `8+2` dated empty
positions, one-short rejection with the exact unavailable message, and
provider/cache isolation. The feature-level FT-004 semantic report is
`semantic-pass`; together with unchanged W5 AC-003/AC-004 evidence, REQ-010
is `implemented` and no row is promoted to `verified`. W18 and W20 remain
`done`; TASK-020 remains failed after exhausted `3/3` attempts. Target-device/
live-provider/network compatibility remains `DEFERRED`, with no runtime `PASS`
claim.

## W31 Main Display physical traceability note

`TASK-034-T3-FT-001-W31` is `done` under its already-recorded scheduler
decision after executor `PASS_FOR_HANDOFF`, independent functional `PASS` and
T3 `semantic-pass`. Fresh physical TECNO LI6 RED/GREEN at `2460×1080` proves
the complete dominant `HH:mm`, reduced secondary weather illustrations, stable
four-slot composition and separate timer controls; host geometry at `2460×1080`
and `1280×720` remains supporting evidence only. The existing RTM lifecycles
for REQ-001, REQ-002, REQ-005 and REQ-023 remain `implemented`; no REQ row is
promoted to `verified`, and no feature/epic closure is inferred. Physical
audio audibility, live provider refresh, other resolutions/devices and
custom-ROM rendering remain residual risks outside W31.

## W34 Main Display mixed-state traceability note

`TASK-037-T3-FT-001-W34` is `done` under its already-recorded scheduler
decision after executor `PASS_FOR_HANDOFF`, independent functional `PASS`, T3
`semantic-pass` and fresh physical TECNO LI6 RED/GREEN at `2460×1080`.
Claim-linked host and native View receipts prove the empty-Yesterday/
three-populated mixed state, accepted 25–30% weather band, 70–75% clock zone,
equal card heights and common bottom alignment for `REQ-001`, `REQ-002`,
`REQ-005` and `REQ-023`. Existing RTM lifecycles remain `implemented`; no REQ
row is promoted to `verified`, and no FT-001/EP-001 closure or promotion is
inferred. W31 `done`, W32 `failed`, W33 `blocked` and W33's superseded
policy-invalid transition history remain unchanged. Timer-digit sizing remains
a separate FT-007 presentation residual.
