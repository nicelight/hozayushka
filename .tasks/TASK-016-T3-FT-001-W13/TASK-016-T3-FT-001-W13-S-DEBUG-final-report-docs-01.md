---
description: Fresh read-only debug diagnosis for TASK-016 W13 smoke symptoms.
status: final
task_id: TASK-016-T3-FT-001-W13
stage_id: S-DEBUG
feature: FT-001
tier: T3
role: Reviewer
---
# Debug report — TASK-016-T3-FT-001-W13

## Симптомы и воспроизведение

Наблюдались два post-W14 generic-emulator симптома:

1. На idle Main Display tap по видимой второй preset-кнопке не перевёл timer в
   countdown.
2. Hold по выбранному `Khujand` не дал положительного перехода на Settings.

Текущий APK в smoke-артефакте и локальный
`app/build/outputs/apk/debug/app-debug.apk` имеют один SHA-256:
`d44418e6f2d970f7f986645bf4d5913fe279ad4420fc3ba36e582dfa902da553`.

Безопасная distinguishing-проба на `Tecno_Pova_6_API_35` / generic Android 15
была выполнена из public Foundation state `Settings: Khujand`, `Timer: IDLE`,
`Weather: 21C/cloud`:

- после перехода на обычный Main Display выполнен один idle tap по второй
  preset в координате `(2304,450)` и выдержана пауза 1 s;
- повторный public Foundation probe сообщил `Timer: IDLE elapsed=0
  remaining=0`, то есть ожидаемый `startPreset` не был подтверждён;
- затем выполнены два public weather-card taps по `(800,800)` с интервалом
  120 ms; повторный probe снова сообщил `Timer: IDLE`, что оставило известное
  disposable состояние очищенным;
- selected-city hold `(1100,500)` длительностью 1000 ms не дал положительного
  Settings evidence. `MainActivity` остаётся тем же Activity и потому один
  `dumpsys activity` сам по себе этот маршрут не различает. Попытка получить
  UI XML после hold не завершилась из-за того же `uiautomator` idle-state
  ограничения; приложение было public-relaunched, финальный focus —
  `MainActivity`.

Smoke-матрица дополнительно содержит повторные 800–1000 ms holds на
calibrated/visual city coordinates и screenshots 25–27 без Settings, а также
screenshots 10 и 28 без countdown после idle preset taps.

## Текущая попытка и change surface

- Current Execution Attempt: W13 Attempt 1, начат `2026-08-09 Asia/Dushanbe`.
- Indexed task: `TASK-016-T3-FT-001-W13`, tier T3, task record сейчас `done`;
  этот debug не меняет lifecycle.
- Task-owned W13 production/test surface: ровно
  `app/src/main/kotlin/com/hozayushka/app/display/DisplayCapability.kt`,
  `app/src/main/kotlin/com/hozayushka/app/app/MainActivity.kt` и
  `app/src/test/kotlin/com/hozayushka/app/DisplayProjectionTest.kt`.
- Actual W13 diff добавляет `MainDisplayTickerOwner`, Activity lifecycle
  forwarding, projection/card renderer и меняет общий
  `ActiveTimerCityTouchStream` на `ActiveCountdownTouchDispatcher`; этот
  dispatcher теперь вызывается из root, regenerated cards, city и preset
  listeners. Relevant source points: `DisplayCapability.kt:571-599`,
  `:634-652`, `:702-710`.
- W14 current-workspace additions находятся в Weather Context
  (`WeatherCapability.kt` и `WeatherContextTest.kt`); в `DisplayCapability.kt`
  и `MainActivity.kt` отдельного W14 touch/layout diff нет. W14 не является
  доказанным владельцем этих симптомов.
- Прочитаны W13 task card, `context.md`, `plan.md`, `progress.md`, `handoff.md`,
  `verification.md`, `red-verification.md`, executor/functional/semantic
  evidence, actual source diff, W12 comparison evidence и текущий
  `.tasks/EMULATOR-SMOKE-2026-08-10/`.

## Causal tracing

### Idle preset

- Symptom: после valid-coordinate public tap timer остаётся `IDLE`.
- Immediate observable cause: состояние не изменилось на `COUNTDOWN`; вызов
  `TimerCapability.startPreset()` через `handlePresetTap()` не подтверждён.
  В source `handlePresetTap()` имеет ожидаемую idle ветку
  (`DisplayCapability.kt:525-531`), а preset listener при
  `captured == false` передаёт событие в локальный `GestureDetector`
  (`:634-652`).
- First violated invariant: `lifecycle-map.md` — `idle` + short tap on valid
  preset MUST transition to `countdown`; это также
  `capability-interfaces.md#main-display-to-timer-and-alert`, где Main Display
  должен submit accepted preset start.
