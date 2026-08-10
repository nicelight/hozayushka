---
description: Лог изменений Memory Bank.
status: active
---
# Changelog

## [2026-08-02] Initial setup
- Created Memory Bank skeleton
- Seeded core docs (product, requirements, testing, task registry)

## [2026-08-03] Pre-PRD framing
- Reconciled glossary with the clarified PRD, including the accepted 10-day forecast horizon.
- Captured reviewed user scenarios, preliminary lifecycle hints, invariants and boundary responsibilities.
- Marked pre-PRD status `ready_for_prd`; global SDD backbone remains pending for `/spec-design`.

## [2026-08-03] Product decomposition
- Created the C4 L1 product map, 26 stable requirements with RTM, four L2 epics and nine L3 features.
- Added PRD bootstrap plan/decision log and routed every feature to the mandatory SDD Design Gate.
- Preserved the pending global backbone and did not create tasks or testing-policy artifacts.

## [2026-08-04] Global SDD backbone
- Accepted one deployable Kotlin Android modular monolith with five capability slices and explicit write ownership.
- Registered the architecture spine, boundary graph, capability/provider/platform/secret contracts, local-data and lifecycle specs.
- Set Global Backbone Status to `complete`, Planning Revision `1`, and recorded `Foundation Required: true` without creating task records.

## [2026-08-05] Wave 0 — TASK-001 closure
- Updated: closed `TASK-001-T3-FT-000-W0` after current functional PASS and per-task semantic PASS.
- Fixed: preserved the accepted `Main Display → Weather Context → Yandex Weather Adapter` ownership path in the Foundation probe.
- Retained: `TASK-002-T3-FT-000-W1` remains the separate planned final Foundation Gate.

## [2026-08-06] Wave 1 — FT-000 Foundation closure
- Closed: the explicit owner set `TASK-002-T3-FT-000-W1` to `done` and accepted the existing host-only build/test, fixture, boundary/package and redacted secret-scan evidence.
- Recorded: no fresh `/verify` or `/red-verify` run followed the host-only scope revision; the owner accepted that omission and deferred target-device compatibility as residual risk.
- Synchronized: `REQ-000` and `FT-000` are `verified`, Foundation pressure responses are established, and product feature task decomposition is now allowed.

## [2026-08-07] Wave 2 — FT-001 boundary sync
- Reconciled: `TASK-003-T3-FT-001-W2` is already `done` with functional `PASS` and semantic `semantic-pass` evidence linked from the task record.
- Updated: `REQ-001`, `REQ-002`, `REQ-003`, `REQ-004`, `REQ-022` and `REQ-023`, plus FT-001 and EP-001 implementation lifecycle routing, to `implemented`.
- Recorded: target-only fullscreen, readability, keep-screen-on and interaction observations remain `DEFERRED` with residual risk; unavailable target evidence does not block this T3 queue and no runtime `PASS` was claimed.
- Deferred: scheduler promotion, dependent unblock/block reconciliation and scheduler checkpoint updates remain owner-owned post-sync actions.

## [2026-08-08] Wave 3 — FT-002 boundary sync
- Reconciled: `TASK-004-T3-FT-002-W3` is already `done` with current attempt-2 functional `PASS` and required semantic `semantic-pass` evidence.
- Updated: direct FT-002 RTM owners `REQ-005`, `REQ-006`, `REQ-007`, `REQ-008` and `REQ-026` to `implemented`; FT-002 lifecycle is `implemented` and current attempt-2 evidence is linked from the feature document.
- Recorded: target card readability, static pseudo-glass and Android runtime/lifecycle observations remain `DEFERRED` and non-blocking with residual risk; no runtime `PASS` was claimed. `REQ-022`/`REQ-023` retain their existing implemented state, while primary-owner `REQ-024`/`REQ-025` remain planned.
- Deferred: scheduler promotion, dependent unblock/block reconciliation and scheduler checkpoint updates remain owner-owned post-sync actions.

## [2026-08-08] Wave 4 — FT-003 boundary sync
- Reconciled: `TASK-005-T3-FT-003-W4` remains `failed` historical evidence; `TASK-012-T3-FT-003-W4` is already `done` with functional `PASS` and required semantic `semantic-pass`.
- Recorded: TASK-012 repairs the supported full-day provider normalization path only; target-device evidence is `DEFERRED` and non-blocking with residual risk, and no runtime `PASS` is claimed.
- Preserved: FT-003/EP-002 and direct RTM lifecycle values remain unchanged; no feature closure or other lifecycle decision is inferred.
- Deferred: scheduler promotion, dependent unblock/block reconciliation, scheduler checkpoint and terminal-state updates remain scheduler-owned post-sync actions.

