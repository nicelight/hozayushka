---
description: L3 feature for the completed timer's overdue presentation and permitted alert sound.
status: draft
id: FT-007
epic: EP-003
lifecycle: implemented
spec_design_status: complete
spec_design_links:
  - .memory-bank/contracts/boundary-map.md
  - .memory-bank/contracts/capability-interfaces.md
  - .memory-bank/contracts/main-display-presentation.md
  - .memory-bank/contracts/platform-runtime.md
  - .memory-bank/domains/local-data.md
  - .memory-bank/states/lifecycle-map.md
  - .memory-bank/testing/runtime-verification.md
last_updated: 2026-08-14
---
# FT-007 — Overdue state and alert

## Product outcome

После окончания countdown владелец получает заметное fullscreen overdue state,
может быстро его отключить и слышит повторяющийся сигнал только если это
разрешено Android.

## Requirements

- REQ-015, REQ-016.

## Use cases

1. Владелец видит, что timer завершён, по fullscreen neon state и blinking `+`.
2. Владелец видит полный elapsed counter без мигания числового значения.
3. Владелец отключает overdue state одним или двойным тапом в любом месте.
4. Владелец слышит выбранный repeatable signal с ramp, если silent/DND не
   запрещают его.

## Acceptance criteria

### FT-007-AC-001 — Fullscreen overdue presentation

- REQ: REQ-015
- At zero, the active preset expands into the accepted fullscreen neon area with
  its color; `+` blinks and the numeric counter does not.

### FT-007-AC-002 — Full elapsed counter and persistent visual state

- REQ: REQ-015
- Counter shows the full elapsed time from timer start, including configured
  duration; visual overdue state remains until a tap dismisses it.

### FT-007-AC-003 — Tap dismissal

- REQ: REQ-015
- Single or double tap anywhere disables the overdue state and returns to main
  display.

### FT-007-AC-004 — Repeatable built-in alert policy

- REQ: REQ-016
- Completion starts the selected built-in signal (`Классический`, `Колокольчик`
  or `Электронный`, default `Классический`) with accepted 5–10 second ramp,
  repeats until dismissal but no longer than 30 minutes.

### FT-007-AC-005 — Audio suppression does not suppress visual overdue state

- REQ: REQ-016
- Audio follows Android silent mode and DND permissions; visual overdue state is
  always shown even when audio is suppressed.

### FT-007-AC-006 — Dedicated overdue presentation hierarchy

- REQ: REQ-015, REQ-023
- The overdue projection uses a dedicated surface with no weather cards, city,
  date or standard card-shell content. Its full elapsed digits remain stable,
  are materially larger than the idle clock and, where the existing active-
  countdown geometry permits without clipping or overlap, are materially larger
  than the active countdown. The transparent neon circular backdrop uses the
  activating preset's existing color identity; the blinking `+` and any-tap
  dismissal remain available.

### FT-007-AC-007 — Active countdown digits fit the timer surface

- REQ: REQ-012, REQ-023
- During an active countdown, the complete formatted remaining value (`MM:SS`,
  or `HH:MM:SS` when hours are present) MUST remain readable and contained
  inside the available dedicated timer surface at the accepted landscape
  geometry, with no clipping or overlap. The countdown MUST retain the
  activating preset's existing neon color identity and separate timer rail;
  weather/date/city and standard card-shell content remain hidden on the
  countdown surface. Countdown cancellation, overdue presentation and alert
  audio behavior remain unchanged.

## Edge / failure behavior

- Audio denial, silent mode, DND or an unavailable audio route never removes the
  visual overdue state or blocks dismissal.
- The alert stops on accepted manual dismissal or its 30-minute audio cap;
  product behavior does not imply a cap on the visual state before dismissal.
- Overdue recovery after temporary process interruption uses the same lifecycle
  state: the visual overdue projection, any-tap dismissal and permitted alert
  policy are re-established from the persisted timer data; this does not
  introduce reboot restoration.

## Sources and normative inputs

- [.memory-bank/prd.md](../prd.md): `PRD-FR-029`–`PRD-FR-031`, `PRD-AC-005`,
  `PRD-AC-008`.
- [.memory-bank/invariants.md](../invariants.md): visual overdue and OS-owned
  audio permission rules.
- [.memory-bank/states/lifecycle-map.md](../states/lifecycle-map.md): transition
  from `countdown` to `overdue` and dismissal.
- [.memory-bank/contracts/capability-interfaces.md](../contracts/capability-interfaces.md):
  Main Display → Timer & Alert any-tap dismissal and Timer & Alert → Settings &
  Location validated sound/volume projection.
- [.memory-bank/contracts/main-display-presentation.md](../contracts/main-display-presentation.md):
  canonical Main Display geometry, timer-rail separation and claim-linked
  visual measurement route for the active countdown follow-up.
