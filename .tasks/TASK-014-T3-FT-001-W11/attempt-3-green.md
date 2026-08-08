# Attempt 3 — final retry correction and fresh GREEN

- Completed: `2026-08-08T16:10:32+05:00`.
- Final retry: scheduler-authorized attempt 3; no fourth attempt is permitted.
- Failure binding: attempt-2 functional `VERDICT: PASS` followed by the required semantic failure in `.protocols/TASK-014-T3-FT-001-W11/red-verification.md` and `.tasks/TASK-014-T3-FT-001-W11/TASK-014-T3-FT-001-W11-S-RED-VERIFY-final-report-docs-01.md`.
- Original RED retained: `.tasks/TASK-014-T3-FT-001-W11/attempt-1-red.md`; no replacement RED was created.

## Correction

Attempt 2 selected its city detector from live Timer state on every `MotionEvent`. The city double-tap callback cancelled the Timer on the second tap, so the following `ACTION_UP` was no longer delivered to that detector and its queued long-press callback survived until it opened Settings.

`ActiveTimerCityTouchStream` now captures only an active-countdown city stream at `ACTION_DOWN` and keeps delivering that same stream through `ACTION_UP` or `ACTION_CANCEL`, regardless of the Timer state change caused by the accepted double tap. Idle city routing, the genuine active-countdown long hold, every non-city listener and all existing callbacks/contracts are unchanged.

Attempt-2 to attempt-3 hashes:

```text
DisplayCapability.kt:  73cecbf88811703d53e473a9dc662bfa4e15ce6226e0cbac98ff99d6dd47c7a3 -> 8b72f3f8d8559896ebb94730c9a54be16d217a5c8a4b2d7bb3ee343dd48751a8
DisplayProjectionTest: ddc4fb4aaa9115cf66b8f4791f4e17db0dc02cd55c947a6139e37a5bbaa0251b -> 4afb5a6340236346c14d4f0431e9a2392aa2107a0c3a41ec234f851dd3d8d0ac
MainActivity.kt:       737c4898b2e11063e3b7a586b998137803593585211f69e79c75046890819059 -> unchanged
```

## Focused host GREEN

`./gradlew testDebugUnitTest --tests com.hozayushka.app.DisplayProjectionTest.cityDoubleTapCannotLeaveDelayedSettingsAfterLongPressTimeout` exited `0` with `BUILD SUCCESSFUL`.

The regression starts a public `TimerCapability` countdown, captures both city tap streams, applies the accepted Timer double-tap cancellation on the second down, proves that the second `ACTION_UP` remains delivered after Timer reaches `idle`, and observes no pending long-press navigation beyond the 600 ms timeout. This is supporting event-state evidence; actual Android `GestureDetector` dispatch and navigation are decided by the emulator probe below.

## Current APK and generic-emulator identity

```text
serial=emulator-5554
avd=Tecno_Pova_6_API_35
model/product=sdk_gphone64_x86_64
device=emu64xa
release=15 api=35 abi=x86_64
physical surface=1080x2436 density=393
landscape app bounds=2436x1080
```

`./gradlew clean assembleDebug` produced APK SHA-256 `5cfb17a4c3d192b44583dce678b342588361bac35fb3bfd5ddf97e84820a7b80`. `adb -s emulator-5554 install -r app/build/outputs/apk/debug/app-debug.apk` returned `Success`; the installed `base.apk` hash matched exactly. A normal `am start -W -S` reported `Status: ok`, `LaunchState: COLD` for `com.hozayushka.app/.app.MainActivity`.

## Fresh layout and route GREEN

Idle Main Display retained:

```text
city:             0,523-2152,620  height=97
timer hint row:   0,620-2152,679  height=59
forecast row:     0,679-2152,732  height=53
weather row:      0,732-2152,1032 height=300
weather cards:    exactly four, each 532 px wide / 292 px content height
preset buttons:   20,8-220,336; 20,352-220,680; 20,696-220,1024
```

Public/synthetic interaction sequence on the installed APK:

1. Started the first visible preset. A non-city tap at `300,950` retained countdown and visibly populated `Для отмены нажмите дважды`; active-countdown bounds retained city `97 px`, timer-hint row `59 px`, forecast row `53 px`, four cards and three presets.
2. Held the selected city center at `1108,677` for `800 ms`. The same resumed `MainActivity` displayed the Settings `ScrollView`; no credential value was visible. System Back returned to the still-active countdown.
3. Double-tapped the non-city card surface with a 100 ms interval. Main Display returned to `idle` and Settings stayed absent.
4. Started another countdown and double-tapped the selected city with the same 100 ms interval. After an explicit 250 ms wait, the hierarchy was already the idle Main Display (`header 732 px`, dominant clock restored) with no `ScrollView`. After a further 750 ms wait—beyond the long-press threshold—the same idle Main Display remained top-resumed/focused and Settings was still absent. The public failed path was run twice with the same result.
5. From idle, tapped Today with unavailable hourly data. `Почасовой прогноз еще не подгрузился` populated the existing `53 px` forecast row without changing the original composition.

This supplies fresh GREEN for genuine city hold, city double tap with delayed no-Settings proof, non-city single/double behavior and the original city/transient-row layout.

## Screenshots

```text
dd1aaab2ea2fc2cea42857ca28db89cb011b9ec647e5f782b37a9d6af6064ad4  attempt-3-main.png
63f9c56ea9c07bf945e6ad1a87060991ec15877753ccfe7286a6e871636a2f02  attempt-3-timer-hint.png
25ee1372023ce9d6eb56f3c4c4a5e96f9257891a5a0b185689a59203290c294b  attempt-3-settings-during-countdown-hold.png
69deeafccf850ee9020e2608da05df2aaa9fda338cb7952a9e4714428a82b1c7  attempt-3-back-to-countdown.png
ccd20b5170f4d65faac299e67698111c20744be57eccb6022cf96598c7231cc8  attempt-3-noncity-double-idle.png
05ee2ce2fae059da263a79ac273485e450578d39cab12ce600d41197e90bb7bc  attempt-3-city-double-no-delayed-settings.png
46b0f8292731206cf932d7521c16153e1cf74f2efd116a08a0c2fd870e4ac455  attempt-3-forecast-message.png
40a3cd6a17c8254efca64977358c5e10067a35397012e14d046189a09c507b82  attempt-3-final-main.png
```

## Boundary, isolation and final state

- Main Display still owns city gesture intent and invokes only the existing `onOpenSettings` callback. Settings & Location semantics/state, Timer & Alert state ownership and every public contract remain unchanged.
- Only public UI interactions and retained synthetic/redacted Khujand/weather state were used. No credential or capability-private state was read or mutated.
- Remote task screenshot temporaries were removed.
- Final emulator state: running and awake; normal `MainActivity` top-resumed/focused after a normal cold start; Timer idle; Settings absent; normal clock/city/four cards/three presets visible.
- Samsung GT-I9300I Android 11 custom-ROM/1280x720 remains `DEFERRED`; no Samsung/custom-ROM/physical-device PASS is claimed.
