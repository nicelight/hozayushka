---
description: Feature-level independent adversarial semantic verification report for FT-004 Revision 2.
status: final
feature: FT-004
stage_id: S-RED-VERIFY
role: Reviewer
verification_cycle: final-independent-red-cycle
---
# Red-verification report — FT-004 Revision 2

## Accepted intent and inspected basis

Проверен W19-owned outcome для `FT-004-AC-001/002/005/006`: один shared
long-term screen с десятью датами от selected-city today; Open-Meteo `10/10`,
OpenWeather `8 + 2` dated empty; provider-specific one-short rejection с
точным сообщением; без synthesis, nearest substitution, cache/history
borrowing from a foreign provider/location, cross-provider fallback или mixing.

Проверены feature/task cards, Revision-2 plans и protocols, direct canonical
specs, actual W19 change surface, executor handoff, свежий функциональный
`VERDICT: PASS` report и `verification.md`, RED baseline, completeness matrix и
gate results. Executor/functional claims использованы как context; source и
tests проверены независимо.

## Claim-by-claim findings

- **AC-001 — PASS.** `WeatherCapability.longTermProjection` применяет
  capability threshold selected provider (`10`/`8`), а
  `ForecastSessionCapability` открывает session только для non-null projection.
  Fresh matrix содержит complete и one-short cases для обеих entry cards;
  `forecastEntryIntent` и `MainActivity` сохраняют один `LONG_TERM` route для
  Tomorrow и Day-after.
- **AC-002 — PASS.** Projection строит ровно десять последовательных дат от
  `Instant` в returned API timezone; фактическая матрица подтверждает
  `2024-01-02`…`2024-01-11` и rows `[5,5]` для обоих providers.
- **AC-005 — PASS.** `9` Open-Meteo и `7` OpenWeather остаются `CLOSED`, без
  rows, с exact `Долгосрочный прогноз еще не подгрузился`; отсутствующая
  позиция не превращается в partial session.
- **AC-006 — PASS.** Open-Meteo даёт десять filled cards; OpenWeather получает
  первые восемь filled и позиции 9–10 с preserved dates и null
  temperature/background/illustration. Nullable renderer оставляет existing
  card/date surface без invented values.
- **Isolation and boundaries — PASS.** Cache projection требует exact
  provider/location identity; tagged history is filtered by the same identity;
  result и adapter identity проверяются до persistence. Selected-provider/
  cache-switch probe даёт unavailable state без вызова другого provider. Exact
  date lookup и index-only projection исключают nearest, synthesis и duplicate
  substitution. Existing 2×5 available-card presentation, shared session/exit
  flow, module ownership and W19 task boundaries preserved; Display change is
  limited to nullable empty-cell rendering.
- **Evidence safety — PASS.** W19 artifacts and fixtures are synthetic/redacted;
  gate results record no credential/APK leakage and no live provider/network
  activity.

## Blockers and residual deferred route

Blockers: none. Target Android 11/custom-ROM rendering and live-provider/
network compatibility remain `DEFERRED` under the explicit operator boundary;
this review claims no runtime `PASS`. No repair is required. Scheduler/lifecycle
owner consumes this feature verdict and retains status/checkpoint authority;
`/mb-sync` remains outside this review.

SEMANTIC_VERDICT: semantic-pass
