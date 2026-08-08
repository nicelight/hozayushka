---
description: Independent verification protocol for TASK-008-T3-FT-006-W7.
status: final
---
# Verification — TASK-008-T3-FT-006-W7

## Basis and executor claim path

The indexed T3 task owns the accepted claims FT-006-AC-001 / REQ-012,
FT-006-AC-002 / REQ-011, FT-006-AC-003 / REQ-013,
FT-006-AC-004 / REQ-014 and FT-006-AC-005 / REQ-025. Direct normative inputs were the task-linked capability,
boundary, architecture, local-data, lifecycle, platform-runtime, invariants
and runtime-verification specs. Timer & Alert remains the owner of persistence,
arithmetic and lifecycle transitions; Main Display routes public commands and
projection only; Settings remains the validated preset owner; reboot recovery
and FT-007 presentation/audio are excluded.

Attempt-1 claim-specific RED is retained at
`.tasks/TASK-008-T3-FT-006-W7/red-baseline.md`. Retry-2 retained that RED and
corrected the child dispatch path; fresh re-verification found the remaining
`refresh()` rebinding defect. Attempt-3 corrected only that task-local path.
These historical executor results are supporting evidence, not independent
proof.

## Reused execute evidence

None. No current-attempt receipt had a conservatively bounded read surface, so
no receipt was reused.

## Fresh repeated checks

- `./gradlew testDebugUnitTest --tests com.hozayushka.app.TimerLifecycleTest --rerun-tasks` — exit `0`.
- `./gradlew clean assembleDebug` — exit `0`, `BUILD SUCCESSFUL`.
- `./gradlew testDebugUnitTest --rerun-tasks` — exit `0`, `BUILD SUCCESSFUL`.
- Ownership/boundary/redaction scan — exit `0`.
- `git diff --check` — exit `0`.

The only compiler diagnostic was the pre-existing deprecated
`MainActivity.onBackPressed` warning. Broad unrelated workspace changes were
not attributed to this task.

## New verifier-owned probes

The isolated Timer suite confirmed:

- selected valid preset starts immediately in `COUNTDOWN`, with owner-derived
  remaining time, persisted preset identity and active-origin projection;
- replacement starts leave exactly one active stored record;
- single tap preserves countdown and exposes the hint, while double tap clears
  the record and returns `IDLE`;
- a fresh Timer capability rehydrates `COUNTDOWN` before the exact duration
  boundary and `OVERDUE` afterward;
- without provider/network input, overdue any-tap dismissal returns `IDLE`.

The refresh-specific source probe mapped to AC-003/AC-005 asserted one
`removeAllViews()`, one rebuilt card, one `addView()`, and one
`card.setOnTouchListener(activeTimerTouchListener)` after the add in
`DisplayCapability.kt:425-434`. The same single listener object remains bound
to city and initial cards; it returns `false` only for `IDLE` and forwards active
events to the main GestureDetector. Thus refreshed weather-card instances retain
the active Timer route, with no duplicate attachment or card accumulation.

Full fresh command output and claim mapping are recorded in
`.tasks/TASK-008-T3-FT-006-W7/verifier-owned-probes-final.md`.

## Device evidence

`adb devices` returned only the header. Target-ROM dispatch, screen-off and
temporary interruption/display observation are `DEFERRED` and non-blocking; no
runtime PASS is claimed. Host and source evidence therefore prove the accepted
deterministic lifecycle/routing claims without silently promoting device proof.

## Scope and architecture checks

Current task-local diff uses the existing Main Display → Timer & Alert and
Timer & Alert → Settings edges. No private Settings/provider access,
composition-root timer business state, new dependency/event boundary, secret
exposure or FT-007 overdue fullscreen/audio/ramp scope was observed.

## Verdict

VERDICT: PASS

## Handoff

- Functional verification complete; required next gate for T3 is the separate
  per-task `/red-verify`.
- Task lifecycle, task card, planning/spec files, scheduler checkpoint and
  prerequisites were not changed by this verification.
