---
description: Bounded independent reviewer request for the PRD-to-feature decomposition gate.
status: active
task_id: TASK-MB-REVIEW-FEAT-PLAN
stage_id: S-FEAT
---
# Review Request: PRD decomposition readiness

## Assignment

Act as `ROLE: Reviewer` in a fresh context and independently decide whether the
current `PRD -> REQ -> EP -> FT` decomposition is ready for `/spec-design`.
Report findings only: do not rewrite product decisions, resolve ambiguous
operator choices, or modify reviewed artifacts.

## Required inputs

- Governing context: `AGENTS.md`, `.memory-bank/constitution.md`,
  `.memory-bank/mbb/index.md`, `.memory-bank/roles/reviewer.md`.
- Product discovery: `.memory-bank/analysis/index.md`,
  `.memory-bank/analysis/product-brief.md`, and, when needed for accepted visual
  decisions, `.memory-bank/analysis/brainstorming/BR-001.md`.
- Clarified product contract: `.memory-bank/prd.md`, `.memory-bank/product.md`,
  `.memory-bank/requirements.md`.
- Decomposition: `.memory-bank/epics/index.md`, all current
  `.memory-bank/epics/EP-*.md`, `.memory-bank/features/index.md`, and all current
  `.memory-bank/features/FT-*.md`.
- Framing and supporting evidence: `.memory-bank/spec-index.md`,
  `.memory-bank/spec-backbone.md`, `.memory-bank/user-scenarios.md`,
  `.memory-bank/glossary.md`, `.memory-bank/invariants.md`,
  `.memory-bank/contracts/boundary-map.md`, and
  `.memory-bank/states/lifecycle-map.md`.

## Review boundary

Evaluate Constitution/PRD consistency, stable and supported `REQ-*` coverage,
`PRD -> REQ -> EP -> FT` traceability, feature value/boundaries/acceptance and
failure behavior, truthful clarification state, workflow ordering, `FT-000`
reservation, and visible Foundation pressure for `/spec-design`.

Where evidence indicates multiple acceptance outcomes, lifecycle/release units,
or actor/authority/boundary paths, perform one bounded falsification probe:
determine whether a concrete second product slice has independent observable
value and acceptance and could be verified or released independently. Boundary
pressure alone is not a defect.

Do not require or review JSON task records, task decomposition, implementation
mechanisms, code, or other task implementation detail. Existing task material,
if encountered, may be considered only for an obvious stale contradiction with
the current product plan.

## Verdict and report contract

- Use exactly `VERDICT: APPROVE` or `VERDICT: REJECT`; this vocabulary overrides
  the generic Reviewer role vocabulary for this assignment.
- `REJECT` is blocking. `APPROVE` may contain non-blocking notes.
- Distinguish missing evidence from an evidenced defect.
- If an unresolved product/decomposition choice controls correctness, include
  the exact operator question, affected artifacts, decision owner, and repair
  route without choosing for the operator.
- Include: verdict, evidence checked, blocking findings, non-blocking notes,
  unresolved operator questions, and owning repair route.
- Return the completed report to the caller for persistence at
  `.tasks/TASK-MB-REVIEW-FEAT-PLAN/TASK-MB-REVIEW-FEAT-PLAN-S-FEAT-final-report-docs-01.md`.

TASK_ID: `TASK-MB-REVIEW-FEAT-PLAN`  
STAGE_ID: `S-FEAT`
