---
description: L3 feature for the always-visible clock, date/city area and main display shell.
status: draft
id: FT-001
epic: EP-001
lifecycle: implemented
spec_design_status: complete
spec_design_links:
  - .memory-bank/architecture/system-architecture.md
  - .memory-bank/contracts/boundary-map.md
  - .memory-bank/contracts/capability-interfaces.md
  - .memory-bank/contracts/main-display-presentation.md
  - .memory-bank/contracts/platform-runtime.md
  - .memory-bank/testing/runtime-verification.md
last_updated: 2026-08-14
---
# FT-001 — Main clock and display shell

## Product outcome

Владелец открывает приложение и сразу получает читаемый fullscreen clock display
с city/date слева над Yesterday, крупными часами над тремя дневными cards,
фиксированным порядком четырёх weather cards и preset buttons справа.

## Requirements

- REQ-001, REQ-002, REQ-003, REQ-004, REQ-005, REQ-022, REQ-023.

## Use cases

1. Владелец смотрит на `HH:mm` и дату из кухонной дистанции.
2. Владелец видит выбранный город либо понятный `Выбрать город`.
3. Владелец удерживает город, чтобы открыть Settings; selected-city short tap
   не меняет экран.
4. Владелец видит accepted clock-colon behavior online, offline и при активном
   timer.

## Acceptance criteria

### FT-001-AC-001 — Runtime display policy

- REQ: REQ-001, REQ-023
App runs only in landscape fullscreen, hides system panels and keeps the
  display on while open.

### FT-001-AC-002 — Main display composition

- REQ: REQ-002, REQ-023
`HH:mm` has no seconds and remains the dominant visual element; city/date are
  on the left, exactly four weather cards occupy the 25–30% bottom band and
  three separate preset buttons are on the right. FT-001 owns shell count,
  composition and placement; FT-002 owns card data, content, freshness and
  weather-specific presentation.

The operator-requested composition delta keeps this ownership and makes the
placement explicit: city and date occupy the left column directly above the
`yesterday` (`Вчера`) card; the large `HH:mm` clock occupies the available
central/upper area directly above the three day-forecast slots
`today`/`tomorrow`/`day_after` (`Сегодня`/`Завтра`/`Послезавтра`); and the three
existing circular preset buttons remain on the right. The four slots stay in
`yesterday`/`today`/`tomorrow`/`day_after` order. The weather-card band MUST
occupy 25–30% of total landscape height and the clock zone MUST occupy the
remaining 70–75%. All four cards MUST have equal height and common bottom
alignment; Yesterday MUST never be taller. Today may remain wider or denser
without becoming taller. The complete `HH:mm` MUST use the maximum
available size in the clock zone without clipping or overlap. The accepted
band/clock ratios and relational presentation rules in the Main Display
Presentation spec are normative. Any pixel tolerance is only a raster
measurement tolerance, not a product target. Timer behavior, fullscreen policy
and weather-card data/content semantics remain unchanged.

### FT-001-AC-003 — Device-time clock and date

- REQ: REQ-002, REQ-022
Date uses `dd` and Russian genitive month without year or weekday. Clock/date
  use device timezone.

### FT-001-AC-004 — Clock colon states

- REQ: REQ-003
Online colon performs the accepted 3-second rise and following fade to 2%;
  offline colon is fixed at 38%; active countdown uses the accepted discrete
  382/618 ms blink.

### FT-001-AC-005 — City interaction

- REQ: REQ-004
Empty city renders `Выбрать город`; its short tap opens Settings, a selected
  city's short tap is a no-op, and a long hold opens Settings.

## Edge / failure behavior

- Missing weather data does not remove the stable main-display shell or shift
  the clock/date zones; the four weather-card positions remain stable.
- Network absence changes only the accepted colon state and weather availability;
  it does not block clock display.
- Timer-specific replacement of the clock is composed by FT-006; this feature
  retains the display shell and its accepted colon state.

## Boundary ownership

- Main Display owns the shell composition, the four-card placement and city
  gesture intent.
- The existing Main Display → Settings & Location contract owns the destination
  request and return path. Settings & Location owns the destination surface and
  settings state; FT-001 does not add catalog, API-key, validation or
  personalization behavior.
- Weather Context/FT-002 owns weather data and card content. FT-001 verifies
  only the four-card composition boundary and does not take FT-002 data
  ownership.

