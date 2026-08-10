---
description: Decision log for FT-002 task decomposition.
status: active
last_updated: 2026-08-10
---
# FT-002 — Decision log

## 2026-08-06 — Clean task surface generated

- FT-002 is eligible for decomposition: PRD clarification is complete, feature
  design is `complete`, the Global Backbone is `complete` at Planning Revision
  `1`, and the Foundation Gate `TASK-002-T3-FT-000-W1` is `done`.
- One T3 task, `TASK-004-T3-FT-002-W3`, owns the cohesive Weather Context
  outcome and depends directly on the already approved
  `TASK-003-T3-FT-001-W2`. Foundation remains a transitive dependency; no
  dependency on FT-003–FT-009 is invented.
- T3 is required by the provider/credential boundary, local persistence and
  production runtime/display impact. The card retains a claim-linked RED/GREEN
  route but records no runtime evidence during planning.
- Existing architecture, boundary, capability-interface, provider,
  presentation, local-data, lifecycle, secret-handling and verification specs
  are reused. Local Secret Handling is linked as a task-relevant canonical
  contract; no competing spec, feature-owned hub or behavior-spec file is
  created.
- The feature's field mappings, redacted fixtures and storage details remain
  implementation-level choices within the accepted boundaries. No new
  dependency, graph edge, public contract, architecture rule or product
  behavior was selected by this decomposition.

## 2026-08-06 — Independent review repair

- Reused the existing Platform Runtime contract for the launch, network,
  device-time and lifecycle signal/wiring path. Android OS remains the signal
  owner; the Application Composition Root and Android Runtime Adapter only
  lift accepted signals, while Weather Context owns refresh, freshness and
  failure projection. No graph edge or boundary was added.
- Retained `REQ-022` and `REQ-024` only as scoped FT-002 integration claims
  because the accepted provider and local-secret contracts make the weather
  timezone and redacted provider/evidence deltas task-relevant. FT-001 remains
  the owner of device clock/date, FT-008 remains the owner of user-facing
  API-key settings/validation, and the RTM-facing ownership map is explicit.
- Added feature-matching `FT-002-AC-006` and `FT-002-AC-007` so fallback and
  redacted-provider proof have exact task locators. No runtime evidence was
  created or backfilled.

## 2026-08-10 — W15 production provider follow-up

- The current source confirms a real integration gap: `FoundationRuntime` still
  wires `RedactedWeatherFixtureAdapter`, and `AndroidManifest.xml` declares
  `ACCESS_NETWORK_STATE` but not the minimum `INTERNET` permission. The accepted
  `WeatherProvider` boundary already fixes the Yandex endpoint, coordinates,
  `hours=true`, `X-Yandex-Weather-Key`, normalized semantic fields, failure
  atomicity and credential redaction; no provider/public contract change is
  needed.
- Created one cohesive T3 follow-up, `TASK-018-T3-FT-002-W15`, depending on the
  latest completed `TASK-017-T3-FT-001-W14`. It owns production transport,
  current/daily/hourly provider mapping, bounded timeout/error/fallback,
  off-main composition wiring, minimum permission, isolated fixture routing and
  host/redacted proof. It does not re-own W3 card/cache/history acceptance.
- The selected implementation route is existing Android/JDK transport and
  execution primitives with no Gradle dependency; the task stops if an external
  dependency, extra permission, new public edge, provider contract/security
  policy change or independent task split becomes necessary.
- FT-003, FT-004 and FT-008 remain compatible through their accepted
  WeatherContext/Settings boundaries. Their historical task records, lifecycle/
  RTM values, W2-W14 terminal history, scheduler checkpoint and Planning
  Revision `1` remain unchanged. No new canonical spec or behavior example is
  created.

## 2026-08-10 — W15 proof-boundary repair after rejected fresh review

- Narrowed `TASK-018-T3-FT-002-W15` to FT-002-owned production-provider
  integration claims: accepted Yandex transport, provider-to-existing-DTO
  mapping, bounded failure/cache preservation, redaction and composition
  wiring. Removed foreign feature AC/REQ ownership from its proof map and
  removed unanchored foreign feature-root entries.
- Downstream forecast read-model and Settings credential/location checks remain
  dependency-context regressions only, linked through exact canonical contract
  locators in W15 `source_artifacts`; they do not claim foreign feature
  acceptance. Identity, tier, wave, dependency, status, hard write boundary,
  Planning Revision `1`, historical records and scheduler/terminal artifacts
  remain unchanged.
