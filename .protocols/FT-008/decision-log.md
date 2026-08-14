---
description: Decision log for FT-008 weather access and offline location settings task decomposition.
status: active
last_updated: 2026-08-10
---
# FT-008 — Decision log

## 2026-08-10 — Revision-2 provider Settings reconciled

- Done W9 remains unchanged historical evidence and retains current ownership
  only for unchanged AC-002–AC-005 location/catalog behavior.
- New `TASK-019-T3-FT-008-W16` solely owns current AC-001/AC-006–AC-008:
  default/no-key Open-Meteo, explicit OpenWeather with local key,
  provider-context failures, stable selection and Open-Meteo attribution.
- W16 depends on the latest W15 baseline, starts `planned`, and is a Settings
  and secret task; transport/dispatch/cache and forecast outcomes remain W17–W19.
- Existing Settings, provider, secret, local-data and verification specs are
  reused. No third provider, backend/shared key, registry, event bus or hard
  write boundary is selected.
- Exact next owner is fresh `/review-tasks-plan --all`; scheduler and `/mb-sync`
  state remain untouched.

## 2026-08-07 — Clean task surface generated

- FT-008 is eligible for decomposition: feature design is `complete`, the
  Global Backbone is `complete` at Planning Revision `1`, and the Foundation
  Gate `TASK-002-T3-FT-000-W1` is `done`.
- No FT-008 task, implementation plan or task-owned evidence was retained in
  the indexed queue. This run creates a clean surface with one task;
  `TASK-010-T3-FT-008-W9` is `planned`.
- The task depends directly on the approved sequential predecessor
  `TASK-009-T3-FT-007-W8`; Foundation remains transitive. No dependency on
  FT-009 or a future task is invented.
- T3 is required by local secret handling, persistence, provider integration,
  offline packaged data and the redacted artifact proof route.
- Existing architecture, boundary, capability-interface, provider,
  local-data, secret-handling, platform-runtime and runtime-verification specs
  are reused. No competing canonical path, feature-owned design hub, graph
  edge, public contract or behavior-spec file is created.
- Settings & Location remains the sole owner of the API key and selected
  location; Weather Context remains the owner of refresh/normalized weather
  state; the catalog remains immutable packaged data.
- No new dependency, storage owner, product decision or Planning Revision
  decision is selected. No runtime evidence is fabricated during planning.
- Fresh handoff route: `/review-tasks-plan FT-008`.
