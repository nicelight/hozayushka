---
description: Named host visual-QA rubric for TASK-031-T3-FT-007-W28.
status: supporting
---
# W28 host visual-QA rubric — attempt 1

Role: `visual-QA` host geometry review. This is executor evidence; independent
`/verify` and T3 `/red-verify` remain required. It is not a device/runtime
visual verdict.

Render basis: deterministic `1280×720` comparison in
[`geometry.json`](geometry.json), with the same idle/W27-active/overdue size.

| Criterion | Result | Decisive observation | Evidence |
|---|---|---|---|
| Focal hierarchy | PASS | Overdue elapsed text `256.0` exceeds idle `188.75` and active `228.0`; plus is `280.0`. | `geometry.json`, focused W28 test XML |
| Dedicated surface | PASS | Overdue branch hides `mainShell`; overlay contains only backdrop, plus and elapsed counter. | `boundary-static-review.md` |
| No weather/city/date/card shell | PASS | Main shell is `GONE` while overdue overlay is visible; no card/city/date child is added to overlay. | `DisplayCapability.kt`, `geometry.json` |
| Preset color identity | PASS | Active `SECOND` resolves to `#FF4FA3` for the neon border. | `geometry.json`, W28 focused stdout |
| Transparent circular treatment | PASS | Overlay and backdrop use transparent background; backdrop draws a circle with stroke only. | `DisplayCapability.kt`, `boundary-static-review.md` |
| Blinking plus | PASS | `plusVisibleAt(0)=true`, `382=false`, `764=true`; only alpha changes. | W28 focused test stdout |
| Numeric stability/full value | PASS | `00:10:00` remains unchanged from `600000..600999ms`, then advances at the next second. | W28 focused test |
| Readability / no clipping or overlap | PASS (host geometry) | Plus and elapsed bounds are disjoint; elapsed width/height fit assertions pass. | `geometry.json`, W28 focused test |
| Target/device separation | DEFERRED | No authorized target or runtime observation was used. | `target-device.md` |

Overall host rubric: `PASS` for the bounded geometry/composition criteria;
target fullscreen/readability/custom-ROM behavior remains `DEFERRED`.
