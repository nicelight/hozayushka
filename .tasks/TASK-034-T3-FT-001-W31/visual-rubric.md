---
task_id: TASK-034-T3-FT-001-W31
stage: visual-qa
reviewer: Implementer self-attested execution rubric
---
# W31 visual rubric

| Criterion | Result | Decisive observation | Locator |
|---|---|---|---|
| Clock dominance/readability | PASS | Complete `HH:mm` is the dominant light focal element; GREEN glyph envelope `725x218`. | `geometry.json` GREEN |
| Full-string containment | PASS | GREEN glyph envelope ends at `y=328`; day-card row starts at `y=405`; no clipping/overlap. | `physical-visual-receipt.md` |
| Reduced icon scale | PASS | Largest measured icon changed from `71x70` to `45x43` px. | `geometry.json` comparison |
| Four-slot stability/order | PASS | `yesterday/today/tomorrow/day_after` shells remain present and ordered in host matrix and physical screenshot. | `weather-slot-matrix.json`, screenshots |
| City/date placement | PASS | GREEN city/date envelope remains in left column above Yesterday. | `physical-visual-receipt.md` |
| Timer separation | PASS | Three control circles remain in right envelope, outside cards. | `geometry.json` GREEN |
| Lightweight rendering | PASS | Only local geometry and canvas scale band changed; no new asset, dependency or effect layer. | `boundary-static-review.md` |
| Host/device separation | PASS | Host geometry is recorded separately; physical GREEN uses only the authorized TECNO serial. | `host-gates.md`, `physical-visual-receipt.md` |

The rubric is execution evidence, not independent `/verify` or `/red-verify`.
