---
description: Итог независимой проверки provider-migration PRD-to-feature reconciliation.
status: final
task_id: TASK-MB-REVIEW-FEAT-PLAN
stage_id: S-FEAT
---
# Review report: provider-migration decomposition readiness

TASK_ID: `TASK-MB-REVIEW-FEAT-PLAN`  
STAGE_ID: `S-FEAT`

## Verdict

VERDICT: APPROVE

Provider-migration decomposition `PRD -> REQ -> EP -> FT` трассируема,
содержательно замкнута и готова к `/spec-design`.

## Evidence checked

- Governing/reviewer context: [`AGENTS.md`](../../AGENTS.md),
  [Constitution](../../.memory-bank/constitution.md),
  [MBB](../../.memory-bank/mbb/index.md),
  [Memory Bank index](../../.memory-bank/index.md) и
  [Reviewer role](../../.memory-bank/roles/reviewer.md).
- Product inputs: [analysis index](../../.memory-bank/analysis/index.md),
  [Product Brief](../../.memory-bank/analysis/product-brief.md),
  [clarified PRD](../../.memory-bank/prd.md),
  [product](../../.memory-bank/product.md) и
  [requirements/RTM](../../.memory-bank/requirements.md).
- Decomposition: [epics index](../../.memory-bank/epics/index.md),
  [EP-002](../../.memory-bank/epics/EP-002-weather-context.md),
  [EP-004](../../.memory-bank/epics/EP-004-settings-location.md),
  [features index](../../.memory-bank/features/index.md),
  [FT-002](../../.memory-bank/features/FT-002-weather-cards-context.md),
  [FT-003](../../.memory-bank/features/FT-003-hourly-forecast.md),
  [FT-004](../../.memory-bank/features/FT-004-ten-day-forecast.md) и
  [FT-008](../../.memory-bank/features/FT-008-weather-location-settings.md).
- Readiness/context: [spec backbone](../../.memory-bank/spec-backbone.md),
  [spec index](../../.memory-bank/spec-index.md),
  [user scenarios](../../.memory-bank/user-scenarios.md),
  [glossary](../../.memory-bank/glossary.md),
  [invariants](../../.memory-bank/invariants.md),
  [foundation](../../.memory-bank/foundation.md),
  [boundary map](../../.memory-bank/contracts/boundary-map.md),
  [weather-provider contract](../../.memory-bank/contracts/weather-provider.md),
  [local-secret contract](../../.memory-bank/contracts/local-secret-handling.md),
  [local-data domain](../../.memory-bank/domains/local-data.md) и
  [lifecycle map](../../.memory-bank/states/lifecycle-map.md).
- Read-only ID comparison against `HEAD`: existing `REQ-000` through `REQ-026`
  and all pre-existing AC IDs of FT-002/003/004/008 remain present. The ID sets
  append REQ-027/028/029 and FT-002-AC-008, FT-004-AC-006,
  FT-008-AC-007/008 for the migration outcomes.

Task JSON, implementation detail, code, emulator/device and Gradle evidence were
not reviewed.

## Acceptance closure

| Product outcome | PRD -> REQ | EP -> FT acceptance |
|---|---|---|
| Open-Meteo default/no-key; explicit OpenWeather/local-key | `PRD-FR-032/033`, `PRD-AC-006/006A` -> REQ-024, REQ-027 | EP-004 -> FT-008-AC-001/006/007 |
| No auto failover/mixing; provider-identified cache/history | `PRD-FR-013/032/037`, PRD domain/failure rules, `PRD-AC-002/008` -> REQ-007, REQ-008, REQ-029 | EP-002 -> FT-002-AC-004/005/008; EP-004 -> FT-008-AC-007 |
| Open-Meteo 10 versus OpenWeather 8 + 2 empty | `PRD-FR-019/020/022`, `PRD-AC-007` -> REQ-010 | EP-002 -> FT-004-AC-001/005/006 |
| Strict eight-slot hourly completeness/unavailable behavior | `PRD-FR-019A/B/022`, `PRD-AC-007A` -> REQ-009 | EP-002 -> FT-003-AC-001/005 |
| Open-Meteo attribution | `PRD-FR-032`, PRD integration terms, `PRD-AC-006` -> REQ-028 | EP-004 -> FT-008-AC-008 |

The new REQ and AC IDs have direct governing REQ/PRD links and deterministic
verification targets. Material failure outcomes retain the selected provider,
reject partial forecast entry, and forbid synthesis or cross-provider data.

## Lifecycle and boundary falsification

- The PRD remains Constitution-checked with `clarification_status: complete`
  and no unresolved product blocker.
- EP-002/EP-004 remain `draft` with lifecycle `planned`; FT-002/003/004/008 are
  `lifecycle: planned` and `spec_design_status: pending`.
- FT-000 remains the reserved Foundation pseudo-feature and owns no provider
  behavior. The Foundation gate stays closed while provider-specific design
  pressure is explicitly routed through the spec backbone and affected feature
  design gates.
- Historical Yandex evidence is explicitly labelled brownfield/superseded and
  does not claim current acceptance. Registered Yandex-only provider, cache and
  ten-filled-day contracts remain visibly stale and are routed to fresh
  `/spec-design`; task, queue and scheduler state are not promoted.
- The provider migration adds no FT. Selection/key/attribution remains the
  independently observable Settings outcome in FT-008; provider-isolated
  weather state remains FT-002; strict hourly and capability-aware long-term
  views remain FT-003 and FT-004. No hidden independently releasable product
  outcome or unsupported extra feature slice is evidenced.

## Blocking findings

Нет.

## Non-blocking notes

- [Analysis index](../../.memory-bank/analysis/index.md) still says the
  decomposition created 26 stable REQ IDs, while the reconciled registry now
  contains 29 and preserves the original 26. The authoritative requirements,
  RTM, epics and feature cards are internally consistent, so this is routing
  metadata drift rather than an acceptance gap.
- The closing PRD handoff still names `/prd-to-features`; decomposition is now
  complete and the current reviewed handoff is `/spec-design`. This is likewise
  navigation drift and does not alter product acceptance.

## Unresolved operator questions

Нет.

## Owning repair route / handoff

- Required handoff: `APPROVE -> /spec-design`.
- Non-blocking routing-metadata repair owner: `/prd-to-features` (durable
  decomposition navigation reconciliation).
