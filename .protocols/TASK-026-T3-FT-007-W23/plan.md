---
description: Execution plan for TASK-026-T3-FT-007-W23.
status: active
---
# Plan — TASK-026-T3-FT-007-W23

## Goal

Use the existing display tick → `TimerCapability` → `PlatformRuntime` path to
prove and, only if needed, repair overdue audio request/start, repeat, stop and
30-minute cap behavior, with deterministic denial/error handling that leaves
visual overdue and dismissal intact.

## Non-goals

- No change to W8 task history, lifecycle, feature/REQ status, scheduler
  checkpoint or terminal evidence.
- No change to TimerAlertPolicy, Display, MainActivity, FoundationRuntime,
  Settings, public graph, dependency, event boundary, permission, audio
  framework, network or credentials.
- No physical audibility claim without an authorized target.

## Inputs / source specs

- Task: `.memory-bank/tasks/TASK-026-T3-FT-007-W23.task.json`
- Feature/REQ: `.memory-bank/features/FT-007-overdue-alert.md`, `REQ-016`
- Canonical: Boundary Map, Capability Interfaces, Platform Runtime, Lifecycle
  Map, Local Data, Runtime Verification and Tier Policy.

## Constraints / invariants (MUST / NEVER)

- MUST keep Timer & Alert as owner of lifecycle and alert requests.
- MUST keep visual overdue independent of audio outcome and any-tap dismissal.
- MUST preserve selected/default signal, validated volume, 5–10 second ramp,
  accepted repeat interval and 30-minute audio-only cap.
- NEVER touch production/test files outside the three indexed boundary files.
- NEVER launch emulator/device/adb or use live audio, network or credentials.

## Scope

### In scope

- `TimerCapability.kt`
- `adapters/platform/PlatformRuntimeAdapter.kt`
- `OverdueAlertTest.kt`
- Required `.protocols/TASK-026-T3-FT-007-W23/` protocol and
  `.tasks/TASK-026-T3-FT-007-W23/` receipts.

### Out of scope

- All other project-authored code, task/lifecycle artifacts and durable product
  docs; no `/verify`, `/red-verify` or `/mb-sync`.

## Proposed changes

### Preflight-confirmed change surface

- Expected hints: all three hard-boundary source/test paths.
- Additional same-outcome files/areas: none authorized.
- Hard `write_boundary` present and satisfied: yes, pending final diff check.
- `forbidden_scope` / stop-condition check: clear; physical target remains
  deferred.

## Applicable quality gates

- [x] `./gradlew clean assembleDebug` — clean Android debug build; exit `0`.
- [x] `./gradlew testDebugUnitTest` — host/unit scheduler and policy suite; exit
  `0`, 101 tests, no failures/errors.
- [x] `node scripts/mb-lint.mjs && git diff --check` — exit `0`, mb-lint passed
  78 files and no diff whitespace errors.

## Claim-linked RED / GREEN (T2/T3)

- applicability: applicable, with accepted pre-implementation GREEN fallback
  for any claim whose existing fake-platform path already satisfies it.
- accepted claim locators: FT-007-AC-004 / REQ-016 and FT-007-AC-005 / REQ-016.
- planned probe: deterministic display-tick driver with synthetic clock and
  fake platform/audio scheduler; isolated denial/error matrix.
- observable RED: missing first tick request/start, missing repeat/stop/cap,
  missing explicit denial/error, or uncaught audio-start failure. A direct
  `advanceAt()` call alone is not sufficient scheduler proof.
- corresponding GREEN: trace and matrix receipts plus required build/unit/
  static gates after the bounded correction; all current-attempt gates pass.
- accepted not-applicable reason: if pre-change scheduler/fake path is already
  claim-equivalent GREEN, do not artificially break it; retain baseline and
  add only missing proof or a separately evidenced crash/denial seam.
- T3 isolation/cleanup: disposable in-memory timer/settings state, synthetic
  timestamps, deterministic fake scheduler/platform, no device/runtime state;
  receipts are in `.tasks/TASK-026-T3-FT-007-W23/`.

## MB-SYNC handoff / owner

- Owner identified: none in this `/exe` handoff; lifecycle owner remains
  responsible for later `/verify`, `/red-verify` and status decision.
- `.memory-bank/` docs needing update: none authorized by the hard boundary.
- Task registry/status update owner: lifecycle owner; unchanged here.
- Changelog update owner: none for this execution.

## Definition of done

- Three source/test paths only are changed for the outcome.
- Scheduler trace, denial/error matrix and physical-audibility separation
  receipts exist with required labels.
- Host fake result is `PASS`; physical audibility is `DEFERRED`.
- Required gates are run and handed off; task lifecycle remains unchanged.
