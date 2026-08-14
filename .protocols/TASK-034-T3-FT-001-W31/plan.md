---
description: Execution plan for TASK-034-T3-FT-001-W31.
status: active
---
# Plan — TASK-034-T3-FT-001-W31

## Goal

Correct only Main Display presentation geometry so the physical TECNO LI6
shows a complete dominant `HH:mm`, reduced secondary weather illustrations,
stable four-slot weather composition and separate timer controls.

## Non-goals

No provider/weather-fetch, timer/runtime ownership, fullscreen owner, resource,
public contract, module, graph, status, scheduler checkpoint or terminal-state
change.

## Preflight-confirmed change surface

- Expected files: `app/src/main/kotlin/com/hozayushka/app/display/DisplayCapability.kt`;
  `app/src/test/kotlin/com/hozayushka/app/DisplayProjectionTest.kt`.
- Additional outcome files: none authorized.
- Hard boundary: present and exact; forbidden scope clear.
- Existing dirty overlap: both hard-boundary files are already modified in the
  workspace; preserve current content and patch only after execution is
  authorized.

## Required gates after unblock

- `./gradlew clean assembleDebug`
- `./gradlew :app:testDebugUnitTest --tests com.hozayushka.app.DisplayProjectionTest`
- `./gradlew testDebugUnitTest`
- `./gradlew lintDebug`
- `git diff --check`
- Fresh host geometry at the actual physical landscape size and `1280x720`.
- Fresh physical RED/GREEN only through `adb -s 1156725456009666`, unlocked.

## Claim-linked RED/GREEN

- applicability: applicable
- attempt: 1
- claim locator: `FT-001-AC-002 / REQ-002 / REQ-023`
- RED: fresh physical baseline captured before behavior write on serial
  `1156725456009666`; see `physical-visual-receipt.md` and `geometry.json`.
- GREEN: same serial/landscape frame after correction; complete `HH:mm`
  `725x218`, largest icon `45x43`, four shells/date-city/timer separation
  preserved; see `physical-main-after.png` and the visual rubric.
- Host geometry: fresh W31 probe at `2460x1080` and `1280x720`; see focused
  test XML and `geometry.json`.
- T3 isolation: only the authorized serial was used; no emulator, network,
  provider call, credential, secret or forbidden path was used.

## Handoff owner

`/verify` and `/red-verify` remain future routes; neither was invoked. Task
status, scheduler ownership, checkpoint and terminal state remain unchanged by
this execution attempt.
