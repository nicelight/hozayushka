---
description: Reserved foundation pseudo-feature for the executable Android baseline and its exit gate.
status: active
id: FT-000
lifecycle: planned
source_of_truth: .memory-bank/foundation.md, .memory-bank/requirements.md
clarification_status: complete
last_clarified: 2026-08-04
clarification_questions: 0
last_updated: 2026-08-04
---
# FT-000 — Foundation executable baseline

## Pseudo-feature status

`FT-000` is reserved for the Foundation Dev Path. It is a workflow
pseudo-feature, not a product feature, and must not contain product behavior or
replace FT-001–FT-009.

## Outcome

The project has one reproducible Kotlin Android walking skeleton that can be
built and exercised through a clean/reset host fixture path. The skeleton
preserves the accepted composition root, capability-slice boundaries, local
state ownership and redacted provider-fixture route needed before product task
design. Target-device/emulator compatibility validation is deferred until the
application is ready for runtime/readiness validation.

## Requirement

- `REQ-000` — executable baseline before product-feature implementation.

## Foundation scope

- one deployable Android application and one composition root;
- only the accepted capability slice roots and external adapter/data paths
  needed by the walking skeleton;
- private owner-local persistence baseline with a deterministic reset/isolation
  path;
- synthetic/redacted provider fixture path without a live API key;
- host-side build/test commands and deterministic fixture/probe route;
- a documented, deferred target-device route for later runtime validation,
  not a Foundation execution gate.

Product screens, provider field mapping, full GeoNames data, detailed timer,
weather, forecast or Settings behavior remain downstream feature work.

## Links

- [Foundation decision](../foundation.md): accepted Dev Path, pressure map and
  exit criteria.
- [REQ-000](../requirements.md): executable-baseline requirement and RTM row.
- [System Architecture](../architecture/system-architecture.md): one deployable
  capability-sliced target and composition-root constraints.
- [Boundary Map](../contracts/boundary-map.md): accepted modules, ownership and
  directed dependencies.
- [Local Data](../domains/local-data.md): local source-of-truth and reset/owner
  invariants.
- [Platform Runtime](../contracts/platform-runtime.md): Android runtime boundary.
- [Local Secret Handling](../contracts/local-secret-handling.md): key locality
  and evidence redaction.
- [Runtime Verification](../testing/runtime-verification.md): foundation proof
  shape and target-device evidence route.
- [FT-000 queue plan](../../.protocols/FT-000/plan.md): task sequence and claims.

## Clarifications

No critical ambiguity remains for task generation. Exact Gradle/package/UI
toolkit/persistence details remain bounded implementation choices under the
accepted architecture; a new dependency, public/package identity decision,
security-posture change or architecture change requires the governing operator
checkpoint and the existing repair route.
