# Progress — TASK-014-T3-FT-001-W11

## Current status
- state: ready for independent verification after retry attempt 3
- last update: 2026-08-08

## Retry attempt 3 binding
- scheduler authorization: final retry attempt 3 after attempt-2 functional `VERDICT: PASS` and required semantic `semantic-fail`; no fourth attempt is permitted
- original RED retained: `.tasks/TASK-014-T3-FT-001-W11/attempt-1-red.md`; no new RED is manufactured
- attempt-1 and attempt-2 execute evidence status: `supporting-only`
- attempt-2 failure evidence: `.protocols/TASK-014-T3-FT-001-W11/red-verification.md` and `.tasks/TASK-014-T3-FT-001-W11/TASK-014-T3-FT-001-W11-S-RED-VERIFY-final-report-docs-01.md`
- exact defect: public city double tap during countdown cancels Timer by 250 ms, then opens Settings by 750 ms without a hold because the city detector does not receive the gesture's terminal event after Timer becomes `idle`
- correction claim: retain only the already-started city detector stream through `ACTION_UP`/`ACTION_CANCEL`; preserve genuine hold, idle city routing, non-city single/double timer routing, layout and public contracts
- fresh evidence obtained: focused delayed-navigation regression, mandatory host gates, current-APK hash/install and public runtime proof beyond the long-press threshold

## Retry attempt 2 binding
- scheduler authorization: retry attempt 2 after attempt-1 functional `VERDICT: PASS` and required semantic `semantic-fail`
- original RED retained: `.tasks/TASK-014-T3-FT-001-W11/attempt-1-red.md`; no second RED is manufactured
- failed semantic evidence: `.protocols/TASK-014-T3-FT-001-W11/red-verification.md` and `.tasks/TASK-014-T3-FT-001-W11/TASK-014-T3-FT-001-W11-S-RED-VERIFY-final-report-docs-01.md`
- confirmed correction claim: during active countdown, the visible selected-city hold must still route through the existing FT-001 `onOpenSettings` callback; timer single-tap hint and double-tap cancellation remain unchanged, with non-city surfaces retaining their current listener
- attempt-1 execute receipts/evidence status: `supporting-only`; fresh attempt-2 GREEN and every mandatory gate are due

## What was done
- Preflight resolved the indexed ready task, done dependency, current Planning Revision 1 approval, direct T3 specs, proof path, dirty baseline and local allocation cause.
- Initialized attempt 1 and durably moved only TASK-014 `ready -> in_progress` before any prospective probe or implementation.
- Built and installed the exact pre-change APK, normalized an inherited synthetic overdue state through the public dismiss gesture, and captured current-attempt claim-specific RED.
- Changed only Main Display vertical allocation: header now measures existing content and weather cards receive the remaining weighted height.
- Added the focused pure host policy regression, obtained real emulator GREEN for normal city, populated timer hint and populated forecast message, and exercised selected-city hold → Settings → system Back.
- Ran the required clean build/full host suite, installed the clean-built APK, repeated the navigation route, and restored the required safe emulator state.
- Retry attempt 2 preserved attempt-1 RED and bound the correction to the required semantic-fail evidence.
- Added one city-specific active-countdown detector: single/double taps still call the existing timer commands, while long press reaches the existing FT-001 city Settings route. Root, weather-card and preset listeners were not changed.
- Added one focused host regression using only public Timer/City routing APIs.
- Built and installed the fresh attempt-2 APK on the authorized generic emulator, then proved non-city single-tap hint, countdown city-hold → Settings, Back to the still-active countdown, non-city double-tap cancellation and final idle cleanup.
- Retry attempt 3 retained all prior RED/failure history and bound the correction to the attempt-2 delayed post-double-tap Settings defect.
- Added one Main Display-local touch-stream guard: only an active-countdown city `ACTION_DOWN` captures the detector stream, and that captured stream remains delivered through `ACTION_UP`/`ACTION_CANCEL` even if accepted double-tap cancellation changes Timer state to `idle`.
- Added one focused host regression covering the terminal event and absence of delayed navigation beyond the long-press timeout.
- Built/installed the fresh attempt-3 APK and obtained current runtime GREEN for non-city single/double, genuine active-countdown city hold/Back, city double cancellation with Main Display retained beyond the long-press threshold, forecast row, original layout and final safe state.

