---
description: Implementation plan for FT-008 weather access and offline location settings.
status: active
last_updated: 2026-08-07
---
# IMPL-FT-008 — Weather access and offline location settings

## Outcome

Implement one Settings & Location outcome in which the owner can retain a
personal weather API key locally, select a country and scoped city from the
bundled GeoNames catalog while offline, and cause a valid city change to use
the selected coordinates through the accepted Weather Context refresh path.
Invalid or unavailable inputs remain inline and preserve the last valid value;
the key never crosses a secret-bearing artifact boundary.

## Bounded task shape

- One task: `TASK-010-T3-FT-008-W9`.
- Primary owner: `Settings & Location`.
- Direct predecessor: `TASK-009-T3-FT-007-W8`; Foundation remains transitive
  through the approved chain ending at `TASK-002-T3-FT-000-W1`.
- Tier: `T3`, because the outcome owns a user secret, local persistence and
  redaction, crosses the accepted Settings/Weather/Catalog boundaries, and
  proves provider failure without exposing credentials.
- Status: `planned`; planning does not claim scheduler closure or promotion.

## Acceptance closure

The single task owns all six FT-008 ACs. `REQ-017` covers the personal key,
default/selected location, coordinates and city-change refresh. `REQ-018`
covers offline country-first search, country scoping, aliases and attribution.
`REQ-024` covers local-only key handling and secret-free artifacts. The task
consumes the existing Weather Context provider/freshness behavior and does not
adopt FT-002 cache/history or FT-009 personalization claims.

| Claim | Owning task result |
|---|---|
| `FT-008-AC-001 / REQ-017, REQ-024` | A valid personal key is retained locally, is absent from source/APK/logs/evidence, and missing/invalid input preserves the last valid key with the owning inline error. |
| `FT-008-AC-002 / REQ-017` | Khujand is the default; a valid selected city persists its country/name/coordinates and requests the accepted Weather Context refresh with that location. |
| `FT-008-AC-003 / REQ-018` | Country search works offline and case-insensitively; city search is available only after country selection and is scoped to that country over the bundled `cities15000` subset. |
| `FT-008-AC-004 / REQ-018` | Display prefers Russian names with canonical fallback, while search matches Russian, canonical and ASCII aliases. |
| `FT-008-AC-005 / REQ-018` | Required GeoNames attribution is visible in Settings before the final back-icon button. |
| `FT-008-AC-006 / REQ-017, REQ-018, REQ-024` | Missing/invalid key, provider/network failure and unknown city produce the accepted inline result without replacing the last valid setting or disabling the stable clock/timer path. |

No accepted FT-008 AC remains without an owner, and no FT-009 alert/glass
personalization outcome is adopted.

## Execution-path sanity check

The bounded path is: Main Display opens Settings → Settings & Location
validates and persists a synthetic key and the default/selected location → the
catalog supplies offline country-first and scoped city results → a valid city
write requests Weather Context refresh through the registered orchestration
edge → Weather Context sends only an ephemeral credential to the provider
adapter and returns success/failure through its existing cache/freshness path.
The host proof covers validation, persistence, catalog filtering, attribution,
redacted request shape and failure preservation. This is one cohesive Settings
outcome with one owner and one user-visible proof path; no independent
prerequisite or rollback unit requires a second task.

## Canonical SDD coverage

All applicable concerns reuse existing subject-based canonical specs. No new
canonical specification, competing path or behavior-spec file is created.

