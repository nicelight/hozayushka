---
description: Fresh independent review request for the FT-002 W15 production Yandex adapter planning surface.
status: active
task_id: TASK-MB-REVIEW-TASKS-PLAN
stage_id: S-TASKS-FT-002
feature: FT-002
reviewed_task: TASK-018-T3-FT-002-W15
---
# Review Request: FT-002 W15

Review as `ROLE: Reviewer` whether the current FT-002 planning surface is safe
to hand to execution at Global Backbone `Planning Revision: 1`, with
`TASK-018-T3-FT-002-W15` as the current planned production-provider follow-up.

Check the canonical task handoff, direct SDD routes, provider/Yandex request
contract, Weather Context and composition-root ownership, dependency/tier/wave
identity, hard write boundary, claim-linked RED/GREEN/evidence sufficiency,
secret/redaction rules, and the standing host/static non-emulator constraint.
Treat FT-003, FT-004 and FT-008 only as compatible boundary consumers/inputs;
do not reopen their historical records or transfer their acceptance ownership.

This review is read-only with respect to production code, task lifecycle,
RTM/feature lifecycle, scheduler checkpoint and terminal state. Do not run an
Android emulator, ADB, Gradle device task, live request, or target-device
process. Only this request and the feature report are review outputs.