## [2026-08-08] Wave 5 — FT-003 boundary sync
- Reconciled: `TASK-005-T3-FT-003-W4` remains `failed` historical evidence, `TASK-012-T3-FT-003-W4` remains `done` for provider normalization, and `TASK-013-T3-FT-003-W5` is `done` for entry/fallback/shared-session lifecycle and consumer integration evidence.
- Recorded: W5 functional `PASS` and semantic `semantic-pass` evidence are linked from FT-003; target-device evidence remains `DEFERRED` and non-blocking with residual risk, and no runtime `PASS` is claimed.
- Preserved: FT-003/EP-002 and direct RTM lifecycle values remain unchanged; no feature closure or other lifecycle decision is inferred.
- Deferred: scheduler post-sync lint/strict-doctor, promotion, dependent unblock/block reconciliation, checkpoint and terminal-state updates remain scheduler-owned actions.

## [2026-08-08] Wave 5 — FT-004 boundary sync
- Reconciled: `TASK-006-T3-FT-004-W5` is already `done` with independent functional `PASS` and required semantic `semantic-pass` evidence; its direct predecessor is the completed `TASK-013-T3-FT-003-W5`.
- Recorded: FT-004 save/reload, entry, ten-card projection, completeness, presentation and shared-session evidence is linked from the feature; target-device evidence remains `DEFERRED` and non-blocking with residual risk, and no runtime `PASS` is claimed.
- Preserved: FT-004/EP-002 and direct RTM lifecycle values remain unchanged; no feature/epic closure, promotion or dependent block/unblock is inferred.
- Deferred: scheduler post-sync lint/strict-doctor, promotion, dependent-state reconciliation, checkpoint and terminal-state updates remain outside `/mb-sync`.

## [2026-08-08] Wave 6 — FT-005 boundary sync
- Reconciled: `TASK-005-T3-FT-003-W4` remains `failed` historical, `TASK-006-T3-FT-004-W5` remains `done`, and `TASK-007-T3-FT-005-W6` remains `done` with attempt-2 correction plus fresh attempt-3 functional `PASS` and semantic `semantic-pass` evidence.
- Recorded: the corrected last-valid editor restoration, 32/32 host checks and accepted preset persistence/label/color/projection boundary evidence are linked from FT-005; target-device evidence remains `DEFERRED` and non-blocking with residual risk, and no runtime `PASS` is claimed.
- Preserved: FT-005/EP-003 and direct RTM owner `REQ-011` lifecycle values remain `planned`; no feature/epic/requirement closure, promotion or dependent-state transition is inferred.
- Deferred: scheduler promotion, dependent unblock/block reconciliation, scheduler checkpoint and terminal-state updates remain scheduler-owned actions.

## [2026-08-08] Wave 7 — FT-006 boundary sync
- Reconciled: `TASK-008-T3-FT-006-W7` is already `done` with fresh functional `PASS` and required semantic `semantic-pass` evidence linked from the authoritative task record.
- Updated: FT-006 lifecycle and direct RTM owners `REQ-012`, `REQ-013`, `REQ-014` and `REQ-025` to `implemented`; FT-005 ownership of `REQ-011` remains `planned`.
- Recorded: immediate start, one-active-timer, protected cancellation, temporary rehydration and network-independent overdue-dismissal evidence is linked from FT-006; target-device evidence remains `DEFERRED` and non-blocking with residual risk, and no runtime `PASS` was claimed.
- Preserved: EP-003 remains `planned`; no epic closure, scheduler promotion, dependent unblock/block reconciliation, scheduler checkpoint or terminal-state update was performed.

## [2026-08-08] Wave 8 — FT-007 boundary sync
- Reconciled: `TASK-009-T3-FT-007-W8` is already `done` with retry-2 fresh functional `PASS` and required semantic `semantic-pass` evidence linked from the authoritative task record.
- Updated: direct RTM owners `REQ-015` and `REQ-016`, plus FT-007 lifecycle, to `implemented`; EP-003 remains `planned` because FT-005/REQ-011 is still planned.
- Recorded: FT-007 overdue presentation, dismissal, alert-policy and temporary-resume evidence is linked from the feature; target-device evidence remains `DEFERRED` and non-blocking with residual risk, and no runtime `PASS` was claimed.
- Preserved: task JSON status/dependencies, scheduler checkpoint, terminal state, `spec-index.md` registry-only rules and production code; no epic closure, promotion or dependent-state transition was performed.
- Deferred: scheduler-owned post-sync lint/strict-doctor and subsequent queue reconciliation remain outside this sync.

