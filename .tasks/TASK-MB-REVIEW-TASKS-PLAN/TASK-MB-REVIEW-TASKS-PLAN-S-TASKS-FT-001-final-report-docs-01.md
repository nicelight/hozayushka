---
description: Fresh independent review of the FT-001 W12 post-repair task-plan surface.
status: final
task_id: TASK-MB-REVIEW-TASKS-PLAN
stage_id: S-TASKS-FT-001
feature: FT-001
---
# Review report: FT-001 task-plan readiness

REVIEWED_PLANNING_REVISION: 1

## Verdict

VERDICT: REJECT

The W12 implementation shape is otherwise runnable and architecturally bounded,
but its claim ownership is not ready for execution under the requested
FT-001-only acceptance boundary.

## Structural integrity

- `.memory-bank/spec-backbone.md` is `complete` at positive `Planning Revision: 1`;
  Foundation Gate `TASK-002-T3-FT-000-W1` is `done`.
- `.memory-bank/tasks/index.json` contains 15 unique resolving entries,
  including `TASK-015-T3-FT-001-W12`. JSON parsing and the schema-required
  fields of W12 are intact; its identity is consistently `T3|FT-001|W12`.
- W12 is `planned`, has the concrete dependency
  `TASK-011-T3-FT-009-W10` (`done`), and remains in wave `W12`. The failed W11
  record is not used as an executable prerequisite.
- The exact feature locator
  `.memory-bank/features/FT-001-main-clock-display.md#FT-001-AC-005` is present
  in W12 `source_artifacts`; stable FT-001 AC-001 through AC-005 headings and
  the historical TASK-003/TASK-014 records resolve.

Evidence: `.memory-bank/spec-backbone.md:118-155`,
`.memory-bank/tasks/index.json:1-64`,
`.memory-bank/tasks/TASK-015-T3-FT-001-W12.task.json:1-18,51-163`,
`.memory-bank/schemas/task.schema.json`,
`.memory-bank/features/FT-001-main-clock-display.md:36-70`.

## Coverage and slicing

The slice is cohesive and minimal: the plan and card keep the repair in Main
Display, use one internal active-countdown dispatcher, capture at
`ACTION_DOWN`, deliver through `ACTION_UP`/`ACTION_CANCEL` without live-state
reclassification, and retain the existing Timer & Alert and Settings &
Location contracts. The card explicitly covers public city hold, city double
with no delayed Settings, non-city single/hint, non-city double/cancel, preset
interactions, overdue dismissal as a guard, and safe cleanup. Host stream proof
is correctly marked supporting; the generic Android runtime is decisive.

Evidence: `.memory-bank/tasks/plans/IMPL-FT-001.md:28-36,103-118`,
`.memory-bank/tasks/TASK-015-T3-FT-001-W12.task.json:41-49,140-162`,
`.memory-bank/contracts/capability-interfaces.md:44-64,101-119`,
`.memory-bank/states/lifecycle-map.md:15-37`.

However, the current plan/card still assign the FT-006 acceptance locator to
W12. The plan calls the W12 outcome a “cross-feature public cancellation
integration delta under `FT-006-AC-003 / REQ-013`”
(`IMPL-FT-001.md:190-197`), while the task uses the exact
`FT-006-AC-003` locator in `verification_targets` and
`evidence_required` (`TASK-015...task.json:71-73,155-157`). The card also puts
`REQ-013` in `reqs` (`TASK-015...task.json:7-10`). Under the tier policy, an
exact AC/canonical proof-obligation locator used in proof mapping assigns that
claim to the task; this makes W12 a cross-feature acceptance owner rather than
only a Main Display repair with an exact `FT-001-AC-005` ownership locator.

This directly violates the requested boundary: `FT-006-AC-003 / REQ-013` may
remain the canonical regression contract and comparison evidence, but must not
be the W12 acceptance locator or task-owned cross-feature claim. This is a
blocking planning/claim-closure defect, not an architecture decision.

