# Plan — TASK-014-T3-FT-001-W11

## Goal
Restore non-zero city and exercised populated transient-row bounds and prove the existing selected-city hold → Settings → system Back route on `Tecno_Pova_6_API_35`.

## Non-goals
- No redesign or accepted composition change.
- No FT-006 double-tap-anywhere correction.
- No FT-008/FT-009 Settings semantics or FT-000 product behavior.
- No API, architecture, owner, graph, dependency, planning, scheduler or terminal-state change.

## Inputs / source specs
- Task record: `.memory-bank/tasks/TASK-014-T3-FT-001-W11.task.json`
- Task index: `.memory-bank/tasks/index.json`
- Feature: `.memory-bank/features/FT-001-main-clock-display.md`
- REQ IDs: `REQ-002`, `REQ-004`, `REQ-023`

## Richer execution inputs
- Source Artifacts and Normative Inputs: task card fields, plus the runtime artifacts listed in `context.md`
- Verification Targets: exact TASK-014 task card fields

## Constraints / invariants (MUST / NEVER)
- MUST keep the dominant clock/date, exactly four weather-card positions, three preset positions, and existing city routing.
- MUST treat emulator evidence as generic Android 15/API35 only and preserve Samsung/custom-ROM risk.
- NEVER read credentials/private state, implement FT-006 cancellation, or touch forbidden lifecycle/scheduler/history scope.

## Scope
### In scope
- One local allocation correction in `DisplayCapability.kt`.
- Cheapest focused host regression in existing `DisplayProjectionTest.kt`.
- Task-owned protocol and emulator evidence.

### Out of scope
- Every task anti-goal and forbidden scope entry.

## Proposed changes
### Touched areas
- `app/src/main/kotlin/com/hozayushka/app/display/DisplayCapability.kt` — correct header allocation only.
- `app/src/test/kotlin/com/hozayushka/app/DisplayProjectionTest.kt` — focused allocation regression.

### Preflight-confirmed change surface
- Expected hints kept: both advisory files above.
- Additional same-outcome files/areas: only task-owned protocol/evidence.
- Hard `write_boundary` present and satisfied: not set.
- `forbidden_scope` / stop-condition check: clear.

## Applicable quality gates
- [x] Attempt 2: `./gradlew clean assembleDebug`
- [x] Attempt 2: `./gradlew testDebugUnitTest` (53/53)
- [x] Attempt 2: `git diff --check`
- [x] Attempt 2: current APK install/cold start and task-owned emulator bounds/screenshots/interactions
- [x] Attempt 3: focused delayed-navigation regression
- [x] Attempt 3: `./gradlew clean assembleDebug`
- [x] Attempt 3: `./gradlew testDebugUnitTest` (54/54)
- [x] Attempt 3: `git diff --check`
- [x] Attempt 3: current APK install/hash/cold start and full task-owned emulator bounds/screenshots/interactions

## Claim-linked RED / GREEN (T2/T3)
- applicability: applicable
- accepted claim locators: `FT-001-AC-002 / REQ-002 / REQ-023`; `FT-001-AC-005 / REQ-004`
- planned probe/environment: current then corrected debug APK on running `emulator-5554`, exact generic Google API35/x86_64 identity; `dumpsys activity top`, screenshots and public UI interactions only
- observable RED: city/status/hint rows at bottom == top; selected-city hold cannot navigate
- corresponding GREEN: populated rows bottom > top; selected-city hold opens Settings and system Back returns; composition remains present
- accepted not-applicable reason and alternative proof: none
- T3 isolation/safe rerun/cleanup: synthetic retained Khujand/redacted fixture, no credentials/private state; finish normal MainActivity awake, timer idle, emulator running

## Retry attempt 2
- retain original RED: `.tasks/TASK-014-T3-FT-001-W11/attempt-1-red.md`; no second RED
- failed-gate binding: `.protocols/TASK-014-T3-FT-001-W11/red-verification.md` and the matching RED-VERIFY final report
- bounded correction: city-specific active-countdown detector adds only the existing city long-hold route while retaining timer single/double actions; non-city listeners remain unchanged
- fresh GREEN: focused public-contract host regression plus current-APK emulator proof of non-city single-tap hint, active-countdown city hold → Settings → system Back to countdown, non-city double-tap cancel and safe idle cleanup
- evidence: `.tasks/TASK-014-T3-FT-001-W11/attempt-2-green.md` and `attempt-2-host-gates.md`

## Retry attempt 3
- final retry: scheduler-authorized attempt 3; no fourth attempt is permitted
- retain history: original attempt-1 RED and both prior semantic-fail reports remain unchanged
- failed-gate binding: attempt 3 is bound to the attempt-2 delayed city-double-tap Settings defect in `.protocols/TASK-014-T3-FT-001-W11/red-verification.md` and the matching RED-VERIFY final report
- bounded correction: Main Display retains delivery of an active-countdown city touch stream through `ACTION_UP`/`ACTION_CANCEL` after the accepted double-tap cancellation changes Timer state to `idle`, preventing the detector's queued long-press callback from surviving the completed double tap
- focused regression: exercise the second tap's terminal event after Timer cancellation and observe no delayed city navigation beyond the long-press timeout; this is supporting host evidence, while the current-APK emulator probe remains decisive
- fresh GREEN: city hold → Settings → Back to still-active countdown; non-city single/double; city double tap → idle with Main Display still focused beyond the long-press threshold; original layout composition and all mandatory host/runtime gates

## MB-SYNC handoff / owner
- Owner: scheduler after `/verify` and required `/red-verify`; `/exe` does not close or sync.

## Definition of done
- Original attempt-1 RED is retained, attempt 3 is bound to the attempt-2 failed semantic gate, fresh claim-equivalent GREEN and every mandatory gate pass, and task-owned evidence is reproducible for `/verify`.
