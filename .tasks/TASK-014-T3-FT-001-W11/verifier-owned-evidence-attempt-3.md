---
description: Fresh verifier-owned host and generic-emulator evidence for TASK-014 final retry attempt 3.
status: final
task_id: TASK-014-T3-FT-001-W11
stage_id: S-VERIFY
attempt: 3
---
# Verifier-owned evidence — attempt 3

## Claim mapping

| Claim | Fresh verifier proof |
|---|---|
| `FT-001-AC-002 / REQ-002 / REQ-023` | Current-APK idle/countdown bounds and decoded screenshots prove non-zero city, populated timer hint and populated forecast message while dominant clock/date, exactly four cards and three presets remain. |
| `FT-001-AC-005 / REQ-004` | During a public active countdown, an 800 ms visible-city hold opened Settings and system Back returned to the still-active countdown. |
| Delayed-navigation correction | Two fresh city-double-tap runs cancelled to idle by the approximately 250 ms checkpoint and remained on Main Display after at least another 750 ms, beyond the 600 ms long-press timeout; Settings never appeared. |
| FT-006 non-regression / scope | Public non-city single tap retained countdown and showed the hint; public non-city double tap cancelled to idle without Settings. Current source confines the correction to Main Display-local city touch-stream delivery and the existing callbacks. |

Attempt 1 retains the honest pre-change layout RED. Attempt 2 is superseded for
closure by its delayed-navigation semantic failure. Attempt 3 retains those
records, binds the correction to that failure and supplies fresh GREEN. All
executor evidence was supporting-only; no previous functional PASS was reused
as this verification's final proof.

## Actual current source and architecture

Fresh SHA-256:

```text
8b72f3f8d8559896ebb94730c9a54be16d217a5c8a4b2d7bb3ee343dd48751a8  DisplayCapability.kt
4afb5a6340236346c14d4f0431e9a2392aa2107a0c3a41ec234f851dd3d8d0ac  DisplayProjectionTest.kt
737c4898b2e11063e3b7a586b998137803593585211f69e79c75046890819059  MainActivity.kt
```

- `ActiveTimerCityTouchStream` is an `internal` Main Display type. It captures
  only an active city stream on `ACTION_DOWN`, continues dispatch through
  `ACTION_UP`/`ACTION_CANCEL` even if accepted double-tap cancellation changes
  Timer to `idle`, then clears its local capture flag.
- The correction symbol occurs only in `DisplayCapability.kt` and its focused
  `DisplayProjectionTest.kt` regression. It adds no persisted/private state,
  storage access, dependency or public API.
- The active city detector keeps the existing Timer single/double callbacks;
  only long press calls `route(CityGesture.LONG_HOLD)`. That route reads the
  public selected-city projection and invokes the existing `onOpenSettings`
  callback. `MainActivity` still supplies `::renderSettingsSurface`.
- Non-city root/cards/presets retain their existing Timer gesture listener.
  Fresh runtime evidence proves single-tap hint and double-tap cancellation on
  a non-city surface.
- The actual route remains the registered `Main Display -> Settings &
  Location` graph edge. No Settings semantic/state write, private-state bypass,
  second layout mechanism, new module/owner/contract/edge or FT-006 product
  change was found on the task surface.

## Fresh host evidence

Run from the repository root on 2026-08-08:

| Command | Result |
|---|---|
| `./gradlew clean assembleDebug` | exit `0`, `BUILD SUCCESSFUL`; 34 tasks executed |
| `./gradlew testDebugUnitTest` | exit `0`, `BUILD SUCCESSFUL`; 54/54 tests, 0 failures/errors/skips; `DisplayProjectionTest` 7/7 |
| `./gradlew testDebugUnitTest --tests com.hozayushka.app.DisplayProjectionTest.cityDoubleTapCannotLeaveDelayedSettingsAfterLongPressTimeout` | exit `0`, `BUILD SUCCESSFUL`; focused delayed-navigation regression passed |
| `git diff --check` | exit `0`, no output |

The clean build emitted only the pre-existing
`MainActivity.onBackPressed` deprecation diagnostic. Fresh local APK SHA-256:

```text
5cfb17a4c3d192b44583dce678b342588361bac35fb3bfd5ddf97e84820a7b80  app-debug.apk
```

## Fresh runtime and installed APK identity

- `adb devices -l`: exactly one target, `emulator-5554`, model/product
  `sdk_gphone64_x86_64`, device `emu64xa`.
- AVD: `Tecno_Pova_6_API_35`; Android `15`, API `35`, ABI `x86_64`; physical
  surface `1080x2436` at density `393`; app landscape bounds `2436x1080`.
