---
description: Execution plan for TASK-031-T3-FT-007-W28.
status: active
---
# Plan — TASK-031-T3-FT-007-W28

## Goal / non-goals
- Goal: complete `FT-007-AC-006 / REQ-015 / REQ-023` in the two-file Main Display boundary.
- Non-goals: timer arithmetic/state/rehydration, any-tap command semantics, audio policy or runtime adapter, resources, settings/weather, lifecycle orchestration, task/status/spec/checkpoint changes, runtime launches.

## Preflight
- Exact indexed card resolves and is `T3`, W28, `in_progress`; sole dependency W27 is `done`.
- Global Backbone is `complete`, Planning Revision `2`; latest FT-007 W28 plan review is `APPROVE` for Revision `2`.
- Existing W27 geometry is the comparison basis: same-size idle `188.75`, active countdown `228.0`, transparent preset-colored circular treatment.
- W8/W23/W27 evidence is historical/read-only; target/device/audio remain `DEFERRED`.
- No fixed ratio/dp/gradient stop is selected. If existing geometry cannot satisfy the overdue hierarchy without clipping/overlap, stop and route to `/feature-doctor FT-007`.

## Execution sequence
1. Capture claim-specific RED before the first production behavior write.
2. Implement overdue-only geometry/presentation in `DisplayCapability.kt` and focused claim probes in `DisplayProjectionTest.kt`.
3. Run focused, full host, clean build, Memory Bank/diff gates; inspect read-only lifecycle/audio owners and exact diff boundary.
4. Record named visual rubric, regression/deferred evidence and compact handoff for `/verify` then T3 `/red-verify`.

## Claims
- Owned claim: same-size overdue surface excludes weather/city/date/card shell; full elapsed digits stable and larger than idle/active where geometry permits; transparent preset-colored circle; blinking `+`; no clipping/overlap.
- Read-only alternatives: existing any-tap/lifecycle/audio owners remain unchanged; W23 audio proof is not adopted.
- T3 isolation: synthetic host-only inputs, no device/network/audio runtime, no persistent external state.
