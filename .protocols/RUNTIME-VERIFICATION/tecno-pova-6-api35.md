# Tecno Pova 6 API 35 supplementary runtime evidence

- Run: 2026-08-08, `Asia/Dushanbe`.
- Tooling: Android Studio/local SDK `/home/serg/Android/Sdk`; emulator `36.6.11.0`.
- Scope: generic Android emulator readiness only. Canonical release acceptance remains physical Samsung GT-I9300I (`s3ve3gds`), Android 11 custom ROM, 1280×720 landscape.

## Launch and runtime identity

GUI launch, detached from the invoking shell and without AVD reset or snapshot mutation:

```bash
setsid /home/serg/Android/Sdk/emulator/emulator \
  -avd Tecno_Pova_6_API_35 \
  -no-snapshot-load -no-snapshot-save -no-boot-anim \
  > /tmp/hozayushka-tecno-pova-6-api35-emulator.log 2>&1 < /dev/null &
```

`adb devices -l` resolved serial `emulator-5554`; `adb -s emulator-5554 shell getprop sys.boot_completed` returned `1`. The persistent emulator process was PID `2375785` with the exact AVD name in its command line and `ro.boot.qemu.avd_name=Tecno_Pova_6_API_35`.

| Fact surface | Exact observation |
|---|---|
| AVD/profile | `Tecno_Pova_6_API_35`; user hardware profile `TECNO POVA 6`, id `tecno_pova_6`, manufacturer `TECNO` |
| Configured image/display | Android 15/API 35 Google APIs x86_64; `1080x2436` at `393` dpi |
| Runtime product props | model `sdk_gphone64_x86_64`; manufacturer `Google`; name `sdk_gphone64_x86_64`; device `emu64xa` |
| Runtime platform props | API `35`; release `15`; primary ABI `x86_64`; ABI list `x86_64,arm64-v8a` |
| Runtime display | `wm size`: `Physical size: 1080x2436`; `wm density`: `Physical density: 393` |
| Orientation | boot/home observation `mCurrentOrientation=0`; app observation `mCurrentOrientation=1`, `ROTATION_90`, logical/app bounds `2436x1080` |

The TECNO values are hardware-profile/configuration metadata. The Google
`sdk_gphone64_x86_64`/`emu64xa` values are the running generic system-image
identity; this run is not evidence of TECNO firmware or hardware.

## Known initial state and safe rerun

- Before launch, ADB had no attached devices and no emulator process was running.
- After cold boot, `com.hozayushka.app` was already installed at version code `1`, version `0.1-foundation`, and had no live process. `install -r` preserved existing app data; no `pm clear`, `-wipe-data`, AVD delete or AVD reset was used.
- The probe displayed the preserved/resettable state as location `Худжанд`, timer `IDLE` with zero elapsed/remaining, and weather `empty`. No probe button was activated.
- Safe rerun first checks `adb devices -l` and `ro.boot.qemu.avd_name`. Reuse the matching running serial; otherwise run the launch command above, wait for `sys.boot_completed=1`, then use `adb -s <serial>` for every command. Rebuild, use `adb install -r`, and start the Activity again. Do not start a second instance, wipe, delete or reset this AVD.

## Build, install and starts

```bash
./gradlew assembleDebug
/home/serg/Android/Sdk/platform-tools/adb -s emulator-5554 \
  install -r app/build/outputs/apk/debug/app-debug.apk
/home/serg/Android/Sdk/platform-tools/adb -s emulator-5554 shell \
  am start -W -S -n com.hozayushka.app/.app.MainActivity
/home/serg/Android/Sdk/platform-tools/adb -s emulator-5554 shell \
  am start -W -S -n com.hozayushka.app/.app.MainActivity \
  --ez foundation_probe true
```

Results: Gradle `BUILD SUCCESSFUL`; install `Success`; both starts returned
`Status: ok`, `LaunchState: COLD`, and the real
`com.hozayushka.app/.app.MainActivity`. APK badging independently identified
that Activity as launchable. The probe UI visibly reported
`Foundation probe mode` and `ADB: --ez foundation_probe true`.

