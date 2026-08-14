---
description: Independent verifier-owned functional evidence for TASK-033 W30.
status: final
task_id: TASK-033-T3-FT-001-W30
tier: T3
---

# Verifier-owned evidence — TASK-033-T3-FT-001-W30

## Scope and provenance

This receipt was produced in the fresh independent `/verify` session after the
clean rebuild and before any verifier behavior write. No production/test
behavior write was made. The probe uses a disposable host-only Android Color
stub and redacted deterministic weather fixtures; it does not launch an
emulator/device/runtime and does not use adb, network, provider, lifecycle,
timer execution or audio.

Historical W26/W28/W29 artifacts were not used as W30 RED/GREEN evidence. They
remain provenance context only. The probe source is disposable
`/tmp/W30VerifierProbe.java`, distinct from the executor's W30 probe.

## Exact fresh probe command

```text
javac -d /tmp/androidstub /tmp/androidstub/android/graphics/Color.java && javac -cp 'app/build/intermediates/compile_app_classes_jar/debug/bundleDebugClassesToCompileJar/classes.jar:/home/serg/Android/Sdk/platforms/android-35/android.jar:/home/serg/.gradle/caches/modules-2/files-2.1/org.jetbrains.kotlin/kotlin-stdlib/2.0.20/7388d355f7cceb002cd387ccb7ab3850e4e0a07f/kotlin-stdlib-2.0.20.jar' -d /tmp /tmp/W30VerifierProbe.java && java -cp '/tmp/androidstub:/tmp:app/build/intermediates/compile_app_classes_jar/debug/bundleDebugClassesToCompileJar/classes.jar:/home/serg/Android/Sdk/platforms/android-35/android.jar:/home/serg/.gradle/caches/modules-2/files-2.1/org.jetbrains.kotlin/kotlin-stdlib/2.0.20/7388d355f7cceb002cd387ccb7ab3850e4e0a07f/kotlin-stdlib-2.0.20.jar' W30VerifierProbe
```

## Exact output

```text
Note: /tmp/W30VerifierProbe.java uses unchecked or unsafe operations.
Note: Recompile with -Xlint:unchecked for details.
W30_VERIFY_PROBE phase=fresh-verifier-owned-baseline fixture=redacted host-only
W30_VERIFY_PROBE decision=RED_NOT_APPLICABLE_only_if_all_claims_pass; no_behavior_write
SIZE 2460x1080 clockText=12:34 region=[551,24,2208,374] fullBounds=[942.0,24.0,1817.0,374.0] measured=875.0x350.0 available=1657x350 widthDelta=-782.0 heightDelta=0.0 aboveCards=true
  cards=[DisplayBounds(left=32, top=374, right=527, bottom=1056), DisplayBounds(left=551, top=374, right=1170, bottom=1056), DisplayBounds(left=1194, top=374, right=1689, bottom=1056), DisplayBounds(left=1713, top=374, right=2208, bottom=1056)] order=YESTERDAY|TODAY|TOMORROW|DAY_AFTER
  NO_DATA=YESTERDAY=EMPTY_SHELL | TODAY=EMPTY_SHELL | TOMORROW=EMPTY_SHELL | DAY_AFTER=EMPTY_SHELL
  PARTIAL=YESTERDAY=SHELL | TODAY=VALUE | TOMORROW=SHELL | DAY_AFTER=SHELL
  POPULATED=YESTERDAY=VALUE | TODAY=VALUE | TOMORROW=VALUE | DAY_AFTER=VALUE
  presets=[DisplayBounds(left=2208, top=186, right=2428, bottom=406), DisplayBounds(left=2208, top=430, right=2428, bottom=650), DisplayBounds(left=2208, top=674, right=2428, bottom=894)] circular=true
  visual rim=11.0 activeRim=13.200001 glowLayers=3 spreads=[5.5, 11.0, 16.5]
  preset=FIRST label=3 м color=#FF7A00 selected=false active=true radial=#FFFF9F47,#FFFF7A00,#FFAD5300
  preset=SECOND label=10 м color=#FF4FA3 selected=true active=false radial=#FFFF80BD,#FFFF4FA3,#FFAD2C6A
  preset=THIRD label=30 м color=#A855F7 selected=false active=false radial=#FFC487FF,#FFA855F7,#FF6E31A8
SIZE 1280x720 clockText=12:34 region=[273,24,1028,252] fullBounds=[365.5,23.999992,935.5,252.0] measured=570.0x228.00002 available=755x228 widthDelta=-185.0 heightDelta=1.5258789E-5 aboveCards=true
  cards=[DisplayBounds(left=32, top=252, right=249, bottom=696), DisplayBounds(left=273, top=252, right=546, bottom=696), DisplayBounds(left=570, top=252, right=787, bottom=696), DisplayBounds(left=811, top=252, right=1028, bottom=696)] order=YESTERDAY|TODAY|TOMORROW|DAY_AFTER
  NO_DATA=YESTERDAY=EMPTY_SHELL | TODAY=EMPTY_SHELL | TOMORROW=EMPTY_SHELL | DAY_AFTER=EMPTY_SHELL
  PARTIAL=YESTERDAY=SHELL | TODAY=VALUE | TOMORROW=SHELL | DAY_AFTER=SHELL
  POPULATED=YESTERDAY=VALUE | TODAY=VALUE | TOMORROW=VALUE | DAY_AFTER=VALUE
  presets=[DisplayBounds(left=1038, top=36, right=1238, bottom=236), DisplayBounds(left=1038, top=260, right=1238, bottom=460), DisplayBounds(left=1038, top=484, right=1238, bottom=684)] circular=true
  visual rim=10.0 activeRim=12.0 glowLayers=3 spreads=[5.0, 10.0, 15.0]
  preset=FIRST label=3 м color=#FF7A00 selected=false active=true radial=#FFFF9F47,#FFFF7A00,#FFAD5300
  preset=SECOND label=10 м color=#FF4FA3 selected=true active=false radial=#FFFF80BD,#FFFF4FA3,#FFAD2C6A
  preset=THIRD label=30 м color=#A855F7 selected=false active=false radial=#FFC487FF,#FFA855F7,#FF6E31A8
W30_VERIFY_PROBE boundary=two-file-display-only; weather/provider/timer/lifecycle/audio=read-only
W30_VERIFY_PROBE result=PASS_candidate_if_receipts_and_source_review_agree
```

