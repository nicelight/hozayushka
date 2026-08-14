---
description: Fresh independent Revision-2 rerun of the FT-008 task-plan review after the deterministic direct-provider-path repair.
status: final
task_id: TASK-MB-REVIEW-TASKS-PLAN
stage_id: S-TASKS-FT-008
feature: FT-008
review_mode: fresh_independent_local
---
# Review report: FT-008 Weather access and offline location settings

REVIEWED_PLANNING_REVISION: 2

## Final verdict

FINAL_VERDICT: APPROVE

The added bare `.memory-bank/contracts/weather-provider.md` input is directly
applicable to W16, and its retained exact selection, credential and attribution
blocks remain sufficient for the Settings-owned outcome. No blocker was found.

## Review boundary

- Read: governing Reviewer/review/schema/tier/acceptance-closure contracts;
  Revision-2 Backbone/index and Foundation; product, requirements/RTM, EP-004,
  FT-008, `IMPL-FT-008`, feature plan and decision log; prior current APPROVE;
  W9/W16 and W15/W17 dependency context; and all direct settings, provider,
  catalog/data, secret, platform and verification specs.
- Fresh delegation was unavailable, so the installed bounded
  `/architecture-review FT-008` contract was performed locally.
- A bounded read-only probe, not doctor, confirmed target-card schema validity,
  one resolving index entry, exact locator resolution, an acyclic dependency
  chain and one bare Weather Provider path. No Gradle, live provider,
  credential, emulator/device, lint, doctor, lifecycle, queue or scheduler
  action was run.

## Four coverage groups

| Coverage group | Result | Evidence |
|---|---|---|
| Structural integrity | Closed | `TASK-019-T3-FT-008-W16` remains the unique schema-valid `T3 / FT-008 / W16` card, legally `planned` behind terminal W15. W9 remains `done`; the chain resolves to the closed Foundation gate without a cycle or promotion. |
| Coverage and slicing | Closed | Historical W9 retains AC-002–AC-005 location/catalog behavior; W16 solely owns current AC-001/AC-006–AC-008 provider/key/failure/attribution behavior. W16 adopts neither W17 transport/cache nor FT-009 personalization scope. |
| Design and architecture readiness | Closed | The bare Weather Provider subject applies because W16 implements its persisted selection/default, key applicability, failure identity and attribution boundary. Exact selection, credential and attribution fragments plus Local Secret Handling and Capability Interfaces keep Settings and Weather Context ownership distinct. |
| Execution readiness | Closed | T3 remains required by local secret persistence and artifact-safety proof. The card keeps the same purpose/outcome, scope controls, exact AC/REQ map, synthetic-marker scans, disposable Settings state and minimal first-run/switch/reopen/failure/attribution RED/GREEN. `planned` remains legal. |

## Repair delta and proof audit

- The root Weather Provider path is semantically applicable to W16's complete
  provider-access Settings outcome; it is not an unrelated hub link.
- The retained `provider-selection-and-dispatch`,
  `credential-and-evidence-rules` and `attribution-and-terms-boundary` fragments
  all resolve and provide the exact default/selection, local-key/redaction and
  ordered attribution rules. The root path does not transfer W17 dispatch,
  adapter or cache proof to W16.
- Comparison with the prior current APPROVE report and accepted plans found the
  same task ID/title, T3/FT/W identity, `planned` status, W15 dependency, REQs,
  AC locators, purpose/outcome, advisory surface, forbidden/stop scope,
  constraints, invariants, verification targets and claim-linked RED/GREEN.
- Revision-2 cards are untracked relative to `HEAD`, so Git has no prior card
  blob for a byte-level repair diff. The operator-supplied one-item delta was
  checked against the prior report's field assertions and the full current
  card; no competing semantic change was found. The Backbone's bounded repair
  handoff is satisfied by the current card and does not reopen design.

## Bounded architecture-review — local route

verdict: APPROVE

findings: none

evidence_checked: Product and EP-004 C4 context; System Architecture AD-006 and
AD-008; Boundary Map; Capability Interfaces weather-access, Settings and
refresh contracts; complete Weather Provider contract; Local Secret Handling;
Local Data; Platform Runtime; Runtime Verification; FT-008, `IMPL-FT-008`,
W9/W16 and W15/W17 ownership and dependency context.

risks_or_questions: none. Settings owns selection/key persistence and may only
request the registered refresh command; Weather Context owns selected-provider
dispatch. The root path creates no security owner, adapter edge, storage bypass,
module, dependency or ADR.

## Blocking findings

None. Repair owner: none.

## Handoff

FT-008 contributes a current Revision-2 `APPROVE` to `/mb-doctor --strict`.
W16 remains `planned` for scheduler-owned promotion.
