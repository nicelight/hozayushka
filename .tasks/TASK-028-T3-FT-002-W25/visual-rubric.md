---
description: Executor visual and semantic rubric for TASK-028-T3-FT-002-W25 evidence.
status: supporting
task_id: TASK-028-T3-FT-002-W25
attempt: 1
---
# W25 visual rubric

The rubric is applied to the deterministic host contact sheets at the accepted
223×444 row-card and 279×444 Today scales. It is supporting executor evidence;
independent `/verify` and T3 `/red-verify` remain the closure gates.

| Criterion | Observation | Evidence |
|---|---|---|
| Six-state preservation | Pass: exactly CLEAR, CLOUD, NEUTRAL_CLOUD, RAIN, SNOW and MOON; no partly-cloudy state | `illustration-contact-sheet.png`, `illustration-bounds.json` |
| Reduced painted envelope | Pass: conservative final envelope is 70% of each matching RED envelope in both dimensions, therefore ≤90% | `illustration-bounds.json` |
| CLEAR sun disk | Pass: `0.70 × 0.32 / 0.19 = 1.1789474×`, inside 1.15–1.30× contract, while rays stay inside reduced envelope | `illustration-bounds.json`, contact sheet |
| Recognizability/contrast | Pass: sun/rays, cloud lobes, rain marks, snow marks and moon remain distinct with the existing palette | `illustration-contact-sheet.png` |
| Content non-overlap | Pass: reduced illustration envelopes stay above existing temperature, pressure and date bounds | `illustration-bounds.json`, focused test |
| Pressure UP/DOWN count | Pass: one/two measured arrows appear for supplied UP/DOWN direction/count; separate shaft and head paths are visible | `pressure-arrow-contact-sheet.png`, `pressure-arrow-bounds.json` |
| Pressure zero/steady | Pass: count 0 produces no pressure child and no painted arrow pixels | `pressure-arrow-bounds.json`, focused test |
| Stroke contract | Pass: 5 px stroke at row-card geometry, within 4–8 px; round cap/join | `pressure-arrow-bounds.json`, source inspection |
| Text/emoji absence | Pass: Main Display illustration and pressure routes use Canvas/Path; forecast-only Unicode helper remains separate | `boundary-static-review.md`, source inspection |

## Deferred target route

Samsung GT-I9300I Android 11 custom-ROM 1280×720 readability, fullscreen,
keep-screen-on and runtime Canvas compatibility are `DEFERRED`. No host image or
static result is promoted to runtime/device `PASS`.
