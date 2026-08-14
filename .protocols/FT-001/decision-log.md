---
description: Decision log for FT-001 task decomposition.
status: active
last_updated: 2026-08-14
---
# FT-001 — Decision log

## 2026-08-14 — W34 completed recovery closure

- The already-recorded scheduler decision closes
  `TASK-037-T3-FT-001-W34` as `done` after executor `PASS_FOR_HANDOFF`,
  independent `/verify PASS`, T3 `/red-verify semantic-pass`, all five host
  gates and fresh physical TECNO LI6 RED/GREEN on serial `1156725456009666`.
- The accepted result is the existing mixed-state Main Display outcome: empty
  Yesterday shares the equal/common-bottom 25–30% weather band with populated
  today/tomorrow/day_after cards beneath the 70–75% clock zone. The exact
  two-file boundary and Weather Context / Timer & Alert read-only ownership are
  preserved; no new spec, edge, dependency or Planning Revision is created.
- W31 remains `done`, W32 remains `failed`, and W33 remains `blocked`. W33's
  attempted `blocked -> failed` transition remains preserved as superseded and
  policy-invalid; W34 is the separate successful recovery successor from W31.
- Oversized timer-digit sizing remains a separate future FT-007 presentation
  residual and is not reopened or mixed into W34. No feature/epic closure,
  promotion, scheduler checkpoint or terminal-state transition is decided by
  this sync.
## 2026-08-14 — W32 accepted macro composition contract

- The operator accepted a product-level visual target after inspecting the
  wide/compact main-screen references, timer sketch, icon-scale references
  and W31 physical screenshot: the weather-card band MUST be 25–30% of total
  landscape height and the clock zone MUST be the remaining 70–75%.
- The four cards remain in yesterday/today/tomorrow/day_after order, have
  equal height and common bottom alignment, and Yesterday MUST never be taller.
  City/date remains in the left column above Yesterday; the complete HH:mm is
  the maximum-fit central/upper hero; illustrations remain materially
  secondary; timer controls remain separate circular transparent controls with
  one-color radial neon rim/glow and spacing.
- The operator explicitly accepted only the 25–30% weather-card band, the
  remaining 70–75% clock zone, and the relational visual rules listed above as
  hard product rules. All other numbers previously drafted for horizontal
  columns, center offsets, illustration footprints/areas, clock-size ratios,
  pairwise gaps or similar geometry are reviewer measurement heuristics, not
  product requirements, and MUST NOT block implementation unless separately
  accepted. The pixel tolerance is only a declared raster measurement
  tolerance, not a product target.
- Existing subject specs did not own this normalized shell geometry and macro
  visual-QA method. Create and register the subject-based
  main-display-presentation.md contract; do not create an FT-001 tech-spec hub.
  Weather Card Presentation is clarified only for equal shell height; provider
  and Timer & Alert ownership remain unchanged.
- Create exactly one new sequential planned T3 task
  TASK-035-T3-FT-001-W32 after done W31. Its exact hard write boundary is
  DisplayCapability.kt plus DisplayProjectionTest.kt. Host RED/GREEN is
  required at 2460×1080 and 1280×720 with raw geometry measurements,
  qualitative/relational review and only the declared raster measurement
  tolerance.
- The later route stops immediately before adb install/upload. No production
  code, adb, emulator, lifecycle, scheduler checkpoint or terminal state is
  changed in this planning run. Next route: fresh /review-tasks-plan FT-001.

## 2026-08-14 — W33 confirmed mixed-state physical defect

- Read-only physical evidence in `.tasks/TASK-035-T3-FT-001-W32/physical-smoke.png`
  confirms a distinct mixed-state defect: an empty Yesterday shell occupies
  most of the height while populated 14/15/16 cards form the bottom band. The
  source diagnosis is the real Main Display View tree: `leftHeader` is
  `WRAP_CONTENT`, `yesterdayCard` is `MATCH_PARENT` with `weight=1` in a
  separate left container, and `bindWeatherCards` separates Yesterday from the
  other three cards. W32 pure geometry and W31 history do not prove this
  allocation claim and remain unchanged.
- WeatherCapability/provider behavior is not implicated. The empty day remains
  a legitimate no-data shell; no value synthesis, provider refresh, freshness,
  timer, runtime or ownership change is authorized.
