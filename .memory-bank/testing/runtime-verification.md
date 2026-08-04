---
description: Concrete foundation, integration and target-device evidence routes for V1 runtime risks.
status: active
last_updated: 2026-08-04
source_of_truth: .memory-bank/prd.md, .memory-bank/constitution.md, .memory-bank/testing/strategy.md
---
# Runtime Verification

## Purpose

This subject spec routes concrete proof for the accepted runtime, state,
provider and secret risks. It supplements the read-only project testing policy
in [Testing Strategy](strategy.md); it does not create a universal test-level
gate or replace feature acceptance criteria.

## Foundation Minimal Proof

Foundation must establish a reproducible path with:

- a clean build and launch command for the single Android application;
- a known initial local-data state and a safe reset or isolated fixture path;
- a visible main-display smoke result;
- a deterministic timer arithmetic/persistence probe;
- a redacted provider fixture path that does not require a live API key; and
- a target-device probe path for fullscreen, keep-screen-on and lifecycle/audio
  behavior where host-side checks cannot prove the result.

The exact project-native commands are intentionally absent until the
Foundation Gate creates the executable baseline. Their absence is a foundation
work item, not a product target blocker after the architecture decision.

## Deterministic Host-Side Checks

Use the cheapest check that proves the requirement:

- timer state transitions, elapsed/remaining arithmetic, one-active-timer rule,
  labels and accepted gesture semantics;
- weather freshness, seven-day history window, pressure thresholds, unknown
  condition fallback and all 78 temperature colors with endpoint clamp;
- device-time versus selected-city-timezone formatting;
- forecast completeness, eight-slot/ten-day ordering and missing-data gating;
- Settings validation, auto-save and preservation of the last valid value; and
- offline country-first/city-scoped search and alias matching.

## Redacted Integration Fixtures

Provider fixtures cover successful current/daily/hourly data, stale cache,
provider/network failure, missing optional fields and incomplete required
forecast fields. Use synthetic credentials only. Evidence must show the
result/verdict, never a key or an unredacted request.

Persistence/recovery probes define before execution:

1. known initial state;
2. safe rerun/reset or isolation;
3. observable expected state; and
4. cleanup that cannot leak a secret or affect another run.

## Target-Device Evidence

Manual device evidence is required for the outcomes that host-side checks do
not reliably establish:

- 1280×720 landscape fullscreen, hidden system panels, keep-screen-on and
  clock readability;
- temporary Activity/foreground/screen-off/process interruption and timer
  rehydration on the target custom ROM;
- overdue visual state, permitted/suppressed audio behavior, ramp and the
  30-minute audio cap; and
- static pseudo-glass/readability at accepted glass-intensity values.

Reboot recovery is not a probe target. A platform limitation is recorded as
runtime evidence and does not expand V1 scope.

## Secret and Artifact Checks

After the Foundation build path exists, inspect source, packaged resources,
logs and produced evidence using a synthetic/redacted fixture workflow. A
check passes only when the real user key was never introduced; test artifacts
must remain key-free by construction.

## Evidence Ownership

Feature/task records own executable commands and verdicts. This spec owns the
minimum proof shape and risk routing; it does not store run logs, screenshots
or task lifecycle state.
