---
description: Weather Context boundary regression evidence for W32.
status: evidence
---
# Weather Context boundary regression

`RED_NOT_APPLICABLE`: intentionally changing provider selection, refresh,
freshness, cache/history, normalized values or card semantics would cross the
task's exact Main Display boundary. Alternative proof is the deterministic
fixture matrix in [`weather-slot-matrix.json`](weather-slot-matrix.json), plus
focused/full host tests and static review of `DisplayCapability.kt`.

The W32 implementation only changes `MainDisplayGeometry` macro allocation and
the Main Display-owned active countdown presentation cap needed to preserve the
existing hierarchy after the clock-zone expansion. Weather data remains read
through `weather.projection(...)`; no provider/adapters/weather files changed.
