---
description: Implementation plan for the FT-001 main clock and display shell.
status: active
last_updated: 2026-08-08
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
3. `TASK-015-T3-FT-001-W12` — own only the bounded Main Display
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

- Weather provider mapping, freshness, history, card content or palette.
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
host stream support. Exact implementation remains executor discretion; no new
file or dependency is required by this plan.

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
