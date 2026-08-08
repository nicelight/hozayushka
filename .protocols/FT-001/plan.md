---
description: Planning surface for FT-001 main clock and display shell.
status: active
last_updated: 2026-08-08
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
| 3 | `TASK-015-T3-FT-001-W12` | T3 | W12 | planned | `TASK-011-T3-FT-009-W10` | Main Display active-countdown dispatch repair |

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
| Minimal Settings destination/return seam | `reuse` | [Main Display → Settings & Location](../../.memory-bank/contracts/capability-interfaces.md#main-display-to-settings-and-location) | Main Display owns gesture intent; Settings & Location owns the destination surface and state. No new edge or public contract is added; if the accepted boundary is insufficient, route to `/spec-design`. |
| Proof and target-device evidence | `reuse` | [Runtime Verification](../../.memory-bank/testing/runtime-verification.md#deterministic-host-side-checks), [Target-Device Evidence](../../.memory-bank/testing/runtime-verification.md#target-device-evidence) | Host checks prove deterministic logic; device evidence proves the visual/runtime outcomes that host checks cannot establish. |
| Supplementary generic-emulator layout evidence | `reuse` | [Supplementary Local Emulator Target](../../.memory-bank/testing/runtime-verification.md#supplementary-local-emulator-target), [Reviewer evidence](../RUNTIME-VERIFICATION/tecno-pova-6-api35-review.md) | The accepted emulator route can prove actual Android View bounds and Settings reachability without promoting a Samsung/custom-ROM claim. |

Feature frontmatter remains `spec_design_status: complete` with its existing
canonical links. No `needed_before_tasks` Backbone row remains.

## Scope boundary

In scope: the Main Display shell, `HH:mm` and Russian date formatting from the
device timezone, accepted online/offline/countdown colon state projection,
exactly four lower-left weather-card positions, stable preset zones, and city
short/long-hold routing through the minimal accepted Settings destination and
return seam. FT-002 owns weather data/content/freshness/palette.

Out of scope: weather provider mapping/freshness/history/card data and content,
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

## Handoff

The successful single-feature route is `/review-tasks-plan FT-001`. This
planning run does not execute the task, run review, run `/mb-doctor`, or run
`/mb-sync`.
