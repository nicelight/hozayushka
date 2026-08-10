---
description: Planning surface for FT-001 main clock and display shell.
status: active
last_updated: 2026-08-10
---
# FT-001 — Feature plan

## Objective

Reconcile FT-001 into the smallest executable outcome that turns the Foundation
display shell into the accepted always-visible main display: a device-time
clock/date surface with the accepted colon states, exactly four stable
lower-left weather cards, and city interaction routed through the existing
capability boundaries.

## Accepted basis

- Feature: [.memory-bank/features/FT-001-main-clock-display.md](../../.memory-bank/features/FT-001-main-clock-display.md)
- Epic: [.memory-bank/epics/EP-001-glanceable-display.md](../../.memory-bank/epics/EP-001-glanceable-display.md)
- Requirements: `REQ-001`, `REQ-002`, `REQ-003`, `REQ-004`, `REQ-022`, `REQ-023`
- Global Backbone: `complete`, Planning Revision `1`
- Foundation Gate: `TASK-002-T3-FT-000-W1`, status `done`
- Clarified PRD: `clarification_status: complete`

## Queue

| Order | Task | Tier | Wave | Status | Depends on | Owner |
|---|---|---|---|---|---|---|
| 1 | `TASK-003-T3-FT-001-W2` | T3 | W2 | done | `TASK-002-T3-FT-000-W1` | Main Display capability |
| 2 | `TASK-014-T3-FT-001-W11` | T3 | W11 | failed | `TASK-011-T3-FT-009-W10` | Main Display layout follow-up |
| 3 | `TASK-015-T3-FT-001-W12` | T3 | W12 | done | `TASK-011-T3-FT-009-W10` | Main Display active-countdown dispatch repair |
| 4 | `TASK-016-T3-FT-001-W13` | T3 | W13 | done | `TASK-015-T3-FT-001-W12` | Main Display ticker debt |
| 5 | `TASK-017-T3-FT-001-W14` | T3 | W14 | done | `TASK-016-T3-FT-001-W13` | Weather Context projection/decode debt |

TASK-003 remains the authoritative completed historical implementation record.
TASK-014 remains the separate post-terminal follow-up for the generic-emulator
layout/reachability delta and is preserved as `failed` after its final semantic
regression. TASK-015 is a new indexed repair because W11 exposed a different
public active-countdown dispatch outcome: a non-city weather-card double tap did
not cancel. It depends on the last successful W10 baseline, not on failed W11;
W11 remains negative evidence and is not reopened or rewritten.

TASK-015 is one cohesive Main Display-owned repair. It uses a shared internal
active-countdown dispatcher that captures the public stream at `ACTION_DOWN`
and preserves terminal delivery across timer state changes, while retaining
city hold-to-Settings and all existing preset/overdue paths. It adds no module,
edge, public contract or downstream timer ownership and is not split by
surface or test type.

The current indexed W12 record is already `done`; this planning reconciliation
does not transition it or rewrite its evidence. `TASK-016-T3-FT-001-W13` was one
planned T3 task for confirmed local Main Display implementation debt and is now
terminal `done`; its record and evidence remain unchanged. It
consolidates the existing 20 Hz ticker's scheduling owner, forwards existing
Activity pause/resume signals to lifecycle-gate that owner, and retains the
last rendered weather-card projection so unchanged scalar ticks do not rebuild
the card view tree. The current scalar clock/date/colon cadence remains; the
Weather Context cache/projection contract, Timer & Alert ownership and
Forecast-wide behavior are not changed. W13 uses host-only proof and does not
plan target-device evidence.

The indexed `TASK-017-T3-FT-001-W14` is `done` after executor
`PASS_FOR_HANDOFF`, fresh functional `PASS` and independent durable semantic
`semantic-pass`. Based only on `TD-W13-001`, it keeps the W13 scalar 20 Hz
clock/date/colon refresh intact and memoizes the existing display-ready
`WeatherProjection` inside Weather Context, invalidating it only after an
accepted successful Weather refresh, an observed validated location change or
an existing selected-city/date/day-night/pressure-trend/24-hour freshness
boundary. Weather Context retains cache, history and projection semantics; the
existing Main Display → Weather Context read edge and public read model remain
unchanged. W14 is host-proof-only and does not claim target-device evidence.

## Canonical SDD coverage

All applicable concerns reuse existing subject-based canonical specs; no new
specification or behavior example is required.

