---
description: Decision log for the FT-000 Foundation queue generation.
status: active
last_updated: 2026-08-04
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
