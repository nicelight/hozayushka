---
description: Evidence-resolvability diagnosis for the post-closure strict-doctor failure of TASK-019-T3-FT-008-W16.
status: final
task_id: TASK-019-T3-FT-008-W16
stage_id: S-DEBUG
attempt: 3
role: Reviewer
---
# Debug report — TASK-019-T3-FT-008-W16

## Symptom and reproduction

- Observed command: `node scripts/mb-doctor.mjs --strict`.
- Fresh read-only reproduction: exit `1`; `mb-lint` passed, then strict doctor emitted `TASK_ACCEPTANCE_EVIDENCE_MISSING` for the done TASK-019 card: “done task does not retain claim-linked execution and verification evidence for its planned AC path.”
- The separate `TASK_PLANNED_READY_CANDIDATE` warning for TASK-020 is unrelated to this diagnosis.
- No product test/build, emulator/AVD/QEMU, `adb`, device, network, provider, or credential operation was run.

## Current attempt and actual change surface

- Current Execution Attempt is `3`, started `2026-08-11T02:45:18+05:00`, as retained in `.protocols/TASK-019-T3-FT-008-W16/context.md:18-20`.
- The closed Attempt-3 correction surface is `app/src/main/kotlin/com/hozayushka/app/settings/SettingsCapability.kt` and `app/src/test/kotlin/com/hozayushka/app/SettingsLocationTest.kt`, plus task-owned Attempt-3 protocol/evidence/report writes. The accepted `app/src/main/res/values/strings.xml` diff predates Attempt 3 and was unchanged by it; this is recorded by the executor and verifier final reports.
- This diagnostic changed no implementation, test, spec, task JSON, protocol, scheduler, lifecycle, planning, or external state. Its only write is this report.

## Confirmed root cause and first violated invariant

The failure is a one-label parser mismatch in `.protocols/TASK-019-T3-FT-008-W16/progress.md:64`:

```text
- accepted claim locators: `FT-008-AC-001`; `FT-008-AC-006`; `FT-008-AC-007`; `FT-008-AC-008`
```

`scripts/mb-doctor/acceptance-trace.mjs:303-320` requires, for every governed AC of a done T2/T3 task:

1. a same-line concrete bullet field in `progress.md` whose label is literally `accepted claim locator(s)` and whose value contains that exact AC ID;
2. concrete `RED observation and evidence` and `GREEN observation and evidence` bullet fields in `progress.md` for the applicable route;
3. the exact AC ID and standalone `VERDICT: PASS` in `verification.md`.

The first violated invariant is item 1. `hasConcreteBulletFieldContainingId` escapes the configured label before matching, so the parentheses are literal; `accepted claim locators` is not an accepted spelling. The single failed field contains all four planned IDs, therefore the same first predicate returns false for `FT-008-AC-001`, `FT-008-AC-006`, `FT-008-AC-007`, and `FT-008-AC-008`.

The remaining evidence shape is already resolvable:

- `.protocols/TASK-019-T3-FT-008-W16/progress.md:67,69` contains the exact concrete RED and GREEN field labels;
- `.protocols/TASK-019-T3-FT-008-W16/verification.md:20-21,101-104` contains every planned AC ID and line 121 contains standalone `VERDICT: PASS`;
- the task card has a valid prospective proof contract for each of the four exact AC locators in `source_artifacts`, `evidence_required`, and `verification_targets`;
- Attempt-3 RED/GREEN substance remains retained in `progress.md:84-91` and `.tasks/TASK-019-T3-FT-008-W16/red-green-evidence.md:91-121`.

This is evidence resolvability only. No functional or semantic closure claim was reassessed.

## Experiments and comparison evidence

- Fresh strict-doctor reproduction produced exactly the reported TASK-019 error.
- A read-only process-memory probe evaluated the doctor predicates. Before substitution the required label value was absent for all four ACs, while RED, GREEN, verifier AC linkage, and verifier PASS were present. Replacing only the label in memory made the progress locator predicate true for all four ACs.
- Nearby done `TASK-018-T3-FT-002-W15` passes with the exact field `- accepted claim locator(s): ...`, concrete RED/GREEN fields, verifier AC linkage, and standalone PASS.
- Existing repair records under `.tasks/TASK-QUEUE-DEVICE-EVIDENCE-REPAIR/` document the same prior mechanical correction for TASK-003 and TASK-004: `accepted claim locators:` → `accepted claim locator(s):`, with evidence content and lifecycle left unchanged.

Rejected causal hypotheses:

- Missing verifier evidence is not causal: the four IDs and standalone PASS are present.
- Missing RED/GREEN evidence is not causal: both exact concrete fields are present, and current Attempt-3 substance is retained.
- A broken planned AC path is not causal: the card's four prospective proof mappings are valid, and strict doctor did not emit `TASK_ACCEPTANCE_PROOF_MISSING` or `TASK_ACCEPTANCE_LINK_INVALID`.

## Minimum recommended correction for the later evidence owner

Allow exactly one protocol file and one mechanical line edit:

- File: `.protocols/TASK-019-T3-FT-008-W16/progress.md`
- Line: current line 64 only.
- Replace only `accepted claim locators:` with `accepted claim locator(s):`.
- Preserve the same four complete AC IDs and all remaining line/content bytes.

No edit is needed in `verification.md`, task JSON, final reports, production/tests/specs, scheduler, lifecycle, or planning. No evidence needs to be recreated or rerun.

## Deterministic regression check

After that evidence-only edit, run from the workspace root:

```bash
node scripts/mb-doctor.mjs --strict
```

Expected result: exit `0`, no `TASK_ACCEPTANCE_EVIDENCE_MISSING`, and `MB_LINT_PASSED`; the unrelated non-blocking TASK-020 readiness warning may remain.

## Residual uncertainty and next owner

- Residual uncertainty for the diagnosed parser cause: none. The actual later edit and post-edit strict-doctor result remain unperformed by this read-only diagnosis.
- Next owner: the task-local evidence-maintenance worker authorized to edit the one protocol line above and rerun strict doctor. No implementation, fresh verification, lifecycle, scheduler, or planning owner is needed for this correction.

DIAGNOSIS: CONFIRMED