- Create exactly one new indexed planned T3 task,
  `TASK-036-T3-FT-001-W33`, sequentially after and dependent on
  `TASK-035-T3-FT-001-W32` as its then-current predecessor. W33 owns the
  shared real View allocation correction, deterministic mixed-state host
  regression and project-native allocation receipt when feasible, plus fresh
  physical RED/GREEN on unlocked TECNO LI6 serial `1156725456009666` after
  authorized build/install. The screenshot and measured card bounds must prove
  equal-height/common-bottom cards in the accepted 25–30% band.
- Reuse the existing Main Display Presentation, Boundary Map, capability,
  platform and runtime-verification specs. No new canonical spec,
  behavior-spec, module, edge, dependency or Planning Revision is created.
  The exact hard write boundary is `DisplayCapability.kt` plus
  `DisplayProjectionTest.kt`; emulator/AVD/QEMU remains forbidden and planner
  does not install/upload an APK.

## 2026-08-14 — W32 failure recovery route

- Scheduler disposition is now authoritative: `TASK-035-T3-FT-001-W32` is
  `failed` after physical smoke exposed the real View-tree mixed-state defect.
  Its prior host `PASS`/semantic-pass and failure evidence remain preserved;
  W32 is not retried or rewritten.
- `TASK-036-T3-FT-001-W33` remains `blocked` on failed W32. Its semantic
  identity, ID, wave, dependency, status and evidence/history are preserved;
  only its single-card handoff is minimally repaired with direct existing SDD
  file links so strict structural checks can recognize the applicable Main
  Display, boundary, capability, runtime and target-verification contracts.
- Create exactly one new planned recovery successor,
  `TASK-037-T3-FT-001-W34`, with the same mixed-state outcome and exact
  `DisplayCapability.kt` plus `DisplayProjectionTest.kt` boundary. W34 depends
  only on successful `TASK-034-T3-FT-001-W31`; this uses the last successful
  baseline without bypassing failed W32 or blocked W33 history.
- W34 requires fresh host and authorized physical RED/GREEN on TECNO LI6;
  emulator/AVD/QEMU, WeatherCapability/provider/data synthesis, timer/runtime
  drift and FT-007 timer-digit sizing remain out of scope. Reuse the existing
  Main Display Presentation and related registered contracts; no new spec or
  Planning Revision is created. Next route: fresh `/review-tasks-plan FT-001`.

## 2026-08-13 — W31 physical clock/icon geometry follow-up

- The operator's unlocked physical TECNO LI6 observation is a new bounded
  continuation of `FT-001-AC-002`: weather icons are materially too large,
  idle `HH:mm` is not the dominant largest element, and the complete clock
  must remain readable and contained at the actual landscape device size.
  The preserved composition is city/date in the left column above Yesterday,
  the clock in the free upper/central area above the three day cards, four
  stable weather slots and separate right-side timer controls.
- W30 did not cover this claim. Its fresh evidence measured host clock/card
  shell/preset geometry at `2460×1080` and `1280×720`, while
  `.tasks/TASK-033-T3-FT-001-W30/target-device.md` kept physical rendering
  `DEFERRED`. The later unlocked smoke recorded launch/health and landscape
  visibility only; it did not measure icon footprint or adjudicate physical
  focal hierarchy. No W30 PASS is promoted or rewritten.
- Create exactly one sequential T3 task,
  `TASK-034-T3-FT-001-W31`, status `planned`, directly after terminal
  `TASK-033-T3-FT-001-W30`. Its only behavior boundary is the existing
  `DisplayCapability.kt` plus `DisplayProjectionTest.kt`; no production code
  is changed in this planning run.
- W31 requires fresh host support plus fresh physical RED/GREEN on only the
  connected and unlocked TECNO LI6 serial `1156725456009666`. Host evidence
  cannot substitute for the physical visual claim; no emulator, AVD or QEMU
  is permitted. If the target is unavailable/locked, record `DEFERRED`, do
  not claim runtime PASS, and leave scheduler handling to the later route.
- Reuse the existing FT-001-AC-002, architecture, Boundary Map, capability,
  Platform Runtime, Weather Card Presentation and Runtime Verification
  contracts. Preserve four slots, date/city placement, timer separation,
  provider/weather projection ownership and Timer & Alert/runtime ownership;
  route any fixed numeric visual choice to `/feature-doctor FT-001` and any
  boundary change to `/spec-design`. Planning Revision remains `2`.

## 2026-08-13 — W29 provenance failure disposition and W30 replacement

