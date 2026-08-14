---
description: Decision log for FT-002 task decomposition.
status: active
last_updated: 2026-08-12
---
# FT-002 — Decision log

## 2026-08-12 — W18 downstream completion reconciled

- The already-decided `TASK-021-T2-FT-003-W18` closure is `done` after fresh
  functional and semantic verification of its selected-provider hourly
  completeness delta. This does not reopen failed W17 or inherit W20 evidence;
  W18's own AC-001/AC-005 evidence remains linked from FT-003.
- W20 remains `done`, TASK-020 remains failed after 3/3 attempts, and W19 /
  `TASK-022-T2-FT-004-W19` remains blocked with its existing identity,
  dependency, lifecycle and historical block evidence. Scheduler recovery,
  promotion and downstream lifecycle changes remain external.

## 2026-08-12 — W20 completion reconciled

- The already-decided `TASK-023-T3-FT-002-W20` closure is `done` after
  executor `PASS_FOR_HANDOFF`, fresh `/verify` `PASS` and final independent
  T3 `/red-verify` `semantic-pass`. Current handoff, verifier-owned host/timer
  evidence and semantic report are linked from the task record and FT-002
  feature surface.
- W20 closes only the selected OpenWeather key-save activation delta. It does
  not reopen failed `TASK-020-T3-FT-002-W17`, change its exhausted `3/3`
  history, or inherit/close downstream forecast acceptance.
- `TASK-021-T2-FT-003-W18` and `TASK-022-T2-FT-004-W19` remain blocked with
  their existing IDs, block evidence and lifecycle; scheduler dependency
  recovery, promotion and checkpoint updates remain external.
- Existing provider, secret, lifecycle and boundary specs remain sufficient;
  no new spec, graph edge, product decision or Planning Revision is created.

## 2026-08-11 — W20 activation repair and downstream dependency rebuild

- The final W17 red verification confirms one bounded unresolved outcome only:
  after first explicit OpenWeather selection, the initial refresh records the
  missing-key failure; a later valid key save performs zero provider calls and
  leaves that obsolete error current. The accepted W17 migration facts remain
  baseline evidence: Yandex is removed, exactly Open-Meteo/OpenWeather are
  wired, ordinary dispatch is selected-only, state is provider/location keyed,
  and key handling is redacted.
- Created `TASK-023-T3-FT-002-W20` as the smallest cohesive T3 repair. Weather
  Context remains the orchestration owner; Settings & Location remains the
  validation/persistence/secret owner; the composition root only wires the
  existing refresh executor. The task owns valid-key-save activation, stale
  missing-key clearance on successful refresh, selected-provider failure
  isolation and secret-safe proof.
- `TASK-023-T3-FT-002-W20` depends on completed `TASK-019-T3-FT-008-W16`, not
  on failed W17. This preserves W17's terminal failure and avoids treating
  failed evidence as a runnable prerequisite. The direct W18 dependency is
  explicitly wired from W17 to `TASK-023-T3-FT-002-W20`; W19 remains behind
  W18, so its recovery is transitive. W18/W19
  IDs, tiers, waves, blocked statuses, block evidence and acceptance scope are
  preserved.
- No canonical spec or Planning Revision change is required. Existing
  provider, capability, secret, lifecycle, local-data and runtime-verification
  contracts are sufficient; no new module, edge, event boundary, provider,
  fallback or secret transport is selected. Fresh `/review-tasks-plan` is the
  next scheduler route for FT-002, FT-003 and FT-004.

## 2026-08-10 — Revision-2 provider migration reconciled

- The approved Global Backbone Planning Revision `2` and successful Foundation
  revalidation supersede the former Yandex target without rewriting W3/W15
  terminal history.
- Current acceptance retains W3 ownership only for unchanged AC-001/AC-003.
  New `TASK-020-T3-FT-002-W17` solely owns the revised AC-002 and AC-004–AC-008
  provider migration outcome.
- W17 depends on `TASK-019-T3-FT-008-W16`, starts `planned`, and stays cohesive:
  exactly two adapters, selected-only dispatch, provider+location cache/history,
  no fallback/mixing, redacted transient OpenWeather `appid`, and Yandex removal.
