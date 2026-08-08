# Tecno Pova 6 API 35 independent runtime/UX review — partial handoff

- Role: Reviewer.
- Run: 2026-08-08, `Asia/Dushanbe`.
- Status: paused at operator request after completing the active lifecycle interaction.
- Scope: fresh observations on the single running `emulator-5554`; no production code, task JSON, lifecycle, scheduler, or terminal state was changed.
- Release boundary: this is supplementary generic Google Android 15/API 35 emulator evidence only. Samsung GT-I9300I (`s3ve3gds`), Android 11 custom ROM, 1280x720 remains `DEFERRED`.

## Partial verdict

`REQUEST_CHANGES` for the observed generic-emulator UX surface.

Runtime identity, normal cold launch, landscape rendering, the synthetic
redacted fixture route, visible timer start, active-button protected cancel,
temporary background/resume, and a bounded screen-off/on recovery completed.
Material Main Display layout and gesture defects block the accepted Settings
entry and prevent credible completion of Settings, volume-zero, forecast, and
clean known-initial overdue flows in this paused run.

## Material findings

### HIGH — city and status/hint rows collapse to zero height

On the actual 2436x1080 landscape app surface, `dumpsys activity top` reports
the city gesture view, timer cancellation hint, and forecast availability
message at `0,516-2152,516`. The city is absent from screenshots and has no
touch area. A long press at the expected location and keyboard focus traversal
did not open Settings. This violates FT-001 city visibility/interaction and
blocks runtime verification of FT-008/FT-009 Settings behavior.

- Evidence: `03-overdue-dismissed-main.png`, `04-settings-open.png`,
  `05-settings-keyboard-route.png`, `06-settings-longkey-route.png`, and
  `adb-snippets-redacted.md`.
- Likely owner: Main Display layout/gesture surface (`DisplayCapability`), with
  downstream impact on Settings & Location entry.

### HIGH — accepted double-tap-anywhere cancellation is not available across the surface

After starting the visible 3-minute preset, a single and double tap at
`1218,400` left countdown active (`02:58` then `02:57`). A single tap on the
active preset preserved countdown, and a double tap on that same button did
cancel it. The cancellation hint could not be seen because its view has zero
height. This is narrower than the FT-006 accepted “single/double tap anywhere”
contract and removes the promised visible guidance.

- Evidence: `10-countdown-started.png` through
  `14-countdown-button-double-tap-cancelled.png`, plus the zero-height hierarchy
  in `adb-snippets-redacted.md`.
- Likely owner: Main Display gesture routing and layout.

### MEDIUM — documented Foundation reset/audio probe controls are unreachable

The supported probe rendered `Audio Probe` and `Cancel and Reset` at
`48,1048-2388,1048`, giving both zero height. UIAutomator exposed only the four
preceding visible buttons; keyboard focus cycled among visible controls. The
documented seed and redacted fixture path worked, but the same runtime surface
did not provide the advertised safe reset or audio-policy action.

- Evidence: `07-foundation-probe-before-reset.uix.xml`,
  `08-redacted-fixture.uix.xml`, `09-foundation-reset.uix.xml`, and
  `adb-snippets-redacted.md`.
- Likely owner: Foundation probe UI / Main Display probe surface.

## Completed flow outcomes