- The fresh W29 task-plan review remained an `APPROVE` for planning, but the
  subsequent independent reports returned `VERDICT: NEEDS-CLARIFICATION` and
  `SEMANTIC_VERDICT: semantic-concern`; bounded executor recovery returned
  `HANDOFF_BLOCKED_FOR_PROVENANCE`. The evidence proves a missing W29
  pre-write RED/executor summary, not a product semantic failure.
- Reconcile `TASK-032-T3-FT-001-W29` from `in_progress` to `blocked` under the
  scheduler failure policy's proven provenance/authority-gap route. Preserve
  the exact current two-file `DisplayCapability.kt` /
  `DisplayProjectionTest.kt` diff, every W29 report, protocol and task-local
  artifact. Do not manufacture RED, reuse W26/W28 evidence, infer a fourth
  attempt or overwrite W29 history.
- Create exactly one sequential replacement,
  `TASK-033-T3-FT-001-W30`, with status `planned` and direct dependency on the
  last successful `TASK-031-T3-FT-007-W28`, not on blocked W29. This is a
  controlled rebuild because the replacement has a new task identity/wave;
  W29 remains visible provenance history and is not a product prerequisite.
- W30 must start from the current worktree baseline with a fresh task-specific
  RED probe at exactly `2460×1080` and `1280×720` before any behavior write.
  If the baseline is already claim-equivalent GREEN, the card permits only an
  explicit fresh `RED_NOT_APPLICABLE` reason with evidence and no
  production/test behavior write. Otherwise only the exact two-file boundary
  may change. Scope is full unclipped `HH:mm`, all four ordered slots under
  `NO_DATA`/partial/populated fixtures, one-color radial preset shading,
  materially wider rim and three static fading glow layers; Main Display
  ownership, touch routing, provider ownership and lifecycle remain intact.
- Target/device/runtime remains `DEFERRED` unless separately authorized. The
  next route is fresh `/review-tasks-plan FT-001`, followed by the applicable
  strict readiness and later execution/verification workflow.

## 2026-08-13 — W29 post-W28 Main Display visual follow-up

- The Explorer diagnosis and operator physical-device observation supplied in
  this planning request are authoritative for one bounded continuation after
  terminal `TASK-031-T3-FT-007-W28`: Main Display landscape rendering must keep
  the complete density-safe `HH:mm` unclipped at `2460×1080` and `1280×720`;
  all four `Yesterday`/today/tomorrow/day-after slots must remain visible in
  honest `NO_DATA`/async refresh and populated redacted-fixture states; and
  each existing circular preset must replace the current linear white→color
  outline with one preset-color radial shade gradient, a materially wider rim
  and a static outward-fading neon glow.
- Create exactly one new sequential T3 task,
  `TASK-032-T3-FT-001-W29`, status `planned`, depending only on terminal
  `TASK-031-T3-FT-007-W28`. W26 and W28 remain terminal records and are not
  reopened, rewritten or re-sliced.
- Reuse FT-001-AC-002 with applicable `REQ-001`, `REQ-002`, `REQ-005` and
  `REQ-023` constraints plus the existing architecture, Boundary Map,
  Capability Interfaces, Weather Card Presentation, Platform Runtime and
  Runtime Verification contracts. No new spec, behavior-spec, module, edge,
  public contract, dependency or resource pipeline is needed.
- Keep the exact hard write boundary to `DisplayCapability.kt` and
  `DisplayProjectionTest.kt`. WeatherCapability, FoundationRuntime, adapters,
  Settings, Timer, resources and MainActivity are read-only. Focused/full host,
  clean build, lint and diff gates plus visual receipts are planned;
  target/device/runtime evidence is explicitly `DEFERRED`. This planning run
  does not execute, verify, sync, launch runtime or alter task/checkpoint/
  terminal statuses.

## 2026-08-12 — W26 post-terminal idle Main Display visual follow-up

- Operator visual feedback after terminal W24 and W25 requests one bounded
  continuation of `FT-001-AC-002`: more spacious transparent preset circles
  with one neon gradient border per preset while preserving the existing color
  identity, substantially larger/adaptive idle `HH:mm` in the available central
  space, equal Yesterday/Tomorrow/Day-after sizing, approximately 20% smaller
  non-Today cards relative to Today, and larger uniform weather-card gaps.
  Active countdown/overdue behavior remains outside this task and belongs to
  later FT-006/FT-007 tasking.
