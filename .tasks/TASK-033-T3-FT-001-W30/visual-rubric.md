# W30 visual-QA rubric

Reviewer: Codex (executor visual-QA review; independent /verify remains due)

Scope: fresh W30 host receipts only; target/device runtime is not inferred.

| Criterion | 2460×1080 | 1280×720 | Decisive observation / locator |
|---|---|---|---|
| Full HH:mm readability and density | PASS | PASS | geometry.json; full bounds remain inside clock region; 1280 height delta is 0.000015 px and within 0.01 tolerance |
| No clipping/overflow | PASS | PASS | red-baseline.md exact output: fitsWithin0.01=true, aboveCards=true |
| Four stable shells, NO_DATA | PASS | PASS | weather-slot-matrix.json; all four EMPTY_SHELL in order |
| Four stable shells, partial | PASS | PASS | weather-slot-matrix.json; only Today has value, other three remain shells |
| Four stable shells, populated | PASS | PASS | weather-slot-matrix.json; all four values are fixture projections, no fabricated values |
| One-color radial preset shading | PASS | PASS | preset-visual-receipts.json; each preset has three shade values derived from its own base color |
| Wider rim / selected-active styling / touch targets | PASS | PASS | preset-visual-receipts.json; equal circular targets and current 10/11 px rims, active rim 12/13.2 px |
| Exactly three static outward-fading glow layers | PASS | PASS | preset-visual-receipts.json; layer count 3 and monotonic spreads |
| Lightweight static rendering | PASS | PASS | boundary-static-review.md; Canvas shader/strokes only, no timer/realtime/audio effect |
| Honest host/device separation | PASS (host) / DEFERRED (device) | PASS (host) / DEFERRED (device) | target-device.md; no runtime target was authorized |

Rubric result: all applicable host observations PASS. This supports the
accepted W30 RED_NOT_APPLICABLE path; it is not an independent verifier
verdict.

