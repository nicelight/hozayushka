---
description: Canonical FT-002 display-ready weather card, palette and pressure-arrow presentation contract.
status: active
last_updated: 2026-08-06
source_of_truth: .memory-bank/prd.md, .memory-bank/features/FT-002-weather-cards-context.md, .memory-bank/analysis/brainstorming/BR-001.md, operator confirmation 2026-08-06
---
# Weather Card Presentation

## Display-ready card contract

Weather Context supplies Main Display exactly four ordered card projections:
`yesterday`, `today`, `tomorrow`, `day_after`. `today` uses the accepted larger
variant; the other three use the equal smaller variant. A filled card contains
date, temperature, illustration input and temperature background, but no
textual day or weather-condition label. A stale projection retains card/date
geometry while removing values, illustration and arrows.

## Temperature and glass rules

- The temperature sign is present only for values from −4 through +4 °C.
- The single compile-time palette contains all 78 accepted HEX values for
  −30…+47 °C; values outside the range clamp to the nearest endpoint.
- Temperature and pressure arrows use the same static pseudo-glass material;
  no realtime blur, refraction or dynamic highlight is introduced.
- Selected-city API timezone controls weather date and day/night choice. Moon
  phase is used only when supplied; otherwise the regular-moon asset is used.

## Personalization preview

Settings preview is governed by the same production card material and
composition rules. It uses Today temperature when present, otherwise `24 °C`,
and renders two overlapping pressure arrows with the temperature number.
Glass intensity `0`, `0.45` and `1` visibly change this same static
pseudo-glass material; preview rendering performs no network request. The
validated glass-personalization projection is persisted by Settings & Location
and consumed by Main Display through the existing
[Main Display → Settings & Location](capability-interfaces.md#main-display-to-settings-and-location)
contract. Main Display supplies Today temperature from its existing Weather
Context projection, or the accepted `24 °C` fallback, so this visual contract
does not authorize a Settings → Weather Context edge or a private-storage
read. Production Today and Settings preview use the same saved projection.

## Pressure trend and fallback rules

The current trend compares approximately three hours of local history: 0–1.5
mmHg yields no arrow, 1.6–3 yields one, and above 3 yields two. When the
three-hour change is exactly zero, the approximately twelve-hour comparison
produces exactly one arrow for any non-zero change. Yesterday uses the largest
registered change from the previous day. Missing history yields no arrow.
Unknown conditions resolve to the neutral cloud illustration while preserving
available temperature and palette data.

## Verification target

Deterministic host probes and redacted fixtures MUST observe ordered projection,
fresh/stale/first-run states, all 78 palette entries plus endpoint clamp,
day/night and moon fallback, pressure thresholds, and unknown-condition
fallback without a crash or invented text. The probe must also demonstrate that
Main Display reads the projection through the accepted capability boundary and
does not write Weather Context state.
