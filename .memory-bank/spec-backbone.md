---
description: Global SDD backbone, area matrix and Foundation Dev Path routing state.
status: active
last_updated: 2026-08-13
---
# SDD Spec Backbone

## Pre-PRD Spec Status

- Status: ready_for_prd
- Last updated: 2026-08-10
- Notes: The clarified provider-migration PRD, reviewed scenarios and approved
  L1-L3 decomposition are reflected in the completed Revision `2` global
  backbone. Historical Yandex wording remains migration evidence only.

## Decomposition Inputs

- User scenarios: [.memory-bank/user-scenarios.md](user-scenarios.md)
  (reviewed); detail is in [.memory-bank/prd.md](prd.md), `Users / Actors`,
  `UX / Interaction Flow` and `Acceptance Criteria`.
- Domain model: [.memory-bank/prd.md](prd.md), `Data / Domain Model`; Settings
  owns explicit provider selection and the optional OpenWeather key, while
  Weather Snapshot/History carry provider identity and Forecast Day preserves
  provider-supported availability.
- Constraints: [.memory-bank/prd.md](prd.md), `Non-functional Requirements`
  and `Integrations / Dependencies`; Open-Meteo is default/no-key for personal
  non-commercial use, OpenWeather is explicit with the owner's local key, the
  refresh interval remains 30 minutes, and Android 11/1280×720, offline
  location, explicit palette and lightweight pseudo-glass remain fixed.
- Non-goals: [.memory-bank/prd.md](prd.md), `Non-goals`, `Edge Cases / Failure
  Handling` and `Acceptance Criteria`; automatic cross-provider fallback,
  mixed/synthesized provider data, backend/cloud/accounts, Google Services,
  reboot recovery, pre-install history, V2 Telegram/TTS and unaccepted UI
  scope remain excluded.
- Risks: provider-specific field/timezone mapping, OpenWeather subscription/key
  failures, provider-identified cache/history, strict eight-slot hourly
  completeness, 10-versus-8 daily capability and attribution/terms evidence
  join the retained target-ROM lifecycle, offline-catalog and readability risks.
- Boundary hints: Open-Meteo and OpenWeather are separate external inputs behind
  application-owned normalization; Settings owns explicit selection and the
  optional OpenWeather key, Weather Context owns provider-identified
  cache/history/freshness, and consumers must not trigger cross-provider
  fallback. The Boundary Map now contains the two fixed adapter nodes and their
  Weather Context edges.
- Lifecycle hints: launch, valid city change, explicit provider change and the
  30-minute cadence may request only the selected provider; matching cache is
  usable for up to 24 hours. Hourly entry requires all eight fixed slots;
  long-term entry requires 10 Open-Meteo or 8 OpenWeather records, with two
  honest empty OpenWeather positions in the ten-position projection.

## Open Design Questions

- None. The fixed two-adapter topology, provider-neutral boundary, selection
  ownership, provider/location cache identity, capability matrix, no-fallback
  rule, secret transport and verification routes are authoritative in the
  registered subject specs.

## Handoff To /prd-to-features

- Ready: yes.
- Required reads: [.memory-bank/prd.md](prd.md),
  [.memory-bank/glossary.md](glossary.md),
  [.memory-bank/user-scenarios.md](user-scenarios.md),
  [.memory-bank/invariants.md](invariants.md) and `Decomposition Inputs` above.
- Stop conditions: do not reintroduce Yandex as a target, automatic fallback,
  mixed/synthesized provider data, ten filled OpenWeather days, or a partial
  OpenWeather hourly screen; route any new product/domain branch to the
  operator rather than changing existing feature/task lifecycle.

## Provider-Migration Design Result

- Global Backbone remains `complete` and advances exactly once to Planning
  Revision `2` because external API, module-boundary and security contracts
  changed.
- The accepted target uses only Open-Meteo Forecast and OpenWeather One Call
  3.0 adapters behind the existing provider-neutral boundary. Weather Context
  is the sole normalization/cache/history owner; Settings & Location owns the
  persisted selection and optional local OpenWeather key.
