---
description: Fresh verifier-owned functional evidence for TASK-035-T3-FT-001-W32.
status: final
task_id: TASK-035-T3-FT-001-W32
stage_id: S-VERIFY
---
# Verifier-owned evidence — TASK-035-T3-FT-001-W32

## Fresh host observations

The current implementation was rerun from rebuilt local classes with Gradle
`--offline` (no network). The focused `DisplayProjectionTest` suite and the
full host unit suite both exited 0. Raw geometry was independently recomputed
from `geometry.json`:

| frame | RED band / clock zone | GREEN band / clock zone | accepted target |
|---|---:|---:|---|
| 2460×1080 | 60.27778% / 39.722222% | 27.962962% / 72.03704% | 25–30% / 70–75% |
| 1280×720 | 58.88889% / 41.111112% | 27.916667% / 72.08333% | 25–30% / 70–75% |

GREEN card heights/bottoms are `[302,302,302,302]` / `[1056,1056,1056,1056]`
and `[201,201,201,201]` / `[696,696,696,696]`. The slot matrix preserves four
slots in `YESTERDAY,TODAY,TOMORROW,DAY_AFTER` order for `NO_DATA`,
`PARTIAL_ASYNC` and `POPULATED_REDACTED`.

The complete clock model measures `1657.0×662.8` inside `1657×730` at
2460×1080 and `755×302` inside `755×471` at 1280×720; the recorded bounds are
contained and non-overlapping. City/date end at the Yesterday top. Illustrations
remain above and shorter than temperatures; preset geometry is circular,
separate and uses the existing orange/pink/purple radial layers.

## Independent commands

- `./gradlew --offline :app:testDebugUnitTest --tests com.hozayushka.app.DisplayProjectionTest`
  — PASS.
- `./gradlew --offline testDebugUnitTest` — PASS.
- `./gradlew --offline clean assembleDebug` — PASS.
- `./gradlew --offline lintDebug` — PASS.
- `git diff --check` — PASS.

## Claim boundary

Executor RED is retained as the honest pre-write claim path in
`red-focused.log`; the reviewer did not mutate production code to manufacture
a second RED. Executor artifacts remain supporting evidence, while the host
reruns and bounded source review above are verifier-owned observations.
No W32 device/runtime proof is inferred; `target-device.md` remains deferred.