- [.memory-bank/contracts/boundary-map.md](../contracts/boundary-map.md): Android
  audio responsibility hint.

## Verification targets

- `PRD-AC-005`, `PRD-AC-008`, `PRD-FR-029`–`PRD-FR-031`, including resumed
  overdue proof after temporary process interruption.
- `FT-007-AC-006`, including same-size host comparison with idle and active
  countdown presentation and explicit target/device/audio deferral.
- `FT-007-AC-007`, including measured full-string containment at the canonical
  host sizes, the operator-observed physical RED/GREEN route when available,
  and explicit physical-target deferral when it is not.

## SDD Design Gate

Global backbone is complete at Planning Revision `2` and the Foundation Gate
anchors remain closed; feature-level design remains complete. Its indexed
task-plan review is stale only by revision mismatch and is reconciled through
`/feature-to-tasks --all` after Foundation revalidation.

Applicable global specs: [Boundary Map](../contracts/boundary-map.md),
[Capability Interfaces](../contracts/capability-interfaces.md), [Platform Runtime](../contracts/platform-runtime.md),
[Local Data](../domains/local-data.md), [Lifecycle Map](../states/lifecycle-map.md)
and [Runtime Verification](../testing/runtime-verification.md).
The alarm/audio mechanism and feature test level remain downstream.

## W8 implementation evidence

The W8 boundary records `TASK-009-T3-FT-007-W8` as `done` after retry-2 fresh
functional `PASS` and T3 semantic `semantic-pass`. The evidence covers the
fullscreen active-preset overdue projection, full elapsed counter, any-tap
dismissal, built-in signals, ramp/repeat/cap behavior, silent/DND/route
suppression and temporary-resume re-establishment. See the [functional report](../../.tasks/TASK-009-T3-FT-007-W8/TASK-009-T3-FT-007-W8-S-VERIFY-final-report-docs-01.md),
[semantic report](../../.tasks/TASK-009-T3-FT-007-W8/TASK-009-T3-FT-007-W8-S-RED-VERIFY-final-report-docs-01.md)
and [verifier-owned probe](../../.tasks/TASK-009-T3-FT-007-W8/verifier-owned-probe.md).
Target-device evidence remains `DEFERRED` and non-blocking with residual risk;
no runtime `PASS` is claimed. FT-007 lifecycle is now `implemented`; no feature
closure, promotion or dependent-state transition is inferred by this boundary
sync.

## 2026-08-12 operator observation and bounded follow-up

The operator reports that the alarm did not beep when the timer reached zero.
This is a new completion-to-audio observation, not a revision of the accepted
`idle|countdown|overdue` lifecycle, one-active-timer rule, cancellation/
dismissal behavior or selected signal/volume/ramp/repeat/cap policy. The W8
terminal `done` result and all of its history remain unchanged.

The follow-up is indexed as
`TASK-026-T3-FT-007-W23`, owned by Timer & Alert across the existing Android
Runtime Adapter boundary. It is limited to the existing TimerCapability /
PlatformRuntimeAdapter request/start path and `OverdueAlertTest`; no new audio
framework, dependency, event bus, permission or network path is authorized.
Host fake-platform/audio-scheduler proof must distinguish request emission and
fake start/repeat/stop from actual audible custom-ROM behavior. Without a
target observation, audio remains `DEFERRED` and no runtime `PASS` is claimed.

W23 is now `done` after fresh executor `PASS_FOR_HANDOFF`, functional `PASS`
and independent T3 semantic `semantic-pass`. The deterministic host evidence
proves the first overdue request/start, repeat boundary, any-tap dismissal,
30-minute cap and all six denial/error cases, including volume zero and safe
ToneGenerator creation/start errors. `HOST_FAKE_RESULT=PASS` is explicitly
separate from `PHYSICAL_AUDIBILITY=DEFERRED`; no device/runtime audio PASS is
claimed. See the [scheduler trace](../../.tasks/TASK-026-T3-FT-007-W23/scheduler-trace.md),
[denial/error matrix](../../.tasks/TASK-026-T3-FT-007-W23/denial-error-matrix.md),
[functional verification](../../.tasks/TASK-026-T3-FT-007-W23/TASK-026-T3-FT-007-W23-S-VERIFY-final-report-docs-01.md)
and [semantic verification](../../.tasks/TASK-026-T3-FT-007-W23/TASK-026-T3-FT-007-W23-S-RED-VERIFY-final-report-docs-01.md).

## 2026-08-12 operator-authorized overdue presentation follow-up

