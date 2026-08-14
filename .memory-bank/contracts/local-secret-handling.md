---
description: Local OpenWeather API-key transport, redaction and evidence contract.
status: active
last_updated: 2026-08-10
source_of_truth: .memory-bank/constitution.md, .memory-bank/prd.md, .memory-bank/invariants.md
---
# Local Secret Handling

## Local API-Key Handling Contract

- The only accepted source of the OpenWeather key is owner input in Settings
  after explicit OpenWeather selection. Open-Meteo neither requests, validates
  nor receives this key.
- Settings & Location owns the valid local secret value. Weather Context may
  obtain it ephemerally only to authorize the selected OpenWeather adapter.
- OpenWeather One Call 3.0 requires `appid` in the query parameter. Therefore
  the raw key may exist in a URL only transiently during construction and
  sending of that explicit outbound HTTPS request. It must be removed or
  redacted before a URL, request, exception or redirect detail crosses any
  persistence, diagnostic, UI or evidence boundary.
- The owner's real key MUST NOT be compiled into the APK, committed as a source
  literal, copied into persisted non-secret Settings/weather data, placed in an
  APK resource, written to logs/crash output, or copied into screenshots,
  fixtures or verification evidence.
- Missing/invalid-key messages apply only to selected OpenWeather. They preserve
  provider selection and the last valid Settings value, invoke no Open-Meteo
  fallback, and do not disable clock or timer behavior.
- Request-shape tests may use an unmistakably synthetic in-memory value, but
  durable fixtures and captured/evidenced URLs remain redacted.

## Storage Mechanism Boundary

The exact project-native persistence primitive is intentionally established by
the Foundation Gate. It must provide local-only storage and preserve the rules
above; choosing a new dependency or changing the security posture remains
subject to the [Constitution](../constitution.md) checkpoint. The mechanism is
not a second global source of truth.

## Evidence and Verification

Verification must prove that Open-Meteo sends no credential and that the real
OpenWeather key is absent from source literals, packaged resources, persisted
non-secret data, logs, screenshots, fixtures and evidence without ever placing
the key in a test artifact. A request-shape test proves that a synthetic value
is passed as `appid` only for selected OpenWeather and records only a redacted
URL/result. The executable route is defined in
[Runtime Verification](../testing/runtime-verification.md).
