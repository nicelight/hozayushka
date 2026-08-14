---
description: Named host visual-QA rubric for TASK-032-T3-FT-001-W29.
status: supporting
task_id: TASK-032-T3-FT-001-W29
tier: T3
attempt: 2
---
# Named visual-QA rubric — W29 Attempt 2

Reviewer role: `Reviewer/visual-QA` (executor-prepared host/static rubric;
independent re-review remains the next owner). Primary artifacts:
`geometry.json`, `weather-slot-matrix.json`, `preset-visual-receipts.json` and
`red-green-contact-sheet.svg`.

The GREEN column is a deterministic host model/source observation. The RED
column is intentionally `UNAVAILABLE`, not inferred from W26/W28 history.

| Criterion | RED | GREEN host result | Decisive observation / locator |
|---|---|---|---|
| Complete clock readability at `2460x1080` | unavailable | PASS supporting | Full `12:34` model bounds `875x350` fit `1657x350`; `geometry.json` |
| Complete clock readability at `1280x720` | unavailable | PASS supporting | Full `12:34` model bounds `570x228.00002` fit `755x228`; `geometry.json` |
| No clock clipping/overflow and clock above cards | unavailable | PASS supporting | Both measured bottoms equal the weather-row top; no model intersection |
| Four-slot stability / order | unavailable | PASS supporting | All cases emit `yesterday/today/tomorrow/day_after`; four fixed card bounds |
| NO_DATA honesty | unavailable | PASS supporting | Four shells retained; values remain null/empty; no fabricated weather |
| Async/in-flight honesty | unavailable | PASS supporting | One supplied `today` projection remains today; absent slots remain absent |
| Populated redacted fixture | unavailable | PASS supporting | Four redacted `21 °C` projections restore by slot order despite reversed input |
| Preset identity/order/labels/colors | unavailable | PASS supporting | `FIRST/SECOND/THIRD`, existing orange/pink/purple colors and Button path retained |
| Radial preset-color shade identity | unavailable | PASS supporting | One `RadialGradient` with highlight/base/shade derived from each base color |
| Materially wider rim | unavailable | PASS supporting | `10px` base / `12px` active vs documented 7px historical baseline; source receipt |
| Static outward fading glow | unavailable | PASS supporting | Three fixed stroke layers, 5/10/15px spread at side 200, decreasing alpha |
| Lightweight rendering | unavailable | PASS supporting | Canvas primitives only; no realtime animation, blur, asset or dependency |
| Host/device evidence separation | n/a runtime RED unauthorized | PASS separation / target DEFERRED | No host result is called device PASS; see `target-device.md` |

## Rubric limitation

The rubric is complete as an evidence map, but the missing pre-write RED means
it cannot independently establish a claim-linked executor RED/GREEN path. It
therefore remains supporting-only and does not authorize task closure.
