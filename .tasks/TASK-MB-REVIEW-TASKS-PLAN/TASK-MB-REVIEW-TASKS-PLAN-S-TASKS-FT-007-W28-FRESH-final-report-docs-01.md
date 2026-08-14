---
description: Fresh independent semantic and bounded architecture review of FT-007 W28.
status: final
task_id: TASK-MB-REVIEW-TASKS-PLAN
stage_id: S-TASKS-FT-007-W28-FRESH
feature: FT-007
reviewed_task: TASK-031-T3-FT-007-W28
review_mode: fresh_independent_local
reviewed_planning_revision: 2
---
# FT-007 W28 — fresh task-plan review

REVIEWED_PLANNING_REVISION: 2

## Итоговый verdict

FINAL_VERDICT: REJECT

Сама карточка `TASK-031-T3-FT-007-W28` задаёт достаточный и bounded
Main Display outcome. Полная canonical planning surface пока не готова к
execution: `IMPL-FT-007.md` содержит два material contradictions с актуальной
W28 task card и feature plan. Они требуют bounded repair через
`/feature-to-tasks FT-007`, после чего review должен быть запущен повторно.

## Review boundary

Проверены обязательные Constitution/MBB/Reviewer/workflow/schema источники,
Global Backbone и `Planning Revision`, FT-007/EP-003, `IMPL-FT-007`, FT-007
feature plan и decision log, W28 task/index, direct canonical specs,
dependency chain, а также W8/W23/W27 task/protocol/evidence context.

Review выполнен в свежем локальном Reviewer context. Delegation для отдельного
fresh Reviewer недоступна, поэтому bounded `/architecture-review` выполнен
локально по тому же контракту. `/exe`, `/verify`, `/red-verify`, `/mb-doctor`,
`/mb-sync`, Gradle/build/test, emulator/device/adb, network, credentials и
audio runtime не запускались.

## Четыре coverage groups

| Coverage group | Result | Evidence |
|---|---|---|
| Structural integrity | PASS | W28 JSON has the required schema-shaped fields and no extra fields; index entry resolves uniquely; identity is exactly `TASK-031-T3-FT-007-W28`, `T3`, `FT-007`, `W28`, `planned`. Read-only dependency traversal is acyclic and reaches done W27 → W26 → W25 → W24 → W23 → W8 → closed Foundation chain. |
| Coverage and slicing | PASS in task card; BLOCKED by plan contradiction | FT-007-AC-006 is a stable feature heading with `REQ-015`/`REQ-023` and exact W28 locator. W8 retains AC-001…AC-005 baseline ownership; W23 retains audio delta ownership; W28 owns only the visual delta. W28 evidence covers dedicated content exclusion, elapsed hierarchy, preset color, transparent circle, blinking `+`, stable numeric value, clipping/overlap, read-only regressions and target/audio deferral. |
| Design and architecture readiness | PASS | FT-007 has `spec_design_status: complete`; registered subject-based routes resolve. Main Display is the accepted composition owner; Timer & Alert owns lifecycle/elapsed/dismissal/audio; Android Runtime owns policy. No new module, graph edge, public contract, event boundary, storage owner or resource route is selected. The local bounded architecture review is `APPROVE`. |
| Execution readiness | REJECT | W28 is correctly `planned` behind done W27, is correctly T3, and has the literal two-file boundary. However, the canonical implementation plan still exposes an old W8/W23 dependency and write-surface interpretation, so a fresh executor can receive conflicting owner/dependency instructions. |

## Bounded architecture review

verdict: APPROVE

findings: none

evidence_checked: C4 product/EP-003/FT-007 context; System Architecture
capability-slice runtime and AD-002/AD-003/AD-004; Boundary Map modules,
dependency graph and accepted ownership; Capability Interfaces Main Display →
Timer & Alert and orchestration ownership; Platform Runtime display and
timer/audio boundaries; Lifecycle Map; Runtime Verification; Constitution,
FT-007, W8, W23, W27 and W28 planning surfaces.

risks_or_questions: Samsung GT-I9300I/Android 11 custom-ROM readability,
fullscreen, lifecycle and physical audio remain the accepted `DEFERRED` route.
No architecture-level owner, boundary, dependency direction or contract gap
changes the local architecture verdict.

## Blocking findings

### BLOCKER-1 — `IMPL-FT-007` has a contradictory direct-predecessor route

Evidence:

- `.memory-bank/tasks/plans/IMPL-FT-007.md:28-30` calls
  `TASK-009-T3-FT-007-W8` the “Direct predecessor” and then says W28
  “additionally depends” on W27.
- The authoritative W28 card has only
  `depends_on: [TASK-030-T3-FT-006-W27]`
  (`.memory-bank/tasks/TASK-031-T3-FT-007-W28.task.json:13-15`).
- The FT-007 queue says W28 is order 3 after W27 and depends on W27
  (`.protocols/FT-007/plan.md:31-45`); the decision log says W28 is directly
  dependent on W27 (`.protocols/FT-007/decision-log.md:20-24`). W8 and W23
  are already transitive/historical context through the approved chain.

Effect: the complete plan does not present one unambiguous dependency
interpretation. An executor/scheduler could invent a redundant W8 edge or treat
the old baseline as the W28 direct predecessor. This fails the review
requirement for coherent plan/dependency slicing even though the task card and
queue are otherwise correct.

Repair owner: `/feature-to-tasks FT-007`; reconcile the implementation plan to
state W27 as W28's sole direct dependency and W8/W23 as preserved
transitive/historical context. Do not alter task status or dependency state in
this review.

