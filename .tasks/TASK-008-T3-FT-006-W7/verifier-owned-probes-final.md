---
description: Fresh verifier-owned final probes for TASK-008-T3-FT-006-W7.
status: final
---
# Fresh verifier-owned final probes — TASK-008-T3-FT-006-W7

## Basis and independence

- Fresh `ROLE: Reviewer` session; no current-attempt execute receipt was
  reused.
- Original attempt-1 RED, retry-2 failure/correction and attempt-3 correction
  were inspected as lineage and supporting evidence only.
- Current task-local production surface inspected: `DisplayCapability.kt`,
  `TimerCapability.kt`, `FoundationRuntime.kt` and `MainActivity.kt`; broad
  unrelated workspace changes were not attributed to this task.

## Fresh gates

- `./gradlew testDebugUnitTest --tests com.hozayushka.app.TimerLifecycleTest --rerun-tasks` — exit `0`.
- `./gradlew clean assembleDebug` — exit `0`, `BUILD SUCCESSFUL`.
- `./gradlew testDebugUnitTest --rerun-tasks` — exit `0`, `BUILD SUCCESSFUL`.
- `git diff --check` — exit `0`.
- `adb devices` — no device or emulator; target evidence is `DEFERRED`,
  non-blocking, and no runtime PASS is claimed.

The only compiler diagnostic was the pre-existing deprecated
`MainActivity.onBackPressed` warning.

## Claim-level observations

- AC-001 / REQ-012: the fresh focused suite observed selected-preset start,
  `COUNTDOWN`, owner-derived remaining time, persisted preset identity and
  active-origin presentation.
- AC-002 / REQ-011: replacement starts overwrite the single Timer store record
  and expose exactly one active preset through the Settings projection.
- AC-003 / REQ-013: the fresh focused suite observed single-tap preservation
  with hint and double-tap cancellation; the source probe additionally covered
  rebuilt weather-card routing.
- AC-004 / REQ-014: synthetic timestamps across a fresh Timer capability over
  the same isolated store observed `COUNTDOWN` before the duration boundary and
  `OVERDUE` after it; reboot recovery was not tested or claimed.
- AC-005 / REQ-025: with no provider/network input, overdue single-tap
  dismissal observed `OVERDUE -> IDLE`; rebuilt cards use the same route.

## Refresh listener regression probe

The probe extracted `refresh()` through the ticker boundary and asserted:

- one `cards.removeAllViews()`;
- one `val card = weatherCard(...)`;
- one `cards.addView(...)`;
- one `card.setOnTouchListener(activeTimerTouchListener)` after the add;
- one listener object, with initial cards and city also bound to it;
- existing single- and double-tap handlers remain present;
- the listener returns `false` in `IDLE` and forwards active timer events to
  `mainGestureDetector`.

Observed source locations: remove `DisplayCapability.kt:425`, add
`:428`, rebind `:434`. This proves every card recreated by `refresh()` is
rebuilt with the active Timer route, without duplicate listener attachment or
card accumulation. The host Timer tests prove the routed single-tap,
double-tap and overdue transitions; Android target dispatch remains deferred.

## Boundary and scope

Fresh checks found no Main Display private storage/provider/API-key access, no
composition-root timer business state, no new module/event boundary or
dependency, and no FT-007 overdue fullscreen/audio/ramp scope in the task-local
diff. Timer-owned private persistence and arithmetic remain inside Timer & Alert;
Main Display consumes its public projection/commands.

Evidence lineage: `.tasks/TASK-008-T3-FT-006-W7/red-baseline.md`,
`.tasks/TASK-008-T3-FT-006-W7/retry-correction-green.md`,
`.tasks/TASK-008-T3-FT-006-W7/attempt-3-refresh-listener-regression.md`,
`.tasks/TASK-008-T3-FT-006-W7/attempt-3-gates.md` and
`.protocols/TASK-008-T3-FT-006-W7/{context,plan,progress,handoff}.md`.
