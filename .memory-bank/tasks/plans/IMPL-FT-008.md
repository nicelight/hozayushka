---
description: Revision-2 implementation plan for provider selection, local OpenWeather key and attributions.
status: active
last_updated: 2026-08-11
---
# IMPL-FT-008 — Provider and weather-access settings

## Outcome

Preserve the accepted W9 offline location/catalog experience and add one
explicit provider setting: Open-Meteo default/no-key or OpenWeather
owner-selected/local-key. Failures remain inline and preserve the last valid
selection; required Open-Meteo and GeoNames attributions precede Back.

## Ordered work

1. Preserve `TASK-010-T3-FT-008-W9` and its terminal evidence unchanged.
2. Retain `TASK-019-T3-FT-008-W16` as the completed
   provider/key/attribution Settings outcome and preserve all three attempts.
3. Let planned `TASK-020-T3-FT-002-W17` consume the resulting projection and
   own provider dispatch/transport/cache behavior after scheduler promotion.

## Ownership and execution path

Settings & Location remains the sole owner of selected provider, selected
location and the OpenWeather owner key. It auto-saves valid changes, preserves
last-valid state on error, exposes its public projection and requests only the
accepted refresh command. It does not call adapters, normalize weather, own
cache/history or choose fallback.

The bounded path is first-run/reopen Settings state → provider selection and
contextual key UI → owner-local persistence/public projection → downstream
refresh command. Attribution is part of the same Settings content order.

## Acceptance map

| Current claim | Owner | Proof |
|---|---|---|
| `AC-002 / REQ-017` | W9 done | Khujand/default and selected coordinates/refresh seam |
| `AC-003`–`AC-005 / REQ-018` | W9 done | Offline catalog, aliases and GeoNames attribution |
| `AC-001 / REQ-024` | W16 done | OpenWeather-only local key; Open-Meteo no-key; durable absence |
| `AC-006 / REQ-017, REQ-018, REQ-024, REQ-027` | W16 done | Contextual provider/key failure and state preservation |
| `AC-007 / REQ-027` | W16 done | First-run default, explicit switch, auto-save/reopen, no fallback claim |
| `AC-008 / REQ-028` | W16 done | Required Open-Meteo attribution alongside GeoNames |

## Advisory surface and proof

- `settings/SettingsCapability.kt` — provider enum/state, contextual key and
  ordered Settings projection
- `res/values/strings.xml` — accepted labels/errors/attribution
- `SettingsLocationTest.kt` — isolated persistence, failure, attribution and
  secret-absence probes

The claim-linked RED was the then-current unconditional Yandex-era
key/no-provider selector state. Final GREEN proves first-run, explicit switch,
reopen, missing/invalid-key,
provider/network and unknown-city preservation, plus synthetic marker scans
across source/resources/APK/log/evidence. No live key or provider request is
used.

No hard `write_boundary` was selected. Project-native clean build and host
tests were execution gates; physical-device/live-provider evidence remains
deferred without a runtime `PASS` claim.

## W16 result and transition handoff

Final Attempt 3 closed with executor `PASS_FOR_HANDOFF`, fresh functional
`PASS` and fresh `semantic-pass`; see the [task record](../TASK-019-T3-FT-008-W16.task.json),
[functional verification](../../../.protocols/TASK-019-T3-FT-008-W16/verification.md)
and [semantic verification](../../../.protocols/TASK-019-T3-FT-008-W16/red-verification.md).
The same task record and [progress log](../../../.protocols/TASK-019-T3-FT-008-W16/progress.md)
retain the unsuccessful Attempt-1 functional failure and Attempt-2 semantic
failure as historical evidence.

The Attempt-3 safeguard denies provider-unidentified legacy key access/refresh.
TASK-020 must replace that deny atomically with selected-OpenWeather-authorized
access while implementing selected-provider dispatch; it remains `planned` and
is not promoted by this sync. Physical-device/live-provider proof remains
`DEFERRED`, with no runtime `PASS` claim.

## Constraints

Exactly two values: Open-Meteo default and OpenWeather explicit. The key is
applicable only to OpenWeather. Preserve W9 location/catalog and FT-009
personalization; do not implement adapters/cache/forecasts, add extra Settings
controls, third providers, fallback, backend/shared key, plugin/DI/event
infrastructure or new dependencies.

## Direct normative inputs

- [.memory-bank/features/FT-008-weather-location-settings.md](../../features/FT-008-weather-location-settings.md)
- [.memory-bank/contracts/capability-interfaces.md](../../contracts/capability-interfaces.md)
- [.memory-bank/contracts/weather-provider.md](../../contracts/weather-provider.md)
- [.memory-bank/contracts/local-secret-handling.md](../../contracts/local-secret-handling.md)
- [.memory-bank/domains/local-data.md](../../domains/local-data.md)
- [.memory-bank/testing/runtime-verification.md](../../testing/runtime-verification.md)
- [.memory-bank/tasks/TASK-019-T3-FT-008-W16.task.json](../TASK-019-T3-FT-008-W16.task.json)

## Handoff

W16 closure is reconciled. The scheduler owns post-sync lint/strict-doctor and
the separate promotion-eligibility pass for planned TASK-020.