### BLOCKER-2 — `IMPL-FT-007` still describes the old W23 write surface as the
combined feature surface

Evidence:

- `.memory-bank/tasks/plans/IMPL-FT-007.md:36-40` says the smallest expected
  implementation/test surface is `TimerCapability.kt`,
  `PlatformRuntimeAdapter.kt` and `OverdueAlertTest.kt`, and says Main Display
  remains unchanged.
- That statement is not scoped to W23 and conflicts with the same plan's W28
  route. The current W28 card assigns Main Display as the owner and makes the
  exact hard boundary `DisplayCapability.kt` plus `DisplayProjectionTest.kt`
  (`.memory-bank/tasks/TASK-031-T3-FT-007-W28.task.json:43-51, 88-101,
  173-179`).
- The feature plan explicitly resolves the W28 surface to those two files and
  keeps TimerCapability/TimerAlertPolicy/PlatformRuntimeAdapter read-only
  (`.protocols/FT-007/plan.md:47-59, 149-195`).

Effect: the canonical implementation plan can direct execution toward the
forbidden Timer/Audio files or imply that the requested Main Display visual
delta is out of scope. Even if the old paragraph was intended only as W23
context, its unlabeled placement makes the planning handoff semantically
ambiguous and violates the hard-boundary review.

Repair owner: `/feature-to-tasks FT-007`; explicitly separate W23's completed
Timer/Audio surface from W28's Main Display two-file surface and preserve the
W28 hard boundary. No code or task card repair is authorized in this review.

## Accepted scope and proof checks

- `FT-007-AC-006` at `.memory-bank/features/FT-007-overdue-alert.md:70-79`
  is the single new owned outcome: dedicated no-weather/no-city/no-date/
  no-standard-card surface; stable full elapsed digits larger than idle and,
  where existing geometry permits, larger than active countdown; activating
  preset color in a transparent neon circle; blinking `+`; any-tap remains.
- W28's claim-linked RED/GREEN contract and named visual-QA rubric are
  task-owned (`.memory-bank/tasks/TASK-031-T3-FT-007-W28.task.json:79-86,
  192-199`). The contract records
  idle/W27 active/overdue text sizes, full elapsed value, hidden content,
  circle shape/fill/color, blinking-plus/numeric stability and clipping/overlap,
  without inventing a fixed dp/ratio/gradient stop. “Large `+`” is bounded by
  the accepted focal-hierarchy/readability check and existing W8 overdue
  presentation; no unaccepted numeric plus ratio is introduced.
- The exact two-file `runtime_context.write_boundary` is literal and valid:
  `DisplayCapability.kt` and `DisplayProjectionTest.kt`. TimerCapability,
  TimerAlertPolicy, PlatformRuntimeAdapter, resources, composition-root paths,
  W23/W27 history, scheduler state and target/audio runtime are forbidden or
  explicit stop routes
  (`.memory-bank/tasks/TASK-031-T3-FT-007-W28.task.json:98-126`).
- T3 is proportionate to production Android display behavior and the retained
  lifecycle/audio/target risk. `planned` is legal because the direct W27
  dependency is done but no promotion is performed. Target/device/audio remain
  `DEFERRED`; host geometry or fake audio cannot become runtime or physical
  audibility `PASS`.
- Historical ownership is preserved: W8 is done for the original overdue
  baseline and W23 is done for completion-time audio request/start/repeat/stop
  and denial/error preservation. W27 is done for the active-countdown
  presentation reference (`228.0` versus idle `188.75`, preset-colored circle).
  W28 consumes those outcomes as context and does not inherit their proof.

## Exact Planning Revision and strict evidence

- Current authoritative value: `.memory-bank/spec-backbone.md:132-145` —
  `Status: complete`, `Planning Revision: 2`.
- Latest durable strict evidence inspected: W27's verifier receipt
  `.protocols/TASK-030-T3-FT-006-W27/verification.md:63-67` records
  `node scripts/mb-doctor.mjs --strict --json` as `status: pass`, 0
  errors/warnings, at the W27 boundary.
- That is not a current W28 strict result. The scheduler status explicitly says
  final lint/strict gates are pending before FT-007 planning
  (`.protocols/AUTONOMOUS-RUN/status.md:14-20`), and this review did not run
  `/mb-doctor` as required by the review contract and operator scope. Therefore
  no current W28 strict `PASS` is claimed; after the plan repair and fresh
  `APPROVE`, the scheduler-owned `/mb-doctor --strict` gate remains required.

## Mutation boundary and changed files

Reviewed durable planning/spec/task/code/status/checkpoint/terminal state was
not changed. No execution, verification, sync, code edit, task promotion,
status change, lifecycle/REQ mutation or runtime launch was performed.

Review outputs written/updated:

- `.tasks/TASK-MB-REVIEW-TASKS-PLAN/REQUEST.md`
- `.tasks/TASK-MB-REVIEW-TASKS-PLAN/TASK-MB-REVIEW-TASKS-PLAN-S-TASKS-FT-007-W28-FRESH-final-report-docs-01.md`
- `PAPERCUTS/GPT-5-Codex __ 08-13-2026 00.15.md` — workflow note for an
  earlier truncated bounded tool output.

The repository already contained unrelated dirty changes before this review;
they were preserved and not treated as review output.

## Handoff

`REJECT` routes to `/feature-to-tasks FT-007` for the two plan-only
reconciliations above, followed by a fresh `/review-tasks-plan FT-007`. This
review does not authorize `/exe`, `/verify`, `/red-verify`, `/mb-doctor`,
promotion, status normalization, `/mb-sync` or any terminal-state transition.
