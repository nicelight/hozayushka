---
description: Local API-key handling, redaction and evidence contract for the user-provided weather credential.
status: active
last_updated: 2026-08-04
source_of_truth: .memory-bank/constitution.md, .memory-bank/prd.md, .memory-bank/invariants.md
---
# Local Secret Handling

## Local API-Key Handling Contract

- The only accepted source of the weather key is user input in Settings.
- Settings & Location owns the valid stored value; Weather Context may receive
  an ephemeral request context only for an authorized provider call.
- The key MUST NOT be compiled into the APK, committed to source, placed in a
  URL/resource, written to logs, included in crash output, or copied into
  screenshots, fixtures or verification evidence.
- Invalid or missing keys preserve the last valid Settings value and expose the
  accepted owning inline error. They do not disable clock or timer behavior.
- Provider and test adapters use redacted placeholders in all durable fixtures.

## Storage Mechanism Boundary

The exact project-native persistence primitive is intentionally established by
the Foundation Gate. It must provide local-only storage and preserve the rules
above; choosing a new dependency or changing the security posture remains
subject to the [Constitution](../constitution.md) checkpoint. The mechanism is
not a second global source of truth.

## Evidence and Verification

Verification must prove absence of the real key from source, packaged
resources, logs and evidence without ever placing the key in a test artifact.
Use a synthetic placeholder for request-shape tests and record only redacted
results. The executable route is defined in
[Runtime Verification](../testing/runtime-verification.md).
