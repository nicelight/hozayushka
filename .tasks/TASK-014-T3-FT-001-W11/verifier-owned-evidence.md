---
description: Fresh host and generic-emulator evidence owned by independent verification of TASK-014.
status: final
task_id: TASK-014-T3-FT-001-W11
stage_id: S-VERIFY
---
# Verifier-owned evidence — TASK-014-T3-FT-001-W11

## Claim mapping

| Claim | Fresh verifier proof |
|---|---|
| `FT-001-AC-002 / REQ-002 / REQ-023` | Fresh host policy regression plus current-APK generic-emulator bounds and screenshots for normal city, populated timer hint and populated forecast message while the accepted Main Display composition remains. |
| `FT-001-AC-005 / REQ-004` | Fresh host routing assertions plus selected-city long hold to Settings and system Back to Main Display on the same current APK. |

## Actual change surface and architecture

- Executor-recorded pre-task hashes were used only to identify the local delta;
  current hashes were recomputed independently:
  - `DisplayCapability.kt`: pre-task
    `869a6bbfa7222186fdea09b379c6a61ab38d5c94fbb1e2759ffa176978baf23b`,
    current `bc13857f822c3a0a491560acbeef5269b1c707cefcf5e1ce02e4c4bf0ace0e69`;
  - `DisplayProjectionTest.kt`: pre-task
    `2ac34a121f7f311a036abe6efb4d90f7e00b5d9d029d9ec6787206ea6b7a88af`,
    current `20035948aedac51d811e342562138b9a545a80dede54d1a466e9bcfd1f7ececd`;
  - `MainActivity.kt`: pre-task and current
    `737c4898b2e11063e3b7a586b998137803593585211f69e79c75046890819059`.
- Current source inspection confirms the one allocation correction:
  `DisplayLayoutSpec.headerWeight=0f`, header height `WRAP_CONTENT`, and
  `weatherRowWeight=1f` applied to a zero-height weighted weather row.
- City routing still reads `SettingsCapability.currentLocation()` and emits
  the existing `onOpenSettings` callback. It does not write Settings state or
  bypass the registered boundary. No new module, graph edge, public contract,
  dependency, state owner or storage path was found in the task delta.

## Fresh host evidence

Run from repository root on 2026-08-08:

| Command | Result |
|---|---|
| `./gradlew clean assembleDebug` | exit 0, `BUILD SUCCESSFUL`, 34 tasks executed |
| `./gradlew testDebugUnitTest` | exit 0, `BUILD SUCCESSFUL`, 52/52 tests, 0 failures/errors/skips |
| `git diff --check` | exit 0, no output |

`DisplayProjectionTest` contributed 5/5 passing tests, including
content-height header/remaining-height weather allocation and the existing
empty-city short tap, selected-city short-tap no-op and hold/open routing map.
The build emitted only the existing `MainActivity.onBackPressed` deprecation
diagnostic; it is outside TASK-014 and did not fail the gate.

Fresh debug APK SHA-256:

```text
ace2bbbc24ea190bf6122dc07cb124f2d9004ed788be3cf33e2fbbb25b33a8f7  app-debug.apk
```

## Fresh emulator identity and installed APK

- `adb devices -l`: exactly one attached target, `emulator-5554`, product/model
  `sdk_gphone64_x86_64`, device `emu64xa`.
- `adb -s emulator-5554 emu avd name`: `Tecno_Pova_6_API_35`.
- Runtime properties: `ro.boot.qemu=1`, Android `15`, API `35`, ABI `x86_64`,
  fingerprint prefix `google/sdk_gphone64_x86_64/emu64xa`.
- Physical emulator surface: `1080x2436`, density `393`; current app landscape
  bounds: `2436x1080`.
- `adb install -r app/build/outputs/apk/debug/app-debug.apk`: `Success`.
- Installed `base.apk` SHA-256 independently matched the local APK exactly:
  `ace2bbbc24ea190bf6122dc07cb124f2d9004ed788be3cf33e2fbbb25b33a8f7`.
