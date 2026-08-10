---
description: Global SDD backbone, area matrix and Foundation Dev Path routing state.
status: active
last_updated: 2026-08-10
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
- Notes: Global/shared target rules are authoritative. The FT-000 Foundation
  Gate is closed: TASK-001 established the executable Android baseline and the
  explicit owner closed TASK-002 on the accepted host-only evidence. No fresh
  `/verify` or `/red-verify` run followed the scope revision; that omission and
  deferred target-runtime compatibility are accepted residual risk. Product
  feature detail is now routed to `/feature-to-tasks`.

## Handoff To /foundation-to-tasks

- Global Backbone Status: complete, Planning Revision 1.
- Foundation Required: true; the normal FT-000 queue is complete.
- Foundation anchors: [.memory-bank/foundation.md](foundation.md) with
  `Foundation Gate Task: TASK-002-T3-FT-000-W1`.
- Foundation result: `TASK-002-T3-FT-000-W1` is `done`; no FT-000 execution
  remains.

## Handoff To /feature-to-tasks

- Product feature SDD design is complete for FT-001–FT-009. The FT-001
  planning surface is retained and indexed at the historical
  `TASK-003-T3-FT-001-W2` (`done`) plus the failed W11 follow-up
  `TASK-014-T3-FT-001-W11` (`failed`); FT-002 is reconciled and indexed at
  `TASK-004-T3-FT-002-W3` (`done`); FT-003 has the indexed W4/W5 execution
  records `TASK-005-T3-FT-003-W4` (`failed`, historical),
  `TASK-012-T3-FT-003-W4` (`done`, provider-normalization repair) and
  `TASK-013-T3-FT-003-W5` (`done`, entry/fallback/shared-session follow-up);
  FT-004 has the indexed completed task
  `TASK-006-T3-FT-004-W5` (`done`). FT-005 has the indexed completed task
  `TASK-007-T3-FT-005-W6` (`done`); FT-006 has the completed task
  `TASK-008-T3-FT-006-W7` (`done`) and feature lifecycle `implemented`; FT-007
  has the completed task `TASK-009-T3-FT-007-W8` (`done`) and feature lifecycle
  `implemented`; FT-008 has the indexed completed task
  `TASK-010-T3-FT-008-W9` (`done`) while its feature/RTM lifecycle values are
  unchanged by this sync; FT-009 has the indexed completed task
  `TASK-011-T3-FT-009-W10` (`done`) while its feature/RTM lifecycle values
  remain `planned`.
- The Foundation Gate is done and its host commands/probes are recorded.
- The W7 boundary does not change the complete global backbone or infer an
  epic lifecycle/promotion/dependency transition. FT-006's direct REQ-012,
  REQ-013, REQ-014 and REQ-025 rows are reconciled to `implemented`; REQ-011
  remains owned by FT-005 and `planned`. Target-device evidence remains
  deferred/non-blocking with no runtime PASS claim.
- The W8 boundary records `TASK-009-T3-FT-007-W8` as `done` after retry-2 fresh
  functional `PASS` and semantic `semantic-pass`; direct REQ-015/016 rows and
  FT-007 lifecycle are reconciled to `implemented`. Target-device evidence
  remains deferred/non-blocking with no runtime PASS claim. EP-003 remains
  `planned` because FT-005/REQ-011 is still planned.
- The W9 boundary records `TASK-010-T3-FT-008-W9` as `done` after fresh
  functional `PASS` and semantic `semantic-pass`; host/build/unit/static and
  redacted provider/catalog evidence is linked from the task and FT-008. Target
  Settings readability/navigation evidence remains deferred/non-blocking with
  no runtime PASS claim. FT-008, REQ-017/018/024, EP-004, dependencies,
  scheduler checkpoint and terminal state remain unchanged; `spec-index.md`
  remains registry-only.
- The W10 boundary records `TASK-011-T3-FT-009-W10` as `done` after fresh
  functional `PASS` and semantic `semantic-pass`; FT-009 evidence is linked
  from the feature and task-owned verification artifacts. Target-only Settings
  readability/static pseudo-glass evidence remains deferred/non-blocking with
  no runtime PASS claim. FT-009, REQ-019/020/021, EP-004, dependencies,
 scheduler checkpoint and terminal state remain unchanged; `spec-index.md`
 remains registry-only. Scheduler post-sync lint/strict-doctor and any
 promotion or dependent-state pass remain outside this reconciliation.
