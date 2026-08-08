---
description: Claim-linked pre-implementation RED evidence for TASK-006-T3-FT-004-W5.
status: final
---
# Pre-implementation RED — TASK-006-T3-FT-004-W5

Attempt: 1. The selected task was durably `in_progress` before this probe.
The probe inspected the pre-change source at repository basis
`a93e46118f0f0b90e311b6174e3f5a8ed7d89fef`; the workspace was already broadly
dirty and no unrelated changes were altered.

## Claim-specific baseline

Command, from `/home/serg/Projects/Mobile_APPS/hozayushka`:

```text
set +e
for item in AC-001|fun dailyProjection; AC-001|fun openLongTerm; AC-002|chunked(5); AC-002|LongTermForecastProjection; AC-003|createLongTermForecastView; AC-003|longTermCard; AC-005|Долгосрочный прогноз еще не подгрузился; do
  rg -n --fixed-strings "$pattern" app/src/main app/src/test
done
```

Observed claim-linked results:

- `AC-001`: RED — no public daily read-model method and no long-term session entry.
- `AC-002`: RED — no 2×5 long-term row projection and no ten-day projection type.
- `AC-003`: RED — no long-term Main Display renderer or long-term card renderer.
- `AC-004`: the shared FT-003 timing/gesture contract was already GREEN in the
  existing capability and was preserved; no long-term integration consumer
  existed yet. No unnecessary production change was made to the core flow.
- `AC-005`: RED — no accepted long-term fallback message or rejection path.

## Preserved prerequisite GREEN

Command:

```text
./gradlew testDebugUnitTest \
  --tests com.hozayushka.app.ForecastSessionTest.sharedSessionTimingAndGesturesFollowAcceptedTransitions \
  --tests com.hozayushka.app.ForecastSessionTest.holdKeepsSessionOpenBeyondOriginalDeadlineAndReleaseClosesImmediately
```

Result: exit `0`, Gradle `BUILD SUCCESSFUL`; the two existing shared-session
tests passed. This is supporting baseline evidence only, not final task
verification.

No production behavior was changed before this RED/GREEN baseline was
recorded. The next probe must obtain claim-equivalent GREEN after the FT-004
implementation.
