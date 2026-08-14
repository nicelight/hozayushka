---
description: Router for the L2 product epics derived from the clarified PRD.
status: active
last_updated: 2026-08-14
---
# Epics Index

- [EP-001 — Glanceable main display](EP-001-glanceable-display.md): always-visible clock and main display surface.
- [EP-002 — Weather context and forecasts](EP-002-weather-context.md): selected-provider cards, cache/history and capability-aware forecast views.
- [EP-003 — Timers and alert](EP-003-timers-alert.md): presets, countdown lifecycle and overdue alert.
- [EP-004 — Settings, location and personalization](EP-004-settings-location.md): provider selection/key applicability, offline location and preferences.

All epics remain `draft`; the Global Backbone is `complete` at Planning
Revision `2`, Foundation revalidation is successful and the Gate anchors remain
closed. All feature design gates are `complete`; historical brownfield task
records remain traceable. Pre-repair Revision-2 task-plan reviews are `APPROVE`;
the W20 repair is now closed and downstream recovery still requires fresh
review.
The provider migration records W16 TASK-019 as `done`, W17 TASK-020 as `failed`
after `3/3` unsuccessful attempts, completed W20 repair TASK-023 and completed
W18 TASK-021 for the selected-provider hourly completeness delta. W19
TASK-022 is now `done` after fresh functional `PASS` and the FT-004
feature-level `semantic-pass`; its earlier transitive block remains historical
task-card evidence. Scheduler post-sync gates remain separate. EP-002 has
indexed FT-003 W4/W5 repair-chain evidence,
W18 claim-linked evidence and the FT-004
W5 outcome; EP-003 has indexed the completed FT-005 W6, FT-006 W7 and FT-007 W8
outcomes from `TASK-007-T3-FT-005-W6`, `TASK-008-T3-FT-006-W7` and
`TASK-009-T3-FT-007-W8`. EP-004 has indexed the completed FT-008 W9 outcome
from `TASK-010-T3-FT-008-W9` and the completed FT-009 W10 outcome from
`TASK-011-T3-FT-009-W10`. EP-001 also indexes the failed FT-001 W11
follow-up `TASK-014-T3-FT-001-W11` and completed W12 repair
`TASK-015-T3-FT-001-W12`; W12's semantic-pass closes the bounded Main Display
dispatch repair while its REQ-013 check remains a cross-feature regression
guard linked from FT-006. W13's completed host/static ticker-debt boundary and
W14's completed host/static Weather Context projection/decode boundary are
linked from FT-001/FT-002 and do not alter EP-001's `implemented` lifecycle.
W29 is terminal `failed` for missing provenance rather than a product semantic
failure, W30 is the completed fresh host-proof replacement, and W31 is the
completed physical TECNO visual follow-up at `2460×1080`; W32 remains failed
after the physical mixed-state defect, W33 remains blocked on W32 with its
superseded policy-invalid transition history, and W34 is the completed recovery
successor from W31. W30 host evidence remains distinct from W31/W34 device
evidence, while other target resolutions, custom-ROM rendering, audio and live
provider refresh remain `DEFERRED`.
EP-002 also indexes the completed FT-002 W15 production-provider boundary
`TASK-018-T3-FT-002-W15`; its compatibility checks do not claim FT-003/FT-004/
FT-008 acceptance or promote EP-002.
EP-002, EP-003 and EP-004 lifecycles remain
`planned` because no epic lifecycle decision was made by this boundary sync;
FT-005/REQ-011 and FT-009/REQ-019/020/021 remain planned. FT-008 and its direct
REQ-017/018/024/027/028 rows are `implemented`, but the FT-009 W10 outcome does
not promote EP-004. W17's implemented provider-migration facts remain distinct
from the failed first-time OpenWeather activation outcome; the completed W20
repair closes only that activation delta and W18 closes the current FT-003
hourly completeness delta, while EP-002 and its three feature lifecycles remain
`planned`. FT-004 is implemented from its W5/W19 evidence, while EP-002 and
EP-004 remain planned; W19's downstream scheduler handling remains external.
Affected FT-002/003/004 plans now route through fresh downstream review and
dependency recovery. Target-device evidence remains
deferred/non-blocking and no runtime `PASS` is claimed. EP-003's W12
cross-feature repair does not change its `planned` lifecycle; W13 target-device
evidence remains `DEFERRED`. W14 target-device evidence remains `DEFERRED`
with no runtime `PASS` claim. W15 target-device/live-provider evidence remains
`DEFERRED`, with no runtime `PASS` claim.