- Existing subject specs are sufficient. No provider framework, registry, event
  bus, new graph edge or hard write boundary is selected.
- Exact next owner is fresh `/review-tasks-plan --all`; scheduler state and
  `/mb-sync` remain untouched.

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

## 2026-08-12 — W22 operator-requested weather-card illustration delta

- Accepted one new cohesive FT-002 outcome: Main Display weather cards must
  show deliberately designed, legible visual illustrations for sun, cloud,
  rain and related snow/neutral/day-night states, positioned in a dedicated
  layer that does not obscure temperature, date or pressure content.
- Added feature-local `FT-002-AC-009` and created
  `TASK-025-T3-FT-002-W22`; existing AC-001..008 ownership, W3/W15/W17/W20
  terminal records, W21 planning history and prior terminal `SUCCESS` remain
  unchanged. W22 depends on planned `TASK-024-T3-FT-001-W21` because both
  tasks compose the same Main Display card surface and must execute
  sequentially.
- Architecture preflight found that `WeatherCapability` already exposes the
  normalized `WeatherIllustration` and optional `moonPhase`, while the main
  `weatherCard` omits that visual layer. Existing Android Canvas/Path/Paint
  primitives are sufficient; the Unicode helper used by forecast cards is not
  reused for the main-card visual treatment. No new asset/resource pipeline,
  dependency, module, public contract or graph edge is selected.
- Main Display is the primary owner through the existing
  `Main Display -> Weather Context` read boundary. Weather provider selection,
  cache/history, refresh/freshness, city-timezone day/night source, pressure
  semantics, temperature/pseudo-glass, clock/timer behavior and forecast-card
  scope remain outside W22 writes. `app/src/main/res/` and
  `app/src/main/assets/` are forbidden; exact production/test boundary is
  `DisplayCapability.kt` plus `DisplayProjectionTest.kt`.
- Required proof is fresh claim-linked RED/GREEN, host build/unit gates,
  static source/resource inspection, deterministic rendered image/contact-sheet
  evidence and measured non-overlap review. Target 1280x720
  Samsung/custom-ROM readability is deferred with residual risk when no
  authorized target observation exists. No emulator/device, ADB, network,
  provider, credentials, `/exe`, `/verify`, `/red-verify` or `/mb-sync` is
  performed by this planning run.

## 2026-08-12 — W25 operator-feedback bounds and pressure-arrow adjustment

- Accepted one bounded follow-up under the existing `FT-002-AC-009`; no new
  feature AC or product state is created. Completed W22 remains the historical
  six-state illustration baseline and its ID, status, evidence and history are
  unchanged.
- Created `TASK-028-T3-FT-002-W25` as the smallest cohesive T3 Main Display
  outcome after `TASK-027-T3-FT-001-W24`, preserving sequential ownership of the
  shared `DisplayCapability.kt` / `DisplayProjectionTest.kt` surface. The task
  reduces measured painted bounds for all six existing illustrations,
  moderately enlarges the CLEAR sun disk inside the reduced composition, and
  replaces only the Main Display Unicode pressure glyphs with measured
  Canvas/Path arrows.
- `WeatherCapability` remains the owner of `pressureDirection`,
  `pressureArrowCount`, pressure history and threshold/fallback calculation.
  `REQ-008` and the registered Weather Card Presentation pressure contract are
  rendering constraints/regression inputs; no WeatherCapability production
  change, provider/network/settings/audio/timer/lifecycle change, partly-cloudy
  state, new spec, graph edge or dependency is selected.
- W25 is `planned` because its direct W24 dependency remains `planned`.
  Planning Revision `2`, Foundation dependency, all prior task statuses and
  evidence, lifecycle/RTM values, scheduler checkpoint and terminal state remain
  unchanged. Fresh RED/GREEN, measured icon/sun bounds, UP/DOWN/zero-arrow
  contact sheet, explicit stroke-width/pixel visibility, independent rubric,
  build/unit/static gates and target-device `DEFERRED` are part of the handoff;
  this planning run performs no implementation, `/mb-sync`, emulator, adb,
  device, network or credential action.
