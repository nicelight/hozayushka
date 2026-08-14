---
description: Host build, unit and static gate evidence for TASK-025-T3-FT-002-W22.
status: supporting
task_id: TASK-025-T3-FT-002-W22
attempt: 1
---
# Host gates

## Required gates

| Gate | Exact command | Result |
|---|---|---|
| Clean Android debug build | `./gradlew clean assembleDebug` | exit `0`, `BUILD SUCCESSFUL` |
| Complete host unit suite | `./gradlew testDebugUnitTest` | exit `0`, `BUILD SUCCESSFUL` |
| Static diff integrity | `git diff --check` | exit `0`, no output |

The clean build and full unit suite were run after the production/test change;
the final unit suite included all existing tests and the new focused display
tests. No emulator, AVD, QEMU, Android Studio virtual device, adb, device,
network, provider or credential action was used.

## Focused host observations

- Four-card order remains `yesterday/today/tomorrow/day_after`.
- Measured W21 row widths remain `223/279/223/223`; Today is larger and the
  three non-Today cards are equal; gaps remain `16/16/16`.
- Filled state input is consumed through existing `WeatherProjection` and
  `WeatherCardProjection`; stale/NO_DATA projections retain null illustration
  and null temperature semantics.
- `WeatherIllustrationCanvas.moonPhaseFraction(null)` and `"regular"` use the
  regular-moon fallback; supplied numeric/named values are accepted without
  crash.
- `WeatherCardContentGeometry` reports illustration bounds disjoint from
  temperature/date/pressure bounds at the 223×444 row-card geometry.

## Supporting command history

- First post-change unit probe reached production compilation but failed only
  in the newly added test due missing imports; the test support was corrected
  inside the hard boundary. This setup failure is not RED evidence.
- Repeated `./gradlew testDebugUnitTest` after correction: exit `0`.
- Final `./gradlew clean assembleDebug`: exit `0`.
- Final `./gradlew testDebugUnitTest`: exit `0`.
- Final `git diff --check`: exit `0`.

## Artifact checksums

- `illustration-contact-sheet.png` — SHA-256
  `b8b44ec1949c20a3eb1e47d3f23f5f8da18f0516c7d42e66fde08ac040d7c7ba`
- `illustration-contact-sheet.svg` — SHA-256
  `d00ae9be5f3842ffdfb22bcfef3ad1301c94029beca534dcc857fbca1c9ddbae`
- `illustration-bounds.json` — SHA-256
  `251bb99e3e356ec60b295275fabc0cdcd8e1f11a907d74d712134e62698a862a`
- `illustration-red-baseline.svg` — SHA-256
  `490a3ae34fb97f6f0d5813e5dbb6080d9278cbf3544b507cc977286f8f7c3c05`

## Reuse-candidate decision

No `/verify` reuse candidate is offered: the worktree has broad unrelated
tracked/untracked protocol and evidence state, so the complete command read
surface is not conservatively bounded by this task-local receipt. The results
remain executor supporting evidence and must be independently rechecked by
`/verify`.
