---
description: Independent verifier-owned probes for TASK-008 countdown lifecycle.
status: final
---
# Verifier-owned probes — TASK-008-T3-FT-006-W7

## Host gates

- `./gradlew clean assembleDebug` — exit `0`, `BUILD SUCCESSFUL`; only the
  pre-existing `MainActivity.onBackPressed` deprecation warning was emitted.
- `./gradlew testDebugUnitTest --rerun-tasks` — exit `0`, `BUILD SUCCESSFUL`.
- `./gradlew testDebugUnitTest --tests com.hozayushka.app.VerifierOwnedFt006ProbeTest --rerun-tasks` — exit `0`, `BUILD SUCCESSFUL`.
  The temporary probe was removed after execution; it did not change
  production code or durable task state.
- Exact boundary/redaction scan and `git diff --check` — exit `0`.
- `adb devices` — no target listed; target evidence remains `DEFERRED` and
  non-blocking, with no runtime `PASS` claim.

## Verifier-owned timer observations

Using fresh in-memory Settings/Timer stores and fixed timestamps:

- A selected validated preset starts in `COUNTDOWN`; changing the Settings
  projection afterward does not alter the persisted duration or active preset
  identity; the store still contains one record.
- At `startedAt + duration - 1 ms` state is `COUNTDOWN`; at the exact duration
  boundary it is `OVERDUE`. Any-tap dismissal reaches `IDLE`; subsequent
  gestures cannot resurrect the timer.
- Repeated starts replace the single persisted record. A single tap preserves
  `COUNTDOWN`; a double tap dismisses and clears the record.

These observations independently support AC-001/002/003/004/005 core Timer &
Alert behavior. They do not by themselves prove Android view dispatch.

## UI/read projection and dispatch inspection

- `DisplayCapability.refresh` deterministically derives countdown text from the
  Timer projection, shows the countdown view, reduces the clock row and applies
  active-origin button styling (`DisplayCapability.kt:382-425`).
- The root GestureDetector sends single/double taps to Timer
  (`DisplayCapability.kt:339-355`), and preset buttons have their own detector
  (`:321-336`).
- The Main Display also installs interactive child handlers that do not call
  Timer: city uses `setOnClickListener`/`setOnLongClickListener`
  (`:357-367`), while weather cards use `setOnClickListener`
  (`:716-724`). Because the root listener returns `false` and these children
  consume their own touch/click paths, a tap/double-tap on those supported
  Main Display targets does not reach `applyTimerGesture`.

This leaves the accepted `double tap anywhere` cancellation and overdue `any
tap` dismissal unproven and concretely bypassed on city/weather-card paths;
the task contract does not scope those gestures to only non-interactive empty
background.
