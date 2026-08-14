---
description: Implementation plan for the FT-001 main clock and display shell.
status: active
last_updated: 2026-08-14
---
# IMPL-FT-001 — Main clock and display shell

## Goal

Deliver the accepted always-visible Main Display outcome on top of the
Foundation Android scaffold: dominant device-time `HH:mm`, Russian date,
city/date above Yesterday, the large clock above the three day cards, exactly
four stable ordered weather cards, accepted colon states, and city gestures
that use the existing Settings boundary.

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
6. `TASK-024-T3-FT-001-W21` (`done`) — implement the operator-requested
   Main Display composition delta inside the existing Main Display owner:
   city/date above Yesterday in the left column, large idle `HH:mm` in the
   central/upper area above Today/Tomorrow/Day-after, and the three existing
   circular preset controls on the right. Preserve the four-slot order, Today
   larger than the three equal smaller cards, and use uniform inter-card gaps
   larger than the current 8dp baseline. W21 depends on terminal
   `TASK-023-T3-FT-002-W20`; it is one T3 task, not a split by surface or test
   type, and does not change timer, fullscreen or weather semantics.
7. `TASK-027-T3-FT-001-W24` (`done`) — apply the bounded reference-driven
   visual follow-up after terminal W21/W22/W23 history: make idle `HH:mm`
   visually dominant and make the three existing right-side preset controls
   true circles. Use fresh host-measured clock bounds, equal square
   width/height/common half-diameter radius proof, a same-size RED/GREEN
   contact sheet and named visual rubric. Depend on terminal
   `TASK-026-T3-FT-007-W23`; keep TimerCapability/audio/W23 paths untouched,
   preserve the Foundation dependency transitively, and do not select a new
   absolute dp/ratio.
8. `TASK-029-T3-FT-001-W26` (`done`) — applied the bounded post-terminal
   visual refinement after terminal `TASK-028-T3-FT-002-W25`: make idle
   `HH:mm` substantially larger and adaptive within available central space;
   make the existing right-side preset controls spacious transparent circles
   with distinct per-preset neon gradient borders and preserved existing color
   identity; keep Yesterday equal to Tomorrow/Day-after; keep the three smaller
   cards about 20% smaller than Today; and use larger uniform weather-card
   gaps. Use fresh claim-linked host RED/GREEN geometry, a same-size contact
   sheet and named visual rubric. Active countdown/overdue behavior remains
   outside this task for later FT-006/FT-007 work. Depend on W25; keep all
   historical task evidence and the Foundation dependency unchanged, and did
   not select fixed dp, ratio or gradient-stop targets. W26's terminal
   identity/evidence remain historical input for the next follow-up.
9. `TASK-032-T3-FT-001-W29` (`failed`) — preserve the post-W28 Main Display
   provenance attempt exactly as history. Its independent review
   `NEEDS-CLARIFICATION`/`semantic-concern` and executor
   `HANDOFF_BLOCKED_FOR_PROVENANCE` show a missing W29 pre-write RED/executor
   summary, not a proved product semantic failure. Preserve the current
   two-file diff, all reports and task-local evidence; do not backfill RED or
   permit a fourth W29 execution. The scheduler disposition is terminal
   `failed` after the provenance gap could not be repaired honestly.
10. `TASK-033-T3-FT-001-W30` (`done`) — replaced the failed provenance attempt
   with one fresh sequential T3 task directly after terminal
   `TASK-031-T3-FT-007-W28`. The fresh task-specific RED probe at exactly
   `2460×1080` and `1280×720` accepted `RED_NOT_APPLICABLE` because the current
   baseline was already claim-equivalent GREEN; no production/test behavior
   write was made. Fresh verifier-owned receipts prove the full unclipped
   `HH:mm`, all four ordered slots under `NO_DATA`/partial/populated fixtures,
   one-color radial preset shading, a materially wider rim and three static
   fading glow layers. The exact two-file boundary, ownership, touch, provider
   and lifecycle semantics remain unchanged, and target/device/runtime remains
   `DEFERRED`. See the task-owned [W30 sync report](../../../.tasks/TASK-033-T3-FT-001-W30/TASK-033-T3-FT-001-W30-S-MB-SYNC-final-report-docs-01.md).
