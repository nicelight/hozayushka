---
description: Implementation plan for the FT-001 main clock and display shell.
status: active
last_updated: 2026-08-10
---
# IMPL-FT-001 — Main clock and display shell

## Goal

Deliver the accepted always-visible Main Display outcome on top of the
Foundation Android scaffold: dominant device-time `HH:mm`, Russian date,
exactly four stable weather cards in the lower-left zone, accepted colon
states, and city gestures that use the existing Settings boundary.

## Ordered work

1. `TASK-003-T3-FT-001-W2` — implement and verify the Main Display outcome
   under the Main Display capability owner. Use deterministic host-side checks
   for formatting, state transitions and gesture routing, then use the
   accepted target-device route for fullscreen, keep-screen-on and readability
   evidence. This task is `done` historical evidence and remains unchanged.
2. `TASK-014-T3-FT-001-W11` — correct only the confirmed zero-height
   city/transient-row allocation and prove the accepted city-to-Settings route
   on `Tecno_Pova_6_API_35`. Preserve TASK-003 evidence and all downstream
   behavior; retain host/build/unit/static gates and defer Samsung/custom-ROM
   acceptance. This task is preserved as `failed` after the independent
   semantic non-city cancellation finding.
3. `TASK-015-T3-FT-001-W12` (`done`) — own only the bounded Main Display
   `FT-001-AC-005` city hold/Settings-preservation delta. Exercise the existing
   downstream protected-cancellation contract (REQ-013; regression guard only;
   canonical basis in TASK-015 `normative_inputs`) through the public non-city
   weather-card dispatch path. Use one Main Display-local dispatcher that
   captures the stream at `ACTION_DOWN` and delivers terminal events without
   rechecking live timer state. Retain the existing city hold-to-Settings path,
   selected/empty city short-tap rules, preset interactions and overdue
   behavior; do not re-own Timer & Alert semantics or add an architecture
   edge/module. The task depends on the last successful W10 baseline, while
   failed W11 remains evidence only.
4. `TASK-016-T3-FT-001-W13` (`done`) — own the confirmed local Main Display
   ticker debt. Consolidate scheduling under one idempotent owner, gate it on
   existing Activity pause/resume and view attach/detach lifecycle, and reuse
   the existing weather-card view tree when the projection/presentation inputs
   are unchanged. Keep the current scalar 20 Hz clock/date/colon cadence;
   Weather Context, Timer & Alert and Forecast Sessions remain owners of their
   existing concerns. W13 is host-proof-only and does not plan target-device
   evidence.
5. `TASK-017-T3-FT-001-W14` (`done`) — based only on `TD-W13-001`, separate
   the required scalar 20 Hz clock/date/colon refresh from repeated Weather
   Context display-ready input construction. Reuse a capability-owned
   `WeatherProjection` snapshot and invalidate it only on accepted successful
   Weather refresh, validated location change or the existing projection/date/
   day-night/pressure-trend/24-hour freshness boundary. Preserve Weather Context
   cache/history/projection ownership and the existing Main Display read edge;
   use only host counting fixtures, build, unit and static gates. W14 is
   host-proof-only and does not claim target-device evidence.

## Primary owner and accepted graph

- Primary owner: `Main Display`.
- Code root evidenced by the Foundation scaffold:
  `app/src/main/kotlin/com/hozayushka/app/display`.