- Create exactly one indexed T3 task,
  `TASK-029-T3-FT-001-W26`, status `planned`, after terminal
  `TASK-028-T3-FT-002-W25`. W24 and W25 identities, statuses, evidence,
  protocols and terminal history remain unchanged; Foundation remains
  transitive through the dependency chain.
- The task is one Main Display-owned visual outcome with hard writes limited to
  `DisplayCapability.kt` and `DisplayProjectionTest.kt`. Preserve preset order,
  labels, existing color identity, selected/active styling, touch routing,
  four-card projection ownership and Timer & Alert semantics. No target-device,
  emulator, adb, network or credential action is authorized.
- Reuse the registered architecture, Boundary Map, capability, platform,
  Weather Card Presentation, lifecycle, testing and tier-policy specs. Fresh
  claim-linked host RED/GREEN must include adaptive clock bounds, preset circle
  and per-preset gradient/transparent visual proof, card bounds/relative
  sizing/gaps, a same-size contact sheet and a named visual rubric. No fixed
  dp, ratio or gradient stops are selected; if execution requires one as a
  product decision, route to `/feature-doctor FT-001`.
- Planning Revision remains `2`; feature/REQ lifecycles, scheduler checkpoint,
  terminal state and all historical task evidence remain unchanged. This run
  creates planning state only and does not execute, verify, red-verify, close,
  promote or sync the new task.

## 2026-08-12 — W24 reference-driven clock/control follow-up

- Operator visual feedback after terminal W21, W22 and W23 identifies one
  bounded continuation of the existing `FT-001-AC-002`: idle `HH:mm` is too
  small and must regain visual dominance; the three existing right-side timer
  preset controls must be truly circular. This adds no AC, product timer/audio
  decision, weather semantic or architecture boundary.
- Create exactly one indexed T3 task,
  `TASK-027-T3-FT-001-W24`, status `planned`, after terminal
  `TASK-026-T3-FT-007-W23`. Foundation remains transitive through the existing
  dependency chain; W21, W22 and W23 identities, statuses and evidence remain
  unchanged.
- The task is one Main Display-owned outcome with hard writes limited to
  `DisplayCapability.kt` and `DisplayProjectionTest.kt`. Preserve existing
  right-side controls, labels, order, styles, touch routing and all Timer &
  Alert semantics. No TimerCapability, audio, Platform Runtime, Weather
  Context, resource or W23 change is authorized.
- Reuse the registered architecture, Boundary Map, Capability Interfaces,
  Weather Card Presentation, Lifecycle Map, Platform Runtime, Testing and
  Runtime Verification specs. Fresh RED/GREEN must include measured clock
  bounds, equal square button width/height, common half-diameter radius,
  same-size contact sheet and a named visual rubric. No absolute dp/ratio is
  selected; material numeric ambiguity routes to `/feature-doctor FT-001`.
- Planning Revision remains `2`; Foundation dependency, lifecycle/RTM values,
  scheduler checkpoint and terminal state are not changed. This run does not
  implement, review, execute, verify, red-verify or sync the task.

## 2026-08-12 — W21 operator-requested Main Display composition delta

- The operator requested one bounded visual/layout change: city/date in the
  left column above Yesterday; the large idle `HH:mm` in the central/upper
  area above Today/Tomorrow/Day-after; and the existing three circular preset
  buttons on the right. The four-card order remains
  `yesterday`/`today`/`tomorrow`/`day_after`.
- Existing Weather Card Presentation already owns the preserved relative
  semantics: Today is larger and the other three are equal smaller cards. W21
  keeps that projection ownership and requires uniform inter-card gaps larger
  than the current 8dp baseline. It does not add day labels or change weather
  content, freshness, palette, day/night, pressure or forecast semantics.
- Create exactly one new indexed T3 task,
  `TASK-024-T3-FT-001-W21`, status `planned`, behind terminal
  `TASK-023-T3-FT-002-W20`. The task is one Main Display-owned composition
  outcome with hard writes limited to `DisplayCapability.kt` and the existing
  `DisplayProjectionTest.kt`; no production code is changed in this planning
  run.
- Reuse System Architecture, Boundary Map, Capability Interfaces, Platform
  Runtime, Weather Card Presentation and Runtime Verification. No new
  canonical spec, ADR, module, graph edge, public contract, dependency or
  behavior-spec file is required. If “slightly” needs an absolute product
  dp/ratio decision, route to `/feature-doctor FT-001`; a boundary/owner change
  routes to `/spec-design`.
