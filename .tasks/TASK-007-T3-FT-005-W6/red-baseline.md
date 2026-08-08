---
description: Claim-specific pre-implementation RED evidence for TASK-007-T3-FT-005-W6.
status: final
---
# Claim-linked RED baseline — TASK-007-T3-FT-005-W6

## Attempt

- attempt: 1
- source basis: `a93e46118f0f0b90e311b6174e3f5a8ed7d89fef` plus the pre-existing workspace changes recorded in `context.md`
- captured: 2026-08-08 05:32 +0500
- probe type: source-level claim probe before any FT-005 production change

## Results

- `FT-005-AC-001 / REQ-011` — RED. `SettingsState` contains only the
  Foundation location; `DisplayLayoutSpec` has only three empty positions and
  `presetButton` renders the position number; `TimerCapability` accepts a
  direct duration and has no validated preset projection. No three independent
  configured definitions or Timer read-path projection exists.
- `FT-005-AC-002 / REQ-011` — RED. `SettingsCapability` exposes only
  `saveFoundationLocation`; no hours/minutes/seconds range checks, positive
  total rule, invalid-input preservation or preset persistence keys exist.
- `FT-005-AC-003 / REQ-011` — RED. `presetButton` uses `position.toString()`;
  no 3m/10m/30m defaults or highest-non-zero-unit floor label formatter exists.
- `FT-005-AC-004 / REQ-011` — RED. Preset buttons use one filled
  `display_button` color, with no orange/pink/purple neon outline and no
  selected/active preset projection exposed through Timer & Alert.

## Exact probe

```text
set +e
rg -n 'Preset|preset|duration|active|selected' app/src/main/kotlin/com/hozayushka/app/settings/SettingsCapability.kt app/src/main/kotlin/com/hozayushka/app/timer/TimerCapability.kt app/src/main/kotlin/com/hozayushka/app/display/DisplayCapability.kt
rg -n 'hours|minutes|seconds|positive|invalid|last valid|validation|saveFoundationLocation' app/src/main/kotlin/com/hozayushka/app/settings/SettingsCapability.kt
rg -n '3m|10m|30m|ч|м|с|label|presetButton' app/src/main/kotlin/com/hozayushka/app/display/DisplayCapability.kt app/src/main/kotlin/com/hozayushka/app/settings/SettingsCapability.kt
rg -n 'orange|pink|purple|neon|stroke|selected|active|setBackgroundColor|Timer.*Reader|Timer.*Projection' app/src/main/kotlin/com/hozayushka/app/display/DisplayCapability.kt app/src/main/kotlin/com/hozayushka/app/timer/TimerCapability.kt app/src/main/res/values/colors.xml
```

## Observable output

The probe found only the existing three-position shell, direct timer duration,
Foundation location persistence, the generic filled button and unrelated
weather-card stroke. It found no accepted FT-005 implementation surface. This
is an honest claim-specific RED, not a setup, syntax or artificial failure.
