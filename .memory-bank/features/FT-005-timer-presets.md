---
description: L3 feature for the three preset timer definitions, labels and configuration outcome.
status: draft
id: FT-005
epic: EP-003
lifecycle: planned
spec_design_status: complete
spec_design_links:
  - .memory-bank/contracts/boundary-map.md
  - .memory-bank/contracts/capability-interfaces.md
  - .memory-bank/domains/local-data.md
  - .memory-bank/states/lifecycle-map.md
  - .memory-bank/testing/runtime-verification.md
last_updated: 2026-08-08
source_of_truth: .memory-bank/prd.md, .memory-bank/requirements.md, operator confirmation 2026-08-06
---
# FT-005 — Preset timer configuration

## Product outcome

Владелец имеет три понятных preset-кнопки и может независимо задать длительность
каждого preset в принятых пределах до запуска countdown.

## Requirements

- REQ-011.

## Use cases

1. Владелец видит три presets с defaults 3, 10 и 30 минут.
2. Владелец задаёт часы/минуты/секунды каждого preset.
3. Владелец понимает длительность по старшей ненулевой единице и цвету кнопки.

## Acceptance criteria

### FT-005-AC-001

- REQ: REQ-011

Exactly three presets exist and each can be configured independently; no more
than one timer can be active at a time.

### FT-005-AC-002

- REQ: REQ-011

Inputs accept hours 0–99, minutes 0–59 and seconds 0–59; total duration must
be greater than zero.

### FT-005-AC-003

- REQ: REQ-011

Button labels show only the highest non-zero unit (`ч`, `м` or `с`) with
floor rounding; initial durations are 3m, 10m and 30m.

### FT-005-AC-004

- REQ: REQ-011

Buttons use the accepted orange, pink and purple neon outlines, and the
selected/active visual state is available to the countdown feature.

## Edge / failure behavior

- Zero total duration or out-of-range field does not become an accepted preset;
  the last valid value remains available through Settings behavior.
- Fractional lower units do not appear in the label and are not rounded up.
- Preset configuration does not create a second active timer or alter overdue
  dismissal semantics.

## Sources and normative inputs

- [.memory-bank/prd.md](../prd.md): `PRD-FR-023`–`PRD-FR-025`, `PRD-FR-032`,
  `PRD-FR-037`–`PRD-FR-038`.
- [.memory-bank/invariants.md](../invariants.md): single active timer and
  accepted Settings validation boundary.
- [.memory-bank/states/lifecycle-map.md](../states/lifecycle-map.md): timer
  lifecycle entry from `idle`.

## Verification targets

- `PRD-FR-023`–`PRD-FR-025`, `PRD-AC-004`, `PRD-AC-006C`.

## SDD Design Gate

Global backbone is complete at Planning Revision `2` and the Foundation Gate
anchors remain closed; feature-level SDD design remains complete. Its indexed
task-plan review is stale only by revision mismatch and is reconciled through
`/feature-to-tasks --all` after Foundation revalidation.

Applicable global specs: [Boundary Map](../contracts/boundary-map.md),
[Capability Interfaces](../contracts/capability-interfaces.md), [Local Data](../domains/local-data.md),
[Lifecycle Map](../states/lifecycle-map.md) and [Runtime Verification](../testing/runtime-verification.md).
The persistence primitive and UI implementation remain downstream.

## W6 implementation evidence

The W6 boundary records `TASK-007-T3-FT-005-W6` as `done` after the attempt-2
editor-restoration correction and fresh attempt-3 functional `PASS` plus T3
semantic `semantic-pass`. The corrected path restores all three visible fields
from the last-valid owner duration without recursive watcher persistence; the
fresh proof covers 32/32 host tests, clean build, validation/persistence,
labels, colors, selected/active projection and boundary checks. See the
[functional report](../../.tasks/TASK-007-T3-FT-005-W6/TASK-007-T3-FT-005-W6-S-VERIFY-final-report-docs-01.md),
[semantic report](../../.tasks/TASK-007-T3-FT-005-W6/TASK-007-T3-FT-005-W6-S-RED-VERIFY-final-report-docs-01.md),
[attempt-3 verifier evidence](../../.tasks/TASK-007-T3-FT-005-W6/verifier-attempt-3.md),
[attempt-2 correction receipt](../../.tasks/TASK-007-T3-FT-005-W6/correction-green-attempt-2.md)
and [deferred target receipt](../../.tasks/TASK-007-T3-FT-005-W6/target-device-attempt-2.md).
Target-device evidence remains `DEFERRED` and non-blocking with residual risk;
no runtime `PASS` is claimed. Feature lifecycle remains `planned`; no feature
closure, promotion or dependent-state transition is inferred by this boundary
sync.