## Sources and normative inputs

- [.memory-bank/prd.md](../prd.md): `PRD-FR-001`–`PRD-FR-006`, `PRD-FR-039`,
  `PRD-NFR-001`–`PRD-NFR-003`, `PRD-AC-001`, `PRD-AC-009`.
- [.memory-bank/invariants.md](../invariants.md): clock dominance and local-only
  product constraints.
 [.memory-bank/contracts/main-display-presentation.md](../contracts/main-display-presentation.md):
   normalized composition, equal-height card band, clock maximum-size method,
   icon/timer hierarchy and visual-QA proof.
- [.memory-bank/user-scenarios.md](../user-scenarios.md): core glance scenario.

## Verification targets
 `REQ-002`, `REQ-005`, `REQ-023`: band/clock ratios, equal card height and
 normalized visual-QA proof from Main Display Presentation.

- `PRD-AC-001`, `PRD-AC-009`, `PRD-FR-003`–`PRD-FR-006`.

## W2 implementation evidence

The indexed implementation task `TASK-003-T3-FT-001-W2` is `done` with
functional `PASS` and semantic `semantic-pass`. Host/static evidence is
recorded; target-only fullscreen, readability, keep-screen-on and interaction
observations remain `DEFERRED` with residual risk under the accepted policy.
See the [task card](../tasks/TASK-003-T3-FT-001-W2.task.json), [functional
verification](../../.tasks/TASK-003-T3-FT-001-W2/TASK-003-T3-FT-001-W2-S-VERIFY-final-report-docs-02.md)
and [semantic verification](../../.tasks/TASK-003-T3-FT-001-W2/TASK-003-T3-FT-001-W2-S-RED-VERIFY-final-report-docs-01.md).

## Post-terminal generic-emulator follow-up

Supplementary Reviewer evidence on `Tecno_Pova_6_API_35` confirms that the
populated city and transient status/hint rows collapse to zero height, making
the accepted city gesture and Settings entry unreachable. TASK-003 remains the
unchanged `done` historical record; the separate planned follow-up
[`TASK-014-T3-FT-001-W11`](../tasks/TASK-014-T3-FT-001-W11.task.json) owns only
the FT-001-AC-002/AC-005 generic-emulator layout/reachability delta. Samsung
GT-I9300I Android 11 custom-ROM and 1280×720 evidence remain `DEFERRED`.

## W11 boundary failure

The indexed follow-up [`TASK-014-T3-FT-001-W11`](../tasks/TASK-014-T3-FT-001-W11.task.json)
is `failed` after its final retry: functional verification passed, but the
required independent semantic verification returned `semantic-fail`. A public
non-city weather-card double tap left an active countdown running, violating
the existing FT-006-AC-003 / REQ-013 cancellation contract; selected-city
double-tap cancellation and delayed Settings protection passed. The task-local
defect is recorded in the
[`TASK-014-noncity-countdown-cancellation`](../bugs/TASK-014-noncity-countdown-cancellation.md)
bug note. See the [semantic report](../../.tasks/TASK-014-T3-FT-001-W11/TASK-014-T3-FT-001-W11-S-RED-VERIFY-final-report-docs-01.md)
and [red-verification protocol](../../.protocols/TASK-014-T3-FT-001-W11/red-verification.md).

This failure does not reopen or replace the historical `TASK-003-T3-FT-001-W2`
record and does not change FT-001's existing `implemented` lifecycle. Repair
must return through normal indexed `/feature-to-tasks FT-001` planning before
fresh execution and verification.

## W12 public active-countdown dispatch repair

The authoritative [`TASK-015-T3-FT-001-W12`](../tasks/TASK-015-T3-FT-001-W12.task.json)
record is now `done` after executor `PASS_FOR_HANDOFF`, fresh functional
`PASS` and independent semantic `semantic-pass`. It owns only the bounded Main
Display `FT-001-AC-005` city hold/Settings-preservation acceptance delta. The
existing downstream protected-cancellation contract (REQ-013; regression guard
only; canonical basis in TASK-015 `normative_inputs`) was exercised by the
public non-city weather-card single/double checks to prevent cross-feature
regression; it is not a W12 acceptance locator, a new FT-006 task, or an
FT-006 lifecycle change. The repair remains inside Main Display and the
existing Timer & Alert and Settings & Location contracts: it captures one
active public touch stream through terminal delivery without live-state
rechecking, while preserving city long-hold -> Settings, selected/empty city
taps, preset interactions and overdue behavior. See the [functional
verification](../../.protocols/TASK-015-T3-FT-001-W12/verification.md),
[semantic verification](../../.protocols/TASK-015-T3-FT-001-W12/red-verification.md),
and [verifier-owned evidence](../../.tasks/TASK-015-T3-FT-001-W12/verifier-owned-evidence-attempt-1.md).

