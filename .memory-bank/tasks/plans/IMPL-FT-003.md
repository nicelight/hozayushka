---
description: Implementation plan for the FT-003 hourly forecast view.
status: active
last_updated: 2026-08-08
---
# IMPL-FT-003 — Hourly forecast view

## Goal

Deliver the eight-slot hourly forecast screen opened from Today only when the
selected-city hourly read model is complete. The screen uses the shared card
presentation, the accepted city timezone and the common forecast exit flow;
missing data leaves the main display unchanged with the accepted message.

## Ordered work

1. `TASK-005-T3-FT-003-W4` — historical failed task for the cohesive hourly
   forecast outcome. Preserve its failed lifecycle and evidence; do not retry
   or rewrite it in this repair route.
2. `TASK-012-T3-FT-003-W4` — repair and verify the Weather Context/provider
   normalization defect: accept a supported 48-record full-day response,
   select the existing eight city-local slots, and preserve all-or-nothing
   required-field validation. This is one cohesive independently observable
   follow-up; session/display behavior remains outside its task-owned outcome.
3. `TASK-013-T3-FT-003-W5` — complete the remaining Forecast Sessions/Main
   Display outcome after TASK-012: Today entry gating, the exact unavailable
   fallback, shared session timing/gestures, and the minimum integration
   regression for the repaired eight-slot projection and shared card surface.
   It does not repeat TASK-012 provider normalization.

## Owner, graph and dependency

- Primary owner for the original feature outcome: `Forecast Sessions`.
- TASK-012 repair owner: `Weather Context`, code root
  `app/src/main/kotlin/<app-package>/weather`; the repair crosses only the
  registered Weather Context → Yandex Weather Adapter provider boundary.
- Original feature code root: `app/src/main/kotlin/<app-package>/forecast`.
- `Main Display → Forecast Sessions` carries Today intent and the returned
  session projection; `Forecast Sessions → Weather Context` carries the
  normalized complete hourly read model.
- Weather Context remains the owner of provider normalization, hourly data and
  completeness. The provider adapter remains transport/mapping only. Main
  Display composes the view and never reads raw provider fields or private
  Weather Context storage.
- Direct prerequisite: `TASK-004-T3-FT-002-W3`; Foundation is retained
  transitively through the approved FT-002 queue.
- TASK-012 uses the same completed TASK-004 baseline and references TASK-005
  only as historical failure evidence. It does not depend directly on the
  failed record, preserving the existing failed-dependent blocking semantics.
- TASK-013 depends directly on done TASK-012 and therefore retains the
  Foundation dependency transitively. It does not depend on failed TASK-005;
  TASK-005 remains preserved historical evidence while TASK-013 establishes
  the remaining session/display outcome.

## Repair scope

TASK-012 is limited to the existing Weather Context → Yandex Weather Adapter
boundary and its deterministic redacted host proof. It changes no public
eight-slot projection, feature acceptance target, graph edge, lifecycle field,
schema or scheduler checkpoint. A valid 48-record provider response must be
accepted when the eight selected slots have all required fields; missing a
required field in any selected slot remains unavailable rather than partial or
fabricated. The selected-city API timezone continues to determine labels and
the following-day boundary.

TASK-013 is limited to Forecast Sessions/Main Display entry, rejection,
transient timing/gesture state and the registered consumer integration. It may
consume the TASK-012 public normalized read model and regression-check the
shared card presentation, but it must not change the provider adapter or
recreate selected-slot normalization/required-field validation.

## Scope

### In scope

- Redacted hourly provider mapping for the exact accepted eight-slot sequence.
- Selected-city API timezone labels and following-day boundary for `00:00` and
  `03:00`.
- All-or-nothing completeness gate and Today entry.
- Two rows of four hourly cards with slot time, shared temperature/glass/
  illustration presentation and no pressure arrow.
- Three-second auto-close, single-tap hint, double-tap close and hold/release
  close, plus the exact missing-data message.

### Out of scope

- Main clock/date/fullscreen and Main Display shell behavior from FT-001.
- Current/daily weather card, freshness/history and pressure ownership from
  FT-002.
- Ten-day forecast from FT-004.
- Presets, countdown, overdue alert, Settings/location content or API-key UI.
- Backend/cloud/accounts, Google Services, reboot recovery, event/message
  infrastructure, new provider/dependency and live credentials.

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
  completeness, projection and session gesture checks.
