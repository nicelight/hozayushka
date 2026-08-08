---
description: Adversarial semantic verification for TASK-005-T3-FT-003-W4 after functional PASS.
status: final
---
# Red Verification — TASK-005-T3-FT-003-W4

## Semantic target

- Task outcome: the supported Yandex hourly input path must produce the complete
  accepted eight-slot Weather Context projection and hourly session without
  weakening current/daily weather behavior.
- Accepted contract and boundaries: `REQ-009`, `FT-003-AC-001` through
  `FT-003-AC-005`, PRD `PRD-FR-019A` through `PRD-FR-019C`, Weather Provider
  `FT-003 Hourly Mapping`, Forecast Data Contract, and Weather Context ownership
  of provider normalization/completeness.

## Evidence and adversarial coverage

- Functional evidence: `.protocols/TASK-005-T3-FT-003-W4/verification.md`
  records `VERDICT: PASS` on the exact-eight redacted fixture, clean build,
  `20/20` tests, boundaries and secret checks.
- Inspected current provider DTO, Weather Context normalization, full task diff
  surface, protocols/reports and direct canonical specs without trusting the
  functional verdict as semantic proof.
- Exercised the accepted full-day provider shape with a synthetic, isolated
  two-day/48-record hourly payload containing every required city-local slot;
  probe: `.tasks/TASK-005-T3-FT-003-W4/ProviderHourlyShapeProbe.java`.

## Admitted findings

- `WeatherCapability` accepts hourly data only when the raw provider list has
  exactly eight records (`hourly.size == expected.size`). The accepted
  `hours=true` provider path supplies full-day hourly values for the first
  forecast days, from which Weather Context must normalize the eight selected
  slots. The verifier probe supplied 48 deterministic records across two days,
  proved all eight accepted slots were present, and observed
  `refresh_result=NULL` plus `hourly_projection=NULL` (probe exit `1`). Thus a
  supported successful provider response cannot open the hourly forecast and
  also causes the otherwise valid structured refresh to be rejected. This is a
  material break of the unambiguous task outcome, REQ-009 and the Weather
  Context normalization boundary.

## Operator questions

- none

## Verdict

SEMANTIC_VERDICT: semantic-fail

## Owner handoff

- Evidence/report paths:
  `.tasks/TASK-005-T3-FT-003-W4/ProviderHourlyShapeProbe.java` and
  `.tasks/TASK-005-T3-FT-003-W4/TASK-005-T3-FT-003-W4-S-RED-VERIFY-final-report-docs-01.md`.
- Recommended owner action: do not close TASK-005 or promote its dependent;
  lifecycle owner routes the supported provider-shape failure through the
  existing task repair/follow-up path, then requires fresh `/verify` and
  `/red-verify` evidence.
- Resume route: active scheduler/explicit lifecycle owner; this Reviewer did
  not invoke `/exe`, `/mb-sync`, BUG creation or a lifecycle transition.
