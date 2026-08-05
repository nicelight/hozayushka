---
description: Execution plan for TASK-002-T3-FT-000-W1.
status: active
---
# Plan — TASK-002-T3-FT-000-W1

## Operator scope revision — 2026-08-05

This task is host-only. Emulator, ADB and physical-device checks are deferred
until the application is ready for runtime/readiness validation and must not be
started by this task. The older target-device plan text below is historical and
superseded by the task card and this decision.

## Goal

Produce fresh, redacted Foundation acceptance evidence for REQ-000 across the
clean Android build, host probes, accepted install/start path, resettable
smoke route and target-runtime compatibility checks, without modifying
production behavior.

## Non-goals

- No FT-001–FT-009 product acceptance or feature implementation.
- No reboot recovery, backend, Google Services, new provider, event system or
  extra runtime permission.
- No live API key, live provider request or unredacted evidence.

## Inputs / source specs

- Task record: `.memory-bank/tasks/TASK-002-T3-FT-000-W1.task.json`
- Task index: `.memory-bank/tasks/index.json`
- Feature: `.memory-bank/features/FT-000-foundation.md`
- REQ IDs: `REQ-000`
- Direct specs: architecture, boundary map, platform runtime, weather
  provider, local secret handling, local data and runtime verification.

## Verification targets

- REQ-000 Foundation Exit Criteria: minimal path and compatibility route are
  evidenced without unresolved Foundation pressure.
- `runtime-verification.md#foundation-minimal-proof`: clean build/launch,
  known reset state, visible display, deterministic timer, redacted fixture and
  target-device route.
- `platform-runtime.md#display-runtime-boundary`: fullscreen, hidden panels,
  keep-screen-on and readability on the target path.
- `platform-runtime.md#timer-and-audio-runtime-boundary`: temporary
  interruption rehydration, visual overdue independence and audio policy;
  reboot recovery excluded.
- `local-secret-handling.md#evidence-and-verification`: key-free source,
  resources, APK, logs and evidence.

## Constraints / invariants (MUST / NEVER)

- MUST use only the implementation/probe surface established by TASK-001.
- MUST use clean/resettable disposable state and redact all evidence.
- NEVER write production source or expand Foundation scope.
- NEVER claim device PASS without an attached authorized target/emulator.

## Scope

### In scope

- Build output and test/evidence protocol paths.
- The exact task gates and documented ADB install/start/probe route.
- Host-side source/package/secret checks needed to support the final gate.

### Out of scope

- Any production implementation change.
- Any forbidden scope in the task runtime context.

## Proposed changes

### Touched areas (hypotheses OK)

- `.protocols/TASK-002-T3-FT-000-W1/` — execution and verification handoff.
- `.tasks/TASK-002-T3-FT-000-W1/` — redacted command/device/artifact evidence.
- `app/build/outputs/apk/debug/app-debug.apk` — generated build output only.

### Preflight-confirmed change surface

- Expected hints kept: protocol/evidence paths and generated APK output.
- Additional same-outcome files/areas: none planned; no production file writes.
- Hard `write_boundary` present and satisfied: not set.
- `forbidden_scope` / stop-condition check: clear; an unavailable target is
  recorded as a blocker rather than repaired by scope expansion.

## Applicable quality gates

- [x] `./gradlew clean assembleDebug testDebugUnitTest` — clean APK and host
  Foundation probes.
- [ ] `adb install -r app/build/outputs/apk/debug/app-debug.apk` — unavailable;
  `adb devices -l` has no target.
- [ ] accepted ADB launch/Foundation probe — unavailable for the same target
  absence; no device result is inferred.
- [x] source/resource/APK/evidence secret scan — synthetic/redacted path only.
- [x] package/boundary inspection and `git diff --check` — no accidental
  contract/forbidden-scope drift.
- [x] `node scripts/mb-lint.mjs` — `mb-lint passed (66 files)`.

## Claim-linked RED / GREEN (T2/T3)

- applicability: not applicable for this verification-only final gate.
- accepted claim locators: `REQ-000`; the five task `verification_targets`;
  direct platform and secret-handling anchors listed above.
- planned test/probe and environment: clean local Gradle/Android SDK state,
  disposable host test state, and authorized ADB target if available.
- observable RED: not applicable; no production behavior is changed.
- corresponding GREEN: fresh host build/test, redacted scan and static route
  checks passed; target-device checks are unavailable and remain open for
  independent verification.
- accepted not-applicable reason and alternative proof: task
  `evidence_required` explicitly accepts this verification-only path.
- T3 isolation, safe rerun, cleanup, and permission boundary: synthetic
  fixtures only, no real key/network, generated debug APK only, reset/cleanup
  local probe state, and no reboot/new permission/external scope.

## MB-SYNC handoff / owner

`/exe` records execution evidence only. `/verify TASK-002-T3-FT-000-W1`
must independently establish the functional verdict; T3 then requires
`/red-verify` before the explicit lifecycle owner can close the task.

- [x] Owner identified: verifier / T3 semantic reviewer / Foundation lifecycle owner.
- [ ] Explicit standalone closure owner recorded: not granted by `/exe`.
- [ ] `.memory-bank/` docs needing update: none unless fresh evidence changes
  a durable Foundation fact; current runtime route is already documented.
- [x] `.memory-bank/index.md` router update needed: no.
- [x] RTM update in `.memory-bank/requirements.md` needed: no.
- [ ] Task registry/status final decision owner: verifier/lifecycle owner.
- [ ] Changelog update owner: wave-boundary `/mb-sync`.

## Definition of done

- All runnable required gates have fresh redacted evidence.
- Unavailable device gates are explicitly recorded without a false PASS.
- Current attempt, actual files, scopes, receipts and next verification route
  are linked from `progress.md` and `handoff.md`.
