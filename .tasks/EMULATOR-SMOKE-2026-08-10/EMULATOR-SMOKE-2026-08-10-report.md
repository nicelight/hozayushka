# Generic-emulator runtime smoke — 2026-08-10

## verdict

`REQUEST_CHANGES` for the current generic-emulator public UI smoke: launch,
fixture, timer probe, lifecycle probe and cleanup are evidenced, but the
selected-city long hold did not open Settings and idle preset taps did not
start a timer. This is supplementary evidence only; no task/lifecycle/queue
closure is inferred.

## device_identity

| Surface | Observation |
|---|---|
| AVD / serial | `Tecno_Pova_6_API_35` / `emulator-5554` |
| Runtime identity | manufacturer `Google`, model `sdk_gphone64_x86_64`, device `emu64xa`, product `sdk_gphone64_x86_64` |
| Android | release `15`, API `35`, ABI `x86_64`, `sys.boot_completed=1` |
| Geometry | configured/physical `1080x2436`, density `393`; app landscape `2436x1080`, `ROTATION_90` |
| APK | `app-debug.apk`, SHA-256 `d44418e6f2d970f7f986645bf4d5913fe279ad4420fc3ba36e582dfa902da553` |

The AVD profile is named TECNO POVA 6, but the running image is generic Google
Android; this is not TECNO firmware and not Samsung/custom-ROM evidence.

## commands_and_evidence

- `adb devices -l`, `getprop`, `wm size`, `wm density`: identity above.
- `./gradlew assembleDebug`: `BUILD SUCCESSFUL` (15s); APK installed with
  `adb -s emulator-5554 install -r ...`: `Success`.
- Normal start and supported probe start both returned `Status: ok` for
  `com.hozayushka.app/.app.MainActivity`.
- Final `dumpsys activity activities` and `dumpsys window windows` resolve
  focused/resumed `MainActivity`; window requests
  `KEEP_SCREEN_ON`, `HIDE_NAVIGATION FULLSCREEN`, `IMMERSIVE_STICKY`.
- Final filtered current-PID logcat had no `FATAL EXCEPTION`, `AndroidRuntime`,
  `ANR in` or `Application Not Responding` match.
- Screenshots are PNG `2436x1080`; probe hierarchies are XML. Hashes are kept
  for the decisive captures below.

## matrix

| Check | Result | Observation / decisive evidence |
|---|---|---|
| Launch and main shell | PASS | Clock `HH:mm`, `10 августа`, `Khujand`, four stable card slots, and `3 м / 10 м / 30 м` presets visible. [`07-main-weather-fixture.png`](screenshots/07-main-weather-fixture.png), [`33-final-main-clean.png`](screenshots/33-final-main-clean.png) |
| Weather fixture seed | PASS | Public Foundation probe reported `settings_seeded`, synthetic Khujand only. [`03-foundation-probe.xml`](hierarchy/03-foundation-probe-after.xml) |
| Weather fixture refresh / projection | PASS | Public probe reported `weather_refreshed_redacted`; Main projection showed synthetic `21°` Today card while four positions stayed stable. [`04-weather-refresh.xml`](hierarchy/04-weather-refresh.xml), [`07-main-weather-fixture.png`](screenshots/07-main-weather-fixture.png) |
| Timer start/countdown via public probe | PASS | Public `Start 1s Timer` reported `COUNTDOWN`; Main showed `00:01` countdown after re-entry. [`11-probe-timer-started.png`](screenshots/11-probe-timer-started.png), [`29-active-gesture-start.png`](screenshots/29-active-gesture-start.png) |
| Idle preset tap start | FAIL | Fresh taps on visible preset buttons did not replace the main clock with countdown (`10-timer-preset-retry.png`, `28-preset-second-calibrated.png`); probe-based timer start remained available. [`10-timer-preset-retry.png`](screenshots/10-timer-preset-retry.png), [`28-preset-second-calibrated.png`](screenshots/28-preset-second-calibrated.png) |
| Active single tap | PASS / partial | Calibrated public weather-card tap left `00:01` countdown active; the 50 ms capture did not show the hint text, so hint visibility is not independently claimed. [`30-active-single-calibrated.png`](screenshots/30-active-single-calibrated.png) |
| Active double tap | PASS | Two calibrated public weather-card taps returned to idle Main. [`31-active-double-calibrated.png`](screenshots/31-active-double-calibrated.png) |
| Selected-city short tap | PASS | Main remained visible; no Settings navigation. [`24-selected-city-short-calibrated.png`](screenshots/24-selected-city-short-calibrated.png) |
| Selected-city long hold → Settings | FAIL | Repeated public 800–1000 ms holds at calibrated and visual city coordinates did not open Settings. [`25-city-long-hold-settings-calibrated.png`](screenshots/25-city-long-hold-settings-calibrated.png), [`26-city-long-hold-small-move.png`](screenshots/26-city-long-hold-small-move.png), [`27-city-long-hold-visual-coord.png`](screenshots/27-city-long-hold-visual-coord.png) |
| Settings surface / Settings Back | UNAVAILABLE | Settings was not reached through the public city route, so its surface and return path could not be evaluated. No credentials were entered. |
| System Back where reachable | PASS | MainActivity system Back returned to launcher; app was subsequently relaunched to Main. [`21-system-back-from-main.png`](screenshots/21-system-back-from-main.png) |
| Activity background/foreground | PASS (probe route) | Public probe timer remained `COUNTDOWN elapsed=448 remaining=552` after HOME and task foreground return. Main-specific process-stop and physical target-ROM recovery remain deferred. [`22-lifecycle-resume-timer.png`](screenshots/22-lifecycle-resume-timer.png) |
| Safe cleanup | PASS | Final public probe hierarchy reported `Timer: IDLE elapsed=0 remaining=0`; final state is focused MainActivity, no Settings, no overdue overlay. [`09-final-probe-idle.xml`](hierarchy/09-final-probe-idle.xml), [`33-final-main-clean.png`](screenshots/33-final-main-clean.png) |

## emulator_instability

- AVD was initially absent from `adb devices -l`; it cold-booted successfully
  without wipe/reset/snapshot mutation.
- `uiautomator dump` intermittently returned `could not get idle state` on the
  live Main shell because its ticker kept the view non-idle; probe-route XML
  dumps succeeded. Screenshots and activity/window dumps remained available.
- `dumpsys activity top` exited `0` but once showed stale launcher hierarchy
  while complementary `dumpsys activity activities` and window focus showed
  MainActivity. This is recorded as command/emulator inconsistency, not an app
  crash.
- Gradle emitted the existing SDK XML version-4/tooling warning; build passed.

## security_and_scope

No API credential was entered, read, logged, or copied. No direct private-store
read/write, `pm clear`, wipe-data, or AVD reset was used. State changes were only
through the public Foundation probe with synthetic/redacted fixture state and
public UI input. No production code, task card, scheduler status, Memory Bank,
specification, or provider implementation was changed.

## residual_risks_and_next_action

- Current generic emulator evidence does not promote Samsung GT-I9300I,
  Android 11 custom ROM, 1280×720 readability/system-bars/keep-screen-on,
  physical lifecycle/process recovery, or audio policy/ramp/cap to PASS.
- Recommended next action: inspect the current Main Display public touch/layout
  path for idle preset delivery and selected-city long-hold reachability, then
  repeat this fresh emulator matrix. Keep Yandex provider work on its separate
  Architect route.