- T3 RED/GREEN proof is claim-linked to `FT-001-AC-002 / REQ-002`: fresh
  pre-change host/static geometry must expose the current left-header,
  equal-card, 8dp-gap baseline; post-change host/static proof must establish
  the requested regions, order, relative allocations and increased gaps.
  Clean build, full host unit suite and diff integrity are mandatory. Samsung
  GT-I9300I Android 11 custom-ROM/1280×720 readability remains `DEFERRED` with
  residual risk and no runtime `PASS` claim.
- Historical W2/W11/W12/W13/W14 task identities, evidence and statuses,
  feature/REQ lifecycles, Planning Revision `2`, scheduler checkpoint and
  terminal `SUCCESS` history remain unchanged. Next owner is fresh
  `/review-tasks-plan FT-001`.

## 2026-08-10 — Revision-2 plan reconciled without a task

- Provider migration does not change FT-001 acceptance or require a follow-up.
- Existing W2/W11–W14 identities, statuses, dependencies and evidence remain
  unchanged; queue action is `reconciled`.
- Exact next owner is fresh `/review-tasks-plan --all`.

## 2026-08-06 — Tasking surface generated

- FT-001 is eligible for decomposition: PRD clarification is complete, the
  feature design status is `complete`, the Global Backbone is `complete` at
  Planning Revision `1`, and the Foundation Gate is `done`.
- One T3 task, `TASK-003-T3-FT-001-W2`, owns the cohesive Main Display outcome.
  T3 is required by the Android runtime/display boundary and the target-device
  evidence route. The task depends directly on
  `TASK-002-T3-FT-000-W1`; no dependency on future FT-002–FT-009 task cards is
  invented.
- Existing architecture, boundary, capability-interface, platform-runtime and
  runtime-verification specs are reused. No competing canonical spec,
  feature-owned design hub, or optional behavior-spec file is created.
- Exact UI toolkit, implementation class split, and package/file identity
  remain execution-level choices within the accepted current Android scaffold.
  No new dependency, public boundary, ownership rule, or product behavior was
  selected by this decomposition.

## 2026-08-08 — Generic-emulator layout follow-up

- Post-terminal Reviewer evidence proves a narrow runtime-layout defect on the
  documented `Tecno_Pova_6_API_35` generic emulator: the populated city and
  transient status/hint rows measure to zero height, so the accepted city
  gesture and Settings entry are unreachable. FT-001 AC-002/AC-005 and the
  existing Main Display → Settings & Location contract already settle the
  target; no UX, contract or global architecture decision is required.
- Create `TASK-014-T3-FT-001-W11` as one `planned` T3 follow-up after the
  completed sequential queue. It depends on `TASK-011-T3-FT-009-W10`, which
  transitively preserves TASK-003 and the closed Foundation gate, and owns only
  the generic-emulator runtime-layout/reachability delta for FT-001-AC-002 and
  FT-001-AC-005. TASK-003 remains `done` historical evidence and is not
  reopened, rewritten or replaced.
- Existing architecture, boundary, capability, platform-runtime and
  runtime-verification specs are reused. Mandatory clean build, host unit and
  static gates remain, while the documented generic emulator supplies the
  decisive supplementary non-zero-bounds/Settings-reachability proof. Samsung
  GT-I9300I Android 11 custom-ROM and 1280×720 behavior remain `DEFERRED`.
- A zero-height Foundation probe control may be observed only when the same
  minimum correction safely restores it without a second mechanism; this is a
  conditional implementation/testing side effect, not FT-000 product scope.
  Planning Revision remains `1`; scheduler checkpoint and terminal `SUCCESS`
  state remain untouched.

## 2026-08-08 — W12 public active-countdown dispatch repair

- Final W11 semantic evidence records a distinct public runtime regression:
  one non-city weather-card double tap at a 120 ms interval left an active
  countdown visible at approximately 350 ms. City double cancellation and
  delayed-Settings protection passed. The public scenario is retained as an
  existing downstream protected-cancellation contract (REQ-013; regression
  guard only; canonical basis in TASK-015 `normative_inputs`), not a new timer
  contract, W12 acceptance locator, or reopened W11 outcome.
- Create `TASK-015-T3-FT-001-W12` as one planned T3 repair task. It depends on
  the last successful `TASK-011-T3-FT-009-W10`; failed W11 remains negative
  evidence and is not a prerequisite or lifecycle target. Historical task
  identities and statuses remain unchanged.
