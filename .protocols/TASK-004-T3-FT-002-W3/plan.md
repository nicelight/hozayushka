---
description: Execution plan for TASK-004-T3-FT-002-W3.
status: active
---
# Plan — TASK-004-T3-FT-002-W3

## Goal

Make the existing Weather Context seam produce and persist the accepted FT-002
four-card display-ready outcome with deterministic host evidence.

## Non-goals

FT-001 shell behavior; FT-003/FT-004 forecast sessions; FT-005–FT-007 timer,
preset, countdown, overdue and alert behavior; FT-008/FT-009 Settings and
personalization; provider/backend expansion, live credentials, new dependencies,
new graph edges or target-device fabrication.

## Inputs / source specs

- Task: `.memory-bank/tasks/TASK-004-T3-FT-002-W3.task.json`
- Feature/REQ: `FT-002`, `REQ-005..REQ-008`, `REQ-022..REQ-026`
- Canonical owners: Weather Context owns normalization, cache/history,
  freshness, trend and fallback; Main Display only renders its read model;
  provider adapter owns boundary mapping; platform adapter only lifts signals.

## Scope

### In scope

- Weather domain records, deterministic provider normalization and redacted
  synthetic fixture path.
- Four ordered cards, selected-city timezone/day-night/moon fallback, sign rule,
  explicit 78-color palette, endpoint clamp and static pseudo-glass model.
- Timestamped successful cache, launch/city/30-minute refresh decisions,
  24-hour freshness and stale contour projection.
- Installation-relative seven-day history and 3-hour/12-hour pressure arrows.
- Main Display consumption of the read-only projection while preserving FT-001.

### Out of scope

All task `forbidden_scope` and anti-goals from the indexed card.

## Preflight-confirmed change surface

- Expected: weather, provider adapter, display, composition root, platform seam,
  resources, host tests/fixtures as needed.
- Actual extra files require same-outcome rationale in progress/handoff.
- Hard `write_boundary`: not set.
- Forbidden scope / stop conditions: clear at start.

## Applicable quality gates

- [ ] `./gradlew clean assembleDebug` — Android debug build.
- [ ] `./gradlew testDebugUnitTest` — deterministic FT-002 behavior.
- [ ] static boundary/secret scans — accepted owner direction and redaction.
- [ ] target device/emulator route — `DEFERRED` if unavailable; no runtime PASS claim.

## Claim-linked RED / GREEN

- applicability: AC-001..AC-006 applicable; AC-007 accepted alternative proof.
- initial RED: recorded in `context.md` and `progress.md` before production change.
- GREEN: claim-specific tests and redacted/static receipts recorded after implementation.
- T3 isolation: in-memory stores and deterministic clock/provider fixtures; no live network/key; reset/isolation in tests.

## Handoff owner

`/verify TASK-004-T3-FT-002-W3` is the next functional verification route. No
closure, promotion, `/verify`, `/red-verify` or `/mb-sync` is performed here.
