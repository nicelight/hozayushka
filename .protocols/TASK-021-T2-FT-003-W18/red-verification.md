---
description: Final independent adversarial semantic verification for TASK-021-T2-FT-003-W18.
status: final
task_id: TASK-021-T2-FT-003-W18
attempt: 1
verification_cycle: final-independent-red-cycle
role: Reviewer
---
# Red Verification — TASK-021-T2-FT-003-W18

## Semantic target

Проверялся только W18-owned outcome: Forecast Sessions открывает hourly view
для выбранного provider лишь при наличии всех восьми selected-city-local slots
(`06:00, 09:00, 12:00, 15:00, 18:00, 21:00` текущего city-local дня и
`00:00`, `03:00` следующего дня); иначе Main Display остаётся видимым и
возвращается точное сообщение `Почасовой прогноз еще не подгрузился`. Источником
каждого значения должен быть только selected provider; synthesis, соседняя
замена, cache/history другого provider, fallback и mixing запрещены.

## Evidence and adversarial coverage

Проверены task card, FT-003 Revision-2 plan/decision log и W18
context/plan/progress/handoff, direct canonical contracts (boundary map,
capability interfaces, weather provider, local data, lifecycle, invariants,
runtime verification), tier policy, executor handoff, свежие S-VERIFY
`verification.md`/report/evidence и `hourly-completeness-matrix.json`.
Executor и functional-verifier claims использовались как context, не как
единственное доказательство.

Claim-by-claim:

- **Timezone и восемь fixed slots — PASS.** Current source вычисляет `today`
  через `record.snapshot.apiTimeZone`, строит только accepted keys и делает
  exact `(date,time)` lookup. Fresh fixture фиксирует `Asia/Dushanbe`,
  `2024-01-02`, `now=2024-01-02T12:00:00Z`; независимый matrix validator
  подтвердил восемь ожидаемых ключей и `[4,4]` rows.
- **Complete Open-Meteo/OpenWeather cases — PASS.** Deterministic test
  проходит оба значения `WeatherProviderId`, complete matrix содержит по одному
  case на provider: `OPEN`, 8 cards, selected-provider call `1`, other-provider
  calls `0`. Четыре OpenWeather current-day slots (`06:00`–`15:00`) при этом
  уже elapsed в `Asia/Dushanbe` и входят в complete projection.
- **Каждая missing position — PASS.** Matrix содержит 16 независимых случаев,
  по 8 на provider. Каждый даёт `hourlyProjection=null`, `CLOSED`, zero cards,
  точное `Почасовой прогноз еще не подгрузился`, selected call `1` и other call
  `0`; четыре OpenWeather missing cases явно отмечены elapsed current-day.
  Fresh host rerun обоих W18 probe tests завершился `BUILD SUCCESSFUL`.
- **No synthesis/substitution/borrowing/fallback/mixing — PASS.**
  `WeatherCapability.hourlyProjection` проверяет наличие exact key и берёт
  значение только из него; `ForecastSessionCapability` создаёт session только
  из non-null projection, иначе оставляет `CLOSED`. `matchingRecord` требует
  одновременно selected provider и location identity. Provider-switch case
  после seed Open-Meteo оставляет OpenWeather session закрытой, с exact message
  и нулём OpenWeather calls; source surface Forecast Sessions не имеет прямого
  доступа к adapter/cache/history.
- **Scope/history/redaction/deferred route — PASS.** W18-owned delta —
  deterministic test proof и matrix; production code, adapters, Settings,
  lifecycle/status, scheduler checkpoint и executor/verifier-owned evidence не
  менялись этим review. Независимые JSON assertions подтвердили W17=`failed`
  (W17), W20=`done` (W20), W19=`blocked` (W19, dependency W18). Evidence
  содержит только synthetic/redacted OpenWeather observations; live provider,
  network, credential, emulator/device, ADB/QEMU и runtime PASS не использовались.

## Admitted findings

None.

## Operator questions

None.

## Blockers

None. No repair is required for the reviewed W18 semantics.

## Deferred scope

Target Android rendering/custom-ROM behavior and live provider/subscription/
network compatibility remain `DEFERRED` under the accepted task boundary. This
is residual target risk only and is not a runtime PASS claim.

## Owner handoff

- Evidence/report paths: this protocol; `.tasks/TASK-021-T2-FT-003-W18/TASK-021-T2-FT-003-W18-S-RED-VERIFY-final-report-docs-01.md`; fresh S-VERIFY `verification.md`, report and `verifier-owned-evidence.md`; `hourly-completeness-matrix.json`; `ForecastSessionTest.kt`.
- Recommended owner action: return the semantic result to the scheduler/lifecycle owner; preserve W17/W20/W19 history and keep closure/promotion external. The later feature-level FT-003 semantic gate remains required by T2 policy.
- Resume route or `n/a`: `n/a` for repair; later `/red-verify --feature FT-003` after the applicable lifecycle boundary.

SEMANTIC_VERDICT: semantic-pass