- Use the target-device route from [Runtime Verification](../../testing/runtime-verification.md#target-device-evidence)
  only for residual forecast-card readability/static-material or timing results
  that host checks cannot establish. Planning creates no runtime evidence.

| Claim | Expected result | Proof artifact |
|---|---|---|
| `FT-003-AC-001 / REQ-009` | Complete hourly read model opens from Today; unavailable/incomplete data stays on Main Display. | Entry/completeness probe |
| `FT-003-AC-002 / REQ-009` | Eight exact slots render in two rows of four, with the last two on the following city-local day. | Redacted fixture projection |
| `FT-003-AC-003 / REQ-009, REQ-022` | Shared temperature/glass/illustration rules, slot time instead of date, no pressure arrow and selected-city timezone. | Presentation/timezone probe plus residual device observation if required |
| `FT-003-AC-004 / REQ-009` | Auto-close, single-tap hint, double-tap close and hold/release close match the accepted transitions. | Deterministic session timing/gesture probe |
| `FT-003-AC-005 / REQ-009, REQ-026` | No session or invented slot is returned for missing required data; exact Russian message is shown. | Rejection/fallback probe |

## Constraints and invariants

- Preserve the accepted Boundary Map and the exact capability-interface edges;
  no direct storage or provider-adapter bypass is allowed.
- Forecast Sessions owns transient session state and gestures; Weather Context
  owns normalized hourly data and the complete/unavailable predicate.
- Use selected-city API timezone for hourly labels and day boundaries; device
  timezone remains the Main Display clock/date source.
- Reuse the shared static pseudo-glass/illustration rules and omit pressure
  arrows from hourly cards.
- Do not add product scope, a new dependency, event infrastructure, live key,
  backend/cloud, Google Services or reboot recovery.

## Direct normative inputs

- [.memory-bank/features/FT-003-hourly-forecast.md](../../features/FT-003-hourly-forecast.md)
- [.memory-bank/epics/EP-002-weather-context.md](../../epics/EP-002-weather-context.md)
- [.memory-bank/requirements.md](../../requirements.md)
- [.memory-bank/prd.md](../../prd.md)
- [.memory-bank/invariants.md](../../invariants.md)
- [.memory-bank/architecture/system-architecture.md](../../architecture/system-architecture.md)
- [.memory-bank/contracts/boundary-map.md](../../contracts/boundary-map.md)
- [.memory-bank/contracts/capability-interfaces.md](../../contracts/capability-interfaces.md)
- [.memory-bank/contracts/weather-provider.md](../../contracts/weather-provider.md)
- [.memory-bank/contracts/weather-card-presentation.md](../../contracts/weather-card-presentation.md)
- [.memory-bank/domains/local-data.md](../../domains/local-data.md)
- [.memory-bank/states/lifecycle-map.md](../../states/lifecycle-map.md)
- [.memory-bank/contracts/platform-runtime.md](../../contracts/platform-runtime.md)
- [.memory-bank/testing/runtime-verification.md](../../testing/runtime-verification.md)
- [.memory-bank/workflows/tier-policy.md](../../workflows/tier-policy.md)

## W4/W5 boundary reconciliation

- `TASK-005-T3-FT-003-W4` remains `failed` historical evidence; its lifecycle,
  protocol/evidence artifacts and scheduler-owned state are preserved.
- `TASK-012-T3-FT-003-W4` is `done` with functional `PASS` and semantic
  `semantic-pass`; target evidence is `DEFERRED`/non-blocking and no runtime
  `PASS` is claimed.
- The feature, epic and RTM lifecycle values are not transitioned by this
  reconciliation. Scheduler-owned promotion, dependency blocking/unblocking,
  checkpoint and terminal state remain outside this plan.

- `TASK-013-T3-FT-003-W5` is `done`, depends on `TASK-012-T3-FT-003-W4`, and
  has fresh T3 `/verify` → `/red-verify` evidence for the entry/fallback,
  shared-session and consumer-regression outcome. Target evidence is
  `DEFERRED`/non-blocking with no runtime `PASS` claim.
- The current strict-doctor queue deadlock remains an external scheduler
  state caused by the failed TASK-005 dependency chain; this planning run does
  not mutate blocked downstream records, checkpoints or terminal state.

The W5 boundary handoff returns to the scheduler for its post-sync lint and
strict-doctor gates and separate scheduler-owned promotion/dependent pass;
`/mb-sync` does not perform those actions.
