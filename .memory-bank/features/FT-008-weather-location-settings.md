---
description: L3 feature for personal weather access, default city and offline country/city selection.
status: draft
id: FT-008
epic: EP-004
lifecycle: planned
last_updated: 2026-08-03
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

- Settings accept and locally retain a personal API key; it is not embedded in
  APK/source/logs/evidence.
- Default location is Khujand, Tajikistan; selected city coordinates feed the
  weather request and city change triggers the accepted weather refresh path.
- Country and city lists use the bundled GeoNames `cities15000` subset, work
  offline and case-insensitively, and city search is scoped to the chosen country.
- Display prefers Russian names and falls back to canonical GeoNames names;
  search matches Russian, canonical and ASCII aliases.
- Required GeoNames attribution appears in Settings before the final back-icon
  button.
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

Global backbone is complete at Planning Revision `1`; feature-level design
remains draft until the Foundation Gate and `/feature-to-tasks`.

Applicable global specs: [Boundary Map](../contracts/boundary-map.md),
[Capability Interfaces](../contracts/capability-interfaces.md), [Weather Provider](../contracts/weather-provider.md),
[Local Secret Handling](../contracts/local-secret-handling.md), [Local Data](../domains/local-data.md),
[Platform Runtime](../contracts/platform-runtime.md) and [Runtime Verification](../testing/runtime-verification.md).
The storage primitive, catalog index and security mechanism details remain
downstream within the accepted secret contract.
