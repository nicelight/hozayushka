---
description: Fresh independent adversarial semantic verification report for TASK-020-T3-FT-002-W17 Attempt 3.
status: final
task_id: TASK-020-T3-FT-002-W17
stage_id: S-RED-VERIFY
feature: FT-002
tier: T3
attempt: 3
role: Reviewer
---
# Red-verification report — TASK-020-T3-FT-002-W17 Attempt 3

## verdict:

REQUEST_CHANGES — the final functional PASS does not satisfy the accepted
first-time selected-OpenWeather activation semantics.

## findings:

- `HIGH`: the normal first-time OpenWeather flow invokes the provider-change
  refresh before a key can exist, records `MISSING_CREDENTIAL`, and makes zero
  provider calls. The subsequent valid key update only persists Settings: it
  does not notify Weather Context, request OpenWeather or invalidate the prior
  failure. A host-only in-memory probe accepted the generated key but still
  returned `OpenWeather: API-ключ не указан` with zero provider calls. Because
  Settings Back does not resume the Activity, selected weather can remain empty
  and the error false until a later lifecycle event or the 30-minute cadence.
  This materially breaks `FT-002-AC-004/008` and `REQ-007/029` on a supported
  production path; the separately inspected secret-redaction contract remains
  intact.

## evidence_checked:

- Indexed task, FT-002 acceptance and direct canonical provider, secret,
  ownership, local-data, lifecycle, runtime-verification and tier-policy specs.
- Final functional PASS and complete W17 app/test diff: Yandex deletion, exact
  two-adapter inventory, selected-only dispatch, coherent Settings snapshot,
  post-fetch identity guard, provider/location cache/history migration,
  redacted key path, both provider parsers/fixtures and downstream forecast
  compatibility surface.
- Reachable call path:
  `SettingsCapability.kt:460` -> `FoundationRuntime.kt:66` ->
  `WeatherCapability.kt:728`; valid key save at `SettingsCapability.kt:468`
  has no refresh callback; `MainActivity.kt:34` performs view navigation only;
  scheduled retry is at `FoundationRuntime.kt:37`.
- Adversarial in-memory probe result:
  `missing_key_state_probe=FAIL`, valid key accepted, unchanged missing-key
  error, zero provider calls, no network/device/live provider/subscription and
  no credential value recorded.
- No material Yandex/third-provider, fallback/mixing, raw-key artifact,
  timezone/migration or TASK-021/TASK-022 scope finding was admitted. Physical
  and live-provider evidence remains `DEFERRED` residual only; no runtime PASS
  is claimed.

## risks_or_questions:

none; no operator interpretation is required.

## Failure / Blocker

- Status: final Attempt-3 semantic failure.
- Where: Settings provider/key transition into Weather Context refresh/error
  lifecycle.
- Expected/observed: valid selected OpenWeather key completes selected-only
  activation; instead it causes no request and leaves a false missing-key state.
- Likely category: task-local integration defect, not architecture ambiguity or
  downstream forecast scope.
- Next action: scheduler-owned failed disposition and new indexed FT-002 repair.
- Replan required: yes. All three execution attempts are consumed; no fourth
  `/exe TASK-020-T3-FT-002-W17` is allowed.

## handoff:

Exact scheduler action: count this as unsuccessful Attempt 3 (`3/3`), record
`TASK-020-T3-FT-002-W17 in_progress -> failed`, create canonical BUG/follow-up
evidence, mark TASK-021 blocked and repeat the dependency pass so TASK-022 is
blocked, update the failure counters and record `HALT_FAILURE_BUDGET`. No fourth
`/exe` is permitted. After scheduler-owned lifecycle writes, run `/mb-sync W17`.
Resume only through `/feature-to-tasks FT-002` for a new indexed repair task,
then fresh plan review, strict readiness, `/exe`, `/verify` and `/red-verify`.

Evidence paths:

- `.protocols/TASK-020-T3-FT-002-W17/red-verification.md`
- `.protocols/TASK-020-T3-FT-002-W17/verification.md`
- `.tasks/TASK-020-T3-FT-002-W17/TASK-020-T3-FT-002-W17-S-VERIFY-final-report-docs-03.md`
- `.tasks/TASK-020-T3-FT-002-W17/TASK-020-T3-FT-002-W17-S-RED-VERIFY-final-report-docs-01.md`
- `app/src/main/kotlin/com/hozayushka/app/settings/SettingsCapability.kt`
- `app/src/main/kotlin/com/hozayushka/app/weather/WeatherCapability.kt`
- `app/src/main/kotlin/com/hozayushka/app/app/FoundationRuntime.kt`
- `app/src/main/kotlin/com/hozayushka/app/app/MainActivity.kt`

SEMANTIC_VERDICT: semantic-fail
