# Attempt 1 — claim-equivalent GREEN

- Final observation completed: `2026-08-08T14:53:36+05:00`
- Source basis: `HEAD a93e46118f0f0b90e311b6174e3f5a8ed7d89fef` plus the preserved dirty baseline in protocol `context.md` and the two TASK-014 product/test edits.
- Final clean-built APK: SHA-256 `ace2bbbc24ea190bf6122dc07cb124f2d9004ed788be3cf33e2fbbb25b33a8f7`.
- Runtime identity: serial `emulator-5554`, AVD `Tecno_Pova_6_API_35`, generic Google `sdk_gphone64_x86_64` / `emu64xa`, API 35 / Android 15 / x86_64, `1080x2436` at 393 dpi, landscape app bounds `2436x1080`.

## Correction basis

The pre-change header and weather row each requested half of the available left-column height. The dominant 132sp clock row (`431 px`) and date (`85 px` in RED) exhausted the header's `516 px`, collapsing all following siblings. The correction removes the header from the weighted remainder split: it measures its existing accepted content, while the weather row alone receives the remaining weight. No content, ordering, count, gesture, route, API, owner or neighboring capability changed.

## GREEN — FT-001-AC-002 / REQ-002 / REQ-023

Normal Main Display bounds after the correction:

```text
MainActivity:       0,0-2436,1080
main shell:         32,24-2184,1056
header:             0,0-2152,732
clock row:          0,0-2152,431
date:               0,431-2152,523   height=92
city gesture view:  0,523-2152,620   height=97
timer hint view:    0,620-2152,679   height=59
forecast message:   0,679-2152,732   height=53
weather row:        0,732-2152,1032  height=300
preset buttons:     20,8-220,336; 20,352-220,680; 20,696-220,1024
```

`green-main.png` visibly preserves dominant `HH:mm`, date, selected `Khujand`, exactly four lower-left weather positions and three right-side presets.

Populated transient rows were exercised through public product UI:

- Timer hint: tapped the 3-minute preset, then single-tapped the active preset. `green-timer-hint.png` visibly shows `Для отмены нажмите дважды`; hierarchy bounds for the populated row are `0,726-2152,785` (`59 px`). The countdown was then cancelled through the existing button-local double-tap route solely for safe cleanup; no FT-006 behavior was changed or claimed.
- Forecast message: with retained synthetic/redacted weather state and no complete hourly forecast, tapped Today. `green-forecast-message.png` visibly shows `Почасовой прогноз еще не подгрузился`; hierarchy bounds are `0,679-2152,732` (`53 px`).

Screenshot SHA-256:

```text
a5c73a0af9c4991a55a71b687d92f95a152978b8f4aa754040d1123cee05f59f  green-main.png
f424510dcab704a2f122e68a9d971a6c6c13420051dfe71f571337987a095b57  green-timer-hint.png
ca6f49a459f6836012841555ed1ad95b324e13e72a01490eed5524fbeafd4e22  green-forecast-message.png
```

## GREEN — FT-001-AC-005 / REQ-004

1. With timer idle and selected city visible, `/home/serg/Android/Sdk/platform-tools/adb -s emulator-5554 shell input swipe 1108 595 1108 595 800` targeted the center of the non-zero city row.
2. The same `MainActivity` replaced Main Display with the visible `Настройки` ScrollView; `green-settings-after-city-hold.png` records the first run.
3. `/home/serg/Android/Sdk/platform-tools/adb -s emulator-5554 shell input keyevent 4` returned to the Main Display; `green-back-to-main.png` records the return.
4. After final clean build/install, the same hold and system Back route were repeated. `final-clean-settings.png` and `final-safe-main.png` bind the interaction to the clean-built APK above.
5. The full host suite retained the existing empty-city short-tap/open, selected-city short-tap/no-op and any-city hold routing assertions.

Interaction/final screenshot SHA-256:

```text
3cbd1e916bd57b22de51e27a82c797ce7fa2652164486b6055296eedaca44761  green-settings-after-city-hold.png
776ff9fe3143c43d7c1b0540228963e38bf15ba290a32363b11a65068242eb62  green-back-to-main.png
25ee1372023ce9d6eb56f3c4c4a5e96f9257891a5a0b185689a59203290c294b  final-clean-settings.png
ee22b6cef6e3b3a8da2fefb9fd196d33b0d1e543c81e0408813be56fe498604a  final-safe-main.png
```

## Conditional Foundation observation

The correction is inside `createMainView`; `createFoundationView` and its probe layout are a separate untouched view. The correction therefore does not incidentally affect Foundation `Audio Probe` or `Cancel and Reset`; the conditional FT-000 observation path is not applicable and no second mechanism or FT-000 behavior was introduced.

## Isolation and final state

- Used only public UI routes and retained synthetic Khujand/redacted weather state.
- No live credential, private Settings/Weather/Timer/Forecast storage, unredacted request or secret-bearing log was read or written.
- Every remote `/sdcard/TASK-014-*.png` temporary used for capture was removed immediately after pull.
- Final emulator state: still running, `mWakefulness=Awake`, normal `MainActivity` top-resumed/focused, normal clock visible (timer idle; no countdown/overdue), selected Khujand, no Settings screen, no credential, `final-safe-main.png`.

This is supplementary generic Android 15/API35 emulator evidence only. It is not Samsung, Android 11 custom-ROM, 1280x720 or physical-device evidence.
