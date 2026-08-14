# Layout RED/GREEN — Attempt 1

## RED

Fresh pre-change source/host evidence is in `red-baseline.md`. The accepted
1280×720 model had idle text size `176`, filled `#314A5A` preset interiors,
solid per-slot strokes, right-control gaps `4/4`, card widths
`223/279/223/223`, and weather gaps `16/16/16`.

## GREEN

The same deterministic `MainDisplayGeometry` route and a bounded alternate
`1024×600` model now show:

- target idle clock text size `188.75` in a central/upper `755×228` region;
  alternate size `139.75` in a `559×187` region, proving available-space
  adaptation without clipping/overlap in the model;
- three equal target circles `200×200`, radius `100`, right-side gaps `24/24`;
  alternate circles adapt to `160×160` with the same relational gap;
- transparent interiors and a Canvas `LinearGradient` border using the
  existing orange/pink/purple slot identities; the implementation does not
  specify gradient positions/stops;
- ordered weather slots unchanged. Target widths are `217/273/217/217`, so
  Yesterday/Tomorrow/Day-after allocate equally and Today remains larger;
  observed smaller-to-Today ratio is `0.7949` (about the requested visual
  intent, not a fixed requirement);
- one common target card gap `24/24/24`, greater than the RED `16/16/16`.

The actual values and both host layouts are recorded in `geometry.json`. The
same-size RED/GREEN visual model is `red-green-contact-sheet.svg`; the named
visual decisions are in `visual-rubric.md`.
