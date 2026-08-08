---
description: Retry attempt 2 claim-linked RED/GREEN basis for TASK-004-T3-FT-002-W3.
status: active
---
# Retry RED/GREEN — TASK-004-T3-FT-002-W3

## Attempt 2 basis

- attempt: 2
- receipt_status: current
- retry source: fresh independent Reviewer report in
  `TASK-004-T3-FT-002-W3-S-VERIFY-final-report-docs-01.md`
- retained prior evidence: attempt 1 RED/GREEN and gates are supporting-only;
  they are not reused as independent verification.

## Corrected claims

| Claim | Retry RED source | Correction required | Fresh GREEN artifact |
|---|---|---|---|
| FT-002-AC-003 / REQ-006, REQ-023 | Reviewer source inspection found the pressure-arrow `TextView` used hard-coded `alpha = 0.32f` instead of the local shared `PseudoGlassMaterial`. | Use the already-created `material` from `WeatherCardPresentation.pseudoGlass(0.45f)` for both temperature and pressure-arrow rendering. | Attempt-2 gate receipt plus fresh source/static assertion in `gate-results-attempt-2.md`. |
| FT-002-AC-004 / REQ-007, REQ-025 | Reviewer call-graph inspection found only production `LAUNCH` wiring; no valid-location caller or 30-minute production caller. | Wire the accepted Settings valid-location callback and lifecycle-owned 30-minute signal to `WeatherCapability.refreshIfNeeded`; keep cadence/freshness decisions in Weather Context. | Attempt-2 trigger tests/source receipt in `gate-results-attempt-2.md`. |

## Preserved claims

AC-001, AC-002, AC-005, AC-006 and AC-007 retain attempt-1 claim-linked
supporting evidence. They receive the mandatory final executor gates again after
the retry change; independent `/verify` remains the verdict owner.

## Isolation and safety

The retry uses deterministic host tests, synthetic redacted provider fixtures,
no live network or credential, and no target-device claim. No architecture,
dependency, public graph edge, product decision, task identity, tier,
dependency, scheduler state or forbidden FT-003..FT-009 scope is changed.
