---
description: Fresh verifier-owned evidence for TASK-015 Attempt 1.
status: complete
task_id: TASK-015-T3-FT-001-W12
attempt: 1
role: Reviewer
---
# Verifier-owned evidence — Attempt 1

## Independence and scope

- This is a fresh Reviewer observation of Attempt 1. No W11 screenshot, W11
  verdict, or W11 failure was used as proof.
- No execute receipt was reused. Executor artifacts were read as supporting
  handoff context only.
- Task-owned acceptance is `FT-001-AC-005 / REQ-004`; `REQ-013` is exercised
  only as the indexed regression guard. No FT-006 ownership was adopted.
- Runtime state was synthetic/redacted Khujand/weather fixture. No credential,
  capability-private storage, or private state was read or mutated.

## Independent host/static checks

| Check | Result | Observation |
|---|---|---|
| `./gradlew clean assembleDebug` | PASS | exit 0, BUILD SUCCESSFUL; APK SHA-256 `d1f8634227c758de4e424e37aa18f863afe5623ee1b794484946606b4039bb30` |
| `./gradlew testDebugUnitTest` | PASS | exit 0; 54 tests, 0 skipped, 0 failures, 0 errors |
| `git diff --check` | PASS | exit 0, no output |
| Focused dispatcher test | PASS | `DisplayProjectionTest.activeCountdownDispatcherKeepsEveryCapturedSurfaceStreamToTerminalEvent`, exit 0 |

The full unit result files are under `app/build/test-results/testDebugUnitTest/`.
The only build warning observed was the existing deprecated
`MainActivity.onBackPressed` override.

## Scope and ownership inspection

- Production/test diff names: `app/src/main/kotlin/com/hozayushka/app/display/DisplayCapability.kt`
  and `app/src/test/kotlin/com/hozayushka/app/DisplayProjectionTest.kt`.
- No diff exists under `app/src/main/kotlin/com/hozayushka/app/timer` or
  `app/src/main/kotlin/com/hozayushka/app/settings`.
- `ActiveCountdownTouchDispatcher` is internal to Main Display. It captures
  only on `ACTION_DOWN`, forwards the captured stream through terminal
  `ACTION_UP`/`ACTION_CANCEL`, and clears capture at either terminal event.
- Existing `TimerCapability` gesture commands and the existing Settings
  callback remain the only cross-slice calls. No module, graph edge, event
  boundary, public contract, timer arithmetic/state owner, or Settings owner
  was added.

## Generic Android runtime identity

Runtime target was the authorized `Tecno_Pova_6_API_35` AVD:

- model/product: `sdk_gphone64_x86_64`
- device: `emu64xa`
- Android: 15 / API 35 / x86_64
- configured physical size: `1080x2436`, density `393`; app observed in
  landscape screenshots `2436x1080`
- current APK installed with `adb -s emulator-5554 install -r`; installed hash
  matched the local APK hash above
- required `adb -s emulator-5554 shell dumpsys activity top` exited 0;
  complementary `dumpsys activity activities` and `dumpsys window windows`
  showed the focused `com.hozayushka.app/.app.MainActivity`

## Fresh public interaction matrix

Coordinates below are physical landscape coordinates on the stated AVD. Each
result is a public UI/probe observation; the screenshot is decisive for public
reachability.

| Flow | Public action and decisive observation | Evidence |
|---|---|---|
| Main shell guard | Clean idle Main Display showed four lower cards and three preset buttons. | [`verifier-attempt-1-main-idle.png`](verifier-attempt-1-main-idle.png) |
| Selected-city idle short tap | Tap city, Main Display remained visible; no Settings navigation. | [`verifier-attempt-1-city-short-idle.png`](verifier-attempt-1-city-short-idle.png) |
| Non-city single | Start preset, tap weather card at `(800,800)`; countdown remained active and `Для отмены нажмите дважды` was visible. | [`verifier-attempt-1-noncity-single-hint.png`](verifier-attempt-1-noncity-single-hint.png) |
| Non-city double | Two weather-card taps at 120 ms; screenshot at ~350 ms showed idle Main Display. | [`verifier-attempt-1-noncity-double-idle.png`](verifier-attempt-1-noncity-double-idle.png) |
| City hold → Settings | Start preset, hold selected city at `(1100,700)` for 800 ms; existing Settings screen opened with only redacted API-key state. | [`verifier-attempt-1-city-hold-settings.png`](verifier-attempt-1-city-hold-settings.png) |
| Settings → system Back | System Back returned to Main Display with the active countdown still visible. | [`verifier-attempt-1-city-hold-back-countdown.png`](verifier-attempt-1-city-hold-back-countdown.png) |
| City double at checkpoint | Two city taps at 120 ms; ~350 ms screenshot showed idle Main Display. | [`verifier-attempt-1-city-double-350.png`](verifier-attempt-1-city-double-350.png) |
| City delayed-navigation protection | Additional screenshot after the 600 ms long-press timeout remained on Main Display with no Settings. | [`verifier-attempt-1-city-double-beyond-timeout.png`](verifier-attempt-1-city-double-beyond-timeout.png) |
| Preset idle start | Tap second preset; 10-minute countdown started and its preset was highlighted. | [`verifier-attempt-1-preset-idle-start.png`](verifier-attempt-1-preset-idle-start.png) |
| Preset active single | Tap active second preset once; countdown remained active and hint appeared. | [`verifier-attempt-1-preset-active-single.png`](verifier-attempt-1-preset-active-single.png) |
| Preset active double | Two active-preset taps at 120 ms; ~350 ms screenshot showed idle Main Display. | [`verifier-attempt-1-preset-active-double-idle.png`](verifier-attempt-1-preset-active-double-idle.png) |
| Overdue setup | Existing public `foundation_probe` route started its public `Start 1s Timer` control; probe observed `Timer: OVERDUE`. | [`verifier-attempt-1-overdue-probe-setup.png`](verifier-attempt-1-overdue-probe-setup.png) |
| Overdue dismissal | Main Display showed overdue overlay; one public tap dismissed it back to idle Main Display. | [`verifier-attempt-1-overdue-before-dismiss.png`](verifier-attempt-1-overdue-before-dismiss.png), [`verifier-attempt-1-overdue-after-dismiss.png`](verifier-attempt-1-overdue-after-dismiss.png) |

## Safe cleanup observation

After the final public tap:

- `dumpsys activity top` exit code was 0;
- activity/window dumps showed focused `MainActivity`, not Settings and no ANR
  window;
- `dumpsys power` showed `mWakefulness=Awake`;
- final screenshot showed idle Main Display, with no overdue overlay or active
  countdown;
- filtered post-cleanup logcat contained no `FATAL EXCEPTION`,
  `AndroidRuntime`, `ANR in com.hozayushka.app`, or `Application Not Responding`;
- no Samsung, custom-ROM, 1280x720, physical-device, lifecycle/audio or
  release-target claim is made.

The AVD had transient cold-start/ADB instability during setup; the final
successful run was repeated after a clean AVD restart and is the runtime proof
recorded above. This does not change the task outcome and is not attributed to
the W12 gesture route.

## Fresh-proof conclusion

All required current Attempt 1 host gates, architecture checks, and verifier-owned
generic Android public outcomes passed. The executor's pre-change run did not
reproduce the historical W11 RED; it is retained as an honest supporting
classification, not converted into RED and not used as proof.
