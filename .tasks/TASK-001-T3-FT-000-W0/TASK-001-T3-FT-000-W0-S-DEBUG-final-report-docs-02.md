# TASK-001-T3-FT-000-W0 — boundary debug report

## Symptom and reproduction

The current adversarial verification finds a material boundary violation on the
supported Foundation probe path: `Main Display` directly imports and constructs
the weather provider request.

The finding is reproducible with the focused source probe:

```text
rg -n '^import com\.hozayushka\.app\.adapters\.weather|weather\.refresh\(' app/src/main/kotlin/com/hozayushka/app/display/DisplayCapability.kt
```

Observed matches:

- `DisplayCapability.kt:11` imports `WeatherProviderRequest` from
  `adapters.weather`.
- `DisplayCapability.kt:128` calls
  `weather.refresh(WeatherProviderRequest.fromSyntheticProbe())`.

The call is wired to Foundation probe UI controls, so it is production APK
reachability rather than test-only code.

## Current attempt and actual change surface

- Execution Attempt: `2`, started `2026-08-04 16:08:43 +0500`, recorded in
  `.protocols/TASK-001-T3-FT-000-W0/context.md:14-35`.
- Task state remains `in_progress`; this diagnosis changes no task, protocol,
  tier, dependency, retry or lifecycle state.
- Relevant Attempt 2 surface:
  `DisplayCapability.kt`, `FoundationRuntime.kt`, `MainActivity.kt`,
  `TimerCapability.kt`, `PlatformRuntimeAdapter.kt`, the host probe test and
  the Foundation navigation/protocol evidence. The full surface is recorded in
  `.protocols/TASK-001-T3-FT-000-W0/context.md:112-133`.
- No implementation or verification file was changed by this diagnosis. This
  report is the only new diagnostic artifact.

## Root cause and first violated invariant

Confirmed root cause: the retry correction placed synthetic provider-request
construction in `DisplayCapability` to make the installed-app weather button
reachable. That local tactic bypassed the accepted Weather Context ownership
boundary even though `WeatherCapability` already owns `refresh()` and the
provider adapter.

The first violated invariant is the closed-world dependency graph:
`.memory-bank/contracts/boundary-map.md:41-58` authorizes `Main Display →
Weather Context` and separately `Weather Context → Yandex Weather Adapter`, and
states that an absent edge is not authorized. The capability contract reinforces
the same rule: `.memory-bank/contracts/capability-interfaces.md:30-42` assigns
provider refresh authority to Weather Context, while
`.memory-bank/contracts/weather-provider.md:18-24` places the provider request
boundary behind that owner. `WeatherCapability.kt:87-110` is the observable
owner-side refresh/cache path that the probe should have called indirectly.

Thus the material failure is `Main Display → Yandex Weather Adapter` plus
provider orchestration outside Weather Context, not merely an import-style
violation.

## Experiments and rejected hypotheses

- The focused import/call-site scan found both the adapter type and request
  construction in `DisplayCapability`; this directly connects the failure to a
  production path.
- The existing `Main Display → Weather Context` graph edge does not authorize
  transitive direct access to the adapter. The graph explicitly treats absent
  edges as unauthorized, so this is not a harmless implementation detail.
- Rejected “the composition root wires the graph” as a justification: the
  composition root passes the Weather owner into Display, but Display still
  constructs the adapter-owned request itself.
- Rejected “the request is synthetic and redacted” as a boundary exemption:
  credential safety is separate from ownership and dependency direction; a
  synthetic request can still cross the wrong edge.
- Rejected “host tests pass” as sufficient: the violating call is on the APK
  probe button, while host tests do not prove the accepted dependency graph.

## Minimum recommended correction

Keep the task identity and accepted graph unchanged. Move synthetic fixture
request construction behind the Weather Context owner—for example, expose a
Foundation-only owner method that internally creates
`WeatherProviderRequest.fromSyntheticProbe()` and invokes the existing
provider-backed refresh. `DisplayCapability` should call only that Weather
Context method and remove its `adapters.weather` import.

Do not add a Display → adapter edge, shared request factory, event bus, new
dependency or feature-level provider mapping. Route to planning only if the
correction would require changing the accepted public boundary or ownership.

## Suitable regression check

- Static boundary check must fail if `DisplayCapability.kt` imports
  `com.hozayushka.app.adapters.weather` or contains
  `WeatherProviderRequest/fromSyntheticProbe`.
- The Foundation probe source must still expose a weather action through the
  `WeatherCapability` owner.
- Rerun `./gradlew clean assembleDebug testDebugUnitTest`, then the existing
  boundary/import and secret scans. The boundary report must show only
  `Main Display → Weather Context` and `Weather Context → Yandex Weather
  Adapter`.

## Residual uncertainty and next owner

The exact name of the Weather Context Foundation-only method is a local
implementation choice; the ownership and direction are already explicit. No
device is needed to establish this source-level finding or verify its removal.

Next owner: TASK-001 lifecycle owner / implementer via a separate
`/exe TASK-001-T3-FT-000-W0` retry. Do not promote or execute TASK-002 until
functional and adversarial evidence is fresh after that correction.

DIAGNOSIS: CONFIRMED
