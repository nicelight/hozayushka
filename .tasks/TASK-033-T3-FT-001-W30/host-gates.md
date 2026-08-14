# W30 host gates

All required current-baseline host gates passed after the fresh W30 probe.
No production/test behavior write occurred.

| Gate | Exact command | Exit | Evidence |
|---|---|---:|---|
| Clean Android debug build | ./gradlew clean assembleDebug | 0 | BUILD SUCCESSFUL in 8s; one existing deprecation warning in MainActivity.kt |
| Focused display projection suite | ./gradlew :app:testDebugUnitTest --tests com.hozayushka.app.DisplayProjectionTest | 0 | BUILD SUCCESSFUL in 3s |
| Full host unit suite | ./gradlew testDebugUnitTest | 0 | BUILD SUCCESSFUL in 1s |
| Android debug lint | ./gradlew lintDebug | 0 | BUILD SUCCESSFUL in 24s; report app/build/reports/lint-results-debug.html |
| Static diff integrity | git diff --check | 0 | no output |

The fresh baseline probe itself is recorded verbatim in red-baseline.md and
was rerun after the clean build against the rebuilt current baseline.

