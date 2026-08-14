---
description: Decision log for clarified PRD decomposition.
status: active
last_updated: 2026-08-10
---
# PRD Bootstrap Decision Log

## 2026-08-03 — Decomposition baseline

- No new operator decision was required. The clarified PRD is the authoritative
  product source and already resolves the forecast horizon to 10 days, the
  hourly entry path, location dataset, timer sound policy and Settings behavior.
- The product map uses four value-oriented epics and nine features. The split
  follows independently acceptable user outcomes; architecture layers, storage,
  provider contracts and testing levels are intentionally not promoted to
  product features here.
- At this 2026-08-03 baseline, Global SDD backbone and Foundation Dev Path were
  still pending for `/spec-design`; later accepted state supersedes this
  historical routing statement.

## 2026-08-10 — Provider-migration decomposition reconciliation

- No new operator decision was required. The accepted PRD delta replaces the
  active Yandex-only target with selectable Open-Meteo/OpenWeather and is
  authoritative for product decomposition.
- Existing EP-002/EP-004 and FT-002/FT-003/FT-004/FT-008 boundaries remain the
  smallest coherent map. Stable REQ/EP/FT/AC IDs are preserved where their
  product outcome remains the same; REQ-027–REQ-029 add the previously unowned
  atomic provider-selection/key-applicability, Open-Meteo-attribution and
  no-cross-provider-isolation outcomes.
- Global Backbone remains `complete` at Planning Revision `1`. Its Yandex-only
  provider design is not repaired here; affected surfaces route first to fresh
  `/spec-design` and, after that reconciliation, to `/feature-to-tasks`.
