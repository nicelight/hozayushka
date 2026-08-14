---
description: L3 feature for personal weather access, default city and offline country/city selection.
status: active
id: FT-008
epic: EP-004
lifecycle: implemented
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
last_updated: 2026-08-11
---
# FT-008 — Weather access and offline location settings

## Product outcome

Владелец оставляет default Open-Meteo без key либо явно выбирает OpenWeather и
вводит его личный локальный key, offline выбирает нужный город, после чего
приложение использует выбранный provider и координаты без hidden fallback.

## Requirements

- REQ-017, REQ-018, REQ-024, REQ-027, REQ-028.

## Use cases

1. Владелец открывает Settings из города или no-city state.
2. Владелец оставляет default Open-Meteo без key либо явно выбирает OpenWeather
   и вводит/меняет его личный API key.
3. Владелец сначала ищет страну, затем ищет город только в выбранной стране;
   display и search используют accepted aliases.
4. Владелец выбирает default Khujand or another accepted city and returns to the
   main display.

## Acceptance Criteria

### FT-008-AC-001 — Local OpenWeather personal API key

- REQ: REQ-024

Settings accept and locally retain the owner's key only for OpenWeather; it is
not embedded in APK/source/logs/evidence. Open-Meteo does not use this key.

- Verification: provider-context key persistence and credential-absence/redaction inspection.

### FT-008-AC-002 — Default and selected location refresh

- REQ: REQ-017
- Default location is Khujand, Tajikistan; selected city coordinates feed the
  weather request and city change triggers the accepted weather refresh path.

- Verification: default/selected coordinate persistence and refresh-trigger scenario.

### FT-008-AC-003 — Offline country-first catalog

- REQ: REQ-018
- Country and city lists use the bundled GeoNames `cities15000` subset, work
  offline and case-insensitively, and city search is scoped to the chosen country.

- Verification: offline country-first/scoped-city catalog scenarios.

### FT-008-AC-004 — Names and aliases

- REQ: REQ-018
- Display prefers Russian names and falls back to canonical GeoNames names;
  search matches Russian, canonical and ASCII aliases.

- Verification: deterministic Russian/canonical/ASCII display and search examples.

### FT-008-AC-005 — GeoNames attribution

- REQ: REQ-018
- Required GeoNames attribution appears in Settings before the final back-icon
  button.

- Verification: Settings content/order inspection.

### FT-008-AC-006 — Inline failure preservation

- REQ: REQ-017, REQ-018, REQ-024, REQ-027

Missing/invalid key messages apply only to selected OpenWeather; network,
selected-provider and unknown-city failures show accepted owning inline
messages without destroying the last valid setting or claiming fallback.

- Verification: contextual validation/provider failure and value-preservation scenarios.

### FT-008-AC-007 — Provider selection and key applicability

- REQ: REQ-027

Settings default to Open-Meteo without a user key and allow OpenWeather only by
explicit owner selection with its local key. Valid selection changes auto-save;
provider/key failure neither changes selection nor requests or reports a
fallback provider.

- Verification: first-run, explicit switch, reopen and provider-failure scenarios.

### FT-008-AC-008 — Open-Meteo attribution

- REQ: REQ-028

Required Open-Meteo attribution appears in Settings alongside the accepted
GeoNames attribution before the final back-icon button.

- Verification: Settings attribution content/order inspection.

## Edge / failure behavior

- No network is required to browse the bundled country/city lists. Coverage:
  FT-008-AC-003, FT-008-AC-004.
- A missing key is an error only for selected OpenWeather. Provider/key failure
  does not disable clock/timers, change selection or invoke Open-Meteo fallback;
  weather cards follow FT-002 matching-cache/empty behavior. Coverage:
  FT-008-AC-001, FT-008-AC-006, FT-008-AC-007.
- The feature does not add Google Services, backend proxy, shared key or an
  unaccepted location source. Coverage: FT-008-AC-001, FT-008-AC-003.
