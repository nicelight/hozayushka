---
description: Execution context for TASK-022-T2-FT-004-W19.
status: active
---
# Context — TASK-022-T2-FT-004-W19

## Purpose

Execute the Revision-2 FT-004 completeness delta: preserve one selected-city
ten-date long-term horizon, fill all ten positions for Open-Meteo, and expose
OpenWeather's complete eight-record capability as eight filled plus two dated
empty positions. One-short sets remain unavailable with the exact accepted
message. No provider fallback, synthesis, borrowing, or display/entry redesign.

## Execution Attempt

- attempt: 1
- started: 2026-08-12T02:58:59+05:00

## Inputs

- Task record: `.memory-bank/tasks/TASK-022-T2-FT-004-W19.task.json`
- Task index: `.memory-bank/tasks/index.json`
- Feature / acceptance: `.memory-bank/features/FT-004-ten-day-forecast.md#FT-004-AC-001`, `#FT-004-AC-002`, `#FT-004-AC-005`, `#FT-004-AC-006`
- Plan/protocol: `.memory-bank/tasks/plans/IMPL-FT-004.md`, `.protocols/FT-004/plan.md`, `.protocols/FT-004/decision-log.md`

## Richer inputs

- Normative contracts: Boundary Map dependency graph and ownership; Capability Interfaces FT-004 long-term session and Forecast Sessions → Weather Context; Weather Provider capability matrix, normalized response, mapping/timezone and failure rules; Local Data FT-004 records; Lifecycle Map; Runtime Verification host-side checks; Invariants; Tier Policy T2 and claim-linked RED/GREEN.
- Upstream boundaries: `.protocols/TASK-021-T2-FT-003-W18/{context,plan,progress,handoff,verification}.md` and `.protocols/TASK-023-T3-FT-002-W20/{context,plan,progress,handoff,verification}.md`; W18/W20 acceptance evidence is prerequisite context only, not W19 proof.
- Planning gate: Global Backbone `complete`, Planning Revision `2`; `.tasks/TASK-MB-REVIEW-TASKS-PLAN/TASK-MB-REVIEW-TASKS-PLAN-S-TASKS-FT-004-RECOVERY-final-report-docs-01.md` is `FINAL_VERDICT: APPROVE` with `REVIEWED_PLANNING_REVISION: 2`.

## Point-of-use preflight

- Exactly one indexed record resolves to this ID; ID segments, `T2`, feature and wave agree.
- Current task is `in_progress` from scheduler invocation; lifecycle/status/checkpoint are not changed by this execution.
- Dependency `TASK-021-T2-FT-003-W18` is `done`; its accepted dependency `TASK-023-T3-FT-002-W20` is `done`. Historical W19 `blocked` decision remains task-card history and is not edited.
- No non-empty `runtime_context.write_boundary`; semantic `forbidden_scope` and stop conditions remain hard. Existing dirty worktree is pre-existing upstream migration work; touched files are inspected and preserved.
- Current implementation RED is reproducible: `longTermProjection` requires ten records and all ten filled fields, so an OpenWeather eight-record selected set cannot produce the accepted 8+2 projection and no provider-specific entry matrix exists.

## Decisions / assumptions

- Use existing `WeatherProviderId.capabilities.supportedDailyRecords` as the provider source of truth; no new contract or module.
- Represent only the two expected OpenWeather tail positions as explicit nullable empty cards. Available-card fields remain unchanged; existing entry, timer and gesture flow is reused.
- OpenWeather's provider record must be exactly eight sequential records starting at selected-city today; Open-Meteo must be exactly ten. A one-short record set returns `null` and the existing exact unavailable message.

## Open questions / blockers

- None after preflight. Target-device/live-provider evidence is explicitly deferred by operator constraint and must not be reported as runtime `PASS`.

## Next session

- Start by reading `context.md`, `plan.md`, `progress.md` and `handoff.md`.
- Next action: run independent `/verify TASK-022-T2-FT-004-W19`; executor gates and GREEN are supporting evidence only.
