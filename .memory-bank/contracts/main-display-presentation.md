---
description: Canonical Main Display composition, normalized geometry and visual-QA contract.
status: active
last_updated: 2026-08-14
source_of_truth: .memory-bank/prd.md, .memory-bank/requirements.md, .memory-bank/features/FT-001-main-clock-display.md, operator visual references and decision 2026-08-14
---
# Main Display Presentation

## Scope and ownership

Main Display owns the landscape shell geometry and visual composition. It
consumes the existing Weather Context read projection and Timer & Alert
presentation through the registered capability contracts. It MUST NOT own or
change weather refresh, normalization, cache/history, provider selection,
timer lifecycle, countdown arithmetic, audio policy or runtime window policy.

Weather Card Presentation remains the authority for card data, temperature,
day/night, palette, glass and pressure semantics. This contract constrains
only the shell geometry and the relative visual footprint of those projected
elements.

## Normalized composition contract

Let W and H be the captured fullscreen landscape frame width and height; all
measurements use the same frame and its pixel coordinate system. The screen
MUST be composed as four regions:

1. left info column: city and date above the yesterday card;
2. central clock hero: complete device-time HH:mm in the free upper/central
   region;
3. bottom weather band: four cards in the order yesterday, today, tomorrow,
   day_after;
4. right timer rail: three separate circular transparent preset controls.

The horizontal regions and their relationships are product rules; their exact
column widths, rail width, center offset, bottom inset and card-gap values are
not product targets. Reviewers record the raw bounds and use the relational
claims below to decide whether the composition is legible and non-overlapping.

## Normative MUST / MUST NOT rules

### Band and card geometry

- MUST allocate the weather-card band 25%..30% of total landscape frame
  height. Measure band_ratio = (max(card.bottom) - min(card.top)) / H and
  report the raw band bounds.
- MUST allocate the remaining 70%..75% of frame height to the clock zone
  above the band; clock_zone_ratio = 1 - band_ratio is reported alongside
  the raw band measurement.
- MUST give all four weather cards equal measured height and a common bottom
  alignment. A reviewer MAY apply `raster_measurement_tolerance = max(2 px,
  0.005H)` only to account for integer rasterization when comparing raw
  bounds; this tolerance is not a product target.
- MUST keep all four cards visible and ordered as
  yesterday/today/tomorrow/day_after in NO_DATA, partial/async and populated
  redacted-fixture states. Missing data MUST preserve the shell and MUST NOT
  synthesize values.
- MUST NOT make Yesterday taller than any other card. A wider or denser Today
  presentation remains a content/allocation choice only if its shell height
  still satisfies the equal-height rule.

### Clock hero

- MUST render the complete HH:mm, including both digits on each side of the
  colon; seconds MUST NOT be introduced.
- MUST keep the clock entirely inside the central/upper free region: every
  rendered ink/glow bound MUST be inside the clock zone and MUST NOT overlap the
  left column, weather cards or timer rail.
- MUST use the maximum available clock size for that region without clipping
  or overlap. Review records the raw clock bounds against the available
  central/upper region and evaluates maximum-fit qualitatively; no derived
  size ratio is a product gate.
- MUST keep the clock in the central/upper region above the weather band and
  visually centered between the left information column and right timer rail;
  exact center offsets are review measurements, not product targets.
- MUST NOT let weather cards, illustrations, temperatures or timer controls
  exceed the clock's visual focal priority.

### Weather illustration hierarchy

- MUST keep each non-empty weather illustration materially secondary to the
  card's temperature/content and to the clock. Review records raw painted
  bounds and area for comparison, but no fixed footprint or area ratio is a
  product gate. An empty/NO_DATA card has no illustration claim to measure.
- MUST NOT allow an illustration to be the largest or dominant painted object
  inside its card; temperature and card surface remain visually primary for
  weather content.

### Timer rail

- MUST keep the three timer controls separate, circular and transparent, with
  their existing order, labels, colors, selected/active styling and touch
  routing.
- MUST use one-color radial neon rim/glow per control, anchored to that
  control's existing preset color, with visible spacing between controls.
  Review records raw width, height and gap measurements; no fixed circle-error
  or pairwise-gap value is a product gate. If raster rounding is reported, use
  only the declared raster measurement tolerance.
- MUST NOT allow timer controls to merge into cards, the clock hero or each
  other, and MUST NOT change Timer & Alert behavior.

### Ownership and runtime stability

- MUST preserve the existing Main Display → Weather Context and Main Display
  → Timer & Alert read/command boundaries.
- MUST preserve fullscreen/landscape policy, device-time/date semantics,
  colon states, weather provider/cache/freshness behavior and timer lifecycle.
- MUST NOT add a provider/runtime path, dependency, resource/asset pipeline,
  public contract, module, graph edge or composition-root orchestration.

## Measurement and tolerance method

The reviewer captures a deterministic frame at exactly 2460×1080 and 1280×720,
records raw pixel bounds for the screen, four cards, city/date, clock ink/glow,
each illustration, temperature text and three controls, then derives the
accepted band/clock ratios from those raw values. Bounds are measured from the
same render path and same fixture state; anti-aliased glow is either included
consistently in all focal bounds or excluded consistently, with the choice
recorded. Exact horizontal proportions, center offsets, illustration ratios,
clock-size ratios and control-gap values remain non-normative review
measurements. The only numeric tolerance is
`raster_measurement_tolerance = max(2 px, 0.005H)`, used solely for raster
rounding when explicitly declared. Evidence reports raw measurements,
qualitative/relational verdicts, any declared raster tolerance and an artifact
locator; it must not convert a heuristic into a product gate.

## Visual-QA rubric

The named reviewer rubric records PASS/FAIL and decisive evidence for:

| Criterion | PASS comparison |
|---|---|
| Macro composition | Four regions are visually legible; city/date left, clock central/upper, cards bottom, controls right. |
| Band ratio | band_ratio is 0.25..0.30; clock_zone_ratio is 0.70..0.75. |
| Equal cards | Four heights are equal and four bottoms share one alignment; the reviewer may declare only the raster measurement tolerance; Yesterday is never taller. |
| Clock focal hierarchy | Complete HH:mm is maximum-fit in the central/upper region, with no clipping/overlap; raw bounds support the qualitative/relational verdict. |
| Weather scale | Populated illustrations are materially secondary to temperature/card content and the clock; raw bounds/area are recorded for review. |
| Timer rail | Three distinct circular controls with transparent interiors, one-color radial rim/glow, visible separation and existing order/labels preserved; raw bounds/gaps are recorded. |
| State stability | NO_DATA, partial/async and populated redacted states keep the same four slots and macro composition. |
| Ownership/no drift | Static diff and focused regressions show no Weather Context/provider/timer/runtime behavior changes. |

## Claim-linked evidence

The implementation task MUST capture honest pre-write RED for the macro
composition at both sizes, then claim-equivalent GREEN after the bounded Main
Display change. Evidence MUST include a machine-readable geometry receipt,
same-size RED/GREEN contact sheet, state matrix, named rubric and boundary
review. A physical/runtime PASS is not inferred from host evidence. If the
operator's upload pause remains active, the later route stops immediately
before adb install; physical evidence is recorded as DEFERRED until a separate
authorization releases that pause.
