TASK: TASK-015-T3-FT-001-W12
ATTEMPT: 1
RUNTIME_CLASSIFICATION: EXECUTION-LOCAL GENERIC-EMULATOR ONLY

## Runtime identity

- AVD: `Tecno_Pova_6_API_35`
- Device: `sdk_gphone64_x86_64` / `emu64xa`, Android 15, API 35, x86_64
- Geometry: 1080x2436, density 393
- Final APK: installed SHA-256 `d1f8634227c758de4e424e37aa18f863afe5623ee1b794484946606b4039bb30`.

## Public probes and artifacts

| Probe | Observation | Screenshot / SHA-256 |
|---|---|---|
| Clean idle Main Display | Main Display remained visible with synthetic Khujand fixture | `attempt-1-green-clean-idle.png` / `5f8c5ba8113cf43d8a713b5168c90df5693c7050404b8b5d0cc67558919498ca` |
| Selected-city idle short tap | No Settings navigation; Main Display remained visible | `attempt-1-green-city-short-idle-clean.png` / `9258b6db265852f43d14a18b6a2fa47e07236de0848708c55b35fc66bd330143` |
| Non-city single tap while active | Countdown and accepted hint remained visible | `attempt-1-green-noncity-single-hint.png` / `f29839e42da057d7bd0119d7d6e4669e4a194d41783f95a4187a7e823891f4de` |
| Non-city double tap, 120 ms, ~350 ms checkpoint | Countdown cancelled to idle Main Display | `attempt-1-green-noncity-double-idle.png` / `ae3df82b4ded7e664ae680465fef25d163bde59668e3d04362c9832c7b724f46` |
| Selected-city 800 ms hold | Existing Settings opened; visible redacted state only | `attempt-1-green-city-hold-settings.png` / `25ee1372023ce9d6eb56c3f4c4a5e96f9257891a5a0b185689a59203290c294b` |
| System Back after city hold | Returned to active countdown | `attempt-1-green-city-hold-back-countdown.png` / `2ee92bccfbd964cd65fc1fee68ebc8fe81d12c5f272a26225aac5b7737b96d48` |
| City double tap, 120 ms, ~350 ms checkpoint | Countdown cancelled to idle | `attempt-1-green-city-double-350ms.png` / `0c73c549eef0001badb355be845677d70ddaae28c939712abdf009e9c01b38f8` |
| City double tap beyond timeout | Stayed on Main Display; no delayed Settings | `attempt-1-green-city-double-beyond-timeout.png` / `a310d09a61e997bde92356fcbe931d1398a3236f9c736d17f8a8028963328891` |
| Preset idle start, active single, active double | Preset start, hint-preserving single, and cancellation paths remained visible | `attempt-1-green-preset-start.png`, `attempt-1-green-preset-active-single.png`, `attempt-1-green-preset-active-double-idle.png` |
| Overdue before/after dismiss | Existing overdue overlay appeared and was dismissible | `attempt-1-green-overdue-before-dismiss.png`, `attempt-1-green-overdue-after-dismiss.png` |

## Cleanup and safety

Final state was MainActivity in `dumpsys window` and `dumpsys activity activities`; device was awake; filtered logcat had no `FATAL EXCEPTION`, `AndroidRuntime`, or `ANR` matches. The required `dumpsys activity top` command exited 0, but its hierarchy was stale toward launcher in this run; MainActivity was confirmed by the complementary activity/window dumps.

This generic emulator evidence is execution-local only. Samsung GT-I9300I Android 11 custom-ROM, 1280x720 geometry, system bars, readability, lifecycle, and audio evidence remain deferred; no target PASS is claimed.
