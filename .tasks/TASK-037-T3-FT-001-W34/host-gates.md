---
task_id: TASK-037-T3-FT-001-W34
attempt: 1
status: current
---
# Host gates — TASK-037-T3-FT-001-W34

All required project-native gates completed after the two-file correction.

| Gate | Command | Result | Evidence |
|---|---|---|---|
| Clean Android debug build | `./gradlew clean assembleDebug` | exit `0`, `BUILD SUCCESSFUL` | `clean-build.log`; APK SHA in `apk-sha256.txt` |
| Focused display projection suite | `./gradlew :app:testDebugUnitTest --tests com.hozayushka.app.DisplayProjectionTest --console=plain` | exit `0`, `31/31` | `focused-green.log`; XML contains W34 GREEN lines |
| Full host unit suite | `./gradlew testDebugUnitTest` | exit `0`, `119/119`, 0 failures/errors | `full-host-tests.log`; test XML files |
| Android debug lint | `./gradlew lintDebug` | exit `0`, `BUILD SUCCESSFUL` | `lint.log`; HTML report under `app/build/reports/` |
| Static diff integrity | `git diff --check` | exit `0` | `diff-check.log` |

Focused W34 GREEN values:

- `2460x1080`: band `754..1056`, ratio `0.27962962`, clock zone
  `0.7203704`, card heights `[302,302,302,302]`, bottoms all `1056`.
- `1280x720`: band `495..696`, ratio `0.27916667`, clock zone
  `0.7208333`, card heights `[201,201,201,201]`, bottoms all `696`.

No reuse candidate is proposed: the workspace has broad pre-existing dirty and
runtime/generated state, so these executor receipts remain supporting evidence
for independent verification.