This does not reopen or rewrite TASK-014's failed historical
layout/reachability record; TASK-014 remains `failed` and unchanged, and
TASK-003 remains `done` and unchanged. Host stream evidence remains supporting
only; generic-emulator public evidence is decisive for the W12 reachability
claims. Samsung/custom-ROM/1280x720 physical evidence remains `DEFERRED` with
no promoted target-device `PASS`. FT-001 lifecycle and its direct RTM values
remain `implemented`; no feature closure or scheduler promotion is inferred by
this boundary sync.

## W13 bounded Main Display ticker debt

The authoritative [`TASK-016-T3-FT-001-W13`](../tasks/TASK-016-T3-FT-001-W13.task.json)
record is now `done` after executor `PASS_FOR_HANDOFF`, fresh functional `PASS`
and independent durable semantic `semantic-pass`. It owns one local
implementation-debt outcome inside Main Display: consolidate
ticker scheduling under one owner, gate callbacks on existing Activity
pause/resume and view attach/detach lifecycle, and retain the last rendered
weather-card presentation so unchanged 20 Hz scalar ticks do not rebuild the
four-card view tree. The current clock/date/colon cadence remains intact;
Weather Context retains cache/projection ownership and Timer & Alert retains
timer semantics. This is a reuse of the existing architecture, Boundary Map,
capability and platform contracts, not a new feature acceptance criterion or
public boundary. See the [executor handoff](../../.protocols/TASK-016-T3-FT-001-W13/handoff.md),
[functional verification](../../.protocols/TASK-016-T3-FT-001-W13/verification.md),
[verifier-owned functional evidence](../../.tasks/TASK-016-T3-FT-001-W13/verifier-owned-evidence.md),
[durable semantic verification](../../.protocols/TASK-016-T3-FT-001-W13/red-verification.md)
and [semantic report](../../.tasks/TASK-016-T3-FT-001-W13/TASK-016-T3-FT-001-W13-S-RED-VERIFY-final-report-docs-01.md).

W13 is host-proof-only by operator scope. The then-current Yandex provider work
(now superseded by the provider-migration PRD), timer/audio
ownership, cross-surface gesture semantics, Forecast-wide optimization and
target-device evidence remain excluded and require separate evidence/decision.
Samsung/custom-ROM/1280x720 physical evidence remains `DEFERRED` with no
promoted target-device `PASS`. FT-001 lifecycle and direct RTM values
`REQ-002`, `REQ-003` and `REQ-022` remain `implemented`; the W2 `done`, W11
`failed` and W12 `done` task records and their protocol/scheduler history remain
unchanged. No feature closure, promotion or scheduler-state transition is
inferred by this boundary sync.

## W14 bounded Weather Context projection/decode follow-up

The authoritative [`TASK-017-T3-FT-001-W14`](../tasks/TASK-017-T3-FT-001-W14.task.json)
record is `done` after executor `PASS_FOR_HANDOFF`, fresh functional `PASS`
and independent durable semantic `semantic-pass`. Based only on advisory
`TD-W13-001`, it owns no new feature behavior or acceptance criterion: its
exact implementation delta remains under `FT-001-AC-002 / REQ-002`, separating
the required W13 scalar 20 Hz clock/date/colon refresh from repeated Weather
Context display-ready input construction. The existing capability-owned
`WeatherProjection` snapshot is reused inside Weather Context and invalidated
only after an accepted successful Weather refresh, an observed validated
location change or an existing selected-city date/day-night/pressure-trend/
24-hour freshness boundary. A failed refresh, network signal, timer/lifecycle
callback or unchanged scalar tick does not invalidate it.

See the [executor handoff](../../.protocols/TASK-017-T3-FT-001-W14/handoff.md),
[functional verification](../../.protocols/TASK-017-T3-FT-001-W14/verification.md),
[verifier-owned functional evidence](../../.tasks/TASK-017-T3-FT-001-W14/verifier-owned-evidence.md),
[durable semantic verification](../../.protocols/TASK-017-T3-FT-001-W14/red-verification.md)
and [semantic report](../../.tasks/TASK-017-T3-FT-001-W14/TASK-017-T3-FT-001-W14-S-RED-VERIFY-final-report-docs-01.md).

