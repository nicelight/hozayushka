---
description: Independent functional verifier report for TASK-020-T3-FT-002-W17 Attempt 2.
status: final
task_id: TASK-020-T3-FT-002-W17
stage_id: S-VERIFY
attempt: 2
role: Reviewer
---
# Verifier report — TASK-020-T3-FT-002-W17 Attempt 2

## Result

Functional result: `FAIL`.

- Required gates: `3/3 PASS`.
- Critical supplemental checks: `4 PASS / 1 FAIL`.
- Clean assemble passed `34/34`; full host suite passed `84/84`; focused
  provider/context/settings regression passed `56/56`; Memory Bank/diff,
  secret/APK and exact-provider inventories passed.
- The exact Attempt-1 in-fetch location reproducer now passes, but a fresh
  verifier matrix found that request location is not immutable from request
  construction through response acceptance.

## Blocking functional finding

- Affected claims: `FT-002-AC-004`, `FT-002-AC-005`, `FT-002-AC-008` /
  `REQ-007`, `REQ-008`, `REQ-029`, plus the direct Weather Provider response,
  cache/history identity and Weather Freshness contracts.
- `refreshIfNeeded` reads location A and constructs the selected-provider
  request from A. `refreshWithProvider` then reads Settings again and captures
  location B as `ProviderRequestIdentity`. Response acceptance compares current
  B to captured B, so the response requested with A coordinates is accepted,
  normalized, cached, added to pressure history and projected as `FRESH` B.
- Current source locators:
  `WeatherCapability.kt:723-724`, `:735-740`, `:629-634`, `:649-653`,
  `:677-707`.
- Fresh host-only matrix: exit `1`; `10` scenarios, `102` assertions,
  `94 PASS / 8 FAIL`. Both Open-Meteo and OpenWeather request-capture cases
  failed the same four observations: old-coordinate response accepted, success
  returned, cache/history updated and new identity displayed `FRESH`.
- This is an observed normative violation, not an unresolved product or
  architecture interpretation.

## Identity and stale-timing matrix

- Exact Attempt-1 location change inside `fetch`: PASS; stale projection and
  wrong-location pressure history are both rejected.
- Eight expanded in-fetch scenarios: PASS across both initial providers,
  location switch and provider switch in both directions, and stale
  success/failure timing. Existing matching cache/history remains unchanged,
  the new identity is `NO_DATA`, no stale failure leaks, no second adapter is
  called, and switching back exposes only the original matching projection.
- Two request-construction-to-identity-capture scenarios: FAIL for both
  Open-Meteo and OpenWeather as described above.
- Probe source:
  `.tasks/TASK-020-T3-FT-002-W17/VerifierAttempt2IdentityMatrixProbe.java`.

## Passing scope, security and regression evidence

- Production inventory is exactly two `WeatherProvider` implementations:
  Open-Meteo and OpenWeather. Source and APK each contain exactly one accepted
  endpoint; Yandex/legacy and third-provider hits are zero.
- Weather Context has one selected provider `fetch` site. Focused call-counting
  checks preserve selected-only dispatch, no fallback/parallel request,
  no substitution/mixing, provider-attributed failures and ordinary
  provider/location trend filtering.
- Open-Meteo remains keyless. OpenWeather key access is selected-only and the
  key reaches only transient `appid` construction. Workspace/evidence/APK
  scans found zero marker, credential and Yandex findings; no credential value
  appears in verifier output.
- Actual Attempt-2 production correction and regression were inspected in
  `WeatherCapability.kt` and `WeatherProviderDispatchTest.kt`. No new module,
  graph edge, dependency, storage/public API, DI/plugin/event mechanism,
  unrelated UI/timer/settings/catalog change or forbidden provider behavior
  was introduced by Attempt 2.
- `ForecastSessionTest` passed as downstream regression evidence only. This
  verifier does not claim `TASK-021` hourly or `TASK-022` long-term acceptance.

## Fresh commands and counts

| Check | Result |
|---|---|
| `./gradlew clean assembleDebug --no-daemon` | exit `0`; `34/34` actionable tasks |
| `./gradlew testDebugUnitTest --rerun-tasks --no-daemon` | exit `0`; `13` reports, `84/84`, zero failures/errors/skips |
| Eight-class focused regression | exit `0`; `8` reports, `56/56`, zero failures/errors/skips |
| Exact Attempt-1 verifier probe | exit `0`; stale projection/history both rejected |
| Attempt-2 identity matrix | exit `1`; `10` scenarios, `94/102` assertions passed, `8` failures |
| `node scripts/mb-lint.mjs && git diff --check` | exit `0`; `78` files; no whitespace errors |
| Task security/APK scan | exit `0`; `4/4 PASS` |
| Independent source/APK provider inventory | PASS; implementations `2`, endpoints `1 + 1`, Yandex `0` |

Debug APK SHA-256:
`19ddca31aabddc69fa889537ab09d24699e573aad55a6516d4d7532837b2d697`.

## Evidence paths and scheduler action

- Canonical protocol:
  `.protocols/TASK-020-T3-FT-002-W17/verification.md`.
- Fresh verifier matrix:
  `.tasks/TASK-020-T3-FT-002-W17/VerifierAttempt2IdentityMatrixProbe.java`.
- Exact Attempt-1 reproducer:
  `.tasks/TASK-020-T3-FT-002-W17/VerifierResponseIdentityProbe.java`.
- Executor retry evidence:
  `.tasks/TASK-020-T3-FT-002-W17/red-green-evidence-attempt-2.md`.

Device/live-provider evidence remains `DEFERRED`; no Android runtime or live-
provider PASS is claimed. The task remains `in_progress`; lifecycle, dependents,
scheduler checkpoint and terminal state are unchanged, and `/mb-sync` was not
run.

Recommended scheduler action: `/exe TASK-020-T3-FT-002-W17` bounded retry that
captures provider, location and request from one immutable access snapshot
before request construction and carries that same identity through acceptance,
then fresh `/verify TASK-020-T3-FT-002-W17`. Do not promote `TASK-021` or
`TASK-022`, close the task, run `/red-verify` or invoke `/mb-sync` before a fresh
functional PASS.