- The W11 boundary records `TASK-014-T3-FT-001-W11` as `failed` after final
  retry-3 functional `PASS` and semantic `semantic-fail`: a public non-city
  weather-card double tap left the active countdown running, violating the
  existing FT-006-AC-003 / REQ-013 contract. The final semantic report and
  task-local bug note are linked from FT-001 and FT-006. TASK-003, FT-001 and
  FT-006 lifecycle/RTM values, dependencies, Planning Revision `1`, the
  scheduler checkpoint and terminal state remain unchanged; `spec-index.md`
  remains a pure registry. Normal indexed FT-001 planning retained the
  failed W11 record and added `TASK-015-T3-FT-001-W12` (initially `planned`),
  whose only task-owned acceptance is the bounded FT-001-AC-005 city
  hold/Settings-preservation delta. The existing downstream protected-
  cancellation contract (REQ-013) remains a regression guard only; its exact
  FT-006 canonical basis is retained in TASK-015 `normative_inputs`, with no
  new FT-006 task or lifecycle change. No new design or contract decision is
  made here; lifecycle/RTM values, existing-task dependencies, Planning
  Revision `1`, scheduler checkpoint and terminal state remain unchanged.
- The W12 boundary records `TASK-015-T3-FT-001-W12` as `done` after executor
  `PASS_FOR_HANDOFF`, fresh functional `PASS` and independent semantic
  `semantic-pass`. The task-owned acceptance remains the bounded FT-001-AC-005
  Main Display city hold/Settings-preservation delta; REQ-013 is a regression
  guard only. FT-001 remains `implemented`; FT-006, REQ-013 and Timer & Alert
  ownership remain unchanged. Generic-emulator evidence is decisive for the
  public matrix, while Samsung/custom-ROM/1280x720 physical evidence remains
  `DEFERRED` with no promoted runtime `PASS`. The task registry already indexes
  W12; `requirements.md` RTM and `spec-index.md` remain unchanged and pure,
  respectively. Scheduler checkpoint, terminal state, retry counters,
  promotion and dependent-state reconciliation remain outside this sync.
- The W13 boundary records `TASK-016-T3-FT-001-W13` as `done` after executor
  `PASS_FOR_HANDOFF`, fresh functional `PASS` and independent durable semantic
  `semantic-pass`; executor, functional and semantic evidence is linked from
  FT-001 and its implementation plan. FT-001/EP-001 lifecycles and direct RTM
  values `REQ-002`, `REQ-003` and `REQ-022` remain `implemented`; Weather
  Context, Timer & Alert, Forecast and the accepted architecture/spec contracts
  remain unchanged. W13 is host/static proof only; Samsung/custom-ROM/1280x720
  physical evidence remains `DEFERRED` with no target-device runtime `PASS`
  claim. The task registry already indexes W13, `spec-index.md` remains a pure
  registry, and no feature/epic closure, promotion, dependent-state,
  scheduler-checkpoint or terminal-state transition is performed by this sync.
- The W14 boundary records `TASK-017-T3-FT-001-W14` as `done` after executor
  `PASS_FOR_HANDOFF`, fresh functional `PASS` and independent durable semantic
  `semantic-pass`; executor, functional and semantic evidence is linked from
  FT-001, FT-002 and the FT-001 implementation plan. The task-owned locator
  remains `FT-001-AC-002 / REQ-002`; `REQ-007`, `REQ-022` and `REQ-025` remain
  governing regression constraints, with existing RTM values unchanged.
  Weather Context keeps cache/history, refresh, freshness and projection
  ownership, and the existing Main Display → Weather Context edge remains
  unchanged. W14 is host/static proof only; Samsung/custom-ROM/1280x720
  physical evidence remains `DEFERRED` with no target-device runtime `PASS`
  claim. The task registry already indexes W14, `spec-index.md` remains a
  pure registry, and no feature/epic closure, promotion, dependent-state,
  scheduler-checkpoint or terminal-state transition is performed by this sync.
- The W15 boundary records `TASK-018-T3-FT-002-W15` as `done` after executor
  `PASS_FOR_HANDOFF`, fresh functional `PASS` and independent semantic
  `semantic-pass`; current task-owned evidence is linked from FT-002, its
  implementation plan and EP-002 routing. W15 proves the accepted Yandex
  request/mapping/failure/cache, synthetic redaction, fixture isolation and
  off-UI wiring delta behind the existing provider boundary. Direct RTM values
  `REQ-005`, `REQ-007`, `REQ-022`, `REQ-025` and `REQ-026` remain `implemented`;
  `REQ-024` remains `planned` under FT-008. Existing provider/public contracts,
  graph ownership, FT-002 lifecycle and EP-002 lifecycle remain unchanged.
  W15 is host/build/static/redacted proof only; target-device and live-provider
  compatibility remain `DEFERRED`, with no runtime `PASS` claim. The task
  registry already indexes W15, `spec-index.md` remains a pure registry, and
  no promotion, dependent-state, scheduler-checkpoint or terminal-state
  transition is performed by this sync.
