---
description: Fresh independent semantic and architecture review of FT-002 W25.
status: final
task_id: TASK-MB-REVIEW-TASKS-PLAN
stage_id: S-TASKS-FT-002-W25
feature: FT-002
reviewed_task: TASK-028-T3-FT-002-W25
review_mode: fresh_independent_local
reviewed_planning_revision: 2
---
# FT-002 W25 planning review

REVIEWED_PLANNING_REVISION: 2

## Final verdict

FINAL_VERDICT: REJECT

## Architecture review

verdict: APPROVE

findings: none. The bounded local architecture review confirms Main Display
owns the visual composition, the existing Main Display → Weather Context read
contract is reused, and no new owner, edge, module, public contract,
orchestration path or WeatherCapability write is required.

evidence_checked: System Architecture, Boundary Map, Capability Interfaces,
Weather Card Presentation, FT-002 feature, IMPL-FT-002, W22 history, W24/W25
task cards, current DisplayCapability.kt and WeatherCapability.kt.

risks_or_questions: Samsung GT-I9300I / Android 11 custom-ROM 1280×720
readability, fullscreen, keep-screen-on and runtime Canvas compatibility remain
the accepted DEFERRED residual risk; no runtime PASS is claimed.

## Checks

- Structural PASS: 28/28 indexed task JSON records parse; the current task
  satisfies the task schema shape, has a unique resolving index entry, and the
  complete dependency graph is acyclic. `W25` is exactly
  `planned/T3/FT-002/W25`; dependency is exactly planned
  `TASK-027-T3-FT-001-W24`; Foundation Gate
  `TASK-002-T3-FT-000-W1` is `done`.
- Revision/design PASS: Global Backbone is `complete` at Planning Revision
  `2`; FT-002 design is `complete`; direct canonical paths resolve; no open
  design question or applicable blocking design row was found.
- Boundary PASS: `runtime_context.write_boundary` is exactly
  `app/src/main/kotlin/com/hozayushka/app/display/DisplayCapability.kt` and
  `app/src/test/kotlin/com/hozayushka/app/DisplayProjectionTest.kt`, both
  existing files. `WeatherCapability.kt` and its production directory are
  forbidden; provider, resources, settings, timer, forecast, app wiring,
  lifecycle, scheduler/checkpoint and terminal state are excluded.
- Current-source/history PASS: W22 is the completed six-state Canvas baseline;
  current code still exposes exactly `CLEAR`, `CLOUD`, `NEUTRAL_CLOUD`, `RAIN`,
  `SNOW`, `MOON`. Current Main Display pressure rendering is Unicode/TextView
  based (`DisplayCapability.kt:1648–1657`), while pressure calculation and
  direction/count remain in `WeatherCapability.kt:1003–1021` and projection
  delivery at `:518–532`.
- Handoff PASS: the card contains fresh RED/GREEN measurements, 223×444 and
  279×444 bounds, 1.15–1.30× CLEAR disk rubric, Canvas/Path arrows, explicit
  4–8 px stroke and shaft/head visibility, UP/DOWN/zero cases, no
  text/emoji/clipping/content overlap, safe rerun and no WeatherCapability
  production change. Clean build, host unit suite and static diff are required
  gates; target-device proof is explicitly `DEFERRED`.
- Execution PASS: no build, test, emulator/device/adb, network, provider or
  credential route was run; no reviewed code, task lifecycle, checkpoint or
  terminal state was changed.

## Blocking finding and owner

1. BLOCKER — AC/REQ ownership contradiction in
   `.memory-bank/tasks/plans/IMPL-FT-002.md:76`. The row labels the W25 claim
   as `AC-009 / REQ-006, REQ-008, REQ-023`, but the canonical
   `FT-002-AC-009` heading owns `REQ-005, REQ-022, REQ-023, REQ-026` only
   (`.memory-bank/features/FT-002-weather-cards-context.md:141–160`). The same
   plan correctly says at `:190–192` that W25 owns no new AC and that `REQ-008`
   is only the pressure-output contract; the task card likewise marks it as a
   presentation integration guard, not a recalculation or re-acceptance.
   This contradiction can make execution assign `REQ-008` (and `REQ-006`) to
   `AC-009` or invent an AC, violating the requested exact ownership and the
   pressure-calculation boundary. Repair owner:
   `/feature-to-tasks FT-002`; retain only `FT-002-AC-009` as W25's AC locator
   and list `REQ-008` as separate contract/regression evidence. Re-run
   `/review-tasks-plan FT-002` afterward.

## Handoff

REJECT routes to `/feature-to-tasks FT-002`, then a fresh
`/review-tasks-plan FT-002`. No lifecycle promotion, checkpoint, terminal
change, `/mb-sync` or execution route is authorized by this review.
