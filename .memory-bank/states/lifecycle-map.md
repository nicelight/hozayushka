---
description: Canonical V1 lifecycle states for timer, weather freshness and forecast sessions.
status: active
last_updated: 2026-08-04
source_of_truth: .memory-bank/prd.md, .memory-bank/invariants.md, operator confirmation 2026-08-04
---
# Lifecycle Map

## Scope

This is the canonical product-visible lifecycle contract. It records accepted
states and transitions without prescribing the Android class or persistence
library that implements them.

## Timer Lifecycle

| From | Trigger | To | Product rule |
|---|---|---|---|
| `idle` | Short tap on a valid preset | `countdown` | Start one selected preset without an intermediate screen. |
| `countdown` | Effective elapsed time reaches configured duration | `overdue` | Keep the visual overdue state active and start the permitted alert behavior. |
| `countdown` | Double tap anywhere | `idle` | Cancel the timer; a single tap only exposes the double-tap hint. |
| `overdue` | Any tap | `idle` | Dismiss the overdue state and stop the alert when it is playing. |
| `countdown` or `overdue` | Temporary process/lifecycle interruption, then resume | Same state recalculated from persisted timer data | Recovery is part of the timer concern; reboot recovery is explicitly out of scope. |

### Timer State Contract

- `idle` has no active timer.
- `countdown` has one selected preset, a positive configured duration and a
  persisted start point from which remaining time is recalculated.
- `overdue` retains the same timer identity and displays full elapsed time from
  the original start point; the `+` may blink, but the numeric counter does not.
- A single tap during `countdown` is not a cancel transition. A double tap
  cancels; any tap in `overdue` dismisses the product state.
- Temporary Activity/foreground/screen-off/process interruption rehydrates the
  state. Reboot does not create a new recovery path.
- Timer & Alert owns every transition and write; Main Display only submits
  accepted gestures and renders the state.

## Weather Data Lifecycle

| From | Trigger | To | Product rule |
|---|---|---|---|
| No usable cache | Successful fetch | `fresh` | Populate current/future cards and begin local history from installation onward. |
| `fresh` | No successful update for more than 24 hours | `stale_empty` | Keep card positions/dates but remove weather values and illustrations. |
| `fresh` or `stale_empty` | Successful fetch | `fresh` | Replace the visible cache and preserve the offline freshness rule. |
| Any cache state | App restart/offline period | Same freshness-derived state | Clock and timers remain usable independently of network availability. |

### Weather Freshness Contract

- The freshness decision is derived from the last successful normalized update,
  not from a failed request or current network availability alone.
- `fresh` remains visible offline through 24 hours; `stale_empty` keeps the four
  card positions/dates but removes weather values, illustrations and arrows.
- Weather Context owns cache/history writes and the transition to the
  freshness-derived projection.

## Forecast Screen Session

| Entry condition | Session behavior | Exit |
|---|---|---|
| Required hourly data exists | Open eight-slot hourly view; use the shared forecast exit flow | Auto-close after 3 seconds, double tap, or release after hold. |
| Required 10-day data exists | Open the shared ten-card long-term view; use the shared forecast exit flow | Auto-close after 3 seconds, double tap, or release after hold. |
| Required data is absent | Stay on the main display | Show the accepted short availability message; no forecast session is created. |

### Shared Forecast Session Contract

- A valid session is either hourly (exactly eight accepted slots) or long-term
  (exactly ten ordered days). Partial data does not create a session.
- Both session types begin with a three-second auto-close timer. Single tap
  cancels auto-close and shows the accepted hint; double tap closes; hold keeps
  the session open and release closes it.
- Forecast Sessions owns transient session state and timing. Weather Context
  owns normalized data and selected-city timezone.

## Cross-Slice Implications

- Timer persistence/recovery and alert dismissal form one lifecycle concern.
- Weather fetching, freshness, local history and forecast availability are
  separate data concerns that feed the main display and forecast views.
- Settings/location changes are a capability boundary that can request weather
  refresh without changing the timer lifecycle.
- Exact storage schema, platform mechanism and serialization remain
  Foundation/feature implementation details bounded by this contract.

## Sources

- [.memory-bank/prd.md](../prd.md): Functional Requirements, Data / Domain Model,
  UX / Interaction Flow and Edge Cases / Failure Handling.
- [.memory-bank/invariants.md](../invariants.md): cross-cutting MUST/NEVER rules.
- [.memory-bank/contracts/capability-interfaces.md](../contracts/capability-interfaces.md): public slice contracts.
- [.memory-bank/contracts/platform-runtime.md](../contracts/platform-runtime.md): Android lifecycle/time/audio boundary.
