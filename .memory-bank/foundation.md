---
description: Foundation Dev Path evidence and feature pressure map.
status: active
last_updated: 2026-08-04
---
# Foundation Dev Path

## Gate Anchors

- Foundation Required: true
- Foundation Requirement: REQ-000
- Foundation Pseudo-Feature: FT-000
- Foundation Gate Task: TASK-002-T3-FT-000-W1

`REQ-000` and `FT-000` are workflow pseudo-identifiers only. This gate does
not introduce product behavior. The foundation queue and its execution
evidence use the normal requirement, feature, task, protocol and plan paths.

## Minimal Work Path

- Build command: To be established by FT-000 with the project-native Android build scaffold.
- Start command: To be established by FT-000 for the target Android 11 device/emulator path.
- Primary entrypoint: One Android application composition root under the accepted single-deployable architecture.
- Smoke path: Clean launch → stable fullscreen main display → local settings seed → start/cancel one timer → re-open after a temporary interruption → exercise a redacted weather fixture without a live key.
- Test command: To be established by FT-000 for deterministic host-side and integration probes.
- Evidence: The current workspace has no executable Android baseline; FT-000 must produce reproducible build/start/test commands, a walking skeleton, local persistence baseline, provider fixture path and target-device probe route.

## Feature Pressure Map

| Feature | Pressure | Foundation Response | Probe | Status |
|---|---|---|---|---|
| FT-001 | Android entry, fullscreen, clock shell and device runtime | Establish one composition root and target-display shell | Launch/readability/fullscreen/keep-screen-on probe | pending_foundation |
| FT-002 | Provider mapping, cache/freshness, history and deterministic weather visuals | Establish local-data owner path and redacted weather fixture | Fresh/stale/missing-field/palette/trend probe | pending_foundation |
| FT-003 | Hourly fields, city timezone and shared forecast session | Establish normalized forecast fixture and session timing path | Eight-slot/timezone/completeness probe | pending_foundation |
| FT-004 | Ten-day horizon, date boundaries and shared forecast exit | Reuse forecast contract and deterministic daily fixture | Ten-card/order/timezone/exit probe | pending_foundation |
| FT-005 | Validated preset values and persistent timer preferences | Establish settings persistence and timer preference read path | Defaults/ranges/labels/last-valid-value probe | pending_foundation |
| FT-006 | Timer persistence and temporary process-stop recovery | Establish active-timer durable data and lifecycle adapter seam | Countdown/overdue/recovery/cancel probe | pending_foundation |
| FT-007 | Overdue visual state, audio policy and target-ROM behavior | Establish platform audio adapter and manual device probe route | Visual dismissal/silent-DND/ramp/cap probe | pending_foundation |
| FT-008 | Local API key, offline GeoNames data and provider access | Establish key-safe local storage, catalog fixture and redacted provider path | Artifact absence/offline search/provider failure probe | pending_foundation |
| FT-009 | Auto-save validation and live pseudo-glass preview | Establish settings/UI test seam and preview fixture path | Valid/invalid persistence and preview-state probe | pending_foundation |

## Deferred Decisions

| Decision | Why deferred | Trigger to revisit |
|---|---|---|
| Exact Gradle/package/UI toolkit setup | No executable Android baseline exists; it does not alter the accepted target shape. | FT-000 scaffold creation. |
| Project-native persistence primitive | The global contract fixes ownership and behavior, not a library choice. | FT-000 storage probe; operator checkpoint if a new dependency is required. |
| Exact provider field serialization | Product semantics are accepted; feature-level mapping needs a runnable fixture. | FT-002–FT-004 feature design. |
| Target custom-ROM lifecycle/audio behavior | Current runtime cannot be observed before the app exists. | FT-000 device probe and later verification. |

## Foundation Exit Criteria

- minimal path passes
- compatibility probes pass
- no P0/P1 design pressure unresolved
- feature dev path allowed

## Queue Handoff

- Queue created: `TASK-001-T3-FT-000-W0` -> `TASK-002-T3-FT-000-W1`.
- Next gate: run `/mb-doctor --strict` for the indexed FT-000 queue.
- Product task design remains blocked until `TASK-002-T3-FT-000-W1` is done.
