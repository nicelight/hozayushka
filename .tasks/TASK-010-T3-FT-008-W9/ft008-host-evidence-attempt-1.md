---
description: Claim-linked host, build, static and redacted-provider evidence for TASK-010-T3-FT-008-W9 attempt 1.
status: active
---
# FT-008 host evidence — attempt 1

## Execution basis

- Task: `TASK-010-T3-FT-008-W9`, tier `T3`, attempt `1`.
- Repository basis: `HEAD=a93e46118f0f0b90e311b6174e3f5a8ed7d89fef` plus the
  broad pre-existing worktree changes recorded in `context.md`.
- Initial RED is preserved at `baseline-red-attempt-1.md`; it was captured
  before the FT-008 production change.
- All provider checks use a synthetic in-memory credential. No live key is
  present in this artifact.

## Claim-linked GREEN comparison

| Claim | Decisive host observation | Result / artifact |
|---|---|---|
| FT-008-AC-001 / REQ-017, REQ-024 | Settings accepts a non-blank token without whitespace, keeps it in the owner store, exposes only a callback-scoped value, rejects blank/whitespace input as `API-ключ не указан`, rejects whitespace-bearing input as `Неверный API-ключ`, preserves the last valid value, and redacts `SettingsState.toString()`. | Green in `SettingsLocationTest.validKeyReloadsAndInvalidInputPreservesLastValidValueWithoutRedactionLeak`; no raw credential in source/APK/evidence scans. |
| FT-008-AC-002 / REQ-017 | Bundled default city `1514879` resolves to Khujand; selected Dushanbe reloads through owner state, and the captured provider request carries selected latitude/longitude. | Green in `defaultAndSelectedLocationReloadsWithCoordinatesAndRefreshRequest`; request credential observation is `[REDACTED]`. |
| FT-008-AC-003 / REQ-018 | Catalog search is case-insensitive, city search returns empty without a country code, and selected country filters out another country's city. The immutable transformed `cities15000.tsv` asset has 34,079 valid ten-column rows. | Green in `offlineCountryFirstSearchIsCaseInsensitiveAndCityScopedToSelectedCountry`; asset/static shape scan. |
| FT-008-AC-004 / REQ-018 | City query matches Russian and canonical/ASCII aliases; display chooses Russian when present and canonical fallback is represented by the model when Russian is absent. | Green through `LocationCatalogEntry.matchesCity` and alias assertions in `SettingsLocationTest`. |
| FT-008-AC-005 / REQ-018 | Settings adds GeoNames attribution before the final back-icon action. | Green in scoped source order check: attribution line `489`, back-icon line `506`. |
| FT-008-AC-006 / REQ-017, REQ-018, REQ-024 | Missing stored key, invalid-credential, network and unknown-city failures are inline and leave saved location/key unchanged. | Green in the three failure-preservation tests in `SettingsLocationTest`. |
| T3 isolation/redaction | Tests use in-memory Settings/weather stores, deterministic TSV and provider doubles; no external request is made; provider fixture exposes only redacted credential. | Green; source/APK/evidence scans found no live or token-shaped credential. |

## Commands and receipts

- `./gradlew testDebugUnitTest --tests com.hozayushka.app.SettingsLocationTest`
  → exit `0`; seven FT-008 tests, zero failures/errors. Report:
  `app/build/reports/tests/testDebugUnitTest/index.html`.
- `./gradlew testDebugUnitTest` → exit `0`; full host suite contains 48 tests
  across eight XML suites, zero skipped/failures/errors. Report:
  `app/build/reports/tests/testDebugUnitTest/index.html`.
- `./gradlew clean assembleDebug` → exit `0`; APK:
  `app/build/outputs/apk/debug/app-debug.apk`; SHA-256
  `71e4c883beca24dca25c171a849508a839c476a14e96ff2dac609fa62cdbd66d`.
- `node scripts/mb-lint.mjs` → exit `0`; `mb-lint passed (77 files)`.
- `git diff --check` → exit `0`; no whitespace errors.
- Scoped dependency/boundary checks → exit `0`: no new Gradle dependency,
  Settings/Display direct provider access, or Display private-store bypass.
- Catalog shape check → exit `0`; `catalog_rows=34079`, every row has ten
  tab-separated columns and the Khujand row is present.
- Attribution order check → exit `0`; attribution line `489` precedes
  back-icon line `506`.
- Source/evidence scan and APK `classes.dex` string scan → exit `0`; no
  `X-Yandex-Weather-Key` or synthetic credential literal in production,
  evidence or packaged APK.

These are executor self-attested supporting receipts. The broad dirty/untracked
worktree means no command is offered as a bounded `/verify` reuse candidate;
fresh verification remains due.

## Target status

- `adb devices -l` → exit `0`, no attached device.
- `emulator -list-avds` → one inactive `Tecno_Pova_6_API_35` AVD is listed;
  no emulator was started.
- Target-only Settings readability/navigation evidence: `DEFERRED`,
  non-blocking, with residual risk on 1280×720 Android 11 rendering and actual
  navigation/readability. No runtime PASS is claimed.