11. `TASK-034-T3-FT-001-W31` (`done`) — completed the physical Main Display
   clock/icon geometry follow-up after W30. Executor `PASS_FOR_HANDOFF`, fresh
   physical TECNO LI6 RED/GREEN at `2460×1080`, independent functional `PASS`,
   T3 `semantic-pass` and all five host gates are recorded in the task evidence.
   The exact two-file boundary remains `DisplayCapability.kt` and
   `DisplayProjectionTest.kt`; host geometry at `2460×1080` and `1280×720` is
   supporting evidence only. Other resolutions/devices, custom-ROM rendering,
   physical audio audibility and live provider refresh remain outside W31.
   See the task-owned [W31 sync report](../../../.tasks/TASK-034-T3-FT-001-W31/TASK-034-T3-FT-001-W31-S-MB-SYNC-final-report-docs-01.md).

## Primary owner and accepted graph

12. `TASK-035-T3-FT-001-W32` (`failed`) — recompose the existing Main
    Display shell under the accepted Main Display Presentation contract:
    weather band 25–30% of total landscape height, clock zone 70–75%, four
    equal-height/bottom-aligned cards, maximum-fit complete HH:mm, secondary
    illustrations and separate circular timer rail. It depends on W31 and
    writes only `DisplayCapability.kt` plus `DisplayProjectionTest.kt`.
    Host RED/GREEN is required at 2460×1080 and 1280×720. The later route
    stops immediately before adb install/upload; physical/runtime evidence
    remains DEFERRED.
13. `TASK-036-T3-FT-001-W33` (`blocked`) — preserve the confirmed mixed-state
    real View allocation defect after W32: an empty Yesterday must share the
    same compact bottom-band allocation as populated today/tomorrow/day_after
    cards, with equal heights and common bottom alignment. Add the deterministic
    host mixed fixture and an allocation-level receipt when the existing
    project-native harness can expose one within the same two-file boundary.
    Require fresh physical RED/GREEN on unlocked TECNO LI6 serial
    `1156725456009666` after authorized build/install, with screenshots and
    measured card bounds. W33 remains dependent on failed W32 and is preserved
    as blocked history; it does not change Weather Context/provider, Timer &
    Alert, runtime or data ownership.
14. `TASK-037-T3-FT-001-W34` (`done`) — recovered the same mixed-state repair
    from successful W31 rather than bypassing history: empty Yesterday shares
    one real View allocation and the equal compact bottom band with populated
    today/tomorrow/day_after cards. The exact boundary remains
    `DisplayCapability.kt` plus `DisplayProjectionTest.kt`; fresh host and
    authorized physical RED/GREEN on TECNO LI6 are recorded. W34 depends only
    on done W31, while failed W32 and blocked W33 remain preserved records;
    oversized timer-digit sizing remains a separate FT-007 residual.

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
- Stable city/date and exactly four weather-card positions: city/date sit in
  the left column above Yesterday, while the central/upper idle clock sits
  above the Today/Tomorrow/Day-after row; the preset-button zone remains on
  the right and does not shift when weather data is absent. FT-002 retains
  weather data, card content, freshness and weather-specific presentation
  ownership.
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

### W21 Main Display composition geometry boundary

- Own only the operator-requested composition delta in `DisplayCapability.kt`:
  left-column city/date above Yesterday, large idle `HH:mm` in the
  central/upper area above Today/Tomorrow/Day-after, and the existing three
  circular preset buttons on the right.
- Keep the four slot identities in `yesterday`/`today`/`tomorrow`/`day_after`
  order. Today must have strictly larger measured allocation than the three
  equal smaller non-today cards; all inter-card gaps must be uniform and larger
  than the current 8dp baseline. No day labels or card-content redesign is
  authorized.
- Preserve fullscreen/landscape, device-time clock/date, city/settings,
  timer/countdown/overdue, forecast-entry and weather projection semantics.
  Main Display remains the orchestration owner and the existing capability
  graph is reused without a new module, edge, dependency, event path or public
  contract.
- This is one T3 task with the hard boundary carried by its indexed card. If an
  absolute dp/ratio is needed as a new product choice for “slightly”, stop and
  route to `/feature-doctor FT-001`; a boundary/owner change routes to
  `/spec-design`.

### W24 Main Display clock/control visual boundary

- Own only the reference-driven visual detail under `FT-001-AC-002`: idle
  `HH:mm` becomes the dominant central/upper focal element and the existing
  three right-side preset controls become true circles.
- Preserve control order, labels, colors, selected/active styling, touch routing,
  timer semantics, W21 card geometry and FT-002 projection ownership. Prove
  equal width/height and a common effective radius of half the diameter using
  measured host bounds; do not invent an absolute product target.
- Hard product/test boundary: `DisplayCapability.kt` and
  `DisplayProjectionTest.kt` only. TimerCapability, audio/Platform Runtime,
  Weather Context, resources, W23 paths and target/device state are forbidden.

### W26 Main Display idle visual hierarchy and card spacing boundary

