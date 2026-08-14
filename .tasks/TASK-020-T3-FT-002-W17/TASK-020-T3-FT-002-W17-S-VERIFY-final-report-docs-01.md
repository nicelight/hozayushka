---
description: Independent functional verifier report for TASK-020-T3-FT-002-W17 Attempt 1.
status: final
task_id: TASK-020-T3-FT-002-W17
stage_id: S-VERIFY
attempt: 1
role: Reviewer
---
# Verifier report — TASK-020-T3-FT-002-W17 Attempt 1

## Result

Functional result: `FAIL`.

- Required gates: `3/3 PASS`; supplemental checks: `3 PASS / 1 FAIL`.
- Clean assemble passed `34/34`; focused host checks passed `55/55`; full host
  suite passed `83/83`; Memory Bank/diff and secret/provider/APK scans passed.
- The blocking verifier probe observed an old-location selected-provider
  response and pressure being persisted and displayed as `FRESH` under the new
  selected location after Settings changed during `fetch`.

## Blocking finding

- Affected claims: `FT-002-AC-004`, `FT-002-AC-005`, `FT-002-AC-008` /
  `REQ-007`, `REQ-008`, `REQ-029` and the direct Weather Provider cache/history
  response-authority rules.
- Cause proven by current source: request coordinates are captured before
  `fetch`, but response acceptance rereads the mutable current location and
  uses it for normalization, cache identity, history identity and projection.
- Reproducer:

  ```bash
  verify_probe_out="$(mktemp -d)"
  verify_app_jar="app/build/intermediates/compile_app_classes_jar/debug/bundleDebugClassesToCompileJar/classes.jar"
  verify_kotlin_jar="/home/serg/.gradle/caches/modules-2/files-2.1/org.jetbrains.kotlin/kotlin-stdlib/2.0.20/7388d355f7cceb002cd387ccb7ab3850e4e0a07f/kotlin-stdlib-2.0.20.jar"
  verify_android_jar="/home/serg/Android/Sdk/platforms/android-35/android.jar"
  verify_classpath="$verify_app_jar:$verify_kotlin_jar:$verify_android_jar"
  javac -encoding UTF-8 -source 17 -target 17 -cp "$verify_classpath" -d "$verify_probe_out" .tasks/TASK-020-T3-FT-002-W17/VerifierResponseIdentityProbe.java
  java -cp "$verify_probe_out:$verify_classpath" VerifierResponseIdentityProbe
  ```

- Observation: exit `1` with
  `selected_projection_fresh=true`, `selected_city_label_matches_new=true` and
  `old_pressure_labeled_as_new_history=true`; network and credentials were not
  used.

## Passing scope and security evidence

- Production contains exactly Open-Meteo and OpenWeather `WeatherProvider`
  implementations and exactly one accepted endpoint literal for each. No
  Yandex production path/string/header exists in source or APK, and no third
  provider endpoint/adapter is present.
- Open-Meteo is default/keyless. OpenWeather requires explicit selection; key
  access is selected-OpenWeather-only, ephemeral and preceded by adapter
  identity validation. W16's generic deny is removed without exposing the key
  to Open-Meteo, mismatch/unknown or legacy transport paths.
- Steady-state dispatch calls one selected adapter with no fallback, parallel
  call, substitution or provider mixing. Request shape, timezone, hPa mapping,
  provider failures and optional fallbacks pass the focused fake-transport
  checks.
- Source/resources/fixtures/protocol/evidence/test reports/APK scans found no
  real or synthetic secret value; request-shape evidence remains redacted.
- Actual Attempt-1 app scope matches the handoff and has no dependency, module,
  graph-edge, plugin/DI/event, hourly-session or long-term-presentation
  expansion. Downstream forecast checks are regression-only.

## Evidence paths and scheduler action

- Canonical protocol:
  `.protocols/TASK-020-T3-FT-002-W17/verification.md`.
- Reproducer source:
  `.tasks/TASK-020-T3-FT-002-W17/VerifierResponseIdentityProbe.java`.
- Executor supporting evidence:
  `.tasks/TASK-020-T3-FT-002-W17/red-green-evidence.md`.

Device/live-provider evidence remains `DEFERRED`; no Android runtime PASS is
claimed. The task remains `in_progress`; lifecycle, dependents, scheduler
checkpoint and terminal state are unchanged, and `/mb-sync` was not run.

Recommended scheduler action: `/exe TASK-020-T3-FT-002-W17` bounded retry for
request/response location-identity atomicity, then fresh `/verify`. Do not
promote `TASK-021`, close, run `/red-verify` or invoke `/mb-sync` before the
fresh functional PASS.

