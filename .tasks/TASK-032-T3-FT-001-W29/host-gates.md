---
description: Host gate receipts for TASK-032-T3-FT-001-W29 recovery.
status: supporting
task_id: TASK-032-T3-FT-001-W29
tier: T3
attempt: 2
---
# Host gates — W29 Attempt 2

All Gradle commands used `--offline --no-daemon`; no network/provider/device
path was used. The two allowed source/test files were unchanged during the
recovery gates. The worktree had broad pre-existing unrelated dirt.

## Input basis

- revision: `4ab1e1fd538f92ab3e705193a4b236777b6616bf5`
- `DisplayCapability.kt` SHA-256:
  `1df3d1bd59abd40317b91b7a38fdcc41495de75550ba9588297202c4d2111bbb`
- `DisplayProjectionTest.kt` SHA-256:
  `0cdf53a0cc1fdb0c6679d87a8cecb8aedac30b729ed765a4a3d32a56ee97d77a`
- scoped diff before/after: exactly the two task boundary paths, both already
  modified before recovery.

## Required gates

| Gate / exact command | Exit | Observable result |
|---|---:|---|
| `./gradlew --offline --no-daemon --console=plain :app:testDebugUnitTest --tests com.hozayushka.app.DisplayProjectionTest` | 0 | `25/25`, zero failures/errors/skips; W29 focused XML at `app/build/test-results/testDebugUnitTest/TEST-com.hozayushka.app.DisplayProjectionTest.xml` |
| `./gradlew --offline --no-daemon --console=plain testDebugUnitTest` | 0 | `113/113`, zero failures/errors/skips across host XML set |
| `./gradlew --offline --no-daemon --console=plain clean assembleDebug` | 0 | `BUILD SUCCESSFUL` in 17s; existing `MainActivity.kt` deprecation warning only |
| `./gradlew --offline --no-daemon --console=plain lintDebug` | 0 | `BUILD SUCCESSFUL`; report `app/build/reports/lint-results-debug.html` |
| `git diff --check` | 0 | no whitespace errors |

## Claim output observed in focused test

- clock: `2460x1080` `available=1657x350,sizePx=291.66666,measured=875.0x350.0`; `1280x720` `available=755x228,sizePx=190.0,measured=570.0x228.00002`;
- slots: four ordered slots in NO_DATA, async and populated redacted fixture;
- presets: colors `#FF7A00/#FF4FA3/#A855F7`, rim `10.0`, active rim `12.0`, glow layers `3`, outer glow `15.0` at side `200`.

## Receipt status

These are current executor recovery gate receipts and remain
`supporting-only`: the clean source input for a W29 pre-write RED was not
preserved by the prior implementation, and broad dirty inputs make reuse as a
conservative `/verify` candidate ineligible. Independent verification must
rerun or independently inspect the gates.
