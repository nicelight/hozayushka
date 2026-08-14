# Fresh RED baseline — Attempt 1

## Claim mapping

- `FT-001-AC-002 / REQ-002`: idle clock must advance beyond the current W24
  visual baseline; right-side preset presentation must be refined without
  changing slot/order/dispatch semantics.
- `FT-001-AC-002 / REQ-005`: the common weather-card gap must be enlarged
  while the existing Today-versus-three-equal-smaller relation remains.
- `REQ-023`: the material visual treatment must remain lightweight and readable.

## Source basis before production change

- `DisplayCapability.kt` SHA-256:
  `5a7ee8f79290ef3c5967e9ce44b40ee429b7030084144ba6bcdd9d1b174a5b9a`
- At current source lines 72–77: preset column `220`, preset gap `4`,
  inter-card gap `16`, smaller weight `1`, Today weight `1.25`, idle clock
  text size `176`.
- Current source applies `GradientDrawable.OVAL` with filled
  `display_button` (`#314A5A`) and a single solid `setStroke` color; the
  interior is therefore not transparent and no gradient shader is present.

## Fresh host RED probe

- Command:
  `./gradlew :app:testDebugUnitTest --tests com.hozayushka.app.DisplayProjectionTest.w26ClaimProbeRequiresLargerAdaptiveClockAndExpandedSpacing --no-daemon`
- CWD: `/home/serg/Projects/Mobile_APPS/hozayushka`
- Input basis: current worktree before W26 production change; only the
  claim-specific probe was added to `DisplayProjectionTest.kt`.
- Exit code: `1`.
- Observable result: `DisplayProjectionTest >
  w26ClaimProbeRequiresLargerAdaptiveClockAndExpandedSpacing FAILED` at
  `DisplayProjectionTest.kt:141`; the current `176f` idle clock does not
  exceed the W24 baseline. The same source facts also record current gaps
  `16`/`4`, below the W26 relational requirements.
- The preceding compile-only lambda-overload failure was discarded as setup
  friction and is recorded only in the session papercut log; it is not this
  RED evidence.

## Current geometry observation

The existing deterministic host geometry test passed before the W26 change and
reported at the accepted `1280×720` model:

- clock region: `[271,24,1028,252]` (`757×228`); text size `176`;
- preset bounds: `[1028,26,1248,246]`, `[1028,250,1248,470]`,
  `[1028,474,1248,694]` (`220×220` each), radii `110`, gaps `4/4`;
- cards: `[32,252,255,696]`, `[271,252,550,696]`,
  `[566,252,789,696]`, `[805,252,1028,696]` with widths
  `223/279/223/223` and gaps `16/16/16`.

Command:
`./gradlew :app:testDebugUnitTest --tests com.hozayushka.app.DisplayProjectionTest.w24GreenGeometryMakesClockDominantAndPresetsCircular --tests com.hozayushka.app.DisplayProjectionTest.mainDisplayGeometryKeepsLeftCenterRightRegionsAndCardRelations --no-daemon --info`

Exit code: `0`; this is baseline observation, not W26 GREEN.
