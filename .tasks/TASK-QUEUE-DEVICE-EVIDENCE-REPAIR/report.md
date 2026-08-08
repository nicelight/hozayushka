# T3 queue device-evidence repair

## Scope

Policy-only repair authorized by the operator. Production code, task lifecycle
status, `.protocols/AUTONOMOUS-RUN/status.md`, scheduler checkpoint and terminal
state were not changed. No device evidence was fabricated.

## Changed files

- `.memory-bank/testing/runtime-verification.md`
- `.memory-bank/requirements.md`
- `.memory-bank/contracts/platform-runtime.md`
- `.memory-bank/contracts/capability-interfaces.md`
- `.memory-bank/epics/EP-003-timers-alert.md`
- `.memory-bank/tasks/TASK-003-T3-FT-001-W2.task.json` through
  `TASK-011-T3-FT-009-W10.task.json`
- `.tasks/TASK-QUEUE-DEVICE-EVIDENCE-REPAIR/report.md`
- `PAPERCUTS/GPT-5 __ 08-07-2026 23.27.md`

## Gates made deferred/non-blocking

- Android emulator/physical-device availability and ADB target probing.
- 1280×720 landscape fullscreen, hidden panels, keep-screen-on and target
  readability observations.
- Target interaction/navigation and temporary lifecycle/screen-off behavior.
- Target-ROM overdue visual/audio ramp, silent/DND and audio-route behavior.
- Target-only static pseudo-glass/readability observations.

Unavailable target evidence must be recorded as `DEFERRED` with residual risk;
runtime `PASS` is forbidden without an actual target observation. These checks
remain later readiness/release follow-up evidence.

## Mandatory host gates retained

- `./gradlew clean assembleDebug`
- `./gradlew testDebugUnitTest`
- Task-specific deterministic host/unit/static/boundary checks.
- Redacted provider/secret/artifact checks and safe isolated fixture evidence.

## Residual risks

- Custom-ROM fullscreen, readability, lifecycle interruption/rehydration and
  audio-policy compatibility remain unobserved.
- Target-only interaction and static-material behavior remain unconfirmed.
- No claim of runtime PASS is made by this repair.

## Governing contract note

Constitution IV still requires physical-device evidence when a result cannot be
reliably proven host-side. The operator-authorized repair narrows that rule only
for the current unavailable-target T3 queue: the evidence remains required as a
later readiness/release follow-up, but is not a queue-blocking gate. The
Constitution was not edited.

## Task statuses needing scheduler recovery

`TASK-003` through `TASK-011` remain `blocked` exactly as recorded. The
existing scheduler state remains `HALT_QUALITY_GATES`, with TASK-003 at
`closure` and its direct dependents blocked. A scheduler/lifecycle owner must
reconcile and resume the queue under the repaired policy; this report does not
change statuses, checkpoint, terminal state or run `/mb-sync`.

## Validation

- `node scripts/mb-lint.mjs` — PASS; JSON/link/frontmatter checks passed.
- Local Ajv structural validation against `task.schema.json` — PASS for all
  nine cards; the schema file was not modified.
- `git diff --check` — PASS.
- `/mb-doctor` and `/mb-sync` were not run to avoid scheduler-state recovery or
  lifecycle synchronization outside the authorized scope.