No instrumentation tests exist: `app/src/androidTest` is absent,
`androidTest_files=0`, and no runner or `androidTestImplementation` is
configured. Per the operator gate, `connectedDebugAndroidTest` was not run and
is `DEFERRED` rather than claimed as `PASS`.

## Non-destructive smoke evidence

```bash
ADB=/home/serg/Android/Sdk/platform-tools/adb
SERIAL=emulator-5554
PID=$($ADB -s $SERIAL shell pidof com.hozayushka.app)
$ADB -s $SERIAL shell dumpsys activity activities
$ADB -s $SERIAL shell dumpsys window windows
$ADB -s $SERIAL shell dumpsys display
$ADB -s $SERIAL shell dumpsys power
$ADB -s $SERIAL exec-out screencap -p > /tmp/hozayushka-tecno-main-clean.png
$ADB -s $SERIAL logcat --pid=$PID -d -v threadtime
```

- Main route: live PID `3751`; `topResumedActivity`, `ResumedActivity` and focused app all resolved to `com.hozayushka.app/.app.MainActivity`.
- Window: `KEEP_SCREEN_ON`; `HIDE_NAVIGATION FULLSCREEN ... IMMERSIVE_STICKY`; transient bars by swipe; `mHasSurface=true`; `isOnScreen=true`; requested and app bounds `2436x1080`; `SCREEN_ORIENTATION_LANDSCAPE` / `ROTATION_90`.
- Power: `mWakefulness=Awake`; the app window's `KEEP_SCREEN_ON` request is observed. This does not prove physical panel behavior.
- Main-process logcat: 98 current-PID lines and `0` matches for `FATAL EXCEPTION`, `AndroidRuntime`, app ANR or app process-fatal patterns. Probe-process logcat: 28 lines and the same `0` fatal/ANR matches.
- The first main screenshot contained Android's one-time `Viewing full screen` confirmation. A fixed-coordinate ADB tap had no effect; bounds were then read with `uiautomator`, and an actual tap at `1688 464` dismissed only that system overlay. No application control was activated.
- Clean main screenshot: [tecno-pova-6-api35-main.png](tecno-pova-6-api35-main.png), PNG `2436x1080`, SHA-256 `be537987dcdb84dd4aa270971b1f35577a2d13abfb8b4aaa68c02990662c0950`.
- Probe screenshot: [tecno-pova-6-api35-foundation-probe.png](tecno-pova-6-api35-foundation-probe.png), PNG `2436x1080`, SHA-256 `007e3ea728db36cf2ad4c0f7265b803fe38d417beff424ca51d8c28093b8d10e`.

Representative checks were `dumpsys activity activities`, `dumpsys window
windows`, `dumpsys display`, `dumpsys power`, `screencap -p`, and
`logcat --pid=<current-app-pid> -d`; outputs above are exact compact extracts.

## Verdicts, cleanup and redaction

| Verdict | Claim |
|---|---|
| PASS | GUI AVD launch, serial resolution, complete boot, exact runtime inventory, debug build/install, real main Activity start, supported Foundation probe start |
| PASS | Generic emulator process/activity health, landscape `2436x1080`, fullscreen/immersive and keep-screen-on requests, rendered screenshots, no current-process fatal/ANR log pattern |
| DEFERRED | `connectedDebugAndroidTest`: no actual instrumentation tests/configuration exist |
| DEFERRED | Physical Samsung GT-I9300I Android 11/custom-ROM 1280×720 readability, geometry, lifecycle/process/screen-off recovery, system-bar/keep-screen-on behavior and alert audio policy/ramp/cap |
| FAIL | None in this bounded generic-emulator readiness run |

Remote temporary UI XML files were removed. The AVD was intentionally left
running with the normal main Activity: final check reported emulator PID
`2375785`, app PID `4218`, `sys.boot_completed=1`, the expected AVD name, and
the resumed `MainActivity`; no emulator cleanup command was run.
When cleanup is explicitly wanted, use the narrow graceful command
`adb -s emulator-5554 emu kill`; it does not delete or reset the AVD.

No credential was entered, read from app storage, printed, or copied into this
evidence. SharedPreferences were not dumped. The retained screenshots expose no
API key, and emulator startup material such as the ADB public key is omitted.
