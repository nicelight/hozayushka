---
description: Honest pre-implementation RED evidence for TASK-010-T3-FT-008-W9 attempt 1.
status: active
---
# FT-008 baseline RED — attempt 1

## Basis

- Captured: `2026-08-08T08:08:54+05:00`.
- Repository basis: `HEAD=a93e46118f0f0b90e311b6174e3f5a8ed7d89fef` with the broad
  pre-existing tracked/untracked worktree changes listed in the execution
  context; no FT-008 production behavior was changed before this probe.
- Task status was durably `in_progress` before the probe.
- Probe type: claim-specific source-surface absence check. It is supporting
  execution evidence, not a final verdict.

## Exact command

The source probe inspected the current production surface with these
claim-specific marker pairs:

```text
AC-001 | apiKey | local personal key Settings path
AC-002 | defaultLocation | Khujand default/selected coordinate owner
AC-003 | LocationCatalog | offline country-first/city-scoped catalog
AC-004 | Russian | discarded broad match; claim-specific alias markers remained absent
AC-005 | GeoNames attribution | GeoNames attribution in Settings
AC-006 | API-ключ не указан | inline key/provider/city failure preservation
```

For each pair, `rg -n -i --glob '*.kt' --glob '*.xml' --glob '*.tsv'
<marker> app/src/main` was run and a no-match was required for the baseline
gap. The task-owned test/evidence surface was also checked with
`rg --files app/src/test .tasks/TASK-010-T3-FT-008-W9`.

## Result

- AC-001 RED — no local personal-key Settings path.
- AC-002 RED — no task-owned Khujand default/selected-coordinate owner path.
- AC-003 RED — no offline country-first/city-scoped catalog implementation.
- AC-004 RED — no task-owned canonical/Russian/ASCII alias model or query path.
  The initial broad `Russian` token matched unrelated date-month formatting and
  was not used as evidence.
- AC-005 RED — no GeoNames attribution in the Settings surface.
- AC-006 RED — no accepted key/provider/city inline failure preservation path.
- T3 isolation/proof RED — no task-owned FT-008 host probe or redacted
  Settings/catalog/provider artifact existed before this attempt.

The probe is an honest baseline gap observation. Existing predecessor
behavior, including foundation redacted fixture and timer/weather tests, is
preserved as supporting prerequisite behavior and is not claimed for FT-008.
