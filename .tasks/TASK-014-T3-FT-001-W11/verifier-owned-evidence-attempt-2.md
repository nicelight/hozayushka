---
description: Fresh verifier-owned host and generic-emulator evidence for TASK-014 retry attempt 2.
status: final
task_id: TASK-014-T3-FT-001-W11
stage_id: S-VERIFY
attempt: 2
---
# Verifier-owned evidence — attempt 2

## Claim mapping and correction

| Claim | Fresh verifier proof |
|---|---|
| `FT-001-AC-002 / REQ-002 / REQ-023` | Current-APK idle/countdown bounds plus decoded screenshots prove non-zero city, populated timer hint and populated forecast message while dominant clock/date, four cards and three presets remain. |
| `FT-001-AC-005 / REQ-004` | During a public active countdown, an 800 ms visible-city hold opened Settings and system Back returned to the still-active countdown. |
| Retry correction / FT-006 non-regression | Public non-city single tap retained countdown and showed the hint; public non-city double tap cancelled to idle. Source inspection confines the new long-press branch to the city detector. |

Attempt 1 retains the original honest layout RED and layout GREEN. The current
retry is bound to the required semantic-fail that proved city hold was consumed
during countdown; no second RED was manufactured.

## Actual current source and architecture

Fresh SHA-256:

```text
73cecbf88811703d53e473a9dc662bfa4e15ce6226e0cbac98ff99d6dd47c7a3  DisplayCapability.kt
ddc4fb4aaa9115cf66b8f4791f4e17db0dc02cd55c947a6139e37a5bbaa0251b  DisplayProjectionTest.kt
737c4898b2e11063e3b7a586b998137803593585211f69e79c75046890819059  MainActivity.kt
```

- The attempt-1 allocation remains one mechanism: content-height header
  (`WRAP_CONTENT`, weight `0`) and remaining-height weather row (weight `1`).
- During active countdown, only the city uses
  `activeTimerCityGestureDetector`; its long press calls the existing
  `route(CityGesture.LONG_HOLD)`, which reads `settings.currentLocation()` and
  invokes the supplied `onOpenSettings` callback.
- `MainActivity` still supplies `::renderSettingsSurface` to
  `createMainView`. No Settings write/private-state access, new destination
  semantics, public interface, dependency, owner, graph edge or storage path
  is present in the correction.
- Root, weather-card and preset routes still use the existing Timer single and
  double actions. The city-specific detector adds the Settings hold without
  replacing those timer actions.

## Fresh host evidence

Run from the repository root on 2026-08-08:

| Command | Result |
|---|---|
| `./gradlew clean assembleDebug` | exit `0`, `BUILD SUCCESSFUL`, 34 tasks executed |
| `./gradlew testDebugUnitTest` | exit `0`, `BUILD SUCCESSFUL`; 53/53 tests, 0 failures/errors/skips |
| `git diff --check` | exit `0`, no output |

`DisplayProjectionTest` passed 6/6, including the focused public-contract
countdown/city-hold/timer-tap regression. The build emitted only the existing
`MainActivity.onBackPressed` deprecation diagnostic.

Fresh local APK SHA-256:

```text
271fef0a097f6efa77769100cf3f819603e8a8e6e7c658ff828ef992e501ec0c  app-debug.apk
```

## Fresh runtime and installed APK identity

- `adb devices -l`: exactly one target, `emulator-5554`, model/product
  `sdk_gphone64_x86_64`, device `emu64xa`.
- AVD: `Tecno_Pova_6_API_35`; `ro.boot.qemu=1`; Android `15`, API `35`, ABI
  `x86_64`; physical surface `1080x2436` at density `393`; app landscape bounds
  `2436x1080`.
- `adb install -r app/build/outputs/apk/debug/app-debug.apk` returned `Success`.
- Installed `base.apk` SHA-256 matched the local APK exactly:
  `271fef0a097f6efa77769100cf3f819603e8a8e6e7c658ff828ef992e501ec0c`.
