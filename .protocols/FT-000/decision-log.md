---
description: Decision log for the FT-000 Foundation queue generation.
status: active
last_updated: 2026-08-06
---
# FT-000 Foundation Decision Log

## 2026-08-04 — Queue generated from accepted Foundation Dev Path

- `Foundation Required: true` remains unchanged. The current workspace has no
  executable Android baseline, while the global backbone explicitly routes the
  missing baseline to FT-000.
- `REQ-000`, the reserved `FT-000` pseudo-feature, two normal JSON task records
  and one final gate were created using the existing schema, index and tier
  policy. No alternative registry, task field, status machine or protocol
  family was introduced.
- The accepted architecture is preserved: one deployable app, one composition
  root, five capability slices, explicit external adapters, owner-local state
  and no internal event/message boundary.
- Existing canonical specs are reused; no competing or third canonical path was
  created.
- No new material operator decision was needed for task generation. Exact
  Gradle/plugin/package/UI toolkit/persistence choices remain implementation
  details explicitly routed to FT-000. If execution needs a new dependency or
  changes public/package, architecture, source-of-truth or security posture, it
  must stop for the governing operator checkpoint and/or `/spec-design` repair
  route.
- The final gate anchor is now
  `TASK-002-T3-FT-000-W1`; product feature tasking remains downstream of its
  completion.

## 2026-08-06 — Explicit owner closure

- The operator directed `VERDICT: PASS`, prohibited any further `/verify` or
  `/red-verify` run and explicitly ordered FT-000 closed.
- Existing clean host build/tests, deterministic fixture, boundary/package and
  redacted secret-scan evidence were accepted as the Foundation basis.
- `TASK-002-T3-FT-000-W1` is `done`; `REQ-000` and `FT-000` are `verified`.
- The owner accepted as residual risk that no fresh independent/adversarial
  verification followed the host-only scope revision. Target-device
  compatibility remains unverified and deferred to a later readiness/release
  task.
- Product feature task decomposition may proceed through
  `/feature-to-tasks FT-<NNN>`.

## 2026-08-05 — Operator decision: defer target-runtime checks

- The operator explicitly narrowed the active FT-000 Foundation Gate to the
  host/build/fixture baseline while the application remains a walking
  skeleton.
- `TASK-002-T3-FT-000-W1` must not start an emulator, run ADB install/launch,
  or execute physical-device smoke. Its active gates are clean build, host
  tests, deterministic local fixture/timer probes, boundary/static checks and
  redacted artifact evidence.
- Fullscreen, lifecycle, timer rehydration and audio-policy checks remain
  valid product/runtime evidence, but are deferred to a later readiness/release
  task after the application is ready. They are not a Foundation closure
  prerequisite.
- Historical verification reports that expected target evidence remain
  retained as historical artifacts and are superseded by this decision. No
  `/exe`, `/verify` or task execution is started by this planning change.