Weather Context remains the sole owner of provider refresh, normalized data,
cache/history, freshness and projection semantics; Main Display remains a
read-only consumer through the existing `Main Display → Weather Context` edge.
Direct FT-001 RTM values remain implemented; `REQ-007` and `REQ-025` remain
the existing Weather Context/Timer & Alert requirements and regression guards,
not new W14 lifecycle claims. W14 is host/static proof only. Samsung/custom-ROM/
1280x720 physical evidence remains `DEFERRED` with no promoted target-device
`PASS`. Forecast, the then-current Yandex provider (now historical and
superseded), Timer/audio, gestures and target-device
behavior remain excluded; no public boundary, module, dependency, event,
provider implementation or Planning Revision changes. W2 `done`, W11
`failed`, W12 `done` and W13 `done` task records and their protocol history
remain unchanged. No feature closure, promotion or scheduler-state transition
is inferred by this boundary sync.

## W21 operator-requested Main Display composition delta

The operator requested a bounded visual/layout change on 2026-08-12. It is a
new detail under `FT-001-AC-002`, not a new requirement, lifecycle transition,
weather semantic or architecture decision. One indexed T3 task owns the
composition delta: left-column city/date above Yesterday, central/upper large
clock above Today/Tomorrow/Day-after, right-side three preset controls, fixed
four-slot order, Today larger than the three equal smaller non-today cards, and
uniformly increased card gaps. Existing timer dispatch/countdown/overdue,
fullscreen policy, card projection content/freshness/palette and city/settings
semantics are regression constraints only.

The exact task-owned geometry is relational rather than an invented pixel
specification: the three non-today cards must have equal measured allocation;
Today must have a strictly larger measured allocation; every weather-card gap
must use one common value greater than the current 8dp baseline; city/date and
the idle clock must be in the stated regions; and the four slot identities must
remain ordered. If execution requires an absolute dp/ratio product choice to
resolve “slightly”, stop and route to `/feature-doctor FT-001` before changing
the task boundary. A boundary/owner change instead routes to `/spec-design`.

`TASK-024-T3-FT-001-W21` was planned behind the terminal
`TASK-023-T3-FT-002-W20`. Its hard write boundary is the existing Main Display
implementation and `DisplayProjectionTest.kt`; host geometry/static proof is
mandatory, while Samsung GT-I9300I Android 11 custom-ROM/1280×720 target
evidence remains `DEFERRED` with residual risk and no runtime `PASS` claim.

W21 is now `done` after fresh executor `PASS_FOR_HANDOFF`, functional `PASS`
and independent T3 semantic `semantic-pass`. The accepted geometry is proved
by host assertions and a rendered contact sheet: city/date is above Yesterday
in the left column, idle `HH:mm` occupies the central/upper region, the three
preset controls remain on the right, Today measures `279 > 223`, the other
three cards are equal, and gaps are `16/16/16`. Timer, weather projection,
gesture and public contracts remain unchanged. See the [executor handoff](../../.protocols/TASK-024-T3-FT-001-W21/handoff.md),
[functional verification](../../.tasks/TASK-024-T3-FT-001-W21/TASK-024-T3-FT-001-W21-S-VERIFY-final-report-docs-01.md),
[semantic verification](../../.tasks/TASK-024-T3-FT-001-W21/TASK-024-T3-FT-001-W21-S-RED-VERIFY-final-report-docs-01.md)
and [contact sheet](../../.tasks/TASK-024-T3-FT-001-W21/red-green-contact-sheet.svg).

## W24 reference-driven clock/control follow-up

Operator visual feedback after the terminal W21, W22 and W23 history identifies
one bounded continuation of `FT-001-AC-002`: the current idle `HH:mm` must gain
clear visual dominance, while the three existing right-side timer preset
controls must become genuinely circular. This is not a new AC, timer/audio
change, weather semantic, lifecycle transition or architecture decision.

