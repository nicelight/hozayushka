---
task_id: TASK-037-T3-FT-001-W34
attempt: 1
status: current
---
# View allocation receipt — RED

The project-native host unit harness has no Robolectric/runtime View tree.
The authorized physical `dumpsys activity top` hierarchy exposes the real
allocation and is therefore used for the allocation-level proof.

Current hierarchy evidence in `physical-red-activity-top.txt` shows:

- left parent `32,24-527,1056`;
- Yesterday parent `0,198-495,1032`, with `WeatherCardLayout 0,0-495,834`;
- central cards parent `0,730-1657,1032`, with three `WeatherCardLayout`
  children of height `302`.

This distinguishes the actual separate weighted Yesterday allocation from the
compact populated-card row. The deterministic host alternative is recorded in
`host-red.log` and `mixed-state-matrix-red.json`.

## GREEN allocation

After installing the rebuilt APK on the same serial, the native hierarchy in
`physical-green-activity-top-2.txt` shows the left Yesterday parent at
`0,730-495,1032` with `WeatherCardLayout 0,0-495,302`; the central row has
three children of heights `302`, all ending at the same screen bottom `1056`.
This is the real View allocation receipt for the corrected path. See
`geometry-green.json` and `physical-visual-receipt-green.md`.
