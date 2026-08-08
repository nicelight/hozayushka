---
description: Verifier-owned probe evidence for TASK-006-T3-FT-004-W5.
status: final
---
# Verifier-owned probe evidence — TASK-006-T3-FT-004-W5

- Full host gate: `./gradlew clean assembleDebug && ./gradlew testDebugUnitTest`; exit `0` and `BUILD SUCCESSFUL`.
- Focused claim gate: `./gradlew testDebugUnitTest --tests com.hozayushka.app.ForecastSessionTest.completeTenDayReadModelSurvivesOwnerReloadAndOpensFromLongTermEntry --tests com.hozayushka.app.ForecastSessionTest.longTermProjectionUsesSelectedCityDayNightAndRejectsIncompleteDailyFields --tests com.hozayushka.app.ForecastSessionTest.longTermSessionUsesSharedTimingAndGestureContract --tests com.hozayushka.app.DisplayProjectionTest.tomorrowAndDayAfterUseTheSameLongTermForecastIntent`; exit `0`.
- Observed: public save/reload equality, exactly ten ordered city-local records, `[5,5]` rows, selected-city day/night projection, no pressure arrows, both entry intents, exact unavailable fallback, and all shared exit transitions.
- Source-only boundary and credential scan: PASS. APK strings redaction scan: PASS. `mb-lint` and task-surface diff-check: PASS.
- `adb devices`: no attached target; target evidence is `DEFERRED`, non-blocking, and no runtime PASS is claimed.
