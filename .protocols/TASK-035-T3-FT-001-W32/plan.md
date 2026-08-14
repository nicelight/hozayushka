---
description: Execution plan for TASK-035-T3-FT-001-W32.
status: active
---
# Plan — TASK-035-T3-FT-001-W32

## Goal
Recompose the existing Main Display shell so the weather band is 25–30% of
frame height, the clock zone is 70–75%, all four cards share height/bottom,
HH:mm is complete and maximum-fit, city/date remains above Yesterday, and the
timer rail remains separate.

## Non-goals
- Weather Context/provider/cache/freshness/data semantics.
- Timer & Alert lifecycle/countdown/cancellation/overdue/audio semantics.
- Runtime policy, resources, dependencies, public contracts, graph edges,
  scheduler checkpoint, historical task state, device/emulator/upload.

## Scope
### In scope
- `app/src/main/kotlin/com/hozayushka/app/display/DisplayCapability.kt`
- `app/src/test/kotlin/com/hozayushka/app/DisplayProjectionTest.kt`

### Out of scope
- Every path in `runtime_context.forbidden_scope`.

## Preflight-confirmed change surface
- Exact hard `write_boundary`: yes.
- Existing dirty overlap: both boundary files already contain preserved
  prior local work; W32 edits remain in those files only.
- Forbidden scope / stop-condition check: clear.

## Applicable quality gates
- `./gradlew clean assembleDebug`
- `./gradlew :app:testDebugUnitTest --tests com.hozayushka.app.DisplayProjectionTest`
- `./gradlew testDebugUnitTest`
- `./gradlew lintDebug`
- `git diff --check`

## Claim-linked RED / GREEN
- applicability: applicable
- accepted claim locators: FT-001-AC-002 / REQ-002 / REQ-005 / REQ-023 and
  `main-display-presentation.md#claim-linked-evidence`
- planned RED: deterministic host geometry/state probe at 2460×1080 and
  1280×720 before W32 behavior write.
- planned GREEN: same host path after the bounded composition correction,
  with machine-readable geometry, state matrix, contact sheet and rubric.
- T3 isolation: no network, credentials, adb, emulator, device launch or APK
  upload; redacted deterministic fixtures only.

## MB-SYNC handoff / owner
- Owner: `/verify TASK-035-T3-FT-001-W32`; `/exe` does not run `/verify`,
  `/red-verify` or `/mb-sync`.
