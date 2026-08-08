---
description: Verifier-owned functional and boundary probes for TASK-010-T3-FT-008-W9.
status: active
---
# FT-008 verifier-owned probe

## Basis

- Task: `TASK-010-T3-FT-008-W9`, tier `T3`, attempt `1`.
- Probe time: `2026-08-08T08:38:04+05:00`.
- Executor receipts were not reused: the worktree has broad pre-existing
  tracked/untracked changes and the receipts are explicitly supporting-only.
- No live credential, network call, emulator, device install or production
  state mutation was used.

## Fresh functional checks

- `./gradlew testDebugUnitTest --tests com.hozayushka.app.SettingsLocationTest`
  — exit `0`; direct report has six tests, zero skipped/failures/errors. The
  six methods cover key validation/redaction, default/selected coordinates and
  refresh request, offline country-first/scoped catalog search, missing-key,
  provider-failure and invalid-credential/unknown-city preservation.
- `./gradlew testDebugUnitTest` — exit `0`; eight XML suites, 48 tests,
  zero skipped/failures/errors.
- `./gradlew clean assembleDebug` — exit `0`; APK SHA-256
  `71e4c883beca24dca25c171a849508a839c476a14e96ff2dac609fa62cdbd66d`.

## Fresh static and artifact probes

- `node scripts/mb-lint.mjs` — exit `0` (`77 files`).
- `git diff --check` — exit `0`.
- Consumer boundary scan found no direct provider-adapter/request import or
  private Settings/Weather/Timer store access in Main Display, Forecast
  Sessions or Settings consumers; no new dependency or event/Google/network
  infrastructure was found.
- Production/assets/resources and packaged `classes.dex` contain neither the
  provider header literal nor the synthetic credential literal. Task protocol
  and evidence contain no raw credential. `SettingsState.toString()` routes
  the key through the wrapper whose string form is `[REDACTED]`.
- The bundled asset has `34079` rows, `0` non-ten-column rows, exactly one
  `1514879` Khujand row, no duplicate city IDs, and `34079` rows with all
  required non-optional columns. The source attribution use is line `489` and
  the final back-icon use is line `506`.
- `adb devices -l` reported no attached target; `emulator -list-avds` reported
  only an inactive AVD. Target-only Settings readability/navigation evidence is
  `DEFERRED` and non-blocking; no runtime PASS is inferred.

## Claim mapping

- `FT-008-AC-001 / REQ-017, REQ-024`: pass — valid local callback-scoped key,
  blank/whitespace validation, last-valid preservation and redacted state/
  artifact boundary.
- `FT-008-AC-002 / REQ-017`: pass — default city ID, selected location
  persistence projection and coordinate-bearing Weather Context request.
- `FT-008-AC-003 / REQ-018`: pass — case-insensitive country search, empty
  city result without country selection, selected-country filtering and valid
  immutable bundled asset shape.
- `FT-008-AC-004 / REQ-018`: pass — Russian-preferred/canonical fallback model
  and Russian/canonical/ASCII query path.
- `FT-008-AC-005 / REQ-018`: pass — attribution is in the Settings content
  before the final back action.
- `FT-008-AC-006 / REQ-017, REQ-018, REQ-024`: pass — missing key, invalid
  credential, network and unknown-city owning messages preserve valid settings;
  full suite retains the stable timer/display regression coverage.

## Result

The executor artifact says seven FT-008 tests, while the current report and
source contain six. This count discrepancy is not used as proof; the direct
rerun and source-to-claim mapping above are used instead. No required claim
failed or remained unproved after that correction.
