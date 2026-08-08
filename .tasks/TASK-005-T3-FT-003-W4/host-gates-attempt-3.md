# Attempt 3 correction and host gate evidence

## Retry basis and correction

- source basis: repository revision
  `a93e46118f0f0b90e311b6174e3f5a8ed7d89fef` with the existing task/user
  worktree preserved.
- latest failed gate evidence:
  `.tasks/TASK-005-T3-FT-003-W4/TASK-005-T3-FT-003-W4-S-VERIFY-final-report-docs-01.md`.
- original RED retained: `.tasks/TASK-005-T3-FT-003-W4/red-baseline.md`.
- prior attempt evidence retained: `.tasks/TASK-005-T3-FT-003-W4/host-gates.md`
  and `.tasks/TASK-005-T3-FT-003-W4/host-gates-attempt-2.md`.
- correction: `ForecastSessionCapability` now records active hold as owner-local
  transient state. `snapshotAt()` preserves `OPEN` while hold is active even
  beyond the original three-second deadline; `release()` closes immediately and
  clears hold state.
- unchanged behavior: without hold the original three-second auto-close remains;
  single tap still enters `HINT` and cancels auto-close, double tap still closes,
  ordinary release-close remains, and no capability owner, dependency, graph
  edge, storage/provider access or task/scheduler lifecycle changed.

Attempt-3 implementation/test files:

- `app/src/main/kotlin/com/hozayushka/app/forecast/ForecastSessionCapability.kt`
- `app/src/test/kotlin/com/hozayushka/app/ForecastSessionTest.kt`

## Claim-equivalent GREEN

- claim: `FT-003-AC-004 / REQ-009`, active hold beyond the original auto-close
  deadline and immediate release-close.
- retry sequence: open at elapsed `0 ms`, hold at `600 ms`, snapshot at
  `3500 ms` remains `OPEN`, then release at the same deterministic timestamp
  returns `CLOSED`.
- targeted command:
  `./gradlew testDebugUnitTest --tests 'com.hozayushka.app.ForecastSessionTest.holdKeepsSessionOpenBeyondOriginalDeadlineAndReleaseClosesImmediately'`.
- targeted result: exit `0`, `BUILD SUCCESSFUL`; exact testcase is present in
  `app/build/test-results/testDebugUnitTest/TEST-com.hozayushka.app.ForecastSessionTest.xml`.
- probe strength: a dedicated compiled host test drives the accepted public
  `openHourly`/`hold`/`snapshotAt`/`release` state surface with deterministic
  timestamps; the original RED is not replayed or replaced.

## Mandatory host gates

- `./gradlew clean assembleDebug` — exit `0`, `BUILD SUCCESSFUL`; debug APK
  SHA-256
  `7a7dbc930121ba638223080ceda5e77cd736db897910546d335e16a41f65464c`.
- `./gradlew testDebugUnitTest` — exit `0`, `BUILD SUCCESSFUL`; `20` tests,
  `0` skipped, `0` failures, `0` errors. Attempt-3 test report:
  `app/build/test-results/testDebugUnitTest/TEST-com.hozayushka.app.ForecastSessionTest.xml`.
- `node scripts/mb-lint.mjs` — exit `0`, `mb-lint passed (76 files)`.
- boundary/static bundle — exit `0`: positive `rg` checks confirmed Forecast
  Sessions uses `WeatherReadPort` plus `PlatformRuntime`, Main Display consumes
  `ForecastSessionCapability`, and the hourly renderer still consumes the shared
  illustration mapping; negative `rg`/function-scoped `awk | rg` checks found no
  raw provider/private-store access or hourly pressure-arrow branch. Attempt-3
  session checks confirmed the hold flag, suspended deadline condition,
  hold activation, release surface and dedicated regression test.
- source/test secret/redaction bundle — exit `0`: `rg -n -i` over `app/src/main`
  and `app/src/test` found no key-shaped credential, authorization value or
  private-key marker; the redacted fixture and `request.redactedCredential()`
  projection were positively confirmed.
- task evidence secret scan — exit `0`: the same credential-shape scan over
  `.tasks/TASK-005-T3-FT-003-W4` and
  `.protocols/TASK-005-T3-FT-003-W4` found no match.
- APK secret scan — exit `0`:
  `unzip -p app/build/outputs/apk/debug/app-debug.apk | strings | rg -n -i <credential-shape-pattern>`
  found no match.
- `git diff --check` — exit `0`.

Credential-shape pattern used consistently by the source, evidence and APK
negative scans:

```text
AIza[0-9A-Za-z_-]{35}|AQVN[0-9A-Za-z_-]{20,}|AKIA[0-9A-Z]{16}|sk-[0-9A-Za-z]{20,}|ya29\.[0-9A-Za-z_-]{20,}|-----BEGIN ([A-Z ]+ )?PRIVATE KEY-----|Api-Key[[:space:]]+[0-9A-Za-z_-]{16,}|Bearer[[:space:]]+[0-9A-Za-z._-]{20,}
```

## Boundary and scope result

- Forecast Sessions remains the owner of session state and timing and uses only
  the approved Weather Context read port and platform timing boundary.
- No non-empty hard write boundary is configured. The two attempt-3 files stay
  within the advisory task surface; protocol/evidence writes are skill-owned.
- `forbidden_scope` was not touched. AC-001 through AC-003 and AC-005 behavior
  remains covered by the full passing host suite.
- No reusable receipt is offered; results depend on the current dirty worktree
  and generated build state and remain executor self-attested evidence.

## Target evidence

- command: `adb devices`.
- result: only `List of devices attached`; no authorized device/emulator target.
- status: `DEFERRED` (non-blocking); no runtime PASS claim.
- planned initial state/rerun/observation/cleanup: start from a closed forecast
  session with the redacted complete-hourly fixture; reopen Today safely; hold
  beyond the three-second deadline, observe the session remaining visible, then
  release and observe immediate close; also observe 1280×720 readability and
  shared Android gestures; close/reset transient fixture/session state.
- residual risk: actual custom-ROM gesture dispatch/timing, glyph/font rendering
  and 1280×720 card readability remain unobserved on an Android target.
