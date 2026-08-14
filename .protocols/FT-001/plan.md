---
description: Planning surface for FT-001 main clock and display shell.
status: active
last_updated: 2026-08-14
---
# FT-001 — Feature plan

## Objective

Reconcile FT-001 into the smallest executable outcome that turns the Foundation
display shell into the accepted always-visible main display: a device-time
clock/date surface with the accepted colon states, city/date above Yesterday,
the large clock above the three day cards, exactly four stable ordered weather
cards, and city interaction routed through the existing capability boundaries.

## Accepted basis

- Feature: [.memory-bank/features/FT-001-main-clock-display.md](../../.memory-bank/features/FT-001-main-clock-display.md)
- Epic: [.memory-bank/epics/EP-001-glanceable-display.md](../../.memory-bank/epics/EP-001-glanceable-display.md)
- Requirements: `REQ-001`, `REQ-002`, `REQ-003`, `REQ-004`, `REQ-005`, `REQ-022`, `REQ-023`;
  W32 owns the new numeric composition detail under `FT-001-AC-002`.
- Global Backbone: `complete`, Planning Revision `2`
- Foundation Gate: `TASK-002-T3-FT-000-W1`, status `done`
- Clarified PRD: `clarification_status: complete`

## Queue

| Order | Task | Tier | Wave | Status | Depends on | Owner |
|---|---|---|---|---|---|---|
| 1 | `TASK-003-T3-FT-001-W2` | T3 | W2 | done | `TASK-002-T3-FT-000-W1` | Main Display capability |
| 2 | `TASK-014-T3-FT-001-W11` | T3 | W11 | failed | `TASK-011-T3-FT-009-W10` | Main Display layout follow-up |
| 3 | `TASK-015-T3-FT-001-W12` | T3 | W12 | done | `TASK-011-T3-FT-009-W10` | Main Display active-countdown dispatch repair |
| 4 | `TASK-016-T3-FT-001-W13` | T3 | W13 | done | `TASK-015-T3-FT-001-W12` | Main Display ticker debt |
| 5 | `TASK-017-T3-FT-001-W14` | T3 | W14 | done | `TASK-016-T3-FT-001-W13` | Weather Context projection/decode debt |
| 6 | `TASK-024-T3-FT-001-W21` | T3 | W21 | done | `TASK-023-T3-FT-002-W20` | Main Display composition geometry |
| 7 | `TASK-027-T3-FT-001-W24` | T3 | W24 | done | `TASK-026-T3-FT-007-W23` | Main Display clock/control visual follow-up |
| 8 | `TASK-029-T3-FT-001-W26` | T3 | W26 | done | `TASK-028-T3-FT-002-W25` | Main Display idle hierarchy, neon preset borders and card spacing |
| 9 | `TASK-032-T3-FT-001-W29` | T3 | W29 | failed | `TASK-031-T3-FT-007-W28` | Preserved provenance-failed Main Display attempt; no product semantic failure inferred |
| 10 | `TASK-033-T3-FT-001-W30` | T3 | W30 | done | `TASK-031-T3-FT-007-W28` | Fresh host-only density/slot/preset proof; physical visual claim remains unproven |
| 11 | `TASK-034-T3-FT-001-W31` | T3 | W31 | done | `TASK-033-T3-FT-001-W30` | Preserved physical clock/icon geometry history |
| 12 | `TASK-035-T3-FT-001-W32` | T3 | W32 | failed | `TASK-034-T3-FT-001-W31` | Preserved scheduler failure: physical mixed-state View-tree allocation defect |
| 13 | `TASK-036-T3-FT-001-W33` | T3 | W33 | blocked | `TASK-035-T3-FT-001-W32` | Preserved blocked mixed-state repair history; failed dependency retained |
| 14 | `TASK-037-T3-FT-001-W34` | T3 | W34 | done | `TASK-034-T3-FT-001-W31` | Recovery successor: shared mixed-state View allocation and physical proof |

TASK-003 remains the authoritative completed historical implementation record.
TASK-014 remains the separate post-terminal follow-up for the generic-emulator
layout/reachability delta and is preserved as `failed` after its final semantic
regression. TASK-015 is a new indexed repair because W11 exposed a different
public active-countdown dispatch outcome: a non-city weather-card double tap did
not cancel. It depends on the last successful W10 baseline, not on failed W11;
W11 remains negative evidence and is not reopened or rewritten.

TASK-015 is one cohesive Main Display-owned repair. It uses a shared internal
active-countdown dispatcher that captures the public stream at `ACTION_DOWN`
and preserves terminal delivery across timer state changes, while retaining
city hold-to-Settings and all existing preset/overdue paths. It adds no module,
edge, public contract or downstream timer ownership and is not split by
surface or test type.

The current indexed W12 record is already `done`; this planning reconciliation
does not transition it or rewrite its evidence. `TASK-016-T3-FT-001-W13` was one
planned T3 task for confirmed local Main Display implementation debt and is now
terminal `done`; its record and evidence remain unchanged. It
consolidates the existing 20 Hz ticker's scheduling owner, forwards existing
Activity pause/resume signals to lifecycle-gate that owner, and retains the
last rendered weather-card projection so unchanged scalar ticks do not rebuild
the card view tree. The current scalar clock/date/colon cadence remains; the
Weather Context cache/projection contract, Timer & Alert ownership and
Forecast-wide behavior are not changed. W13 uses host-only proof and does not
plan target-device evidence.