## [2026-08-08] Wave 9 — FT-008 boundary sync
- Reconciled: `TASK-010-T3-FT-008-W9` is already `done` with fresh functional `PASS` and required semantic `semantic-pass` evidence linked from the task and FT-008.
- Recorded: local key/location, offline catalog/aliases, attribution, coordinate-bearing refresh and inline failure-preservation evidence is linked; target-device Settings readability/navigation remains `DEFERRED` and non-blocking with residual risk, and no runtime `PASS` was claimed.
- Preserved: FT-008/EP-004 and direct RTM lifecycle values, task dependencies, scheduler checkpoint, terminal state and production code; no feature/epic closure, promotion or dependent-state transition was inferred.
- Preserved: `spec-index.md` as a registry-only surface; `.memory-bank/tasks/index.json` already indexes the authoritative W9 task record.
- Deferred: scheduler-owned post-sync lint/strict-doctor and next scheduler queue reconciliation remain outside this sync.

## [2026-08-08] Wave 10 — FT-009 boundary sync
- Reconciled: `TASK-011-T3-FT-009-W10` is already `done` with fresh functional `PASS` and semantic `semantic-pass` evidence linked from FT-009 and the task-owned verification artifacts.
- Recorded: alert/glass personalization, persistence, live preview, invalid-value preservation, consumer-boundary and redaction evidence is linked; target-only Settings readability/static pseudo-glass evidence remains `DEFERRED` and non-blocking with residual risk, and no runtime `PASS` was claimed.
- Updated: FT-009 feature routing, the feature index, the spec-backbone W10 reference and EP-004's indexed W10 outcome.
- Preserved: FT-009/EP-004 and direct RTM lifecycle values remain `planned`; task JSON status/dependencies, scheduler checkpoint, terminal state, `spec-index.md` registry-only rules and production code were not changed.
- Deferred: scheduler-owned post-sync lint/strict-doctor, promotion and dependent-state reconciliation remain outside this sync.

## [2026-08-08] Wave 11 — FT-001 failure boundary sync
- Reconciled: `TASK-014-T3-FT-001-W11` is already `failed` after the configured
  initial attempt plus two retries; final functional `PASS` and required
  semantic `semantic-fail` evidence are linked from the task-owned reports
  and protocol.
- Recorded: the independent semantic failure is a public non-city weather-card
  double tap that leaves an active countdown running, violating the existing
  FT-006-AC-003 / REQ-013 cancellation contract. The durable bug note and
  recovery route are linked from FT-001 and FT-006.
- Preserved: historical `TASK-003-T3-FT-001-W2` and W10
  `TASK-011-T3-FT-009-W10` terminal history; FT-001/FT-006 lifecycle values,
  direct RTM rows, task JSON/index links, Planning Revision `1`,
  `spec-index.md`, production code, scheduler checkpoint, terminal state,
  promotion/dependency decisions and retry budget.
- Deferred: normal indexed `/feature-to-tasks FT-001` repair planning and fresh
  `/exe`, `/verify` and `/red-verify` evidence are required; scheduler-owned
  post-sync lint/strict-doctor and the failure-budget terminal halt remain
  outside this sync.

## [2026-08-09] Wave 12 — FT-001 dispatch repair boundary sync
- Reconciled: `TASK-015-T3-FT-001-W12` was already marked `done` after executor
  `PASS_FOR_HANDOFF`, fresh functional `PASS` and independent semantic
  `semantic-pass`; the authoritative task record and W12 evidence remain the
  source of truth.
- Updated: FT-001, its implementation plan, EP-001 routing, the FT-001/epic
  indexes, spec-backbone handoff and the historical W11 defect note with the
  completed W12 repair and evidence links. FT-006/EP-003 now record the
  cross-feature regression repair without changing Timer & Alert ownership.
- Recorded: generic `Tecno_Pova_6_API_35` public evidence covers the required
  matrix and cleanup; Samsung GT-I9300I/custom-ROM/1280x720 physical evidence
  remains `DEFERRED` and no target runtime `PASS` is claimed.
- Preserved: FT-001/FT-006 lifecycles, direct RTM values (`REQ-004` remains
  `implemented`; `REQ-013` remains the existing FT-006 contract), task index,
  `spec-index.md`, canonical SDD/boundary contracts, TASK-003/TASK-014 history,
  production code, task lifecycle/status source, scheduler checkpoint,
  terminal state, retry counters, promotion and dependent-state decisions.
- Deferred: scheduler-owned post-sync lint/strict-doctor, promotion and
  dependent-state reconciliation remain outside this sync.

## [2026-08-10] Wave 13 — FT-001 ticker debt boundary sync
- Reconciled: the authoritative [TASK-016 task card](tasks/TASK-016-T3-FT-001-W13.task.json)
  is already `done` after executor `PASS_FOR_HANDOFF`, fresh functional `PASS`
  and independent durable semantic `semantic-pass`.
