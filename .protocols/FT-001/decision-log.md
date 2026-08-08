---
description: Decision log for FT-001 task decomposition.
status: active
last_updated: 2026-08-08
---
# FT-001 — Decision log

## 2026-08-06 — Tasking surface generated

- FT-001 is eligible for decomposition: PRD clarification is complete, the
  feature design status is `complete`, the Global Backbone is `complete` at
  Planning Revision `1`, and the Foundation Gate is `done`.
- One T3 task, `TASK-003-T3-FT-001-W2`, owns the cohesive Main Display outcome.
  T3 is required by the Android runtime/display boundary and the target-device
  evidence route. The task depends directly on
  `TASK-002-T3-FT-000-W1`; no dependency on future FT-002–FT-009 task cards is
  invented.
- Existing architecture, boundary, capability-interface, platform-runtime and
  runtime-verification specs are reused. No competing canonical spec,
  feature-owned design hub, or optional behavior-spec file is created.
- Exact UI toolkit, implementation class split, and package/file identity
  remain execution-level choices within the accepted current Android scaffold.
  No new dependency, public boundary, ownership rule, or product behavior was
  selected by this decomposition.

## 2026-08-08 — Generic-emulator layout follow-up

- Post-terminal Reviewer evidence proves a narrow runtime-layout defect on the
  documented `Tecno_Pova_6_API_35` generic emulator: the populated city and
  transient status/hint rows measure to zero height, so the accepted city
  gesture and Settings entry are unreachable. FT-001 AC-002/AC-005 and the
  existing Main Display → Settings & Location contract already settle the
  target; no UX, contract or global architecture decision is required.
- Create `TASK-014-T3-FT-001-W11` as one `planned` T3 follow-up after the
  completed sequential queue. It depends on `TASK-011-T3-FT-009-W10`, which
  transitively preserves TASK-003 and the closed Foundation gate, and owns only
  the generic-emulator runtime-layout/reachability delta for FT-001-AC-002 and
  FT-001-AC-005. TASK-003 remains `done` historical evidence and is not
  reopened, rewritten or replaced.
- Existing architecture, boundary, capability, platform-runtime and
  runtime-verification specs are reused. Mandatory clean build, host unit and
  static gates remain, while the documented generic emulator supplies the
  decisive supplementary non-zero-bounds/Settings-reachability proof. Samsung
  GT-I9300I Android 11 custom-ROM and 1280×720 behavior remain `DEFERRED`.
- A zero-height Foundation probe control may be observed only when the same
  minimum correction safely restores it without a second mechanism; this is a
  conditional implementation/testing side effect, not FT-000 product scope.
  Planning Revision remains `1`; scheduler checkpoint and terminal `SUCCESS`
  state remain untouched.

## 2026-08-08 — W12 public active-countdown dispatch repair

- Final W11 semantic evidence records a distinct public runtime regression:
  one non-city weather-card double tap at a 120 ms interval left an active
  countdown visible at approximately 350 ms. City double cancellation and
  delayed-Settings protection passed. The public scenario is retained as an
  existing downstream protected-cancellation contract (REQ-013; regression
  guard only; canonical basis in TASK-015 `normative_inputs`), not a new timer
  contract, W12 acceptance locator, or reopened W11 outcome.
- Create `TASK-015-T3-FT-001-W12` as one planned T3 repair task. It depends on
  the last successful `TASK-011-T3-FT-009-W10`; failed W11 remains negative
  evidence and is not a prerequisite or lifecycle target. Historical task
  identities and statuses remain unchanged.
- Select the smallest sufficient approach: one Main Display-local,
  stateful active-countdown dispatcher captures the public surface at
  `ACTION_DOWN` and keeps the same stream through `ACTION_UP/CANCEL` instead of
  rechecking live TimerLifecycleState for every event. It routes existing
  timer commands, retains city hold-to-Settings and leaves idle city, preset
  and overdue paths intact. No public contract, owner, module, graph edge,
  dependency, event/message boundary or Planning Revision changes.
- The task must add focused host stream support but treat it as supporting
  evidence only. Fresh generic-emulator public runtime scenarios are mandatory
  for city hold, city double with delayed-Settings protection, non-city single,
  non-city double, preset guards and safe cleanup. Samsung/custom-ROM/1280x720
  remains `DEFERRED`; scheduler checkpoint, terminal state and RTM lifecycle
  are outside this planning reconciliation.
