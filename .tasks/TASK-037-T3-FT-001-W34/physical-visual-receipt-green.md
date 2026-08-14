---
task_id: TASK-037-T3-FT-001-W34
attempt: 1
status: current
stage: physical-green
---
# Physical visual receipt — GREEN

Target: the same unlocked TECNO LI6 serial `1156725456009666`; no other
serial/device route was used.

## Install and runtime

- Clean rebuilt APK: `app/build/outputs/apk/debug/app-debug.apk`.
- APK SHA-256: `8ca63377907ce59c6f127680d0ad7308cab3160e0af06e8e6c9fadf6f82547fc`.
- `adb -s 1156725456009666 install -r ...` → `Success`.
- Launch: `com.hozayushka.app/.app.MainActivity`.
- Screenshot: [physical-green.png](physical-green.png), SHA-256
  `140e41b06afa9d4ffa90a5697fffbc555a83d2b210e256b356d94d6883528a5a`,
  dimensions `2460x1080`.
- `dumpsys activity top` → `mResumed=true`, `mHasWindowFocus=true`,
  fullscreen app frame `2460x1080`; display orientation remains landscape.
- Window policy → screen ON, interactive/awake, keyguard `showing=false`,
  `inputRestricted=false`.

## Measured native View bounds

Raw bounds from `physical-green-activity-top-2.txt`:

- Yesterday: `(32,754)-(527,1056)`, `495x302`.
- Today: `(551,754)-(1170,1056)`, `619x302`.
- Tomorrow: `(1194,754)-(1689,1056)`, `495x302`.
- Day-after: `(1713,754)-(2208,1056)`, `495x302`.

The four cards share one bottom band `754..1056`; height spread and bottom
spread are both `0`. The screenshot visibly retains empty Yesterday, populated
dates `14/15/16`, complete `03:05` HH:mm, city/date above Yesterday and the
three separate timer controls on the right. No clipping or overlap is visible.

## GREEN result

`GREEN`: the corrected installed app uses equal-height/common-bottom cards in
the accepted 25–30% band, with the 70–75% clock zone and preserved composition.
