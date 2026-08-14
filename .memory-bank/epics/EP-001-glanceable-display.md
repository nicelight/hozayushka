---
description: L2 epic for the always-visible kitchen clock and main display interaction surface.
status: draft
id: EP-001
lifecycle: implemented
last_updated: 2026-08-14
---
# EP-001 — Glanceable main display

## Value

Владелец получает спокойный fullscreen display, где время читается издалека и
остаётся главным элементом, а дата, город и точки входа в таймеры/settings
доступны без навигационного шума.

## Success metrics

- `HH:mm` визуально доминирует и читается на целевом 1280×720 landscape display.
- Основной экран сохраняет согласованную композицию при наличии и отсутствии
  weather data.
- Запуск Settings из city interaction не нарушает основной display flow.

## Acceptance criteria

- Landscape fullscreen, скрытые системные панели и keep-screen-on соблюдаются.
- Часы, дата, город, weather-card area и preset-button area находятся в
  принятой композиции; colon имеет online/offline/timer состояния.
- Device timezone используется для больших часов и основной даты, а выбранный
  city/API timezone не меняет их отображение.
- No-city and city-hold behavior открывают Settings согласно PRD, а selected-city
  short tap остаётся no-op.

## Features

- [FT-001 — Main clock and display shell](../features/FT-001-main-clock-display.md)

## Sources and constraints

- [.memory-bank/prd.md](../prd.md): `PRD-FR-001`–`PRD-FR-006`, `PRD-FR-039`,
  `PRD-NFR-001`–`PRD-NFR-003`, `PRD-AC-001`, `PRD-AC-009`.
- [.memory-bank/invariants.md](../invariants.md): clock dominance and accepted
  local-only product boundaries.
- [.memory-bank/user-scenarios.md](../user-scenarios.md): core glance scenario.

## Design status

Epic remains `draft`; its global architecture and platform boundary are
registered, and its FT-001 implementation history includes W2, the failed W11
follow-up, the completed W12 repair, the completed W13 ticker-debt boundary,
the completed W14 Weather Context projection/decode boundary, the failed W29
provenance attempt and the completed W30 replacement; W31 is the completed
physical clock/icon-geometry follow-up. W30 records fresh
verifier-owned host proof for the Main Display clock, four ordered weather
shells and preset visual treatment after an accepted `RED_NOT_APPLICABLE`
route; the exact two-file boundary and existing ownership edges remain
unchanged. W31 records fresh physical TECNO visual PASS at `2460×1080`, with
host geometry at `2460×1080` and `1280×720` kept as supporting evidence only.
W32 remains scheduler-`failed` after physical mixed-state smoke exposed the
real View-tree allocation defect, and W33 remains `blocked` on W32; its
superseded `blocked -> failed` transition is preserved as policy-invalid
history. W34 is the completed recovery successor from W31, with fresh host
and physical RED/GREEN proving the equal/common-bottom mixed-state allocation
inside the existing Main Display two-file boundary. W29 remains terminal
`failed` for missing pre-write provenance, not
for an evidenced product semantic failure. Detailed evidence is linked from
[FT-001](../features/FT-001-main-clock-display.md), the [W29 semantic
report](../../.tasks/TASK-032-T3-FT-001-W29/TASK-032-T3-FT-001-W29-S-RED-VERIFY-final-report-docs-01.md),
the [W30 sync report](../../.tasks/TASK-033-T3-FT-001-W30/TASK-033-T3-FT-001-W30-S-MB-SYNC-final-report-docs-01.md),
the [W31 sync report](../../.tasks/TASK-034-T3-FT-001-W31/TASK-034-T3-FT-001-W31-S-MB-SYNC-final-report-docs-01.md)
and the [W34 sync report](../../.tasks/TASK-037-T3-FT-001-W34/TASK-037-T3-FT-001-W34-S-MB-SYNC-final-report-docs-01.md).
Its task-owned verification reports provide the recovery evidence.
Target-only runtime observations remain `DEFERRED` under the accepted policy,
including other resolutions/devices, custom-ROM rendering, physical audio
audibility and live provider refresh, with no claim beyond the accepted W34
TECNO visual PASS for this boundary. The separate oversized timer-digit
observation remains an FT-007 residual and does not alter EP-001. EP-001
lifecycle remains `implemented`; no epic closure, promotion or
dependent-state transition is inferred by this boundary sync.
