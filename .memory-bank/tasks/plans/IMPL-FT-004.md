---
description: Implementation plan for the FT-004 ten-day forecast view.
status: active
last_updated: 2026-08-08
---
# IMPL-FT-004 — Ten-day forecast view

## Goal

Deliver the ten-day forecast screen opened from Tomorrow or Day-after only when
the selected-city daily read model is complete. The screen uses today plus the
next nine city-local days in a two-by-five layout, shared card presentation and
the common forecast exit flow; missing data leaves Main Display unchanged with
the accepted message.

## Ordered work

1. `TASK-006-T3-FT-004-W5` — implement and verify the cohesive daily mapping,
   long-term session entry, two-by-five projection and shared exit behavior
   under the Forecast Sessions owner, using the existing Weather Context and
   Main Display contracts.

## Owner, graph and dependency

- Primary owner: `Forecast Sessions`.
- Code root: `app/src/main/kotlin/com/hozayushka/app/forecast`.
- `Main Display → Forecast Sessions` carries Tomorrow/Day-after intent and the
  returned session projection; `Forecast Sessions → Weather Context` carries
  the normalized complete daily read model.
- `Weather Context → Yandex Weather Adapter` remains the provider boundary for
  daily normalization. Main Display composes the view and never reads raw
  provider fields or private Weather Context storage.
- Direct prerequisite: `TASK-013-T3-FT-003-W5`, which is done and depends on
  done `TASK-012-T3-FT-003-W4`; Foundation is retained transitively through
  the completed FT-003 → FT-002 → FT-001 chain. `TASK-005-T3-FT-003-W4`
  remains failed historical evidence and is not a live prerequisite.

## Scope

### In scope

- Redacted daily provider mapping for exactly ten ordered records.
- Selected-city API timezone for daily dates, day boundaries and day/night
  selection.
- All-or-nothing completeness gate and Tomorrow/Day-after entry.
- Two rows of five cards with `dd`, shared temperature/glass/illustration
  presentation and no pressure arrow.
- Three-second auto-close, single-tap hint, double-tap close and hold/release
  close through the shared forecast-session contract, plus the exact
  missing-data message.

### Out of scope

- Main clock/date/fullscreen and Main Display shell behavior from FT-001.
- Current/daily card, freshness, local-history and pressure ownership from
  FT-002.
- Hourly forecast content from FT-003; only the already accepted shared exit
  contract is consumed.
- Presets, countdown, overdue state, alert behavior, Settings/location content
  or API-key UI from FT-005 through FT-009.
- Backend/cloud/accounts, Google Services, reboot recovery, event/message
  infrastructure, a new provider or dependency, and live credentials.

## Expected advisory change surface

- `app/src/main/kotlin/com/hozayushka/app/forecast/`
- `app/src/main/kotlin/com/hozayushka/app/weather/`
- `app/src/main/kotlin/com/hozayushka/app/adapters/weather/`
- `app/src/main/kotlin/com/hozayushka/app/display/`
- `app/src/main/kotlin/com/hozayushka/app/adapters/platform/`
- `app/src/main/kotlin/com/hozayushka/app/app/` (wiring only if needed)
- `app/src/main/res/`
- `app/src/test/kotlin/com/hozayushka/app/`
- `app/src/test/resources/fixtures/`

These paths are advisory and non-exhaustive; no hard write boundary is set.

## Quality gates and verification

- `./gradlew clean assembleDebug` — clean Android build.
- `./gradlew testDebugUnitTest` — deterministic redacted mapping, timezone,
  completeness, owner-local save/reload persistence, projection and
  shared-session checks.
