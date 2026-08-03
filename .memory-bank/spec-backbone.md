---
description: Pre-PRD spec framing and global SDD backbone state.
status: active
---
# SDD Spec Backbone

## Pre-PRD Spec Status
- Status: ready_for_prd
- Last updated: 2026-08-03
- Notes: Clarified PRD markers are valid, glossary is reconciled, scenario/domain/lifecycle inputs are sufficient for L1-L3 decomposition, and no unresolved product/domain blocker remains.

## Decomposition Inputs
- User scenarios: [.memory-bank/user-scenarios.md](user-scenarios.md) (reviewed); source of detail is [.memory-bank/prd.md](prd.md), `Users / Actors`, `UX / Interaction Flow`, and `Acceptance Criteria`.
- Domain model: [.memory-bank/prd.md](prd.md), `Data / Domain Model`; current entities are User Settings, Offline Location Entry, Weather Snapshot, Weather History, Forecast Day and Active Timer. A separate core-domain spec is not required before decomposition.
- Constraints: [.memory-bank/prd.md](prd.md), `Non-functional Requirements` and `Integrations / Dependencies`; target Android 11 custom ROM, landscape 1280×720, local API key, offline location search, explicit palette and lightweight pseudo-glass.
- Non-goals: [.memory-bank/prd.md](prd.md), `Non-goals`, `Edge Cases / Failure Handling` and `Acceptance Criteria`; backend/cloud/accounts, Google Services, reboot recovery, pre-install history, V2 Telegram/TTS and unaccepted UI scope are excluded.
- Risks: [.memory-bank/prd.md](prd.md), `Integrations / Dependencies`, `Edge Cases / Failure Handling` and `Verification Strategy`; target-ROM timer lifecycle, offline dataset footprint, provider field availability and device readability remain downstream design/verification risks.
- Boundary hints: [.memory-bank/contracts/boundary-map.md](contracts/boundary-map.md); preliminary responsibility notes only, with no API/schema or architecture decision.
- Lifecycle hints: [.memory-bank/states/lifecycle-map.md](states/lifecycle-map.md); timer, weather freshness and forecast-session transitions that affect feature cuts.

## Open Design Questions
- Which architecture/module and storage boundaries best satisfy the accepted local-only product shape on the target device? Defer to `/spec-design`.
- Which exact provider-field mapping and project-native persistence/testing mechanisms are needed for the accepted weather and timer behavior? Defer to subject SDD specs after `/spec-design`.
- How does the target Android 11 custom ROM behave for temporary process stop, timer recovery and permitted alert audio? Treat as a risk-based design/device-verification question, not a product-scope blocker.
- The exact application name is non-blocking and may be chosen before packaging; it does not affect decomposition.

## Backbone Area Matrix
| Area | Status | Authoritative source | Notes |
|---|---|---|---|
| architecture_style | blocked | - | Decide in /spec-design after /prd-to-features. |
| source_of_truth | blocked | - | Decide in /spec-design after /prd-to-features. |
| module_boundaries | blocked | .memory-bank/contracts/boundary-map.md | Preliminary responsibility/scope notes are captured; decide in /spec-design after /prd-to-features. |
| user_scenarios | blocked | .memory-bank/user-scenarios.md | Reviewed scenario input is captured; global architecture status remains pending. |
| constraints | blocked | .memory-bank/prd.md | Product constraints are captured in PRD; refine technical implications in /spec-design. |
| non_goals | blocked | .memory-bank/prd.md | Product non-goals are captured in PRD; preserve them during decomposition. |
| domain_model | blocked | .memory-bank/prd.md | PRD Data / Domain Model is sufficient for decomposition; create a subject spec only if shared design pressure requires it. |
| data_flow | blocked | - | Decide in /spec-design after /prd-to-features. |
| storage | blocked | - | Decide in /spec-design after /prd-to-features. |
| api_contracts | blocked | - | Decide authoritative/needed/not_applicable/blocked in /spec-design. |
| event_message_contracts | blocked | - | Decide authoritative/needed/not_applicable/blocked in /spec-design. |
| agent_io_contracts | blocked | - | Decide authoritative/needed/not_applicable/blocked in /spec-design. |
| security_safety | blocked | - | Decide in /spec-design after /prd-to-features. |
| deployment | blocked | - | Decide in /spec-design after /prd-to-features. |
| risks | blocked | .memory-bank/prd.md | Product risks are captured; technical risk treatment belongs in /spec-design and verification. |
| open_questions | blocked | .memory-bank/spec-backbone.md | Deferred design questions remain here until /spec-design resolves or scopes them. |

## Handoff To /prd-to-features
- Ready: yes
- Required reads: [.memory-bank/prd.md](prd.md), [.memory-bank/glossary.md](glossary.md), [.memory-bank/spec-index.md](spec-index.md), [.memory-bank/user-scenarios.md](user-scenarios.md), [.memory-bank/invariants.md](invariants.md), [.memory-bank/contracts/boundary-map.md](contracts/boundary-map.md), [.memory-bank/states/lifecycle-map.md](states/lifecycle-map.md), and this file.
- Stop conditions: a later decomposition step discovers a new actor/scenario/lifecycle/domain branch, contradicts the accepted 10-day forecast horizon or timer/API-key/offline rules, or requires an architecture/public-contract decision before `/spec-design`.

## Handoff To /spec-design
- Global Backbone Status: intentionally pending until /spec-design
- Downstream readiness: /feature-to-tasks, /autopilot, and autonomous scheduler mode must wait for /spec-design.
- Backbone areas to revisit: architecture style, source of truth, module boundaries, data flow, storage, applicable provider/interface contracts, security/safety handling without inventing posture, deployment/device runtime, risks and open questions. Reuse captured scenarios, constraints, non-goals and lifecycle hints as inputs.
- Candidate specs: [.memory-bank/spec-index.md](spec-index.md) Planned Specs; likely subjects include architecture, weather/provider boundary, local data/storage, timer lifecycle and verification, but `/spec-design` decides which are applicable.

## Global Backbone Status
- Status: blocked
- Planning Revision: 0
- Mode: pending
- Architecture artifact strategy: pending
- Not applicable areas:
  - TBD
- Notes: /spec-design has not completed the global architecture scaffold yet.
