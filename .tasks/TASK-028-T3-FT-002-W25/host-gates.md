---
description: Host build, focused/full unit and static gate evidence for TASK-028-T3-FT-002-W25.
status: supporting
task_id: TASK-028-T3-FT-002-W25
attempt: 1
---
# Host gates

## Required gates

| Gate | Exact command | Result |
|---|---|---|
| Focused display contract | `./gradlew :app:testDebugUnitTest --tests com.hozayushka.app.DisplayProjectionTest` | exit `0`, `BUILD SUCCESSFUL`, 17 tests |
| Clean Android debug build | `./gradlew clean assembleDebug` | exit `0`, `BUILD SUCCESSFUL` |
| Complete host unit suite | `./gradlew testDebugUnitTest` | exit `0`, `BUILD SUCCESSFUL`, 105 tests, 0 failures/errors/skips |
| Static diff integrity | `git diff --check` | exit `0`, no output |
| Evidence JSON parse | `jq empty illustration-bounds.json pressure-arrow-bounds.json` | exit `0` |

The clean-build compiler emitted only the existing `MainActivity.kt` deprecated
override warning; it did not fail the build. All gates were host-only. No
emulator/AVD/QEMU, Android Studio virtual device, adb/device, provider/network
or credential action was used.

## Focused observations

- `WeatherIllustrationCanvas` retains exactly the six existing enum states and
  uses a centered `0.70f` paint transform; the CLEAR disk ratio is
  `1.1789474×` and the focused test checks the accepted `1.15…1.30` range.
- `PressureArrowCanvas` clamps count to `0…2`, keeps projection direction
  semantics, declares a `5 px` rounded stroke and two separate path segments;
  focused tests prove UP/DOWN preservation and zero suppression.
- At 1280×720, card widths/order/gaps remain `223/279/223/223`,
  `yesterday/today/tomorrow/day_after`, and the existing Today geometry stays
  larger. Existing stale/NO_DATA tests still prove no illustration/pressure
  values.
- Contact sheets and bounds artifacts pass the executor rubric for all six
  states, reduced envelopes, non-overlap and UP/DOWN/zero visibility.

## Static boundary observation

The initial preflight already showed unrelated dirty provider/Weather Context,
adapter, resource and historical Memory Bank/task state. Those changes are not
attributed to W25. The W25 production/test edits are confined to the two hard
boundary files; required protocol/evidence and one papercut are workflow
artifacts. See `boundary-static-review.md`.

## Artifact checksums

- `illustration-contact-sheet.png` — `8c5b3b960829ab5c82750e66473721010cc30fbc170350eb6b71a3ff343ab987`
- `illustration-contact-sheet.svg` — `1b1aedbc765cbd98cc0135f42e21ce323d3da722a12385e8e8533bf268d28d53`
- `illustration-bounds.json` — `9338a0c307bb3b60e99f198d550511160a0a825c997ff9ac42456ae866c8579a`
- `pressure-arrow-contact-sheet.png` — `feb5643919def688d75d5c12e0d4a4a6eef22a5d97b31a8270fbc4fccd40fbc7`
- `pressure-arrow-contact-sheet.svg` — `9b99f2fc20516d68110a66280802bfdcbaea7c8b957b77e9d6850e0cdd684677`
- `pressure-arrow-bounds.json` — `862e45a4a0c1231521cd5a3e9b74e34ecef5ae294c2d323b629c2f2adc8207c9`

## Reuse-candidate decision

No `/verify` reuse candidate is offered: the worktree has broad unrelated
tracked/untracked state, so the complete command read surface is not
conservatively bounded by this task-local receipt. These results are executor
supporting evidence and must be independently rechecked by `/verify`.