- Own only the new visual detail under `FT-001-AC-002`: spacious transparent
  right-side preset circles with one distinct neon gradient border per preset
  and preserved existing color identity; a substantially larger/adaptive idle
  `HH:mm` in available central/upper space; equal Yesterday/Tomorrow/Day-after
  allocation; approximately 20% smaller non-Today cards relative to Today; and
  larger uniform inter-card gaps.
- Preserve preset order, labels, selected/active styling and touch routing;
  device-time clock/date; four-card order/content/freshness/palette/day-night/
  pressure ownership; and all Timer & Alert semantics. Active countdown and
  overdue are regression-only and remain later FT-006/FT-007 scope.
- No fixed product dp, ratio or gradient stops are selected. If a decisive
  visual verdict requires one, stop at `/feature-doctor FT-001`; a new owner,
  module, edge, public contract or dependency routes to `/spec-design`.
- Hard product/test boundary is exactly `DisplayCapability.kt` and
  `DisplayProjectionTest.kt`; no resources, assets, emulator/device/adb,
  network, credentials or historical task changes are allowed.

### W29 Main Display density-safe landscape and slot/preset visual boundary

- Own only the Main Display visual correction under `FT-001-AC-002`: full
  `HH:mm` fitting with no clipping/overflow at `2460×1080` and `1280×720`,
  stable ordered `yesterday`/`today`/`tomorrow`/`day_after` shells for
  NO_DATA, partial and populated redacted-fixture cases, and one preset-color
  radial shade gradient with a materially wider rim plus three static
  outward-fading glow layers around each existing circle.
- Preserve preset order, labels, existing colors, selected/active styling,
  touch routing and Timer & Alert semantics. Weather Context/provider remains
  the sole owner of data, availability, freshness, cache/history, normalized
  values and weather-specific presentation; no missing value is synthesized.
- Use relational/measured proof only; no fixed dp, font, ratio, rim width or
  gradient stops. Hard write boundary is exactly
  `DisplayCapability.kt` and `DisplayProjectionTest.kt`. WeatherCapability,
  FoundationRuntime, adapters, Settings, Timer, resources and MainActivity are
  read-only; target/device/runtime is DEFERRED unless later separately
  authorized.

### W29 provenance outcome and W30 replacement boundary

W29 is reconciled as terminal `failed` for provenance, not as a product
semantic failure. The fresh
task-plan review was `APPROVE` for planning, but the later independent
functional review returned `NEEDS-CLARIFICATION`, semantic review returned
`semantic-concern`, and bounded recovery returned
`HANDOFF_BLOCKED_FOR_PROVENANCE` because no honest W29 pre-write RED or prior
executor summary exists. W26/W28 evidence cannot be promoted to W29 RED, and
the current two-file diff plus all W29 reports/task-local artifacts remain
preserved.

`TASK-033-T3-FT-001-W30` was the single replacement. It depends on terminal W28
directly so failed W29 is not treated as a runnable prerequisite.
W30's fresh task-specific probe at both exact host sizes accepted the explicit
`RED_NOT_APPLICABLE` route before any behavior write; verifier-owned geometry,
slot, preset, rubric and boundary receipts plus all five host gates support the
closure. The exact two-file behavior boundary remained unchanged. W30 proves
the full unclipped clock, four ordered slots for `NO_DATA`/partial/populated
fixtures, one-color radial preset shading, a materially wider rim and three
static fading glow layers, while preserving Main Display ownership, touch
routing, Weather Context/provider ownership and Timer & Alert lifecycle
semantics. Target/device/runtime remains `DEFERRED`; W26/W28/W29 evidence is
not W30 RED/GREEN. See the [W30 executor handoff](../../../.tasks/TASK-033-T3-FT-001-W30/TASK-033-T3-FT-001-W30-S-EXE-final-report-code-01.md),
[functional verification](../../../.tasks/TASK-033-T3-FT-001-W30/TASK-033-T3-FT-001-W30-S-VERIFY-final-report-docs-01.md),
[semantic verification](../../../.tasks/TASK-033-T3-FT-001-W30/TASK-033-T3-FT-001-W30-S-RED-VERIFY-final-report-docs-01.md)
and [sync report](../../../.tasks/TASK-033-T3-FT-001-W30/TASK-033-T3-FT-001-W30-S-MB-SYNC-final-report-docs-01.md).

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

