---
description: Pure SDD spec registry and planned-spec index.
status: active
last_updated: 2026-08-14
source_of_truth:
  - .memory-bank/spec-index.md
---
# SDD Spec Index

## Purpose

- Keep a concise registry of existing and planned SDD specs.
- Read this index before creating a new subject spec or doing serious
  design-pressure work.
- Keep global readiness, backbone status, matrix and workflow handoffs in
  [.memory-bank/spec-backbone.md](spec-backbone.md).
- Keep feature `spec_design_status` in feature documents, not in this index.

## Spec Registry

| Type | Path | Status | Scope | Change route |
|---|---|---|---|---|
| governance | [.memory-bank/constitution.md](constitution.md) | active | Top governing policy. | /constitution |
| invariants | [.memory-bank/invariants.md](invariants.md) | active | Global MUST/NEVER rules grounded in the PRD and Constitution. | /spec-init or /spec-design |
| glossary | [.memory-bank/glossary.md](glossary.md) | active | Product vocabulary and domain terms. | /brief, /spec-init or /spec-design |
| scenario | [.memory-bank/user-scenarios.md](user-scenarios.md) | active | Reviewed actors, core scenarios and decomposition implications. | /spec-init or /spec-design |
| architecture | [.memory-bank/architecture/system-architecture.md](architecture/system-architecture.md) | active | Accepted one-deployable architecture, spine, runtime composition and deployment boundary. | /spec-design |
| contract | [.memory-bank/contracts/boundary-map.md](contracts/boundary-map.md) | active | Canonical module inventory and directed dependency graph. | /spec-design or /feature-to-tasks |
| contract | [.memory-bank/contracts/capability-interfaces.md](contracts/capability-interfaces.md) | active | Public in-process capability contracts and ownership rules. | /spec-design or /feature-to-tasks |
| contract | [.memory-bank/contracts/weather-provider.md](contracts/weather-provider.md) | active | Selectable Open-Meteo/OpenWeather endpoints, provider-neutral normalization boundary, capability matrix and no-fallback rules. | /spec-design or /feature-to-tasks |
| contract | [.memory-bank/contracts/weather-card-presentation.md](contracts/weather-card-presentation.md) | active | FT-002 display-ready cards, temperature palette, pseudo-glass and pressure-trend presentation. | /spec-design or /feature-to-tasks |
| contract | [.memory-bank/contracts/main-display-presentation.md](contracts/main-display-presentation.md) | active | Main Display composition, normalized geometry, clock/card/icon hierarchy, timer rail and visual-QA proof. | /spec-design or /feature-to-tasks |
| contract | [.memory-bank/contracts/platform-runtime.md](contracts/platform-runtime.md) | active | Android lifecycle, time, display and audio boundary. | /spec-design or /feature-to-tasks |
| contract | [.memory-bank/contracts/local-secret-handling.md](contracts/local-secret-handling.md) | active | Local OpenWeather key, mandatory transient `appid` query transport, redaction and evidence boundary. | /spec-design or /feature-to-tasks |
| domain | [.memory-bank/domains/local-data.md](domains/local-data.md) | active | Local domain subjects, provider/location cache-history identity, write ownership, persistence and retention invariants. | /spec-design or /feature-to-tasks |
| state | [.memory-bank/states/lifecycle-map.md](states/lifecycle-map.md) | active | Timer, selected-provider weather freshness and capability-aware forecast-session lifecycle contracts. | /spec-design or /feature-to-tasks |
| testing | [.memory-bank/testing/strategy.md](testing/strategy.md) | active | Bootstrap-owned risk-based testing policy; read-only in this gate. | explicit project-level user decision |
| testing | [.memory-bank/testing/runtime-verification.md](testing/runtime-verification.md) | active | Concrete foundation, integration, secret and target-device proof routes. | /foundation-to-tasks or /feature-to-tasks |
| foundation | [.memory-bank/foundation.md](foundation.md) | active | Foundation Dev Path decision, anchors and feature pressure evidence. | /spec-design or /foundation-to-tasks |

## Planned Specs

| Area | Expected path | Needed by | Notes |
|---|---|---|---|
| feature_subject_concerns | `.memory-bank/contracts/*`, `.memory-bank/domains/*`, `.memory-bank/states/*`, `.memory-bank/testing/*` | /feature-to-tasks | Discover and extend the registered subject paths for concrete FT-001–FT-009 detail; do not create feature-owned hubs. |
| runbook_concerns | `.memory-bank/runbooks/*` | /foundation-to-tasks or /feature-to-tasks | Create only if an operational procedure is required by an evidenced runtime/deployment risk. |

## Broken / Missing Links

- None known in the registered canonical paths after the `/spec-design` link check.

## Update Rules

- Keep this file as registry metadata only: type, canonical path, status, scope,
  change route, planned paths and broken links.
- Canonical identity is the subject-based path. Do not add a separate key,
  feature owner, `used_by` copy or reverse-usage section.
- Do not add global backbone status, matrices, decision bodies, blockers or
  handoffs here; use [spec-backbone.md](spec-backbone.md) and linked specs.
- Data contracts belong to the subject contract that owns their boundary
  compatibility. Detailed lifecycle, schema, rationale and verification rules
  stay in their linked canonical documents.
