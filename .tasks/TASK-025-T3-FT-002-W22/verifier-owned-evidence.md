---
description: Fresh verifier-owned functional evidence for TASK-025-T3-FT-002-W22.
status: final
task_id: TASK-025-T3-FT-002-W22
role: Reviewer
---
# Verifier-owned evidence — TASK-025-T3-FT-002-W22

## Fresh checks

| Check | Command | Result |
|---|---|---|
| Clean Android debug build | `./gradlew clean assembleDebug` | exit `0`, `BUILD SUCCESSFUL` |
| Complete host unit suite | `./gradlew testDebugUnitTest` | exit `0`, `BUILD SUCCESSFUL`; 99 tests, 0 failures/errors/skips |
| Offline clean build rerun | `./gradlew --offline --no-daemon clean assembleDebug` | exit `0`, `BUILD SUCCESSFUL` |
| Offline host suite rerun | `./gradlew --offline --no-daemon testDebugUnitTest` | exit `0`, `BUILD SUCCESSFUL` |
| Static diff integrity | `git diff --check` | exit `0`, no output |

All checks were host-only. No emulator/AVD/QEMU, Android Studio virtual device,
adb/device, live provider, network or credential path was used.

## Outcome observations

- `DisplayCapability.kt:139-180` dispatches exactly `CLEAR`, `CLOUD`,
  `NEUTRAL_CLOUD`, `RAIN`, `SNOW` and `MOON` through Canvas/Path/Paint drawing;
  `:1622-1627` consumes the existing projection illustration and `moonPhase`.
- `WeatherIllustrationCanvas.moonPhaseFraction(null)` and `"regular"` return
  the regular-moon fallback; named/numeric phases are covered by the focused
  unit test. No Main Display `illustrationText`/weather-glyph path exists;
  that helper remains under the forecast-card path.
- `WeatherCardContentGeometry` and `WeatherCardLayout` reserve the upper
  illustration envelope and place temperature/date/pressure content in the
  separate measured envelopes. The focused test and bounds artifact report all
  three intersections as false.
- `illustration-bounds.json` independently checks four-slot order
  `yesterday/today/tomorrow/day_after`, widths `223/279/223/223`, Today larger,
  equal non-Today widths and gaps `16/16/16`; additional panels cover
  `NEUTRAL_CLOUD` and `MOON`, with `nullMoonPhaseFallback: regular`.
- Weather Context source remains the owner of selected-city timezone/day-night,
  `MOON` selection, stale/NO_DATA empty cards, provider identity and pressure;
  Main Display continues to call only `weather.projection(now)`.
- Current W22 hard-boundary diff names only
  `DisplayCapability.kt` and `DisplayProjectionTest.kt`. No W22 resource,
  asset, dependency, provider, secret, network, timer or lifecycle write is
  observed. Broader unrelated worktree changes, including an existing
  `strings.xml` diff, are not attributed to W22.

## Visual artifacts

- Contact sheet: `illustration-contact-sheet.png` (1280×960 RGB PNG), visually
  reviewed for sun/rays, cloud lobes, three rain marks, three snow marks,
  moon-phase silhouette, contrast and no clipping/text/emoji.
- Source: `illustration-contact-sheet.svg`.
- Bounds: `illustration-bounds.json`.
- Fresh RED: `illustration-red-baseline.svg` and `illustration-red-green.md`.

## Deferred target evidence

Samsung GT-I9300I (`s3ve3gds`) Android 11 custom-ROM 1280×720 landscape
readability, fullscreen, keep-screen-on and target Canvas compatibility remain
`DEFERRED` under the task boundary. Host/static/image evidence is not promoted
to runtime/device `PASS`.
