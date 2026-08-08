---
description: Foundation queue plan for the accepted executable Android baseline.
status: active
last_updated: 2026-08-06
---
# FT-000 Foundation Queue Plan

## Objective

Convert the accepted Foundation Dev Path into the minimum sequential queue that
proves an executable Kotlin Android baseline before product task design. The
queue is planning state only; it does not implement product features or close
the final gate.

## Accepted basis

- [Foundation decision](../../.memory-bank/foundation.md): `Foundation Required:
  true`, pressure map and exit criteria.
- [Global SDD backbone](../../.memory-bank/spec-backbone.md): `complete`,
  `Planning Revision: 1`.
- [System Architecture](../../.memory-bank/architecture/system-architecture.md):
  one deployable modular monolith, one composition root and five capability
  slices.
- [Boundary Map](../../.memory-bank/contracts/boundary-map.md): accepted module
  ownership and directed dependency graph.
- [Runtime Verification](../../.memory-bank/testing/runtime-verification.md):
  minimum build/start/smoke/fixture/device proof route.

## Queue

| Order | Task | Tier | Wave | Status | Depends on | Role |
|---|---|---|---|---|---|---|
| 1 | `TASK-001-T3-FT-000-W0` | T3 | W0 | done | none | Implement the walking skeleton, owner-local reset path, synthetic/redacted fixture path and host probe route. |
| 2 | `TASK-002-T3-FT-000-W1` | T3 | W1 | done | `TASK-001-T3-FT-000-W0` | Final Foundation Gate: clean build/test, deterministic host smoke and redacted evidence. |

`TASK-002-T3-FT-000-W1` is the one and only final foundation gate. It depends
on every current Foundation implementation/probe task.

## Reused canonical specs

No new canonical spec is required. Existing subject owners are sufficient:

- runtime/module shape: `architecture/system-architecture.md`;
- module and public boundary ownership: `contracts/boundary-map.md` and
  `contracts/capability-interfaces.md`;
- Android lifecycle/display/audio: `contracts/platform-runtime.md`;
- provider boundary and redaction: `contracts/weather-provider.md` and
  `contracts/local-secret-handling.md`;
- local persistence ownership/reset invariants: `domains/local-data.md`;
- lifecycle semantics: `states/lifecycle-map.md`;
- proof and evidence: `testing/runtime-verification.md` and
  `testing/strategy.md`.

The queue does not create a Foundation registry, schema, lifecycle or protocol
family.

## Scope boundary

Foundation establishes only the executable substrate needed by the accepted
host smoke path: one app/composition root, the required capability-slice
discovery roots, private owner-local persistence baseline, deterministic
disposable fixtures, redacted provider path, a supported Foundation probe mode
and host checks. Target-device/emulator checks are deferred until the
application is ready for runtime/readiness validation.
It does not implement weather mapping, forecast semantics, timer UX, Settings
behavior, the complete GeoNames catalog or any FT-001–FT-009 acceptance
criteria.

## Claim-linked proof plan

`TASK-001` carries prospective RED/GREEN mappings for the missing baseline,
architecture composition, boundary graph, local-state reset, secret-safe fixture
path and Foundation proof route. `TASK-002` is verification-only, so meaningful
RED is recorded as not applicable with a concrete reason; its alternative is a
fresh clean/reset run of the host evidence matrix and artifact scanning. No
ADB, emulator or physical-device check is part of this queue.

Every T3 check uses synthetic/disposable state, safe reset/cleanup and no live
credential. A device limitation is evidence and follows the existing stop route;
it does not authorize scope growth.

## Handoff

The Foundation queue is complete. The explicit owner closed the final gate on
2026-08-06 using the existing host-only evidence and accepted the omitted fresh
independent/adversarial checks plus deferred target-device compatibility as
residual risk. Product task design may proceed through
`/feature-to-tasks FT-<NNN>`.