- Canonical specs are the reused subject paths
  [.memory-bank/architecture/system-architecture.md](architecture/system-architecture.md),
  [.memory-bank/contracts/boundary-map.md](contracts/boundary-map.md),
  [.memory-bank/contracts/weather-provider.md](contracts/weather-provider.md),
  [.memory-bank/contracts/local-secret-handling.md](contracts/local-secret-handling.md),
  [.memory-bank/domains/local-data.md](domains/local-data.md),
  [.memory-bank/states/lifecycle-map.md](states/lifecycle-map.md) and
  [.memory-bank/testing/runtime-verification.md](testing/runtime-verification.md).
  No parallel provider spec/hub is created.
- Open-Meteo is default/no-key under the accepted personal non-commercial Free
  API and attribution boundary. OpenWeather is explicit selection and uses the
  owner's local key in the official transient HTTPS `appid` query, with the URL
  redacted before persistence, diagnostics or evidence.
- Yandex production code and provider-less persisted records remain labelled
  brownfield migration evidence only. They are not a third target adapter and
  cannot be relabelled as either accepted provider.

## Deferred Design Routes

No material product/domain decision remains unresolved. The following design
details are intentionally routed without changing the accepted backbone:

- FT-000 establishes the Gradle/project package, executable entry, local
  persistence primitive and first host-side probes. Target-device probes are
  deferred until the application is ready for runtime/readiness validation.
- Provider-specific serializer types and fixture literals are bounded
  FT-002/003/004/008 detail under the accepted global endpoints, mapping and
  isolation contracts.
- Target-device probes establish observed custom-ROM lifecycle/audio behavior;
  they do not add reboot recovery or a new product boundary.
- The final application name/package may be selected before packaging.

## Backbone Area Matrix

