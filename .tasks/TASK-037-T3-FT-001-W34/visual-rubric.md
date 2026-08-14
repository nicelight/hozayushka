---
task_id: TASK-037-T3-FT-001-W34
attempt: 1
status: current
---
# Main Display visual rubric

Evidence: `physical-red.png`, `physical-green.png`, native View dumps and
`geometry-red.json` / `geometry-green.json`.

| Criterion | Result | Decisive evidence |
|---|---|---|
| Macro composition | PASS | city/date left above Yesterday, clock central/upper, four cards bottom, three separate controls right |
| Band ratio | PASS | physical `0.27962962`; host `0.27962962` and `0.27916667` |
| Equal cards | PASS after correction | physical View heights `[302,302,302,302]`, bottoms `[1056,1056,1056,1056]`; host same relational result |
| Clock focal hierarchy | PASS | complete HH:mm visible and contained above band; no clipping/overlap observed |
| Weather scale | PASS | populated illustrations remain secondary to card content and clock |
| Timer rail | PASS | three distinct circular controls remain separate on the right |
| State stability | PASS | empty Yesterday shell plus populated `14/15/16` retained; no data synthesized |
| Ownership/no drift | PASS for execution scope | only two declared behavior files changed by W34; provider/weather/timer/runtime paths not written |

RED establishes the prior defect: empty Yesterday height `834` versus `302`
for each populated card. GREEN establishes the corrected shared allocation.
