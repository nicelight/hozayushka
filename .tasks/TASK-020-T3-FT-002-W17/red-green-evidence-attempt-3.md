---
description: Final retry Attempt-3 RED/GREEN evidence for coherent selected-provider request identity.
status: active
task_id: TASK-020-T3-FT-002-W17
execution_attempt: 3
---
# Attempt 3 — coherent request identity RED/GREEN

## Retry binding

- Original claim RED remains in `red-green-evidence.md`; Attempt-2 correction
  RED remains in `red-green-evidence-attempt-2.md`.
- Fresh correction basis is the independent Attempt-2 `/verify` matrix
  `94/102`, the confirmed `/debug` report and scheduler `GO_WITH_CONDITIONS`
  premortem. The affected claims are `FT-002-AC-004`, `FT-002-AC-005` and
  `FT-002-AC-008` / `REQ-007`, `REQ-008`, `REQ-029`.
- Isolation: deterministic host-only fake providers, synthetic in-memory key,
  disposable stores and fixed time/location. No credential value, network,
  live provider, subscription, emulator/AVD/QEMU, Android Studio virtual
  device, `adb` or physical device is used.

## Fresh retry RED

Command:

```text
./gradlew testDebugUnitTest --tests 'com.hozayushka.app.WeatherProviderDispatchTest.immutablePreRequestSnapshotAndStaleResponseMatrix' --rerun-tasks --no-daemon --info
```

Result: exit `1`; `10` scenarios, `102` checks, `94` passed and `8` failed.
All eight in-fetch stale success/failure scenarios passed. Both provider
request-capture-window scenarios failed the same four observations: response
accepted, success returned, cache/history updated and projection shown FRESH
under the later location identity. Assertion output contained no credential or
synthetic-key value.

## Fresh correction GREEN

Production correction:

- Settings loads one immutable provider+location projection and retains key
  authority only behind its nested selected-OpenWeather callback.
- Weather derives cadence identity, adapter and request from that projection.
- Immediately after fetch, Weather reads one coherent current projection and
  rejects a provider/location mismatch before inspecting the result or changing
  failure/cache/history/projection state.

Focused command:

```text
./gradlew testDebugUnitTest --tests 'com.hozayushka.app.WeatherProviderDispatchTest.immutablePreRequestSnapshotAndStaleResponseMatrix' --tests 'com.hozayushka.app.WeatherProviderDispatchTest.keyReadCadenceAdapterAndFreshnessBoundariesAreExact' --rerun-tasks --no-daemon
```

Result: exit `0`, `2/2` tests. Durable output records:

- identity matrix: `10` scenarios, `102/102`, zero failures;
- stale success/failure: records/history unchanged, no stale inline error,
  selected adapter one call per stale attempt, other adapter zero;
- key reads: Open-Meteo `0`, OpenWeather due refresh `1`, network unavailable
  `0`, scheduled before 30 minutes `0`, mismatched adapter `0`;
- exactly 30 minutes: one selected adapter call, other adapter zero;
- freshness: `FRESH` through 24 hours inclusive and `STALE_EMPTY` at
  24 hours + 1 ms; and
- credential value recorded: false.

Unmodified verifier-owned probe result: exit `0`; `10` scenarios, `102/102`,
zero failures. Neither `VerifierAttempt2IdentityMatrixProbe.java` nor
`VerifierResponseIdentityProbe.java` was edited.

## Required gates

- `./gradlew clean assembleDebug --no-daemon` → exit `0`, `34/34` actionable
  tasks.
- `./gradlew testDebugUnitTest --rerun-tasks --no-daemon` → exit `0`, `86/86`
  across `13` reports, zero failures/errors/skips.
- `node scripts/mb-lint.mjs && git diff --check` → exit `0`; `78` Memory Bank
  files and no diff whitespace errors.
- Task security/APK scan → exit `0`, `4/4 PASS`: zero known marker, credential
  candidate and Yandex findings in workspace/evidence/APK surfaces.
- Independent inventory: exactly `2` production provider implementations;
  source endpoints `1 + 1`, APK endpoint entries `1 + 1`, Yandex `0 + 0`.
- Debug APK SHA-256:
  `4e0e569fe99cddb5c29906914993dda6324727d19bc1b5e48349acf1fb55646f`.

No reuse candidate is offered because the workspace has broad shared
dirty/untracked inputs and Gradle has a broad read surface.
