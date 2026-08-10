---
description: Historical scheduler-recorded task-local defect preventing TASK-014 closure.
status: active
last_updated: 2026-08-09
source_of_truth: .tasks/TASK-014-T3-FT-001-W11/TASK-014-T3-FT-001-W11-S-RED-VERIFY-final-report-docs-01.md
---
# TASK-014 non-city countdown cancellation defect

## Observed failure

The required independent semantic probe performed one public double tap on a
visible non-city weather-card surface while the countdown was active. After a
120 ms interval and approximately 350 ms observation, the public hierarchy
still showed the active countdown. The accepted FT-006 double-tap cancellation
contract was therefore not preserved by the final TASK-014 attempt.

The selected-city double-tap cancellation and delayed Settings protection
passed. Source review found no Settings/private-state/boundary drift, so this
is a task-local behavior failure rather than an operator decision.

Evidence:

- `.protocols/TASK-014-T3-FT-001-W11/red-verification.md`
- `.tasks/TASK-014-T3-FT-001-W11/TASK-014-T3-FT-001-W11-S-RED-VERIFY-final-report-docs-01.md`
- `.tasks/TASK-014-T3-FT-001-W11/attempt-3-noncity-double-idle.png`

## Required recovery

The required recovery route was completed by the normal indexed FT-001 repair
task [`TASK-015-T3-FT-001-W12`](../tasks/TASK-015-T3-FT-001-W12.task.json),
which preserved city hold and delayed-navigation behavior, restored non-city
active-countdown double-tap cancellation and obtained fresh `/exe`, `/verify`
and `/red-verify` evidence. Do not claim Samsung, custom-ROM, 1280x720 or
physical-device PASS from the generic emulator.

Scheduler disposition: TASK-014 failed after the configured initial attempt
plus two retries; no fourth retry is permitted in the current run.

## Repair planning

The indexed FT-001 planning reconciliation created
[`TASK-015-T3-FT-001-W12`](../tasks/TASK-015-T3-FT-001-W12.task.json) with the
last successful W10 task as its dependency. W12 is now `done` after fresh
functional `PASS` and semantic `semantic-pass`. It owns the public Main
Display dispatch integration only; it did not reopen W11, alter REQ-013
semantics, move Timer & Alert ownership or claim host-only Android touch
proof. See the [functional verification](../../.protocols/TASK-015-T3-FT-001-W12/verification.md),
[semantic verification](../../.protocols/TASK-015-T3-FT-001-W12/red-verification.md),
and [verifier-owned evidence](../../.tasks/TASK-015-T3-FT-001-W12/verifier-owned-evidence-attempt-1.md).

The generic Android public matrix is execution-local; Samsung,
custom-ROM and 1280x720 physical-device evidence remains `DEFERRED` with no
target runtime `PASS` claim.
