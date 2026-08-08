---
description: L3 feature for personal weather access, default city and offline country/city selection.
status: active
id: FT-008
epic: EP-004
lifecycle: planned
spec_design_status: complete
spec_design_links:
  - .memory-bank/contracts/boundary-map.md
  - .memory-bank/contracts/capability-interfaces.md
  - .memory-bank/contracts/weather-provider.md
  - .memory-bank/contracts/local-secret-handling.md
  - .memory-bank/contracts/platform-runtime.md
  - .memory-bank/domains/local-data.md
  - .memory-bank/testing/runtime-verification.md
source_of_truth: .memory-bank/prd.md, .memory-bank/requirements.md, operator confirmation 2026-08-06
last_updated: 2026-08-08
---
# FT-008 — Weather access and offline location settings

## Product outcome

Владелец вводит личный weather API key и offline выбирает нужный город, после
чего приложение использует его координаты и сохраняет weather access locally.

## Requirements

- REQ-017, REQ-018, REQ-024.

## Use cases

1. Владелец открывает Settings из города или no-city state.
2. Владелец вводит/меняет личный API key.
3. Владелец сначала ищет страну, затем ищет город только в выбранной стране;
   display и search используют accepted aliases.
4. Владелец выбирает default Khujand or another accepted city and returns to the
   main display.

## Acceptance criteria

### FT-008-AC-001 — Local personal API key

- REQ: REQ-017, REQ-024.
- Settings accept and locally retain a personal API key; it is not embedded in
  APK/source/logs/evidence.

### FT-008-AC-002 — Default and selected location refresh

- REQ: REQ-017.
- Default location is Khujand, Tajikistan; selected city coordinates feed the
  weather request and city change triggers the accepted weather refresh path.

### FT-008-AC-003 — Offline country-first catalog

- REQ: REQ-018.
- Country and city lists use the bundled GeoNames `cities15000` subset, work
  offline and case-insensitively, and city search is scoped to the chosen country.

### FT-008-AC-004 — Names and aliases

- REQ: REQ-018.
- Display prefers Russian names and falls back to canonical GeoNames names;
  search matches Russian, canonical and ASCII aliases.

### FT-008-AC-005 — GeoNames attribution

- REQ: REQ-018.
- Required GeoNames attribution appears in Settings before the final back-icon
  button.

### FT-008-AC-006 — Inline failure preservation

- REQ: REQ-017, REQ-018, REQ-024.
- Missing/invalid API key, network failure and unknown city show accepted owning
  inline messages without destroying the last valid setting.

## Edge / failure behavior

- No network is required to browse the bundled country/city lists.
- A missing key or failed weather request does not disable clock/timers; weather
  cards follow FT-002 freshness/empty behavior.
- The feature does not add Google Services, backend proxy, shared key or an
  unaccepted location source.

## Sources and normative inputs

- [.memory-bank/prd.md](../prd.md): `PRD-FR-006`, `PRD-FR-013`, `PRD-FR-017`,
  `PRD-FR-032`–`PRD-FR-034`, `PRD-FR-037`–`PRD-FR-039`, `PRD-AC-006`–
  `PRD-AC-006C`.
- [.memory-bank/invariants.md](../invariants.md): local API key, offline
  location and no-Google/no-backend rules.
- [.memory-bank/contracts/boundary-map.md](../contracts/boundary-map.md):
  provider and bundled location-data boundaries.
- [.memory-bank/glossary.md](../glossary.md): accepted city/weather vocabulary.

## Verification targets

- `PRD-AC-006`, `PRD-AC-006A`–`PRD-AC-006C`, `PRD-AC-008`, `PRD-FR-033`–
  `PRD-FR-034`.

## SDD Design Gate

Global backbone is complete at Planning Revision `1` and the Foundation Gate
is closed; FT-008 feature-level SDD design and task planning are complete for
this reconciliation.

Applicable global specs: [Boundary Map](../contracts/boundary-map.md),
[Capability Interfaces](../contracts/capability-interfaces.md), [Weather Provider](../contracts/weather-provider.md),
[Local Secret Handling](../contracts/local-secret-handling.md), [Local Data](../domains/local-data.md),
[Platform Runtime](../contracts/platform-runtime.md) and [Runtime Verification](../testing/runtime-verification.md).
The storage primitive, catalog index and security mechanism details remain
downstream within the accepted secret contract.

## W9 implementation evidence

The W9 boundary records `TASK-010-T3-FT-008-W9` as `done` with fresh functional
`PASS` and T3 semantic `semantic-pass`. The evidence covers local key
persistence and redaction, Khujand/default and selected-coordinate refresh,
offline country-first and scoped city search, Russian/canonical/ASCII aliases,
GeoNames attribution and inline failure preservation. See the [functional
report](../../.tasks/TASK-010-T3-FT-008-W9/TASK-010-T3-FT-008-W9-S-VERIFY-final-report-docs-01.md),
[semantic report](../../.tasks/TASK-010-T3-FT-008-W9/TASK-010-T3-FT-008-W9-S-RED-VERIFY-final-report-docs-01.md)
and [verifier-owned probe](../../.tasks/TASK-010-T3-FT-008-W9/verifier-owned-probe.md).
Target-device Settings readability/navigation evidence remains `DEFERRED` and
non-blocking with residual risk; no runtime `PASS` is claimed. The FT-008
feature and REQ-017/018/024 lifecycle values remain unchanged by this sync;
promotion and dependent-state reconciliation remain scheduler-owned.
