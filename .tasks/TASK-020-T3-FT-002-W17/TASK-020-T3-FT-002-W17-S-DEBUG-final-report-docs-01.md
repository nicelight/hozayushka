---
description: Confirmed diagnosis of the Attempt-2 selected-provider request identity split for TASK-020-T3-FT-002-W17.
status: final
task_id: TASK-020-T3-FT-002-W17
stage_id: S-DEBUG
execution_attempt: 2
role: Reviewer
---
# Debug report — TASK-020-T3-FT-002-W17 Attempt 2

## Symptom and reproduction

- Canonical Attempt-2 `/verify` result: functional `FAIL` despite all three
  required gates passing. The verifier-owned host probe
  `.tasks/TASK-020-T3-FT-002-W17/VerifierAttempt2IdentityMatrixProbe.java`
  completed 10 scenarios with `94/102` assertions passing and 8 failures.
- The exact Attempt-1 location change inside `fetch` now passes, as do all eight
  expanded in-fetch location/provider stale-success/stale-failure scenarios.
- The remaining two scenarios fail identically for Open-Meteo and OpenWeather:
  request coordinates are captured from location A, Settings returns location B
  to the later identity capture, and the A response is accepted as a successful
  B refresh. Cache, pressure history and projection B are updated, and B appears
  `FRESH`.
- This diagnosis did not rerun a build, test suite or probe. Reproduction is the
  fresh canonical verifier observation bound to current Attempt 2, corroborated
  by the complete probe source and current production call path. No network,
  credential, emulator/AVD/QEMU, `adb` or device operation was performed.

## Current Execution Attempt and actual change surface

- Current Execution Attempt: `2`; indexed task status remains `in_progress`.
- Attempt-2 app change surface recorded by the executor and confirmed in the
  current source/diff:
  - `app/src/main/kotlin/com/hozayushka/app/weather/WeatherCapability.kt`
  - `app/src/test/kotlin/com/hozayushka/app/WeatherProviderDispatchTest.kt`
- Attempt 2 added `ProviderRequestIdentity` and a response-time identity guard.
  The retained Attempt-1 migration surface supplies exactly two adapters,
  selected-only dispatch, provider/location records and selected-OpenWeather
  key access. No current evidence points to an adapter, decoder, cache schema,
  composition or provider-inventory defect.

## Confirmed call sequence and root cause

The failing call sequence is directly supported by current source and the
verifier's `SwitchingLocationAccess`:

1. `WeatherCapability.refreshIfNeeded` resolves provider P, then reads location
   A at `WeatherCapability.kt:722-724`.
2. It performs cadence lookup for `(P,A)`, selects adapter P and constructs the
   immutable `WeatherProviderRequest` from A coordinates at `:729-740`.
   OpenWeather additionally calls `withSelectedOpenWeatherApiKey`, which loads
   Settings again to authorize/read the key; Open-Meteo remains keyless.
3. `refreshWithProvider` receives that already-built request but rereads mutable
   Settings location at `:629` and creates `ProviderRequestIdentity(P,B)` at
   `:630-634`.
4. Adapter P fetches with request A at `:640` and returns an A response.
5. `acceptProviderResult` compares current `(P,B)` to captured `(P,B)` at
   `:649-650`; the guard therefore passes because it never compares against the
   identity that constructed request A.
6. The response is normalized with location B and written to B history/cache/
   projection at `:677-707`, then returned as success.

Confirmed root cause: Attempt 2 placed the immutable identity boundary before
`fetch` but after request construction. `ProviderRequestIdentity` therefore
describes a later Settings read, not the selected-provider request it purports
to identify. The response guard is internally consistent yet guards the wrong
identity.

The first violated invariant occurs at `WeatherCapability.kt:629-634`, before
any state write: one selected-provider refresh must consume the single validated
Settings & Location access projection containing provider, selected location/
coordinates and selected-OpenWeather key authority. Instead, request authority
is split across multiple mutable Settings reads and request A is rebound to
identity B. This violates:

- `.memory-bank/contracts/capability-interfaces.md#weather-context-to-settings-and-location`:
  one validated access projection and selection resolved once per refresh;
