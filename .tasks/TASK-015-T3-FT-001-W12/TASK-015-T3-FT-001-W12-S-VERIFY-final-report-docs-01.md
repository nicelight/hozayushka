---
description: Fresh independent functional verification report for TASK-015 Attempt 1.
status: final
task_id: TASK-015-T3-FT-001-W12
stage_id: S-VERIFY
feature: FT-001
attempt: 1
---
# Verification report — TASK-015-T3-FT-001-W12

## Result

Fresh Reviewer verification passed the current Attempt 1 outcome for the
task-owned `FT-001-AC-005 / REQ-004` city hold/Settings-preservation delta and
the indexed `REQ-013` protected-cancellation regression guard.

Independent gates:

- `./gradlew clean assembleDebug` — PASS, exit 0; APK SHA-256
  `d1f8634227c758de4e424e37aa18f863afe5623ee1b794484946606b4039bb30`;
- `./gradlew testDebugUnitTest` — PASS, 54/54 with 0 failures, errors or skips;
- `git diff --check` — PASS;
- required `dumpsys activity top` — exit 0; complementary activity/window
  evidence confirmed focused `MainActivity`.

Fresh generic Android public evidence passed for selected-city short tap,
city hold → Settings → system Back to active countdown, city double cancellation
with no delayed Settings beyond the long-press timeout, non-city single hint,
non-city double cancellation, preset start/single/double, overdue dismissal,
four-card/three-preset guard and final safe cleanup.

## Boundary verdict

The diff is limited to Main Display production code and its focused host test.
No Timer & Alert ownership, arithmetic, persistence, lifecycle, overdue
semantics, Settings semantics, graph edge, module, event boundary or public
contract drift was observed. W11 artifacts were not used as proof; the current
Attempt 1 pre-change GREEN remains honestly classified as supporting evidence.

## Evidence paths

- Protocol: [verification.md](../../.protocols/TASK-015-T3-FT-001-W12/verification.md)
- Detailed verifier evidence: [verifier-owned-evidence-attempt-1.md](verifier-owned-evidence-attempt-1.md)
- Runtime screenshots: `verifier-attempt-1-*.png` in this directory

## Target/deferred evidence and residual risk

The authorized runtime was generic Google Android 15/API35 x86_64 on
`Tecno_Pova_6_API_35`. Samsung GT-I9300I Android 11 custom-ROM, 1280x720
physical geometry, readability/system bars, lifecycle and audio evidence remain
`DEFERRED` and are not promoted from emulator evidence. The AVD had transient
cold-start/ADB instability during setup; the successful repeated run is the
recorded runtime proof.

Task status remains `in_progress`. No lifecycle closure, scheduler edit,
`/red-verify`, `/mb-sync`, retry or promotion was performed.

VERDICT: PASS
