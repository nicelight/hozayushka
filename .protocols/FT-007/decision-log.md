---
description: Decision log for FT-007 overdue state and alert task decomposition.
status: active
last_updated: 2026-08-12
---
# FT-007 — Decision log

## 2026-08-14 — Operator-authorized active-countdown containment route bounded as W35

- The operator reports a real physical RED observation on TECNO LI6 serial
  `1156725456009666` after installing the current APK: the three-minute preset
  enters countdown with weather/date hidden and the timer rail visible, but the
  countdown digits are severely oversized and clipped. Evidence:
  `/tmp/hozayushka-physical-smoke-countdown-2026-08-14.png`. This is not a
  formal PASS, and overdue behavior was not tested in that smoke.
- Add the minimally sufficient feature criterion
  `FT-007-AC-007 / REQ-012 / REQ-023` for complete active-countdown digit
  containment. The outcome is distinct from completed W28
  `FT-007-AC-006`; W8/W23/W28 behavior, status and evidence remain unchanged.
- Create one `T3` task, `TASK-038-T3-FT-007-W35`, owned by Main Display and
  directly dependent only on successful `TASK-037-T3-FT-001-W34`, the current
  successful two-file Main Display baseline. W31 remains `done`, W32 remains
  `failed`, W33 remains `blocked`, and W34 remains `done`; this route does not
  rewrite or bypass their historical identities or evidence.
- The hard write boundary remains exactly
  `DisplayCapability.kt` plus `DisplayProjectionTest.kt`. Host RED/GREEN must
  measure the complete formatted countdown string (`MM:SS` and `HH:MM:SS`)
  against the available timer surface at `1280x720` and `2460x1080`; no fixed
  font ratio, dp target or unrelated UI change is selected.
- Existing Timer & Alert countdown arithmetic/cancellation, overdue rendering,
  audio request/start/repeat/stop and silent/DND/route behavior are read-only
  regressions. Future TECNO physical RED/GREEN is allowed only through the
  authorized serial route; if unavailable, physical readability is
  `DEFERRED` with residual risk. No emulator/AVD/QEMU, production code, device
  work, scheduler checkpoint, lifecycle/REQ state or terminal state changes are
  made by this planning run.
- Reuse the registered Main Display presentation, boundary, capability,
  platform-runtime, lifecycle and runtime-verification specs. No new canonical
  spec, graph edge, public contract, dependency beyond W34, permission,
  behavior-spec or event boundary is created. Next route:
  `/review-tasks-plan FT-007`.

## 2026-08-12 — Operator-authorized overdue presentation route bounded as W28

- The operator authorizes one visual overdue follow-up based on the completed
  W27 presentation reference: use a dedicated no-weather/no-city/no-date/
  no-standard-card surface, keep the full elapsed digits materially larger than
  idle and larger than active countdown where the existing display geometry
  permits, reuse the activating preset color in a transparent neon circular
  backdrop, and keep the `+` visibly blinking.
- Add the minimally sufficient feature criterion
  `FT-007-AC-006 / REQ-015 / REQ-023` for this residual visual outcome. Existing
  W8 AC-001…AC-005 ownership and W23's audio integration ownership remain
  unchanged; W28 owns only AC-006.
- Create one `T3` task, `TASK-031-T3-FT-007-W28`, directly dependent on the
  completed `TASK-030-T3-FT-006-W27` and owned by Main Display. Its hard write
  boundary is exactly `DisplayCapability.kt` plus `DisplayProjectionTest.kt`.
  TimerCapability, TimerAlertPolicy, PlatformRuntimeAdapter and existing
  any-tap/lifecycle/audio behavior are read-only regression owners.
- Host claim-linked visual evidence is mandatory. Target/device/audio are
  explicitly `DEFERRED`; no fake audio or host geometry result may claim
  physical audibility or runtime `PASS`. No production code, lifecycle,
  checkpoint or terminal state is changed by this planning run.
- Reuse the registered architecture, boundary, capability-interface,
  lifecycle, platform-runtime and runtime-verification specs. No new canonical
  spec, graph edge, dependency, event boundary, permission or behavior-spec
  file is created. Next route: `/review-tasks-plan FT-007`.

## 2026-08-12 — Operator missing-beep observation bounded as W23 follow-up

- The operator reports that the alarm did not beep when the timer reached zero.
  This is accepted as a new completion-to-audio observation; it does not alter
  the `idle|countdown|overdue` lifecycle, one-active-timer rule,
  cancellation/dismissal semantics or selected signal/volume/ramp/repeat/cap
  policy.
- Preserve `TASK-009-T3-FT-007-W8` as terminal `done` with its functional,
  semantic and historical evidence unchanged. Do not repair by mutating or
  reopening W8.
- Create one `T3` follow-up, `TASK-026-T3-FT-007-W23`, depending only on W8.
  Timer & Alert remains the owner; the bounded surface is the existing
  `TimerCapability` → `PlatformRuntimeAdapter` request/start path plus
  `OverdueAlertTest`.
- Host fake-platform/audio-scheduler evidence must prove the zero-crossing
  request/fake start, accepted repeat cadence, dismissal stop and denial/error
  behavior. Physical audibility on a target/custom ROM is a separate
  observation; without a device it remains `DEFERRED` and cannot become runtime
  `PASS` by host inference.
- No new spec, feature AC, graph edge, dependency, scheduler framework,
  permission, network path, production change, lifecycle/status update,
  scheduler checkpoint or terminal-state transition is selected here. Next
  route: `/review-tasks-plan FT-007`.

## 2026-08-10 — Revision-2 plan reconciled without a task

- Provider migration does not change overdue presentation or alert policy.
- W8 remains `done` with unchanged identity, dependency and evidence; queue
  action is `reconciled` and no follow-up is created.
- Exact next owner is fresh `/review-tasks-plan --all`.

## 2026-08-07 — Clean task surface generated

- FT-007 is eligible for decomposition: feature design is `complete`, the
  Global Backbone is `complete` at Planning Revision `1`, and the Foundation
  Gate `TASK-002-T3-FT-000-W1` is `done`.
- The previously removed FT-007 product task surface has no retained indexed
  task, plan or task-owned evidence. This run therefore creates a clean
  surface; the queue action is `created`.
- One T3 task, `TASK-009-T3-FT-007-W8`, owns the cohesive overdue outcome:
  fullscreen visual state, full elapsed counter, any-tap dismissal and the
  permitted repeatable built-in alert policy. It depends directly on the
  approved `TASK-008-T3-FT-006-W7`; Foundation remains transitive and no
  dependency on future FT-008 or FT-009 is invented.
- T3 is required by production Android runtime/display behavior, mutable timer
  lifecycle integration, platform audio policy, target-ROM evidence and the
  30-minute alert cap. The card retains claim-linked RED/GREEN proof and
  creates no runtime evidence during planning.
- Existing architecture, boundary, capability-interface, local-data,
  lifecycle, platform-runtime and runtime-verification specs are reused. No
  competing canonical path, feature-owned design hub, graph edge, public
  contract or optional behavior-spec file is created.
- Timer & Alert remains the sole owner of overdue state and alert requests;
  Main Display renders the public projection and submits dismissal; Android
  owns silent/DND and route permission. The task consumes the accepted
  Settings projection and default built-in signal without adding the later
  FT-009 Settings surface.
- No new dependency, storage owner, product decision or Planning Revision
  decision is selected by this decomposition. The fresh handoff route is
  `/review-tasks-plan FT-007`.