## Commands run (with results)
- Read-only context/source/runtime inspection → OK; exact commands will be consolidated in task evidence.
- `./gradlew assembleDebug` → exit 0 (`BUILD SUCCESSFUL`), pre-change setup.
- `adb -s emulator-5554 install -r app/build/outputs/apk/debug/app-debug.apk` → exit 0 (`Success`).
- `adb ... am start -W -S -n com.hozayushka.app/.app.MainActivity` → cold-start OK.
- Identity, `dumpsys activity top`, screenshot and public hold/focus checks → decisive RED; see `attempt-1-red.md`.
- `./gradlew testDebugUnitTest --tests com.hozayushka.app.DisplayProjectionTest` → exit 0.
- `./gradlew clean assembleDebug` → exit 0.
- `./gradlew testDebugUnitTest` → exit 0; 52 tests, 0 failures/errors/skips.
- `git diff --check` → exit 0, no output; final static integrity gate passed.
- Clean-built APK install/cold start, Main Display/transient bounds, city hold, Settings screenshot and system Back → GREEN; see `attempt-1-green.md`.
- `./gradlew testDebugUnitTest --tests com.hozayushka.app.DisplayProjectionTest.activeCountdownKeepsCityHoldAlongsideProtectedTimerTaps` → exit 0, focused attempt-2 GREEN.
- Attempt 2 `./gradlew clean assembleDebug` → exit 0; fresh APK SHA-256 `271fef0a097f6efa77769100cf3f819603e8a8e6e7c658ff828ef992e501ec0c`.
- Attempt 2 `./gradlew testDebugUnitTest` → exit 0; 53 tests, 0 failures/errors/skips.
- Attempt 2 `git diff --check` → exit 0, no output.
- Attempt 2 APK install/current-hash match/cold start and public runtime sequence → GREEN; see `attempt-2-green.md`.
- `./gradlew testDebugUnitTest --tests com.hozayushka.app.DisplayProjectionTest.cityDoubleTapCannotLeaveDelayedSettingsAfterLongPressTimeout` → exit 0, focused attempt-3 GREEN.
- Attempt 3 `./gradlew clean assembleDebug` → exit 0; fresh APK SHA-256 `5cfb17a4c3d192b44583dce678b342588361bac35fb3bfd5ddf97e84820a7b80`.
- Attempt 3 `./gradlew testDebugUnitTest` → exit 0; 54 tests, 0 failures/errors/skips.
- Attempt 3 `git diff --check` → exit 0, no output.
- Attempt 3 APK install/current-hash match/cold start and public runtime sequence → GREEN; see `attempt-3-green.md`.

## Claim-linked RED / GREEN (T2/T3)
### Attempt 1 — supporting-only
- attempt: 1
- applicability: applicable
- accepted claim locators: `FT-001-AC-002 / REQ-002 / REQ-023`; `FT-001-AC-005 / REQ-004`
- accepted not-applicable reason and alternative proof: none
- RED command/probe: pre-change build/install/cold start; `adb ... dumpsys activity top`; `adb ... input swipe 300 540 300 540 800`; focused-activity checks.
- RED observation and evidence: city/timer hint/forecast message all `0,516-2152,516`; city absent; hold cannot open Settings. `.tasks/TASK-014-T3-FT-001-W11/attempt-1-red.md` and `red-main.png`.
- GREEN command/probe: focused/full host tests plus clean build; clean-built APK install/cold start; `dumpsys activity top`; public preset/today/city hold/system Back interactions; task-owned screenshots.
- GREEN observation and evidence: city `97 px`, populated timer hint `59 px`, populated forecast message `53 px`; dominant clock/date, four cards and three presets present; hold opened Settings and Back returned. `.tasks/TASK-014-T3-FT-001-W11/attempt-1-green.md`.
- claim-equivalent probe changes and rationale: RED and GREEN use the same documented AVD, view-hierarchy/screenshot method and city hold route; GREEN additionally populates each transient row through public UI and repeats navigation after final clean install.
- T3 isolation/cleanup/permission evidence: exact authorized generic AVD identity; public UI and retained synthetic Khujand/redacted fixture only; no credential/private-state access; remote screenshot temporaries removed; final state awake normal MainActivity/timer idle.

### Attempt 2 — supporting-only after semantic failure
- attempt: 2
- applicability: applicable correction of `FT-001-AC-005 / REQ-004`; attempt-1 layout GREEN remains supporting context
- RED source/result: original `.tasks/TASK-014-T3-FT-001-W11/attempt-1-red.md` retained; no second RED manufactured
- retry correction basis: required semantic failure in `.protocols/TASK-014-T3-FT-001-W11/red-verification.md` and `.tasks/TASK-014-T3-FT-001-W11/TASK-014-T3-FT-001-W11-S-RED-VERIFY-final-report-docs-01.md`
- correction: dedicated active-countdown city detector routes long press through existing `CityInteractionRouter`/`onOpenSettings`; single/double callbacks still call the accepted Timer & Alert commands; all non-city listeners remain unchanged
- focused host GREEN: public `TimerCapability` single tap retained countdown plus hint, city hold mapped to `OPEN_SETTINGS` without changing timer state, and double tap returned idle
- runtime GREEN: current installed APK on exact `Tecno_Pova_6_API_35` generic Google API35/x86_64 runtime showed non-city single-tap hint, 800 ms city hold → Settings, system Back → still-active countdown, and non-city double-tap → idle
- probe changes and rationale: attempt 2 adds the countdown state omitted by functional attempt-1 verification and directly reproduces the semantic-fail route; no private state or Settings mutation was used
- evidence: `.tasks/TASK-014-T3-FT-001-W11/attempt-2-green.md`, `attempt-2-host-gates.md` and four attempt-2 screenshots
- T3 isolation/cleanup: retained synthetic/redacted Khujand/weather state only; no credential/private-state access; remote temporaries removed; emulator left awake with normal MainActivity focused and timer idle

