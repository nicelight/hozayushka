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

- Build command: `./gradlew assembleDebug` (established by
  `TASK-001-T3-FT-000-W0`; final clean-build evidence belongs to the Foundation
  Gate).
- Host baseline command: `./gradlew clean assembleDebug testDebugUnitTest`.
  The installed-app route remains documented for later readiness validation;
  it is not executed by the Foundation Gate while the application is still a
  walking skeleton.
- Primary entrypoint: One Android application composition root under the accepted single-deployable architecture.
- Host smoke path: known/resettable local fixture → redacted weather fixture →
  deterministic timer arithmetic/persistence probe → host test result and
  cleanup. Emulator, ADB and physical-device smoke are deferred until the
  application is ready for runtime validation.
- Test command: `./gradlew testDebugUnitTest`, with the deterministic
  `com.hozayushka.app.FoundationProbesTest` owner-local reset/reload and
  redacted provider fixture probes.
- Evidence: Before TASK-001 the workspace had no executable Android baseline;
  TASK-001 establishes the preliminary build/start/test commands, walking
  skeleton, supported Foundation probe mode, owner-local persistence baseline
  and provider fixture path. The final Foundation Gate proves the host baseline;
  target-device compatibility is a later readiness gate.

## TASK-001 preliminary baseline

`TASK-001-T3-FT-000-W0` establishes the executable project shape and explicit
Foundation probe mode before the final Foundation Gate. The runtime uses one
`app` module, one composition root under `com.hozayushka.app.app`, the accepted
capability/adapters roots, and private owner-local `SharedPreferences` stores.
The probe mode routes Settings, Weather, Timer and platform audio/lifecycle
operations through their owning boundaries; host probes still use isolated
in-memory owner stores and a generated in-memory credential whose durable
provider result is `[REDACTED]`.

The install/start commands are a later runtime-validation route, not a
Foundation Gate prerequisite. Fullscreen, keep-screen-on, temporary
interruption/lifecycle and audio observations remain deferred until the
application is ready; no emulator or physical device should be started for
FT-000 host verification.

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
| Exact Gradle/package/UI toolkit setup | TASK-001 uses the cached Android Gradle/Kotlin plugins, provisional `com.hozayushka.app` namespace and platform `Activity`/`View` APIs; this does not add a runtime library or alter the target shape. | Final packaging review / Foundation Gate. |
| Project-native persistence primitive | TASK-001 establishes owner-local Android `SharedPreferences` stores plus isolated in-memory host-probe stores; no shared storage owner is introduced. | Foundation Gate and later feature schema work; operator checkpoint if a new dependency or security posture is required. |
| Exact provider field serialization | Product semantics are accepted; feature-level mapping needs a runnable fixture. | FT-002–FT-004 feature design. |
| Target custom-ROM lifecycle/audio behavior | Runtime cannot be meaningfully evaluated while only the walking skeleton is under construction. | Later readiness/release validation after the application is ready; not FT-000 execution. |

## Foundation Exit Criteria

- minimal path passes
- host minimal path passes; target-device compatibility remains explicitly deferred
- no P0/P1 design pressure unresolved
- feature dev path allowed

## Queue Handoff

- Queue created: `TASK-001-T3-FT-000-W0` -> `TASK-002-T3-FT-000-W1`.
- Next gate: run `/mb-doctor --strict` for the indexed FT-000 queue.
- Product task design may proceed after the host-only Foundation Gate is done;
  target-device compatibility remains a later readiness/release gate.
