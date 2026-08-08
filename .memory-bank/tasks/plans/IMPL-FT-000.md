---
description: Implementation plan for the FT-000 executable Android Foundation baseline.
status: active
last_updated: 2026-08-06
---
# IMPL-FT-000 — Foundation executable baseline

## Goal

Deliver the smallest walking skeleton that proves the accepted Android runtime
shape and leaves a reproducible, redacted Foundation evidence path for product
feature planning.

## Ordered work

1. `TASK-001-T3-FT-000-W0` — establish the project-native Android scaffold,
   single composition root, accepted capability-slice discovery roots and
   external adapter roots. Add only the private owner-local persistence and
   deterministic fixture/probe seams required by the Foundation smoke path.
2. `TASK-002-T3-FT-000-W1` — from a clean/reset state, run the build, host
   probes and synthetic/redacted artifact scan. Target-device compatibility is
   deferred to a later readiness/release task. This is the single final
   Foundation Gate.

## Accepted execution constraints

- Preserve `AD-001`–`AD-003`, the Boundary Map graph, local-data ownership,
  platform runtime rules and local-secret redaction.
- Use no live key, live provider request, backend, Google Services, reboot
  recovery, event bus, shared business-storage owner or speculative product
  slice.
- Keep `touched_files` advisory. Do not turn accepted code-root discovery paths
  into a mechanical hard write boundary.
- Exact package/file identity, UI toolkit and persistence mechanism remain
  bounded execution choices. A new dependency, material public/architecture/
  security decision or changed Foundation objective requires operator direction
  through the existing route.

## Evidence and closure

The task cards carry claim-linked RED/GREEN or the accepted verification-only
not-applicable rationale, concrete gates and target paths. Full T3 protocol and
independent `/verify` evidence are required during execution; this plan does
not fabricate execution evidence or mark either task done.

The final gate is `done` by explicit owner decision. The owner accepted the
existing host-only evidence and recorded the omitted fresh independent/
adversarial checks plus deferred target-device compatibility as residual risk.
Route to `/feature-to-tasks FT-<NNN>` for the first product feature;
`/autopilot` must not select FT-000 work.
