---
description: Execution plan for TASK-015 bounded Main Display active-countdown dispatch repair.
status: active
---
# Plan — TASK-015-T3-FT-001-W12

## Goal

Make active-countdown touch delivery coherent across the existing Main Display
public surfaces while preserving city hold → Settings → Back, Timer & Alert
gesture semantics, preset behavior and overdue dismissal.

## Non-goals

- No Timer & Alert arithmetic, persistence, lifecycle/state-machine or overdue semantic change.
- No Settings & Location semantic/private-state change.
- No lifecycle/checkpoint/terminal-state mutation of historical tasks or scheduler state.
- No new module, graph edge, public contract, event/message boundary, dependency or production target-device claim.

## Inputs / source specs
- Task record: `.memory-bank/tasks/TASK-015-T3-FT-001-W12.task.json`
- Task index: `.memory-bank/tasks/index.json`
- Feature: `.memory-bank/features/FT-001-main-clock-display.md`
- REQ IDs: `REQ-004`; `REQ-013` is regression basis only

## Richer execution inputs
- Source Artifacts: task card, W11 red-verification and verifier-owned evidence, FT-001 plan/decision log
- Normative Inputs: task-card direct canonical SDD links, especially Main Display contracts, ownership/boundary map, timer lifecycle, runtime verification and T3 claim-linked RED/GREEN policy
- Verification Targets: task card `verification_targets` and `evidence_required`

## Constraints / invariants (MUST / NEVER)
- MUST capture an active-countdown stream at `ACTION_DOWN` and deliver its terminal events without live-state reclassification.
- MUST preserve selected-city short-tap no-op, city long hold → existing Settings callback, empty-city short tap → Settings, preset idle start/active single/double and overdue any-tap dismissal.
- MUST use only existing Main Display → Timer & Alert and Main Display → Settings & Location contracts.
- NEVER move timer ownership, alter timer arithmetic/persistence/lifecycle/overdue semantics, access private neighbor state, or add an event/message boundary.
- NEVER touch forbidden scheduler/history files or claim Samsung/custom-ROM/1280×720 PASS.

## Scope
### In scope

- `app/src/main/kotlin/com/hozayushka/app/display/DisplayCapability.kt`
- `app/src/test/kotlin/com/hozayushka/app/DisplayProjectionTest.kt`
- Task-owned protocol and evidence under `.protocols/TASK-015-T3-FT-001-W12/` and `.tasks/TASK-015-T3-FT-001-W12/`

### Out of scope

- All other production modules, MainActivity, task history, scheduler state, lifecycle closure and `/verify`/`/red-verify`/`/mb-sync`.

## Proposed changes
### Touched areas
- `DisplayCapability.kt` — replace the city-only active stream guard with one internal dispatcher used by root/background, dynamic weather cards, city and preset listeners; bind regenerated cards to the same path.
- `DisplayProjectionTest.kt` — focused host stream/lifecycle/surface-routing support, retaining TimerCapability single/double/overdue assertions.

### Preflight-confirmed change surface
- Expected hints kept: both advisory files above.
- Additional same-outcome files/areas and rationale: protocol/evidence only; no other production or test file is authorized by this task.
- Hard `write_boundary` present and satisfied: not set.
- `forbidden_scope` / stop-condition check: clear.

## Applicable quality gates
- [ ] `./gradlew clean assembleDebug` — clean Android debug build.
- [ ] `./gradlew testDebugUnitTest` — complete host gesture/lifecycle regression suite.
- [ ] `git diff --check` — static diff integrity.
- [ ] `/home/serg/Android/Sdk/platform-tools/adb -s emulator-5554 shell dumpsys activity top` — required runtime identity/top-activity observation after current APK public probes.

## Claim-linked RED / GREEN (T2/T3)
- applicability: applicable
- accepted claim locator(s): `FT-001-AC-005 / REQ-004`; `REQ-013` protected-cancellation is regression-only; runtime safety/cleanup is evidence-required
- planned test/probe and environment: current APK on exact `Tecno_Pova_6_API_35` generic Android 15/API35 x86_64 emulator, public UI only; host dispatcher probe for supporting stream evidence
- observable RED: current-attempt public RED was required but not reproduced on this exact run; historical W11 failure remains supporting context only. Pre-implementation GREEN was observed for non-city double and the city hold/Back route, with selected-city idle short-tap no-op.
- corresponding GREEN: public non-city single preserves countdown/hint, non-city double cancels by checkpoint, city hold opens Settings and Back returns to active countdown, city double has no delayed Settings, preset/overdue guards remain intact after the bounded dispatcher implementation.
- accepted not-applicable reason and alternative proof: no RED_NOT_APPLICABLE classification; pre-implementation GREEN is recorded for the observed already-green regression path and does not replace required post-change verification.
- T3 isolation, safe rerun, cleanup, and permission boundary: synthetic/redacted state only; no credentials/private storage; normal MainActivity focused, timer idle, Settings absent, emulator awake at handoff; generic evidence is not Samsung/custom-ROM/physical PASS

## MB-SYNC handoff / owner
Scheduler or explicit standalone lifecycle owner performs sync after independent verification and semantic verification. `/exe` records handoff only.

Checklist:
- [ ] Owner identified: scheduler/lifecycle owner after `/verify` and `/red-verify`
- [ ] `.memory-bank/` docs needing update: none beyond task-owned execution handoff; no WHY/WHERE drift
- [ ] `.memory-bank/index.md` router update needed: no
- [ ] RTM update in `.memory-bank/requirements.md` needed: no
- [ ] Task registry/status update owner: lifecycle owner; current `/exe` status is `in_progress`
- [ ] Changelog update owner: lifecycle owner if workflow boundary requires it

## Definition of done

Fresh claim-scoped RED is retained, the bounded dispatcher repair and focused
host support are implemented, required host/build/static gates pass, exact
generic-emulator public evidence is recorded honestly with deferred physical
target risk, and the handoff routes to `/verify` without closing the T3 task.