| Concern | Action | Canonical basis | Planning reason |
|---|---|---|---|
| Deployable architecture and capability ownership | `reuse` | [System Architecture](../../.memory-bank/architecture/system-architecture.md#architecture-spine), `AD-001`, `AD-003`, `AD-005` | Main Display remains the orchestration owner; the composition root only wires the runtime. |
| Accepted module inventory and graph | `reuse` | [Boundary Map](../../.memory-bank/contracts/boundary-map.md#modules), [Dependency Graph](../../.memory-bank/contracts/boundary-map.md#dependency-graph) | All task-relevant units and edges already have registered identities. |
| Main Display consumer contracts | `reuse` | [Capability Interfaces](../../.memory-bank/contracts/capability-interfaces.md#main-display-to-weather-context), [Timer and Alert](../../.memory-bank/contracts/capability-interfaces.md#main-display-to-timer-and-alert), [Forecast Sessions](../../.memory-bank/contracts/capability-interfaces.md#main-display-to-forecast-sessions), [Settings and Location](../../.memory-bank/contracts/capability-interfaces.md#main-display-to-settings-and-location) | The shell renders projections and delegates gestures; it does not own neighbor state. |
| Device time, window flags and network-sensitive colon mode | `reuse` | [Platform Runtime](../../.memory-bank/contracts/platform-runtime.md#display-runtime-boundary) | The accepted Android boundary already defines device-time and fullscreen policy. |
| Four-card shell and ownership split | `reuse` | [FT-001 AC-002](../../.memory-bank/features/FT-001-main-clock-display.md#FT-001-AC-002), [Main Display → Weather Context](../../.memory-bank/contracts/capability-interfaces.md#main-display-to-weather-context) | FT-001 owns exactly four lower-left positions and composition; FT-002 retains weather data/content/freshness/palette ownership. |
| Main Display ticker ownership and lifecycle gating | `reuse` | [Platform Runtime](../../.memory-bank/contracts/platform-runtime.md#display-runtime-boundary), [System Architecture AD-003](../../.memory-bank/architecture/system-architecture.md#AD-003), [Boundary Map](../../.memory-bank/contracts/boundary-map.md#dependency-graph) | Activity lifecycle forwarding and one local scheduler owner fit the accepted runtime/wiring boundary; no new contract, edge, dependency or module is needed. |
| Local weather-card render reuse | `reuse` | [FT-001 AC-002](../../.memory-bank/features/FT-001-main-clock-display.md#FT-001-AC-002), [Main Display → Weather Context](../../.memory-bank/contracts/capability-interfaces.md#main-display-to-weather-context) | Main Display may retain the last rendered read-model presentation; Weather Context keeps cache, refresh and projection ownership. W13 does not cross into Weather Context implementation. |
| Weather Context display-ready projection snapshot and invalidation | `reuse` | [Local Data — FT-002 Weather Context Records](../../.memory-bank/domains/local-data.md#ft-002-weather-context-records), [Weather Provider — Refresh, Cache and Failure Rules](../../.memory-bank/contracts/weather-provider.md#refresh-cache-and-failure-rules), [Lifecycle Map — Weather Freshness Contract](../../.memory-bank/states/lifecycle-map.md#weather-freshness-contract), [Main Display → Weather Context](../../.memory-bank/contracts/capability-interfaces.md#main-display-to-weather-context) | TD-W13-001 is satisfied inside the existing Weather Context owner; no new state boundary, public contract, edge, event or dependency is needed. |
| Minimal Settings destination/return seam | `reuse` | [Main Display → Settings & Location](../../.memory-bank/contracts/capability-interfaces.md#main-display-to-settings-and-location) | Main Display owns gesture intent; Settings & Location owns the destination surface and state. No new edge or public contract is added; if the accepted boundary is insufficient, route to `/spec-design`. |
| Proof route for W13 | `reuse` | [Testing Strategy](../../.memory-bank/testing/strategy.md#risk-based-checks), [Runtime Verification](../../.memory-bank/testing/runtime-verification.md#deterministic-host-side-checks) | Host scheduler/projection probes, build and static gates prove the bounded debt; W13 does not plan target-device evidence. Historical W2/W11/W12 device evidence remains unchanged. |
| Proof route for W14 | `reuse` | [Testing Strategy](../../.memory-bank/testing/strategy.md#risk-based-checks), [Runtime Verification](../../.memory-bank/testing/runtime-verification.md#deterministic-host-side-checks), [Tier Policy](../../.memory-bank/workflows/tier-policy.md#claim-linked-red--green-for-t2t3) | Counting in-memory cache/projection fixtures prove reuse and accepted invalidation; clean build, host unit suite and static diff are sufficient. No emulator or target-device evidence is in scope. |
| Supplementary generic-emulator layout evidence | `reuse` | [Supplementary Local Emulator Target](../../.memory-bank/testing/runtime-verification.md#supplementary-local-emulator-target), [Reviewer evidence](../RUNTIME-VERIFICATION/tecno-pova-6-api35-review.md) | The accepted emulator route can prove actual Android View bounds and Settings reachability without promoting a Samsung/custom-ROM claim. |

Feature frontmatter remains `spec_design_status: complete` with its existing
canonical links. No `needed_before_tasks` Backbone row remains.

## Scope boundary

In scope: the Main Display shell, `HH:mm` and Russian date formatting from the
device timezone, accepted online/offline/countdown colon state projection,
exactly four lower-left weather-card positions, stable preset zones, and city
short/long-hold routing through the minimal accepted Settings destination and
return seam. FT-002 owns weather data/content/freshness/palette.

Out of scope: weather provider mapping, weather history, card data and content.
W14's internal snapshot invalidation preserves, but does not re-own, existing
Weather Context freshness and projection semantics. Other out-of-scope items
remain:
hourly or long-term forecast sessions, preset configuration, countdown lifecycle
and overdue alert, offline location catalog, API-key settings, validation,
personalization preview and any new UI controls. The minimal accepted Settings
destination/return seam is the only Settings surface in FT-001; the remaining
Settings behavior remains downstream FT-008/FT-009 concern.

The W11 follow-up narrows this scope to FT-001-AC-002 and FT-001-AC-005:
non-zero city/transient-row allocation and the existing city-to-Settings route
on the documented generic emulator. Existing timer-hint and forecast-message
semantics remain owned by their original features; their populated bounds are
only regression guards for the shared layout. Foundation probe controls are a
conditional same-correction gate, not a new FT-000 outcome.

The W12 repair owns only the bounded `FT-001-AC-005`
city-hold/Settings-preservation acceptance delta. It exercises the existing
downstream protected-cancellation contract (REQ-013; regression guard only;
canonical basis in TASK-015 `normative_inputs`) through the non-city
weather-card single/double path to prevent cross-feature regression; this is not
a W12 acceptance locator, a new FT-006 task, or an FT-006 lifecycle change. It
must prove that path through actual generic Android event dispatch while
preserving city double/no-delayed-Settings, empty/selected city taps, preset
interactions, overdue dismissal and safe cleanup. This does not reopen or
rewrite TASK-014's failed historical layout/reachability record. Host stream
tests support the dispatcher but never stand in for Android touch reachability.
Samsung/custom-ROM/1280x720 remains deferred.

The W13 debt task owns only the local Main Display ticker outcome: one
idempotent scheduler owner, pause/resume and attach/detach gating through the
existing Activity/platform seam, and no repeated weather-card view-tree
rebuild when the existing read-model presentation is unchanged. Its task-owned
feature deltas are `FT-001-AC-002`, `FT-001-AC-003` and `FT-001-AC-004` with
`REQ-002`, `REQ-003` and `REQ-022`; the colon/countdown path is a regression
guard and Timer & Alert remains the owner. No Yandex, Weather Context cache
cadence, timer/audio ownership, gesture semantics, Forecast-wide optimization,
target-device evidence or new architecture/contract is included.

The W14 follow-up owns only the TD-W13-001 upstream cost: repeated scalar reads
must reuse the existing capability-owned display-ready Weather Context snapshot,
while accepted successful refresh/location and existing projection/freshness
boundaries rebuild it as needed. Its exact task-owned feature locator is
`FT-001-AC-002 / REQ-002`; `REQ-007`, `REQ-022` and `REQ-025` are governing
weather/time/failure constraints and regression guards. The hard write boundary
is `WeatherCapability.kt` plus the existing `WeatherContextTest.kt`. W14 does
not change W13's ticker, public read contract, Weather Context ownership,
provider, Forecast, Timer/audio, gestures, target-device evidence, scheduler
state or Planning Revision.

## W14 boundary reconciliation

`TASK-017-T3-FT-001-W14` is `done` and depends on terminal
`TASK-016-T3-FT-001-W13`. The completed boundary is independently proven in the
existing Weather Context host test: repeated projection calls reuse one
snapshot, while accepted successful refresh, validated location, selected-city
time, pressure and 24-hour freshness boundaries rebuild it. The current
four-card, day/night, date, pressure-trend, failure and stale-empty semantics
remain regression results; no feature AC or RTM lifecycle is changed. See the
[executor handoff](../TASK-017-T3-FT-001-W14/handoff.md),
[functional verification](../TASK-017-T3-FT-001-W14/verification.md),
[verifier-owned evidence](../../.tasks/TASK-017-T3-FT-001-W14/verifier-owned-evidence.md),
[durable semantic verification](../TASK-017-T3-FT-001-W14/red-verification.md)
and [semantic report](../../.tasks/TASK-017-T3-FT-001-W14/TASK-017-T3-FT-001-W14-S-RED-VERIFY-final-report-docs-01.md).

Historical W2 `done`, W11 `failed`, W12 `done` and W13 `done` records remain
unchanged. FT-001/EP-001 and FT-002/EP-002 lifecycle/RTM values, scheduler
checkpoint, terminal state and Planning Revision `1` remain unchanged; target
device evidence remains `DEFERRED` with no runtime `PASS` claim.

## Handoff

The successful single-feature route is `/review-tasks-plan FT-001`. This
planning run does not execute the task, run review, run `/mb-doctor`, or run
`/mb-sync`.
