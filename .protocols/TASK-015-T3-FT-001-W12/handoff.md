---
description: Execution handoff for TASK-015 bounded Main Display active-countdown dispatch repair.
status: active
---
# Handoff — TASK-015-T3-FT-001-W12

## Summary
- `PASS_FOR_HANDOFF`: one internal Main Display active-countdown dispatcher now captures streams at `ACTION_DOWN` and preserves terminal delivery through `ACTION_UP`/`ACTION_CANCEL` across root/background, weather cards, city and presets.
- Scope is limited to Main Display active-countdown dispatch and focused host support; no lifecycle closure is performed.

## Where to look
- key files:
  - `app/src/main/kotlin/com/hozayushka/app/display/DisplayCapability.kt`
  - `app/src/test/kotlin/com/hozayushka/app/DisplayProjectionTest.kt`
  - `.protocols/TASK-015-T3-FT-001-W12/progress.md`
  - `.tasks/TASK-015-T3-FT-001-W12/`
- advisory `touched_files` deviations and rationale: none known; protocol/evidence files are workflow-owned.
- hard write-boundary compliance: not set; semantic forbidden scope remains enforced.

## How to run / verify
- gates:
  - `./gradlew clean assembleDebug`
  - `./gradlew testDebugUnitTest`
  - `git diff --check`
  - `/home/serg/Android/Sdk/platform-tools/adb -s emulator-5554 shell dumpsys activity top`
- claim-linked RED/GREEN evidence:
  - current Attempt 1 baseline and pre-change classification: `attempt-1-prechange.md`
  - focused host stream GREEN: `attempt-1-focused-host.txt`
  - host/build/static gates: `attempt-1-host-gates.md`
  - decisive generic public matrix and cleanup: `attempt-1-runtime-matrix.md`
- current-attempt reuse candidate locators: none
- superseded/supporting-only receipt locators: W11 historical evidence and this executor evidence are supporting-only; no reuse candidate is offered because emulator state and repository inputs are broad/external.

## Known issues
- Physical Samsung/custom-ROM/1280×720 evidence remains deferred and must not be promoted.
- The historical W11 non-city RED was not reproduced in this attempt's pre-change run; it is not backfilled. Independent `/verify` must still prove current task outcome.

## Follow-ups
- After execution handoff: `/verify TASK-015-T3-FT-001-W12`; then required `/red-verify TASK-015-T3-FT-001-W12`; lifecycle owner handles status/sync.
