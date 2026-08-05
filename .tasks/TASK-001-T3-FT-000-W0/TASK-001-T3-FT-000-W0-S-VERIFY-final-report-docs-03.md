# Verification report — TASK-001-T3-FT-000-W0

## Verdict basis

- Fresh verifier run: `2026-08-04 23:55 +0500`.
- Indexed task: `T3`, `REQ-000`, status `in_progress`, no dependencies.
- Required gates: `./gradlew assembleDebug` and
  `./gradlew testDebugUnitTest`; both were exercised together from
  `./gradlew clean assembleDebug testDebugUnitTest`.
- No executor receipt was reused.

## Claim mapping and observations

| Claim | Fresh observation | Result |
|---|---|---|
| `REQ-000` / Foundation minimal proof | Clean Android build succeeded; host XML has 2 tests, 0 failures/errors/skips; launch/install and explicit probe invocation are recorded. | PASS |
| `AD-001` | One APK/module and one launchable `MainActivity`; only accepted capability/adapters roots are present. | PASS |
| `AD-002` / durable data rules | Host probe observed owner-local write, reload, overdue rehydration, reset and isolation. | PASS |
| `AD-003` / dependency graph | Fresh source import/root scan found only registered Consumer → Provider edges; no shared storage/event/backend path. | PASS |
| Provider/secret rules | Synthetic credential remained in memory; fixture/result used `[REDACTED]`; source/resources/build/evidence/APK scan was clean. | PASS |
| Target-device route | APK route is compiled and documented; `adb devices` has no attached device. Device PASS is intentionally not claimed by this task. | PASS for route recording |

## Reproducible commands

```text
./gradlew clean assembleDebug testDebugUnitTest
node scripts/mb-lint.mjs
git diff --check
/home/serg/Android/Sdk/build-tools/34.0.0/aapt dump badging app/build/outputs/apk/debug/app-debug.apk
/home/serg/Android/Sdk/build-tools/34.0.0/aapt dump xmltree app/build/outputs/apk/debug/app-debug.apk AndroidManifest.xml
adb devices
```

Results:

- Gradle: exit `0`, `BUILD SUCCESSFUL`, 40 actionable tasks.
- Host tests: `tests="2"`, `failures="0"`, `errors="0"`, `skipped="0"`.
- `mb-lint`: exit `0`, 65 files.
- `git diff --check`: exit `0`.
- APK: `com.hozayushka.app`, min SDK 30, landscape, launchable
  `com.hozayushka.app.app.MainActivity`.
- APK SHA-256:
  `0162c8f282334150f6731bc00efebd5e302c084693fc11534552eb1c80ee7188`.
- `adb devices`: no attached target.

## Targeted probe commands

Boundary probe:

```text
find app/src/main/kotlin/com/hozayushka/app -mindepth 1 -maxdepth 1 -type d -printf '%f\n' | sort
rg -n '^import com\.hozayushka\.app' app/src/main/kotlin/com/hozayushka/app --glob '*.kt' | sort
find app/src/main/kotlin/com/hozayushka/app -type d \( -name core -o -name common -o -name repository -o -name repositories -o -name services -o -name eventbus \) -print
rg -n -i 'eventbus|event bus|retrofit|ktor|room|workmanager|backend|google services|broadcastreceiver' app/src/main/kotlin app/src/main/AndroidManifest.xml app/build.gradle.kts
```

The forbidden-root and forbidden-boundary probes returned no matches. Production
reachability inspection used:

```text
rg -n 'saveFoundationLocation|resetFoundationState|refresh\(|start\(|cancel\(|rehydrateAt|requestAudioProbeAt|onActivityPaused|onActivityResumed|AudioManager|ToneGenerator|setOnClickListener|foundationProbe' app/src/main --glob '*.kt'
```

Secret probe: the fresh verifier scan used the bounded credential-pattern scan
from `.tasks/TASK-001-T3-FT-000-W0/secret-scan.md`, excluded only the
self-documenting scan report and current verifier report, and inspected
`app/src`, task/protocol evidence, test results and the packaged APK. Both
credential checks returned no matches; the scan separately confirmed the
expected `[REDACTED]` marker in the fixture/provider path.

## Scope and lifecycle

The fresh inspection found no product feature behavior, backend/cloud/Google
Services, reboot recovery, event bus, shared business-data owner, unauthorized
dependency or real credential. The current task remains `in_progress`; this
report does not close or promote it. The required next route for T3 is
`/red-verify TASK-001-T3-FT-000-W0`.

VERDICT: PASS
