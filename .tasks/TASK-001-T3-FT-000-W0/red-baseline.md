# Initial RED baseline — attempt 1

Captured after `ready → in_progress` and before any production scaffold write.
This is execution RED evidence, not a task verdict.

## Claim mapping

- `REQ-000`: no executable Android build/start/test/smoke path exists.
- `system-architecture.md#AD-001`: no composition root or accepted slice roots
  exist in the current checkout.
- `boundary-map.md#dependency-graph`: no executable boundary wiring exists to
  inspect.
- `local-data.md#durable-data-rules`: no known resettable local fixture path
  exists.
- `local-secret-handling.md#evidence-and-verification`: no redacted provider
  fixture/artifact scan path exists.
- `runtime-verification.md#foundation-minimal-proof`: no Foundation proof route
  exists.

## Exact pre-write probe

Command:

```text
git status --short; git rev-parse HEAD; for path in settings.gradle.kts build.gradle.kts gradle.properties gradlew app/src/main/AndroidManifest.xml app/src/main/kotlin app/src/test; do if [ -e "$path" ]; then printf '%s: present\n' "$path"; else printf '%s: absent\n' "$path"; fi; done; if [ -x ./gradlew ]; then ./gradlew --version; else printf '%s\n' './gradlew: unavailable'; fi
```

Working directory: `/home/serg/Projects/Mobile_APPS/hozayushka`

Observed output:

```text
 M .memory-bank/tasks/TASK-001-T3-FT-000-W0.task.json
?? .protocols/TASK-001-T3-FT-000-W0/
?? .tasks/TASK-001-T3-FT-000-W0/
e00238676b0810431ba351a6c2e091898022d8cb
settings.gradle.kts: absent
build.gradle.kts: absent
gradle.properties: absent
gradlew: absent
app/src/main/AndroidManifest.xml: absent
app/src/main/kotlin: absent
app/src/test: absent
./gradlew: unavailable
```

The three untracked paths shown are the required `/exe` protocol/evidence
bookkeeping created before this probe; they are not production scaffold.
