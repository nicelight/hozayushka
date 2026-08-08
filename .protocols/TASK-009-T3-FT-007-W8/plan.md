---
description: Execution plan for TASK-009-T3-FT-007-W8.
status: active
---
# Plan — TASK-009-T3-FT-007-W8

## Goal

Deliver the FT-007 fullscreen overdue projection, stable full elapsed counter,
any-tap dismissal and repeatable built-in alert policy while keeping visual
state independent from Android audio suppression.

## Non-goals

- FT-005 preset validation/defaults/labels/colors.
- FT-006 countdown/cancellation/arithmetic/recovery implementation.
- FT-009 user-facing sound/volume Settings.
- Reboot recovery, new permissions, event infrastructure, private-store
  bypasses, composition-root business orchestration or target runtime claims.

## Inputs / source specs

- Task record: `.memory-bank/tasks/TASK-009-T3-FT-007-W8.task.json`
- Feature/Epic: `FT-007-overdue-alert.md`, `EP-003-timers-alert.md`
- REQ IDs: `REQ-015`, `REQ-016`
- Direct canonical specs: boundary map, capability interfaces, lifecycle map,
  local data, platform runtime and runtime verification.

## Constraints / invariants

- MUST keep Timer & Alert as owner of overdue state and alert request.
- MUST keep Main Display as renderer/gesture dispatcher and Android as audio
  policy owner.
- MUST keep the visual overdue state after silent/DND/unavailable-route audio
  suppression and cap only audio at 30 minutes.
- NEVER add reboot recovery, new permissions, private storage access,
  composition-root business logic or live credentials.

## Scope

### In scope

- `timer/`: overdue projection and deterministic alert policy/request state.
- `display/`: fullscreen overdue composition and any-tap routing.
- `adapters/platform/`: accepted policy/audio request seam.
- `app/`: lifecycle/display wiring only if needed to consume the public seams.
- `app/src/test/`: isolated host claim probes and redacted artifacts.

### Out of scope

- All forbidden_scope and stop-condition items in the indexed task card.

## Proposed changes

### Preflight-confirmed change surface

- Expected hints kept: timer, display, platform adapter and tests.
- Additional same-outcome files/areas and rationale: none at preflight.
- Hard `write_boundary` present and satisfied: not set.
- `forbidden_scope` / stop-condition check: clear.

## Applicable quality gates

- [x] `./gradlew clean assembleDebug` — clean Android debug build.
- [x] `./gradlew testDebugUnitTest` — deterministic FT-007 host probes.
- [x] Static boundary/secret inspection — no forbidden bypass or secret-bearing
  source/evidence.

## Claim-linked RED / GREEN (T2/T3)

- applicability: applicable
- accepted claim locators: FT-007-AC-001 through AC-005; REQ-015/016.
- planned test/probe and environment: isolated in-memory timer, synthetic
  timestamps and synthetic platform policy inputs.
- observable RED: baseline source/probe shows no task-owned overdue fullscreen
  projection, repeatable built-in policy or suppression-preserving integration.
- corresponding GREEN: task-owned deterministic result artifact after changes.
- T3 isolation, safe rerun, cleanup, and permission boundary: no live device,
  credentials or external permissions; each host probe uses disposable state.

## MB-SYNC handoff / owner

- Owner identified: `/verify` then explicit lifecycle owner.
- `.memory-bank/` docs needing update: none for this bounded implementation;
  protocol/evidence are the durable execution handoff.
- Task registry/status update owner: `/verify`/lifecycle owner; `/exe` owns only
  `ready -> in_progress`.

## Definition of done

- Production and host test changes satisfy the selected FT-007 outcome,
  mandatory build/unit/static evidence is recorded, target evidence is marked
  DEFERRED with residual risk, and handoff routes to `/verify`.
