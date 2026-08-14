---
description: Fresh verifier-owned host, matrix, boundary and redaction evidence for TASK-021-T2-FT-003-W18.
status: final
task_id: TASK-021-T2-FT-003-W18
stage: S-VERIFY
role: Reviewer
---
# Verifier-owned evidence — TASK-021-T2-FT-003-W18

## Fresh execution

All observations below were produced in this `/verify` cycle. Executor receipts
were inspected as supporting evidence only; no executor receipt was reused as
independent proof. No production source, task card, lifecycle/status,
scheduler checkpoint or executor evidence was modified.

| Check | Fresh result |
|---|---|
| W18 outcome probe | `./gradlew testDebugUnitTest --tests 'com.hozayushka.app.ForecastSessionTest.selectedProvidersRequireAllEightSlotsAndNeverBorrowMissingValues' --tests 'com.hozayushka.app.ForecastSessionTest.selectedProviderChangeDoesNotBorrowAnotherProviderHourlyCache'` → exit `0`, `BUILD SUCCESSFUL` |
| Clean debug build | `./gradlew clean assembleDebug` → exit `0`, `BUILD SUCCESSFUL`, 34 actionable tasks; unrelated existing `MainActivity.kt` deprecation warning only |
| Full host suite after clean | `./gradlew testDebugUnitTest --rerun-tasks --no-daemon` → exit `0`, `BUILD SUCCESSFUL`; 13 XML suites, 93 tests, 0 skipped, 0 failures, 0 errors |
| Redaction suites | Open-Meteo adapter, OpenWeather adapter and Settings key-redaction targeted tests → exit `0`; full XML also contains 3/3 Open-Meteo and 3/3 OpenWeather tests with zero failures/errors |
| Matrix shape/result validator | `jq -e` validator over `hourly-completeness-matrix.json` → `true` |
| Memory Bank lint | `node scripts/mb-lint.mjs` → exit `0`, `mb-lint passed (78 files)` |
| Diff integrity | `git diff --check` → exit `0`, no findings |
| Source/evidence credential-shaped scan | No raw assignment or 32-hex credential-shaped match in app source, W18 evidence/protocols or test reports |
| APK credential-shaped scan | No raw assignment or 32-hex credential-shaped match in `app-debug.apk` strings |

Debug APK SHA-256 from this verification cycle:
`3b1965b0b3e7cefbeeaf7b7cd9eb522837875e6db494058165bfa25a9f22a22`.

## Outcome observations

- Fixture basis is resettable and provider-separated: `Asia/Dushanbe`,
  `2024-01-02`, `now = 2024-01-02T12:00:00Z`, and a new in-memory settings,
  cache, provider-call counter and closed Forecast Session per case.
- The exact selected-city keys are six current-day positions
  `06:00, 09:00, 12:00, 15:00, 18:00, 21:00`, followed by next-day `00:00`
  and `03:00`. The complete matrix has one Open-Meteo and one OpenWeather
  case; each opens with 8 cards in rows `[4, 4]`, selected-provider identity,
  one selected call and zero other-provider calls.
- The 16 missing-slot cases remove each key once for each provider. Every case
  has `hourlyProjection == null`, `CLOSED`, zero cards and the exact message
  `Почасовой прогноз еще не подгрузился`, with one selected-provider call and
  zero other-provider calls. OpenWeather cases for current-day `06:00`,
  `09:00`, `12:00` and `15:00` are explicitly marked elapsed.
- The provider-switch case seeds only Open-Meteo, switches selection to
  OpenWeather, and observes `CLOSED` with the exact unavailable message,
  empty rows and zero OpenWeather calls. This is the direct cache/provider
  isolation observation; no cross-provider value is available to the session.
- `ForecastSessionCapability` consumes only `WeatherReadPort.hourlyProjection`
  and returns the accepted rejection state/message; it does not read adapter,
  cache or history state. `WeatherCapability` matches provider and location
  identity before exposing the hourly record and checks every fixed key and
  required value before returning a projection.

## Claim mapping

- `FT-003-AC-001 / REQ-009`: PASS from the fresh complete two-provider cases,
  exact city-timezone key projection, provider identity and 8-card open result,
  including elapsed-slot semantics in the OpenWeather matrix.
- `FT-003-AC-005 / REQ-009, REQ-026`: PASS from all 16 one-missing-slot cases,
  exact unavailable message, no session, no cards and selected-only call
  counts, plus the provider-switch cache-isolation case.
- `weather-provider.md#mapping-and-timezone-obligations`: PASS for the fixed
  city-local sequence and no nearest/synthetic/cross-provider slot.
- `weather-provider.md#failure-rules` and the linked capability/local-data
  contracts: PASS for incomplete projection rejection and selected provider /
  location cache identity in the observed surface.

## Redaction and boundary observations

Open-Meteo request tests observed no credential, no `appid`/`apikey` and a
null redacted credential. OpenWeather request tests used only a generated
synthetic probe value, asserted `[REDACTED]` in the result envelope and did not
print or persist the raw value. W18 fixtures and evidence contain only the
literal redaction description, never a user key. No live provider, network,
device/emulator, ADB, QEMU or real credential was used.

The current worktree contains unrelated pre-existing migration/Memory Bank
changes. W18's handoff identifies the task-owned delta as the deterministic
`ForecastSessionTest` proof plus matrix artifact; no W18 production behavior
change was required after the honest pre-implementation GREEN.

Target Android/device and live-provider behavior remains `DEFERRED` under the
task/operator constraint. No runtime `PASS` claim is made.