Evidence: `.memory-bank/workflows/tier-policy.md:81-109`,
`.memory-bank/tasks/plans/IMPL-FT-001.md:190-197`,
`.memory-bank/tasks/TASK-015-T3-FT-001-W12.task.json:7-10,71-73,119-120,155-157`,
`.memory-bank/features/FT-006-countdown-lifecycle.md:122-142`.

## Design readiness and bounded architecture review

Feature design is `complete`; no applicable pending/blocked design row or
competing canonical path was found. Direct W12 links cover System Architecture,
Boundary Map, Capability Interfaces, Platform Runtime, Lifecycle Map, Runtime
Verification and Tier Policy. Timer & Alert remains the sole timer-state owner;
Settings & Location remains the destination/state owner.

Bounded `/architecture-review FT-001` was performed locally because no fresh
delegation surface was available:

- `verdict: APPROVE`
- `findings`: C4 L1 product, EP-001 and FT-001 responsibilities are coherent;
  W12 fits the registered Main Display change unit and existing Main Display →
  Timer & Alert / Settings & Location edges. The dispatcher is an internal
  mechanism, not a new public/event boundary. No hidden or cyclic dependency,
  private-state bypass, composition-root orchestration, new module or graph
  edge is required.
- `evidence_checked`: `.memory-bank/product.md`,
  `.memory-bank/epics/EP-001-glanceable-display.md`, FT-001, FT-006, System
  Architecture AD-001/AD-003/AD-004, Boundary Map, Capability Interfaces,
  Platform Runtime, Lifecycle Map, Runtime Verification, the FT-001 plan,
  TASK-003, TASK-014 and TASK-015.
- `risks_or_questions`: none changing architecture readiness. The ownership
  defect above remains a task-plan repair, not an architecture blocker.

## Execution readiness

- T3 is correct for public Android touch/event behavior and its runtime risk.
  The handoff has purpose/outcome, direct SDD routes, constraints/invariants,
  gates, RED/GREEN evidence shape, generic-AVD identity/checkpoints, public
  route matrix, deferred Samsung scope, and cleanup conditions.
- `touched_files` remains advisory and no hard `write_boundary` is invented.
  Forbidden scope protects TASK-003/TASK-014 history, scheduler state, private
  cross-slice state, Timer & Alert semantics, secrets and deferred physical
  target claims.
- `TASK-003-T3-FT-001-W2` is still `done`; `TASK-014-T3-FT-001-W11` is still
  `failed`. Neither historical record is normalized, reopened or used as a
  new owner by this review.
- No project build, test, execute, verify, red-verify, mb-sync or doctor run
  was performed. The applicable doctor gate remains downstream and is not
  substituted by this semantic review.

Evidence: `.memory-bank/tasks/TASK-003-T3-FT-001-W2.task.json`,
`.memory-bank/tasks/TASK-014-T3-FT-001-W11.task.json`,
`.memory-bank/tasks/TASK-015-T3-FT-001-W12.task.json:18-49,86-163`,
`.memory-bank/testing/runtime-verification.md:69-122`,
`.protocols/AUTONOMOUS-RUN/status.md:14-25,95-105`.

## Blocking findings and precise owner

1. **BLOCKER — cross-feature acceptance locator remains task-owned.**
   `TASK-015` and `IMPL-FT-001` use `FT-006-AC-003 / REQ-013` as the W12
   acceptance/proof claim, while the required owner is the exact
   `FT-001-AC-005` locator. Reconcile the W12 plan/card so the only exact W12
   feature-acceptance owner is `FT-001-AC-005`; retain `FT-006-AC-003 / REQ-013`
   only as a non-owning canonical regression contract/evidence reference.

   Precise repair owner: `/feature-to-tasks FT-001`. After the repair, rerun
   `/review-tasks-plan FT-001`.

No other blocking finding was identified. This review did not change task
status, lifecycle, index, plan, scheduler checkpoint, historical evidence or
runtime state; only the two required review outputs are written.