- `.memory-bank/contracts/weather-provider.md#weather-provider-boundary` and
  `#provider-selection-and-dispatch`: request provider/location/key authority
  belongs to the selected Settings context and exactly its matching adapter;
- `.memory-bank/contracts/weather-provider.md#cache-history-and-refresh-rules`
  and `.memory-bank/domains/local-data.md#ft-002-weather-context-records`: only
  the provider/location identity matching the request may receive normalized
  cache/history state.

The later B normalization/write is the first resulting data-state violation of
`FT-002-AC-004`, `FT-002-AC-005`, `FT-002-AC-008` / `REQ-007`, `REQ-008`,
`REQ-029` and the Weather Freshness contract.

## Experiments and materially useful rejected hypotheses

- Attempt-1 guard absence is no longer the current cause: the exact original
  verifier probe and all eight mutations performed inside `fetch` pass.
- Provider-specific transport/decoder behavior is not causal: the same split is
  observed with deterministic fake Open-Meteo and OpenWeather providers before
  any real transport behavior differs.
- Cache filtering is not the originating defect: the filter accepts B because
  Weather Context itself incorrectly labels the A response with B before the
  write. Changing storage layout or adding another cache check would guard a
  self-created false identity rather than fix request authority.
- Cross-provider fallback, third-provider wiring and secret leakage are not
  implicated; the canonical verifier independently passed those inventories and
  scans.

## One correct snapshot boundary and minimum correction

The single boundary is the existing `Weather Context -> Settings & Location`
access projection, captured once at the beginning of `refreshIfNeeded` and
before cadence lookup, adapter selection, key access or request construction.
Settings & Location must load one immutable `SettingsState` and expose, for that
one refresh preparation only:

- selected provider;
- selected immutable `LocationContext` and canonical location identity;
- selected-OpenWeather key authority from that same state, with the raw key
  usable only inside the existing ephemeral callback; and
- enough values for Weather Context to construct the immutable
  `WeatherProviderRequest` inside that callback.

Weather Context must derive one prepared attempt from this access snapshot:
`ProviderRequestIdentity + matching adapter + WeatherProviderRequest`. Cadence
lookup also uses this identity. `refreshWithProvider` must accept the prepared
identity and must not reread Settings to invent a request identity. Response
acceptance may read the current selection/location only as a stale guard, but it
must compare them with the original pre-request snapshot and must never use the
new read to relabel or normalize the response.

The OpenWeather key is request authority, not a cache/history identity
component. No key value, hash, version or new lifecycle state is required:
the same immutable access snapshot decides whether the OpenWeather key may be
used and supplies it only to that snapshot's request construction. Open-Meteo
must never enter the key callback.

### Minimum final-retry implementation write surface

Exactly these task-local files are sufficient:

1. `app/src/main/kotlin/com/hozayushka/app/settings/SettingsCapability.kt` —
   make the existing Weather Access reader expose one Settings-owned immutable
   refresh-access snapshot/callback from one `SettingsState` load. Do not change
   persistence, validation, UI, provider set, key storage/redaction posture or
   dependency edges.
2. `app/src/main/kotlin/com/hozayushka/app/weather/WeatherCapability.kt` — build
   cadence identity, adapter choice, request identity and request from that one
   snapshot; carry the same identity through fetch and acceptance; remove the
   second pre-fetch location capture as an authority source.
3. `app/src/test/kotlin/com/hozayushka/app/WeatherProviderDispatchTest.kt` — add
   the distinguishing deterministic matrix below and mechanically adapt its
   one Weather Access fake if the existing interface shape changes.

No change is required or allowed for this correction in
`WeatherProviderAdapter.kt`, either provider adapter, `FoundationRuntime.kt`,
cache serialization/storage, Settings UI/resources, forecast/timer code,
canonical specs, task identity/tier/dependencies or lifecycle/scheduler state.
Normal `/exe` protocol and evidence writes remain workflow-owned rather than
implementation scope. The verifier-owned current probe must not be weakened or
edited by execution to manufacture a pass.

## Required stale success/failure behavior

- If current provider or canonical location identity differs from the original
  pre-request snapshot when a provider result arrives, both success and failure
  are stale and are discarded: return no refresh result; do not normalize, save
  cache, append history, rebuild the projection, overwrite a matching old
  partition, update `lastRefreshFailure`, leak an inline error to the new
  identity or call the other adapter.
