---
description: Execution handoff for TASK-021-T2-FT-003-W18.
status: active
---
# Handoff — TASK-021-T2-FT-003-W18

## Summary
- Added deterministic provider-separated regression proof for W18 AC-001/AC-005: both selected providers require all eight fixed city-local slots, every one of 16 missing-slot cases rejects entry with the exact unavailable message, and provider switching cannot borrow the other provider's cache.
- Production behavior was unchanged because the pre-implementation claim probe was already GREEN; the task-owned delta is the missing durable proof matrix.

## Where to look
- key files: `app/src/test/kotlin/com/hozayushka/app/ForecastSessionTest.kt`, `.tasks/TASK-021-T2-FT-003-W18/hourly-completeness-matrix.json`, `.tasks/TASK-021-T2-FT-003-W18/red-baseline.md`.
- advisory `touched_files` deviations and rationale: `WeatherCapability.kt`, `ForecastSessionCapability.kt` and `app/src/test/resources/fixtures/` were not changed; the existing selected-provider exact-slot implementation already passed the new claim-equivalent matrix, and in-memory fixtures are synthetic/redacted with no resource change required.
- hard write-boundary compliance: not set; semantic forbidden scope remains clear.

## How to run / verify
- gates: `progress.md` → Attempt 1 gate receipts; clean build, full host unit suite and `mb-lint && git diff --check` all exit 0.
- claim-linked RED/GREEN evidence: `progress.md` → Claim-linked RED/GREEN; `.tasks/TASK-021-T2-FT-003-W18/red-baseline.md` → RED; `.tasks/TASK-021-T2-FT-003-W18/hourly-completeness-matrix.json` → 2 complete + 16 missing-slot + cache-isolation GREEN.
- current-attempt reuse candidate locators: none offered; receipts are supporting-only due broad project/generated read surfaces and pre-existing worktree changes.
- superseded/supporting-only receipt locators: `.protocols/TASK-021-T2-FT-003-W18/progress.md` → `## Gate receipts — Attempt 1`.

## Known issues
- Target-device/live-provider evidence is deferred by task/operator constraints; no runtime PASS.
- Independent `/verify` has not run and remains the next required owner step.

## Follow-ups
- Fresh `/verify TASK-021-T2-FT-003-W18`; scheduler owns lifecycle/status/checkpoint and later feature semantic review.