For W21 the expected delta is only
`app/src/main/kotlin/com/hozayushka/app/display/DisplayCapability.kt` and the
existing `app/src/test/kotlin/com/hozayushka/app/DisplayProjectionTest.kt`.
No MainActivity, Weather Context, Timer & Alert, Forecast, Settings, resource,
provider or architecture file is planned.
For W24 the expected delta is the same two paths only. The task card carries a
non-empty hard `write_boundary`; no implementation may expand it.
For W26 the expected delta remains exactly the same two paths only. The task
card carries a non-empty hard `write_boundary`; no implementation may expand it.
For W29 the expected delta is exactly the same two paths only. The task card
carries a non-empty hard `write_boundary`; no implementation may expand it.
For W30 the expected delta remains exactly the same two paths only. The task
card carries a non-empty hard `write_boundary`; if fresh W30 baseline proof is
already claim-equivalent GREEN, no behavior write is permitted.

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
- For W21, clean build, full host unit suite and static diff are mandatory. A
  fresh pre-change RED must expose the current left-header/equal-card/8dp-gap
  geometry; GREEN host/static proof must distinguish the left/central/right
  regions, four slot order, Today-versus-non-today allocation and the increased
  uniform gap. Target 1280×720 readability, fullscreen and keep-screen-on are
  `DEFERRED` without an authorized target observation; no runtime `PASS` may be
  inferred.
- For W24, the same gates are mandatory. Fresh RED/GREEN must record actual
  clock bounds, right-side preset bounds and effective radius, then attach a
  same-size contact sheet and named visual rubric proving clock dominance,
  preserved anchors, no clipping/overlap and three equal circular controls.
  Samsung/custom-ROM 1280x720 runtime evidence remains `DEFERRED`; no host
  visual result becomes target runtime `PASS`.
- For W26, the clean build, focused `DisplayProjectionTest` suite, complete
  host suite and static diff gates are mandatory. Fresh claim-linked RED/GREEN
  must record actual adaptive clock,
  preset-circle/gradient/color, four-card relative-sizing and common-gap
  observations in a same-size contact sheet and geometry artifact. A named
  visual rubric must decide transparent controls, per-preset neon distinction,
  existing colors, clock focal hierarchy, no clipping/overlap, the operator's
  about-20%-smaller card intent and lightweight treatment without introducing
  fixed dp/ratio/gradient-stop targets. Samsung/custom-ROM 1280x720 runtime
  evidence remains `DEFERRED`; no host visual result becomes target runtime
  `PASS`, and no emulator/device/adb/network action is authorized.
- For W29, clean build, focused `DisplayProjectionTest`, full host unit suite,
  `lintDebug` and `git diff --check` were required by its historical card.
  Missing pre-write RED provenance is the reason W29 is terminal `failed`; its
  receipts are not promoted to W30.
- For W30, the same five project-native gates are recorded as exit `0`. The
  fresh task-specific RED probe at both exact sizes accepted
  `RED_NOT_APPLICABLE` before any behavior write, with full-clock bounds,
  four-slot `NO_DATA`/partial/populated redacted fixtures and preset receipts.
  Target/device evidence remains `DEFERRED`; host evidence cannot become
  runtime `PASS`.

## Claim-linked proof plan

The indexed FT-001 task surface covers all five FT-001 acceptance claims. Execution must first
record the current Foundation baseline honestly; an already-green part of a
claim is preserved and is not changed without need. The final proof is:

| Claim | Decisive result | Artifact |
|---|---|---|
| `FT-001-AC-001` | Landscape/fullscreen, system panels hidden, screen held on while open | Host/static result plus target-device notes/screenshot |
| `FT-001-AC-002 / REQ-002` | City/date are above Yesterday at left; idle `HH:mm` is central/upper above Today/Tomorrow/Day-after; presets remain right; four slots retain order; the weather band is 25–30% of frame height, all card shells have equal height/bottom alignment, and the clock zone is 70–75%. FT-002 owns data/content. | Fresh normalized host geometry and static/source boundary evidence; target screenshot/bounds only after upload pause is released, otherwise explicit DEFERRED residual-risk note |
| `FT-001-AC-003` | Device timezone drives clock/date and date has only `dd` plus Russian genitive month | Deterministic unit-test output |
| `FT-001-AC-004` | Online/offline/countdown colon state transitions match the accepted timing/brightness values | Deterministic state-test output; visual result only where device evidence is needed |
| `FT-001-AC-005` | Empty-city short tap and any-city hold reach the minimal accepted Settings destination; selected-city short tap is a no-op and Back returns to Main Display | Gesture/routing test output plus target-device interaction notes |

W24 adds no new claim locator: its `FT-001-AC-002 / REQ-002` proof is the
fresh measured clock/control bounds, same-size RED/GREEN contact sheet and
Reviewer/visual-QA rubric. The W21 card geometry and all timer/audio checks are
regression guards; they do not transfer ownership or historical evidence to W24.

