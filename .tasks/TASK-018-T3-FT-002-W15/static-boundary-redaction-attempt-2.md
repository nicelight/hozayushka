---
description: Static boundary and redaction evidence for TASK-018-T3-FT-002-W15 attempt 2.
status: supporting-only
---
# Static boundary and redaction — attempt 2

Deterministic host-only scans were run after the green gates.

- Request/wiring shape scan found the accepted Yandex endpoint, `hours=true`,
  header-only key path, isolated fixture adapter and existing executor wiring.
- Manifest scan found exactly `ACCESS_NETWORK_STATE` and `INTERNET`.
- Correction rule scan found the pre-cache checks for full-daily conditions,
  non-empty/incomplete hourly data and empty hourly data when a prior hourly
  cache exists.
- The process-only token
  `W15-ATTEMPT-2-SYNTHETIC-ONLY-7f1e` was absent from source, W15 evidence,
  W15 protocol files and the APK; it was never written or emitted.
- No `TASK-018`, `FT-003`, `FT-004` or `FT-008` marker was present in the two
  correction source/test surfaces.

Observed result: static boundary/redaction checks `PASS`.

No real or user-like credential, unredacted provider data, log, screenshot,
live request or target-device artifact was used.
