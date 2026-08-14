---
description: Execution plan for TASK-037-T3-FT-001-W34.
status: active
---
# Plan — TASK-037-T3-FT-001-W34

## Goal

Correct the existing Main Display real-View allocation so all four ordered
weather cards, including empty Yesterday, share the compact 25–30% bottom band,
equal height and common bottom alignment while preserving clock, city/date,
timer and provider/data ownership.

## Non-goals

- No WeatherCapability/provider/data synthesis or resources.
- No Timer & Alert, runtime/fullscreen, Settings, Forecast or composition-root changes.
- No emulator/AVD/QEMU, network/provider calls, credentials or other serial.
- No edits to W31 `done`, W32 `failed`, W33 `blocked`, scheduler checkpoint or terminal state.

## Inputs / source specs

- Task record: `.memory-bank/tasks/TASK-037-T3-FT-001-W34.task.json`
- Feature/AC: `FT-001-AC-002`; REQs `REQ-001`, `REQ-002`, `REQ-005`, `REQ-023`
- Direct specs: Main Display Presentation, Weather Card Presentation, Boundary Map, Capability Interfaces, Platform Runtime, Runtime Verification.

## Scope

### In scope

- `DisplayCapability.kt`: one shared allocation path/parameters for the existing Yesterday and populated cards.
- `DisplayProjectionTest.kt`: deterministic mixed empty-Yesterday/three-populated regression and allocation-level geometry evidence.

### Out of scope

- All paths outside the exact two-file code/test boundary, except `/exe` protocol/task evidence bookkeeping.

## Preflight-confirmed change surface

- Expected hints kept: `DisplayCapability.kt`, `DisplayProjectionTest.kt`.
- Additional same-outcome files/areas: none planned.
- Hard `write_boundary`: present and satisfied.
- `forbidden_scope` / stop-condition check: clear.

## Applicable quality gates

- [ ] `./gradlew clean assembleDebug` — clean Android debug build.
- [ ] `./gradlew :app:testDebugUnitTest --tests com.hozayushka.app.DisplayProjectionTest` — focused display regression.
- [ ] `./gradlew testDebugUnitTest` — full host unit suite.
- [ ] `./gradlew lintDebug` — Android debug lint.
- [ ] `git diff --check` — static diff integrity.
- [ ] Authorized physical RED/GREEN on TECNO serial `1156725456009666` — actual View geometry and preserved composition.

## Claim-linked RED / GREEN (T2/T3)

- applicability: applicable
- accepted claim locators: `FT-001-AC-002 / REQ-002 / REQ-005 / REQ-023`
- planned probe/environment: fresh installed-app screenshot, orientation/size, measured four-card bounds in mixed state on unlocked TECNO; deterministic host mixed fixture at `2460×1080` and `1280×720`.
- observable RED: current installed app and current host allocation show separated/taller empty Yesterday relative to the populated-card band.
- corresponding GREEN: rebuilt app and corrected host allocation show equal heights/common bottom, 25–30% band, 70–75% clock zone, fixed order and no clipping/overlap.
- accepted read-only alternatives: provider/weather and timer/runtime ownership proven by boundary/static and host regression evidence; no writes to those owners.
- T3 isolation: only serial `1156725456009666`; disposable redacted host artifacts; no emulator, network, credentials or provider calls.

## MB-SYNC handoff / owner

- Owner identified: human / lifecycle owner after `/verify` and `/red-verify`.
- Task lifecycle/status decision: not owned by `/exe` for T3; current task remains open after handoff.
- Memory Bank durable docs: no task-owned WHY/WHERE change required; task card and protocol point to existing canonical specs.

## Definition of done

Fresh host and same-device physical RED/GREEN plus all required gates are
recorded; final functional/semantic lifecycle decisions remain with the
required downstream owners.
