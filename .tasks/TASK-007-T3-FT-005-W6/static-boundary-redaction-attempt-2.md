---
description: Current retry static boundary and redaction gate evidence.
status: supporting
---
# Static / boundary / redaction gates — attempt 2

- attempt: `2`
- receipt_status: `supporting-only`
- completed_at: `2026-08-08 05:56 +0500`

## Commands and result

The following bounded command exited `0`:

```text
set -e
if rg -n 'SharedPreferences|WeatherCacheStore|Provider(Current|Daily|Hourly)Weather|ProviderWeatherData|adapters\.weather|WeatherProvider' app/src/main/kotlin/com/hozayushka/app/display; then exit 1; fi
if rg -n 'SettingsStateStore|SharedPreferencesSettingsStateStore|WeatherCapability|WeatherProvider|adapters\.weather|WeatherCacheStore' app/src/main/kotlin/com/hozayushka/app/timer; then exit 1; fi
if rg -n "X-Yandex-Weather-Key:[[:space:]]+[A-Za-z0-9._-]{8,}|Bearer[[:space:]]+[A-Za-z0-9._-]{20,}|BEGIN (RSA|OPENSSH|PRIVATE) KEY|apiKey[[:space:]]*=[[:space:]]*['\\\"][^'\\\"]+|api_key[[:space:]]*=[[:space:]]*['\\\"][^'\\\"]+" app/src/main app/src/test .tasks/TASK-007-T3-FT-005-W6; then exit 1; fi
if git diff --unified=0 -- app/src/main/kotlin/com/hozayushka/app/settings/SettingsCapability.kt app/src/main/kotlin/com/hozayushka/app/timer/TimerCapability.kt app/src/main/kotlin/com/hozayushka/app/display/DisplayCapability.kt app/src/main/kotlin/com/hozayushka/app/app/FoundationRuntime.kt app/src/main/res/values/colors.xml app/src/main/res/values/strings.xml app/src/test/kotlin/com/hozayushka/app/TimerPresetTest.kt | rg '^\\+.*(FT-006|FT-007|overdue|audio|X-Yandex|apiKey|api_key)'; then exit 1; fi
git diff --check
node scripts/mb-lint.mjs
```

The first equivalent scan attempt was a workflow false positive because it
matched the literal regex documented in the prior evidence file; that papercut
is recorded in `PAPERCUTS/GPT-5 __ 08-08-2026 05.55.md`. The retry command
matches only secret-shaped values and passed. No private store/provider access,
forbidden FT-006/FT-007 behavior, or secret-bearing value was observed.

## Input basis and disposition

- source basis: repository `HEAD`
  `a93e46118f0f0b90e311b6174e3f5a8ed7d89fef` plus the declared task-local
  correction and pre-existing workspace changes.
- receipt: current attempt 2 supporting evidence; not an independent verifier
  result and not a `/verify` reuse candidate.