- Linked: [executor handoff](../.protocols/TASK-016-T3-FT-001-W13/handoff.md),
  [functional verification](../.protocols/TASK-016-T3-FT-001-W13/verification.md),
  [verifier-owned evidence](../.tasks/TASK-016-T3-FT-001-W13/verifier-owned-evidence.md),
  [durable semantic verification](../.protocols/TASK-016-T3-FT-001-W13/red-verification.md)
  and [semantic report](../.tasks/TASK-016-T3-FT-001-W13/TASK-016-T3-FT-001-W13-S-RED-VERIFY-final-report-docs-01.md)
  from the FT-001 feature and implementation-plan evidence surfaces.
- Updated: FT-001/EP-001 lifecycle/spec evidence and feature/epic navigation;
  direct RTM values `REQ-002`, `REQ-003` and `REQ-022` remain `implemented`,
  and `spec-index.md` remains a pure registry.
- Recorded: W13 is host/static proof only. Samsung/custom-ROM/1280x720
  physical evidence remains `DEFERRED`, with no target-device runtime `PASS`
  claim; Weather Context, Timer & Alert, Forecast and existing architecture
  contracts remain unchanged.
- Preserved: task statuses and task index, scheduler checkpoint, terminal
  state, retry counters, queue semantics, promotion/dependent-state decisions,
  production code and tests.
- Deferred: scheduler-owned post-sync lint/strict-doctor, promotion and
  dependent-state reconciliation remain outside this sync.

## [2026-08-10] Wave 14 — FT-001 Weather Context projection boundary sync
- Reconciled: the authoritative [TASK-017 task card](tasks/TASK-017-T3-FT-001-W14.task.json)
  is already `done` after executor `PASS_FOR_HANDOFF`, fresh functional `PASS`
  and independent durable semantic `semantic-pass`; claim-linked closure
  evidence is preserved in the indexed task record.
- Linked: W14 executor, functional, verifier-owned and semantic evidence from
  FT-001, the FT-001 implementation plan, and the affected FT-002 Weather
  Context feature/epic routing surfaces.
- Updated: FT-001/FT-002 evidence and navigation, EP-001/EP-002 routing, the
  FT-001 feature plan, the spec-backbone W14 record and task/feature/epic index
  surfaces; existing RTM values, feature/epic lifecycles and `spec-index.md`
  remain unchanged and registry-only.
- Recorded: W14 is host/static proof only. Samsung/custom-ROM/1280x720 physical
  evidence remains `DEFERRED`, with no target-device runtime `PASS` claim; no
  provider, public contract, Forecast, Timer/audio, gesture or target-device
  behavior changed.
- Preserved: production/tests, historical task records, task status semantics,
  dependencies, scheduler checkpoint, terminal state, retry/queue semantics,
  Planning Revision `1` and promotion/dependent-state decisions.
- Deferred: caller-owned post-sync lint/doctor, promotion and dependent-state
  reconciliation remain outside this `/mb-sync` boundary.

## [2026-08-10] Wave 15 — FT-002 production provider boundary sync
- Reconciled: the authoritative [TASK-018 task card](tasks/TASK-018-T3-FT-002-W15.task.json)
  is already `done` after executor `PASS_FOR_HANDOFF`, fresh functional `PASS`
  and independent semantic `semantic-pass`; the attempt-1 semantic failure is
  retained as historical evidence and attempt 2 is the current closure basis.
- Linked: W15 handoff, functional verification, verifier-owned evidence and
  semantic verification/evidence from the FT-002 feature and implementation
  plan, with EP-002 and feature/epic indexes routed to the same task record.
- Updated: W15 production Yandex request/mapping/failure/cache, synthetic
  redaction, fixture isolation, off-UI wiring and minimum-permission outcome;
  existing provider/public contracts, ownership graph, FT-002 lifecycle and
  EP-002 lifecycle remain unchanged. Direct RTM values remain unchanged;
  `REQ-024` remains `planned` under FT-008, and FT-003/FT-004/FT-008 checks are
  compatibility regressions only.
- Recorded: W15 is host/build/static/redacted proof only. Target Android/custom
  ROM/device readiness and live-provider/network compatibility remain
  `DEFERRED`; no target-device runtime `PASS` is claimed.
- Preserved: task identity/status `done`, all historical task records, task
  index, Planning Revision `1`, scheduler checkpoint, terminal state, retry and
  queue semantics, production code and tests.
- Deferred: scheduler-owned post-sync `mb-lint`, strict doctor, promotion and
  dependent-state reconciliation remain outside this sync.
