---
description: Fresh independent adversarial semantic verification for TASK-020-T3-FT-002-W17 Attempt 3.
status: final
task_id: TASK-020-T3-FT-002-W17
attempt: 3
role: Reviewer
---
# Red Verification — TASK-020-T3-FT-002-W17

## Semantic target

- Task outcome: replace Yandex with exactly Open-Meteo and OpenWeather, dispatch
  only the selected provider, keep provider/location state isolated, and expose
  the owner key only to an authorized selected-OpenWeather request.
- Accepted boundary: a valid provider change must reach the newly validated
  selected-provider access context without fallback; failures and inline state
  must remain honest for the current selection. TASK-021 hourly completeness
  and TASK-022 long-term projection remain downstream.

## Evidence and adversarial coverage

- Functional basis: final Attempt-3
  `.protocols/TASK-020-T3-FT-002-W17/verification.md` records `VERDICT: PASS`;
  it was treated as functional evidence, not semantic proof. The indexed task
  remains `in_progress`, and the scheduler checkpoint records three execution
  attempts consumed with two unsuccessful attempts before this gate.
- Actual change surface: inspected the complete current W17 production/test
  diff, including the provider boundary, deleted Yandex adapter/fixture, both
  new adapters/fixtures, Settings access snapshot, Weather Context dispatch,
  cache/history migration, composition wiring, provider tests, identity
  matrices and forecast compatibility tests. Repository production inventory
  contains only the two accepted adapters/endpoints and no Yandex/third-provider
  path; the Foundation fixture remains the separately accepted no-network probe.
- Selected-only/state coverage: inspected launch, location/provider-change and
  scheduled routing; one-adapter dispatch; provider/result mismatch rejection;
  post-fetch provider/location guard; stale success/failure effects; matching
  cache/history/trends; legacy provider-less/Yandex record denial; provider
  timezone, required current fields, optional condition/moon fallbacks and
  subset preservation. No automatic fallback, second-provider call or
  cross-provider state use was found in the ordinary fetch path.
- Secret coverage: inspected the coherent Settings snapshot, pre-key network/
  cadence/adapter suppression, Open-Meteo credential rejection, transient
  OpenWeather `appid` construction and redacted result/storage/evidence
  surfaces. No raw credential value was used or recorded by this verification.
- Adversarial host probe: in disposable in-memory state, the normal sequence
  `select OpenWeather without a stored key -> provider-change refresh -> enter
  a generated valid key` produced
  `missing_key_state_probe=FAIL; key_update_accepted=true;
  error_before=OpenWeather: API-ключ не указан;
  error_after_valid_key=OpenWeather: API-ключ не указан;
  provider_fetch_calls=0`. It used no network, device, live provider,
  subscription or recorded credential value.
- Production reachability: `SettingsCapability.updateWeatherProvider` persists
  selection and invokes `onValidProviderChanged` (`SettingsCapability.kt:460`),
  which queues `PROVIDER_CHANGE` (`FoundationRuntime.kt:66`). With no key,
  `refreshIfNeeded` records `MISSING_CREDENTIAL` without a provider call
  (`WeatherCapability.kt:758`). The subsequently accepted key update only saves
  Settings (`SettingsCapability.kt:468`) and has no Weather Context callback;
  Settings Back only swaps the view (`MainActivity.kt:34`), while the next
  automatic opportunity is the 30-minute cadence (`FoundationRuntime.kt:37`).
- Physical-device and live-provider evidence remains `DEFERRED` residual
  readiness evidence. No runtime PASS is claimed, and its absence is not the
  semantic failure.

## Admitted findings

- `HIGH` — first-time OpenWeather activation does not complete the accepted
  provider-change refresh after the key becomes valid. The only provider-change
  callback runs before the owner can enter the newly required key, so it records
  a missing-key failure and sends no request. Saving the valid key neither asks
  Weather Context to refresh nor invalidates that failure; the application can
  therefore remain without selected-provider weather until a later resume or
  30-minute cadence and continues to report that the now-present key is missing.
  This is a reachable, deterministic break of selected-provider refresh and
  honest current error state, not a downstream forecast or live-provider risk.

## Operator questions

none.

## Failure / Blocker

- Status: semantic failure of the final Attempt-3 outcome.
- Where: `SettingsCapability.updateWeatherProvider`,
  `SettingsCapability.updateOpenWeatherApiKey`,
  `WeatherCapability.refreshIfNeeded`, and `FoundationRuntime` refresh wiring.
- Expected: once explicit OpenWeather selection has a valid owner key, the
  provider-change path requests only OpenWeather and clears the obsolete
  missing-key state.
- Observed: selection refresh fails before key entry; accepted key entry causes
  zero provider calls and leaves `OpenWeather: API-ключ не указан` current.
- Likely category: task-local Settings-to-Weather activation/lifecycle gap.
- Next action: scheduler failure disposition and normal indexed FT-002 repair
  planning; no same-task retry remains.
- Replan required: yes — all three execution attempts are consumed, so no
  fourth `/exe TASK-020-T3-FT-002-W17` is permitted.

## Verdict

SEMANTIC_VERDICT: semantic-fail

## Owner handoff

- Evidence/report paths: this protocol;
  `.tasks/TASK-020-T3-FT-002-W17/TASK-020-T3-FT-002-W17-S-RED-VERIFY-final-report-docs-01.md`;
  `.protocols/TASK-020-T3-FT-002-W17/verification.md`;
  `.tasks/TASK-020-T3-FT-002-W17/TASK-020-T3-FT-002-W17-S-VERIFY-final-report-docs-03.md`;
  `app/src/main/kotlin/com/hozayushka/app/settings/SettingsCapability.kt`;
  `app/src/main/kotlin/com/hozayushka/app/weather/WeatherCapability.kt`;
  `app/src/main/kotlin/com/hozayushka/app/app/FoundationRuntime.kt`;
  `app/src/main/kotlin/com/hozayushka/app/app/MainActivity.kt`.
- Recommended owner action: count this semantic-fail once for Attempt 3
  (`3/3` unsuccessful), record `TASK-020 in_progress -> failed`, permit no
  fourth `/exe`, record canonical BUG/follow-up evidence, mark direct dependent
  TASK-021 `blocked` and repeat dependency blocking so TASK-022 is `blocked`,
  update failure counters and record `HALT_FAILURE_BUDGET`. After those
  scheduler-owned writes, reconcile the failed W17 boundary with `/mb-sync W17`.
- Resume route: explicit halt to `/feature-to-tasks FT-002` for a new indexed
  repair task, then fresh `/review-tasks-plan FT-002`, strict readiness,
  `/exe`, `/verify` and required `/red-verify`. This Reviewer changed no task
  lifecycle, dependents, scheduler checkpoint or terminal state and did not run
  `/mb-sync`.
