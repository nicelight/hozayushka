# TASK-027-T3-FT-001-W24 — reference visual rubric

Reviewer / visual-QA role: `Codex Implementer — named execution visual-QA
reviewer`. This is supporting `/exe` evidence; independent T3 `/verify` and
`/red-verify` remain required and are not replaced by this rubric.

Attempt: 2. Artifact under review: `red-green-contact-sheet.svg`, same-size
`1280×720` RED/GREEN host model plus the fresh reachable-refresh regression.
Decisive numeric source:
`clock-bounds.json`.

| Rubric row | RED observation | GREEN result | Decisive evidence | Result |
|---|---|---|---|---|
| Reachable idle ticker refresh | Attached/resumed refresh path assigned `132f` to hour/colon/minute after the static model reported `176f`. | The same refresh-size selector now applies `176f` in idle and preserves `32f` in countdown. | `clock-bounds.json` `red.reachableRefresh` / `green.reachableRefresh`; focused `DisplayProjectionTest.reachableMainDisplayRefreshKeepsIdleClock176AndCountdown32`. | PASS |
| Clock is the first focal point | Baseline text style `132`; central region is visually comparable to surrounding shell in the RED panel. | Idle `HH:mm` style is `176` versus `132` baseline (`+44`, same central region) and is rendered as the largest text in the panel. | `clock-bounds.json` `red.clock.textStyle=132`, `green.clock.textStyle=176`; SVG `.clock-green`. | PASS |
| Left/central/right anchors remain | City/date, central clock region, lower cards and right controls are present in the same horizontal anchors. | City/date remains above Yesterday at left; clock remains `[271,24,1028,252]` above Today/Tomorrow/Day-after; presets remain right. | `clock-bounds.json`; unchanged card/order assertions in `DisplayProjectionTest.kt`. | PASS |
| Four-card order and relative allocation | W21 card model is `[yesterday,today,tomorrow,day_after]`. | Same order; widths `223/279/223/223`, Today strictly larger, other three equal; gaps `16/16/16`. | `clock-bounds.json` `green.weatherCards`; host GREEN test. | PASS |
| Circular controls and whitespace | Three right controls are `220×224` with radius `18`; radius is below half side and shapes are rounded rectangles. | Three right controls are `220×220`, vertically centered at top `26/250/474`, with `4`-unit inter-control gaps and radius `110`; SVG uses circles and leaves balanced whitespace. | `clock-bounds.json`; `presetCornerRadii=[110,110,110]`; SVG `<circle>`; host GREEN gap assertion. | PASS |
| No clipping or overlap | Baseline model is bounded, but controls are not circular. | Clock remains within central header; controls remain within right shell; no card overlap; bounds are disjoint and fit `1280×720`. | Exact bounds in `clock-bounds.json`; host GREEN assertions. | PASS |
| Lightweight/static treatment | Existing static shell and preset presentation. | Uses existing Android `GradientDrawable.OVAL`; no labels, effects, assets, animation or new visual system added. | Scoped diff: only DisplayCapability/test; no resources/dependencies. | PASS |

Target device: `TARGET_DEVICE=DEFERRED`. Samsung GT-I9300I Android 11
custom-ROM 1280×720 readability/fullscreen/keep-screen-on and actual runtime
circle rendering were not observed, by task restriction. Host SVG/geometry is
not promoted to target-device runtime `PASS`; residual risk remains.