- Package identity: versionCode `1`, versionName `0.1-foundation`, targetSdk 35.
- Cold start: `Status: ok`, `LaunchState: COLD`, normal
  `com.hozayushka.app/.app.MainActivity`.

## Fresh Main Display bounds and public flows

All coordinates below are public UI coordinates. The retained city/weather
state is synthetic/redacted; no credential or private store was read or
modified.

### Normal Main Display

```text
MainActivity:       0,0-2436,1080
main shell:         32,24-2184,1056
header:             0,0-2152,732
clock row:          0,0-2152,431
date:               0,431-2152,523
city gesture view:  0,523-2152,620  height=97
timer hint view:    0,620-2152,679  height=59
forecast message:   0,679-2152,732  height=53
weather row:        0,732-2152,1032 height=300
weather cards:      x=0..532, 540..1072, 1080..1612, 1620..2152
preset buttons:     y=8..336, 352..680, 696..1024
```

`verify-main.png` visibly shows dominant `HH:mm`, date, selected `Khujand`,
exactly four lower-left card positions and three right-side presets.

### Populated timer hint

The first preset was started and then single-tapped through the visible public
button. The fresh screenshot shows `Для отмены нажмите дважды`; the populated
timer-hint view measured `0,726-2152,785`, height `59 px`. Four card positions
and three preset positions remained present. A button-local double tap was used
only for safe cleanup to timer idle; it is not FT-006 evidence and no
double-tap-anywhere result is claimed.

### Populated forecast message

With the retained synthetic/redacted weather projection and no complete hourly
session, a public tap on Today populated the existing shared row with
`Почасовой прогноз еще не подгрузился`. The row measured
`0,679-2152,732`, height `53 px`; normal dominant `HH:mm`, date, city, four
card positions and three presets remained visible. No FT-003 forecast semantic
claim is adopted.

### Selected-city hold and Back

An 800 ms hold at `1108,595`, the center of the visible selected-city row,
replaced Main Display with the Settings `ScrollView` while the same
`MainActivity` remained resumed/focused. The public screen showed `Настройки`,
`Khujand` and `API-ключ не указан`; no credential value was present. Android
system Back returned to Main Display, with the same non-zero city row and
accepted composition visible.

### Safe final state

After the interaction proof, a final normal cold start cleared transient
presentation state. Final observations:

- `MainActivity` resumed and focused;
- normal `HH:mm` visible and countdown view hidden (timer idle);
- selected city visible, no Settings or overdue surface;
- `mWakefulness=Awake`;
- emulator remained running;
- all `/sdcard/TASK-014-VERIFY-*.png` temporaries were absent after cleanup.

## Fresh artifact hashes

```text
f201ba68318c0e771c6ded0d036e805b107ce00c2954a183e983b3809606bb7b  verify-main.png
2d0c6d7b09a6e112d8c2b7d9c2caf5b5167aaaa57e4586ad96288bfa80b0a441  verify-timer-hint.png
4e70c30b410e481ff2b564f7f05cfe297ae034362e2db335d5dbf35e5e5b6736  verify-forecast-message.png
25ee1372023ce9d6eb56f3c4c4a5e96f9257891a5a0b185689a59203290c294b  verify-settings-after-city-hold.png
0c7f3a65d2a788bf8b56c675a11cf649ddc4d9cb9dafac52546055926ee9862d  verify-back-to-main.png
28c68df18b33fce476e8195c092e1a2d947639d3e961dcda66a07ba8083a8033  verify-final-main.png
```

## Deferred target risk

This evidence is only generic Google Android 15/API35 x86_64 emulator evidence.
Samsung GT-I9300I Android 11 custom-ROM/1280x720 geometry, readability,
system-bar, keep-screen-on and interaction behavior remains `DEFERRED`; no
Samsung/custom-ROM/physical-device PASS is claimed.
