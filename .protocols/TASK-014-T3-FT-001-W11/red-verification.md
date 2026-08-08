---
description: Independent adversarial semantic verification for TASK-014 attempt 3.
status: final
task_id: TASK-014-T3-FT-001-W11
feature: FT-001
tier: T3
attempt: 3
---
# Red Verification — TASK-014-T3-FT-001-W11

## Accepted intent and inspected basis

The accepted outcome is the corrected non-zero Main Display city/transient-row
surface, selected-city hold through the existing Settings route, city double-tap
cancellation without delayed Settings, and unchanged non-city Timer cancellation.

Inspected independently:

- `.memory-bank/tasks/TASK-014-T3-FT-001-W11.task.json` and its FT-001/REQ-002,
  REQ-004, REQ-023 mapping;
- direct canonical ownership, capability, platform-runtime and runtime-verification
  specs; FT-006 protected-cancellation contract;
- current source/diff surface and attempt-3 hashes:
  `DisplayCapability.kt` `8b72f3f...`, `DisplayProjectionTest.kt`
  `4afb5a6...`, `MainActivity.kt` `737c489...`;
- current installed public runtime on `emulator-5554`, generic
  `sdk_gphone64_x86_64` / `emu64xa`, Android 15 API 35.

`ActiveTimerCityTouchStream` remains Main Display-local and retains an active
city stream through terminal `ACTION_UP`/`ACTION_CANCEL` (source lines
187–200, 500–507). City routing still invokes only `onOpenSettings`; the
existing `MainActivity` callback remains `::renderSettingsSurface` (lines
432–435 and `46`). No Settings/FT-006 private-state access, storage bypass,
new owner/edge/contract, or second layout mechanism was found. No
Samsung/custom-ROM/1280x720 PASS claim is made.

## Runtime semantic probes

- Active countdown + selected-city double tap at the visible city target
  cancelled to ordinary idle: at the approximately 300 ms checkpoint the
  public view showed the normal clock and the countdown view was `GONE`; after
  an additional 800 ms, MainActivity remained resumed/focused with no Settings
  ScrollView. This also covered the delayed long-press timeout path.
- Active countdown + one public non-city double tap on a visible weather-card
  surface (`800,900`, 120 ms interval) did not cancel: after approximately
  350 ms the public view still showed the countdown and active layout (`0,0–2152,838`,
  countdown visible; city `0,629–2152,726`). MainActivity remained focused and
  Settings was absent. This is a material break of the unambiguous FT-006
  protected double-tap cancellation contract, not a speculative concern.
- Safe cleanup then used the already-working public city double-tap route;
  final public state was idle MainActivity, countdown `GONE`, Settings absent,
  emulator awake and attached. No credential or private state was read or
  mutated.

## Finding

`BLOCKER` — the current installed attempt-3 runtime did not cancel an active
countdown after the required non-city double tap. The accepted outcome is not
semantically satisfied even though the city-specific delayed-navigation path
passed.

## Evidence paths

- `.memory-bank/tasks/TASK-014-T3-FT-001-W11.task.json`
- `.memory-bank/features/FT-001-main-clock-display.md`
- `.memory-bank/features/FT-006-countdown-lifecycle.md`
- `.memory-bank/contracts/boundary-map.md`
- `.memory-bank/contracts/capability-interfaces.md`
- `.memory-bank/contracts/platform-runtime.md`
- `.memory-bank/testing/runtime-verification.md`
- `.protocols/TASK-014-T3-FT-001-W11/verification.md`
- `.tasks/TASK-014-T3-FT-001-W11/verifier-owned-evidence-attempt-3.md`
- `.tasks/TASK-014-T3-FT-001-W11/attempt-3-city-double-no-delayed-settings.png`
- `.tasks/TASK-014-T3-FT-001-W11/attempt-3-noncity-double-idle.png`

## Handoff

Task/lifecycle/scheduler state was not changed. Recommended scheduler action:
withhold T3 semantic closure and keep the task open for the active owner to
repair or explain the non-city cancellation path, then rerun fresh functional
`/verify` and `/red-verify` before closure. No Samsung/custom-ROM follow-up is
promoted by this result.
