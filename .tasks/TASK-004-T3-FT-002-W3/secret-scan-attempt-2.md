---
description: Current attempt-2 redacted secret evidence for TASK-004-T3-FT-002-W3.
status: active
---
# Secret Scan — TASK-004-T3-FT-002-W3 — Attempt 2

- attempt: 2
- receipt_status: current
- claim: FT-002 AC-007 / REQ-024 keeps credentials synthetic and absent from
  source, tests, task evidence and the packaged APK
- cwd: `/home/serg/Projects/Mobile_APPS/hozayushka`
- command:

  ```text
  test -d .tasks/TASK-004-T3-FT-002-W3 && if rg -n --pcre2 '(?i)(api[_ -]?key|x-yandex-weather-key|secret|credential)\s*[:=]\s*["'"'"'](?!\[REDACTED\]|synthetic|redacted)[A-Za-z0-9_./+=-]{16,}["'"'"']' app/src/main app/src/test .tasks/TASK-004-T3-FT-002-W3; then exit 1; else rc=$?; test "$rc" -eq 1; fi && if rg -n --pcre2 '(?i)sk-[A-Za-z0-9]{20,}|yandex-[A-Za-z0-9]{20,}' app/src/main app/src/test .tasks/TASK-004-T3-FT-002-W3; then exit 1; else rc=$?; test "$rc" -eq 1; fi && if strings app/build/outputs/apk/debug/app-debug.apk | rg -n --pcre2 '(?i)sk-[A-Za-z0-9]{20,}|yandex-[A-Za-z0-9]{20,}|x-yandex-weather-key'; then exit 1; else rc=$?; test "$rc" -eq 1; fi
  ```
- exit_code: `0`
- completed_at: `2026-08-08 00:38 +05`
- evidence: no credential-like literal, provider-key-shaped value or
  `x-yandex-weather-key` string found. Synthetic fixture output remains
  `[REDACTED]`; no real key was introduced.
