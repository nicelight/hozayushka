---
description: Global SDD backbone, area matrix and Foundation Dev Path routing state.
status: active
last_updated: 2026-08-04
---
# SDD Spec Backbone

## Pre-PRD Spec Status

- Status: completed_for_decomposition
- Last updated: 2026-08-04
- Notes: The clarified PRD, reviewed scenarios, glossary, domain/lifecycle
  inputs and product feature set are available. The pre-PRD gate is complete;
  the global backbone below is now the active design gate.

## Decomposition Inputs

- User scenarios: [.memory-bank/user-scenarios.md](user-scenarios.md)
  (reviewed); detail is in [.memory-bank/prd.md](prd.md), `Users / Actors`,
  `UX / Interaction Flow` and `Acceptance Criteria`.
- Domain model: [.memory-bank/prd.md](prd.md), `Data / Domain Model`, refined
  by [.memory-bank/domains/local-data.md](domains/local-data.md).
- Constraints: [.memory-bank/prd.md](prd.md), `Non-functional Requirements`
  and `Integrations / Dependencies`; target Android 11 custom ROM, landscape
  1280×720, local API key, offline location, explicit palette and lightweight
  pseudo-glass.
- Non-goals: [.memory-bank/prd.md](prd.md), `Non-goals`, `Edge Cases / Failure
  Handling` and `Acceptance Criteria`; backend/cloud/accounts, Google Services,
  reboot recovery, pre-install history, V2 Telegram/TTS and unaccepted UI scope
  remain excluded.
- Risks: target-ROM timer lifecycle, offline dataset footprint, provider field
  availability, API-key evidence and device readability are routed to
  [.memory-bank/testing/runtime-verification.md](testing/runtime-verification.md).
- Boundary target: [.memory-bank/contracts/boundary-map.md](contracts/boundary-map.md)
  and [.memory-bank/architecture/system-architecture.md](architecture/system-architecture.md).
- Lifecycle target: [.memory-bank/states/lifecycle-map.md](states/lifecycle-map.md).

## Deferred Design Routes

No material global target decision remains unresolved. The following concrete
details are intentionally routed without changing the accepted backbone:

- FT-000 establishes the Gradle/project package, executable entry, local
  persistence primitive and first host-side probes. Target-device probes are
  deferred until the application is ready for runtime/readiness validation.
- FT-002–FT-004 and FT-008 finalize provider field mapping and redacted fixture
  shapes within the accepted Yandex contract.
- Target-device probes establish observed custom-ROM lifecycle/audio behavior;
  they do not add reboot recovery or a new product boundary.
- The final application name/package may be selected before packaging.

## Backbone Area Matrix

| Area | Status | Authoritative source | Notes |
|---|---|---|---|
| architecture_style | authoritative | [System Architecture](architecture/system-architecture.md), operator confirmation 2026-08-04 | One deployable Kotlin Android modular monolith with capability slices. |
| source_of_truth | authoritative | [Local Data](domains/local-data.md), [System Architecture](architecture/system-architecture.md) | Application-owned local normalized state; provider and OS remain external input authorities. |
| module_boundaries | authoritative | [Boundary Map](contracts/boundary-map.md), [Capability Interfaces](contracts/capability-interfaces.md) | Five capability slices, explicit write owners and accepted directed edges. |
| user_scenarios | authoritative | [Reviewed User Scenarios](user-scenarios.md) | Scenario-sensitive input has parseable `Status: reviewed`. |
| constraints | authoritative | [PRD](prd.md), [Constitution](constitution.md) | Target device, offline behavior, local key, visual/readability and KISS constraints are preserved. |
| non_goals | authoritative | [PRD](prd.md), [Invariants](invariants.md) | Backend/cloud/accounts, Google Services, reboot recovery, pre-install history and unaccepted V2/UI scope remain excluded. |
| domain_model | authoritative | [Local Data](domains/local-data.md), [PRD](prd.md) | Ownership matrix covers settings/location, weather, history, timer and forecast session data. |
| data_flow | authoritative | [System Architecture](architecture/system-architecture.md), [Capability Interfaces](contracts/capability-interfaces.md) | Provider, platform and in-process flows have explicit owners and no direct storage bypass. |
| storage | authoritative | [Local Data](domains/local-data.md), [Local Secret Handling](contracts/local-secret-handling.md) | Local persistence ownership and invariants are fixed; exact project-native primitive is a bounded FT-000 detail. |
| api_contracts | authoritative | [Weather Provider](contracts/weather-provider.md), [Capability Interfaces](contracts/capability-interfaces.md) | Yandex REST and in-process capability contracts are registered; feature-level field fixtures remain downstream. |
| event_message_contracts | not_applicable | [System Architecture](architecture/system-architecture.md#architecture-spine) | `not_applicable - accepted single-runtime V1 has no internal event/message boundary or broker; provider communication is an API contract.` |
| agent_io_contracts | not_applicable | [PRD](prd.md), [System Architecture](architecture/system-architecture.md) | `not_applicable - V1 has no agent, tool, plugin or protocol I/O boundary.` |
| security_safety | authoritative | [Local Secret Handling](contracts/local-secret-handling.md), [Invariants](invariants.md), [Runtime Verification](testing/runtime-verification.md) | API-key locality/redaction, OS-owned audio policy and visual overdue safety are explicit. |
| deployment | authoritative | [System Architecture](architecture/system-architecture.md), [Platform Runtime](contracts/platform-runtime.md) | One manually installed APK for Android 11 custom ROM; Foundation establishes the executable baseline. |
| risks | authoritative | [Runtime Verification](testing/runtime-verification.md), [PRD](prd.md) | Lifecycle, audio, provider fixture, persistence and device-readability risks have proof routes. |
| open_questions | authoritative | [System Architecture Deferred Design Routes](architecture/system-architecture.md#deferred-design-routes), [Foundation](foundation.md) | No unresolved global target question; remaining implementation decisions have an owner and trigger. |

## Global Backbone Status

- Status: complete
- Planning Revision: 1
- Mode: strict_architecture_scaffold
- Architecture artifact strategy: split-by-boundary-topic
- Not applicable areas:
  - event_message_contracts: not_applicable - accepted single-runtime V1 has no internal event/message boundary or broker; provider communication is an API contract.
  - agent_io_contracts: not_applicable - V1 has no agent, tool, plugin or protocol I/O boundary.
- Notes: Global/shared target rules are authoritative. Foundation Dev Path
  remains required before product feature task design: TASK-001 establishes
  the preliminary executable Android baseline and TASK-002 proves the final
  clean/reset host baseline. Target-runtime compatibility is a later
  readiness/release gate and is not executed while the application is still a
  walking skeleton. Feature-level detail remains routed to FT-000 and
  `/feature-to-tasks`.

## Handoff To /foundation-to-tasks

- Global Backbone Status: complete, Planning Revision 1.
- Foundation Required: true; the normal FT-000 queue is created.
- Foundation anchors: [.memory-bank/foundation.md](foundation.md) with
  `Foundation Gate Task: TASK-002-T3-FT-000-W1`.
- Immediate route: run `/mb-doctor --strict`, then execute the FT-000 queue
  through the `/autonomous`-owned Foundation phase.

## Handoff To /feature-to-tasks

- Product feature design remains draft for FT-001–FT-009.
- Do not start product task design until the Foundation Gate is done and its
  executable commands/probes are recorded.
- After Foundation Gate completion, run `/feature-to-tasks FT-<NNN>` in manual
  flow or `/spec-auto --all` in autonomous flow, then review the feature task
  plans for positive Planning Revision `1`.
