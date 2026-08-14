# Fresh W30 baseline probe — attempt 1

## Decision

RED_NOT_APPLICABLE: the current baseline is claim-equivalent GREEN for the
W30-owned host claims. The accepted reason applies: intentionally breaking an
already accepted display would falsify the accepted behavior and manufacture a
failure. No production/test behavior write was made.

This is fresh W30 evidence. W26, W28 and W29 artifacts were not used as W30
RED/GREEN; they remain provenance context only.

## Input state

- Task: TASK-033-T3-FT-001-W30
- Attempt: 1
- CWD: /home/serg/Projects/Mobile_APPS/hozayushka
- Repository revision: 4ab1e1fd538f92ab3e705193a4b236777b6616bf
- Current two-file diff basis: 2c18f3a6f99dcd617a24b6ebaac44894804b03de8eae55758f956fa99eb57fb2
- Pre/post behavior files: DisplayCapability.kt and DisplayProjectionTest.kt were already modified in the worktree; W30 added no delta.
- Probe state: disposable host-only Java reflection probe, redacted deterministic fixtures, Android Color host stub under /tmp/androidstub; no emulator/device/adb/network/provider/audio runtime.

## Exact command

    javac -d /tmp/androidstub /tmp/androidstub/android/graphics/Color.java && javac -cp "app/build/intermediates/compile_app_classes_jar/debug/bundleDebugClassesToCompileJar/classes.jar:/home/serg/Android/Sdk/platforms/android-35/android.jar:$(find /home/serg/.gradle/caches/modules-2/files-2.1/org.jetbrains.kotlin -path '*/kotlin-stdlib/2.0.20/*/kotlin-stdlib-2.0.20.jar' -print -quit)" -d /tmp /tmp/W30Probe.java && task_stdlib=$(find /home/serg/.gradle/caches/modules-2/files-2.1/org.jetbrains.kotlin -path '*/kotlin-stdlib/2.0.20/*/kotlin-stdlib-2.0.20.jar' -print -quit); java -cp "/tmp/androidstub:/tmp:app/build/intermediates/compile_app_classes_jar/debug/bundleDebugClassesToCompileJar/classes.jar:/home/serg/Android/Sdk/platforms/android-35/android.jar:$task_stdlib" W30Probe

## Exact output

<pre>
Note: /tmp/W30Probe.java uses unchecked or unsafe operations.
Note: Recompile with -Xlint:unchecked for details.
W30_PROBE attempt=1 phase=pre-behavior-write fixture=redacted host-only
W30_PROBE baseline_claim_check=claim-equivalent-GREEN-if-all-receipts-pass
SIZE 2460x1080 clock=12:34 clockRegion=[551,24,2208,374] fullHHmmBounds=[942.0,24.0,1817.0,374.0] measured=875.0x350.0 available=1657x350 textSizePx=291.66666 strictFits=true fitsWithin0.01=true roundingDelta=-782.0x0.0 aboveCards=true
  cardShellBounds=[DisplayBounds(left=32, top=374, right=527, bottom=1056), DisplayBounds(left=551, top=374, right=1170, bottom=1056), DisplayBounds(left=1194, top=374, right=1689, bottom=1056), DisplayBounds(left=1713, top=374, right=2208, bottom=1056)]
  slotBounds/order=YESTERDAY=[32,374,527,1056] | TODAY=[551,374,1170,1056] | TOMORROW=[1194,374,1689,1056] | DAY_AFTER=[1713,374,2208,1056]
  slots NO_DATA=YESTERDAY=EMPTY_SHELL | TODAY=EMPTY_SHELL | TOMORROW=EMPTY_SHELL | DAY_AFTER=EMPTY_SHELL
  slots PARTIAL=YESTERDAY=SHELL | TODAY=VALUE | TOMORROW=SHELL | DAY_AFTER=SHELL
  slots POPULATED=YESTERDAY=VALUE | TODAY=VALUE | TOMORROW=VALUE | DAY_AFTER=VALUE
  presetTouchTargets=[DisplayBounds(left=2208, top=186, right=2428, bottom=406), DisplayBounds(left=2208, top=430, right=2428, bottom=650), DisplayBounds(left=2208, top=674, right=2428, bottom=894)] equalCircular=true
  preset FIRST label=3 м color=#FF7A00 selected=false active=true radial=#FFFF9F47,#FFFF7A00,#FFAD5300 rim=11.0 activeRim=13.200001 glowLayers=3 glowSpreads=[5.5, 11.0, 16.5]
  preset SECOND label=10 м color=#FF4FA3 selected=true active=false radial=#FFFF80BD,#FFFF4FA3,#FFAD2C6A rim=11.0 activeRim=13.200001 glowLayers=3 glowSpreads=[5.5, 11.0, 16.5]
  preset THIRD label=30 м color=#A855F7 selected=false active=false radial=#FFC487FF,#FFA855F7,#FF6E31A8 rim=11.0 activeRim=13.200001 glowLayers=3 glowSpreads=[5.5, 11.0, 16.5]
