---
description: Independent functional verifier report for TASK-019-T3-FT-008-W16 Attempt 1.
status: final
task_id: TASK-019-T3-FT-008-W16
stage_id: S-VERIFY
---
# Verifier report — TASK-019-T3-FT-008-W16

## Result

- Functional result: `FAIL`.
- Fresh clean build passed; targeted Settings tests passed `10/10`; full host
  suite passed `69/69`; `mb-lint` and `git diff --check` passed.
- `FT-008-AC-006`, `FT-008-AC-007`, `FT-008-AC-008` and the behavioral part of
  `FT-008-AC-001` were independently observed as passing.
- `FT-008-AC-001 / REQ-024` fails as a complete claim because
  `red-green-evidence.md:45` contains one unredacted synthetic-key marker in
  durable evidence while claiming that marker is absent. The raw value is not
  repeated in this report.

## Fresh verifier evidence

- `./gradlew clean assembleDebug` → exit `0`, `34` actionable tasks.
- Focused forced host rerun → exit `0`, `10` tests, no failures/errors/skips.
- Full forced host rerun → exit `0`, `69` tests, no failures/errors/skips.
- Runtime synthetic-marker scan → `0` hits in source/resources,
  protocol/evidence, reports/test-results and `0` hits in decompressed APK
  entries; credential-literal shape scan → `0` candidates.
- Known-disallowed-literal scan → `1` hit in task executor evidence, the
  blocking finding above.
- Packaged-resource inspection found Open-Meteo URL/licence, GeoNames,
  provider/key and Back resources; deterministic/source ordering puts both
  attributions before final Back.
- App diff scope is exactly three task paths; no provider
  transport/dispatch/cache/history/hourly/long-term implementation is present.

## Safety and residuals

- No emulator/AVD/QEMU, `adb`, physical device, real credential, live provider
  call or runtime PASS claim.
- Physical UI and live-provider evidence remain deferred/unclaimed. Downstream
  provider/forecast scope remains with `TASK-020` through `TASK-022`.
- Task status remains `in_progress`.

## Recommended scheduler action

Run `/exe TASK-019-T3-FT-008-W16` retry to redact/remove the prohibited raw
synthetic marker and correct the contradictory evidence statement, rerun the
secret checks, then return to fresh `/verify TASK-019-T3-FT-008-W16`.
