---
description: Fresh attempt-2 physical-device smoke/debug report for Hozayushka.
status: final
---
# Real-device smoke/debug — attempt 2

- verdict: `APPROVE` for the requested offline/redacted physical-device smoke scope; no reproducible app defect found.
- role: `Reviewer`, read-only
- run: `2026-08-10`, Asia/Dushanbe, approximately `20:17–20:27 +05:00`
- device: serial `1156725456009666`, `TECNO LI6`, Android `15` / API `35`, physical `1080×2460`, density `440`
- package: `com.hozayushka.app`; Activity: `com.hozayushka.app/.app.MainActivity`
- device constraint: every device command used explicit `adb -s 1156725456009666`; no other serial was used.
- prohibited routes: no emulator, AVD, Android QEMU, emulator-related process, or connected-device Gradle task was started or used. Host process scan is empty: [`host-emulator-process-scan.txt`](evidence-attempt-2/host-emulator-process-scan.txt).
- provider constraint: no credential was entered and no live Yandex request was made; live-provider PASS is not claimed.

## Verdict matrix

| Scope | Verdict | Evidence / observation |
|---|---|---|
| Serial/model and runtime identity | `PASS` | [`device-identity.txt`](evidence-attempt-2/device-identity.txt), [`device-baseline.txt`](evidence-attempt-2/device-baseline.txt), final identity [`final-device-identity.txt`](evidence-attempt-2/final-device-identity.txt) |
| Host build/unit gates | `PASS` | [`host-assembleDebug.txt`](evidence-attempt-2/host-assembleDebug.txt), [`host-testDebugUnitTest.txt`](evidence-attempt-2/host-testDebugUnitTest.txt) |
| Launch / Main screen | `PASS` | `am start -W` returned `Status: ok`; [`01-main-initial.png`](evidence-attempt-2/01-main-initial.png) |
| Clock / date / city | `PASS` | Main showed live device clock, `10 августа`, and selected `Худжанд`/fixture-seeded `Khujand`; [`01-main-initial.png`](evidence-attempt-2/01-main-initial.png), [`05-main-weather-fixture.png`](evidence-attempt-2/05-main-weather-fixture.png) |
| Weather cards / redacted fixture path | `PASS` | Probe hierarchy reported `weather_refreshed_redacted`, `Weather: 21C/cloud`; Main showed the populated `21°` card. [`04-probe-weather-fixture.xml`](evidence-attempt-2/04-probe-weather-fixture.xml), [`05-main-weather-fixture.png`](evidence-attempt-2/05-main-weather-fixture.png) |
| Presets | `PASS` | Main visibly rendered `3 М`, `10 М`, `30 М`; first preset selected and started countdown. [`06-main-public-timer-start.png`](evidence-attempt-2/06-main-public-timer-start.png) |
| Public timer start | `PASS` | First preset produced visible `03:00` countdown at `20:21:18`. [`06-main-public-timer-start.png`](evidence-attempt-2/06-main-public-timer-start.png) |
| Public timer cancel | `PASS` | Two taps returned Main to idle clock at `20:21:19`. [`07-main-public-timer-cancel.png`](evidence-attempt-2/07-main-public-timer-cancel.png) |
| Overdue transition | `PASS` | Redacted probe reached `Timer: OVERDUE elapsed=1251 remaining=0`; Main restored orange overdue state with `00:00:33`. [`17-overdue-probe-observed.xml`](evidence-attempt-2/17-overdue-probe-observed.xml), [`18-main-overdue.png`](evidence-attempt-2/18-main-overdue.png) |
| Overdue dismissal | `PASS` | Tap on overdue surface returned idle Main. [`19-main-after-overdue-dismiss.png`](evidence-attempt-2/19-main-after-overdue-dismiss.png) |
| Settings navigation | `PASS` | Selected-city long-hold opened `Настройки`; hierarchy captured title, API-key field (empty), and location controls. [`10-settings-open.xml`](evidence-attempt-2/10-settings-open.xml), [`10-settings-open.png`](evidence-attempt-2/10-settings-open.png) |
| Settings Back | `PASS` | Android Back returned Main and preserved selected city/weather fixture. [`11-main-after-settings-back.png`](evidence-attempt-2/11-main-after-settings-back.png) |
| Background / foreground | `PASS` | Home moved focus to launcher; HOT return restored Main and persisted countdown (`03:00` → `02:55`). [`12-main-before-background.png`](evidence-attempt-2/12-main-before-background.png), [`13-home-background.png`](evidence-attempt-2/13-home-background.png), [`14-main-after-foreground.png`](evidence-attempt-2/14-main-after-foreground.png) |
| Cleanup | `PASS` | `force-stop`, `pm clear`, uninstall; final focus launcher; `pidof`, `pm path`, and package list empty. [`cleanup-uninstall.txt`](evidence-attempt-2/cleanup-uninstall.txt), [`cleanup-focus.txt`](evidence-attempt-2/cleanup-focus.txt), [`cleanup-pid.txt`](evidence-attempt-2/cleanup-pid.txt), [`cleanup-pm-path.txt`](evidence-attempt-2/cleanup-pm-path.txt), [`cleanup-package-list.txt`](evidence-attempt-2/cleanup-package-list.txt) |
| Live Yandex provider | `NOT RUN` | Deliberately excluded: no live credentials/request; no live-provider PASS. |

