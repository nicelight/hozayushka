---
description: Claim-linked host, build, static and presentation evidence for TASK-011-T3-FT-009-W10 attempt 1.
status: active
---
# FT-009 host evidence — attempt 1

## Execution basis

- Task: `TASK-011-T3-FT-009-W10`, tier `T3`, attempt `1`.
- Repository basis: `HEAD=a93e46118f0f0b90e311b6174e3f5a8ed7d89fef` plus the
  broad pre-existing tracked/untracked worktree changes recorded in
  `.protocols/TASK-011-T3-FT-009-W10/context.md`.
- Honest pre-implementation RED: `.tasks/TASK-011-T3-FT-009-W10/baseline-red-attempt-1.md`.
- No live credentials, network request, emulator start, device install or
  target runtime side effect was used.

## Claim-linked GREEN comparison

| Claim | Decisive host observation | Result / artifact |
|---|---|---|
| FT-009-AC-001 / REQ-019 | Owner projection defaults to built-in `Классический` and volume `70`; valid built-in signal and volume `0` auto-save and reload through the Settings owner. | Green in `FT009PersonalizationTest.defaultsValidChangesAndReloadUseOneValidatedSettingsProjection`; invalid volume preserves the previous projection. |
| FT-009-AC-001 / REQ-020 | Glass intensity defaults to `0.45`, accepts `0…1`, auto-saves/reloads, and invalid values preserve the previous valid value. The Main Display-owned preview uses Today temperature or `24 °C`, shared production-card projection/material, exactly two overlapping arrow views and no refresh/provider call. Pure material results at `0`, `0.45` and `1` are distinct and static. | Green in `FT009PersonalizationTest.invalidVolumeAndGlassValuesPreserveLastValidProjectionWithOwningErrors` and `previewUsesTodayOrFallbackTemperatureTwoArrowsAndStaticMaterialAtGestureValues`; production/UI path is `DisplayCapability.createSettingsView` → `SettingsPreviewProjection` → shared `weatherCard`. |
| FT-009-AC-001 / REQ-021 | Settings adds inline-only sound/volume/glass controls, preserves valid state on domain validation failure, keeps accepted existing inline messages (`API-ключ не указан`, `Неверный API-ключ`, `Нет подключения`, `Город не найден`, `Укажите время больше нуля`) and wires both system Back and bottom Back to Main Display. | Green by owner/UI source inspection plus full unit/build/static gates; no `AlertDialog`/`Dialog` path is present in Settings. |
| FT-009-AC-001 / REQ-019 / Platform Runtime | Timer & Alert reads the existing validated sound/volume projection and does not write Settings. Volume `0` returns no app audio request while the timer remains `OVERDUE`/visually overdue; Android silent/DND handling remains in the platform adapter. | Green in `FT009PersonalizationTest.timerReadsProjectionAndVolumeZeroSuppressesOnlyAppAlertAudio`; existing FT-007 platform-policy tests remain regression coverage only, not adopted as FT-009 proof. |
| Boundary / anti-goals | Settings owns mutable personalization; Main Display composes preview; no Settings → Weather Context edge, private-store bypass, direct provider/platform bypass, modal validation, new dependency or event boundary was found. | Green in scoped static/boundary scan and `git diff --check`. |
| T3 isolation/redaction | Tests use disposable in-memory state and deterministic projections; source/test/APK scans found no provider key header or token-shaped secret. | Green; no credential-bearing artifact was created. |

## Commands and receipts

- `./gradlew testDebugUnitTest --tests com.hozayushka.app.FT009PersonalizationTest`
  → exit `0`; targeted FT-009 probe passed.
- `./gradlew testDebugUnitTest` → exit `0`; final fresh report contains
  `tests=52`, `failures=0`, `errors=0`, `skipped=0` at
  `app/build/reports/tests/testDebugUnitTest/index.html`.
- `./gradlew clean assembleDebug` → exit `0`; APK is
  `app/build/outputs/apk/debug/app-debug.apk`, SHA-256
  `f8c77d190c906f419f5f3bff4b50b3d47be0ad521ecd101b794beeae2d5aae8f`.
- `git diff --check` → exit `0`.
- Scoped static/presentation/boundary scan → exit `0`: no Settings/Main Display
  direct provider/platform bypass, no Settings → Weather edge, no modal path;
  shared `pseudoGlass(glassIntensity)`, preview factory, two-arrow rendering,
  accepted inline errors and volume-zero suppression are present.
- Redaction scan over `app/src/main`, `app/src/test` and the final APK strings
  → exit `0`; no provider key header or token-shaped secret.
- `node scripts/mb-lint.mjs` → exit `0`; `mb-lint passed (77 files)`.

These are executor self-attested supporting receipts. The broad pre-existing
dirty/untracked workspace prevents a conservative bounded-input reuse claim;
fresh `/verify` evidence remains due.

## Target status

- `adb devices -l` → exit `0`, no attached device.
- `emulator -list-avds` → inactive `Tecno_Pova_6_API_35` is listed; it was not
  started.
- Target-only Settings readability/navigation and static pseudo-glass evidence:
  `DEFERRED`, non-blocking, with residual risk on 1280×720 Android 11 custom-ROM
  rendering/readability. No runtime `PASS` is claimed.
