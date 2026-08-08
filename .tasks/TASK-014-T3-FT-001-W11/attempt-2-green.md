# Attempt 2 — retry correction and fresh GREEN

- Completed: `2026-08-08T15:31:35+05:00`
- Source basis: `HEAD a93e46118f0f0b90e311b6174e3f5a8ed7d89fef` plus the preserved dirty baseline and TASK-014 attempt-1 changes.
- Retry basis: attempt-1 functional `VERDICT: PASS` followed by the required semantic failure in `.protocols/TASK-014-T3-FT-001-W11/red-verification.md` and `.tasks/TASK-014-T3-FT-001-W11/TASK-014-T3-FT-001-W11-S-RED-VERIFY-final-report-docs-01.md`.
- Original RED retained: `.tasks/TASK-014-T3-FT-001-W11/attempt-1-red.md`; attempt 2 did not manufacture another RED.

## Correction

During active countdown, the city no longer delegates its event stream to the detector that handles only timer single/double taps. A city-specific active-countdown detector retains those same Timer & Alert actions and adds only `onLongPress -> CityGesture.LONG_HOLD -> onOpenSettings`. The existing root, weather-card, preset, Settings destination and Back routes are unchanged.

Attempt-1 to attempt-2 source hashes:

```text
DisplayCapability.kt:   bc13857f822c3a0a491560acbeef5269b1c707cefcf5e1ce02e4c4bf0ace0e69 -> 73cecbf88811703d53e473a9dc662bfa4e15ce6226e0cbac98ff99d6dd47c7a3
DisplayProjectionTest:  20035948aedac51d811e342562138b9a545a80dede54d1a466e9bcfd1f7ececd -> ddc4fb4aaa9115cf66b8f4791f4e17db0dc02cd55c947a6139e37a5bbaa0251b
MainActivity.kt:        737c4898b2e11063e3b7a586b998137803593585211f69e79c75046890819059 -> unchanged
```

## Focused host GREEN

`./gradlew testDebugUnitTest --tests com.hozayushka.app.DisplayProjectionTest.activeCountdownKeepsCityHoldAlongsideProtectedTimerTaps` exited `0` with `BUILD SUCCESSFUL`.

The regression starts countdown through public `TimerCapability`, proves a single tap retains `COUNTDOWN` and exposes the accepted hint, proves selected-city long hold resolves to the existing `OPEN_SETTINGS` intent while countdown remains active, and proves a double tap returns the timer to `IDLE`. It introduces no Settings behavior or private-state access and does not claim host proof of Android touch dispatch.

## Generic-emulator GREEN

Runtime identity was re-established before installation:

```text
serial=emulator-5554
avd=Tecno_Pova_6_API_35
model/product=sdk_gphone64_x86_64
device=emu64xa
release=15 api=35 abi=x86_64
physical surface=1080x2436 density=393
landscape app bounds=2436x1080
```

`./gradlew clean assembleDebug` produced APK SHA-256 `271fef0a097f6efa77769100cf3f819603e8a8e6e7c658ff828ef992e501ec0c`. `adb -s emulator-5554 install -r app/build/outputs/apk/debug/app-debug.apk` returned `Success`; the installed `base.apk` SHA-256 matched exactly. A normal cold start returned `Status: ok`, `LaunchState: COLD` for `com.hozayushka.app/.app.MainActivity`.

Public/synthetic interaction sequence:

1. Started the visible first preset at `2304,196`; countdown became visible. Active-countdown bounds retained city `0,629-2152,726` (`97 px`), timer hint `0,726-2152,785` (`59 px`), forecast row `0,785-2152,838` (`53 px`), four weather cards (`186 px` high) and three presets.
2. Single-tapped the non-city weather-card surface at `300,950`; `attempt-2-timer-hint.png` visibly records `Для отмены нажмите дважды` while countdown remains active.
3. Held the visible selected-city target at `1108,677` for `800 ms`; the same resumed `MainActivity` rendered the Settings `ScrollView`. `attempt-2-settings-during-countdown-hold.png` shows `Настройки`, synthetic city `Khujand` and `API-ключ не указан`, with no credential value.
4. Sent system Back; `attempt-2-back-to-countdown.png` visibly records the still-active countdown, proving city hold did not cancel it.
5. Double-tapped the non-city active preset with a reproducible `100 ms` inter-tap interval; `attempt-2-double-tap-cancelled.png` and final hierarchy record the standard idle Main Display.

Artifact SHA-256:

```text
6f2f42ae8b387a51ef54bf7abb88d454f0353d83ad46aaca0011703d9c0f8087  attempt-2-timer-hint.png
25ee1372023ce9d6eb56f3c4c4a5e96f9257891a5a0b185689a59203290c294b  attempt-2-settings-during-countdown-hold.png
f0d4063d8bf9ed9a8fc55b1d7c0edcdf4df979909c05767c74eb4fae172014bf  attempt-2-back-to-countdown.png
e5a4a555ae9ee120d816941d0487ed49cb3f2332185a800ab9cfcf39a1b8b20e  attempt-2-double-tap-cancelled.png
```

## Boundary, isolation and final state

- Main Display remains the city-gesture intent owner and invokes only the existing `onOpenSettings` callback. Settings & Location state, destination semantics and public contracts did not change.
- Non-city root/weather/preset routing was not changed. Fresh host and runtime evidence preserves timer single-tap hint and double-tap cancellation.
- Only public UI interactions and retained synthetic/redacted state were used. No credential or capability-private state was read or mutated; remote screenshot/XML temporaries were removed.
- Final state: emulator running and awake; normal `MainActivity` top-resumed/focused; timer idle; Settings absent; selected synthetic city visible.
- Samsung GT-I9300I Android 11 custom-ROM/1280x720 behavior remains `DEFERRED`; this artifact makes no Samsung/custom-ROM/physical-device PASS claim.