`TASK-027-T3-FT-001-W24` owns the Main Display-only visual delta. It preserves
the existing right-side control order, labels, colors, selected/active styling,
touch routing and Timer & Alert semantics. Its proof is qualitative reference
matching backed by host-measured clock bounds, equal square control bounds,
common half-diameter radius evidence, a same-size RED/GREEN contact sheet and a
named visual rubric. No absolute product dp/ratio is introduced; a material
numeric choice routes to `/feature-doctor FT-001`.

The hard write boundary is exactly `DisplayCapability.kt` and
`DisplayProjectionTest.kt`. `TimerCapability.kt`, audio/Platform Runtime,
Weather Context and all W23 paths remain forbidden. W21, W22 and W23 terminal
statuses/evidence, Planning Revision `2`, Foundation dependency, scheduler
checkpoint and terminal state remain unchanged. Samsung/custom-ROM 1280x720
runtime readability and circular rendering remain `DEFERRED` without target
evidence.

W24 is now `done` after executor `PASS_FOR_HANDOFF`, fresh functional `PASS`
and independent T3 `semantic-pass` on Attempt 2. The reachable idle ticker
keeps the reference-aligned `176f` clock size while countdown remains `32f`;
the three existing right-side controls are measured `220x220` circles with
radius `110`. Host suite, contact sheet, bounds and regression gates pass.
Samsung/custom-ROM target readability and runtime circle rendering remain
`DEFERRED`, with no device/runtime `PASS` claim. See the [W24 sync report](../../.tasks/TASK-027-T3-FT-001-W24/TASK-027-T3-FT-001-W24-S-MB-SYNC-final-report-docs-01.md),
[functional verification](../../.tasks/TASK-027-T3-FT-001-W24/TASK-027-T3-FT-001-W24-S-VERIFY-final-report-docs-01.md)
and [semantic verification](../../.tasks/TASK-027-T3-FT-001-W24/TASK-027-T3-FT-001-W24-S-RED-VERIFY-final-report-docs-01.md).

## W26 post-terminal idle Main Display visual follow-up

Operator visual feedback after terminal W24 and W25 requests one bounded
continuation of `FT-001-AC-002`: more spacious transparent preset circles with
one distinct neon gradient border per preset while preserving each existing
color identity; a substantially larger/adaptive idle `HH:mm` using the
available central space; Yesterday equal to Tomorrow and Day-after; all three
smaller cards about 20% smaller than Today; and larger uniform inter-card gaps.
Active countdown and overdue behavior are explicitly excluded and remain later
FT-006/FT-007 scope.

The new indexed [`TASK-029-T3-FT-001-W26`](../tasks/TASK-029-T3-FT-001-W26.task.json)
is the single Main Display-owned planned T3 task after terminal
[`TASK-028-T3-FT-002-W25`](../tasks/TASK-028-T3-FT-002-W25.task.json). Its hard
write boundary is exactly `DisplayCapability.kt` and `DisplayProjectionTest.kt`.
It preserves preset order, labels, existing color identity, selected/active
styling, touch routing, four-card projection ownership and Timer & Alert
semantics. Fresh claim-linked host RED/GREEN evidence, a same-size contact sheet
and a named visual rubric are required; Samsung/custom-ROM 1280x720 runtime
evidence remains `DEFERRED`, with no emulator/device/adb/network action.

No fixed dp, ratio or gradient stops are selected. If execution cannot produce
an unambiguous visual verdict without turning one of those into a new product
decision, route to `/feature-doctor FT-001`; a boundary or owner change routes
to `/spec-design`. W24/W25 task identities, statuses, evidence and terminal
history remain unchanged, as do the feature lifecycle, RTM values, Planning
Revision `2`, scheduler checkpoint and terminal state.

W26 is now `done` after executor `PASS_FOR_HANDOFF`, fresh functional `PASS`
and independent T3 `semantic-pass`. Host evidence proves clock sizes `188.75`
and alternate `139.75`, preset bounds `200x200` with `24` spacing, transparent
per-color neon treatment, and card geometry `217/273/217/217` with common gap
`24`; focused/full host, build, diff and visual gates pass. Active countdown and
overdue surfaces remain excluded for FT-006/FT-007. Samsung/custom-ROM target
readability and runtime rendering remain `DEFERRED`, with no device/runtime
`PASS` claim. See the [W26 sync report](../../.tasks/TASK-029-T3-FT-001-W26/TASK-029-T3-FT-001-W26-S-MB-SYNC-final-report-docs-01.md),
[functional verification](../../.tasks/TASK-029-T3-FT-001-W26/TASK-029-T3-FT-001-W26-S-VERIFY-final-report-docs-01.md)
and [semantic verification](../../.tasks/TASK-029-T3-FT-001-W26/TASK-029-T3-FT-001-W26-S-RED-VERIFY-final-report-docs-01.md).

