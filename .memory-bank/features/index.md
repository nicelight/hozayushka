---
description: Router for the L3 product features derived from the clarified PRD.
status: active
last_updated: 2026-08-10
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
- [FT-004 — Ten-day forecast view](FT-004-ten-day-forecast.md)

## EP-003 — Timers and alert

- [FT-005 — Preset timer configuration](FT-005-timer-presets.md)
- [FT-006 — Countdown lifecycle and cancellation](FT-006-countdown-lifecycle.md)
- [FT-007 — Overdue state and alert](FT-007-overdue-alert.md)

## EP-004 — Settings, location and personalization

- [FT-008 — Weather access and offline location settings](FT-008-weather-location-settings.md)
- [FT-009 — Alert and glass personalization](FT-009-personalization-settings.md)

All product feature SDD design statuses are `complete`; the global backbone is
complete at Planning Revision `1`, and the required Foundation Gate is closed.
FT-001 has the indexed historical completed task `TASK-003-T3-FT-001-W2`, the
failed W11 follow-up `TASK-014-T3-FT-001-W11`, the completed W12 repair
`TASK-015-T3-FT-001-W12` and the completed W13 ticker-debt boundary
`TASK-016-T3-FT-001-W13` and the completed W14 Weather Context projection/decode
follow-up `TASK-017-T3-FT-001-W14`; FT-002 has the indexed completed tasks
`TASK-004-T3-FT-002-W3` and `TASK-018-T3-FT-002-W15`, plus the cross-feature
W14 Weather Context evidence; W15 is host/build/static/redacted proof only and
does not change FT-002's implemented lifecycle or direct RTM ownership;
FT-003 has the indexed W4
records `TASK-005-T3-FT-003-W4` (`failed`, historical) and
`TASK-012-T3-FT-003-W4` (`done`, provider-normalization repair) and
`TASK-013-T3-FT-003-W5` (`done`, entry/fallback/shared-session follow-up);
FT-004 has the indexed completed task `TASK-006-T3-FT-004-W5` (`done`); FT-005
has the indexed completed task `TASK-007-T3-FT-005-W6` (`done`); FT-006 has
the indexed completed task `TASK-008-T3-FT-006-W7` (`done`) and feature
lifecycle `implemented`; FT-007 has the indexed completed task
`TASK-009-T3-FT-007-W8` (`done`) and feature lifecycle `implemented`; FT-008 has
the indexed completed task `TASK-010-T3-FT-008-W9` (`done`); FT-009 has the
indexed completed task `TASK-011-T3-FT-009-W10` (`done`). FT-003, FT-004, FT-005
and FT-008–FT-009 feature lifecycles remain `planned`; W10 evidence does not
imply feature closure, scheduler promotion or a dependent-state transition.
The W11 semantic-fail evidence is cross-linked from FT-006 as a regression
against its existing cancellation contract; W12 is cross-linked as the
completed FT-001 repair, while FT-006's lifecycle and direct RTM values remain
unchanged. Samsung/custom-ROM/1280x720 physical evidence remains
`DEFERRED`/non-blocking with no runtime `PASS` claim. W13 host/static evidence
remains linked from FT-001; Samsung/custom-ROM/1280x720 physical evidence
remains `DEFERRED` with no runtime `PASS` claim. W14 remains host/static proof
only, and W15 target-device/live-provider evidence remains `DEFERRED`, with no
runtime `PASS` claim. Neither boundary changes FT-001/FT-002 lifecycle or direct
RTM ownership.
Target-only FT-009 Settings readability/static pseudo-glass evidence remains
`DEFERRED`/non-blocking, with no runtime `PASS` claim.