| Area | Status | Authoritative source | Notes |
|---|---|---|---|
| architecture_style | authoritative | [System Architecture](architecture/system-architecture.md), operator confirmation 2026-08-04 | One deployable Kotlin Android modular monolith with capability slices. |
| source_of_truth | authoritative | [Local Data](domains/local-data.md), [System Architecture](architecture/system-architecture.md) | Application-owned local normalized state; Weather Context is the sole normalized weather/cache/history owner, while selected provider and OS remain external input authorities. |
| module_boundaries | authoritative | [Boundary Map](contracts/boundary-map.md), [Capability Interfaces](contracts/capability-interfaces.md) | Five capability slices, exactly two weather adapters/endpoints, explicit write owners and accepted directed edges. |
| user_scenarios | authoritative | [Reviewed User Scenarios](user-scenarios.md) | Scenario-sensitive input has parseable `Status: reviewed`. |
| constraints | authoritative | [PRD](prd.md), [Constitution](constitution.md) | Target device, offline behavior, Open-Meteo default/no-key, explicit OpenWeather/local-key, visual/readability and KISS constraints are preserved. |
| non_goals | authoritative | [PRD](prd.md), [Invariants](invariants.md) | Backend/cloud/accounts, Google Services, reboot recovery, pre-install history and unaccepted V2/UI scope remain excluded. |
| domain_model | authoritative | [Local Data](domains/local-data.md), [PRD](prd.md) | Ownership covers provider selection, provider/location cache-history identity, capability-aware forecast positions, settings/location, timer and forecast session data. |
| data_flow | authoritative | [System Architecture](architecture/system-architecture.md), [Capability Interfaces](contracts/capability-interfaces.md) | Settings supplies validated selection/context; Weather Context invokes only the selected adapter and owns normalization/state; consumers cannot bypass it. |
| storage | authoritative | [Local Data](domains/local-data.md), [Local Secret Handling](contracts/local-secret-handling.md) | Owner-local persistence and provider/location identity are fixed; the OpenWeather key remains a Settings-owned secret and is absent from non-secret state. |
| api_contracts | authoritative | [Weather Provider](contracts/weather-provider.md), [Capability Interfaces](contracts/capability-interfaces.md) | Exact Open-Meteo Forecast and OpenWeather One Call 3.0 endpoints, provider-neutral envelope, mapping, capability and no-fallback contracts are authoritative. |
| event_message_contracts | not_applicable | [System Architecture](architecture/system-architecture.md#architecture-spine) | `not_applicable - accepted single-runtime V1 has no internal event/message boundary or broker; provider communication is an API contract.` |
| agent_io_contracts | not_applicable | [PRD](prd.md), [System Architecture](architecture/system-architecture.md) | `not_applicable - V1 has no agent, tool, plugin or protocol I/O boundary.` |
| security_safety | authoritative | [Local Secret Handling](contracts/local-secret-handling.md), [Invariants](invariants.md), [Runtime Verification](testing/runtime-verification.md) | OpenWeather `appid` is allowed only in transient outbound HTTPS construction and redacted elsewhere; Open-Meteo is credential-free; retained OS/audio safety remains explicit. |
| deployment | authoritative | [System Architecture](architecture/system-architecture.md), [Platform Runtime](contracts/platform-runtime.md) | One manually installed APK for Android 11 custom ROM; Foundation establishes the executable baseline. |
| risks | authoritative | [Runtime Verification](testing/runtime-verification.md), [Weather Provider](contracts/weather-provider.md), [PRD](prd.md) | No-key/terms, subscription/query-secret, provider isolation, strict hourly, 10-versus-8+2 mapping and target-runtime risks have explicit proof or residual-risk routes. |
| open_questions | authoritative | [Provider-Migration Design Result](#provider-migration-design-result), [Foundation](foundation.md) | No unresolved global target or Foundation decision remains; serializer/fixture literals are bounded feature detail. |

## Global Backbone Status

- Status: complete
- Planning Revision: 2
- Mode: strict_architecture_scaffold
- Architecture artifact strategy: split-by-boundary-topic
- Not applicable areas:
  - event_message_contracts: not_applicable - accepted single-runtime V1 has no internal event/message boundary or broker; provider communication is an API contract.
  - agent_io_contracts: not_applicable - V1 has no agent, tool, plugin or protocol I/O boundary.
- Notes: Provider-migration API, boundary, state and security contracts are
  reconciled without production-code or task-state changes. The FT-000
  Foundation Gate remains closed: TASK-001 established the executable Android
  baseline and the explicit owner closed TASK-002 on accepted host-only
  evidence. Previous indexed task-plan reviews are `APPROVE` for Planning
  Revision `2`; W20 closure is reconciled and the affected downstream
  dependency route still requires fresh review while preserving all historical
  task statuses and evidence.
  Existing target-runtime and live-provider evidence remains deferred under the
  registered verification routes.

## Handoff To /foundation-to-tasks

- Global Backbone Status: complete, Planning Revision 2.
- Foundation Required: true; the normal FT-000 queue is complete.
- Foundation anchors: [.memory-bank/foundation.md](foundation.md) with
  `Foundation Gate Task: TASK-002-T3-FT-000-W1`.
- Foundation result: Revision `2` revalidation is complete. The migration
  reuses the established adapter seam, owner-local state and fixture harness
  and needs no new Foundation work. `TASK-002-T3-FT-000-W1` remains `done`;
  existing anchors, historical task statuses and evidence remain unchanged.

## Handoff To /feature-to-tasks

- Revision `2` remains current without rewriting historical task evidence.
  Foundation revalidation, `/feature-to-tasks --all` reconciliation and the
  pre-repair task-plan reviews are complete for FT-001–FT-009; W20 and W18 are
  closed on their current evidence, while the affected downstream recovery
  route still requires fresh feature-plan review.
- Provider-migration feature-level design is `complete` for FT-002, FT-003,
  FT-004 and FT-008. Revision-2 planning now includes
  `TASK-019-T3-FT-008-W16`, failed `TASK-020-T3-FT-002-W17`, completed
  `TASK-023-T3-FT-002-W20`, completed `TASK-021-T2-FT-003-W18` and
  completed `TASK-022-T2-FT-004-W19`. W17's identity, 3/3 history and evidence
  remain unchanged; W18's prior dependency-block history and W19's earlier
  blocked history remain retained as historical task-card evidence.
- W19 task closure/evidence is reconciled at this boundary; FT-004 is
  `implemented` and REQ-010 is `implemented`, while EP-002 and EP-004 remain
  `planned`. Planning Revision remains `2`; no feature/epic promotion,
  scheduler checkpoint or terminal-state transition is applied here.
- The remainder of this section records pre-migration planning/execution history
  only; Yandex-specific evidence does not define the target or authorize
  lifecycle changes. Historically, product feature SDD design was complete for
  FT-001–FT-009. The FT-001
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
- The W16 boundary records `TASK-019-T3-FT-008-W16` as `done` after final
  Attempt-3 executor `PASS_FOR_HANDOFF`, fresh functional `PASS` and fresh
  independent semantic `semantic-pass`. Attempt 1's functional evidence
  failure and Attempt 2's semantic failure remain traceable in task-owned
  records; final evidence is linked from FT-008, FT-002 and their implementation
  plans. FT-008 and direct RTM rows REQ-017/018/024/027/028 are reconciled to
  `implemented`; EP-004 remains `planned` because FT-009 remains planned.
  Provider-unidentified legacy key access/refresh is intentionally denied as a
  transition safeguard; planned TASK-020 must atomically replace that deny with
  selected-OpenWeather-authorized access while implementing selected-provider
  dispatch. Physical-device/live-provider evidence remains `DEFERRED`, with no
  runtime `PASS`. TASK-020/021/022 stay `planned`; promotion, dependent-state,
  scheduler-checkpoint and terminal-state changes remain outside this sync.
- The W17 boundary records already-authoritative TASK-020 as `failed` after
  final Attempt-3 executor `PASS_FOR_HANDOFF`, functional `PASS`, required
  semantic `semantic-fail` and exhausted `3/3` attempt budget. Implemented
  provider-migration facts remain separately durable: exactly Open-Meteo and
  OpenWeather, no Yandex production path, ordinary selected-only dispatch,
  provider/location state identity and redacted credential handling. The
  accepted activation outcome failed because first-time OpenWeather selection
  refreshes before key entry and a later valid-key save causes no refresh,
  leaves an obsolete missing-key error and makes zero provider calls.
  TASK-021 is directly `blocked`; TASK-022 is transitively `blocked`; neither
  has execution or acceptance evidence. FT-002/003/004, EP-002 and open RTM
  lifecycle values remain `planned`. Device/live-provider evidence remains
  `DEFERRED`, with no runtime `PASS`. No fourth TASK-020 execution, promotion or
  unblock is eligible; the W20 repair is indexed as
  `TASK-023-T3-FT-002-W20`, and the W18 direct dependency is rebuilt to it.
- The W20 boundary records already-authoritative `TASK-023-T3-FT-002-W20` as
  `done` after executor `PASS_FOR_HANDOFF`, fresh functional `PASS` and final
  independent T3 `semantic-pass`; current handoff, verifier-owned host/timer,
  functional and semantic evidence are linked from the task and FT-002 plan.
  W17 remains failed after `3/3`; W18 is done with fresh AC-001/AC-005
  claim-linked completeness evidence, and W19 is done with fresh
  AC-001/AC-002/AC-005/AC-006 evidence plus the FT-004 feature-level
  `semantic-pass`. REQ-007, REQ-009, REQ-010 and REQ-029 are now
  `implemented`; FT-002 and FT-003 remain `planned`, FT-004 is `implemented`,
  and EP-002/EP-004 remain `planned`. Target-device/live-provider evidence
  remains `DEFERRED`, with no runtime `PASS` claim. Scheduler post-sync gates,
  promotion and terminal-state handling remain outside this sync.
- The W21 boundary records already-authoritative `TASK-024-T3-FT-001-W21` as
  `done` after fresh executor `PASS_FOR_HANDOFF`, functional `PASS` and T3
  semantic `semantic-pass`. Its Main Display geometry evidence proves the
  left/central/right composition, ordered cards, Today `279 > 223`, equal
  non-today cards and `16/16/16` gaps. Samsung/custom-ROM 1280x720 target
  evidence remains `DEFERRED`, with no runtime `PASS`; W22 remains planned
  behind W21 and scheduler promotion remains outside this sync.
- The W22 boundary records already-authoritative `TASK-025-T3-FT-002-W22` as
  `done` after fresh executor `PASS_FOR_HANDOFF`, functional `PASS` and T3
  semantic `semantic-pass`. Its display-owned Canvas/Path/Paint illustrations,
  contact sheet and measured bounds prove the six accepted condition states,
  no text/emoji and non-overlap while preserving Weather Context/provider
  ownership. Samsung/custom-ROM 1280x720 target evidence remains `DEFERRED`,
  with no runtime `PASS`; the new `TASK-028-T3-FT-002-W25` remains planned
  after `TASK-027-T3-FT-001-W24` for the bounded icon-bounds/sun/pressure-arrow
  adjustment, and scheduler promotion remains outside this sync.

- The W23 boundary records already-authoritative `TASK-026-T3-FT-007-W23` as
  `done` after fresh executor `PASS_FOR_HANDOFF`, functional `PASS` and T3
  semantic `semantic-pass`. Host fake-platform evidence proves first request/
  start, repeat, dismissal, 30-minute cap and six denial/error cases; physical
  audibility remains `DEFERRED`, with no runtime `PASS`. No new audio framework,
  dependency, permission or event boundary was introduced.

- The W24 boundary records `TASK-027-T3-FT-001-W24` as `done` after executor
  `PASS_FOR_HANDOFF`, fresh functional `PASS` and independent T3
  `semantic-pass` on Attempt 2. Reachable idle ticker evidence proves the
  enlarged `176f` clock persists while countdown remains `32f`; the three
  existing right-side controls are `220x220` circles with radius `110`.
  Host suite, contact sheet, bounds, rubric and regression evidence pass.
  Samsung/custom-ROM target readability and runtime circle rendering remain
  `DEFERRED`, with no runtime `PASS`; W25 remains planned behind W24 and
  scheduler promotion remains outside this sync.

- The W25 boundary records `TASK-028-T3-FT-002-W25` as `done` after executor
  `PASS_FOR_HANDOFF`, fresh functional `PASS` and independent T3
  `semantic-pass`. Its six-state Main Display illustration evidence proves
  approximately 69.5–70.2% painted envelopes without clipping/overlap, a
  `1.1789474x` CLEAR sun disk, and visible `5 px` Canvas/Path pressure arrows
  with zero/steady absence. Weather Context pressure semantics and card/timer/
  audio/provider boundaries remain unchanged. Samsung/custom-ROM target
  readability and runtime Canvas compatibility remain `DEFERRED`, with no
  runtime `PASS`; W25 is the final indexed task in this visual route.

- The W26 boundary records `TASK-029-T3-FT-001-W26` as `done` after executor
  `PASS_FOR_HANDOFF`, fresh functional `PASS` and independent T3
  `semantic-pass`. Its idle Main Display evidence proves adaptive clock sizes
  `188.75`/`139.75`, `200x200` preset circles with `24` spacing and card
  geometry `217/273/217/217` with gap `24`; host gates pass. Active countdown
  and overdue surfaces remain excluded for later FT-006/FT-007 planning.
  Samsung/custom-ROM target readability and runtime neon/geometry rendering
  remain `DEFERRED`, with no runtime `PASS`; the next feature routes remain
  outside this sync.

- The W27 boundary records `TASK-030-T3-FT-006-W27` as `done` after executor
  `PASS_FOR_HANDOFF`, fresh functional `PASS` and independent T3
  `semantic-pass`. Its active countdown evidence proves a dedicated
  no-weather/no-city/no-date/no-card surface, digits `228.0` versus idle
  `188.75`, and a transparent preset-colored neon circle; timer lifecycle,
  protected gestures and offline independence remain green. Overdue rendering
  and audio remain excluded for FT-007. Target/device/audio runtime evidence
  remains `DEFERRED`, with no runtime `PASS`.

- The W28 boundary records already-authoritative `TASK-031-T3-FT-007-W28` as
  `done` after executor `PASS_FOR_HANDOFF`, fresh functional `PASS` and T3
  semantic `semantic-pass`. At `1280x720`, its dedicated overdue surface proves
  stable elapsed `256.0` larger than active `228.0` and idle `188.75`, a
  transparent activating-preset-colored circle, independently blinking plus,
  and fitting non-overlapping bounds. The exact production/test boundary is
  `DisplayCapability.kt` plus `DisplayProjectionTest.kt`; Timer & Alert,
  lifecycle, dismissal, platform/audio ownership and W8/W23/W27 history remain
  unchanged. Target/device/audio evidence is `DEFERRED`, with no runtime or
  physical-audibility `PASS`; no new spec, graph edge, dependency, contract or
  lifecycle decision is introduced. FT-007 and direct RTM lifecycle values
  remain unchanged, EP-003 remains `planned`, and promotion, dependent-state,
  scheduler-checkpoint and terminal-state handling remain outside this sync.

- The W29 boundary preserves already-authoritative
  `TASK-032-T3-FT-001-W29` as terminal `failed` under its scheduler provenance
  disposition. The missing honest pre-write RED and prior executor summary are
  an authority/provenance gap, not an evidenced product semantic failure;
  W26/W28 evidence is not promoted to W29 RED, and the exact two-file code
  history plus all W29 reports/protocols remain unchanged. W29 does not alter
  FT-001/EP-001 lifecycle, RTM values, canonical specs, the dependency graph,
  checkpoint or terminal handling.

- The W30 boundary records already-authoritative
  `TASK-033-T3-FT-001-W30` as `done` after executor `PASS_FOR_HANDOFF`, fresh
  functional `PASS` and T3 `semantic-pass`. Fresh verifier-owned host evidence
  supports the accepted `RED_NOT_APPLICABLE` route at `2460×1080` and
  `1280×720`: the full `HH:mm`, four ordered `NO_DATA`/partial/populated
  shells, and the three preset radial/rim/glow claims remain green without a
  production/test behavior write. The exact two-file boundary and Main
  Display → Weather Context / Timer & Alert ownership edges remain unchanged;
  all five host gates passed, while target/device/runtime evidence remains
  `DEFERRED` and is not promoted to runtime `PASS`. No new spec, contract,
  graph edge, dependency, lifecycle or RTM decision is introduced. See the
  [W30 sync report](../.tasks/TASK-033-T3-FT-001-W30/TASK-033-T3-FT-001-W30-S-MB-SYNC-final-report-docs-01.md).

- The W31 boundary records already-authoritative
  `TASK-034-T3-FT-001-W31` as `done` after executor `PASS_FOR_HANDOFF`, fresh
  physical TECNO LI6 RED/GREEN at `2460×1080`, functional `PASS` and T3
  `semantic-pass`. The complete `HH:mm` is physically contained and dominant;
  weather illustrations are materially reduced; city/date, four ordered slots
  and separate right-side timer controls remain stable. Host geometry at
  `2460×1080` and `1280×720` plus all five host gates are supporting evidence,
  explicitly separate from the physical visual PASS. The exact two-file
  boundary and Main Display → Weather Context / Timer & Alert read-only edges
  remain unchanged; emulator/AVD/QEMU is forbidden. Other resolutions/devices,
  custom-ROM rendering, physical audio audibility and live provider refresh
  remain residual risks outside W31. No canonical spec, contract, graph edge,
  dependency, feature/epic lifecycle or RTM decision is introduced. See the
  [W31 sync report](../.tasks/TASK-034-T3-FT-001-W31/TASK-034-T3-FT-001-W31-S-MB-SYNC-final-report-docs-01.md).

- The W34 boundary records already-authoritative
  `TASK-037-T3-FT-001-W34` as `done` after executor `PASS_FOR_HANDOFF`, fresh
  physical TECNO LI6 RED/GREEN at `2460×1080`, functional `PASS` and T3
  `semantic-pass`. Host and native View receipts prove the accepted mixed
  empty-Yesterday/three-populated allocation at `2460×1080` and `1280×720`;
  the exact two-file Main Display boundary and Weather Context / Timer & Alert
  read-only edges remain unchanged. W31 remains `done`, W32 remains `failed`,
  and W33 remains `blocked`, including its superseded policy-invalid
  `blocked -> failed` transition history. No canonical spec, contract, graph
  edge, dependency, feature/epic lifecycle or RTM decision is introduced. The
  separate oversized timer-digit observation remains FT-007 residual scope.
  See the [W34 sync report](../.tasks/TASK-037-T3-FT-001-W34/TASK-037-T3-FT-001-W34-S-MB-SYNC-final-report-docs-01.md).
