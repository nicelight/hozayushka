---
description: Preliminary lifecycle hints that affect product decomposition; detailed state contracts remain downstream.
status: draft
last_updated: 2026-08-03
---
# Lifecycle Map

## Scope

This is a decomposition map, not a detailed state machine. It records only
product-visible lifecycles and transitions already accepted in the clarified
PRD.

## Timer Lifecycle

| From | Trigger | To | Decomposition implication |
|---|---|---|---|
| `idle` | Short tap on a preset | `countdown` | Start one selected preset without an intermediate screen. |
| `countdown` | Effective elapsed time reaches configured duration | `overdue` | Keep the visual overdue state active and start the permitted alert behavior. |
| `countdown` | Double tap anywhere | `idle` | Cancel the timer; a single tap only exposes the double-tap hint. |
| `overdue` | Any tap | `idle` | Dismiss the overdue state and stop the alert when it is playing. |
| `countdown` or `overdue` | Temporary process/lifecycle interruption, then resume | Same product state recalculated from persisted timer data | Recovery is part of the timer concern; reboot recovery is explicitly out of scope. |

## Weather Data Lifecycle

| From | Trigger | To | Decomposition implication |
|---|---|---|---|
| No usable cache | Successful fetch | `fresh` | Populate current/future cards and begin local history from installation onward. |
| `fresh` | No successful update for more than 24 hours | `stale_empty` | Keep card positions/dates but remove weather values and illustrations. |
| `fresh` or `stale_empty` | Successful fetch | `fresh` | Replace the visible cache and preserve the offline freshness rule. |
| Any cache state | App restart/offline period | Same freshness-derived state | Clock and timers remain usable independently of network availability. |

## Forecast Screen Session

| Entry condition | Session behavior | Exit |
|---|---|---|
| Required hourly data exists | Open eight-slot hourly view; use the shared forecast exit flow | Auto-close after 3 seconds, double tap, or release after hold. |
| Required 10-day data exists | Open the shared ten-card long-term view; use the shared forecast exit flow | Auto-close after 3 seconds, double tap, or release after hold. |
| Required data is absent | Stay on the main display | Show the accepted short availability message; no forecast session is created. |

## Decomposition Implications

- Timer persistence/recovery and alert dismissal form one lifecycle concern.
- Weather fetching, freshness, local history and forecast availability are
  separate data concerns that feed the main display and forecast views.
- Settings/location changes are a boundary event that can invalidate or refresh
  weather data without changing the timer lifecycle.
- Exact storage schema, platform mechanism and detailed gesture state belong to
  `/spec-design` and later subject specs.

## Sources

- [.memory-bank/prd.md](../prd.md): Functional Requirements, Data / Domain Model,
  UX / Interaction Flow and Edge Cases / Failure Handling.
- [.memory-bank/invariants.md](../invariants.md): cross-cutting MUST/NEVER rules.
