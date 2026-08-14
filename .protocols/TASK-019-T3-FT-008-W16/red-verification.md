---
description: Fresh independent adversarial semantic verification for TASK-019-T3-FT-008-W16 Attempt 3.
status: final
task_id: TASK-019-T3-FT-008-W16
attempt: 3
role: Reviewer
---
# Red Verification — TASK-019-T3-FT-008-W16

## Semantic target

- Retest the prior supported-path credential-authority and false-attribution
  failure after the final bounded Attempt-3 correction.
- TASK-019 may persist and render Open-Meteo default/no-key and explicit
  OpenWeather/local-key Settings state, local validation and attributions. It
  must not release the owner key to legacy Yandex or another unaccepted
  provider, identify an untagged legacy failure from current selection, or
  implement TASK-020 transport/dispatch/fallback behavior.

## Evidence and adversarial coverage

- Functional basis: final Attempt-3 `.protocols/TASK-019-T3-FT-008-W16/verification.md`
  and its report record `VERDICT: PASS`; the authoritative task remains
  `in_progress` and all three execution attempts are consumed.
- Actual correction surface: current app diff, Attempt-3 start time and file
  times confirm correction writes only to `SettingsCapability.kt` and
  `SettingsLocationTest.kt`; the existing `strings.xml` delta predates Attempt
  3. Weather Context, composition, adapters, outbound requests, cache/history,
  forecasts and dependencies have no Attempt-3 app change.
- Production reachability: `FoundationRuntime` routes launch, cadence and valid
  location changes through `refreshIfNeeded(..., requireStoredCredential =
  true)`; provider changes use the same location-refresh callback. Current
  `SettingsCapability.withWeatherApiKey` returns `null` without invoking its
  callback, so no stored owner key can create a request or reach the wired
  Yandex adapter. The foundation fixture creates its own synthetic request and
  has no stored-key read path. Repository-wide production call-site and secret
  owner searches found no second release path.
- Failure attribution: `contextualizeWeatherError` no longer consults current
  selection. Untagged network/unknown-city messages remain untagged and
  untagged legacy key messages are denied; only Settings-owned OpenWeather key
  validation adds the OpenWeather label in the OpenWeather key UI.
- Preservation/boundary: the deny changes only the provider-unidentified
  release seam. Provider/key persistence and reopen, Open-Meteo key
  inapplicability, last-valid local validation, provider/location state,
  Settings section order and attributions remain covered by the unchanged
  owner implementation and a fresh focused host result of `10/10` with zero
  failures/errors/skips.
- Downstream atomic handoff: `TASK-020-T3-FT-002-W17` depends on TASK-019 and
  requires removal of Yandex, exactly two selected adapters, Open-Meteo with no
  credential, and OpenWeather owner-key use only in transient `appid`
  construction. Together with the Attempt-3 handoff/checkpoint, this
  unambiguously requires TASK-020 to replace the blanket deny in the same
  selected-dispatch change with selected-OpenWeather-authorized access; it does
  not authorize fallback, mixing or a third provider.
- Fresh semantic probes: targeted `SettingsLocationTest` passed `10/10`; the
  task security scan passed with zero known-marker workspace/APK hits, zero
  credential-literal candidate groups and accepted packaged resources. No
  network, live provider, credential, emulator/AVD/QEMU, `adb` or physical
  device was used, and no runtime/device PASS is claimed.

## Admitted findings

none.

## Operator questions

none.

## Verdict

SEMANTIC_VERDICT: semantic-pass

## Owner handoff

- Evidence/report paths: this protocol;
  `.tasks/TASK-019-T3-FT-008-W16/TASK-019-T3-FT-008-W16-S-RED-VERIFY-final-report-docs-01.md`;
  `.protocols/TASK-019-T3-FT-008-W16/verification.md`;
  `.tasks/TASK-019-T3-FT-008-W16/TASK-019-T3-FT-008-W16-S-VERIFY-final-report-docs-03.md`.
- Recommended owner action: scheduler records
  `TASK-019-T3-FT-008-W16 in_progress -> done` with final Attempt-3 functional
  PASS and this semantic-pass, then runs `/mb-sync W16`; only the scheduler may
  perform subsequent TASK-020 readiness/promotion.
- Resume route: `n/a`; this Reviewer changed no lifecycle, dependents,
  checkpoint or terminal state and did not run `/mb-sync`.
