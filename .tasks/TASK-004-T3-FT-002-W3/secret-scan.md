---
description: Redacted secret/artifact evidence for TASK-004-T3-FT-002-W3.
status: active
---
# Secret Scan — TASK-004-T3-FT-002-W3

Attempt 1 receipt; `receipt_status: supporting-only`. Current retry secret
evidence is in `secret-scan-attempt-2.md`.

## Scope

`app/src/main`, `app/src/test`, `.tasks/TASK-004-T3-FT-002-W3`.

## Exact receipts

```text
test -d .tasks/TASK-004-T3-FT-002-W3
if rg -n --pcre2 '(?i)(api[_ -]?key|x-yandex-weather-key|secret|credential)\s*[:=]\s*["'"'"'](?!\[REDACTED\]|synthetic|redacted)[A-Za-z0-9_./+=-]{16,}["'"'"']' app/src/main app/src/test .tasks/TASK-004-T3-FT-002-W3; then exit 1; else rc=$?; test "$rc" -eq 1; fi
if rg -n --pcre2 '(?i)sk-[A-Za-z0-9]{20,}|yandex-[A-Za-z0-9]{20,}' app/src/main app/src/test .tasks/TASK-004-T3-FT-002-W3; then exit 1; else rc=$?; test "$rc" -eq 1; fi
```

Result: exit `0`; no credential-like literal and no provider-key-shaped value
was found. Provider tests use `WeatherProviderRequest.fromSyntheticProbe()` and
expose only `[REDACTED]`.