The indexed `TASK-017-T3-FT-001-W14` is `done` after executor
`PASS_FOR_HANDOFF`, fresh functional `PASS` and independent durable semantic
`semantic-pass`. Based only on `TD-W13-001`, it keeps the W13 scalar 20 Hz
clock/date/colon refresh intact and memoizes the existing display-ready
`WeatherProjection` inside Weather Context, invalidating it only after an
accepted successful Weather refresh, an observed validated location change or
an existing selected-city/date/day-night/pressure-trend/24-hour freshness
boundary. Weather Context retains cache, history and projection semantics; the
existing Main Display → Weather Context read edge and public read model remain
unchanged. W14 is host-proof-only and does not claim target-device evidence.

The operator-requested W21 delta is one cohesive Main Display visual/layout
outcome. It keeps city/date in a left column above the `yesterday` card, moves
the large idle `HH:mm` into the central/upper area above the
`today`/`tomorrow`/`day_after` cards, and retains the three right-side circular
preset controls. The four card slots remain ordered; Today has strictly larger
measured allocation than the three equal smaller non-today cards; and all
inter-card gaps are uniform and larger than the current 8dp baseline. This is
an extension of the existing FT-001 AC-002 composition detail, not a new
module, graph edge, public contract or requirement. Relative geometry is the
accepted proof contract; execution must route to `/feature-doctor FT-001` if
absolute “slightly” values become a material product choice.

W21 depends on the latest terminal queue outcome `TASK-023-T3-FT-002-W20`
to preserve the current application surface, while the closed Foundation gate
remains transitive. Its primary owner is Main Display and its advisory change
surface is only `DisplayCapability.kt` plus the existing
`DisplayProjectionTest.kt`. Timer/countdown/overdue, fullscreen, city/settings,
weather projection content/freshness/palette and provider semantics are
regression constraints, not re-owned outcomes.

The terminal W22 and W23 records remain cross-feature history for the current
application surface: `TASK-025-T3-FT-002-W22` and
`TASK-026-T3-FT-007-W23` are preserved as `done` with their evidence. W24 was
sequenced after W23 and is terminal `done`; W25 is the terminal cross-feature
`TASK-028-T3-FT-002-W25` history and remains unchanged. W26 was sequenced after
W25 and is now terminal `done`. W28 is the terminal cross-feature
`TASK-031-T3-FT-007-W28` predecessor for the new W29 follow-up; its identity,
status, evidence and protocol history remain unchanged, and W29 keeps the
Foundation Gate transitive through that existing dependency chain.

The operator's W24 visual feedback is one cohesive Main Display outcome under
`FT-001-AC-002`: increase idle `HH:mm` visual dominance according to a
qualitative reference rubric and make the three existing right-side preset
controls true circles. Host proof must record actual clock bounds, equal square
button bounds and one common half-diameter radius in a fresh RED/GREEN contact
sheet. No absolute dp/ratio is selected; if one becomes necessary,
`/feature-doctor FT-001` owns the clarification.

The operator's W26 visual feedback is one cohesive post-terminal Main Display
outcome under the same `FT-001-AC-002`: make the three existing right-side
preset controls more spacious and transparent with a distinct neon gradient
border per preset while preserving each existing color identity; make idle
`HH:mm` substantially larger and adaptive within the available central area;
keep Yesterday equal to Tomorrow/Day-after; keep the three smaller cards about
20% smaller than Today; and use larger uniform weather-card gaps. Active
countdown/overdue behavior remains excluded for later FT-006/FT-007 tasks.
Fresh host proof must compare actual bounds and a same-size contact sheet, then
apply a named visual rubric. The operator's relative wording is not converted
to a new fixed dp, ratio or gradient-stop target; material numeric ambiguity
routes to `/feature-doctor FT-001`.

The W29/W30 route is now reconciled: W29 remains terminal `failed` for
provenance, while W30 is terminal `done` only for fresh host claims. W30
measured clock/card-slot/preset geometry at `2460×1080` and `1280×720`, but its
`target-device.md` explicitly kept physical rendering `DEFERRED`; the later
unlocked TECNO smoke recorded launch/health and landscape visibility, not a
visual hierarchy verdict. It therefore did not cover the operator's physical
observation that weather icons dominate and the clock is too small.

The new indexed
[`TASK-034-T3-FT-001-W31`](../../.memory-bank/tasks/TASK-034-T3-FT-001-W31.task.json)
is one bounded Main Display correction after terminal W30. It owns only the
physical visual claim under `FT-001-AC-002`: the complete `HH:mm` is the
largest readable contained element at the actual unlocked TECNO landscape
size, weather icons are materially reduced and secondary, and city/date, four
stable slots and separate right-side timer controls remain intact. Host
geometry is supporting evidence; fresh physical RED/GREEN on serial
`1156725456009666` is required for this visual claim. Emulator/AVD/QEMU is
forbidden. The exact write boundary remains `DisplayCapability.kt` plus
`DisplayProjectionTest.kt`; provider, timer, runtime ownership and public
contracts remain read-only.