## W29 post-W28 density-safe Main Display visual follow-up

The authoritative Explorer diagnosis and operator physical-device observation
for the next bounded follow-up identify a Main Display landscape defect after
terminal W28: the complete `HH:mm` must remain density-safe and unclipped at
`2460×1080` and `1280×720`; all four `Yesterday`/today/tomorrow/day-after
slots must remain visible during honest `NO_DATA`/async refresh and with a
populated redacted fixture; and each existing circular preset must use one
preset-color radial shade gradient, a materially wider rim than the current
baseline and a static outward-fading neon glow.

The indexed
[`TASK-032-T3-FT-001-W29`](../tasks/TASK-032-T3-FT-001-W29.task.json) was one
historical T3 attempt after terminal cross-feature
[`TASK-031-T3-FT-007-W28`](../tasks/TASK-031-T3-FT-007-W28.task.json). It reuses
`FT-001-AC-002` with applicable `REQ-001`, `REQ-002`, `REQ-005` and `REQ-023`
constraints. The hard write boundary remains exactly `DisplayCapability.kt`
and `DisplayProjectionTest.kt`; WeatherCapability, FoundationRuntime, adapters,
Settings, Timer, resources and MainActivity are read-only. Preset order,
labels, colors, selected/active/touch contracts and Timer & Alert semantics
remain unchanged, as do Weather Context/provider ownership and W26/W28
terminal history. Its later provenance disposition is recorded below; this
section does not promote its supporting receipts to product evidence.

Fresh host RED/GREEN at both sizes must cover clock no-clipping/overflow, all
four slots in NO_DATA/async/populated redacted-fixture states, and radial/rim/
glow visual receipts with a named rubric. Focused/full/build/lint/diff gates
were required. Target/device/runtime evidence is explicitly `DEFERRED` under
the current authorization and cannot be promoted from host evidence. No new
specification or behavior-spec was created; the planning route was replaced by
the fresh W30 execution and verification recorded below.

## W29 provenance disposition and W30 recovery

The fresh W29 planning review was `APPROVE`, but independent functional and
semantic review later returned `NEEDS-CLARIFICATION` and
`semantic-concern`; bounded executor evidence recovery returned
`HANDOFF_BLOCKED_FOR_PROVENANCE`. This is a missing pre-write RED/executor
summary and authority gap, not evidence of a product semantic failure.

The indexed [`TASK-032-T3-FT-001-W29`](../tasks/TASK-032-T3-FT-001-W29.task.json)
is therefore terminal `failed` under the recorded scheduler failure
disposition. The failure is a provenance/authority gap: no honest W29
pre-write RED or prior executor summary exists. It is not a product semantic
failure. Its current two-file code diff, all W29 reports, protocols and
task-local artifacts remain preserved; W26/W28 evidence is not promoted to W29
RED and no fourth W29 attempt is inferred. See the [W29 executor
handoff](../../.tasks/TASK-032-T3-FT-001-W29/TASK-032-T3-FT-001-W29-S-EXE-final-report-code-01.md),
[functional verification](../../.tasks/TASK-032-T3-FT-001-W29/TASK-032-T3-FT-001-W29-S-VERIFY-final-report-docs-01.md)
and [semantic verification](../../.tasks/TASK-032-T3-FT-001-W29/TASK-032-T3-FT-001-W29-S-RED-VERIFY-final-report-docs-01.md).

