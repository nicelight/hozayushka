---
description: Router for the L3 product features derived from the clarified PRD.
status: active
last_updated: 2026-08-14
---
# Features Index

## Reserved foundation pseudo-feature

- [FT-000 — Foundation executable baseline](FT-000-foundation.md): workflow
  pseudo-feature; it is not a product feature and owns no product behavior.

## EP-001 — Glanceable main display

- [FT-001 — Main clock and display shell](FT-001-main-clock-display.md)

## EP-002 — Weather context and forecasts

- [FT-002 — Main weather cards and local context](FT-002-weather-cards-context.md)
- [FT-003 — Hourly forecast view](FT-003-hourly-forecast.md)
- [FT-004 — Ten-position long-term forecast view](FT-004-ten-day-forecast.md)

## EP-003 — Timers and alert

- [FT-005 — Preset timer configuration](FT-005-timer-presets.md)
- [FT-006 — Countdown lifecycle and cancellation](FT-006-countdown-lifecycle.md)
- [FT-007 — Overdue state and alert](FT-007-overdue-alert.md)

## EP-004 — Settings, location and personalization

- [FT-008 — Weather access and offline location settings](FT-008-weather-location-settings.md)
- [FT-009 — Alert and glass personalization](FT-009-personalization-settings.md)

The Global Backbone is `complete` at Planning Revision `2`, Foundation
revalidation is successful and the required Gate anchors remain closed. All
nine feature design statuses are now `complete`. `/feature-to-tasks --all`
reconciled every stale Revision-1 plan without changing existing terminal task
state, and the pre-repair Revision-2 task-plan reviews are `APPROVE`; the
affected downstream recovery requires fresh review. The provider
migration now has completed `TASK-019-T3-FT-008-W16`, failed
`TASK-020-T3-FT-002-W17` after `3/3` unsuccessful attempts, completed repair
`TASK-023-T3-FT-002-W20` and completed `TASK-021-T2-FT-003-W18`.
`TASK-022-T2-FT-004-W19` is now `done` after fresh functional `PASS` and the
feature-level FT-004 `semantic-pass`; its earlier transitive block remains
historical task-card evidence. Scheduler post-sync gates remain separate.

Historical brownfield evidence remains indexed: FT-001 has the completed task `TASK-003-T3-FT-001-W2`, the
failed W11 follow-up `TASK-014-T3-FT-001-W11`, the completed W12 repair
`TASK-015-T3-FT-001-W12` and the completed W13 ticker-debt boundary
`TASK-016-T3-FT-001-W13` and the completed W14 Weather Context projection/decode
follow-up `TASK-017-T3-FT-001-W14`, the terminal failed W29 provenance attempt
`TASK-032-T3-FT-001-W29`, the completed W30 replacement
`TASK-033-T3-FT-001-W30` and the completed physical W31 follow-up
`TASK-034-T3-FT-001-W31`, failed W32 `TASK-035-T3-FT-001-W32`, blocked W33
`TASK-036-T3-FT-001-W33` and completed recovery W34
`TASK-037-T3-FT-001-W34`; FT-002 has the indexed completed tasks
`TASK-004-T3-FT-002-W3` and `TASK-018-T3-FT-002-W15`, plus the cross-feature
W14 Weather Context evidence; W15 is host/build/static/redacted proof only and
at that historical boundary did not change FT-002's then-`implemented`
lifecycle or direct RTM ownership;
FT-003 has the indexed W4
records `TASK-005-T3-FT-003-W4` (`failed`, historical) and
`TASK-012-T3-FT-003-W4` (`done`, provider-normalization repair),
`TASK-013-T3-FT-003-W5` (`done`, entry/fallback/shared-session follow-up) and
`TASK-021-T2-FT-003-W18` (`done`, selected-provider completeness delta);
FT-004 has the indexed completed tasks `TASK-006-T3-FT-004-W5` (`done`) and
`TASK-022-T2-FT-004-W19` (`done`); FT-005
has the indexed completed task `TASK-007-T3-FT-005-W6` (`done`); FT-006 has
the indexed completed task `TASK-008-T3-FT-006-W7` (`done`) and feature
lifecycle `implemented`; FT-007 has the indexed completed task
`TASK-009-T3-FT-007-W8` (`done`), the completed audio follow-up
`TASK-026-T3-FT-007-W23` (`done`) and the completed visual follow-up
`TASK-031-T3-FT-007-W28`; feature lifecycle remains `implemented`; FT-008 has
the indexed completed task `TASK-010-T3-FT-008-W9` (`done`); FT-009 has the
indexed completed task `TASK-011-T3-FT-009-W10` (`done`). Current FT-002,
FT-003, FT-005 and FT-009 feature lifecycles remain `planned`; FT-004 is
`implemented` after its completed W19 capability delta and feature-level
semantic pass; FT-008
is `implemented` after its completed W9 and W16 acceptance ownership;
W10 evidence does not
imply feature closure, scheduler promotion or a dependent-state transition.
The W11 semantic-fail evidence is cross-linked from FT-006 as a regression
against its existing cancellation contract; W12 is cross-linked as the
completed FT-001 repair, while FT-006's lifecycle and direct RTM values remain
unchanged. Samsung/custom-ROM/1280x720 physical evidence remains
`DEFERRED`/non-blocking with no runtime `PASS` claim. W13 host/static evidence
remains linked from FT-001; Samsung/custom-ROM/1280x720 physical evidence
remains `DEFERRED` with no runtime `PASS` claim. W14 remains host/static proof
only, and W15 target-device/live-provider evidence remains `DEFERRED`, with no
runtime `PASS` claim. At their historical boundaries neither W14 nor W15
changed FT-001/FT-002 lifecycle or direct RTM ownership; the current
provider-migration decomposition separately sets FT-002 lifecycle to `planned`
without changing any task state.
W34's equal/common-bottom mixed-state proof is linked from FT-001 and does not
change the `implemented` FT-001 lifecycle; its oversized timer-digit residual
remains separate FT-007 presentation scope.
Target-only FT-009 Settings readability/static pseudo-glass evidence remains
`DEFERRED`/non-blocking, with no runtime `PASS` claim.
W16 final Attempt-3 evidence is linked from FT-008 and FT-002. The two earlier
unsuccessful attempts remain traceable, provider-unidentified legacy key access
was replaced by W17's implemented exact-two-provider migration. W17 final
functional evidence preserves Yandex removal, selected-only ordinary dispatch,
provider/location isolation and redaction separately from the required
semantic failure: valid key save after first-time OpenWeather selection causes
no refresh and leaves the obsolete missing-key error current. The completed
W20 repair owns only that activation delta; W18 owns FT-003 AC-001/AC-005 with
fresh claim-linked host evidence, and W19 owns FT-004 AC-001/AC-002/AC-005/AC-006
with fresh claim-linked host evidence plus the feature semantic report. FT-002
and FT-003 remain `planned`; FT-004 is `implemented`. Physical-device/live-
provider evidence remains `DEFERRED` without runtime `PASS`. Scheduler
post-sync gates and terminal handling remain external; this router performs no
promotion or unblock.
