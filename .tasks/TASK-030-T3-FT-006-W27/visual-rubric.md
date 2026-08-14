# W27 host visual rubric — attempt 1

Reviewer route: named visual-QA rubric applied to the deterministic same-size
host geometry/render model. This is host evidence only; target readability and
fullscreen behavior remain `DEFERRED`.

| Criterion | Result | Decisive observation | Locator |
|---|---|---|---|
| Countdown focal hierarchy | PASS | Countdown text size is `228.0`, final idle result in the same `1280×720` geometry is `188.75`; active digits are larger. | `geometry.json`, `DisplayProjectionTest.w27GreenCountdownSurfaceIsDedicatedLargerAndPresetIdentified` |
| Dedicated surface | PASS | Active projection uses a dedicated central surface and hides the idle left/header/card regions; right preset controls remain available. | `DisplayCapability.kt` active surface branch; `geometry.json` |
| Weather/card content exclusion | PASS | Active surface geometry is above the weather-card row and active refresh skips weather-card rendering; cards are `GONE` during countdown. | `geometry.json`; `DisplayCapability.kt` refresh |
| City/date exclusion | PASS | Left city/date region is hidden during countdown and is outside the active surface bounds. | `geometry.json`; `DisplayCapability.kt` refresh |
| Activating preset color identity | PASS | Backdrop color is assigned through existing `PresetPresentation.colorHex(activePresetSlot)`; sample `SECOND` is `#FF4FA3`. | `DisplayCapability.kt`; `geometry.json` |
| Transparent circular treatment | PASS | Backdrop view has transparent background and draws only a circular `STROKE` using the existing neon white→preset-color treatment. | `NeonCountdownBackdropView`; `geometry.json` |
| Selected/active preset indication | PASS | Existing `PresetPresentation.styles` and `applyPresetStyle` path remain in refresh; active sample has `selected=true`, `active=true`. | `DisplayCapability.kt`; `DisplayProjectionTest` |
| Readability | PASS (host) | Same-size host geometry has positive bounds and no required surface intersections; target/device readability is not inferred. | `geometry.json` |
| Clipping/overlap | PASS (host geometry) | Circular backdrop is square-bounded within surface; surface ends at weather row top and excludes city/date regions. | `geometry.json` |
| Lightweight static treatment | PASS | No fill, asset, resource or animation was added; border reuses existing static neon gradient identity. | `DisplayCapability.kt`; scoped diff |

Overall host rubric result: PASS for the W27 presentation criteria. This rubric
does not claim emulator, Samsung/custom-ROM, audio or runtime/device PASS.