- Missing required attribution blocks acceptance of the Settings content.
  Coverage: FT-008-AC-005, FT-008-AC-008.

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
  `PRD-FR-034`, `REQ-027`, `REQ-028`.

## SDD Design Gate

The Global Backbone is `complete` at Planning Revision `2`, Foundation
revalidation is successful and the Gate anchors remain closed. Default/no-key
Open-Meteo, explicit OpenWeather/local-key selection, provider-specific failure
and Open-Meteo attribution are reconciled into
`TASK-019-T3-FT-008-W16`; feature-level design is `complete`. Transient
OpenWeather `appid` transport remains downstream FT-002/W17 ownership.
Existing W9 task identity, scheduler state and terminal history remain
unchanged.

Current Revision-2 ownership is exact: W9 retains unchanged AC-002–AC-005;
W16 owns revised AC-001/AC-006 and new AC-007/AC-008.

Applicable global specs: [Boundary Map](../contracts/boundary-map.md),
[Capability Interfaces](../contracts/capability-interfaces.md), [Weather Provider](../contracts/weather-provider.md),
[Local Secret Handling](../contracts/local-secret-handling.md), [Local Data](../domains/local-data.md),
[Platform Runtime](../contracts/platform-runtime.md) and [Runtime Verification](../testing/runtime-verification.md).
These are the authoritative global inputs for subsequent feature planning.

## W9 implementation evidence

This section is historical brownfield evidence for the former single-provider
Settings contract. It does not prove FT-008-AC-007 or FT-008-AC-008.

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
feature and REQ-017/018/024 lifecycle values remained unchanged by the W9
sync; promotion and dependent-state reconciliation remained scheduler-owned.

## W16 provider-settings implementation evidence

The indexed [`TASK-019-T3-FT-008-W16`](../tasks/TASK-019-T3-FT-008-W16.task.json)
is `done` after final Attempt-3 executor `PASS_FOR_HANDOFF`, fresh functional
`PASS` and fresh independent `semantic-pass`. The closure basis is linked from
the [executor handoff](../../.protocols/TASK-019-T3-FT-008-W16/handoff.md),
[functional verification](../../.protocols/TASK-019-T3-FT-008-W16/verification.md),
[Attempt-3 verifier report](../../.tasks/TASK-019-T3-FT-008-W16/TASK-019-T3-FT-008-W16-S-VERIFY-final-report-docs-03.md),
[semantic verification](../../.protocols/TASK-019-T3-FT-008-W16/red-verification.md)
and [semantic report](../../.tasks/TASK-019-T3-FT-008-W16/TASK-019-T3-FT-008-W16-S-RED-VERIFY-final-report-docs-01.md).

The two unsuccessful attempts remain traceable rather than being rewritten:
Attempt 1 ended in functional `FAIL` because task-owned evidence retained a raw
synthetic marker, and Attempt 2 reached functional `PASS` before adversarial
`semantic-fail` exposed owner-key release to legacy transport and false
selection-derived attribution. Their receipts remain in the authoritative task
record, [Attempt-1 verifier report](../../.tasks/TASK-019-T3-FT-008-W16/TASK-019-T3-FT-008-W16-S-VERIFY-final-report-docs-01.md),
[Attempt-2 verifier report](../../.tasks/TASK-019-T3-FT-008-W16/TASK-019-T3-FT-008-W16-S-VERIFY-final-report-docs-02.md)
and [claim-linked attempt log](../../.protocols/TASK-019-T3-FT-008-W16/progress.md).

Combined W9 and W16 evidence implements FT-008 and direct RTM owners
REQ-017, REQ-018, REQ-024, REQ-027 and REQ-028. The final bounded safeguard
intentionally denies provider-unidentified legacy key access/refresh;
`TASK-020-T3-FT-002-W17` must atomically replace that deny with selected
OpenWeather-authorized key access while installing selected-provider dispatch.
TASK-020 remains `planned`; this feature sync performs no promotion. Physical-
device and live-provider evidence remains `DEFERRED`, with no runtime `PASS`.