- Use the target-device route from [Runtime Verification](../../testing/runtime-verification.md#target-device-evidence)
  only for residual forecast-card readability/static-material or timing results
  that host checks cannot establish. Planning creates no runtime evidence.

| Claim | Expected result | Proof artifact |
|---|---|---|
| `FT-004-AC-001 / REQ-010, REQ-026` | A successful normalized ten-day result survives an isolated Weather Context save/reload with an identical complete read model; Tomorrow and Day-after open the same long-term session only for that saved model, while unavailable data stays on Main Display. | Isolated save/reload/entry result with reset and cleanup outcome |
| `FT-004-AC-002 / REQ-010, REQ-022` | Ten ordered city-local daily records render in two rows of five, beginning today and ending on day nine after today. | Redacted fixture projection |
| `FT-004-AC-003 / REQ-010, REQ-022, REQ-026` | Cards show `dd`, temperature background, temperature and illustration, use selected-city day/night selection and omit pressure arrows. | Presentation/timezone probe plus residual device observation if required |
| `FT-004-AC-004 / REQ-010` | Auto-close, single-tap hint/cancel, double-tap close and hold/release close match the shared transitions. | Deterministic session timing/gesture probe |
| `FT-004-AC-005 / REQ-010, REQ-026` | No session or invented day is returned for missing required data; exact Russian message is shown. | Rejection/fallback probe |

## Constraints and invariants

- Preserve the accepted Boundary Map and exact capability-interface edges; no
  direct storage or provider-adapter bypass is allowed.
- Forecast Sessions owns transient long-term session state and gestures;
  Weather Context owns normalized daily data and the complete/unavailable
  predicate.
- Use selected-city API timezone for daily dates, boundaries and day/night
  selection; device timezone remains the Main Display clock/date source.
- Required daily data is all-or-nothing; no partial sequence, invented day or
  fabricated field may open a session.
- Reuse the accepted static pseudo-glass/illustration rules and omit pressure
  arrows from long-term cards.
- Do not add product scope, a new dependency, event infrastructure, live key,
  backend/cloud, Google Services or reboot recovery.

## Direct normative inputs

- [.memory-bank/features/FT-004-ten-day-forecast.md](../../features/FT-004-ten-day-forecast.md)
- [.memory-bank/epics/EP-002-weather-context.md](../../epics/EP-002-weather-context.md)
- [.memory-bank/requirements.md](../../requirements.md)
- [.memory-bank/prd.md](../../prd.md)
- [.memory-bank/invariants.md](../../invariants.md)
- [.memory-bank/architecture/system-architecture.md](../../architecture/system-architecture.md), [AD-003](../../architecture/system-architecture.md#ad-003---cross-slice-orchestration-stays-in-a-capability-owner)
- [.memory-bank/contracts/boundary-map.md](../../contracts/boundary-map.md)
- [.memory-bank/contracts/capability-interfaces.md](../../contracts/capability-interfaces.md)
- [.memory-bank/contracts/weather-provider.md](../../contracts/weather-provider.md)
- [.memory-bank/contracts/weather-provider.md#refresh-cache-and-failure-rules](../../contracts/weather-provider.md#refresh-cache-and-failure-rules)
- [.memory-bank/contracts/weather-card-presentation.md](../../contracts/weather-card-presentation.md)
- [.memory-bank/domains/local-data.md](../../domains/local-data.md)
- [.memory-bank/domains/local-data.md#ft-004-long-term-forecast-records](../../domains/local-data.md#ft-004-long-term-forecast-records)
- [.memory-bank/states/lifecycle-map.md](../../states/lifecycle-map.md)
- [.memory-bank/contracts/platform-runtime.md](../../contracts/platform-runtime.md)
- [.memory-bank/testing/runtime-verification.md](../../testing/runtime-verification.md)
- [.memory-bank/testing/runtime-verification.md#redacted-integration-fixtures](../../testing/runtime-verification.md#redacted-integration-fixtures)
- [.memory-bank/workflows/tier-policy.md](../../workflows/tier-policy.md)

## W5 boundary reconciliation

- `TASK-006-T3-FT-004-W5` is `done` with independent functional `PASS` and
  semantic `semantic-pass` evidence for the planned ten-day outcome. Its direct
  prerequisite is the completed `TASK-013-T3-FT-003-W5`; the failed historical
  `TASK-005-T3-FT-003-W4` remains preserved and is not reopened.
- Target-device evidence remains `DEFERRED` and non-blocking with residual
  risk; no runtime `PASS` is claimed. Feature, epic and RTM lifecycle values
  remain unchanged, and scheduler promotion, dependent-state reconciliation,
  checkpoint and terminal-state updates remain outside this sync.

## Handoff

The task-plan surface is already represented by the completed indexed task and
its evidence links; this boundary returns to the scheduler/explicit owner for
applicable post-sync gates and any separate lifecycle or promotion decision.
