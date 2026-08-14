---
description: Bounded independent reviewer request for provider-migration PRD-to-feature reconciliation.
status: active
task_id: TASK-MB-REVIEW-FEAT-PLAN
stage_id: S-FEAT
---
# Review Request: provider-migration decomposition readiness

## Assignment

Act as `ROLE: Reviewer` in a fresh independent context and decide whether the
provider-migration reconciliation across `PRD -> REQ -> EP -> FT` is ready for
`/spec-design`. Report findings only; do not rewrite product decisions or any
reviewed product, specification, lifecycle, task, scheduler, or code artifact.

## Required inputs

- Governing and role context: `AGENTS.md`, `.memory-bank/constitution.md`,
  `.memory-bank/mbb/index.md`, `.memory-bank/index.md`, and
  `.memory-bank/roles/reviewer.md`.
- Product discovery and clarified contract: `.memory-bank/analysis/index.md`,
  `.memory-bank/analysis/product-brief.md`, `.memory-bank/prd.md`,
  `.memory-bank/product.md`, and `.memory-bank/requirements.md` including RTM.
- Relevant decomposition: `.memory-bank/epics/index.md`,
  `.memory-bank/epics/EP-002-weather-context.md`,
  `.memory-bank/epics/EP-004-settings-location.md`,
  `.memory-bank/features/index.md`, and affected feature documents FT-002,
  FT-003, FT-004, and FT-008.
- Framing and minimum accepted context: `.memory-bank/spec-backbone.md`,
  `.memory-bank/spec-index.md`, `.memory-bank/user-scenarios.md`,
  `.memory-bank/glossary.md`, `.memory-bank/invariants.md`,
  `.memory-bank/foundation.md`, and only the linked accepted contracts needed
  to judge truthful design-pending state.

## Review focus

- Open-Meteo default/no-key and explicit OpenWeather/local-key traceability.
- No automatic failover or mixed provider data; provider-identified
  cache/history.
- Open-Meteo 10 daily positions versus OpenWeather 8 records plus 2 honest empty
  positions.
- Strict completeness and unavailable behavior for all eight fixed hourly
  slots.
- Required Open-Meteo attribution.
- Stability of existing IDs and grounding of REQ-027/028/029,
  FT-002-AC-008, FT-004-AC-006, and FT-008-AC-007/008.
- Truthful lifecycle/design-pending state, no unsupported extra feature slice,
  and complete PRD -> REQ -> EP -> FT acceptance closure.

Perform one bounded feature-boundary falsification probe. Boundary pressure by
itself is not a defect; reject only for a proven hidden independent product
outcome or unresolved material boundary decision.

## Review boundary

Do not require or review JSON task records, task decomposition, implementation
mechanisms, code, emulator/device evidence, Gradle output, queue state, or
scheduler behavior. If historical task material is encountered, consider it
only for an obvious stale contradiction with the current product plan.

## Verdict and output contract

- Use exactly `VERDICT: APPROVE` or `VERDICT: REJECT`.
- `REJECT` is blocking; `APPROVE` may include non-blocking notes.
- Distinguish missing evidence from an evidenced defect.
- Include evidence, blocking findings, non-blocking notes, unresolved operator
  questions, and the owning repair route.
- Persist the final report at
  `.tasks/TASK-MB-REVIEW-FEAT-PLAN/TASK-MB-REVIEW-FEAT-PLAN-S-FEAT-final-report-docs-01.md`.

TASK_ID: `TASK-MB-REVIEW-FEAT-PLAN`  
STAGE_ID: `S-FEAT`
