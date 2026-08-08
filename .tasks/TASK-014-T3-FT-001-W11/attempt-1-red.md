# Attempt 1 — claim-linked RED

- Captured: `2026-08-08T14:46:28+05:00`
- Production source state: pre-TASK-014 hashes recorded in protocol `context.md`.
- Built APK: `app/build/outputs/apk/debug/app-debug.apk`, SHA-256 `f8c77d190c906f419f5f3bff4b50b3d47be0ad521ecd101b794beeae2d5aae8f`.
- Installed identity: `versionCode=1`, `versionName=0.1-foundation`.
- Runtime: serial `emulator-5554`, AVD `Tecno_Pova_6_API_35`, Google `sdk_gphone64_x86_64` / `emu64xa`, API 35 / Android 15 / x86_64, physical `1080x2436` at 393 dpi, app landscape bounds `2436x1080`, boot complete.

## Setup and safe state

1. `./gradlew assembleDebug` → exit 0, `BUILD SUCCESSFUL`.
2. `/home/serg/Android/Sdk/platform-tools/adb -s emulator-5554 install -r app/build/outputs/apk/debug/app-debug.apk` → `Success`.
3. `/home/serg/Android/Sdk/platform-tools/adb -s emulator-5554 shell am start -W -S -n com.hozayushka.app/.app.MainActivity` → `Status: ok`, `LaunchState: COLD`.
4. Cold start exposed an inherited synthetic overdue overlay; `.tasks/TASK-014-T3-FT-001-W11/setup-inherited-overdue.png` records it. One public overdue-dismiss tap at `1218,540` returned to Main Display and timer idle. No private state or credential was read or mutated.

## RED — FT-001-AC-002 / REQ-002 / REQ-023

Command:

`/home/serg/Android/Sdk/platform-tools/adb -s emulator-5554 shell dumpsys activity top` (task-owned compact hierarchy transcribed below)

Observed Main Display bounds:

```text
MainActivity:       0,0-2436,1080
main shell:         32,24-2184,1056
header:             0,0-2152,516
clock row:          0,0-2152,431
date:               0,431-2152,516
city gesture view:  0,516-2152,516  height=0
timer hint view:    0,516-2152,516  height=0
forecast message:   0,516-2152,516  height=0
weather row:        0,516-2152,1032
preset buttons:     20,8-220,336; 20,352-220,680; 20,696-220,1024
```

The visible clock/date, exactly four weather-card positions and three presets remain present, but the selected city is absent. Screenshot: `red-main.png`, SHA-256 `67b2f3f0b2fc9aaed1412e1d7a543227077b8da5ba5631fd9809cfb6eddb13fb`.

## RED — FT-001-AC-005 / REQ-004

- The city view has no touch area (`bottom == top == 516`).
- Public input probe: `/home/serg/Android/Sdk/platform-tools/adb -s emulator-5554 shell input swipe 300 540 300 540 800` on the absolute collapsed-row line.
- After the 800 ms hold, `dumpsys activity activities` still reported `topResumedActivity=...com.hozayushka.app/.app.MainActivity`, and `dumpsys window` still reported that MainActivity as current/focused. Settings did not open.

This is an honest behavior/layout RED from the current source and current installed APK. It predates the first TASK-014 production change and is not a setup, syntax, artificial, or unrelated failure.
