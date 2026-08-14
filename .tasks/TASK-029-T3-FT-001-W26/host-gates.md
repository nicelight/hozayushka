# Host gates — Attempt 1

All Gradle commands used `--offline`; no network/provider/credential path was
used.

- Fresh RED claim probe before production change:
  `./gradlew :app:testDebugUnitTest --tests com.hozayushka.app.DisplayProjectionTest.w26ClaimProbeRequiresLargerAdaptiveClockAndExpandedSpacing --no-daemon`
  → exit `1` on the real W24 baseline assertion; evidence:
  `red-baseline.md`.
- Focused final display suite:
  `./gradlew :app:testDebugUnitTest --tests com.hozayushka.app.DisplayProjectionTest --offline --no-daemon`
  → exit `0`; 18 tests, 0 failures/errors/skips.
- Full final host suite:
  `./gradlew testDebugUnitTest --offline --no-daemon`
  → exit `0`; 106 tests, 0 failures/errors/skips across the final report set.
- Clean debug build:
  `./gradlew clean assembleDebug --offline --no-daemon`
  → exit `0`, `BUILD SUCCESSFUL`. The only compiler diagnostic is the
  pre-existing `MainActivity.kt` deprecated-override warning.
- Static diff integrity:
  `git diff --check` → exit `0`.
- Evidence syntax:
  `xmllint --noout red-green-contact-sheet.svg` and `jq empty geometry.json`
  → exit `0`.

The final W26 focused test prints target clock `188.75`, alternate clock
`139.75`, target preset bounds `200×200` with radius `100` and gaps `24`,
target cards `217/273/217/217` and common gaps `24/24/24`.
