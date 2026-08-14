---
description: Fresh claim-linked RED/GREEN evidence for TASK-028-T3-FT-002-W25 visual correction.
status: active
task_id: TASK-028-T3-FT-002-W25
attempt: 1
---
# Illustration RED/GREEN — W25

## Fresh RED — before production/test writes

Observed from the current W25 pre-change source:

- `DisplayCapability.kt:1631-1638` places each existing illustration View in
  the complete `WeatherCardContentGeometry.illustrationBounds`; the W22 drawing
  receives the full local view bounds, so the six painted compositions remain
  at the oversized W22 envelope.
- `WeatherIllustrationCanvas.draw` dispatches the six existing states, but the
  current dimensions use the full local bounds; the current CLEAR sun uses
  `radius = min(bounds.width, bounds.height) * 0.19f` and the current cloud/rain/
  snow/moon primitives are not reduced inside that envelope.
- `DisplayCapability.kt:1657-1667` creates one `TextView` per clamped pressure
  arrow and uses Unicode `"↑"` for UP and `"↓"` otherwise. There is no Canvas/
  Path shaft/head, explicit stroke width, rounded cap/join or pixel-visibility
  measurement. The separate forecast Unicode path is not Main Display scope.
- The current deterministic card geometry remains four ordered cards at
  1280×720 with bounds `(32,252)-(255,696)`, `(271,252)-(550,696)`,
  `(566,252)-(789,696)`, `(805,252)-(1028,696)` and widths `223,279,223,223`.
  At card-local 223×444 the illustration View is `(13,13)-(210,150)` =
  `197×137`; at 279×444 it is `(16,16)-(263,150)` = `247×134`.
- Fresh RED artifacts: [illustration-red-baseline.svg](illustration-red-baseline.svg)
  and [pressure-arrow-red-baseline.svg](pressure-arrow-red-baseline.svg).

RED result: the current visual layer is recognizable but oversized relative to
the requested correction, and pressure trend output is invisible as a measured
Canvas/Path contract because it is Unicode/TextView-based.

## Claim mapping

- `FT-002-AC-009 / REQ-005, REQ-022, REQ-023, REQ-026`: the current full W22
  envelopes and fresh source/baseline render are the honest RED for the
  requested bounds/sun/legibility correction.
- `REQ-008 / weather-card-presentation.md#pressure-trend-and-fallback-rules`:
  current Unicode/TextView pressure output is RED for the renderer-only
  Canvas/Path visibility/stroke proof; calculation, thresholds and history
  remain outside this claim.
- `REQ-005`, `REQ-006`, `REQ-007`, `REQ-025`, `REQ-026`, `REQ-029` regression,
  provider/state/resource/network/timer/audio/lifecycle claims use accepted
  alternative proof because a meaningful RED would require forbidden behavior
  changes.

## GREEN — after implementation

- `WeatherIllustrationCanvas` now centers a `PAINT_SCALE = 0.70f` transform for
  all six existing Canvas/Path/Paint states. The conservative final envelope is
  therefore 69.54–70.15% of the matching fresh RED envelope in both dimensions
  at row and Today geometry, below the required 90% ceiling.
- CLEAR uses `CLEAR_SUN_RADIUS_FACTOR = 0.32f` inside the scaled envelope versus
  the fresh RED `0.19f` factor. The measured diameter ratio is
  `0.70 × 0.32 / 0.19 = 1.1789474`, inside the required 1.15–1.30 range; the
  shortened ray span remains inside the reduced envelope.
- Main Display pressure children are now `PressureArrowView` Canvas views. Each
  draws separate shaft/head `Path` segments with round cap/join and a fixed
  5 px stroke. `PressureArrowCanvas.visibleCount` preserves the existing 0–2
  clamp; count zero creates no child and therefore no arrow pixels. Direction
  is projection-supplied, with the existing null fallback to DOWN preserved.
- Existing `WeatherCardContentGeometry` card-local anchors remain unchanged;
  measured illustration envelopes remain disjoint from temperature/date/pressure
  bounds. Four-card order, Today sizing, six states, moon fallback, empty/stale
  behavior and the separate forecast Unicode path remain unchanged.
- Focused display tests pass after the production/test change. Clean build,
  complete host suite and diff/static checks pass; see `host-gates.md` and
  `boundary-static-review.md`.
- Fresh rendered artifacts: [illustration-contact-sheet.png](illustration-contact-sheet.png),
  [pressure-arrow-contact-sheet.png](pressure-arrow-contact-sheet.png), measured
  [illustration-bounds.json](illustration-bounds.json) and
  [pressure-arrow-bounds.json](pressure-arrow-bounds.json). Independent rubric:
  [visual-rubric.md](visual-rubric.md).

GREEN result: claim-equivalent host/static/image evidence passes for the bounded
W25 visual outcome. Target-device runtime evidence remains explicitly
`DEFERRED`.
