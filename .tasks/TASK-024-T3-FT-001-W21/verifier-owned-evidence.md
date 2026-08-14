---
description: Fresh verifier-owned evidence for TASK-024-T3-FT-001-W21 functional and semantic review.
status: final
task_id: TASK-024-T3-FT-001-W21
role: Reviewer
---
# Verifier-owned evidence — TASK-024-T3-FT-001-W21

## Fresh commands

| Check | Command | Result |
|---|---|---|
| Geometry outcome probe | `./gradlew --offline --no-daemon testDebugUnitTest --tests com.hozayushka.app.DisplayProjectionTest.mainDisplayGeometryKeepsLeftCenterRightRegionsAndCardRelations` | exit 0, `BUILD SUCCESSFUL` |
| Clean build | `./gradlew --offline --no-daemon clean assembleDebug` | exit 0, `BUILD SUCCESSFUL` |
| Full host suite | `./gradlew --offline --no-daemon testDebugUnitTest` | exit 0, 96 tests, 0 failures/errors/skips |
| Diff integrity | `git diff --check` | exit 0 |

The commands were host-only and offline. No emulator/AVD/QEMU, adb/device,
network, credential or secret-bearing path was used.

## Geometry observation

At the deterministic 1280x720 model, measured card bounds are:

`[32,1078,255,1474] [271,1078,550,1474] [566,1078,789,1474] [805,1078,1028,1474]`

Widths are `223/279/223/223`; order is
`yesterday/today/tomorrow/day_after`; gaps are `16/16/16`. City/date bounds
are in the left header above Yesterday; clock bounds are central/upper above
Today/Tomorrow/Day-after; preset bounds begin at the right edge `x=1028`.
The rendered RED/GREEN comparison is
`red-green-contact-sheet.svg`; fresh RED is recorded in `red-baseline.md`.

## Semantic boundary observation

The W21 composition adds only Main Display-local geometry and keeps timer
gesture dispatch, city routing, weather projection reads, forecast intents and
preset handling on their existing paths. `WeatherCardSlot` and projection
construction retain `YESTERDAY, TODAY, TOMORROW, DAY_AFTER`. No new
inter-module edge, owner, storage write, resource or public capability contract
appears in the task-local delta. The current worktree has unrelated provider
migration/resource changes outside W21; they are not attributed to this task.

## Deferred target evidence

Samsung GT-I9300I Android 11 custom-ROM 1280x720 readability, fullscreen and
keep-screen-on remain `DEFERRED` with residual risk. No runtime/device PASS is
claimed.