W26 adds no new claim locator: its `FT-001-AC-002 / REQ-002` and
`FT-001-AC-002 / REQ-005` proof is fresh measured idle-clock/preset/card geometry,
per-preset transparent-gradient visual evidence, a same-size RED/GREEN contact
sheet and Reviewer/visual-QA rubric. Active countdown/overdue, timer/audio and
Weather Context checks remain regression guards; they do not transfer ownership
or historical evidence to W26. The about-20% wording remains relational
operator intent, not a newly invented fixed ratio.

W29 added no new feature AC. Its owned locator was `FT-001-AC-002 / REQ-002`,
with `REQ-001` and `REQ-023` as display policy/NFR constraints and `REQ-005`
as a read-only weather projection regression. W29 remains terminal `failed`
because its claim path lacks honest pre-write provenance; its reports and
current diff are historical evidence only.

W30 owns the same accepted locator and direct constraints. Its completed proof
is fresh and task-specific: full-clock bounds at both sizes, four slots under
`NO_DATA`/partial/populated fixtures, one-color radial preset treatment,
materially wider rim and three static fading glow layers. W26/W28/W29 evidence
cannot satisfy W30 RED/GREEN; W30 used the explicit fresh
`RED_NOT_APPLICABLE` alternative and made no behavior write.

W31 is the single sequential physical-visual follow-up after W30. W30's host
proof did not measure weather-icon footprint or establish physical focal
hierarchy, and its target record kept physical rendering `DEFERRED`; the later
unlocked TECNO smoke established launch/health and landscape visibility only.
W31 therefore owns fresh physical RED/GREEN on the connected, unlocked TECNO
LI6 serial `1156725456009666`, with host geometry at the actual recorded
landscape size and `1280×720` as supporting evidence. Its accepted outcome is
the same `FT-001-AC-002`: complete contained `HH:mm` is the largest readable
element, weather icons are materially reduced and secondary, city/date stays
above Yesterday, four ordered slots remain stable, and right-side timer
controls remain separate. The hard write boundary remains exactly
`DisplayCapability.kt` plus `DisplayProjectionTest.kt`; W30 evidence is not
promoted to W31 proof, emulator/AVD/QEMU is forbidden, and no provider,
Timer & Alert, runtime owner or public contract changes are authorized.

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
checkpoint and terminal state remain unchanged; Planning Revision is reconciled
to `2`; no
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
- Preserve the existing circular preset controls and all timer/countdown/overdue
  interaction behavior while changing only shell geometry.
 Preserve Weather Context's four-card projection semantics, including slot
  order, equal shell height/bottom alignment, any accepted Today width/density
  variation, card content/freshness, palette, day/night and pressure
  presentation; Main Display only composes the read model.
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
- [.memory-bank/contracts/main-display-presentation.md](../../contracts/main-display-presentation.md)
- [.memory-bank/contracts/platform-runtime.md](../../contracts/platform-runtime.md)
- [.memory-bank/testing/runtime-verification.md](../../testing/runtime-verification.md)
- [.memory-bank/invariants.md](../../invariants.md)
- [.memory-bank/constitution.md — bounded checkpoints and Definition of Done](../../constitution.md#iii-bounded-agent-autonomy-and-human-checkpoints)
- [.memory-bank/workflows/tier-policy.md — claim-linked T3 proof](../../workflows/tier-policy.md#claim-linked-red--green-for-t2t3)

## Handoff

W29 is preserved as terminal `failed` provenance history, W30 remains the
completed host-only replacement directly behind terminal W28, and W31 is the
completed physical visual follow-up directly after W30. The W30 boundary sync
and W31 boundary sync are recorded in the task-owned [W30 sync report](../../../.tasks/TASK-033-T3-FT-001-W30/TASK-033-T3-FT-001-W30-S-MB-SYNC-final-report-docs-01.md)
and [W31 sync report](../../../.tasks/TASK-034-T3-FT-001-W31/TASK-034-T3-FT-001-W31-S-MB-SYNC-final-report-docs-01.md);
no historical report, task-local artifact, lifecycle/status or current
two-file code diff is overwritten. Fresh `/review-tasks-plan FT-001` is the
next owner; W32 remains `failed`, W33 remains `blocked`, and W34 is the
completed recovery successor from done W31. The exact W34 closure is recorded
in the [W34 sync report](../../../.tasks/TASK-037-T3-FT-001-W34/TASK-037-T3-FT-001-W34-S-MB-SYNC-final-report-docs-01.md).
Scheduler-owned strict post-sync gates and dependency-halt handling remain
external; no promotion or dependent unblock is performed by this sync.
