---
task_id: TASK-037-T3-FT-001-W34
stage: verify
status: current
reviewer_owned: true
---
# Verifier-owned evidence — TASK-037-T3-FT-001-W34

## Fresh host checks

All commands were run offline from the repository root; no provider/network
call or credential-bearing input was used.

- `./gradlew --offline clean assembleDebug` — exit `0`, `BUILD SUCCESSFUL`.
- `./gradlew --offline :app:testDebugUnitTest --tests com.hozayushka.app.DisplayProjectionTest --console=plain` — exit `0`; XML reports `31` tests, `0` failures, `0` errors. The W34 mixed fixture testcase is present and its system output reports both required sizes.
- `./gradlew --offline testDebugUnitTest` — exit `0`; the 13 host XML suites sum to `119` tests, `0` failures, `0` errors; no `<failure>` or `<error>` tags were found.
- `./gradlew --offline lintDebug` — exit `0`, `BUILD SUCCESSFUL`.
- `git diff --check` — exit `0`.

Fresh focused W34 output:

- `2460x1080`: band `754..1056`, `302px`, `band_ratio=0.27962962`,
  `clock_zone_ratio=0.7203704`, heights `[302,302,302,302]`, bottoms
  `[1056,1056,1056,1056]`, slots `YESTERDAY=false`, `TODAY=true`,
  `TOMORROW=true`, `DAY_AFTER=true`.
- `1280x720`: band `495..696`, `201px`, `band_ratio=0.27916667`,
  `clock_zone_ratio=0.7208333`, heights `[201,201,201,201]`, bottoms
  `[696,696,696,696]`.
- The mixed test asserts the empty Yesterday projection has no temperature and
  the populated slots retain `23°`, `24°`, `25°`; it does not synthesize an
  empty-day value.

Primary fresh host locator: `app/build/test-results/testDebugUnitTest/TEST-com.hozayushka.app.DisplayProjectionTest.xml`.

## Fresh physical read-only probe

Only `adb -s 1156725456009666` was used. No install/reinstall, emulator,
AVD/QEMU, network or provider action was performed during verification.

- `get-state` → `device`.
- `wm size` → physical `1080x2460`; `dumpsys display` → active logical/app
  frame `2460x1080`, `mCurrentOrientation=1`, landscape/`ROTATION_90`.
- `dumpsys activity top` → `com.hozayushka.app/.app.MainActivity`,
  `mResumed=true`, `mHasWindowFocus=true`, fullscreen bounds
  `0,0-2460,1080`.
- Native View tree → Yesterday child screen bounds `(32,754)-(527,1056)`,
  Today `(551,754)-(1170,1056)`, Tomorrow `(1194,754)-(1689,1056)`,
  Day-after `(1713,754)-(2208,1056)`; all are `302px` high and end at
  `1056`. Clock parent is above the band; timer controls remain three separate
  `220x220` views on the right.
- Window policy → `showing=false`, `inputRestricted=false`,
  `screenState=SCREEN_STATE_ON`, `interactiveState=INTERACTIVE_STATE_AWAKE`.
- Fresh screenshot: [verifier-green.png](verifier-green.png), SHA-256
  `af9167bf1f1c6b045830f166b1d5bf68c7b0e5cac45169d57e87643072ed72a5`.
  Visual inspection shows empty Yesterday/date `13`, populated `14/15/16`,
  complete `03:17`, city/date above the card, separate `3/10/30 M` timer rail,
  no clipping or overlap.

Supporting same-device RED/GREEN and native receipts remain in
`physical-red.png`, `physical-green.png`, `physical-visual-receipt.md`,
`physical-visual-receipt-green.md`, `physical-red-activity-top.txt` and
`physical-green-activity-top-2.txt`.

## Boundary and history checks

- W34-attributed behavior boundary is exactly
  `DisplayCapability.kt` plus `DisplayProjectionTest.kt`; current app diff
  outside these paths is pre-existing workspace dirt and is not attributed to
  W34. The allocation correction sets Yesterday's measured height to the
  same `MainDisplayGeometry` card height and removes its weighted allocation.
- Weather/provider, timer/audio/lifecycle and runtime/fullscreen ownership
  remain read-only/boundary regressions; no W34 write was found in those
  paths. W34 task-owned evidence explicitly records accepted
  `RED_NOT_APPLICABLE` alternatives for those neighbor claims.
- W31 remains `done`, W32 remains `failed`, W33 remains `blocked`; task card,
  scheduler checkpoint and terminal state were not changed.
