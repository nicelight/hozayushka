---
description: Concrete foundation, integration and target-device evidence routes for V1 runtime risks.
status: active
last_updated: 2026-08-10
source_of_truth: .memory-bank/prd.md, .memory-bank/constitution.md, .memory-bank/testing/strategy.md
---
# Runtime Verification

## Purpose

This subject spec routes concrete proof for the accepted runtime, state,
provider and secret risks. It supplements the read-only project testing policy
in [Testing Strategy](strategy.md); it does not create a universal test-level
gate or replace feature acceptance criteria.

## Foundation Minimal Proof

Foundation must establish a reproducible path with:

- a clean build and host-test command for the single Android application;
- a known initial local-data state and a safe reset or isolated fixture path;
- a visible main-display smoke result;
- a deterministic timer arithmetic/persistence probe;
- a redacted provider fixture path that does not require a live API key.

Target-device probing is a separate readiness/release concern. It is not part
of the Foundation Gate while the application is still a walking skeleton.

`TASK-001-T3-FT-000-W0` establishes the preliminary project-native commands
`./gradlew assembleDebug` and `./gradlew testDebugUnitTest`, plus the ADB
install/start route recorded in [Foundation](../foundation.md). The supported
installed-app probe route is the same Activity with
`--ez foundation_probe true`; it exposes reset/seed Settings, redacted fixture
refresh, timer start/rehydration/cancel and the platform audio-policy probe
through the owning capability boundaries. The weather request is constructed
inside Weather Context; Display does not access the provider adapter directly.
The final Foundation Gate reruns these commands from the clean baseline and
records host-side evidence. It must not start an emulator, run ADB install or
launch, or perform physical-device smoke while the application is not ready.
The target-device route below is invoked by a later runtime/readiness task.

## Deterministic Host-Side Checks

Use the cheapest check that proves the requirement:

- timer state transitions, elapsed/remaining arithmetic, one-active-timer rule,
  labels and accepted gesture semantics;
- weather freshness, seven-day history window, pressure thresholds, unknown
  condition fallback, provider/location cache-history identity and all 78
  temperature colors with endpoint clamp;
- device-time versus selected-city-timezone formatting;
- selected-provider dispatch with no second adapter call; Open-Meteo 10 versus
  OpenWeather 8+2 daily projection; exact eight-slot completeness and
  missing-data gating;
- Open-Meteo default/no-key selection and explicit OpenWeather/local-key
  selection, provider-change refresh and failure without selection change;
- Settings validation, auto-save and preservation of the last valid value; and
- offline country-first/city-scoped search and alias matching.

## Redacted Integration Fixtures

Provider fixtures are separate and provider-identified:

- Open-Meteo fixtures prove the `/v1/forecast` request has no credential,
  maps returned city timezone/current/hourly/daily values, fills 10 daily
  positions and follows Free API attribution/terms boundaries.
- OpenWeather fixtures prove the `/data/3.0/onecall` request uses an
  unmistakably synthetic `appid` only after explicit selection, maps
  timezone/current/48-hour-hourly/8-day-daily values and projects 8+2 daily
  positions. Captured URLs/results are redacted before evidence is written.
- A call-counting two-adapter harness proves launch, city/provider change,
  cadence and each selected-provider failure invoke only the selected adapter.
- Provider/location cache and history fixtures prove mismatched records never
  display, complete a forecast or enter a pressure comparison.
- One-missing-slot examples cover each of the eight fixed city-local hourly
  positions for both providers; incomplete provider-supported daily examples
  cover fewer than 10 Open-Meteo or fewer than 8 OpenWeather records.

Fixtures also cover successful current/daily/hourly data, stale cache,
provider/network/access failure and missing optional fields. Use synthetic
OpenWeather credentials only. Evidence must show the result/verdict, never a
key or an unredacted request.

Persistence/recovery probes define before execution:

1. known initial state;
2. safe rerun/reset or isolation;
3. observable expected state; and
4. cleanup that cannot leak a secret or affect another run.

## Target-Device Evidence

This route is intentionally deferred until the application is ready for
runtime/readiness validation. It is not an automatic prerequisite for
`TASK-002-T3-FT-000-W1`, and a Foundation host verification must never start an
emulator or physical device merely to fill this section.

Manual device evidence remains the correct proof route for outcomes that
host-side checks do not reliably establish, but it is deferred, non-blocking
evidence for the current T3 product queue while an authorized target is
unavailable. Build, unit/host, static, redacted-fixture and boundary checks
remain mandatory task gates. A task must not claim runtime `PASS` without an
actual device/emulator observation; instead record `DEFERRED`, the unavailable
target condition and the residual risk. The deferred evidence is a later
runtime/readiness or release follow-up and must not by itself keep a product
task `blocked`.

### Supplementary Local Emulator Target

The authorized local readiness route may use Android Studio or the direct SDK
tools under `/home/serg/Android/Sdk`. Its supplementary target is AVD
`Tecno_Pova_6_API_35`: hardware profile `TECNO POVA 6`, Android 15/API 35
Google APIs x86_64, configured at 1080×2436 and 393 dpi. The profile metadata
does not emulate TECNO firmware: its runtime system image identifies as the
generic Google `sdk_gphone64_x86_64` model/product and `emu64xa` device.

This AVD may establish build/install/start readiness and observed generic
Android emulator behavior such as landscape rotation, fullscreen requests,
keep-screen-on flags and absence of a launch crash. It is not the release
target. The canonical release target remains the physical Samsung GT-I9300I
(`s3ve3gds`) on a compatible Android 11 custom ROM at 1280×720 landscape.
Emulator evidence must not promote a Samsung, custom-ROM or physical-device
`PASS`; target geometry/readability, system-bar and keep-screen-on behavior,
temporary interruption/process rehydration, and silent/DND/audio route, ramp
and cap behavior remain residual target-device risks. The observed local run is
recorded in [Tecno Pova 6 API 35 emulator evidence](../../.protocols/RUNTIME-VERIFICATION/tecno-pova-6-api35.md).

The deferred device evidence covers:

- 1280×720 landscape fullscreen, hidden system panels, keep-screen-on and
  clock readability;
- temporary Activity/foreground/screen-off/process interruption and timer
  rehydration on the target custom ROM;
- overdue visual state, permitted/suppressed audio behavior, ramp and the
  30-minute audio cap; and
- static pseudo-glass/readability at accepted glass-intensity values.

Reboot recovery is not a probe target. A platform limitation is recorded as
runtime evidence and does not expand V1 scope.

When a target is available, use the route below with known initial state,
safe rerun/isolation, observable result and cleanup. When it is unavailable,
record the same evidence item as `DEFERRED` and retain the risk; never replace
it with a host-side runtime claim.

## Secret and Artifact Checks

After the Foundation build path exists, inspect source literals, persisted
non-secret state, packaged resources, logs, screenshots and produced evidence
using a synthetic/redacted fixture workflow. A check passes only when the real
user key was never introduced. Prove separately that Open-Meteo constructs no
credential and that selected OpenWeather constructs the mandatory HTTPS
`appid` query transiently, with the captured URL redacted before it becomes
evidence.

## Evidence Ownership

Feature/task records own executable commands and verdicts. This spec owns the
minimum proof shape and risk routing; it does not store run logs, screenshots
or task lifecycle state.