- Select the smallest sufficient approach: one Main Display-local,
  stateful active-countdown dispatcher captures the public surface at
  `ACTION_DOWN` and keeps the same stream through `ACTION_UP/CANCEL` instead of
  rechecking live TimerLifecycleState for every event. It routes existing
  timer commands, retains city hold-to-Settings and leaves idle city, preset
  and overdue paths intact. No public contract, owner, module, graph edge,
  dependency, event/message boundary or Planning Revision changes.
- The task must add focused host stream support but treat it as supporting
  evidence only. Fresh generic-emulator public runtime scenarios are mandatory
  for city hold, city double with delayed-Settings protection, non-city single,
  non-city double, preset guards and safe cleanup. Samsung/custom-ROM/1280x720
  remains `DEFERRED`; scheduler checkpoint, terminal state and RTM lifecycle
  are outside this planning reconciliation.

## 2026-08-09 — W13 bounded Main Display ticker debt

- Current code evidence confirms one bounded local implementation-debt outcome:
  `DisplayCapability.createMainView()` has two local ticker start paths (the
  attach callback and an unconditional post), and each 50 ms refresh reads the
  weather projection, removes the card tree and recreates all four cards.
  Activity `onPause`/`onResume` and the existing platform lifecycle seam are
  present, while no Main Display lifecycle owner is currently gating this
  ticker.
- Create `TASK-016-T3-FT-001-W13` as one planned T3 task depending on the
  already terminal `TASK-015-T3-FT-001-W12`. The single outcome is to make the
  Main Display ticker have one idempotent owner, stop/suppress it on Activity
  pause and view detach, restart one loop on resume/attach, and reuse the
  existing weather-card view tree until the existing projection/presentation
  inputs change. The 20 Hz scalar cadence remains so clock/date/colon behavior
  is not redesigned.
- Reuse the registered architecture, Boundary Map, Main Display → Weather
  Context, Main Display → Timer & Alert and Display Runtime contracts. MainActivity
  may only forward existing lifecycle signals; no new public contract, module,
  graph edge, dependency or event/message boundary is selected. Weather
  Context cache/refresh ownership, Timer & Alert/audio ownership, gesture
  semantics, Forecast-wide optimization and target-device evidence are
  explicitly excluded. Host-only scheduler/projection probes are sufficient
  for this bounded debt; if they are not, execution must stop rather than
  expand scope.
- Planning Revision remains `1`; historical W2/W11/W12 task identity,
  lifecycle, evidence and protocol history remain untouched, as do scheduler
  checkpoint and terminal-state artifacts. No execution, `/verify`,
  `/red-verify` or `/mb-sync` is performed in this planning run.

## 2026-08-10 — W14 projection/decode debt follow-up

- Advisory `TD-W13-001` confirms one residual W13 debt only: the scalar 20 Hz
  refresh still reloads/decode-builds the Weather Context display-ready
  projection even when the visible weather input is unchanged. W13's ticker,
  lifecycle gating and card-tree reuse are accepted terminal history and are not
  reopened.
- Create exactly one new indexed task,
  `TASK-017-T3-FT-001-W14`, with status `planned`, depending only on terminal
  `TASK-016-T3-FT-001-W13`. Its task-owned locator is
  `FT-001-AC-002 / REQ-002`; `REQ-007`, `REQ-022` and `REQ-025` remain governing
  weather/time/failure constraints and regression guards, not new product
  behavior or RTM lifecycle claims.
- Select the smallest sufficient path: memoize the existing display-ready
  `WeatherProjection` inside Weather Context and invalidate only after an
  accepted successful refresh, observed validated location change or an
  existing date/day-night/pressure-trend/24-hour freshness boundary. Failed
  refresh, network status, timer/lifecycle callbacks and unchanged scalar ticks
  do not invalidate it. The existing Main Display → Weather Context edge,
  `WeatherReadPort`, ownership of cache/history/projection semantics and all
  public boundaries remain unchanged.
- The hard write boundary is the existing `WeatherCapability.kt` and
  `WeatherContextTest.kt`. Host counting fixtures, clean build, full host unit
  suite and static diff are sufficient; Forecast, Yandex provider, Timer/audio,
  gestures, ticker lifecycle and target-device evidence remain excluded. No
  new canonical spec or behavior-spec file is created.
- Planning Revision remains `1`; W2/W11/W12/W13 terminal records, scheduler
  checkpoint, terminal state and RTM lifecycle remain untouched. This run does
  not execute, verify, sync, review or mark anything done.