| Flow | Generic emulator result | Evidence |
|---|---|---|
| New runtime-doc AVD/profile facts and current identity | `PASS` | Exact AVD, Google runtime props, API/ABI, size/density and rotation match `runtime-verification.md`; see `adb-snippets-redacted.md`. |
| Cold normal launch and resumed Activity | `PASS` | `am start -W -S` returned `Status: ok`, `LaunchState: COLD`; final resumed app PID is `5050`. |
| Landscape Main Display and dominant clock | `PASS` for generic emulator observation | `03-overdue-dismissed-main.png`, `18-safe-normal-main.png`; Samsung 1280x720 readability remains `DEFERRED`. |
| Main Display city shell / Settings entry | `FAIL` | City view has zero height; accepted short/long route cannot be exercised. |
| Seed + redacted weather fixture | `PASS` | `08-redacted-fixture.png`/`.uix.xml`: `weather_refreshed_redacted`, `21C/cloud`; no credential was used. |
| Timer start/countdown | `PASS` | `10-countdown-started.png`. |
| Protected cancellation | `FAIL` against “anywhere”; button-local path works | `10`–`14` screenshots. |
| Temporary background/resume | `PASS` for same-process generic-emulator observation | `15-lifecycle-countdown-before.png` (`01:46`) and `16-lifecycle-background-resume.png` (`01:40`), same PID `5050`. |
| Screen-off/on recovery | `PASS` for bounded generic-emulator observation | `mWakefulness=Asleep` then `Awake`; `17-lifecycle-screen-off-on.png` shows recalculated `01:33`, same PID. |
| Overdue visual + dismissal | `PARTIAL` | An inherited overdue state was visibly observed and dismissed (`02`–`03`), but it was not generated from this run's known initial state. |
| Settings defaults/validation/persistence, synthetic key/location, volume `0` | `DEFERRED` | Accepted Settings entry is unreachable; no alternate/private-state bypass was used. |
| Hourly/10-day forecast entry/return | `DEFERRED` | Not started before pause; zero-height forecast message is already evidenced, but no forecast PASS/FAIL is claimed. |
| Logcat fatal/ANR and audio-policy/ramp/cap review | `NOT_RUN` | Exact resume point below. |
| Samsung/custom-ROM/1280x720 release behavior | `DEFERRED` | The generic Google AVD must not replace the physical release target. |

## Commands used

```bash
adb devices -l
adb -s emulator-5554 emu avd name
adb -s emulator-5554 shell getprop <selected-runtime-property>
adb -s emulator-5554 shell wm size
adb -s emulator-5554 shell wm density
adb -s emulator-5554 shell am start -W -S -n com.hozayushka.app/.app.MainActivity
adb -s emulator-5554 shell am start -W -S -n com.hozayushka.app/.app.MainActivity --ez foundation_probe true
adb -s emulator-5554 shell uiautomator dump --compressed <temporary-device-path>
adb -s emulator-5554 shell dumpsys activity top
adb -s emulator-5554 shell dumpsys activity activities
adb -s emulator-5554 shell dumpsys display
adb -s emulator-5554 shell dumpsys power
adb -s emulator-5554 shell input tap|swipe|keyevent ...
adb -s emulator-5554 shell screencap -p <temporary-device-path>
adb -s emulator-5554 pull <temporary-device-path> .protocols/RUNTIME-VERIFICATION/
```

Every temporary remote screenshot/XML path used by the completed captures was
removed after pull. UIAutomator could not dump the continuously updating Main
Display (`ERROR: could not get idle state`); static Foundation probe dumps
succeeded. Main Display claims therefore use screenshots plus Android's
`dumpsys activity top` view hierarchy, and no UIAutomator PASS is inferred.

## Docs consistency

- The newly added supplementary target facts in
  `.memory-bank/testing/runtime-verification.md` match the fresh AVD config and
  runtime props.
- Its Samsung/custom-ROM distinction is preserved in this verdict.
- The doc's supported Foundation probe description is only partially true at
  runtime: seed/fixture are reachable, while reset/audio controls collapse to
  zero height.
- `mb-lint` was not started before the operator pause; no lint verdict is
  claimed.

## Safe state and exact resume point

The active lifecycle scenario was completed and its timer was cancelled. Final
state: emulator running and awake, `sys.boot_completed=1`, AVD
`Tecno_Pova_6_API_35`, app PID `5050`, normal `MainActivity` resumed/focused,
timer idle, synthetic Khujand/redacted weather fixture retained, no credential
entered or exposed. Evidence: `18-safe-normal-main.png`.

Resume without repeating completed flows:

1. Reconfirm the same serial/AVD and safe normal Main Display.
2. Run only the pending host grounding (`assembleDebug`/applicable tests and APK
   identity if needed), `mb-lint` for the new doc, and bounded redacted logcat.
3. Do not claim Settings/volume-zero PASS unless the accepted Settings entry
   becomes credibly reachable; record the existing defect instead of bypassing
   private state.
4. If continuing device work is still requested, generate a fresh known-state
   1-second overdue/recovery chain through the public Foundation path where
   possible, then attempt forecast entry/return only if a user-reachable route
   exists. Keep Samsung/custom-ROM outcomes `DEFERRED`.
