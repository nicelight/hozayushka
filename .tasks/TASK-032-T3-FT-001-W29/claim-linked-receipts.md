---
description: Claim-to-receipt index for TASK-032-T3-FT-001-W29 recovery.
status: supporting
task_id: TASK-032-T3-FT-001-W29
tier: T3
attempt: 2
---
# W29 claim-linked receipts — Attempt 2

This index keeps every W29 task-card evidence item visible. `GREEN
supporting-only` means the current host/static observation exists; it does not
repair the missing historical pre-write RED.

| Claim / obligation | RED | GREEN or alternative proof | Locator |
|---|---|---|---|
| `FT-001-AC-002 / REQ-002`: complete `HH:mm`, bounds and no clipping at both exact sizes | unavailable | `geometry.json`, host-model contact sheet, focused test | `geometry.json`, `red-green-contact-sheet.svg` |
| Four stable slots in order through NO_DATA/async/populated redacted fixture | unavailable | `weather-slot-matrix.json`, focused/full host tests | `weather-slot-matrix.json` |
| Preset order/labels/colors/selected-active/touch plus radial/rim/glow | unavailable | `preset-visual-receipts.json`, source review, focused test | `preset-visual-receipts.json` |
| `REQ-023` named visual-QA rubric | unavailable | host rubric at both sizes; target separation explicit | `visual-rubric.md` |
| `REQ-001` fullscreen/system-panel/keep-screen-on/physical landscape | runtime RED unauthorized | accepted alternative; target/device `DEFERRED` | `target-device.md`, `boundary-static-review.md` |
| `REQ-005` Weather read-only regression | intentional break forbidden | focused/full host regression + static read-only edge | `weather-boundary-regression.md` |
| Timer & Alert read-only regression | intentional break forbidden | focused/full host regression + static handler/owner review | `timer-boundary-regression.md` |
| Target/device deferral | runtime not authorized | `DEFERRED`, no host-to-runtime promotion | `target-device.md` |
| T3 isolation/cleanup and historical-state preservation | historical RED path missing | offline host-only, redacted fixtures, unchanged lifecycle/history | `host-gates.md`, `boundary-static-review.md` |

The exact blocker is recorded in `red-baseline.md`, `progress.md` and
`handoff.md`; no receipt is offered as a `/verify` reuse candidate.