- Platform boundary crossed: Main Display → Android Runtime Adapter through
  [Display Runtime Boundary](../../contracts/platform-runtime.md#display-runtime-boundary).
- Settings boundary crossed: Main Display → Settings & Location through
  [Main Display to Settings and Location](../../contracts/capability-interfaces.md#main-display-to-settings-and-location).
- Executable prerequisite/owner: the current scaffold must expose a minimal
  Settings destination and return seam in the existing Settings & Location
  code root. Main Display owns the city gesture intent; Settings & Location
  owns that destination surface and settings state; the composition root only
  wires the route. This uses the accepted edge and adds no module, public
  contract or graph edge. If the seam cannot be provided within that boundary,
  stop and route the new decision to `/spec-design`.
- Existing projection consumers retained through the accepted Main Display →
  Weather Context, Timer & Alert and Forecast Sessions contracts. No direct
  storage or private-adapter access is authorized.
- Composition-root changes, if required for wiring, remain wiring-only and do
  not move product orchestration out of Main Display.

## Scope

### In scope

- Landscape fullscreen display policy, hidden system panels and keep-screen-on.
- Dominant `HH:mm` without seconds and device-time date in `dd` plus Russian
  genitive month, without year or weekday.
- Stable city/date, exactly four lower-left weather-card positions and
  preset-button zones that do not shift when weather data is absent. FT-002
  retains weather data, card content, freshness and weather-specific
  presentation ownership.
- Online 3-second rise/following fade-to-2% colon, offline fixed 38% colon and
  active-countdown 382/618 ms blink as a display projection.
- Empty-city `Выбрать город` behavior, selected-city short-tap no-op and
  long-hold/empty-city short-tap routing to the minimal accepted Settings
  destination and return to Main Display.

### Out of scope

- Weather provider mapping, weather history, card content or palette. W14's
  internal snapshot invalidation preserves, but does not re-own, existing
  Weather Context freshness and projection semantics.
- Hourly/long-term forecast session behavior.
- Preset configuration, countdown lifecycle, cancellation or overdue alert.
- Offline country/city catalog, API-key handling and personalization preview.
- Settings catalog/content, API-key validation and personalization preview; the
  minimal destination/return seam required by the accepted city route is the
  only Settings surface in scope.
- Backend, cloud/accounts, Google Services, reboot recovery, heavy realtime
  visual effects or unaccepted controls.
- Reopening TASK-003, changing scheduler terminal history, changing timer or
  forecast semantics, and treating Foundation probe controls as FT-000 product
  work.

### W11 follow-up boundary

- Own only the FT-001-AC-002/AC-005 runtime-layout and Settings-reachability
  delta evidenced by the generic emulator.
- Keep city and each exercised populated transient row non-zero inside the
  existing shell while preserving clock dominance, date, four cards and three
  presets. Existing transient-message semantics remain regression-only inputs.
- If the same minimum correction also restores zero-height Foundation probe
  controls, observe that as a conditional gate only; do not introduce a second
  Foundation-specific mechanism.

### W12 active-countdown dispatch boundary

- Own only the bounded `FT-001-AC-005` city hold/Settings-preservation delta.
  Exercise the existing downstream protected-cancellation contract (REQ-013;
  regression guard only; canonical basis in TASK-015 `normative_inputs`) on the
  public Main Display event-delivery path: non-city weather-card double tap at
  120 ms must cancel an active countdown, while a single tap keeps it active and
  shows the accepted hint.
- Preserve city double with delayed-Settings protection, selected-city
  short-tap no-op, empty-city short-tap, preset interactions, overdue any-tap
  dismissal and the four-card/preset shell as the remaining regression guards.
- Use the existing Main Display → Timer & Alert and Main Display → Settings &
  Location contracts. Timer & Alert remains the sole owner of timer semantics;
  no new contract, module, graph edge, dependency or public state is created.
- The decisive proof is a fresh public runtime matrix on
  `Tecno_Pova_6_API_35`; focused host stream coverage is supporting evidence
  and must not be used to claim Android touch reachability. Safe cleanup is
  mandatory, and Samsung/custom-ROM/1280x720 remains `DEFERRED`.

### W13 Main Display ticker debt boundary

- Own only the local ticker scheduler lifecycle and card-render reuse inside
  `DisplayCapability.kt`, with wiring-only Activity pause/resume forwarding in
  `MainActivity.kt` and focused host support in the existing display test.
- Maintain one active loop at most while the Main Display is attached/resumed;
  pause and detach must suppress rescheduling, and resume/attach must restore
  one loop without duplicate starts. Keep the scalar clock/date/colon cadence
  required by FT-001.
- Avoid repeated weather-card view-tree reconstruction when the existing
  Weather Context projection/presentation inputs are unchanged; rebuild only
  on a changed input. This does not change Weather Context cache/refresh or
  projection ownership and does not add a notification/public contract.
- The task-owned feature deltas are `FT-001-AC-002`, `FT-001-AC-003` and
  `FT-001-AC-004`; the colon/countdown path is a regression guard only. No
  timer/audio ownership, provider work, gesture semantics, Forecast-wide
  optimization, target-device evidence or new architecture is in scope.

### W14 Weather Context projection/decode debt boundary

- Own only the residual `TD-W13-001` cost confirmed after W13: each scalar
  refresh must not reload/decode/build the same display-ready weather input.
- Primary owner is `Weather Context` at
  `app/src/main/kotlin/com/hozayushka/app/weather`; Main Display remains the
  read-only consumer through the existing `Main Display → Weather Context`
  contract. No Main Display ticker, Activity lifecycle, renderer or public
  contract change is planned.
- Reuse an in-memory capability-owned `WeatherProjection` snapshot and rebuild
  only after accepted successful refresh, observed validated location change or
  an existing selected-city/date/day-night/pressure-trend/24-hour freshness
  boundary required by the current projection. Failed refresh and unchanged
  scalar ticks preserve the snapshot.
- The exact task-owned feature locator is `FT-001-AC-002 / REQ-002`.
  `REQ-007`, `REQ-022` and `REQ-025` govern the preserved weather/time/failure
  semantics and remain regression guards. No new FT-001 AC, RTM lifecycle,
  module, edge, dependency, event, provider, Forecast, Timer/audio, gesture or
  target-device scope is introduced.

## Expected advisory change surface

- `app/src/main/kotlin/com/hozayushka/app/display/DisplayCapability.kt` —
  Main Display composition, clock/date projection, colon state and gestures.
- `app/src/main/kotlin/com/hozayushka/app/app/MainActivity.kt` — accepted
  display entry/lifecycle wiring if the current Foundation surface needs
  replacement by the product shell.
- `app/src/main/kotlin/com/hozayushka/app/app/FoundationRuntime.kt` — wiring
  only if the product display requires a public capability projection already
  owned by a slice.
- `app/src/main/kotlin/com/hozayushka/app/adapters/platform/PlatformRuntimeAdapter.kt`
  — device time/window/network signal access behind the existing platform
  boundary, only where the current seam is insufficient.
- `app/src/main/kotlin/com/hozayushka/app/settings/` — minimal accepted
  Settings destination/return seam owned by Settings & Location; no catalog,
  API-key, validation or personalization behavior.
- `app/src/main/res/values/` — Russian strings and minimal static display
  resources when required by the accepted shell.
- `app/src/test/kotlin/com/hozayushka/app/` — deterministic host checks for
  display formatting, colon state and city gesture routing.

For W11 the expected delta narrows to `DisplayCapability.kt` and the existing
`DisplayProjectionTest.kt` convention. For W12 the expected delta remains in
those same two paths: the Main Display active-countdown dispatcher and focused
host stream support. For W13 the expected delta remains
`DisplayCapability.kt`, `MainActivity.kt` and the existing
`DisplayProjectionTest.kt`; no new file or dependency is required by this
plan. Exact implementation remains executor discretion within the hard
boundary carried by the W13 task card.
For W14 the expected delta is only `WeatherCapability.kt` and the existing
`WeatherContextTest.kt`; no new file, dependency or public wiring is required.

These paths are advisory and non-exhaustive. No hard `write_boundary` is set;
the semantic scope, forbidden scope and stop conditions remain binding.

## Applicable quality gates and UAT

- `./gradlew clean assembleDebug` — proves the Android application still
  assembles from a clean state.
- `./gradlew testDebugUnitTest` — proves deterministic clock/date, colon-state,
  composition and city-gesture checks.
- Target-device evidence from
  [Runtime Verification](../../testing/runtime-verification.md#target-device-evidence)
  — proves 1280×720 landscape fullscreen, hidden system panels, keep-screen-on
  and readability that host checks cannot reliably establish.
- `git diff --check` — mandatory static integrity gate for the bounded change.
- The documented `Tecno_Pova_6_API_35` route — mandatory supplementary W11
  proof of non-zero Android View bounds, city long-hold Settings entry and Back
  return; it remains generic-emulator evidence only.
- For W12, the same generic emulator must provide public city hold, city double
  with delayed-Settings checkpoint, non-city single with hint, non-city double
  cancellation, preset interaction and safe-cleanup observations. Host
  dispatch tests are supporting only; they do not prove public Android touch
  reachability.
- For W13, the clean build, host unit suite and static diff gate must prove the
  single scheduler owner, pause/resume gating and unchanged-versus-changed
  card-tree refresh using an isolated fake scheduler. No emulator or physical
  target route is part of this bounded debt task.
- For W14, the same clean build, host unit suite and static diff gate plus an
  isolated counting Weather Context fixture must prove repeated projection
  reuse and accepted refresh/location/freshness invalidation. No emulator or
  physical target route is part of this bounded debt task.

## Claim-linked proof plan

The indexed FT-001 task surface covers all five FT-001 acceptance claims. Execution must first
record the current Foundation baseline honestly; an already-green part of a
claim is preserved and is not changed without need. The final proof is:

| Claim | Decisive result | Artifact |
|---|---|---|
| `FT-001-AC-001` | Landscape/fullscreen, system panels hidden, screen held on while open | Host/static result plus target-device notes/screenshot |
| `FT-001-AC-002 / REQ-002` | `HH:mm` is dominant; city/date stay left, exactly four weather cards stay in the lower-left area and three preset positions stay right with and without weather projection. FT-002 owns data/content. | Host count/placement assertion plus target-device readability evidence |
| `FT-001-AC-003` | Device timezone drives clock/date and date has only `dd` plus Russian genitive month | Deterministic unit-test output |
| `FT-001-AC-004` | Online/offline/countdown colon state transitions match the accepted timing/brightness values | Deterministic state-test output; visual result only where device evidence is needed |
| `FT-001-AC-005` | Empty-city short tap and any-city hold reach the minimal accepted Settings destination; selected-city short tap is a no-op and Back returns to Main Display | Gesture/routing test output plus target-device interaction notes |

The material NFR proof for `REQ-023` is linked to AC-001 and AC-002 and must
include a pass/fail comparison against the accepted 1280×720 readability and
lightweight-static-UI constraints. Any manual runtime check uses the known
target device and the safe rerun/cleanup route already defined by the runtime
verification spec.

TASK-014 does not re-own all five historical claims. Its exact claim ownership
is limited to the new runtime-layout delta under `FT-001-AC-002` and the
city-target/Settings-reachability delta recorded in its failed historical
attempt under `FT-001-AC-005`. TASK-015 owns only the bounded current
city-hold/Settings-preservation acceptance delta under `FT-001-AC-005`.
The existing downstream protected-cancellation contract (REQ-013; regression
guard only; canonical basis in TASK-015 `normative_inputs`) and the
layout-history/timer/overdue checks remain regression guards. This does not
reopen or rewrite TASK-014. Both tasks retain generic-emulator-only evidence
and defer Samsung/custom-ROM results.

## W12 boundary reconciliation

`TASK-015-T3-FT-001-W12` is `done` after executor `PASS_FOR_HANDOFF`, fresh
functional `PASS` and independent semantic `semantic-pass`. The completed
boundary is the Main Display-local active-countdown dispatcher and its public
generic-emulator proof for city hold/Settings preservation, non-city
single/double regression behavior, preset/overdue guards and safe cleanup.
The focused host stream output remains supporting evidence only. Samsung,
custom-ROM and 1280x720 physical-device evidence remains `DEFERRED`; no target
runtime `PASS` is claimed. FT-001/REQ-004 and FT-006/REQ-013 ownership and
lifecycle values remain unchanged, and TASK-003/TASK-014 history remains
preserved.

## W13 boundary reconciliation

`TASK-016-T3-FT-001-W13` is `done` after executor `PASS_FOR_HANDOFF`, fresh
functional `PASS` and independent durable semantic `semantic-pass`. The
completed boundary is the Main Display-local ticker owner, existing lifecycle
gating and unchanged-versus-changed weather-card render reuse, proven by clean
build, host unit and static diff gates. See the [executor handoff](../../../.protocols/TASK-016-T3-FT-001-W13/handoff.md),
[functional verification](../../../.protocols/TASK-016-T3-FT-001-W13/verification.md),
[verifier-owned evidence](../../../.tasks/TASK-016-T3-FT-001-W13/verifier-owned-evidence.md),
[durable semantic verification](../../../.protocols/TASK-016-T3-FT-001-W13/red-verification.md)
and [semantic report](../../../.tasks/TASK-016-T3-FT-001-W13/TASK-016-T3-FT-001-W13-S-RED-VERIFY-final-report-docs-01.md).

FT-001 and direct RTM values `REQ-002`, `REQ-003` and `REQ-022` remain
`implemented`; Weather Context, Timer & Alert, Forecast and the existing
architecture/spec contracts remain unchanged. W13 is host/static proof only;
Samsung/custom-ROM/1280x720 physical evidence remains `DEFERRED`, with no
target-device runtime `PASS` claim. No feature/epic closure, promotion,
dependent-state, scheduler checkpoint or terminal-state change is performed by
this sync.

## W14 boundary reconciliation

`TASK-017-T3-FT-001-W14` is `done` after executor `PASS_FOR_HANDOFF`, fresh
functional `PASS` and independent durable semantic `semantic-pass`. It is the
smallest independent Weather Context memoization follow-up for `TD-W13-001`:
repeated scalar reads reuse a display-ready projection snapshot, while accepted
successful refresh, validated location and existing date/day-night/pressure-
trend/24-hour freshness boundaries rebuild it. See the [executor handoff](../../../.protocols/TASK-017-T3-FT-001-W14/handoff.md),
[functional verification](../../../.protocols/TASK-017-T3-FT-001-W14/verification.md),
[verifier-owned evidence](../../../.tasks/TASK-017-T3-FT-001-W14/verifier-owned-evidence.md),
[durable semantic verification](../../../.protocols/TASK-017-T3-FT-001-W14/red-verification.md)
and [semantic report](../../../.tasks/TASK-017-T3-FT-001-W14/TASK-017-T3-FT-001-W14-S-RED-VERIFY-final-report-docs-01.md).

The host proof remains inside the existing Weather Context test convention;
no new canonical spec or behavior example is required. W14 is host/static
proof only. Samsung/custom-ROM/1280x720 physical evidence remains `DEFERRED`
with no target-device runtime `PASS` claim. W2 `done`, W11 `failed`, W12
`done` and W13 `done` identities, evidence, protocols and terminal history
remain unchanged. FT-001/EP-001 lifecycles, direct RTM values, scheduler
checkpoint, terminal state and Planning Revision `1` remain unchanged; no
feature closure, promotion or dependent-state transition is inferred.

## Constraints and invariants

- Preserve Main Display ownership of composition and gestures; do not put
  business orchestration in `MainActivity`, a generic helper, or the
  composition root.
- Keep the minimal Settings destination/return seam under the existing
  Settings & Location owner and existing Main Display → Settings & Location
  edge; do not add a new edge or make Settings read Weather Context.
- Use device timezone for the main clock/date. Selected-city/API timezone is
  not allowed to shift the main clock.
- Preserve the stable shell when weather is missing or the network is absent.
- Keep Weather Context's private cache/history and display-ready projection
  ownership intact; memoization is internal and does not add a consumer write
  path or public invalidation contract.
- Preserve the accepted colon states and do not infer timer arithmetic in Main
  Display; consume the Timer & Alert projection.
- Do not add a dependency or change package, public contract, architecture,
  security posture or product scope without the governing checkpoint.
- Never add API-key literals, backend/cloud/accounts, Google Services,
  reboot-recovery behavior or heavy realtime visual effects.

## Direct normative inputs

- [.memory-bank/features/FT-001-main-clock-display.md](../../features/FT-001-main-clock-display.md)
- [.memory-bank/requirements.md](../../requirements.md)
- [.memory-bank/prd.md](../../prd.md)
- [.memory-bank/architecture/system-architecture.md](../../architecture/system-architecture.md)
- [.memory-bank/contracts/boundary-map.md](../../contracts/boundary-map.md)
- [.memory-bank/contracts/capability-interfaces.md](../../contracts/capability-interfaces.md)
- [.memory-bank/contracts/platform-runtime.md](../../contracts/platform-runtime.md)
- [.memory-bank/testing/runtime-verification.md](../../testing/runtime-verification.md)
- [.memory-bank/invariants.md](../../invariants.md)
- [.memory-bank/constitution.md — bounded checkpoints and Definition of Done](../../constitution.md#iii-bounded-agent-autonomy-and-human-checkpoints)
- [.memory-bank/workflows/tier-policy.md — claim-linked T3 proof](../../workflows/tier-policy.md#claim-linked-red--green-for-t2t3)

## Handoff

After this planning surface is accepted, the immediate route is
`/review-tasks-plan FT-001`; execution is not part of this plan.