- Projection then comes only from the newly current matching partition: use its
  accepted freshness if one exists, otherwise `NO_DATA`; switching back exposes
  the untouched original matching partition.
- If provider/location still match the original snapshot, success follows the
  existing atomic normalize/write path. A matching failure preserves matching
  cache/history/projection, records only the selected-provider inline failure
  and never invokes fallback.

## Minimum distinguishing regression matrix

Retain the verifier's current 10-scenario shape; it is the minimum symmetric
matrix that distinguishes the complete correction from both incomplete
attempts without expanding into a full Cartesian product:

- 8 in-fetch scenarios: both initial providers × location/provider switch ×
  stale success/failure. These fail the Attempt-1 implementation, prove that a
  stale failure cannot leak an error, and preserve no-fallback behavior.
- 2 request-construction/capture-window scenarios: Open-Meteo and OpenWeather.
  These fail Attempt 2 and prove provider-specific keyless/keyed request
  preparation starts from the same immutable access snapshot.

For every stale scenario assert: returned result is null; prior cache/history
is value-equivalent (or remains absent); current projection is matching-cache
freshness or `NO_DATA`, never stale-response `FRESH`; no stale inline error;
selected adapter exactly once; other adapter zero times. In the two capture-
window scenarios additionally assert request coordinates come from snapshot A;
Open-Meteo has no credential/key access; OpenWeather performs exactly one
selected-key authorization from snapshot A and constructs a credential-bearing
request without recording the value.

Recommended durable focused command after implementation:

```bash
./gradlew testDebugUnitTest --tests 'com.hozayushka.app.WeatherProviderDispatchTest.immutablePreRequestSnapshotAndStaleResponseMatrix' --rerun-tasks --no-daemon
```

Fresh `/verify` must also rerun the independent probe with its 10 scenario
semantics preserved. Using the current compiled-app classpath shape:

```bash
verify_probe_out="$(mktemp -d)"
verify_app_jar="app/build/intermediates/compile_app_classes_jar/debug/bundleDebugClassesToCompileJar/classes.jar"
verify_kotlin_jar="/home/serg/.gradle/caches/modules-2/files-2.1/org.jetbrains.kotlin/kotlin-stdlib/2.0.20/7388d355f7cceb002cd387ccb7ab3850e4e0a07f/kotlin-stdlib-2.0.20.jar"
verify_android_jar="/home/serg/Android/Sdk/platforms/android-35/android.jar"
verify_classpath="$verify_app_jar:$verify_kotlin_jar:$verify_android_jar"
javac -encoding UTF-8 -source 17 -target 17 -cp "$verify_classpath" -d "$verify_probe_out" .tasks/TASK-020-T3-FT-002-W17/VerifierAttempt2IdentityMatrixProbe.java
java -cp "$verify_probe_out:$verify_classpath" VerifierAttempt2IdentityMatrixProbe
```

Expected current-probe result after correction: exit `0`, 10 scenarios,
`102/102`, zero failures. A fresh verifier may mechanically adapt its probe to
the corrected access API, but must preserve all scenarios and assertions and
must add, not replace, the key-authority observations above.

## Replan, residual uncertainty and next owner

- Replan required: **no**. The correction implements the already accepted
  access projection and selected-provider request identity inside the existing
  `Weather Context -> Settings & Location` edge. Task identity, T3 tier,
  acceptance ownership, provider set, storage owner, security posture and
  architecture graph remain unchanged; `touched_files` is advisory and no hard
  `runtime_context.write_boundary` exists.
- Root-cause uncertainty: none. The actual final-retry edit and post-edit probe
  results remain unperformed by this read-only diagnosis.
- Because the correction crosses the existing Settings and Weather Context
  surfaces, the `/debug` handoff route is
  `/technical-premortem TASK-020-T3-FT-002-W17`, followed by its advisory
  return to the bounded final `/exe` retry and then fresh `/verify`. Do not run
  `/red-verify`, promote TASK-021/022, close/change lifecycle, replan or invoke
  `/mb-sync` before functional PASS.

DIAGNOSIS: CONFIRMED
