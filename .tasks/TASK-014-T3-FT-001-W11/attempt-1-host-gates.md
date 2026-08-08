# Attempt 1 — host gates

## Focused regression

- Command: `./gradlew testDebugUnitTest --tests com.hozayushka.app.DisplayProjectionTest`
- Result: exit `0`, `BUILD SUCCESSFUL`.
- Coverage: the stable shell policy now records content-height header allocation (`headerWeight=0`) and remaining-height weather allocation (`weatherRowWeight=1`) while retaining exactly four cards/three presets; existing city routing assertions retain empty-city short tap, selected-city short-tap no-op and hold/open behavior.
- Limitation: this host policy assertion supports the correction but does not claim Android View measurement or touch reachability; the emulator artifacts are decisive for those outcomes.

## Required final gates

- `./gradlew clean assembleDebug` → exit `0`, `BUILD SUCCESSFUL`, all 34 tasks executed. One pre-existing `MainActivity.onBackPressed` deprecation diagnostic was emitted; it did not fail the build and is outside this task.
- `./gradlew testDebugUnitTest` → exit `0`, `BUILD SUCCESSFUL`; generated XML contains `52` test cases and `0` failure/error/skipped elements.
- `git diff --check` → exit `0`, no output; repeated after the final protocol/report update with the same result.
- Final APK: `app/build/outputs/apk/debug/app-debug.apk`, SHA-256 `ace2bbbc24ea190bf6122dc07cb124f2d9004ed788be3cf33e2fbbb25b33a8f7`; `adb install -r` → `Success`.
- Final cold start: `adb ... am start -W -S -n com.hozayushka.app/.app.MainActivity` → `Status: ok`, `LaunchState: COLD`.

No execute gate is offered as a `/verify` reuse candidate because the shared worktree has broad tracked/untracked deviations that prevent a conservative bounded input snapshot. These are supporting-only executor results; `/verify` remains independent.
