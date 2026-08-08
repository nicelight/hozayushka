---
description: Fresh independent verifier evidence for TASK-007-T3-FT-005-W6.
status: final
---
# Fresh verifier evidence — TASK-007-T3-FT-005-W6

## Run basis

- reviewer session: 2026-08-08 06:03 +0500
- repository HEAD: `a93e46118f0f0b90e311b6174e3f5a8ed7d89fef`
- task state observed: `in_progress`; lifecycle, planning, specs and scheduler state were not changed
- executor attempt-2 receipts were inspected but not reused; all results below are fresh verifier observations

## Fresh functional gates

- `./gradlew clean assembleDebug` — exit `0`, `BUILD SUCCESSFUL`; APK SHA-256 `46f0e5ae97a88d64777821e29e80d0920b1e8b21c682f2b8e3fd9cdfbb7eb940`.
- `./gradlew testDebugUnitTest --rerun-tasks` — exit `0`, `BUILD SUCCESSFUL`.
- `./gradlew clean testDebugUnitTest` after probe cleanup — exit `0`; XML totals `32` tests, `0` failures, `0` errors.
- `node scripts/mb-lint.mjs` — exit `0`, `mb-lint passed (77 files)`.
- `git diff --check` — exit `0`.

## Corrected invalid-input path

Fresh source-contract probe and line inspection established:

- all three `EditText` fields are represented by three field triples at
  `SettingsCapability.kt:276-291`;
- `restoreFields` sets all three field values under `restoringFields = true`
  and clears the guard only after the loop (`:306-314`);
- each watcher routes through `persistFields`, whose first branch returns while
  restoring (`:316-317`), so restoration cannot recursively persist;
- rejected input calls `restoreFields(result.duration)` and keeps the owning
  inline error (`:324-325`);
- validation returns the current duration before `stateStore.save` (`:222-230`),
  so invalid input cannot write owner-local preset state.

The temporary verifier-owned host probe
`VerifierOwnedFt005Attempt3ProbeTest.invalidOwnerUpdateDoesNotWriteOrChangeActiveTimerProjection`
was run with `./gradlew testDebugUnitTest --tests
com.hozayushka.app.VerifierOwnedFt005Attempt3ProbeTest --rerun-tasks` and passed.
It exercised hours `100`, minutes `60`, seconds `60` and zero total after a
valid `2:04:06`; every rejection produced the same duration, made zero owner
store writes, and left the active Timer snapshot and presentation unchanged.
The temporary test source was removed before the final clean suite.

## Other task-owned claims

The final clean suite passed the existing FT-005 tests for three independent
defaults/reload, all accepted ranges and positive-total validation,
highest-non-zero floor labels, fixed orange/pink/purple colors, and one active
Timer projection. Current source retains the public `TimerPresetReader` edge;
Display uses Timer projections and does not access private Settings storage.

## Target route

`adb devices` returned only `List of devices attached`. Target visual/readability
and custom-ROM runtime evidence remain `DEFERRED` and non-blocking; no runtime
`PASS` is claimed.
