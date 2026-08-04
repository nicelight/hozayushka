---
description: Требования (REQ-IDs) + traceability matrix (RTM).
status: draft
last_updated: 2026-08-03
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
  build/start/test/smoke/compatibility evidence. Foundation MUST NOT introduce
  product behavior beyond the walking skeleton or any live user API key.
  Sources: `.memory-bank/foundation.md`, `.memory-bank/architecture/system-architecture.md`,
  `.memory-bank/testing/runtime-verification.md`.

### Main display and clock

- **REQ-001** — Main display MUST run in landscape fullscreen, hide system
  panels and keep the screen on while the app is open. Source: `PRD-FR-001`.
- **REQ-002** — Main display MUST show dominant `HH:mm` clock without seconds,
  city/date on the left, four weather cards along the lower-left area and three
  preset buttons on the right; date uses `dd` plus Russian genitive month and no
  year/weekday. Sources: `PRD-FR-002`, `PRD-FR-005`, `PRD-AC-001`.
- **REQ-003** — Clock colon MUST use the accepted online pulse, offline fixed
  brightness and active-timer blink behaviors. Sources: `PRD-FR-003`–`PRD-FR-004`.
- **REQ-004** — City interaction MUST follow the accepted hold/open-Settings,
  no-city tap/open-Settings and selected-city short-tap/no-op rules. Source:
  `PRD-FR-006`.

### Weather context and forecast

- **REQ-005** — Main weather cards MUST preserve the fixed yesterday/today/
  tomorrow/day-after order, accepted relative sizing and card content/day-night
  presentation without textual day/weather labels. Sources: `PRD-FR-007`,
  `PRD-FR-008`, `PRD-FR-009`, `PRD-FR-010`, `PRD-AC-002`.
- **REQ-006** — Temperature presentation MUST apply the −4…+4 sign rule, all
  78 explicit BR-001 HEX values with endpoint clamp, and the accepted static
  pseudo-glass material. Sources: `PRD-FR-009`, `PRD-FR-011`–`PRD-FR-012`,
  `PRD-AC-003`.
- **REQ-007** — Weather/current pressure MUST refresh after launch, city change
  and every 30 minutes when network is available; the last successful result
  MUST be cached, usable offline through 24 hours, then render all four cards as
  empty transparent contours. Sources: `PRD-FR-013`–`PRD-FR-014`.
- **REQ-008** — Local weather history MUST start at installation, retain the
  accepted seven-day window, calculate current and yesterday pressure trends by
  the accepted thresholds, and keep first-run yesterday stable as a dated empty
  contour. Sources: `PRD-FR-015`, `PRD-FR-016`, `PRD-FR-017`, `PRD-FR-018`.
- **REQ-009** — Hourly forecast MUST open from Today only when data exists and
  show the eight accepted slots in a 2×4 layout with shared forecast exit flow;
  otherwise it stays on main display with the accepted message. Sources:
  `PRD-FR-019A`, `PRD-FR-019B`, `PRD-FR-019C`, `PRD-FR-022`, `PRD-AC-007A`.
- **REQ-010** — Long-term forecast MUST open from Tomorrow or Day-after only
  when data exists and show the selected-city today plus nine following days in
  a 2×5 layout with shared exit flow; otherwise it stays on main display with the
  accepted message. Sources: `PRD-FR-019`, `PRD-FR-020`, `PRD-FR-021`,
  `PRD-FR-022`, `PRD-AC-007`.

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

- **REQ-017** — Settings MUST support a personal API key, default Khujand
  location, selected country/city coordinates and refresh behavior after a city
  change. Sources: `PRD-FR-032`, `PRD-FR-034`.
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
  host-side require the accepted device verification route. Sources:
  `PRD-NFR-001`, `PRD-NFR-002`, `PRD-NFR-003`, `PRD-NFR-004`, `PRD-NFR-005`,
  `PRD-NFR-006`, `PRD-AC-001`, `PRD-AC-003`.
- **REQ-024** — The user-provided API key MUST remain local and MUST NOT appear
  in APK contents, source, logs or verification evidence. Sources: `PRD-FR-033`,
  `PRD-AC-006`, Constitution Product Non-Negotiables.
- **REQ-025** — Network or weather-service unavailability MUST not break the
  clock, timer lifecycle, cancellation or overdue dismissal; fresh cached
  weather remains available offline under REQ-007. Sources: `PRD-NFR-004`,
  `PRD-AC-008`, `Edge Cases / Failure Handling`.
- **REQ-026** — Unknown weather conditions or missing optional fields MUST not
  crash or invent text; use the neutral cloud fallback while preserving available
  temperature/color, and do not open forecast views without required data.
  Sources: `PRD-FR-010`, `PRD-FR-022`, `Edge Cases / Failure Handling`,
  `PRD-AC-007`, `PRD-AC-010`.

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

## Traceability (RTM)
| REQ | Epic | Feature | Test | Lifecycle |
|---|---|---|---|---|
| REQ-000 | Foundation | FT-000 | Foundation Exit Criteria | planned |
| REQ-001 | EP-001 | FT-001 | PRD-AC-001 | planned |
| REQ-002 | EP-001 | FT-001 | PRD-AC-001 | planned |
| REQ-003 | EP-001 | FT-001 | PRD-FR-003/004 | planned |
| REQ-004 | EP-001 | FT-001 | PRD-FR-006 | planned |
| REQ-005 | EP-002 | FT-002 | PRD-AC-002 | planned |
| REQ-006 | EP-002 | FT-002 | PRD-AC-003 | planned |
| REQ-007 | EP-002 | FT-002 | PRD-FR-013/014 | planned |
| REQ-008 | EP-002 | FT-002 | PRD-FR-015/018 | planned |
| REQ-009 | EP-002 | FT-003 | PRD-AC-007A | planned |
| REQ-010 | EP-002 | FT-004 | PRD-AC-007 | planned |
| REQ-011 | EP-003 | FT-005 | PRD-FR-023/025 | planned |
| REQ-012 | EP-003 | FT-006 | PRD-AC-004 | planned |
| REQ-013 | EP-003 | FT-006 | PRD-FR-028 | planned |
| REQ-014 | EP-003 | FT-006 | PRD-AC-005 | planned |
| REQ-015 | EP-003 | FT-007 | PRD-FR-029/030 | planned |
| REQ-016 | EP-003 | FT-007 | PRD-AC-005 | planned |
| REQ-017 | EP-004 | FT-008 | PRD-FR-032/034 | planned |
| REQ-018 | EP-004 | FT-008 | PRD-AC-006B | planned |
| REQ-019 | EP-004 | FT-009 | PRD-AC-006/006C | planned |
| REQ-020 | EP-004 | FT-009 | PRD-AC-003/006 | planned |
| REQ-021 | EP-004 | FT-009 | PRD-AC-006A/006C | planned |
| REQ-022 | EP-001 | FT-001 | PRD-AC-009 | planned |
| REQ-023 | EP-001 | FT-001 | PRD-AC-001/003 | planned |
| REQ-024 | EP-004 | FT-008 | PRD-AC-006 | planned |
| REQ-025 | EP-003 | FT-006 | PRD-AC-008 | planned |
| REQ-026 | EP-002 | FT-002 | PRD-AC-007/010 | planned |
