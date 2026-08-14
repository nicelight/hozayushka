---
description: Host fake denial/error matrix for TASK-026-T3-FT-007-W23 AC-005.
status: supporting
---
# AC-005-DENIAL-ERROR-MATRIX

## Basis

- Attempt `1`; each row uses disposable timer/settings state, synthetic clock,
  the display-tick driver and a deterministic fake platform.
- `visual_overdue` is checked before dismissal; `any_tap_dismissal` is checked
  with double tap; no row throws/crashes; `post_dismissal_requests=0`.

| case | policy_input | audio_result | reason | visual_overdue | any_tap_dismissal | no_crash | post_dismissal_requests | comparison |
|---|---|---|---|---|---|---|---:|---|
| `VOLUME_0` | app volume `0`, classic | `DENIED` | `app_volume_suppressed` | true | `OVERDUE -> IDLE` | true | 0 | PASS |
| `SILENT_NON_NORMAL_RINGER` | ringer non-normal | `DENIED` | `ringer_mode_suppressed` | true | `OVERDUE -> IDLE` | true | 0 | PASS |
| `DND` | interruption filter none | `DENIED` | `dnd_suppressed` | true | `OVERDUE -> IDLE` | true | 0 | PASS |
| `UNAVAILABLE_ROUTE` | no output route | `DENIED` | `audio_route_unavailable` | true | `OVERDUE -> IDLE` | true | 0 | PASS |
| `UNAVAILABLE_SERVICE` | AudioManager unavailable | `DENIED` | `audio_service_unavailable` | true | `OVERDUE -> IDLE` | true | 0 | PASS |
| `AUDIO_START_ERROR` | ToneGenerator start throws/returns false | `ERROR` | `audio_start_error` | true | `OVERDUE -> IDLE` | true | 0 | PASS |

## Result

All six accepted denial/error inputs affect audio only. The visual overdue
state remains available, any tap remains dismissible, and no later scheduler
request occurs after dismissal. Host fake policy/request handling: PASS.