## Exact run timings

- `20:17:07`: run started; host build/test completed before device install.
- `20:17:27` → `20:17:31`: clean Main launch; `Status: ok`, `LaunchState: COLD`, `TotalTime: 3788 ms`.
- `20:19:57`: probe seeded Settings; `20:20:08` → `20:20:09`: redacted weather fixture refreshed.
- `20:20:34` → `20:20:39`: Main relaunch with fixture; screenshot showed `21°` card.
- `20:21:17` → `20:21:18`: public `3 М` preset started; `20:21:18` → `20:21:19`: double-tap cancellation completed.
- `20:21:47` and `20:22:32`: short taps on selected city correctly remained no-op; `20:23:12` → `20:23:14`: long-hold opened Settings.
- `20:23:35` → `20:23:36`: Android Back returned Main.
- `20:24:14`: countdown started; `20:24:16`: Home/background; `20:24:18` → `20:24:19`: foreground return, countdown visible at `02:55`.
- `20:25:06` → `20:25:10`: overdue probe launch; `20:25:12` start command; `20:25:14`: probe reported `OVERDUE elapsed=1251`.
- `20:25:41` → `20:25:46`: Main restored overdue visual; `20:25:47`: overdue dismissed.
- `20:26:53`: cleanup started and completed.

## Debug evidence and limitations

- Probe and Settings UI hierarchies were captured successfully: [`02-foundation-probe.xml`](evidence-attempt-2/02-foundation-probe.xml), [`04-probe-weather-fixture.xml`](evidence-attempt-2/04-probe-weather-fixture.xml), [`17-overdue-probe-observed.xml`](evidence-attempt-2/17-overdue-probe-observed.xml), [`10-settings-open.xml`](evidence-attempt-2/10-settings-open.xml).
- Main Display hierarchy dumps repeatedly returned `ERROR: could not get idle state` because the live Main ticker continuously schedules refreshes. This is a UI-dump tooling limitation already observed for this surface; Main screenshots and window-focus evidence were captured. Failed dump artifacts are retained as [`ui-dump-main-command.txt`](evidence-attempt-2/ui-dump-main-command.txt) and [`ui-dump-main-weather-command.txt`](evidence-attempt-2/ui-dump-main-weather-command.txt). It was not classified as an app defect.
- Logcat was captured before cleanup: [`device-logcat-full.txt`](evidence-attempt-2/device-logcat-full.txt), [`device-logcat-app-slice.txt`](evidence-attempt-2/device-logcat-app-slice.txt). No `FATAL EXCEPTION`, `AndroidRuntime`, or `Fatal signal` was found. Two `ActivityTaskManager ... app died` lines at `20:25:04.852` and `20:25:40.533` coincide with explicit reviewer `am force-stop` calls before probe/Main overdue relaunch and are not crash evidence.
- No credentials, API key, or live provider response entered the screenshots, hierarchy, or logcat. The Settings hierarchy explicitly shows `API-ключ не указан`.

## Reviewer report format

- verdict: `APPROVE` for offline/redacted physical-device smoke; live Yandex is `NOT RUN` by design.
- findings: requested launch, Main rendering, clock/date/city, fixture weather, presets, timer start/cancel/overdue/dismiss, Settings long-hold/Back, background/foreground, logging, and cleanup were observed on the authorized physical device. No reproducible product defect was established.
- evidence_checked: device identity, host build/unit, screenshots, UI hierarchies, exact timestamps, window focus, full/app-slice logcat, crash scan, and final package cleanup.
- risks_or_questions: live-provider compatibility and any behavior requiring real credentials remain intentionally unverified; Main hierarchy XML remains unavailable because `uiautomator` cannot reach idle on the 50 ms ticker surface. No indexed follow-up is recommended from this smoke run.

Production code, tests, task cards, lifecycle/status, scheduler checkpoint, terminal state, Memory Bank, and `/mb-sync` were not modified or invoked by this Reviewer session.
