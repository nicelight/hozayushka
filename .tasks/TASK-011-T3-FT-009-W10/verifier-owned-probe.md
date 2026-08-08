---
description: Verifier-owned functional probe for TASK-011-T3-FT-009-W10.
status: final
task_id: TASK-011-T3-FT-009-W10
stage_id: S-VERIFY-PROBE
feature: FT-009
---
# Verifier-owned functional probe

## Basis

Fresh checks were run against the current source and generated build after
executor receipts were classified as `supporting-only`. The claim path was
applicable: executor RED is recorded in `baseline-red-attempt-1.md`, and
executor GREEN is supporting context only.

## Independent observations

- `./gradlew testDebugUnitTest --tests com.hozayushka.app.FT009PersonalizationTest --rerun-tasks` passed; all 4 FT-009 tests passed.
- `./gradlew testDebugUnitTest --rerun-tasks` passed; current XML reports contain 52 tests, 0 failures, 0 errors and 0 skipped.
- `./gradlew clean assembleDebug --rerun-tasks` passed; APK SHA-256 is `f8c77d190c906f419f5f3bff4b50b3d47be0ad521ecd101b794beeae2d5aae8f`.
- `git diff --check`, `node scripts/mb-lint.mjs`, scoped boundary/no-modal/static checks and source/APK redaction checks passed.
- `adb devices -l` found no attached device; `Tecno_Pova_6_API_35` is only an inactive listed AVD. Target-only readability/static pseudo-glass evidence is `DEFERRED`; no runtime PASS is claimed.

## Claim mapping

- `FT-009-AC-001 / REQ-019`: `FT009PersonalizationTest` proves the built-in
  signal set/default, volume 0..100/default 70, valid auto-save/reload,
  read-only Timer consumption and volume-zero audio suppression while the
  timer remains `OVERDUE` and the Settings snapshot is unchanged. The
  production adapter persists the same projection through
  `SharedPreferencesSettingsStateStore` and `FoundationRuntime` wiring.
- `FT-009-AC-001 / REQ-020`: the targeted test proves Today and `24 °C`
  fallback, temperature text, two arrows and distinct static materials at
  0/0.45/1. Source inspection confirms the preview slider calls
  `SettingsPreviewProjection` and the same `weatherCard`/`pseudoGlass` path
  used by production cards; the preview calls the read-only Weather projection
  and makes no provider request.
- `FT-009-AC-001 / REQ-021`: the targeted test proves invalid volume/glass
  preservation and owning errors. Source inspection confirms the exact five
  accepted inline messages, inline-only error TextViews, no dialog path and
  both bottom/system Back routes. The full suite and build provide regression
  support for the existing Settings surface.

## Boundary and isolation checks

`SettingsCapability` writes only its `SettingsStateStore`; Main Display owns
preview composition and reads Weather through `WeatherCapability`; Timer & Alert
reads `TimerAlertSettingsProjection` and does not write Settings. No new
Settings -> Weather edge, private-store bypass, provider/platform bypass,
event boundary, dependency or secret-bearing artifact was found. Tests use
fresh in-memory state; no live credentials, network, device install or
external permission side effect was used.
