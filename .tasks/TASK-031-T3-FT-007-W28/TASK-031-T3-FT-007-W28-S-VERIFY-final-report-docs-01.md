---
description: Independent functional verification report for TASK-031-T3-FT-007-W28.
status: active
task_id: TASK-031-T3-FT-007-W28
tier: T3
---
# `/verify` report — TASK-031-T3-FT-007-W28

Functional verification is complete for the task-owned claim
`FT-007-AC-006 / REQ-015 / REQ-023`. The task remains `in_progress`; no
lifecycle, scheduler, checkpoint, terminal-state or `/mb-sync` mutation was
performed.

## Evidence checked

- Exact indexed card, FT-007 W28 plan, implementation plan, T3 protocol
  (`context.md`, `plan.md`, `progress.md`, `handoff.md`, `verification.md`),
  direct task-linked contracts/lifecycle/runtime specs, and all task-local
  evidence under `.tasks/TASK-031-T3-FT-007-W28/`.
- Claim-linked RED: `red-baseline.md` records the pre-W28 `76f` overdue
  counter, opaque preset fill and absent dedicated circle against idle `188.75f`
  and W27 active `228f`.
- Fresh verifier-owned GREEN: focused
  `./gradlew --offline --no-daemon :app:testDebugUnitTest --tests com.hozayushka.app.DisplayProjectionTest`
  passed. Current artifact
  `app/build/test-results/testDebugUnitTest/TEST-com.hozayushka.app.DisplayProjectionTest.xml`
  reports `22/22`, `0` failures/errors and prints the decisive W28 values.
- Fresh full host
  `./gradlew --offline --no-daemon testDebugUnitTest` passed; the current XML
  set under `app/build/test-results/testDebugUnitTest/` totals `110/110`,
  `0` failures/errors, including `TimerLifecycleTest` `5/5` and
  `OverdueAlertTest` `7/7`.
- Fresh `./gradlew --offline --no-daemon clean assembleDebug` passed;
  `node scripts/mb-lint.mjs` passed (`78 files`); `git diff --check` passed.

## Claim results

- Dedicated overdue surface: PASS. The overdue branch hides `mainShell` and
  the overlay contains only the transparent backdrop, plus and elapsed counter;
  weather cards, city, date and standard card shell are not rendered on that
  surface. Static source locator:
  `app/src/main/kotlin/com/hozayushka/app/display/DisplayCapability.kt:1514-1529`.
- Hierarchy/stability: PASS. At fixed `1280x720`, idle `188.75`, active
  `228.0`, overdue elapsed `256.0`, plus `280.0`; full elapsed value is
  `00:10:00`, stable through `600999ms` and advancing at the next second.
  Bounds are disjoint and fit the surface. Artifact:
  `.tasks/TASK-031-T3-FT-007-W28/geometry.json` and focused XML.
- Visual treatment: PASS for host geometry. The backdrop is transparent,
  circle/stroke-only and receives `PresetPresentation.colorHex(activeSlot)`;
  the plus blinks at `0/382/764ms` while the numeric counter does not. Static
  source locator: `DisplayCapability.kt:1430-1459,1517-1529,1994-2025`.
- Boundary/ownership: PASS. Scoped W28 diff is exactly
  `DisplayCapability.kt` and `DisplayProjectionTest.kt`; W28-specific symbols
  occur only there. Timer arithmetic, lifecycle, audio request/policy and
  platform adapter remain read-only regression owners. Evidence:
  `.tasks/TASK-031-T3-FT-007-W28/{lifecycle-regression.md,audio-regression.md,boundary-static-review.md}`.

## Deferred separation

Target readability/fullscreen/custom-ROM lifecycle and physical audio remain
`DEFERRED`. Host geometry, host tests and historical W23 fake-audio evidence
are not runtime/device/physical-audibility PASS. No emulator, device, adb,
network or audio runtime was used.

VERDICT: PASS

## Handoff

Run the required independent T3 `/red-verify TASK-031-T3-FT-007-W28`. Closure
authority remains with the lifecycle owner.