## Gate reruns

| Gate | Exact command | Result |
|---|---|---|
| Clean Android debug build | `./gradlew clean assembleDebug` | exit 0, `BUILD SUCCESSFUL`; existing MainActivity deprecation warning only |
| Focused display projection suite | `./gradlew :app:testDebugUnitTest --tests com.hozayushka.app.DisplayProjectionTest` | exit 0, `BUILD SUCCESSFUL` |
| Full host unit suite | `./gradlew testDebugUnitTest` | exit 0, `BUILD SUCCESSFUL` |
| Android debug lint | `./gradlew lintDebug` | exit 0, `BUILD SUCCESSFUL`; report written to `app/build/reports/lint-results-debug.html` |
| Static diff integrity | `git diff --check` | exit 0, no output |

## Current source/boundary observation

- `git rev-parse HEAD` → `4ab1e1fd538f92ab3e705193a4b236777b6616bf`.
- The current exact two-file diff hash is
  `2c18f3a6f99dcd617a24b6ebaac44894804b03de8eae55758f956fa99eb57fb2`,
  matching the executor's pre/post W30 basis; W30 added no production/test
  behavior delta.
- The task hard production/test boundary remains exactly
  `app/src/main/kotlin/com/hozayushka/app/display/DisplayCapability.kt` and
  `app/src/test/kotlin/com/hozayushka/app/DisplayProjectionTest.kt`.
- No W30 write touched WeatherCapability, providers/adapters, Settings,
  FoundationRuntime, MainActivity, Timer, resources, lifecycle wiring or audio.
  The current worktree has broader pre-existing migration changes; they are not
  attributed to W30.
- Target/device/fullscreen/keep-screen-on/physical readability evidence remains
  `DEFERRED`; host output is not a runtime PASS.

## Claim mapping

- `FT-001-AC-002 / REQ-002`: both host sizes show a complete `12:34` inside
  the measured central/upper region and above the weather row; the only
  1280×720 excess is `1.5258789E-5` px floating-point rounding.
- `FT-001-AC-002 / REQ-002`: each host retains four ordered shells for
  `NO_DATA`, partial and populated redacted projections without layout shift
  or fabricated values.
- `FT-001-AC-002 / REQ-002`: all three controls retain order, labels, existing
  colors, selected/active flags and circular touch bounds; each has one
  base-color radial shade sequence, a 10/11 px rim (12/13.2 px active) and
  exactly three monotonic outward glow spreads.
- `REQ-023`: host readability/lightweight checks pass; device/runtime half is
  honestly deferred.
- `REQ-001`, `REQ-005` and Timer & Alert are read-only regression/boundary
  alternatives, supported by the fresh probe and static source review; no
  forbidden runtime operation was invoked.