- `adb install -r app/build/outputs/apk/debug/app-debug.apk` returned `Success`.
  Installed `base.apk` SHA-256 matched the local APK exactly:
  `5cfb17a4c3d192b44583dce678b342588361bac35fb3bfd5ddf97e84820a7b80`.
- Normal `MainActivity` cold start returned `Status: ok`, `LaunchState: COLD`.

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

During countdown the hierarchy retained city `0,629-2152,726` (`97 px`), timer
hint `0,726-2152,785` (`59 px`), forecast row `0,785-2152,838` (`53 px`), four
weather cards and three presets. `verify3-idle.png`,
`verify3-countdown-hint.png` and `verify3-forecast-message.png` visibly retain
the dominant clock/date and accepted composition; the latter two show the
populated transient rows.

## Fresh public UI sequence

1. Started the visible first preset. A non-city tap at `300,950` preserved the
   countdown and displayed `Для отмены нажмите дважды` in the non-zero hint row.
2. Held the visible selected city at `1108,700` for `800 ms`. Settings opened
   in the same resumed `MainActivity`; the screenshot shows `API-ключ не указан`
   and no credential value. System Back returned to the still-active countdown.
3. Double-tapped the non-city card surface with a `100 ms` interval. Timer
   returned to idle and Settings stayed absent.
4. Started a fresh countdown and double-tapped the selected city with a
   `100 ms` interval. First run: idle/no `ScrollView` at `+264.5 ms`, then still
   idle/no Settings after a further `+759.9 ms` wait (`+1511.5 ms` total from
   second tap). Repeat: idle/no `ScrollView` at `+265.7 ms`, then still idle/no
   Settings after a further `+770.4 ms` wait (`+1672.4 ms` total). Both late
   checkpoints are beyond the `600 ms` long-press timeout.
5. From idle, repeated the public Today tap until the ticker-exposed view
   accepted it; `Почасовой прогноз еще не подгрузился` populated the existing
   `53 px` forecast row without altering the composition. This proves only the
   row/layout result and does not adopt FT-003 semantics.

All interaction used public UI and retained synthetic/redacted Khujand/weather
state. No credential, SharedPreferences, capability-private store or
secret-bearing log was read or mutated.

## Artifact hashes

```text
1254710edacdf4d3d0e7197c66f50c77b3c2675c586d3ff3ff0d8e04d80f814a  verify3-idle.png
c0ce66908397f0b48223a01340c549f69bb4e8acad39e8b6deffcf0ab5167450  verify3-countdown-hint.png
25ee1372023ce9d6eb56f3c4c4a5e96f9257891a5a0b185689a59203290c294b  verify3-settings-hold.png
171957757943b40b9b5abc423ee806e488aef9783406a5459ce0b9147584dfb4  verify3-back-countdown.png
6a249eb564d918526bd42a78bf750d7c39ecd861c0608aafb26b1a7963490577  verify3-noncity-double-idle.png
d668f17225eaa5bb714c1d6becc1e6f33ed02b1880cc65fd730a5b69ecc89d77  verify3-city-double-250ms.png
d5447fb869e067f6223c4a291eac9fc48cbc81d60ff772f8e243290eb5df5b98  verify3-city-double-beyond-timeout.png
15fd9a37b7019332a80ec6c4497ef7f3d3f3029fbef87417faa03c58a15b144b  verify3-city-double-repeat-250ms.png
a38c737c3d31d89f0dbd6b9dae392f1283aea18ba415bf2ad3ae84ef0d09b201  verify3-city-double-repeat-beyond-timeout.png
fceac20eb4497e909d5c15abfe45c2360df4fe0dab1fbd98655e028cf6500ad1  verify3-forecast-message.png
1cacb222be829310048a9d0e91a97a063d75d57a6452579c9247a7688547c0b2  verify3-final-idle.png
```

## Isolation, cleanup and residual risk

- After the decisive probes, the emulator process exited during verifier
  final-state tooling. The same AVD was relaunched without wipe/reset, the same
  exact APK was reinstalled and hash-matched again, and a normal cold start
  restored the required final state. This did not replace or supply the
  earlier functional observations.
- Final state: `MainActivity` resumed/focused, normal idle clock visible,
  Settings absent, `mWakefulness=Awake`, exact AVD running. No verifier remote
  temporaries exist under `/sdcard`; the emulator launch session remains open.
- Samsung GT-I9300I Android 11 custom-ROM/1280x720 geometry, readability,
  system-bar, keep-screen-on and interaction behavior remains `DEFERRED`; no
  Samsung/custom-ROM/physical-device PASS is claimed.
