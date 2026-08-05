# TASK-001-T3-FT-000-W0 — debug report

## Symptom and reproduction

Observed symptom: the formal functional evidence records `VERDICT: PASS`, but
the installed APK can only launch the static Foundation shell and apply window
flags. The required Settings, timer, weather, interruption/rehydration and
audio operations are not reachable through a supported app/device path.

The symptom is reproducible from the current source surface without a device:

- `MainActivity` only applies the window policy, creates the Foundation view,
  and reapplies the window policy on resume
  (`app/src/main/kotlin/com/hozayushka/app/app/MainActivity.kt:6-20`).
- `DisplayCapability` creates three `TextView` instances and contains no
  operation controls or gesture route
  (`app/src/main/kotlin/com/hozayushka/app/display/DisplayCapability.kt:11-49`).
- Production call-site, interaction, lifecycle/audio and instrumentation scans
  have no matches for the required operations; the same capability calls occur
  only under `app/src/test/kotlin/com/hozayushka/app/FoundationProbesTest.kt`.
  The complete scan and its result are recorded in
  `.protocols/TASK-001-T3-FT-000-W0/red-verification.md:28-41`.
- `PlatformRuntimeAdapter` exposes time and window flags only; it has no
  lifecycle signal or audio request surface
  (`app/src/main/kotlin/com/hozayushka/app/adapters/platform/PlatformRuntimeAdapter.kt:11-53`).

Therefore the verification-only smoke and compatibility matrix in
`.memory-bank/tasks/TASK-002-T3-FT-000-W1.task.json` cannot be executed through
the implementation currently produced by TASK-001.

## Current attempt and actual change surface

- Execution Attempt: `1`, started `2026-08-04 14:57:04 +0500`, recorded in
  `.protocols/TASK-001-T3-FT-000-W0/context.md:14-20`.
- Task state remains `in_progress`; this diagnosis does not alter lifecycle
  state, tier, dependencies, scope or retry state.
- Actual change surface is the one-module Gradle/Android project, composition
  root, display/weather/forecast/timer/settings capability roots, platform and
  weather adapters, host tests/fixture, Foundation navigation and protocol/task
  evidence, as recorded in
  `.protocols/TASK-001-T3-FT-000-W0/progress.md:25-52`.
- No implementation, test, specification or scheduler file was changed by
  this diagnosis. The only diagnostic artifact is this report.

## Root cause and first violated invariant

Confirmed root cause: execution interpreted “walking skeleton” as a static
display shell plus host-only capability probes and deliberately deferred the
app/device operation path to the final gate. The implementation comments and
protocol scope describe the shell as preliminary, while the accepted Foundation
proof requires a clean launch, known/resettable local state, visible smoke,
timer persistence/arithmetic, redacted provider use, and a target-device path
for lifecycle/audio behavior. The task/queue split then made TASK-002
verification-only and restricted it to the surface produced by TASK-001.

The first violated invariant is the Foundation minimal-proof contract:
`.memory-bank/testing/runtime-verification.md:18-26` requires the executable
Foundation path to include the local-state/smoke/provider operations and the
target-device lifecycle/audio route. The related platform invariant is also
violated: `.memory-bank/contracts/platform-runtime.md:27-36` requires the
application to receive temporary lifecycle signals, rehydrate timer state and
request policy-compliant audio. The APK currently provides neither supported
entry points nor the platform surface needed to exercise those outcomes.

This connects the symptom to the root cause and invariant independently of the
absence of a target device: source and packaged-entry inspection already shows
that the calls cannot be reached even when a device is later attached.

## Experiments and rejected hypotheses

- Reproduced the production-call-site and interaction scan from the red
  verification. Settings seed/reset, timer start/cancel and weather refresh
  appear in host tests only; production has no matching calls. This confirms a
  missing app path rather than an unobserved runtime failure.
- Inspected the APK entry configuration. The manifest has a valid launchable
  `MainActivity` and landscape configuration, so package/install/start failure
  is not the cause; the reachable behavior is simply limited to the static
  shell.
- Rejected “host tests passed” as sufficient evidence: those tests construct
  `InMemory*StateStore` capabilities directly and do not launch or instrument
  the APK (`app/src/test/kotlin/com/hozayushka/app/FoundationProbesTest.kt`).
- Rejected “no ADB device” as the root cause: it explains why physical
  observations were unavailable, but cannot explain the source-level absence
  of interaction, lifecycle and audio routes. The missing routes would remain
  absent on a connected device.
- Rejected “all operations are product behavior and therefore downstream” as a
  complete explanation: full feature semantics remain downstream, but the
  accepted Foundation smoke explicitly needs the minimal supported operations;
  TASK-002 is forbidden to implement them.

## Minimum recommended correction

Keep TASK-001 open and correct the Foundation implementation so one supported
installed-app/instrumentation route can seed/reset the disposable Settings and
weather state, start/cancel and rehydrate one timer, exercise the redacted
fixture, and expose the lifecycle/audio probe seam through the composition root
and owning capabilities. Keep full product UX, feature semantics, reboot
recovery, real credentials, new permissions and unapproved architecture or
dependency decisions out of this correction.

If that minimum route requires a changed public boundary, source of truth,
composition/ownership decision, new dependency or product behavior, stop at
the applicable task stop condition and route the original FT-000 planning
surface before retrying. Otherwise the existing task identity is sufficient for
the correction; the correction owner should rerun `/exe`, then independent
`/verify` and `/red-verify`.

## Suitable regression check

Add a task-scoped installed-app or instrumentation smoke check that starts from
a known reset state and proves the complete supported route: launch/display,
Settings seed, timer start/cancel, temporary interruption with timer
rehydration, redacted weather refresh, visual overdue state and permitted/
suppressed audio handling. Pair it with a source/manifest reachability check
that fails when those operations exist only under `app/src/test`. Keep the
existing deterministic host probes and secret/artifact scan as supporting
checks.

## Residual uncertainty and next owner

Residual uncertainty is limited to the exact minimal UI/instrumentation shape
and target-ROM behavior; those choices require implementation-owner judgment
within the accepted boundary and cannot be resolved by this read-only
diagnosis. The absence of an attached device still prevents physical runtime
observations, but it is not the confirmed blocker.

Next owner: TASK-001 lifecycle owner / implementer for an in-scope correction.
Do not advance or execute `TASK-002-T3-FT-000-W1` until the corrected surface
has fresh functional and adversarial evidence. No lifecycle, planning or
architecture decision is made by this report.

DIAGNOSIS: CONFIRMED