The operator authorizes one visual route based on the completed W27 presentation
reference: overdue gets a dedicated no-weather/no-city/no-date/no-standard-card
surface, the full elapsed digits receive the strongest available hierarchy, and
the transparent neon circular backdrop reuses the activating preset color. This
is recorded as `FT-007-AC-006`; it does not revise Timer & Alert ownership of
elapsed arithmetic, lifecycle transitions or any-tap dismissal, and it does not
revise the accepted audio policy.

The route is indexed as `TASK-031-T3-FT-007-W28`, directly after the completed
`TASK-030-T3-FT-006-W27`. Main Display owns the two-file presentation boundary
(`DisplayCapability.kt` and `DisplayProjectionTest.kt`); W23's completed
Timer & Alert/Android Runtime Adapter result and the lifecycle/dismissal
contracts remain read-only regression owners. Any need to touch those owners,
change audio behavior, or add a new boundary is a stop-and-replan condition.

Host visual evidence is mandatory: fresh claim-linked RED/GREEN, same-size idle
and active-countdown comparisons, and a named visual-QA rubric. Target,
physical-device and audio evidence are explicitly `DEFERRED`; host geometry or
fake audio must not be promoted to physical audibility or runtime PASS. No
production code, runtime evidence, lifecycle/checkpoint or terminal state is
changed by this planning boundary.

## W28 implementation evidence

The W28 boundary records `TASK-031-T3-FT-007-W28` as `done` after executor
`PASS_FOR_HANDOFF`, fresh functional `PASS` and independent T3 semantic
`semantic-pass`. At the fixed `1280x720` host comparison size, the dedicated
overdue surface hides weather cards, city, date and standard card-shell content;
elapsed digits are stable at `256.0`, larger than idle `188.75` and active
countdown `228.0`, while the large plus is `280.0` and blinks independently.
The transparent circular backdrop preserves the activating preset color
(`#FF4FA3` in the accepted sample), and the measured bounds fit without
clipping or overlap.

See the [W28 sync report](../../.tasks/TASK-031-T3-FT-007-W28/TASK-031-T3-FT-007-W28-S-MB-SYNC-final-report-docs-01.md),
[executor handoff](../../.tasks/TASK-031-T3-FT-007-W28/TASK-031-T3-FT-007-W28-S-EXE-final-report-code-01.md),
[functional verification](../../.tasks/TASK-031-T3-FT-007-W28/TASK-031-T3-FT-007-W28-S-VERIFY-final-report-docs-01.md),
[semantic verification](../../.tasks/TASK-031-T3-FT-007-W28/TASK-031-T3-FT-007-W28-S-RED-VERIFY-final-report-docs-01.md)
and [task evidence](../../.tasks/TASK-031-T3-FT-007-W28/). Timer & Alert,
lifecycle, any-tap dismissal and W23 audio ownership remain unchanged and
read-only for W28. Target readability/fullscreen/custom-ROM lifecycle and
physical audio remain `DEFERRED`; no runtime/device/physical-audibility `PASS`
is claimed. FT-007 lifecycle remains `implemented`; no feature or epic closure,
promotion or dependent-state transition is inferred by this boundary sync.

## Separate W34 residual

The W34 Main Display allocation closure explicitly leaves oversized timer-digit
sizing as a separate future FT-007 presentation concern. This note does not
change FT-007 lifecycle, `FT-007-AC-006`, Timer & Alert ownership or any W34
acceptance claim; it remains a later operator-authorized route.

## 2026-08-14 operator-authorized active-countdown presentation follow-up

The operator reports a real presentation defect on TECNO LI6 serial
`1156725456009666` after installing the current APK: tapping the three-minute
preset opens the dedicated countdown surface with weather/date hidden and the
timer rail visible, but the countdown digits are severely oversized and the
formatted value is clipped. The screenshot
`/tmp/hozayushka-physical-smoke-countdown-2026-08-14.png` is task-scoped RED
observation only, not a formal PASS; overdue behavior was not tested in this
smoke.

This residual is bounded as `FT-007-AC-007 / REQ-012 / REQ-023`. It is separate
from the completed W28 overdue presentation (`FT-007-AC-006`) and does not
reopen or duplicate W8/W23/W28 behavior. Main Display remains the presentation
owner; FT-006/Timer & Alert retain countdown arithmetic, cancellation,
overdue/lifecycle and alert ownership.

The follow-up is indexed as
`TASK-038-T3-FT-007-W35`, directly after the successful W34 Main Display
baseline. It requires fresh host RED/GREEN from the existing geometry/projection
path at `1280×720` and `2460×1080`, using full `MM:SS` and `HH:MM:SS` cases and
actual measured text/ink bounds rather than a scalar font-size comparison.
The authorized TECNO route is a future execution-time physical RED/GREEN
target; if that target is unavailable, the task records physical readability
as `DEFERRED` with residual risk. Host evidence never becomes runtime PASS,
and no emulator/AVD/QEMU route is valid.