Exactly one replacement,
[`TASK-033-T3-FT-001-W30`](../tasks/TASK-033-T3-FT-001-W30.task.json), was
planned directly after terminal W28 rather than depending on failed W29.
W30 started from the current worktree baseline and its fresh task-specific
probe at `2460×1080` and `1280×720` accepted `RED_NOT_APPLICABLE`: the current
baseline was already claim-equivalent GREEN, so no production/test behavior
write was made. Fresh verifier-owned evidence proves the full unclipped
`HH:mm`, four ordered slots under `NO_DATA`/partial/populated redacted
fixtures, and the three existing circular presets' one-color radial shading,
materially wider rim and three static fading glow layers. The exact two-file
boundary remained unchanged, ownership/touch/provider/lifecycle boundaries
remain preserved, and all five required host gates passed. See the [W30
executor handoff](../../.tasks/TASK-033-T3-FT-001-W30/TASK-033-T3-FT-001-W30-S-EXE-final-report-code-01.md),
[functional verification](../../.tasks/TASK-033-T3-FT-001-W30/TASK-033-T3-FT-001-W30-S-VERIFY-final-report-docs-01.md),
[semantic verification](../../.tasks/TASK-033-T3-FT-001-W30/TASK-033-T3-FT-001-W30-S-RED-VERIFY-final-report-docs-01.md)
and [W30 sync report](../../.tasks/TASK-033-T3-FT-001-W30/TASK-033-T3-FT-001-W30-S-MB-SYNC-final-report-docs-01.md).
Target/device/runtime evidence remains `DEFERRED`; host evidence is not a
runtime `PASS`.

## W31 physical clock/icon geometry follow-up

W30's fresh host proof was sufficient for its bounded host claims but did not
measure the physical weather-icon footprint or establish physical focal
hierarchy. Its target separation record kept physical rendering `DEFERRED`;
the later unlocked TECNO smoke established launch/health and landscape
visibility only. The operator's physical observation therefore remains an
unresolved visual defect, not a W30 lifecycle contradiction or a reason to
rewrite W30 evidence.

The indexed
[`TASK-034-T3-FT-001-W31`](../tasks/TASK-034-T3-FT-001-W31.task.json) is now
`done` after executor `PASS_FOR_HANDOFF`, independent `/verify` `PASS` and
`/red-verify` `semantic-pass`. Fresh physical RED/GREEN on the unlocked TECNO
LI6 serial `1156725456009666` at the recorded `2460×1080` landscape frame
proves the complete `HH:mm` contained and dominant (`725×218`) and materially
reduced secondary weather illustrations (largest `45×43`, versus `71×70` RED).
City/date remains above Yesterday, all four ordered slots remain present, and
the three timer controls remain separate on the right. Supporting host geometry
covers `2460×1080` and `1280×720`; it is kept distinct from the physical visual
PASS. All five required host gates passed, and the exact behavior boundary
remains `DisplayCapability.kt` plus `DisplayProjectionTest.kt`.

W31 preserves Main Display's read-only Weather Context and Timer & Alert
presentation boundaries; no provider/refresh/freshness/cache/history, timer
lifecycle/control dispatch/audio, fullscreen/runtime owner or public contract
change is attributed to it. Emulator/AVD/QEMU remains forbidden. Other
resolutions/devices, custom-ROM rendering, physical audio audibility and live
provider refresh remain residual risks outside W31 and are not claimed as W31
PASS. See the [W31 sync report](../../.tasks/TASK-034-T3-FT-001-W31/TASK-034-T3-FT-001-W31-S-MB-SYNC-final-report-docs-01.md),
[executor handoff](../../.tasks/TASK-034-T3-FT-001-W31/TASK-034-T3-FT-001-W31-S-EXE-final-report-code-01.md),
[functional verification](../../.tasks/TASK-034-T3-FT-001-W31/TASK-034-T3-FT-001-W31-S-VERIFY-final-report-docs-01.md)
and [semantic verification](../../.tasks/TASK-034-T3-FT-001-W31/TASK-034-T3-FT-001-W31-S-RED-VERIFY-final-report-docs-01.md).

## W33 confirmed mixed-state allocation follow-up

The W32 physical screenshot confirms a separate real-View allocation defect that
host pure geometry did not expose: when Yesterday is empty while 14/15/16 are
populated, the empty Yesterday shell spans most of the height and the populated
cards collapse into the bottom band. The source path is Main Display-owned:
`leftHeader` remains `WRAP_CONTENT`, `yesterdayCard` remains `MATCH_PARENT` with
`weight=1` in a separate left container, and `bindWeatherCards` keeps Yesterday
apart from the other three cards. WeatherCapability/provider behavior is not
part of the defect; the empty day remains a legitimate empty shell with no
fabricated values.

W32 is now scheduler-`failed` after the authorized physical smoke exposed the
real View-tree mixed-state defect; its prior host PASS/semantic-pass and failure
evidence remain preserved. The blocked
[`TASK-036-T3-FT-001-W33`](../tasks/TASK-036-T3-FT-001-W33.task.json) record
remains blocked on W32 with its identity, dependency, evidence and history
unchanged; only its direct SDD/single-card handoff structure is repaired.

