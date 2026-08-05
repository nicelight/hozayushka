# Secret/artifact scan — attempt 1

This scan is executor-owned supporting evidence. It checks the source,
fixture, task evidence, test report and packaged APK for credential-like output;
the verifier must perform a fresh scan for T3 closure.

## Exact command

```text
secret_pattern="x-yandex-weather-key|bearer[[:space:]]+[A-Za-z0-9._-]{12,}|api([_-]|[[:space:]])key[[:space:]]*[:=][[:space:]]*'[^']+'"; if rg -n -i --hidden --glob '!secret-scan.md' "$secret_pattern" app/src .tasks/TASK-001-T3-FT-000-W0 .protocols/TASK-001-T3-FT-000-W0 app/build/test-results; then printf '%s\n' 'credential-like source/evidence match found'; exit 1; else printf '%s\n' 'no credential-like source/evidence match'; fi; if strings app/build/outputs/apk/debug/app-debug.apk | rg -n -i "$secret_pattern"; then printf '%s\n' 'credential-like APK match found'; exit 1; else printf '%s\n' 'no credential-like APK match'; fi
```

## Result

- cwd: `/home/serg/Projects/Mobile_APPS/hozayushka`
- exit_code: `0`
- completed_at: `2026-08-04 15:15` local time (Asia/Dushanbe)
- output:

```text
no credential-like source/evidence match
no credential-like APK match
```

The provider probe creates its credential only in memory. The fixture contains
`[REDACTED]`, the adapter returns only the redacted marker, and no live request
or user credential is present. The report itself is excluded from the source
scan because it contains the literal scan pattern; this does not weaken the
APK/source/test/evidence inputs being checked. The scan pattern is deliberately
narrow and is not a substitute for verifier-owned review of the complete
artifact surface.

## Attempt 2 correction scan

The corrected APK and source surface were rescanned after the Foundation probe
mode, lifecycle hooks and audio adapter were added. The scan excluded only this
file because its documented command contains the literal detection pattern.

Command:

```text
if rg -n -i --hidden --glob '!secret-scan.md' -e 'x-yandex-weather-key' -e 'bearer[[:space:]]+[A-Za-z0-9._-]{12,}' -e "api([_-]|[[:space:]])key[[:space:]]*[:=][[:space:]]*'[^']+'" app/src .tasks/TASK-001-T3-FT-000-W0 .protocols/TASK-001-T3-FT-000-W0 app/build/test-results; then echo 'credential-like source/evidence match found'; exit 1; else echo 'no credential-like source/evidence match'; fi
if strings app/build/outputs/apk/debug/app-debug.apk | rg -n -i -e 'x-yandex-weather-key' -e 'bearer[[:space:]]+[A-Za-z0-9._-]{12,}' -e "api([_-]|[[:space:]])key[[:space:]]*[:=][[:space:]]*'[^']+'"; then echo 'credential-like APK match found'; exit 1; else echo 'no credential-like APK match'; fi
```

Result: both checks exited `0`; output was `no credential-like
source/evidence match` and `no credential-like APK match`. The synthetic
credential remains in-memory only and the app route displays no credential
value.

## Attempt 3 correction scan

The current APK and evidence surface were rescanned after moving synthetic
request construction behind `WeatherCapability`.

Command:

```text
if rg -n -i --hidden --glob '!secret-scan.md' -e 'x-yandex-weather-key' -e 'bearer[[:space:]]+[A-Za-z0-9._-]{12,}' -e "api([_-]|[[:space:]])key[[:space:]]*[:=][[:space:]]*'[^']+'" app/src .tasks/TASK-001-T3-FT-000-W0 .protocols/TASK-001-T3-FT-000-W0 app/build/test-results; then echo 'credential-like source/evidence match found'; exit 1; else echo 'no credential-like source/evidence match'; fi
if strings app/build/outputs/apk/debug/app-debug.apk | rg -n -i -e 'x-yandex-weather-key' -e 'bearer[[:space:]]+[A-Za-z0-9._-]{12,}' -e "api([_-]|[[:space:]])key[[:space:]]*[:=][[:space:]]*'[^']+'"; then echo 'credential-like APK match found'; exit 1; else echo 'no credential-like APK match'; fi
```

Result: both checks exited `0`; output was `no credential-like source/evidence
match` and `no credential-like APK match`. The synthetic credential remains
in-memory only and the owner-routed app route displays no credential value.
