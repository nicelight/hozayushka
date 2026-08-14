---
task_id: TASK-037-T3-FT-001-W34
attempt: 1
status: current
stage: physical-red
---
# Physical visual receipt — RED

Target: unlocked TECNO LI6, serial `1156725456009666` only. No emulator,
AVD/QEMU, network, provider call or credential was used.

## Device/runtime observation

- `adb -s 1156725456009666 get-state` → `device`.
- `adb -s 1156725456009666 shell wm size` → physical size `1080x2460`.
- `dumpsys display` → active logical frame `2460x1080`,
  `mCurrentOrientation=1`, app frame `2460x1080`.
- `dumpsys activity top` → focused/resumed
  `com.hozayushka.app/.app.MainActivity`, `mResumed=true`,
  `mHasWindowFocus=true`, fullscreen app bounds `0,0-2460,1080`.
- `dumpsys window policy` → `showing=false`, `inputRestricted=false`,
  `screenState=SCREEN_STATE_ON`, `interactiveState=INTERACTIVE_STATE_AWAKE`.
- `uiautomator dump` was unavailable on this ROM (`could not get idle state`);
  the native `dumpsys activity top` View Hierarchy is the allocation receipt.

## RED artifact

- Screenshot: [physical-red.png](physical-red.png)
- SHA-256: `fa487017fbab16dffdc45b0ea388d8ae57844f0e0ab9413054de5fe72d6749b1`
- PNG dimensions: `2460x1080`.
- Visible fixture: empty Yesterday shell; populated dates `14`, `15`, `16`.

## Measured View bounds

Raw bounds from `physical-red-activity-top.txt`:

- Yesterday: `(32,222)-(527,1056)`, `495x834`.
- Today: `(551,754)-(1170,1056)`, `619x302`.
- Tomorrow: `(1194,754)-(1689,1056)`, `495x302`.
- Day-after: `(1713,754)-(2208,1056)`, `495x302`.

The populated cards share the accepted `754..1056` band (`302 / 1080 =
0.27962962`), while empty Yesterday occupies the separate taller weighted
left allocation. The screenshot-painted Yesterday border is approximately
`(32,231)-(529,1048)` because its existing `scaleY` is applied; the View tree
bounds above are the decisive allocation measurement.

## RED result

`RED`: mixed state is present without synthesized Yesterday data, but the
empty Yesterday View is 834px high versus 302px for each populated card.
City/date, complete `HH:mm`, four-slot order and separate timer rail are
visible; no physical PASS is inferred from host evidence.
