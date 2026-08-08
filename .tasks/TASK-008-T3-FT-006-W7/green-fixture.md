# Claim-equivalent GREEN — attempt 1

## Isolated fixture

- Timer state: fresh `InMemoryTimerStateStore` per test, or one explicitly
  resettable store for the rehydration case.
- Time: fixed synthetic millisecond timestamps only.
- Settings: `SettingsCapability(InMemorySettingsStateStore())` supplies the
  existing validated preset projection; no Settings writes are introduced by
  Timer & Alert.
- Provider/network: no provider object, request, credential or live network is
  used by the timer tests; the no-provider claim exercises the same owner path.
- Cleanup: each test owns its fixture; no filesystem or external state is
  touched.

## Command

```text
./gradlew testDebugUnitTest --tests com.hozayushka.app.TimerLifecycleTest --rerun-tasks
```

Working directory: `/home/serg/Projects/Mobile_APPS/hozayushka`

Result: exit code `0`; `BUILD SUCCESSFUL`.

## Claim results

- `FT-006-AC-001 / REQ-012`: selected SECOND preset starts at the supplied
  timestamp, snapshot is `COUNTDOWN`, remaining is owner-derived `599000 ms`,
  active projection contains only SECOND, and the display formatter produces
  `09:59`.
- `FT-006-AC-002 / REQ-011`: starting FIRST then THIRD replaces the one
  persisted record; the projection reports exactly one active THIRD slot.
- `FT-006-AC-003 / REQ-013`: single tap returns `COUNTDOWN` with the hint flag;
  double tap returns `IDLE` and clears the active record.
- `FT-006-AC-004 / REQ-014`: a fresh Timer capability over the same store
  rehydrates `COUNTDOWN` with `1000 ms` remaining before the boundary and
  `OVERDUE` with full elapsed arithmetic after it.
- `FT-006-AC-005 / REQ-025`: with no provider/network input, rehydrated
  `OVERDUE` accepts a single tap and returns `IDLE`.

This is executor supporting evidence, not independent verification or a
runtime/device PASS.
