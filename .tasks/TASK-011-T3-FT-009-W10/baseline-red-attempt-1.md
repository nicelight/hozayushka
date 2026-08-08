---
description: Claim-specific pre-implementation RED for TASK-011-T3-FT-009-W10 attempt 1.
status: active
---
# FT-009 pre-implementation RED — attempt 1

## Basis

- Task: `TASK-011-T3-FT-009-W10`, tier `T3`, attempt `1`.
- Repository basis: `HEAD=a93e46118f0f0b90e311b6174e3f5a8ed7d89fef` plus the
  broad pre-existing tracked/untracked worktree changes recorded in
  `.protocols/TASK-011-T3-FT-009-W10/context.md`.
- Probe ran after `ready → in_progress` and before any FT-009 production edit.

## Claim-specific observation

- Search for task-owned `glassIntensity`, `GlassIntensity`, Settings preview,
  `updateAlertVolume`, `SeekBar` and `RadioGroup` found no FT-009 validated
  state/update/UI surface. The only `alert.volume` match is the existing
  FT-007 consumer persistence key and is not a user-facing FT-009 control.
- `DisplayCapability.weatherCard` hard-codes
  `WeatherCardPresentation.pseudoGlass(0.45f)` and creates one arrow view for a
  positive `pressureArrowCount`; no reusable live preview path exists.
- Existing FT-008 Settings stops after location/timer editors and the Back
  action. Existing FT-007 timer tests cover the read projection/audio policy,
  but do not prove the FT-009 Settings controls, glass projection or preview.

## Probe

```text
rg -n 'glassIntensity|GlassIntensity|SettingsPreview|settings-preview|updateAlertVolume|alert\.volume|SeekBar|RadioGroup' app/src/main/kotlin app/src/test/kotlin
→ only app/src/main/kotlin/com/hozayushka/app/settings/SettingsCapability.kt:281
  const val KEY_ALERT_VOLUME = "settings.alert.volume"

rg -n -C 2 'pseudoGlass\(0\.45f\)|pressureArrowCount > 0|text = if \(projection\.pressureDirection' app/src/main/kotlin/com/hozayushka/app/display/DisplayCapability.kt
→ fixed production material at line 820; one arrow branch at lines 843–851

rg -n -C 3 'settings_timer_title|timerPresetEditor|settings_back_icon|return scroll' app/src/main/kotlin/com/hozayushka/app/settings/SettingsCapability.kt
→ existing timer editor and Back tail at lines 497–510; no FT-009 controls
```

## RED result

The task-owned AC/REQ proof path is genuinely absent: no validated glass
projection or UI, no live gesture preview, no FT-009 volume control/invalid
preservation surface, and no production-card/preview comparison path. This is
an honest RED for FT-009; existing FT-008 and FT-007 behavior is preserved as
prerequisite context and is not adopted as this task's evidence.