## Canonical SDD coverage

Applicable concerns reuse the registered subject specs. The Main Display
geometry/visual-QA concern reuses the existing subject-based canonical contract
(not a feature-ID hub); no parallel canonical path or behavior example is
required.

| Concern | Action | Canonical basis | Planning reason |
|---|---|---|---|
| Main Display geometry and visual QA | `reuse` | [Main Display Presentation](../../.memory-bank/contracts/main-display-presentation.md) | The registered subject spec already owns normalized shell geometry, band ratio and macro visual proof; W34 reuses it for the same FT-001-AC-002 outcome. |
| Deployable architecture and capability ownership | `reuse` | [System Architecture](../../.memory-bank/architecture/system-architecture.md#architecture-spine), `AD-001`, `AD-003`, `AD-005` | Main Display remains the orchestration owner; the composition root only wires the runtime. |
| Accepted module inventory and graph | `reuse` | [Boundary Map](../../.memory-bank/contracts/boundary-map.md#modules), [Dependency Graph](../../.memory-bank/contracts/boundary-map.md#dependency-graph) | All task-relevant units and edges already have registered identities. |
| Main Display consumer contracts | `reuse` | [Capability Interfaces](../../.memory-bank/contracts/capability-interfaces.md#main-display-to-weather-context), [Timer and Alert](../../.memory-bank/contracts/capability-interfaces.md#main-display-to-timer-and-alert), [Forecast Sessions](../../.memory-bank/contracts/capability-interfaces.md#main-display-to-forecast-sessions), [Settings and Location](../../.memory-bank/contracts/capability-interfaces.md#main-display-to-settings-and-location) | The shell renders projections and delegates gestures; it does not own neighbor state. |
| Device time, window flags and network-sensitive colon mode | `reuse` | [Platform Runtime](../../.memory-bank/contracts/platform-runtime.md#display-runtime-boundary) | The accepted Android boundary already defines device-time and fullscreen policy. |
| Four-card shell and ownership split | `reuse` | [FT-001 AC-002](../../.memory-bank/features/FT-001-main-clock-display.md#ft-001-ac-002-main-display-composition), [Main Display → Weather Context](../../.memory-bank/contracts/capability-interfaces.md#main-display-to-weather-context) | FT-001 owns exactly four ordered positions and composition; FT-002 retains weather data/content/freshness/palette ownership. |
| Main Display ticker ownership and lifecycle gating | `reuse` | [Platform Runtime](../../.memory-bank/contracts/platform-runtime.md#display-runtime-boundary), [System Architecture AD-003](../../.memory-bank/architecture/system-architecture.md#ad-003---cross-slice-orchestration-stays-in-a-capability-owner), [Boundary Map](../../.memory-bank/contracts/boundary-map.md#dependency-graph) | Activity lifecycle forwarding and one local scheduler owner fit the accepted runtime/wiring boundary; no new contract, edge, dependency or module is needed. |
| Local weather-card render reuse | `reuse` | [FT-001 AC-002](../../.memory-bank/features/FT-001-main-clock-display.md#ft-001-ac-002-main-display-composition), [Main Display → Weather Context](../../.memory-bank/contracts/capability-interfaces.md#main-display-to-weather-context) | Main Display may retain the last rendered read-model presentation; Weather Context keeps cache, refresh and projection ownership. W13 does not cross into Weather Context implementation. |
| Weather Context display-ready projection snapshot and invalidation | `reuse` | [Local Data — FT-002 Weather Context Records](../../.memory-bank/domains/local-data.md#ft-002-weather-context-records), [Weather Provider — Cache, History and Refresh Rules](../../.memory-bank/contracts/weather-provider.md#cache-history-and-refresh-rules), [Lifecycle Map — Weather Freshness Contract](../../.memory-bank/states/lifecycle-map.md#weather-freshness-contract), [Main Display → Weather Context](../../.memory-bank/contracts/capability-interfaces.md#main-display-to-weather-context) | TD-W13-001 is satisfied inside the existing Weather Context owner; no new state boundary, public contract, edge, event or dependency is needed. |
| Minimal Settings destination/return seam | `reuse` | [Main Display → Settings & Location](../../.memory-bank/contracts/capability-interfaces.md#main-display-to-settings-and-location) | Main Display owns gesture intent; Settings & Location owns the destination surface and state. No new edge or public contract is added; if the accepted boundary is insufficient, route to `/spec-design`. |
| Proof route for W13 | `reuse` | [Testing Strategy](../../.memory-bank/testing/strategy.md#risk-based-checks), [Runtime Verification](../../.memory-bank/testing/runtime-verification.md#deterministic-host-side-checks) | Host scheduler/projection probes, build and static gates prove the bounded debt; W13 does not plan target-device evidence. Historical W2/W11/W12 device evidence remains unchanged. |
| Proof route for W14 | `reuse` | [Testing Strategy](../../.memory-bank/testing/strategy.md#risk-based-checks), [Runtime Verification](../../.memory-bank/testing/runtime-verification.md#deterministic-host-side-checks), [Tier Policy](../../.memory-bank/workflows/tier-policy.md#claim-linked-red--green-for-t2t3) | Counting in-memory cache/projection fixtures prove reuse and accepted invalidation; clean build, host unit suite and static diff are sufficient. No emulator or target-device evidence is in scope. |
| Supplementary generic-emulator layout evidence | `reuse` | [Supplementary Local Emulator Target](../../.memory-bank/testing/runtime-verification.md#supplementary-local-emulator-target), [Reviewer evidence](../RUNTIME-VERIFICATION/tecno-pova-6-api35-review.md) | The accepted emulator route can prove actual Android View bounds and Settings reachability without promoting a Samsung/custom-ROM claim. |
| W21 Main Display composition geometry | `reuse` | [FT-001 AC-002](../../.memory-bank/features/FT-001-main-clock-display.md#ft-001-ac-002-main-display-composition), [Boundary Map](../../.memory-bank/contracts/boundary-map.md#dependency-graph), [Display Runtime Boundary](../../.memory-bank/contracts/platform-runtime.md#display-runtime-boundary) | The requested placement is inside Main Display's existing owner and graph; no new spec, edge, dependency or behavior example is required. |
| W21 card order, relative size and presentation preservation | `reuse` | [Weather Card Presentation](../../.memory-bank/contracts/weather-card-presentation.md#display-ready-card-contract), [FT-002 AC-001](../../.memory-bank/features/FT-002-weather-cards-context.md#ft-002-ac-001-ordered-card-projection), [Main Display → Weather Context](../../.memory-bank/contracts/capability-interfaces.md#main-display-to-weather-context) | W21 changes only placement/allocation; Weather Context retains slot identity, content, freshness, palette and day/night ownership. |
| W21 visual/layout proof | `reuse` | [Deterministic host-side checks](../../.memory-bank/testing/runtime-verification.md#deterministic-host-side-checks), [Target-device evidence](../../.memory-bank/testing/runtime-verification.md#target-device-evidence), [T3 RED/GREEN](../../.memory-bank/workflows/tier-policy.md#claim-linked-red--green-for-t2t3) | Host geometry/static checks are mandatory; target Samsung/custom-ROM/1280×720 readability remains DEFERRED when unavailable. |
| W24 clock dominance and circular control geometry | `reuse` | [FT-001 AC-002](../../.memory-bank/features/FT-001-main-clock-display.md#ft-001-ac-002-main-display-composition), [Boundary Map](../../.memory-bank/contracts/boundary-map.md#dependency-graph), [Display Runtime Boundary](../../.memory-bank/contracts/platform-runtime.md#display-runtime-boundary) | The visual correction remains Main Display-owned; relative measured bounds, effective radius and a named rubric prove the reference match without a new subject spec or absolute product target. |
| W24 timer/card preservation and visual proof | `reuse` | [Capability Interfaces](../../.memory-bank/contracts/capability-interfaces.md#main-display-to-timer-and-alert), [Lifecycle Map](../../.memory-bank/states/lifecycle-map.md#timer-state-contract), [Weather Card Presentation](../../.memory-bank/contracts/weather-card-presentation.md#display-ready-card-contract), [T3 RED/GREEN](../../.memory-bank/workflows/tier-policy.md#claim-linked-red--green-for-t2t3) | Existing right-side handlers, timer lifecycle/audio owners and FT-002 card projection remain regression-only; host gates and deferred target evidence are sufficient. |
| W26 idle clock and preset visual treatment | `reuse` | [FT-001 AC-002](../../.memory-bank/features/FT-001-main-clock-display.md#ft-001-ac-002-main-display-composition), [Boundary Map](../../.memory-bank/contracts/boundary-map.md#dependency-graph), [Display Runtime Boundary](../../.memory-bank/contracts/platform-runtime.md#display-runtime-boundary), [Capability Interfaces](../../.memory-bank/contracts/capability-interfaces.md#main-display-to-timer-and-alert) | The follow-up stays inside Main Display presentation and existing timer read/command boundaries; adaptive bounds, transparent circular controls, existing color identity and per-preset gradient distinction are visual proof concerns, not a new component contract. |
| W26 card hierarchy and proof route | `reuse` | [Weather Card Presentation](../../.memory-bank/contracts/weather-card-presentation.md#display-ready-card-contract), [Main Display → Weather Context](../../.memory-bank/contracts/capability-interfaces.md#main-display-to-weather-context), [Testing Strategy](../../.memory-bank/testing/strategy.md#risk-based-checks), [Runtime Verification](../../.memory-bank/testing/runtime-verification.md#deterministic-host-side-checks), [T3 RED/GREEN](../../.memory-bank/workflows/tier-policy.md#claim-linked-red--green-for-t2t3) | Main Display changes only composition geometry; Weather Context retains card data/content/freshness/palette/day-night/pressure ownership. Same-size host visual evidence and a named rubric are sufficient; target runtime remains DEFERRED. |
| W29 density-safe clock and landscape host proof | `reuse` | [FT-001 AC-002](../../.memory-bank/features/FT-001-main-clock-display.md#ft-001-ac-002-main-display-composition), [Display Runtime Boundary](../../.memory-bank/contracts/platform-runtime.md#display-runtime-boundary), [Runtime Verification](../../.memory-bank/testing/runtime-verification.md#deterministic-host-side-checks), [T3 RED/GREEN](../../.memory-bank/workflows/tier-policy.md#claim-linked-red--green-for-t2t3) | The correction stays in Main Display presentation. Two exact landscape host sizes prove available-space adaptation and no clipping/overflow; existing fullscreen/keep-screen-on policy remains read-only and target/device evidence is DEFERRED. |
| W29 four-slot NO_DATA/async/populated preservation | `reuse` | [Main Display → Weather Context](../../.memory-bank/contracts/capability-interfaces.md#main-display-to-weather-context), [Weather Card Presentation](../../.memory-bank/contracts/weather-card-presentation.md#display-ready-card-contract), [Runtime Verification redacted fixtures](../../.memory-bank/testing/runtime-verification.md#redacted-integration-fixtures) | Main Display keeps the existing four-slot shell while consuming the existing read model. NO_DATA, async refresh and populated redacted-fixture cases do not authorize WeatherCapability/provider or freshness changes. |
| W29 radial rim/glow and regression proof | `reuse` | [FT-001 AC-002](../../.memory-bank/features/FT-001-main-clock-display.md#ft-001-ac-002-main-display-composition), [Main Display → Timer & Alert](../../.memory-bank/contracts/capability-interfaces.md#main-display-to-timer-and-alert), [Runtime Verification](../../.memory-bank/testing/runtime-verification.md#target-device-evidence), [T3 RED/GREEN](../../.memory-bank/workflows/tier-policy.md#claim-linked-red--green-for-t2t3) | Existing circular controls and the Timer boundary are preserved while host receipts prove radial preset-color shading, wider rim, static fading glow, touch/active contracts and honest device deferral. No new visual spec, module or resource pipeline is needed. |

Feature frontmatter remains `spec_design_status: complete` with its existing
canonical links. No `needed_before_tasks` Backbone row remains.

## Scope boundary

In scope: the Main Display shell, `HH:mm` and Russian date formatting from the
device timezone, accepted online/offline/countdown colon state projection,
the W21 city/date-left and clock-central composition with exactly four ordered
weather-card positions, stable preset zones, and city
short/long-hold routing through the minimal accepted Settings destination and
return seam. FT-002 owns weather data/content/freshness/palette.

Out of scope: weather provider mapping, weather history, card data and content.
W14's internal snapshot invalidation preserves, but does not re-own, existing
Weather Context freshness and projection semantics. Other out-of-scope items
remain:
hourly or long-term forecast sessions, preset configuration, countdown lifecycle
and overdue alert, offline location catalog, API-key settings, validation,
personalization preview and any new UI controls. The minimal accepted Settings
destination/return seam is the only Settings surface in FT-001; the remaining
Settings behavior remains downstream FT-008/FT-009 concern.

The W11 follow-up narrows this scope to FT-001-AC-002 and FT-001-AC-005:
non-zero city/transient-row allocation and the existing city-to-Settings route
on the documented generic emulator. Existing timer-hint and forecast-message
semantics remain owned by their original features; their populated bounds are
only regression guards for the shared layout. Foundation probe controls are a
conditional same-correction gate, not a new FT-000 outcome.

The W12 repair owns only the bounded `FT-001-AC-005`
city-hold/Settings-preservation acceptance delta. It exercises the existing
downstream protected-cancellation contract (REQ-013; regression guard only;
canonical basis in TASK-015 `normative_inputs`) through the non-city
weather-card single/double path to prevent cross-feature regression; this is not
a W12 acceptance locator, a new FT-006 task, or an FT-006 lifecycle change. It
must prove that path through actual generic Android event dispatch while
preserving city double/no-delayed-Settings, empty/selected city taps, preset
interactions, overdue dismissal and safe cleanup. This does not reopen or
rewrite TASK-014's failed historical layout/reachability record. Host stream
tests support the dispatcher but never stand in for Android touch reachability.
Samsung/custom-ROM/1280x720 remains deferred.

The W13 debt task owns only the local Main Display ticker outcome: one
idempotent scheduler owner, pause/resume and attach/detach gating through the
existing Activity/platform seam, and no repeated weather-card view-tree
rebuild when the existing read-model presentation is unchanged. Its task-owned
feature deltas are `FT-001-AC-002`, `FT-001-AC-003` and `FT-001-AC-004` with
`REQ-002`, `REQ-003` and `REQ-022`; the colon/countdown path is a regression
guard and Timer & Alert remains the owner. No Yandex, Weather Context cache
cadence, timer/audio ownership, gesture semantics, Forecast-wide optimization,
target-device evidence or new architecture/contract is included.

The W14 follow-up owns only the TD-W13-001 upstream cost: repeated scalar reads
must reuse the existing capability-owned display-ready Weather Context snapshot,
while accepted successful refresh/location and existing projection/freshness
boundaries rebuild it as needed. Its exact task-owned feature locator is
`FT-001-AC-002 / REQ-002`; `REQ-007`, `REQ-022` and `REQ-025` are governing
weather/time/failure constraints and regression guards. The hard write boundary
is `WeatherCapability.kt` plus the existing `WeatherContextTest.kt`. W14 does
not change W13's ticker, public read contract, Weather Context ownership,
provider, Forecast, Timer/audio, gestures, target-device evidence, scheduler
state or Planning Revision.

### W21 Main Display composition geometry boundary

- Own only the requested Main Display composition delta under
  `FT-001-AC-002`: city/date above Yesterday in the left column; the large
  idle `HH:mm` in the central/upper area above Today/Tomorrow/Day-after; and
  the existing three circular preset controls on the right.
- Preserve the exact four-slot order and the existing Weather Card
  Presentation ownership. Today must have strictly larger measured allocation
  than the three equal smaller non-today cards; all inter-card gaps must be
  uniform and greater than the current 8dp baseline. Do not add day labels or
  alter card data, freshness, palette, day/night, pressure, or forecast entry
  semantics.
- Preserve fullscreen/landscape, device-time clock/date, city/settings route,
  timer/countdown/overdue behavior and existing public capability edges. Main
  Display remains the sole orchestration owner; no Activity/composition-root
  business logic, new module, dependency, event path or public contract is
  allowed.
- The task is T3 because it changes production Android runtime geometry and
  must carry target-readability risk honestly. Numeric dp/ratio choices are
  execution details only while they satisfy the relational contract; a new
  absolute product choice routes to `/feature-doctor FT-001`.

### W24 Main Display clock/control visual boundary

- Own only the Main Display visual correction under `FT-001-AC-002`: idle
  `HH:mm` gains clear reference-aligned visual dominance in the central/upper
  region, and the existing three right-side preset controls become true circles.
- Preserve the existing control order, labels, colors, selected/active states,
  touch routing and Timer & Alert semantics. Prove equal width/height and a
  common effective radius equal to half the diameter; do not select an
  absolute product dp/ratio.
- Preserve the W21 four-card order, Today-versus-three-equal-smaller relation,
  gaps and Weather Context ownership. Do not touch TimerCapability, audio,
  Platform Runtime, W23 paths, resources, neighbor owners or public contracts.
- T3 proof is fresh host RED/GREEN with measured clock/control bounds, a
  same-size contact sheet and named Reviewer/visual-QA rubric. Samsung/custom-
  ROM 1280x720 runtime readability and circle rendering remain `DEFERRED`.

### W26 Main Display idle visual hierarchy and card spacing boundary

- Own only the new Main Display visual detail under `FT-001-AC-002`: more
  spacious transparent right-side preset circles with one distinct neon
  gradient border per preset and preserved existing color identity; a
  substantially larger/adaptive idle `HH:mm` using available central space;
  equal Yesterday/Tomorrow/Day-after allocation; approximately 20% smaller
  non-Today cards relative to Today; and larger uniform inter-card gaps.
- Preserve preset order, labels, selected/active styling and touch routing;
  four-card order/content/freshness/palette/day-night/pressure ownership;
  device-time clock/date; and all Timer & Alert semantics. Active countdown and
  overdue are regression-only and remain later FT-006/FT-007 scope.
- No fixed product dp, ratio or gradient stops are selected. If a decisive
  result requires one, stop and route to `/feature-doctor FT-001`; a new owner,
  module, edge, public contract or dependency routes to `/spec-design`.
- Hard product/test boundary is exactly `DisplayCapability.kt` and
  `DisplayProjectionTest.kt`. Fresh claim-linked host RED/GREEN, a same-size
  contact sheet and named visual rubric are mandatory. Samsung/custom-ROM
  1280x720 runtime evidence remains `DEFERRED`; no emulator/device/adb/network
  action is authorized.

### W29 Main Display density-safe landscape and slot/preset visual boundary

- Own only the post-W28 Main Display visual correction under
  `FT-001-AC-002`: the full `HH:mm` must fit without clipping/overflow at
  `2460×1080` and `1280×720`; the four ordered `yesterday`/`today`/
  `tomorrow`/`day_after` slots remain visible in honest `NO_DATA`, async
  refresh and populated redacted-fixture states; and each existing circular
  preset uses one preset-color radial shade gradient, a materially wider rim
  than the fresh baseline and a static outward-fading neon glow.
- Preserve preset order, labels, existing colors, selected/active styling,
  touch routing and all Timer & Alert semantics. Weather Context/provider keeps
  data, availability, freshness, cache/history and card presentation
  ownership; no missing value is synthesized and no slot is removed or
  reordered.
- Use only measured relational evidence; do not select fixed dp, font, ratio,
  rim width or gradient stops. If a decisive visual/product choice is needed,
  route to `/feature-doctor FT-001`; an owner/boundary change routes to
  `/spec-design`.
- The hard product/test boundary is exactly `DisplayCapability.kt` and
  `DisplayProjectionTest.kt`. WeatherCapability, FoundationRuntime, adapters,
  Settings, Timer, resources and MainActivity are read-only. Target/device and
  physical-device evidence remains `DEFERRED`; no emulator, device, adb,
  runtime, network or credential action is authorized in this plan.

### W29 provenance reconciliation and W30 recovery route

- The independent fresh W29 review returned `NEEDS-CLARIFICATION` with a
  semantic concern, and the bounded evidence-recovery handoff returned
  `HANDOFF_BLOCKED_FOR_PROVENANCE`. The missing pre-write RED/executor summary
  is an authority/provenance gap; no product semantic failure is evidenced.
- Reconcile `TASK-032-T3-FT-001-W29` as `blocked` under scheduler failure
  policy. Preserve its current two-file worktree diff, every W29 report and
  every W29 protocol/task-local artifact. Do not manufacture RED, reuse W26/W28
  evidence, or infer a fourth W29 execution.
- Create exactly one replacement `TASK-033-T3-FT-001-W30` as a planned T3
  task. It is sequential in wave order but depends directly on the last
  successful `TASK-031-T3-FT-007-W28`, not on blocked W29. W29 remains visible
  provenance history and is not treated as a product prerequisite.
- W30 starts from the current worktree baseline with a fresh task-specific RED
  probe at both exact host sizes before any behavior write. If the baseline is
  already claim-equivalent GREEN, W30 may record the explicit accepted
  `RED_NOT_APPLICABLE` reason and make no production/test behavior write;
  otherwise only the exact `DisplayCapability.kt` plus
  `DisplayProjectionTest.kt` boundary may change. W30 covers full unclipped
  `HH:mm`, four ordered slots under `NO_DATA`/partial/populated fixtures, one
  preset-color radial gradient, a materially wider rim and three static fading
  glow layers while preserving ownership, touch, provider and lifecycle.
- Target/device/runtime remains `DEFERRED` unless separately authorized. No
  W29/W28 evidence may be reused as W30 RED/GREEN.

## W29 proof route

- Fresh host RED/GREEN is required at exactly `2460×1080` and `1280×720` for
  complete `HH:mm` bounds/no clipping, four slots in `NO_DATA`/async refresh/
  populated redacted-fixture states, and preset radial/rim/glow receipts.
- Required gates are `./gradlew clean assembleDebug`, focused
  `DisplayProjectionTest`, `./gradlew testDebugUnitTest`, `./gradlew lintDebug`
  and `git diff --check`, plus a named Reviewer/visual-QA rubric and scoped
  boundary review. Target/device/runtime evidence is `DEFERRED` under current
  authorization and cannot be promoted from host evidence.
- W29 owns no new feature AC or canonical spec. It reuses FT-001-AC-002 and
  the existing Main Display, Weather Card, Timer & Alert, Platform Runtime and
  Runtime Verification contracts; W26/W28 terminal records remain read-only.

## W31 physical visual proof route

- W31 is one sequential Main Display-owned T3 task after terminal W30. Its
  task-owned claim remains `FT-001-AC-002`, constrained by `REQ-001`,
  `REQ-002`, `REQ-005` and `REQ-023`; no new canonical spec, behavior-spec,
  module, graph edge, public contract or provider/runtime owner is required.
- The exact hard write boundary is `DisplayCapability.kt` plus
  `DisplayProjectionTest.kt`. Fresh host probes support the actual physical
  landscape size and `1280×720`, measuring clock/icon/card bounds and the
  four-slot matrix. Fresh physical RED/GREEN on the connected, unlocked TECNO
  LI6 serial `1156725456009666` is required for the visual claim; launch/health
  smoke alone is insufficient and W30 host evidence is not reused as W31
  proof.
- GREEN must show complete contained `HH:mm` dominance, materially reduced
  secondary weather icons, left city/date above Yesterday, stable ordered
  four-slot cards and separate right-side timer controls. No fixed dp/font/
  ratio/icon target is selected; unresolved numeric product choice routes to
  `/feature-doctor FT-001`. Emulator/AVD/QEMU is forbidden.

## W32 Main Display composition contract and paused upload route

- The operator accepted the numeric composition target: weather-card band
  25–30% of total landscape height and clock zone 70–75%. All four cards
  are equal height and bottom-aligned; Yesterday is never taller.
- The new subject-based canonical spec
  [Main Display Presentation](../../.memory-bank/contracts/main-display-presentation.md)
  defines normalized left/central/bottom/right regions, maximum-fit HH:mm
  measurement, secondary illustration ratios, timer-rail rules, tolerances,
  visual-QA rubric and claim-linked RED/GREEN evidence.
- Create exactly one new sequential T3 task
  TASK-035-T3-FT-001-W32, planned after done W31. Its hard boundary is
  exactly DisplayCapability.kt and DisplayProjectionTest.kt. W29/W30/W31
  identity, status, evidence and history remain read-only.
- Host proof is required at 2460×1080 and 1280×720. The later route stops
  immediately before adb install/upload; physical/runtime evidence remains
  DEFERRED until a separate operator authorization. Next owner is fresh
  /review-tasks-plan FT-001.

## W32 physical failure and W33 blocked history

- W32 physical evidence confirms a task-local Main Display defect not visible
  in its pure host geometry: with Yesterday empty and 14/15/16 populated, the
  real View tree leaves `leftHeader` as `WRAP_CONTENT` and
  `yesterdayCard` as `MATCH_PARENT` with `weight=1` in a separate left
  container, while `bindWeatherCards` binds the other three cards elsewhere.
  The result is an oversized empty Yesterday shell and a bottom band containing
  the populated cards.
- W32 is now `failed` by scheduler disposition after the authorized physical
  smoke exposed a real View-tree defect that host pure geometry did not prove:
  empty Yesterday remains a tall separate weighted shell while populated 14/15/16
  occupy the bottom band. Its host PASS/semantic-pass and failure evidence remain
  preserved in the W32 card.
- W33 remains `blocked` with its original ID, wave, dependency and mixed-state
  repair semantics. Its handoff is minimally repaired with direct existing
  canonical SDD paths; it remains history and is not reopened or promoted.

## W34 recovery successor

- The planning route created exactly one T3 task,
  `TASK-037-T3-FT-001-W34`, with the exact same two-file boundary and
  mixed-state repair outcome as the blocked W33 route. W34 depends only on
  successful `TASK-034-T3-FT-001-W31`, the last successful Main Display
  baseline; it deliberately does not depend on failed W32 and does not bypass
  blocked W33 history.
- W34 retained the accepted 25–30% weather band, 70–75% clock zone, four equal
  card heights/common bottom alignment, legitimate empty Yesterday shell and no
  value synthesis. Fresh host and authorized physical RED/GREEN on unlocked
  TECNO LI6 serial `1156725456009666` are now recorded; emulator/AVD/QEMU was
  forbidden and the planner performed no install/upload.
- WeatherCapability/provider, Timer & Alert, Android runtime, city/date and
  timer ownership remain regression constraints. No new canonical spec,
  behavior-spec, module, edge, dependency or Planning Revision is created.

## W34 boundary reconciliation

`TASK-037-T3-FT-001-W34` is `done` under the already-recorded scheduler
closure: executor `PASS_FOR_HANDOFF`, `/verify PASS`, T3 `/red-verify
semantic-pass`, all five host gates passing and fresh physical TECNO LI6
RED/GREEN on serial `1156725456009666`. The accepted evidence proves the
mixed empty-Yesterday/three-populated state at `2460×1080` and `1280×720`,
including the equal/common-bottom four-card allocation in the accepted band.

W31 remains `done`, W32 remains `failed`, and W33 remains `blocked`; W33's
attempted `blocked -> failed` transition remains preserved as superseded and
policy-invalid. W34 depends only on W31 and does not bypass or rewrite W32/W33.
The timer-digit sizing observation remains a separate FT-007 residual. FT-001
and EP-001 lifecycle/RTM values, Planning Revision `2`, scheduler checkpoint
and terminal handling are unchanged by this sync. See the [W34 sync report](../../.tasks/TASK-037-T3-FT-001-W34/TASK-037-T3-FT-001-W34-S-MB-SYNC-final-report-docs-01.md).

## W14 boundary reconciliation

`TASK-017-T3-FT-001-W14` is `done` and depends on terminal
`TASK-016-T3-FT-001-W13`. The completed boundary is independently proven in the
existing Weather Context host test: repeated projection calls reuse one
snapshot, while accepted successful refresh, validated location, selected-city
time, pressure and 24-hour freshness boundaries rebuild it. The current
four-card, day/night, date, pressure-trend, failure and stale-empty semantics
remain regression results; no feature AC or RTM lifecycle is changed. See the
[executor handoff](../TASK-017-T3-FT-001-W14/handoff.md),
[functional verification](../TASK-017-T3-FT-001-W14/verification.md),
[verifier-owned evidence](../../.tasks/TASK-017-T3-FT-001-W14/verifier-owned-evidence.md),
[durable semantic verification](../TASK-017-T3-FT-001-W14/red-verification.md)
and [semantic report](../../.tasks/TASK-017-T3-FT-001-W14/TASK-017-T3-FT-001-W14-S-RED-VERIFY-final-report-docs-01.md).

Historical W2 `done`, W11 `failed`, W12 `done` and W13 `done` records remain
unchanged. FT-001/EP-001 and FT-002/EP-002 lifecycle/RTM values, scheduler
checkpoint and terminal state remain unchanged; Planning Revision is reconciled
to `2`; target
device evidence remains `DEFERRED` with no runtime `PASS` claim.

## Handoff

Queue action is `reconciled`: W29 remains preserved terminal `failed` provenance
history, W30 and W31 remain terminal `done`, W32 remains scheduler `failed`,
W33 remains `blocked`, and W34 is `done` as the recovery successor depending
on W31. No historical task/status/checkpoint/terminal state is changed and no
production code is edited by this sync. The scheduler's next owner is its
caller-owned strict post-sync gate followed by the documented dependency halt;
no promotion or dependent unblock is performed here.
