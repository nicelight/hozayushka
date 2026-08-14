---
description: Independent functional verifier report for TASK-019-T3-FT-008-W16 Attempt 2.
status: final
task_id: TASK-019-T3-FT-008-W16
stage_id: S-VERIFY
attempt: 2
---
# Verifier report — TASK-019-T3-FT-008-W16 Attempt 2

## Result

- Current functional result: `PASS`.
- Fresh clean build passed; targeted Settings tests passed `10/10`; full host
  suite passed `69/69`; `mb-lint` and `git diff --check` passed.
- The prohibited Attempt 1 synthetic marker is absent from the relevant
  workspace/task/generated evidence and every decompressed APK entry.
- All task-owned `FT-008-AC-001`, `FT-008-AC-006`, `FT-008-AC-007` and
  `FT-008-AC-008` behavior and scope claims were independently observed.

## Fresh evidence

- Debug APK SHA-256:
  `b2399d0c27d43949fe7bf58909de89cb958eef7b75c313b92c838707c0d91eeb`.
- Full-suite Settings XML SHA-256:
  `a8c5840d5309650e86253f91eccc23fa9d71d517eac7e7d2d4d199dc17acdcf2`.
- Verifier-owned scans: exact marker `0` workspace hits and `0` decompressed
  APK-entry hits; credential-shape groups `0`; APK unredacted-`appid` groups
  `0`.
- Packaged Open-Meteo URL/licence, GeoNames, provider/key and Back resources
  are present; deterministic/source order puts both attributions before Back.
- App diff is exactly the three task paths; forbidden provider/forecast and
  dependency/build scope is untouched.

## Claim map

- `FT-008-AC-001 / REQ-024`: Open-Meteo no-key default, explicit
  OpenWeather-only local key and corrected durable evidence hygiene pass.
- `FT-008-AC-006 / REQ-017, REQ-018, REQ-024, REQ-027`: contextual
  missing/invalid-key, network/provider and unknown-city failures preserve
  last-valid state and make no fallback claim.
- `FT-008-AC-007 / REQ-027`: first-run default, explicit switch, auto-save,
  reopen and failure-stable selection pass.
- `FT-008-AC-008 / REQ-028`: Open-Meteo plus GeoNames attribution content and
  order before final Back pass.
- Registered Settings ownership/refresh edge and forbidden scope pass.

## Safety, residuals and route

- No real credential, live call, emulator/AVD/QEMU, `adb` or physical device
  was used. No live-provider or device-runtime PASS is claimed.
- Physical UI/live-provider evidence remains deferred; downstream transport and
  forecast outcomes remain with `TASK-020` through `TASK-022`.
- Authoritative status remains `in_progress`.
- Exact next scheduler action: `/red-verify TASK-019-T3-FT-008-W16`.
