# Attempt 1 — fresh RED geometry evidence

Captured before the W21 production composition change.

- Source input basis: pre-change task-file diff checksum
  `edc452a76da769a15abdea8c24f786ec2034c6beec1dedc22e0eaa9ef1bf529a`.
- Host command: `./gradlew testDebugUnitTest --tests com.hozayushka.app.DisplayProjectionTest.stableShellReservesHeaderContentBeforeFourCardsAndThreePresets`
- Result: exit `0`; the existing baseline shell test passed.
- Source observation: the old `left` column contains the complete header with
  clock, date and city; the weather row is below that header; all four cards
  use equal `weight=1f`; non-first cards use the current `8` margin.
- Deterministic 1280×720 source-derived horizontal bounds: inner left/center
  weather shell `x=32..1028` (996 units); cards are
  `yesterday=[32,275]`, `today=[283,526]`,
  `tomorrow=[534,777]`, `day_after=[785,1028]`, each width `243`, with
  gaps `[8,8,8]`. The preset column begins at `x=1028`.
- Mismatch against `FT-001-AC-002 / REQ-002`: city/date and idle clock are
  not split into left/central regions, Today has no larger measured layout
  allocation, and the gap is not greater than the 8dp baseline.

Rendered source-derived comparison: `red-green-contact-sheet.svg`.