SIZE 1280x720 clock=12:34 clockRegion=[273,24,1028,252] fullHHmmBounds=[365.5,23.999992,935.5,252.0] measured=570.0x228.00002 available=755x228 textSizePx=190.0 strictFits=false fitsWithin0.01=true roundingDelta=-185.0x1.5258789E-5 aboveCards=true
  cardShellBounds=[DisplayBounds(left=32, top=252, right=249, bottom=696), DisplayBounds(left=273, top=252, right=546, bottom=696), DisplayBounds(left=570, top=252, right=787, bottom=696), DisplayBounds(left=811, top=252, right=1028, bottom=696)]
  slotBounds/order=YESTERDAY=[32,252,249,696] | TODAY=[273,252,546,696] | TOMORROW=[570,252,787,696] | DAY_AFTER=[811,252,1028,696]
  slots NO_DATA=YESTERDAY=EMPTY_SHELL | TODAY=EMPTY_SHELL | TOMORROW=EMPTY_SHELL | DAY_AFTER=EMPTY_SHELL
  slots PARTIAL=YESTERDAY=SHELL | TODAY=VALUE | TOMORROW=SHELL | DAY_AFTER=SHELL
  slots POPULATED=YESTERDAY=VALUE | TODAY=VALUE | TOMORROW=VALUE | DAY_AFTER=VALUE
  presetTouchTargets=[DisplayBounds(left=1038, top=36, right=1238, bottom=236), DisplayBounds(left=1038, top=260, right=1238, bottom=460), DisplayBounds(left=1038, top=484, right=1238, bottom=684)] equalCircular=true
  preset FIRST label=3 м color=#FF7A00 selected=false active=true radial=#FFFF9F47,#FFFF7A00,#FFAD5300 rim=10.0 activeRim=12.0 glowLayers=3 glowSpreads=[5.0, 10.0, 15.0]
  preset SECOND label=10 м color=#FF4FA3 selected=true active=false radial=#FFFF80BD,#FFFF4FA3,#FFAD2C6A rim=10.0 activeRim=12.0 glowLayers=3 glowSpreads=[5.0, 10.0, 15.0]
  preset THIRD label=30 м color=#A855F7 selected=false active=false radial=#FFC487FF,#FFA855F7,#FF6E31A8 rim=10.0 activeRim=12.0 glowLayers=3 glowSpreads=[5.0, 10.0, 15.0]
W30_PROBE boundary=display-read-only; provider/weather/timer/lifecycle/audio untouched
W30_PROBE result=PASS_CLAIM_EQUIVALENT_BASELINE_CANDIDATE
</pre>

## Claim reading

- At 2460x1080, 12:34 is measured as 875.0x350.0 inside 1657x350, with
  full bounds [942.0,24.0,1817.0,374.0], above the four card shells beginning
  at y=374.
- At 1280x720, 12:34 is measured as 570.0x228.00002 inside 755x228. The
  0.000015 px height excess is floating-point rounding; fitsWithin0.01=true,
  and the rendered bounds remain the clock region [273,24,1028,252], above
  cards beginning at y=252. No observable clipping/overflow is present.
- Both hosts retain ordered YESTERDAY|TODAY|TOMORROW|DAY_AFTER bounds and stable
  shells for NO_DATA, partial and populated fixtures.
- All three existing presets retain order, labels, colors, selected/active
  flags and equal circular touch targets. Each has a one-color radial shade,
  current wide rim, and exactly three outward fading static glow layers.

## RED/GREEN contract record

- RED applicability: the mapped visual claims are applicable.
- RED result: RED_NOT_APPLICABLE, because baseline claim-equivalent GREEN was
  observed before any behavior write; no artificial break was introduced.
- GREEN/supporting result: the same fresh W30 baseline receipt supports the
  accepted alternative proof. Final verifier-owned GREEN remains due under T3
  workflow.
- Current source scope after probe: unchanged; no behavior write occurred.

