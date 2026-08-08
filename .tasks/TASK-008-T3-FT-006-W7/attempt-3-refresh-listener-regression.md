# Claim-equivalent GREEN — retry attempt 3

## Correction

`DisplayCapability.refresh()` now keeps each newly created weather-card view
in a local variable and binds the existing conditional
`activeTimerTouchListener` immediately after `cards.addView(...)`. The
listener still returns `false` while Timer is `IDLE`, so the existing
weather-card click path and countdown single-tap protection remain unchanged.

## Deterministic regression probe

Command, from `/home/serg/Projects/Mobile_APPS/hozayushka`:

```text
set -euo pipefail
source='app/src/main/kotlin/com/hozayushka/app/display/DisplayCapability.kt'
refresh_body="$(sed -n '/        fun refresh()/,/        val ticker =/p' "$source")"
test "$(printf '%s\n' "$refresh_body" | rg -c 'cards\.removeAllViews\(\)')" = 1
test "$(printf '%s\n' "$refresh_body" | rg -c 'val card = weatherCard\(')" = 1
test "$(printf '%s\n' "$refresh_body" | rg -c 'cards\.addView\(')" = 1
test "$(printf '%s\n' "$refresh_body" | rg -c 'card\.setOnTouchListener\(activeTimerTouchListener\)')" = 1
rg -n 'city\.setOnTouchListener\(activeTimerTouchListener\)|card\.setOnTouchListener\(activeTimerTouchListener\)|onSingleTapConfirmed|onDoubleTap' "$source"
printf '%s\n' 'refresh listener regression probe passed'
```

Result: exit `0`; output included the city binding at line 377, rebuilt-card
binding at line 434, the existing single/double gesture handlers, and
`refresh listener regression probe passed`.

## Claim mapping

- `FT-006-AC-003 / REQ-013`: recreated weather-card child views retain the
  active Timer route, so supported double-tap cancellation is not lost after
  refresh; the existing Timer unit probe continues to prove single tap keeps
  the countdown active.
- `FT-006-AC-005 / REQ-025`: recreated weather-card child views retain the
  active Timer route for overdue any-tap dismissal without provider/network
  input.

This is deterministic source-path GREEN for the corrected dispatch seam; no
Android target runtime PASS is claimed.
