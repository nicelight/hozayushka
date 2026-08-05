---
description: Execution plan for TASK-001-T3-FT-000-W0.
status: active
---
# Plan — TASK-001-T3-FT-000-W0

## Goal

Materialize the minimum runnable Kotlin Android Foundation baseline described
by `REQ-000`, preserving the accepted capability-sliced architecture and
leaving reproducible host/device evidence for `/verify` and the final
Foundation Gate.

## Non-goals

- No FT-001–FT-009 product behavior or acceptance criteria.
- No complete weather/provider field mapping, forecast semantics, timer UX,
  Settings UI or full GeoNames dataset.
- No backend, cloud, Google Services, event bus, shared business-data module,
  reboot recovery, live provider request or real credential.

## Inputs / source specs

- Task record: `.memory-bank/tasks/TASK-001-T3-FT-000-W0.task.json`
- Task index: `.memory-bank/tasks/index.json`
- Feature: `.memory-bank/features/FT-000-foundation.md`
- REQ IDs: `REQ-000`
- Direct canonical specs: architecture, boundary map, capability interfaces,
  platform runtime, weather provider, local secret handling, local data and
  runtime verification.

## Constraints / invariants (MUST / NEVER)

- MUST keep one deployable application and one composition root limited to
  wiring, lifecycle, start and shutdown.
- MUST keep mutable state behind its owning capability and use only accepted
  directed edges.
- MUST provide deterministic disposable fixture/reset behavior and redacted
  provider output without a live key.
- NEVER add product feature behavior, technical-layer slices, shared storage,
  event/message infrastructure, backend/cloud/Google Services or reboot
  recovery.
- NEVER write a real user credential to source, resources, logs, screenshots
  or evidence.

## Scope

### In scope

- Root Gradle project and one Android `app` module.
- Composition root, minimal fullscreen shell, explicit Foundation probe mode and
  accepted capability/adapter discovery roots.
- Private owner-local persistence adapters and host-side disposable stores.
- Redacted fixture provider and test fixture resource.
- Host probes, secret/artifact scan, install/start route and documentation of
  the target-device probe route, including owner-routed lifecycle/audio checks.

### Out of scope

- Product UI beyond the visible Foundation shell.
- Live networking, complete persistence schema, complete location data and
  target-device PASS claims.

## Proposed changes

### Touched areas (hypotheses OK)

- `settings.gradle.kts`, `build.gradle.kts`, `gradle.properties`, Gradle wrapper
  — reproducible one-module build.
- `app/` — Android manifest, resources, composition root, accepted slice roots,
  adapter roots and host probes.
- `.memory-bank/foundation.md` — concrete command/route navigation after the
  executable baseline exists.
- `.protocols/TASK-001-T3-FT-000-W0/` and `.tasks/TASK-001-T3-FT-000-W0/` —
  execution evidence and handoff.

### Preflight-confirmed change surface

- Expected hints kept: all task `touched_files` areas are represented.
- Additional same-outcome files/areas and rationale: Gradle wrapper files,
  Android resources/assets and Foundation navigation evidence are required to
  make the selected build/start/test path reproducible.
- Hard `write_boundary` present and satisfied: not set; semantic scope enforced.
- `forbidden_scope` / stop-condition check: clear for the bounded scaffold;
  no new material product, public-contract, security or architecture decision
  is planned.

## Applicable quality gates

- [x] Android debug build: `./gradlew assembleDebug` — proves one deployable
  Android baseline can be assembled.
- [x] Host-side foundation probes: `./gradlew testDebugUnitTest` — proves
  owner-local reset/reload and redacted fixture behavior in disposable state.
- [x] Secret/artifact scan: `rg` scan over source/resources/build outputs and
  task evidence — proves no credential-like literal or unredacted fixture
  result is retained.
- [x] Boundary review: source-root/import inspection — proves accepted roots,
  composition wiring and no unauthorized shared/event/backend boundary.
- [x] Install/start route: `adb install -r ...` and `adb shell am start ...`
  recorded; `adb devices` had no target, so no device PASS is claimed.

## Claim-linked RED / GREEN (T2/T3)

- applicability: applicable for six task-scoped claims.
- accepted claim locators:
  - `REQ-000`
  - `system-architecture.md#AD-001`
  - `boundary-map.md#dependency-graph`
  - `local-data.md#durable-data-rules`
  - `local-secret-handling.md#evidence-and-verification`
  - `runtime-verification.md#foundation-minimal-proof`
- planned test/probe and environment: current empty checkout for RED; local
  Android SDK/Gradle environment and isolated JVM test state for GREEN.
- observable RED: recorded in `progress.md` before production scaffold writes.
- corresponding GREEN: build/test output, source/resource inspection, secret
  scan and install/start route recorded after implementation.
- accepted not-applicable reason and alternative proof: target-device PASS is
  not claimable in this task; the alternative is the documented manual route
  and final Foundation Gate evidence.
- T3 isolation, safe rerun, cleanup, and permission boundary: each host probe
  owns fresh in-memory stores and generated temporary state, resets all owner
  stores in `finally`, uses no live network/credential and stays within the
  project root plus task evidence/protocol paths.

### Attempt 2 correction basis

The adversarial semantic failure is repaired within this task by exposing the
minimum installed-app Foundation route. The probe mode is explicit and
Foundation-only; it does not add product Settings UX, feature mapping, reboot
recovery, live networking, permissions or a new dependency/architecture edge.

## MB-SYNC handoff / owner

Scheduler or the explicit lifecycle owner performs verification/status closure;
`/exe` records only execution handoff.

- [x] Owner identified: verifier / Foundation lifecycle owner for closure.
- [ ] Explicit standalone owner basis recorded if manual closure is expected:
  user direct instruction authorizes execution, not T3 final closure.
- [x] `.memory-bank/` docs needing update (WHY/WHERE, no pseudocode):
  `.memory-bank/foundation.md` concrete baseline routes.
- [ ] `.memory-bank/index.md` router update needed: no.
- [ ] RTM update in `.memory-bank/requirements.md` needed: no; REQ-000 remains
  planned until the final Foundation Gate.
- [x] Task registry/status update owner: `/exe` owns `ready → in_progress`;
  verifier/lifecycle owner owns final status.
- [ ] Changelog update owner: wave-boundary `/mb-sync` or lifecycle owner.

## Definition of done

- Scaffold and tests are implemented inside the task scope.
- Both required gates are run and their exact current-attempt evidence is
  recorded.
- Claim-linked GREEN evidence and actual changed files are recorded in
  `progress.md`; `handoff.md` points to them and routes to `/verify`.
- T3 final verdict and lifecycle decision remain outside `/exe`.
