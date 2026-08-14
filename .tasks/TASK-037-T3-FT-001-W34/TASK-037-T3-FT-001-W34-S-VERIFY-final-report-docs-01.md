---
description: Independent functional verification report for TASK-037-T3-FT-001-W34.
status: final
task_id: TASK-037-T3-FT-001-W34
stage_id: S-VERIFY
feature: FT-001
tier: T3
---
# Verification report — TASK-037-T3-FT-001-W34

## verdict

Fresh verifier-owned host and physical evidence proves the W34 mixed-state
outcome. The exact W34 behavior boundary is the two declared files; the broad
dirty worktree is separated as pre-existing state.

## evidence_checked

- W34 task card, FT-001-AC-002, REQ-001/REQ-002/REQ-005/REQ-023, direct Main
  Display Presentation contract, boundary/capability/platform/runtime specs,
  tier policy, and W31/W32/W33 history.
- W34 context/plan/progress/handoff, executor handoff, all W34 task evidence,
  W31 successful baseline, W32 failed disposition and W33 blocked record.
- Fresh offline clean build, focused/full host tests, lint and diff check.
- Fresh read-only TECNO probe and screenshot on serial `1156725456009666`;
  no emulator/AVD/QEMU, reinstall, provider/network or credential action.

## claim result

The host mixed fixture retains an empty Yesterday and populated `14/15/16`
slots while proving the accepted geometry at `2460x1080` and `1280x720`:
`0.27962962/0.7203704` and `0.27916667/0.7208333`, equal heights and common
bottoms. Same-device physical GREEN confirms four `302px` cards ending at
`1056` in the actual `2460x1080` landscape frame. The screenshot preserves
complete HH:mm, city/date, fixed slot order and three separate timer controls;
no clipping/overlap is visible.

The executor's pre-write RED remains supporting claim-path evidence: physical
Yesterday `834px` versus populated `302px`, with the same empty/14/15/16
fixture. It is not used as the independent GREEN proof.

## handoff

T3 functional verification is complete. Route to `/red-verify TASK-037-T3-FT-001-W34`;
leave task status, task card, scheduler checkpoint, terminal state, W31/W32/W33
history and `/mb-sync` unchanged.

## evidence paths

- `.protocols/TASK-037-T3-FT-001-W34/verification.md`
- `.tasks/TASK-037-T3-FT-001-W34/verifier-owned-evidence.md`
- `.tasks/TASK-037-T3-FT-001-W34/verifier-green.png`
- `.tasks/TASK-037-T3-FT-001-W34/geometry-red.json`
- `.tasks/TASK-037-T3-FT-001-W34/geometry-green.json`
- `.tasks/TASK-037-T3-FT-001-W34/mixed-state-matrix-red.json`
- `.tasks/TASK-037-T3-FT-001-W34/mixed-state-matrix-green.json`
- `.tasks/TASK-037-T3-FT-001-W34/physical-visual-receipt.md`
- `.tasks/TASK-037-T3-FT-001-W34/physical-visual-receipt-green.md`
- `.tasks/TASK-037-T3-FT-001-W34/host-gates.md`
- `.tasks/TASK-037-T3-FT-001-W34/boundary-static-review.md`

VERDICT: PASS
