---
description: Execution progress for TASK-035-T3-FT-001-W32.
status: active
---
# Progress — TASK-035-T3-FT-001-W32

## Current status
- state: verifying
- last update: 2026-08-14

## What was done
- Preflight resolved the selected indexed T3 task, completed W31 dependency,
  current Planning Revision 2 and FT-001 planning APPROVE.
- Task status was promoted `planned → ready → in_progress` by `/exe`.
- Attempt 1 was initialized before any prospective probe or production write.
- Bounded W32 geometry correction and claim-linked tests were implemented in
  the exact two behavior files.

## Commands run (with results)
- Read-only task/spec/protocol preflight → OK; details in `context.md`.
- Fresh RED probe → expected claim-specific failure, exit 1; raw values at
  both sizes are in `geometry.json` and `red-focused.log`.
- Focused GREEN → exit 0, 30/30; receipt `focused-final.log`.
- Clean debug build → exit 0; receipt `clean-build.log`.
- Full host suite → exit 0, 118/118; receipt `full-host-tests-final.log`.
- Lint → exit 0; receipt `lint.log`.
- `git diff --check` → exit 0; receipt `diff-check.log`.

## Claim-linked RED / GREEN (T2/T3)
- attempt: 1
- applicability: applicable
- accepted claim locator(s): FT-001-AC-002 / REQ-002 / REQ-005 / REQ-023
- accepted not-applicable reason and alternative proof: Weather Context and
  Timer & Alert behavior claims are read-only regression claims; fixture/static
  boundary review is the alternative proof.
- RED command/probe: `./gradlew :app:testDebugUnitTest --tests com.hozayushka.app.DisplayProjectionTest --tests 'com.hozayushka.app.DisplayProjectionTest.w32CompositionContractFitsBandAndClockZoneAtBothHostSizes' --console=plain`; exit 1 on the pre-correction ratio assertion.
- RED observation and evidence: 2460×1080 band `0.6027778`, clock zone
  `0.39722222`; 1280×720 band `0.5888889`, clock zone `0.41111112`.
  Artifact: `geometry.json`, receipt `red-focused.log`.
- GREEN command/probe: `./gradlew :app:testDebugUnitTest --tests com.hozayushka.app.DisplayProjectionTest --console=plain`; exit 0, 30/30.
- GREEN observation and evidence: 2460×1080 band `0.27962962`, clock zone
  `0.7203704`; 1280×720 band `0.27916667`, clock zone `0.7208333`; all card
  heights/bottoms equal, HH:mm contained, state matrix stable.
  Artifacts: `geometry.json`, `clock-fit.json`, `weather-slot-matrix.json`,
  `visual-rubric.md`, `red-green-contact-sheet.svg`.
- claim-equivalent probe changes and rationale: RED probe was converted to
  the same accepted ratio assertion for GREEN after the production fix; no
  artificial failure or weakened probe was used.
- T3 isolation/cleanup/permission evidence: no external side effect; no adb,
  install/upload, emulator/device, network or credentials.

## Evidence links
- `.tasks/TASK-035-T3-FT-001-W32/geometry.json`
- `.tasks/TASK-035-T3-FT-001-W32/clock-fit.json`
- `.tasks/TASK-035-T3-FT-001-W32/weather-slot-matrix.json`
- `.tasks/TASK-035-T3-FT-001-W32/visual-rubric.md`
- `.tasks/TASK-035-T3-FT-001-W32/host-gates.md`

## Open issues / risks
- Physical rendering, fullscreen, keep-screen-on and custom-device visual
  behavior remain DEFERRED; host evidence is not runtime PASS.
- Existing unrelated dirty files remain preserved.

## Next step (single concrete action)
- Hand off `PASS_FOR_HANDOFF` to `/verify TASK-035-T3-FT-001-W32` without
  changing terminal lifecycle or running upload.