### Attempt 3 — current final retry
- attempt: 3
- applicability: applicable correction of `FT-001-AC-005 / REQ-004`, with fresh regression coverage of `FT-001-AC-002 / REQ-002 / REQ-023`
- RED source/result: original `.tasks/TASK-014-T3-FT-001-W11/attempt-1-red.md` retained; no new RED manufactured
- retry correction basis: attempt-2 semantic failure in `.protocols/TASK-014-T3-FT-001-W11/red-verification.md` and `.tasks/TASK-014-T3-FT-001-W11/TASK-014-T3-FT-001-W11-S-RED-VERIFY-final-report-docs-01.md`
- correction: active-countdown city touch-stream ownership is fixed at `ACTION_DOWN` and retained through `ACTION_UP`/`ACTION_CANCEL`, so a Timer state change during double tap cannot strand the detector's queued long press
- focused host GREEN: second-tap terminal delivery after Timer cancellation clears the modeled pending long press; no delayed Settings after the 600 ms timeout
- runtime GREEN: current installed APK preserved non-city single/double and genuine city hold/Back; city double returned idle by the 250 ms checkpoint and remained on Main Display after a further 750 ms, with no Settings `ScrollView`
- layout GREEN: city `97 px`, timer-hint row `59 px`, forecast row `53 px`, dominant clock/date, four cards and three presets retained
- evidence: `.tasks/TASK-014-T3-FT-001-W11/attempt-3-green.md`, `attempt-3-host-gates.md` and eight attempt-3 screenshots
- probe changes and rationale: attempt 3 adds the exact delayed post-double-tap checkpoint omitted by attempt-2 execution; actual Android dispatch remains emulator-decided
- T3 isolation/cleanup: public UI and retained synthetic/redacted state only; no private-state access; remote temporaries removed; emulator running/awake with normal MainActivity focused and timer idle

## Reuse Candidates (optional)
- None.

## Evidence links
- `.tasks/TASK-014-T3-FT-001-W11/attempt-1-red.md`
- `.tasks/TASK-014-T3-FT-001-W11/red-main.png`
- `.tasks/TASK-014-T3-FT-001-W11/setup-inherited-overdue.png`
- `.tasks/TASK-014-T3-FT-001-W11/attempt-1-green.md`
- `.tasks/TASK-014-T3-FT-001-W11/attempt-1-host-gates.md`
- `.tasks/TASK-014-T3-FT-001-W11/TASK-014-T3-FT-001-W11-S-EXE-final-report-code-01.md`
- `.protocols/TASK-014-T3-FT-001-W11/red-verification.md`
- `.tasks/TASK-014-T3-FT-001-W11/TASK-014-T3-FT-001-W11-S-RED-VERIFY-final-report-docs-01.md`
- `.tasks/TASK-014-T3-FT-001-W11/attempt-2-green.md`
- `.tasks/TASK-014-T3-FT-001-W11/attempt-2-host-gates.md`
- `.tasks/TASK-014-T3-FT-001-W11/attempt-2-timer-hint.png`
- `.tasks/TASK-014-T3-FT-001-W11/attempt-2-settings-during-countdown-hold.png`
- `.tasks/TASK-014-T3-FT-001-W11/attempt-2-back-to-countdown.png`
- `.tasks/TASK-014-T3-FT-001-W11/attempt-2-double-tap-cancelled.png`
- `.tasks/TASK-014-T3-FT-001-W11/TASK-014-T3-FT-001-W11-S-EXE-final-report-code-02.md`
- `.tasks/TASK-014-T3-FT-001-W11/attempt-3-green.md`
- `.tasks/TASK-014-T3-FT-001-W11/attempt-3-host-gates.md`
- `.tasks/TASK-014-T3-FT-001-W11/attempt-3-main.png`
- `.tasks/TASK-014-T3-FT-001-W11/attempt-3-timer-hint.png`
- `.tasks/TASK-014-T3-FT-001-W11/attempt-3-settings-during-countdown-hold.png`
- `.tasks/TASK-014-T3-FT-001-W11/attempt-3-back-to-countdown.png`
- `.tasks/TASK-014-T3-FT-001-W11/attempt-3-noncity-double-idle.png`
- `.tasks/TASK-014-T3-FT-001-W11/attempt-3-city-double-no-delayed-settings.png`
- `.tasks/TASK-014-T3-FT-001-W11/attempt-3-forecast-message.png`
- `.tasks/TASK-014-T3-FT-001-W11/attempt-3-final-main.png`
- `.tasks/TASK-014-T3-FT-001-W11/TASK-014-T3-FT-001-W11-S-EXE-final-report-code-03.md`

## Open issues / risks
- Samsung GT-I9300I Android 11 custom-ROM/1280x720 geometry and interaction remain deferred.
- No reuse candidates: broad shared dirty inputs and external emulator state make all executor evidence supporting-only.

## Next step (single concrete action)
- `/verify TASK-014-T3-FT-001-W11`.
