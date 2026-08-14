---
task_id: TASK-034-T3-FT-001-W31
attempt: 1
stage: physical-red
status: current
---
# Physical visual receipt — RED

Target: unlocked TECNO LI6, serial `1156725456009666` only.

## Device/runtime observation

- `adb -s 1156725456009666 get-state` → `device`.
- Model → `TECNO LI6`.
- `wm size` → `Physical size: 1080x2460`.
- Runtime landscape app frame → `2460x1080`; `dumpsys display` reported
  `mCurrentOrientation=1`, `logicalFrame=Rect(0, 0 - 2460, 1080)` and
  `mOverrideDisplayInfo ... real 2460 x 1080`.
- `dumpsys window` → focused
  `com.hozayushka.app/com.hozayushka.app.app.MainActivity`; screen state ON;
  `mShowingDream=false`, `mDreamingLockscreen=false`; no lockscreen window was
  focused. Full-screen capture contains no status/navigation panels.
- `adb shell cmd window is-keyguard-locked` is unsupported on this build and
  returned `Unknown command`; it is not used as unlock evidence.

## RED artifact

- Screenshot: [physical-main-before.png](physical-main-before.png)
- SHA-256: `8480037b45b393a928111393dab82dca99f68e0ce138ac46c3ffb5db5a0ab2da`
- PNG dimensions/full app bounds: `2460x1080`.

## Measured bounds (screenshot pixels)

- Complete visible `HH:mm` glyph envelope: `(1051,103)-(1701,304)`,
  `650x201`; full clock region inferred from the app composition is above the
  three cards, but the rendered glyph is not using the available central area
  strongly enough for the requested physical focal hierarchy.
- City/date envelope: `(89,48)-(465,195)`; left column preserved above the
  Yesterday shell.
- Weather card shells: Yesterday `(32,231)-(529,1048)`; Today
  `(553,374)-(1168,1056)`; Tomorrow `(1196,383)-(1687,1047)`; Day-after
  `(1715,382)-(2206,1048)`. The first shell has no populated illustration in
  this capture, while the other three remain visible.
- Visible weather illustration footprints: Today `(827,459)-(894,529)`
  (`67x70`), Tomorrow `(1406,468)-(1477,537)` (`71x69`), Day-after
  `(1925,468)-(1996,537)` (`71x69`).
- Timer-control visual regions remain separate on the right; pale label/rim
  envelope is approximately `(2230,186)-(2428,900)` and does not overlap the
  weather-card shells.

## RED verdict

`RED`: the current physical composition is the pre-correction baseline. The
full clock is contained and readable, but this capture is retained as the
task-specific physical baseline for the required clock-dominance and secondary
weather-icon correction; no production behavior write occurred before this
receipt.

## Fresh GREEN comparison

- Screenshot: [physical-main-after.png](physical-main-after.png)
- SHA-256: `cc742832b4edf6f52194dd207b9e5f71cd6481483ca83adbfc2e1cf63791cf08`
- Same device serial, same recorded landscape frame `2460x1080`, same
  fullscreen composition and focused MainActivity.
- Complete visible `HH:mm` glyph envelope: `(1015,110)-(1740,328)`,
  `725x218`; all glyphs are contained above the day-card row.
- City/date remains `(89,48)-(465,195)` above Yesterday.
- Weather shells: Yesterday `(32,231)-(529,1048)`; Today
  `(551,405)-(1170,1056)`; Tomorrow `(1194,405)-(1689,1056)`; Day-after
  `(1713,405)-(2208,1056)`.
- Weather illustration footprints: Today `(840,467)-(881,510)` (`41x43`),
  Tomorrow `(1419,477)-(1464,520)` (`45x43`), Day-after
  `(1938,477)-(1983,520)` (`45x43`).
- Timer-control envelope: `(2208,186)-(2428,894)`, still separate on the
  right of the weather cards.
- Comparison: clock width/height increased by `75/17 px`; largest visible icon
  width/height decreased by `26/27 px`; central weather row moved down `31 px`
  while retaining all four shells. No clipping or overlap was observed.

GREEN claim result: the complete `HH:mm` is the largest readable contained
element, illustrations are materially reduced and secondary, and the required
city/date, four-slot order and right-side timer separation remain visible.
