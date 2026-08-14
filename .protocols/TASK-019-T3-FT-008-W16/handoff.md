---
description: Current executor handoff for TASK-019-T3-FT-008-W16.
status: active
---
# Handoff — TASK-019-T3-FT-008-W16

## Summary
- Attempt 3 denies the provider-unidentified generic key callback and removes selection-only attribution from untagged legacy errors while preserving stored OpenWeather key state, local missing/invalid messages and accepted Settings UI/persistence.
- Focused launch/location probes prove no key handoff or injected legacy-provider invocation after OpenWeather selection/key save; previous attempts remain supporting-only.
- Executor route: `PASS_FOR_HANDOFF`; authoritative status remains `in_progress` for independent verification.

## Where to look
- key files: `SettingsCapability.kt`, `SettingsLocationTest.kt`, Attempt 3 section of `red-green-evidence.md`, `progress.md` and executor report `code-03`
- Attempt 3 application files: exactly `SettingsCapability.kt` and `SettingsLocationTest.kt`; existing accepted `strings.xml` diff was not changed
- hard retry boundary compliance: Weather Context, `FoundationRuntime`, adapters, outbound requests, cache/history, forecast, dependencies and all other source are untouched

## How to run / verify
- gates: focused RED `10/8 failed`, then GREEN `10/10`; clean assemble; full host suite `69/69`; task security/static scan; Memory Bank/diff integrity
- claim-linked RED/GREEN evidence: `.tasks/TASK-019-T3-FT-008-W16/red-green-evidence.md#attempt-3--final-bounded-provider-isolation-correction`
- current-attempt reuse candidate locators: none
- superseded/supporting-only locators: Attempt 1 and Attempt 2 sections/reports in the same task evidence directory

## Known issues
- No blocker. The transition deny suppresses current legacy refresh after OpenWeather key save; TASK-020 must atomically replace it with selected-OpenWeather-authorized transport. Secret/static scans are clean. Device/live-provider runtime evidence is deferred and unclaimed.

## Follow-ups
- Exact next owner: fresh `/verify TASK-019-T3-FT-008-W16` against Execution Attempt 3; fresh T3 `/red-verify` follows only after functional PASS under the scheduler/lifecycle owner.