The planning route created exactly one recovery successor,
[`TASK-037-T3-FT-001-W34`](../tasks/TASK-037-T3-FT-001-W34.task.json), with the
same shared real View allocation outcome, deterministic empty-Yesterday/
three-populated host regression and fresh physical RED/GREEN on unlocked TECNO
LI6 serial `1156725456009666` after authorized build/install. W34 depends only
on successful [`TASK-034-T3-FT-001-W31`](../tasks/TASK-034-T3-FT-001-W31.task.json),
the last successful Main Display baseline; it does not bypass failed W32 or
blocked W33. Its exact hard write boundary remains `DisplayCapability.kt` plus
`DisplayProjectionTest.kt`. The physical receipt must include screenshots and
measured bounds proving the four equal-height/common-bottom cards in the
25–30% band beneath the 70–75% clock zone. Emulator/AVD/QEMU is forbidden;
WeatherCapability/provider/data synthesis, timer/runtime drift and FT-007
timer-digit sizing remain out of scope.

## W34 mixed-state allocation recovery

The indexed [`TASK-037-T3-FT-001-W34`](../tasks/TASK-037-T3-FT-001-W34.task.json)
is now `done` under its already-recorded scheduler closure after executor
`PASS_FOR_HANDOFF`, independent `/verify` `PASS`, T3 `/red-verify`
`semantic-pass` and fresh physical PASS on unlocked TECNO LI6 serial
`1156725456009666` at `2460×1080` landscape. Host GREEN proves the accepted
mixed empty-Yesterday/three-populated fixture at `2460×1080` and `1280×720`:
band ratios `0.27962962`/`0.27916667`, clock-zone ratios
`0.7203704`/`0.7208333`, equal card heights and common bottoms. The physical
View receipt confirms four `302px` cards ending at `1056`, while empty
Yesterday, complete `HH:mm`, city/date and the separate timer rail remain
intact without clipping or overlap.

W34 remains bounded to
`DisplayCapability.kt` plus `DisplayProjectionTest.kt`; Weather Context,
Timer & Alert, provider/data, lifecycle, fullscreen/runtime and public
contracts remain read-only. W31 stays `done`, W32 stays `failed`, and W33
stays `blocked`, including its superseded policy-invalid `blocked -> failed`
transition history. The accepted timer-digit sizing observation is a separate
future FT-007 presentation residual and is not part of W34. See the [W34 sync
report](../../.tasks/TASK-037-T3-FT-001-W34/TASK-037-T3-FT-001-W34-S-MB-SYNC-final-report-docs-01.md),
[functional verification](../../.tasks/TASK-037-T3-FT-001-W34/TASK-037-T3-FT-001-W34-S-VERIFY-final-report-docs-01.md)
and [semantic verification](../../.tasks/TASK-037-T3-FT-001-W34/TASK-037-T3-FT-001-W34-S-RED-VERIFY-final-report-docs-01.md).

FT-001 lifecycle remains `implemented`; this boundary does not infer feature
closure, promotion, dependent-state transition or a new Planning Revision.

## SDD Design Gate

Global backbone is complete at Planning Revision `2`, the Foundation Gate
anchors remain closed, and the registered Boundary Map contains the accepted
module inventory and dependency graph. Feature-level design remains complete;
its indexed task-plan review, W21/W24/W26 closure, W29 failure disposition,
W30 host-only closure, W31 physical closure, W32 physical failure, W33 blocked
history and W34 completed recovery closure are current at Planning Revision `2`; no
additional product or architecture decision is introduced by this boundary.

Applicable global specs: [System Architecture](../architecture/system-architecture.md),
[Boundary Map](../contracts/boundary-map.md), [Capability Interfaces](../contracts/capability-interfaces.md),
[Weather Provider](../contracts/weather-provider.md), [Weather Card Presentation](../contracts/weather-card-presentation.md),
[Local Data](../domains/local-data.md), [Lifecycle Map](../states/lifecycle-map.md),
[Platform Runtime](../contracts/platform-runtime.md) and [Runtime Verification](../testing/runtime-verification.md).
The feature does not choose the UI toolkit or test level. Exact implementation
choices remain execution discretion unless they change an accepted boundary or
require a new dependency.