| Concern | Action | Canonical basis | Planning reason |
|---|---|---|---|
| Architecture and capability ownership | `reuse` | [Capability Slice Runtime](../../architecture/system-architecture.md#capability-slice-runtime), [AD-002](../../architecture/system-architecture.md#ad-002---application-owned-local-state-is-the-product-source-of-truth), [AD-003](../../architecture/system-architecture.md#ad-003---cross-slice-orchestration-stays-in-a-capability-owner), [AD-006](../../architecture/system-architecture.md#ad-006---user-api-keys-are-local-and-redacted) | Settings & Location owns validation, selected location and key storage; it requests Weather Context refresh but does not own weather data or provider transport. |
| Module inventory and dependency graph | `reuse` | [Modules](../../contracts/boundary-map.md#modules), [Dependency Graph](../../contracts/boundary-map.md#dependency-graph), [Accepted Ownership Summary](../../contracts/boundary-map.md#accepted-ownership-summary) | Every changed unit and crossed edge already exists: Main Display → Settings & Location, Settings & Location → Weather Context, Weather Context → Yandex Weather Adapter and Settings & Location → Bundled Location Catalog. |
| Settings and location public boundaries | `reuse` | [Main Display to Settings and Location](../../contracts/capability-interfaces.md#main-display-to-settings-and-location), [Location Refresh Orchestration](../../contracts/capability-interfaces.md#location-refresh-orchestration), [Settings and Location to Bundled Location Catalog](../../contracts/capability-interfaces.md#settings-and-location-to-bundled-location-catalog), [Weather Context to Settings and Location](../../contracts/capability-interfaces.md#weather-context-to-settings-and-location) | The selected location is written only by Settings & Location; refresh and normalized weather state remain Weather Context-owned, and catalog access is read-only/offline. |
| Provider request and failure semantics | `reuse` | [Weather Provider Boundary](../../contracts/weather-provider.md#weather-provider-boundary), [Refresh, Cache and Failure Rules](../../contracts/weather-provider.md#refresh-cache-and-failure-rules), [Credential and Evidence Rules](../../contracts/weather-provider.md#credential-and-evidence-rules) | City change uses the accepted Yandex request path; failures preserve the valid location and existing weather freshness behavior. |
| Local data and persistence | `reuse` | [Ownership Matrix](../../domains/local-data.md#ownership-matrix), [Durable Data Rules](../../domains/local-data.md#durable-data-rules), [Validation and Serialization Boundaries](../../domains/local-data.md#validation-and-serialization-boundaries) | Valid settings auto-save, invalid values remain unsaved, the catalog is immutable, and no consumer writes another capability's store. |
| Secret handling | `reuse` | [Local API-Key Handling Contract](../../contracts/local-secret-handling.md#local-api-key-handling-contract), [Storage Mechanism Boundary](../../contracts/local-secret-handling.md#storage-mechanism-boundary), [Evidence and Verification](../../contracts/local-secret-handling.md#evidence-and-verification) | The user key is local-only, ephemeral at the provider boundary and always redacted from durable artifacts. |
| Platform/network compatibility | `reuse` | [Compatibility and Failure Rules](../../contracts/platform-runtime.md#compatibility-and-failure-rules) | Network absence is a signal; it cannot disable clock/timer behavior or make offline catalog search unavailable. |
| Verification and secret-safe evidence | `reuse` | [Deterministic Host-Side Checks](../../testing/runtime-verification.md#deterministic-host-side-checks), [Redacted Integration Fixtures](../../testing/runtime-verification.md#redacted-integration-fixtures), [Secret and Artifact Checks](../../testing/runtime-verification.md#secret-and-artifact-checks) | Host checks are sufficient for settings/catalog/provider semantics; synthetic credentials and redacted fixtures are mandatory. |

No `needed_before_tasks` Backbone row remains and Planning Revision remains
positive and unchanged at `1`.

## Scope boundary

### In scope

- Settings entry from the accepted Main Display route and the owning inline
  errors for key/location/provider failure.
- Local persistence of a validated personal API key and selected
  country/city/coordinates, with Khujand as the default.
- Bundled GeoNames `cities15000` country-first search, country-scoped city
  search, case-insensitive matching, Russian/canonical/ASCII aliases and
  required attribution.
- Requesting the accepted Weather Context refresh after a valid city change;
  provider credentials remain ephemeral and redacted.
- Last-valid-value preservation for missing/invalid key and unknown city, and
  stable clock/timer behavior on network/provider failure. The owning inline
  messages are `API-ключ не указан`, `Неверный API-ключ`, `Нет подключения`
  and `Город не найден`.

### Out of scope

- Weather normalization, cache/history/freshness ownership and weather-card
  presentation from FT-002.
- Main Display clock/fullscreen behavior from FT-001, forecasts from FT-003/
  FT-004 and timer/overdue/audio behavior from FT-005–FT-007.
- Alert sound/volume, glass intensity, preview and other personalization from
  FT-009.
- A shared or embedded API key, backend/proxy, cloud sync, accounts, Google
  Services, a second location source, new event infrastructure or unaccepted
  Settings controls.
- Live credentials or runtime evidence during planning.

## Primary owner, boundaries and execution path

- Primary owner: `Settings & Location`, code root
  `app/src/main/kotlin/com/hozayushka/app/settings`.
- The Settings owner composes the user-facing screen and owns validation,
  persistence and selected-location state. It reads the immutable catalog and
  requests Weather Context refresh only after a valid location write.
- Weather Context owns refresh timing, provider mapping, cache/history and
  freshness; Yandex Weather Adapter owns transport mapping only. Neither is
  allowed to expose or persist the key outside the request path.
- Main Display owns only Settings navigation and selected-city display
  composition. The composition root wires the accepted interfaces and does not
  own Settings or refresh business logic.

## Expected advisory change surface

- `app/src/main/kotlin/com/hozayushka/app/settings/` — Settings screen/state,
  key and location validation, local persistence, catalog query and inline
  errors.
- `app/src/main/kotlin/com/hozayushka/app/weather/` — accepted location read
  model and refresh request seam; no duplicate Settings storage owner.
- `app/src/main/kotlin/com/hozayushka/app/adapters/weather/` — redacted
  provider request/response seam only.
- `app/src/main/kotlin/com/hozayushka/app/display/` — Settings navigation and
  selected-city projection only.
- `app/src/main/kotlin/com/hozayushka/app/app/` — composition wiring only when
  required by the accepted graph.
- `app/src/main/assets/geonames/` — bundled GeoNames-derived catalog data and
  attribution asset if required by the project-native implementation.
- `app/src/test/kotlin/com/hozayushka/app/` and
  `app/src/test/resources/fixtures/` — deterministic settings/catalog and
  redacted provider probes; no live key.

These paths are advisory and non-exhaustive. No hard `write_boundary` is set;
the semantic scope, forbidden scope and stop conditions remain binding.

## Applicable gates, UAT and claim-linked proof

- `./gradlew clean assembleDebug` — clean Android debug build.
- `./gradlew testDebugUnitTest` — deterministic Settings validation,
  persistence, catalog, attribution and redacted refresh/failure checks.
- Use known isolated Settings state with a synthetic credential, deterministic
  bundled fixture and safe reset/cleanup. The proof records only redacted
  results. Target-device evidence is not invented during planning and is
  limited later to host-insufficient Settings readability/navigation results.

| Claim | Decisive result | Artifact |
|---|---|---|
| `FT-008-AC-001 / REQ-017, REQ-024` | Valid key reloads locally; missing/invalid key shows the owning inline error and keeps the last valid value; source/APK/log/evidence scan finds no key. | Isolated settings persistence and secret-scan result using a synthetic placeholder |
| `FT-008-AC-002 / REQ-017` | Default is Khujand; selected city coordinates persist and the refresh request carries that location through Weather Context. | Deterministic selected-location and redacted refresh result |
| `FT-008-AC-003 / REQ-018` | Offline case-insensitive country search returns matches; city results appear only for the selected country and use the bundled subset. | Offline catalog query result |
| `FT-008-AC-004 / REQ-018` | Display and search resolve Russian, canonical and ASCII aliases according to the accepted precedence. | Alias projection/search result |
| `FT-008-AC-005 / REQ-018` | Attribution is present in the Settings projection before the final back-icon action. | Settings structure/content result |
| `FT-008-AC-006 / REQ-017, REQ-018, REQ-024` | Missing/invalid key, provider/network failure and unknown city show `API-ключ не указан`, `Неверный API-ключ`, `Нет подключения` or `Город не найден` as applicable, preserve valid settings and leave the stable clock/timer route available. | Failure-preservation and boundary integration result |

## Constraints and invariants

- Settings & Location is the sole mutable owner of the API key, selected
  country/city and coordinates; the catalog remains immutable packaged data.
- The key may be supplied only by user input, only through the authorized
  Weather Context request path, and never in source, APK resources, logs,
  screenshots, fixtures or verification evidence.
- Valid settings auto-save; invalid values remain unsaved and preserve the last
  valid value with an owning inline error. Country selection precedes and scopes
  city search.
- Weather Context owns refresh, normalization, cache/history and freshness;
  this task does not duplicate those rules or write its private storage.
- No backend, Google Services, shared key, reboot recovery, new dependency,
  event/message boundary or unaccepted Settings surface is introduced.

## Direct normative inputs

- [.memory-bank/features/FT-008-weather-location-settings.md](../../features/FT-008-weather-location-settings.md)
- [.memory-bank/epics/EP-004-settings-location.md](../../epics/EP-004-settings-location.md)
- [.memory-bank/requirements.md](../../requirements.md)
- [.memory-bank/prd.md](../../prd.md)
- [.memory-bank/invariants.md](../../invariants.md)
- [.memory-bank/architecture/system-architecture.md](../../architecture/system-architecture.md)
- [.memory-bank/contracts/boundary-map.md](../../contracts/boundary-map.md)
- [.memory-bank/contracts/capability-interfaces.md](../../contracts/capability-interfaces.md)
- [.memory-bank/contracts/weather-provider.md](../../contracts/weather-provider.md)
- [.memory-bank/contracts/local-secret-handling.md](../../contracts/local-secret-handling.md)
- [.memory-bank/contracts/platform-runtime.md](../../contracts/platform-runtime.md)
- [.memory-bank/domains/local-data.md](../../domains/local-data.md)
- [.memory-bank/testing/runtime-verification.md](../../testing/runtime-verification.md)
- [.memory-bank/workflows/tier-policy.md](../../workflows/tier-policy.md)
- [.memory-bank/tasks/TASK-009-T3-FT-007-W8.task.json](../TASK-009-T3-FT-007-W8.task.json)

## Handoff

The fresh next step is `/review-tasks-plan FT-008`. Execution,
`/mb-doctor`, `/verify`, `/red-verify` and `/mb-sync` are not part of this
planning run.
