# Named visual-QA rubric — Attempt 1

Reviewer role: `Reviewer/visual-QA` (host artifact review).
Artifact: `red-green-contact-sheet.svg`; decisive measurements:
`geometry.json`.

| Criterion | Result | Decisive observation |
|---|---|---|
| Clock is the first focal point | PASS | GREEN idle text size `188.75` exceeds RED `176`; it remains in the central/upper region above the card row. |
| Adaptive central occupation | PASS | `1280×720` uses `188.75`; bounded `1024×600` adapts to `139.75`; both remain positive and above the weather row. |
| No clock clipping/overlap in host model | PASS | Clock bounds end at the weather-row top; both measured layouts preserve the central/upper boundary. |
| Three right controls remain equal circles | PASS | GREEN target bounds are all `200×200`, radii all `100`; alternate bounds are all `160×160`, radii all `80`. |
| Preset spacing is more spacious | PASS | GREEN control gaps are `24/24`, versus fresh RED `4/4`; controls remain right-side and ordered. |
| Transparent interiors | PASS | GREEN implementation uses transparent oval background; contact sheet shows the display background through each interior. |
| Per-preset neon gradient distinction | PASS | Canvas `LinearGradient` borders are distinct orange/pink/purple identities; no fixed gradient positions/stops are selected. |
| Existing preset identity/order/active styling | PASS | `TimerPresetSlot.entries`, labels, `PresetPresentation.outlineColors`, alpha selection and active-width distinction remain unchanged. |
| Four-card order/content contract | PASS | Geometry and focused tests retain `yesterday/today/tomorrow/day_after`; binding still consumes the existing projection and `weatherCard` inputs. |
| Card relative allocation | PASS | GREEN widths `217/273/217/217`: Yesterday/Tomorrow/Day-after equal, Today larger; observed reduction is `20.51%` relative to Today, reported relationally. |
| Common card gap | PASS | All GREEN gaps are `24`, greater than RED `16`; no per-card exception was introduced. |
| Readability/lightweight static treatment | PASS | Existing static Android primitives only; no realtime blur, refraction, asset, resource or animation path was added. Target runtime remains `DEFERRED`. |
