# Real-device smoke/debug report

- verdict: `BLOCKED` — secure device keyguard не позволил получить app-surface evidence; product PASS/FAIL verdict не выносится.
- role: Reviewer, read-only
- run: 2026-08-10, Asia/Dushanbe
- authorized device: serial `1156725456009666`; manufacturer `TECNO`; model `TECNO LI6`; Android `15` / API `35`; `1080×2460`, density `440`
- constraints: все device-команды запускались с `adb -s 1156725456009666`; emulator/AVD/QEMU/connected-device Gradle tasks не запускались; credentials и live Yandex key не вводились.

## Host evidence

- `./gradlew assembleDebug`: PASS, APK `app/build/outputs/apk/debug/app-debug.apk`.
- `./gradlew testDebugUnitTest`: PASS. Это host-only evidence, не заменяет device smoke.
- APK установился (`Success`), затем данные пакета были очищены.

## Device observations

1. `am start -W -n com.hozayushka.app/.app.MainActivity` вернул `Status: ok`; процесс приложения был запущен, а Activity присутствовала в task.
2. Surface приложения не была доступна: UI dump показал `com.android.systemui:id/keyguard_pin_view` и текст `Используйте отпечаток пальца или введите пароль`; logcat содержит `mAllSleepTokens ... keyguard`.
3. `wm dismiss-keyguard`, wake/menu key events и один свайп не сняли secure keyguard. Повторный launch также оставил Activity под keyguard.
4. Screenshots получились полностью черными из-за secure lock-screen capture; XML UI dump — фактическое evidence keyguard. Это не классифицируется как пустой Main screen приложения.
5. В redacted app-slice logcat нет `FATAL EXCEPTION`. Последняя запись `Force removing ActivityRecord ... app died` соответствует выполненному cleanup `force-stop`, а не диагностированному crash.

## Requested coverage matrix

| Область | Verdict | Причина / evidence |
|---|---|---|
| Launch / Main screen | `BLOCKED` | Activity launch accepted, app surface закрыта keyguard; `01–03` screenshots/XML, app-slice logcat |
| Clock / date / city | `NOT OBSERVED` | Нельзя отличить app rendering от lock screen без unlock |
| Weather cards / redacted fixture path | `NOT OBSERVED` | Fixture UI path недоступен; live request не выполнялся |
| Presets | `NOT OBSERVED` | UI input недоступен |
| Timer start / cancel / overdue | `NOT OBSERVED` | UI input недоступен; active timer не создавался |
| Settings navigation / back | `NOT OBSERVED` | UI input недоступен |
| Background / foreground | `NOT PROVEN` | Наблюдался только launch под keyguard; meaningful app lifecycle smoke не выполнен |
| Cleanup | `PASS` | `force-stop`, `pm clear`, uninstall установленного debug package; package отсутствует в финальной проверке |
| Yandex live provider | `NOT RUN` | Нет live authorized key/request; PASS не заявляется |

## Findings and follow-up

- `BLOCKER (environment)`: target phone locked by secure PIN/biometric keyguard. Это внешний state blocker, не подтвержденный defect приложения.
- App defect read-only не диагностирован, поэтому indexed follow-up не назначается. Минимальное действие оператора — unlock того же устройства без передачи credentials, после чего повторить этот же smoke scope.
- Production code, tests, task cards, lifecycle statuses, scheduler checkpoint, terminal state, Memory Bank и `/mb-sync` не изменялись.

## Evidence paths

- [01-launch-main.png](evidence/01-launch-main.png), [01-launch-main.xml](evidence/01-launch-main.xml)
- [02-launch-unlocked.png](evidence/02-launch-unlocked.png), [02-launch-unlocked.xml](evidence/02-launch-unlocked.xml)
- [03-keyguard-blocker.png](evidence/03-keyguard-blocker.png), [03-keyguard-blocker.xml](evidence/03-keyguard-blocker.xml)
- [device-state-before-cleanup.txt](evidence/device-state-before-cleanup.txt)
- [device-logcat-app-slice-redacted.txt](evidence/device-logcat-app-slice-redacted.txt)
- [host-testDebugUnitTest.txt](evidence/host-testDebugUnitTest.txt)
- [cleanup-final-state.txt](evidence/cleanup-final-state.txt)

## Reviewer report format

- verdict: `BLOCKED`
- findings: secure keyguard prevented runtime coverage; no app crash or product defect established
- evidence_checked: authorized-device identity, install/start, screenshots, UI dumps, redacted logcat, host build/unit test, cleanup state
- risks_or_questions: target-device runtime behavior remains unverified until operator unlocks the same physical device; live-provider compatibility remains unverified by design