- Package: versionCode `1`, versionName `0.1-foundation`, targetSdk `35`.
- Normal cold start returned `Status: ok`, `LaunchState: COLD`.

## Fresh layout evidence

Idle Main Display hierarchy:

```text
MainActivity:       0,0-2436,1080
main shell:         32,24-2184,1056
header:             0,0-2152,732
clock row:          0,0-2152,431
date:               0,431-2152,523   height=92
city:               0,523-2152,620   height=97
timer hint row:     0,620-2152,679   height=59
forecast row:       0,679-2152,732   height=53
weather row:        0,732-2152,1032 height=300
weather cards:      x=0..532, 540..1072, 1080..1612, 1620..2152
preset buttons:     y=8..336, 352..680, 696..1024
```

`verify2-main.png` and `verify2-final-main.png` visibly retain dominant
`HH:mm`, date, selected `Khujand`, four lower cards and three right presets.
Public Today interaction populated `Почасовой прогноз еще не подгрузился` in
the `53 px` row; `verify2-forecast-message-final.png` records it. This is only
row/layout evidence, not adoption of FT-003 forecast semantics.

During countdown the hierarchy retained city `0,629-2152,726` (`97 px`), timer
hint `0,726-2152,785` (`59 px`), forecast row `0,785-2152,838` (`53 px`), four
weather cards (`186 px` high) and three presets.

## Fresh public UI sequence

1. Started the visible 10-minute preset at `2304,516`; countdown became active.
2. Single-tapped the non-city preset surface at `2304,196`; countdown remained
   active and `verify2-timer-hint-final2.png` shows
   `Для отмены нажмите дважды`.
3. Held visible selected city at `1108,677` for `800 ms`;
   `verify2-settings-during-countdown.png` shows the Settings surface in the
   same `MainActivity`, selected `Khujand`, and `API-ключ не указан` with no
   credential value.
4. Sent system Back; `verify2-back-to-countdown.png` shows countdown still
   active (`08:04` when captured).
5. Double-tapped the non-city active 10-minute preset at `2304,516` with a
   `100 ms` interval; `verify2-double-tap-idle.png` shows the normal idle clock.

All interactions used public UI and retained synthetic/redacted state. No
credential, SharedPreferences, capability-private store or secret-bearing log
was read or mutated.

## Artifact hashes

```text
26addec3845dfe84ca9b1ec318e3699963239d98845ab77b58da054fd09dce65  verify2-main.png
ad6b3f660ac7c9c3704e9b74b9fb17b5da9865ff65281c62495f5a4a1db6b030  verify2-forecast-message-final.png
172f9a009f377a3deacc783c195963a9a485d73edff006b8888254397e8121a2  verify2-timer-hint-final2.png
3cbd1e916bd57b22de51e27a82c797ce7fa2652164486b6055296eedaca44761  verify2-settings-during-countdown.png
586785ba63d879509cf1802b0c44801c43fcf79c6b27409b768099814b8aba7e  verify2-back-to-countdown.png
faf897e6340fc1731f0eb8e625a763b6178bc3cc7d4f04214be094c42845a406  verify2-double-tap-idle.png
d76795514b68e54e5014b122c842bc26344eb66c8cd10f9a852d0d2f8b7da2bd  verify2-final-main.png
```

## Isolation, cleanup and residual risk

- Final cold start left `MainActivity` resumed/focused, normal Main Display
  visible, timer idle, Settings absent and `mWakefulness=Awake`; emulator stays
  running.
- All `/sdcard/TASK-014-VERIFY2-*` remote temporaries were removed after pull.
- Samsung GT-I9300I Android 11 custom-ROM/1280x720 geometry, readability,
  system-bar, keep-screen-on and interaction behavior remains `DEFERRED`; no
  Samsung/custom-ROM/physical-device PASS is claimed.
