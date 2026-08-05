# Adversarial semantic verification — TASK-001-T3-FT-000-W0

## Accepted intent and inspected evidence

The T3 task must leave a supported installed-app Foundation probe path while
preserving the accepted capability-slice graph. Functional `/verify` is `PASS`,
but it is not semantic proof. The review inspected the task card, downstream
Foundation Gate task, direct canonical specs, current production/test source,
manifest, Gradle dependencies, APK route and full task protocol.

## Admitted material finding

The installed-app Foundation weather probe in
`app/src/main/kotlin/com/hozayushka/app/display/DisplayCapability.kt` imports
`WeatherProviderRequest` from `adapters.weather` and calls
`weather.refresh(WeatherProviderRequest.fromSyntheticProbe())`.

This is a supported `Main Display → Yandex Weather Adapter` dependency and
provider-request construction outside Weather Context. The canonical graph
allows `Main Display → Weather Context` and separately
`Weather Context → Yandex Weather Adapter`; `boundary-map.md#dependency-graph`
explicitly disallows absent edges. The Main Display contract also assigns
provider refresh authority to Weather Context. Therefore the task's accepted
architecture/boundary outcome is materially false on its supported probe path.

Evidence: `.memory-bank/contracts/boundary-map.md:35-58`,
`.memory-bank/contracts/capability-interfaces.md:30-42,110-138`,
`.memory-bank/contracts/weather-provider.md:18-24`,
`DisplayCapability.kt:11,127-128`, and
`WeatherCapability.kt:87-110`.

## Failure / Blocker

- Status: semantic failure.
- Expected: provider request construction and refresh orchestration stay behind
  the Weather Context owner; Main Display uses only its registered boundary.
- Observed: Main Display directly imports the provider adapter request type and
  constructs the request on the installed-app path.
- Next action: correct within the current Foundation scope, remove the direct
  Display → adapter dependency, then rerun `/exe`, `/verify` and `/red-verify`.
  Replan through `/feature-to-tasks FT-000` is needed only if the correction
  changes the accepted scope or graph.

SEMANTIC_VERDICT: semantic-fail
