---
description: Fresh claim-linked RED/GREEN evidence for Main Display weather illustrations.
status: active
task_id: TASK-025-T3-FT-002-W22
attempt: 1
---
# Illustration RED/GREEN

## Fresh RED — before production change

Observed from the current pre-change source before any W22 production/test
write:

- `DisplayCapability.kt:1288-1331` builds the Main Display `weatherCard` with
  only temperature, calendar date and pressure-arrow `TextView`s. It never
  reads `projection.illustration` or `projection.moonPhase`.
- `DisplayCapability.kt:1340-1376` contains the separate forecast-card Unicode
  helper, but that path is not Main Display evidence and is outside this
  task's owned visual surface.
- The existing deterministic W21 geometry at 1280×720 is four ordered cards
  with bounds `(32,252)-(255,696)`, `(271,252)-(550,696)`,
  `(566,252)-(789,696)`, `(805,252)-(1028,696)` and widths `223,279,223,223`.
- The baseline render [illustration-red-baseline.svg](illustration-red-baseline.svg)
  shows the four card slots with empty illustration areas and no condition
  silhouette. It is a deterministic host-equivalent pre-change render, not
  W3/W17/W20 evidence.

RED result: the Main Display four-card composition had no rendered condition
illustration despite the display-ready projection carrying `WeatherIllustration`
and optional `moonPhase`; this is the exact task-owned gap.

## Claim mapping

- `FT-002-AC-009 / REQ-005, REQ-022, REQ-023, REQ-026`: current no-illustration
  Main Display source and baseline render are the fresh RED.
- `REQ-005`, `REQ-022`, `REQ-026` stale/empty, order, day/night and fallback
  regression paths use claim-equivalent host tests/static inspection because
  deliberately breaking Weather Context ownership is forbidden by this task.
- `REQ-007`, `REQ-008`, `REQ-025`, `REQ-029` and resource/secret/network safety
  use accepted not-applicable alternative proof; a meaningful RED would
  require an unauthorized provider/state/lifecycle/resource change.

## GREEN — fresh after implementation

- `DisplayCapability.kt` now draws the existing `WeatherIllustration` input with
  an in-card `View` using only `Canvas`, `Path` and `Paint`: CLEAR sun with
  detached rays; CLOUD and NEUTRAL_CLOUD cloud silhouettes; RAIN cloud with
  three marks; SNOW cloud with three snowflake marks; MOON with supplied phase
  shaping and regular fallback for null/`regular`.
- `WeatherCardContentGeometry` reserves a top illustration envelope and
  separate temperature/date/pressure envelopes. Focused host assertions pass
  that the illustration envelope does not intersect any of the three content
  envelopes at 223×444 row-card geometry.
- Illustration is created only when the existing projection has a non-null
  illustration; NO_DATA/stale empty projections therefore keep the existing
  date-only empty card semantics.
- Fresh rendered evidence: [illustration-contact-sheet.png](illustration-contact-sheet.png)
  (source [SVG](illustration-contact-sheet.svg)); measured result:
  [illustration-bounds.json](illustration-bounds.json); rubric:
  [illustration-review.md](illustration-review.md).
- Required host/build/static gates are recorded in
  [host-gates.md](host-gates.md); boundary/resource/provider isolation is in
  [boundary-resource-review.md](boundary-resource-review.md).

GREEN result: claim-equivalent host/static evidence passes for the accepted
visual delta; target-device runtime evidence remains explicitly `DEFERRED`.
