# TASK-003-T3-FT-001-W2 — Attempt 1 RED baseline

Captured after `ready -> in_progress` and before the first production change.

## AC-001 / REQ-001, REQ-023

Foundation already contains static landscape/fullscreen/keep-screen-on policy:

- `AndroidManifest.xml:14` declares `android:screenOrientation="landscape"`.
- `PlatformRuntimeAdapter.kt:56` sets `FLAG_KEEP_SCREEN_ON`.
- `PlatformRuntimeAdapter.kt:60-65` sets fullscreen, hide-navigation and immersive flags.

This is a pre-implementation GREEN for the host-visible policy surface; target
fullscreen/readability evidence remains device-only and was not claimed.

## AC-002 / REQ-002, REQ-023

Foundation `DisplayCapability` only creates three generic TextViews at lines
54-56 and has no four-card or three-preset composition. Claim-specific RED:
the accepted stable shell is absent.

## AC-003 / REQ-002, REQ-022

Foundation exposes only `deviceTimeText()` backed by `DateTimeFormatter("HH:mm")`;
no date projection or Russian month formatter exists. Claim-specific RED:
the accepted device-time date is absent.

## AC-004 / REQ-003

No colon-state, pulse, network mode, 382/618 blink or brightness projection is
present in Main Display or Platform Runtime. Claim-specific RED: accepted
online/offline/countdown behavior is absent.

## AC-005 / REQ-004

No Main Display city click/hold routing, Settings destination, or Back return
seam exists. Claim-specific RED: accepted city interaction is absent.

## Probe basis

Read-only source inspection of the exact current Foundation files; no setup,
syntax or artificial-failure result was used as RED. Existing user changes
were preserved.
