---
description: Host visual rubric evidence for TASK-035-T3-FT-001-W32.
status: evidence
---
# W32 visual rubric

Attempt 1 uses the deterministic host geometry path and the same redacted
fixtures as the focused `DisplayProjectionTest` suite. Raw bounds are in
[`geometry.json`](geometry.json); the same-size visual locator is
[`red-green-contact-sheet.svg`](red-green-contact-sheet.svg).

| Criterion | Host evidence | Result |
|---|---|---|
| Macro composition | Left city/date bounds end at the Yesterday top; central clock ends at the band top; four bottom cards and right rail are distinct at both sizes. | PASS |
| Band / clock zone | 2460×1080: `0.27962962` / `0.7203704`; 1280×720: `0.27916667` / `0.7208333`. | PASS |
| Equal cards | 2460×1080 heights `[302,302,302,302]`, bottoms `[1056,1056,1056,1056]`; 1280×720 heights `[201,201,201,201]`, bottoms `[696,696,696,696]`. Yesterday is not taller. | PASS |
| Clock focal hierarchy | Complete HH:mm model is maximum-fit and contained in the central/upper zone; no card/timer overlap is present in raw bounds. | PASS |
| Weather scale | At 1280×720, illustration bounds are 193×36 / 249×36 / 193×36 / 193×36 while temperature bounds are 174×68 / 218×68 / 174×68 / 174×68; all illustrations end before temperature and are materially secondary. At 2460×1080 the same relation holds with illustration heights 54 and temperature height 103. | PASS |
| Timer rail | Three 200×200 square bounds are used as circular controls, gaps are 24px, colors are `#FF7A00`, `#FF4FA3`, `#A855F7`, and the host paint contract has three radial glow layers with transparent interiors. | PASS |
| State stability | NO_DATA, PARTIAL_ASYNC and POPULATED_REDACTED retain four ordered shells; see [`weather-slot-matrix.json`](weather-slot-matrix.json). | PASS |
| Ownership / no drift | Static boundary review and focused/full host regressions retain read-only Weather Context and Timer & Alert boundaries. | PASS |

The only numeric tolerance used for raster comparison is
`max(2px, 0.005H)`; no illustration area ratio, horizontal proportion,
clock-size ratio or timer-gap value is used as a product gate.
