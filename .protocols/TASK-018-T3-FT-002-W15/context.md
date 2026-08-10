---
description: Execution context for TASK-018-T3-FT-002-W15.
status: active
---
# Context — TASK-018-T3-FT-002-W15

## Purpose
Execute the accepted production Yandex provider integration delta behind the existing WeatherProvider boundary, preserving Weather Context ownership and the redacted fixture route.

## Execution Attempt
- attempt: 2
- started: 2026-08-10 15.25 Asia/Dushanbe
- basis: scheduler-authorized same-task correction from the independent semantic
  report and `.protocols/TASK-018-T3-FT-002-W15/red-verification.md`.
- correction scope: reject incomplete full-daily required condition data and
  empty/incomplete hourly data before normalization/cache replacement; add only
  deterministic host cache-preservation regressions.

## Previous Execution Attempt
- attempt: 1
- status: supporting-only after independent semantic verification requested
  correction; production behavior and accepted public boundaries remain the
  same-task basis.

## Inputs (what drives this task)
- Task record: `.memory-bank/tasks/TASK-018-T3-FT-002-W15.task.json`
- Task index: `.memory-bank/tasks/index.json`
- Specs: `.memory-bank/contracts/weather-provider.md`, `.memory-bank/contracts/local-secret-handling.md`, `.memory-bank/contracts/platform-runtime.md`, `.memory-bank/contracts/boundary-map.md`, `.memory-bank/contracts/capability-interfaces.md`, `.memory-bank/architecture/system-architecture.md`, `.memory-bank/testing/runtime-verification.md`
- Acceptance criteria source: `.memory-bank/features/FT-002-weather-cards-context.md#FT-002-AC-002`, `#FT-002-AC-004`, `#FT-002-AC-006`, `#FT-002-AC-007`

## Richer inputs
- Planning Revision: `1`; latest FT-002 task-plan review: `APPROVE`, `REVIEWED_PLANNING_REVISION: 1`.
- Dependency: `TASK-017-T3-FT-001-W14` is `done`.
- Direct task scope: transport/request shape, provider-to-existing-DTO mapping, bounded failure/cache preservation, secret redaction, fixture isolation, composition/runtime wiring and `INTERNET`.
- Hard write boundary: task `runtime_context.write_boundary`; forbidden scope and stop conditions are binding.

## Loaded context set
- `AGENTS.md`
- `.memory-bank/constitution.md`
- `.memory-bank/mbb/index.md`
- `.memory-bank/spec-backbone.md`
- `.memory-bank/spec-index.md`
- `.memory-bank/tasks/index.json`
- `.memory-bank/workflows/tier-policy.md`
- direct task-linked canonical contracts and feature/task-plan docs

## Decisions / assumptions
- Use Android/JDK `HttpURLConnection` and a small deterministic parser; no Gradle dependency or public contract change.
- Keep the synchronous `WeatherProvider` boundary for host tests; composition-root refresh calls will dispatch through an existing JDK executor off the UI thread.
- The redacted fixture provider is a separate injected route and must never call production transport.
- Synthetic credentials are permitted only in-memory in isolated probes; durable output is redacted.

## Commands run / environment notes
- Read-only preflight and source inspection completed; no emulator, ADB, Gradle device task, target-device process or live request launched.
- Worktree contains unrelated pre-existing user changes; touched W14 files will be preserved.

## Open questions / blockers
- None at preflight. Target-device evidence is explicitly deferred by operator constraint.

## Next session
- Start by reading: `context.md`, `plan.md`, `progress.md`.
- Next action: execute the recorded claim-specific RED probes, then implement the bounded adapter and wiring.
