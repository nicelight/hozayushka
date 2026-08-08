---
description: Fresh claim-equivalent GREEN evidence for the TASK-007 retry correction.
status: supporting
---
# Correction GREEN — attempt 2

- attempt: `2`
- receipt_status: `supporting-only`
- completed_at: `2026-08-08 05:53 +0500`

## Claim and correction

- Claim: `FT-005-AC-002 / REQ-011`; rejected timer preset input preserves the
  last-valid owner state and the Settings editor displays those corresponding
  valid values after validation failure.
- Correction: `SettingsCapability.timerPresetEditor` restores the rejected
  result's returned duration into all three `EditText` fields and suppresses
  recursive `TextWatcher` callbacks during that restoration.
- Historical RED source: `.tasks/TASK-007-T3-FT-005-W6/red-baseline.md` and
  the independent semantic-fail report; retained unchanged from attempt 1.

## Focused probe

- command: `./gradlew testDebugUnitTest --tests com.hozayushka.app.TimerPresetTest.rejectedEditorUpdateRestoresLastValidDurationValues --rerun-tasks`
- cwd: `/home/serg/Projects/Mobile_APPS/hozayushka`
- exit_code: `0`
- input basis: repository `HEAD` `a93e46118f0f0b90e311b6174e3f5a8ed7d89fef` plus the pre-existing dirty/untracked workspace recorded in `context.md`, with attempt-2 changes in `SettingsCapability.kt`, `TimerPresetTest.kt` and the task protocol.
- observed result: rejected `2:60:6` after accepted `2:4:6` returned the last-valid
  duration `2:4:6`; `editorFieldValues()` produced `"2"`, `"4"`, `"6"`; the
  owner projection remained `TimerPresetDuration(2, 4, 6)`.
- evidence: focused JUnit task passed; the production editor uses the same
  returned duration helper for all three field restorations.