- Root-cause status: не подтверждён. Host tests покрывают fake dispatcher
  decisions и timer transitions, но не Android `View` dispatch
  `MotionEvent -> Button.OnTouchListener -> GestureDetector` и не ожидают
  delayed `onSingleTapConfirmed`. Поэтому они не различают ошибку listener,
  target bounds/coordinate, event timing и stale-window evidence.

### Selected-city long hold

- Symptom: smoke screenshots не показывают Settings после повторных holds.
- Immediate observable cause: положительный вызов `onOpenSettings()` / Settings
  surface не зафиксирован. Static route сама по себе корректна:
  `CityInteractionRouter.route(true, LONG_HOLD)` возвращает
  `OPEN_SETTINGS`, а `city.setOnLongClickListener` вызывает этот route
  (`DisplayCapability.kt:534-539`, `:702-707`).
- First violated invariant: `capability-interfaces.md#main-display-to-settings-
  and-location` / `FT-001-AC-005` — selected-city hold MUST open Settings;
  selected-city short tap MUST remain a no-op.
- Root-cause status: не подтверждён. При idle dispatcher возвращает `false` и
  source должен передать stream в native `TextView` long-click handling
  (`:594-599`, `:702-707`). При этом main hierarchy с фактическими bounds не
  получена: smoke фиксирует `uiautomator: could not get idle state`, а один
  historical W12 coordinate note `(1100,700)` не согласуется с визуальным
  city placement около y=500. Screenshots доказывают видимость текста, но не
  точную hit-target reachability.

## Эксперименты и отклонённые гипотезы

- Fresh public preset replay reproduced the idle result and was cleaned through
  the already-proven active-card double-tap route. No credentials, private
  storage read/write, `pm clear`, wipe/reset or production write was used.
- W13 host evidence is green (focused 9/9, full 56/56, clean build and diff
  check), but its touch test is an in-memory dispatcher test, not a public
  Android View probe. It cannot establish child hit delivery.
- W12 generic public evidence passed preset start and city hold on the same
  generic AVD class, so the W13 shared touch-listener change is a material
  suspect. Static inspection does not prove it: idle preset fallback still
  calls its detector and idle city fallback still returns to native View
  handling.
- App crash/ANR is not supported: smoke logcat has no `FATAL EXCEPTION`,
  `AndroidRuntime`, `ANR in` or `Application Not Responding`; current activity
  and window dumps show focused/resumed `MainActivity`.
- Pure stale-window explanation is weakened by that focus evidence, but not
  eliminated because `dumpsys` cannot expose the same-Activity Settings content
  and Main UI XML is unavailable while the ticker prevents an idle dump.
- W14 Weather Context projection memoization is not a sufficient explanation:
  no W14 Display/MainActivity touch diff or evidence connects it to either
  event path.

## Диагноз

The observed contract failures are real enough to require fresh public UI
evidence, but the available artifacts do not connect either symptom to a
specific production statement or prove that the ADB coordinate was inside the
intended child View. Therefore the classification remains inconclusive rather
than attributing the result to W13 ticker logic, ADB, uiautomator or a stale
window without event/bounds evidence.

## Минимальное направление коррекции

Сначала зафиксировать фактические Main View bounds и выдержку для delayed
single-tap (`> GestureDetector` double-tap timeout) в disposable public probe.
Если valid-bounds replay повторит симптомы, минимальное code direction —
изолировать idle child delivery от active-countdown capture: оставить
`ActiveCountdownTouchDispatcher` только для captured active streams, а idle
preset direct-detector и city native long-click path вернуть к W12-equivalent
delivery. Ticker lifecycle/card-reuse часть W13 не расширять и не менять до
такой связи evidence.

## Regression check

На fresh seeded generic emulator повторить public matrix с сохранёнными node
bounds и явным ожиданием: idle preset tap → visible countdown; selected-city
short tap → остаётся Main; selected-city hold 800–1000 ms → Settings content
(`Настройки`) и Back → Main; active single/double gestures и final public
cleanup → `Timer: IDLE`. Host dispatcher tests остаются дополнительной
проверкой и не заменяют Android View reachability. Зафиксировать также
focused/resumed window, raw input coordinates и timing каждого шага.

## Residual uncertainty and next owner

Остаются две неопределённости: фактическая hit-target boundary main child
Views и доставлен ли delayed/long gesture до конкретного listener. Следующий
владелец — W13 implementation/runtime-evidence owner: сначала закрыть bounds /
timing evidence gap; при подтверждённом app-side listener defect подготовить
contained `DisplayCapability` correction и затем отдельный execution/verification
handoff. Этот debug не запускал `/verify`, `/red-verify` или `/mb-sync` и не
менял implementation, tests, specs, task JSON, status либо scheduler.

DIAGNOSIS: INCONCLUSIVE
