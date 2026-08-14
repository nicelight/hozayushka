---
description: Fresh claim-linked RED baseline for TASK-031-T3-FT-007-W28.
status: supporting
---
# W28 claim-linked RED baseline — attempt 1

Claim: `FT-007-AC-006 / REQ-015 / REQ-023`.

The current pre-W28 source was inspected at the accepted W27 comparison size
`1280×720`. The probe used W27's durable same-size geometry as the reference;
it did not modify production or test behavior.

Command:

```text
node <<'NODE' ...
```

Observed probe result:

```json
{
  "renderSize": { "width": 1280, "height": 720 },
  "idleClockTextSize": 188.75,
  "activeCountdownTextSize": 228,
  "baselineOverdueCounterTextSize": 76,
  "baselineOverduePlusTextSize": 176,
  "baselineUsesOpaquePresetFill": true,
  "baselineHasDedicatedOverdueCircularBackdrop": false,
  "elapsedLargerThanIdle": false,
  "elapsedLargerThanActive": false,
  "claim": "FT-007-AC-006 / REQ-015 / REQ-023"
}
```

Result: honest RED for the owned visual claim. The existing overdue branch uses
an opaque preset fill, has no dedicated overdue circular backdrop, and its
`76f` elapsed counter is smaller than both the W27 idle `188.75f` and active
`228f` text sizes. The existing focused `DisplayProjectionTest` suite still
passed (`./gradlew --offline --no-daemon :app:testDebugUnitTest --tests
com.hozayushka.app.DisplayProjectionTest`, exit `0`); that is a baseline host
regression result, not W28 GREEN.

W27 source/test changes and all other pre-existing worktree changes remain
outside this RED claim and were not rewritten.
