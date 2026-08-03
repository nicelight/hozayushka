---
description: Product Requirements Document.
status: draft
type: prd
clarification_status: complete
constitution_checked: true
last_updated: 2026-08-03
---
# Product Requirements Document: кухонные часы с погодой и таймерами

## Source Inputs

- [.memory-bank/analysis/product-brief.md](analysis/product-brief.md): текущий
  Product Brief с `Decision: proceed`.
- [`IDEA.md`](../IDEA.md): подробный пользовательский Design Brief и исходные
  правила поведения V1.
- [.memory-bank/analysis/brainstorming/BR-001.md](analysis/brainstorming/BR-001.md):
  принятые решения о температурной палитре и pseudo-glass.
- [.memory-bank/constitution.md](constitution.md): ратифицированные governing
  principles уровня `junior`, KISS, human checkpoints и Definition of Done.
- [API Яндекс Погоды](https://yandex.cloud/ru/marketplace/products/yandexweather/yandex-weather-api):
  актуальная официальная карточка интеграции; на 2026-08-02 заявляет прогноз до
  10 дней, принятый оператором как горизонт V1.
- [Почасовой прогноз Yandex Weather API](https://yandex.ru/dev/weather/doc/ru/concepts/forecast-rest):
  актуальный REST contract с `hours=true` и 24 hourly values для первых 2–3
  дней.
- [GeoNames Gazetteer dump](https://download.geonames.org/export/dump/):
  принятый источник мирового offline-каталога `cities15000` под CC BY 4.0.

## Product Summary

Персональное Kotlin Android-приложение превращает постоянно включённый
горизонтальный смартфон на кухне в спокойный glanceable display: крупные часы,
краткая погода и три preset-таймера доступны без навигационного шума. Время
остаётся главным элементом, а запуск частого таймера занимает одно касание.

## Goals

- Показывать текущее время так, чтобы оно читалось издалека на экране 1280×720.
- Давать краткий погодный контекст через четыре визуально различимые карточки.
- Запускать один из трёх preset-таймеров одним нажатием и не допускать случайной
  отмены активного отсчёта.
- Сохранять корректный таймер вне foreground и после временной остановки
  процесса приложения.
- Сохранять основной сценарий часов, таймеров и свежей cached weather без сети.
- Оставаться достаточно лёгким и предсказуемым для целевого Android 11
  устройства.

## Non-goals

- Публикация приложения или распространение APK в V1.
- Backend, cloud sync, accounts, multi-user или общий встроенный API key.
- Google Services, автозапуск после reboot и AMOLED pixel shifting.
- Погодная история за период до установки.
- Telegram-бот и Android TTS до V2.
- Тяжёлый realtime blur, refraction/lensing, background capture, динамические
  specular highlights или morphing-анимация.
- Дополнительные функции, настройки или элементы левой области без отдельного
  принятого решения оператора.

## Users / Actors

- **Владелец**: единственный пользователь, вручную запускающий приложение на
  стационарном кухонном смартфоне и взаимодействующий короткими касаниями,
  двойным тапом и удержанием.
- **Yandex Weather API**: внешний источник текущей погоды, давления и прогноза.
- **Android OS**: предоставляет время, lifecycle, сеть, хранение и звуковое
  окружение; reboot recovery не входит в V1.

## Functional Requirements

### Main display and clock

- `PRD-FR-001`: приложение работает только в landscape fullscreen, скрывает
  системные панели и удерживает экран включённым, пока приложение открыто.
- `PRD-FR-002`: основной экран показывает часы `HH:mm` без секунд; дата и город
  находятся слева, четыре погодные карточки — внизу слева направо, три круглые
  preset-кнопки — справа.
- `PRD-FR-003`: при доступной сети двоеточие часов плавно набирает яркость 3
  секунды и снижает её до 2% следующие 3 секунды. Без сети оно неподвижно на 38%
  максимальной яркости.
- `PRD-FR-004`: во время активного таймера двоеточие мигает дискретно: 382 мс
  видно, 618 мс скрыто.
- `PRD-FR-005`: город отображается маленьким текстом; дата состоит из числа
  `dd` и русского названия месяца в родительном падеже, без года и дня недели.
- `PRD-FR-006`: долгое удержание города открывает Settings. Если город не
  выбран, показывается «Выбрать город», а короткий тап также открывает Settings.
  Короткий тап по уже выбранному городу не выполняет действие.

### Weather cards and local history

- `PRD-FR-007`: основной экран всегда сохраняет порядок карточек: вчера,
  сегодня, завтра, послезавтра. «Сегодня» немного крупнее; «Вчера», «Завтра» и
  «Послезавтра» имеют одинаковый размер и немного меньше «Сегодня».
- `PRD-FR-008`: заполненная карточка показывает минималистичную weather
  illustration, температуру, календарное число и температурно-зависимый фон;
  словесные названия дня и состояния погоды не показываются.
- `PRD-FR-009`: знак температуры показывается только в диапазоне −4…+4 °C
  включительно; за пределами диапазона знак опускается, а холод/тепло кодируется
  цветом фона.
- `PRD-FR-010`: будущие карточки используют дневные данные днём и ночные данные
  после заката. Фаза Луны используется только при наличии поля API; иначе
  показывается обычная луна без локальных астрономических расчётов.
- `PRD-FR-011`: каждая целая температура −30…+47 °C использует свой HEX из
  принятой таблицы BR-001. Значения ниже и выше диапазона clamp к крайним
  цветам. Все 78 значений хранятся явно в одном compile-time source.
- `PRD-FR-012`: температура и стрелки давления используют общий статичный
  pseudo-glass из прозрачного fill и светлой/тёмной кромок.
- `PRD-FR-013`: текущая погода и давление обновляются после запуска, смены
  города и каждые 30 минут при доступной сети. Последний успешный результат
  хранится локально.
- `PRD-FR-014`: данные возрастом не более 24 часов считаются актуальными и
  доступны offline. Если последнее успешное обновление старше 24 часов, все
  четыре карточки становятся прозрачными контурами без данных.
- `PRD-FR-015`: приложение хранит собственную погодную историю за последние
  семь дней, начиная с установки, для карточки «Вчера» и тренда давления.
- `PRD-FR-016`: текущий тренд сравнивает давление примерно с 3 часами назад:
  0–1,5 мм рт. ст. — без стрелки, 1,6–3 — одна, более 3 — две. Если изменение за
  3 часа равно нулю, сравнение примерно с 12 часами назад показывает ровно одну
  стрелку при любом ненулевом изменении. При отсутствии данных стрелки нет.
- `PRD-FR-017`: карточка «Вчера» показывает 0, 1 или 2 стрелки по самому крупному
  штатно зарегистрированному изменению предыдущих суток.
- `PRD-FR-018`: состояние карточки «Вчера» до накопления первой истории остаётся
  стабильным: карточка остаётся на своём месте как прозрачный контур с
  календарной датой, но без температуры, weather illustration и стрелки.

### Multi-day forecast

- `PRD-FR-019A`: короткий тап по карточке «Сегодня» открывает отдельный hourly
  forecast screen при наличии почасовых данных.
- `PRD-FR-019B`: hourly forecast screen показывает восемь карточек двумя рядами
  по четыре. Первый ряд: 06:00, 09:00, 12:00, 15:00 текущего дня; второй ряд:
  18:00, 21:00 текущего дня, 00:00 и 03:00 следующего дня. Каждая карточка
  повторяет стиль «Послезавтра»: температурный фон, glass-температура и weather
  illustration без стрелки давления; вместо календарной даты показывается время
  слота.
- `PRD-FR-019C`: hourly forecast screen использует тот же exit flow, что и
  long-term screen: auto-close через 3 секунды без взаимодействия, одиночный
  тап отменяет auto-close и показывает подсказку, двойной тап закрывает, hold
  удерживает экран открытым, release сразу закрывает.
- `PRD-FR-019`: короткий тап по карточке «Завтра» или «Послезавтра» открывает
  один и тот же long-term forecast screen только при наличии сохранённого
  10-дневного прогноза.
- `PRD-FR-020`: forecast screen показывает 10 карточек двумя рядами по пять, без
  стрелок давления; каждая карточка содержит `dd`, фон температуры, температуру
  и weather illustration с тем же day/night правилом. Диапазон начинается с
  текущего календарного дня выбранного города и включает девять следующих дней.
- `PRD-FR-021`: без взаимодействия экран закрывается через 3 секунды. Одиночный
  тап отменяет auto-close и показывает «Дважды нажмите, чтобы закрыть»; двойной
  тап закрывает экран. Удержание оставляет экран открытым, отпускание сразу его
  закрывает.
- `PRD-FR-022`: при тапе по «Завтра» или «Послезавтра» без доступного прогноза
  forecast screen не открывается, снизу кратко появляется сообщение
  «Долгосрочный прогноз еще не подгрузился». При тапе по «Сегодня» без hourly
  data экран также не открывается, снизу появляется сообщение «Почасовой прогноз
  еще не подгрузился».

### Preset timers and alert

- `PRD-FR-023`: доступны три отдельно настраиваемых preset-таймера; одновременно
  активен только один. Начальные длительности: 3, 10 и 30 минут.
- `PRD-FR-024`: каждый preset принимает 0–99 часов, 0–59 минут и 0–59 секунд;
  итоговая длительность должна быть больше нуля.
- `PRD-FR-025`: подпись preset-кнопки показывает только старшую ненулевую
  единицу (`ч`, `м` или `с`) с округлением вниз. Кнопки имеют соответственно
  оранжевую, розовую и фиолетовую неоновую окантовку.
- `PRD-FR-026`: короткий тап по preset-кнопке немедленно запускает таймер;
  countdown занимает место больших часов, текущее время уменьшается и
  перемещается в сторону, активная кнопка светится.
- `PRD-FR-027`: таймер сохраняет корректный отсчёт при смене Activity,
  нахождении вне foreground, погасшем экране и временной остановке процесса.
  Восстановление после reboot не требуется.
- `PRD-FR-028`: одиночный тап во время countdown не отменяет его и временно
  показывает подсказку о двойном тапе; двойной тап в любом месте отменяет
  таймер и возвращает обычный основной экран.
- `PRD-FR-029`: после нуля активная кнопка разворачивается в полноэкранную
  неоновую область своего цвета; мигает знак `+`, а немигающий счётчик идёт
  вверх и показывает полное время с момента запуска.
- `PRD-FR-030`: в overdue state одинарный или двойной тап в любом месте
  отключает таймер и возвращает основной экран.
- `PRD-FR-031`: завершение запускает повторяющийся встроенный звук с плавным
  нарастанием в первые 5–10 секунд. Сигнал длится до ручного отключения, но не
  более 30 минут. Visual overdue state показывается всегда; звук воспроизводится
  только когда Android разрешает его с учётом silent mode и DND. V1 включает
  «Классический», «Колокольчик» и «Электронный»; default — «Классический».

### Settings and location

- `PRD-FR-032`: Settings позволяют вводить и менять личный Yandex Weather API
  key, выбирать страну и город, настраивать три preset duration, выбирать звук,
  менять app alert volume и glass intensity.
- `PRD-FR-033`: API key хранится локально, не встраивается в APK, не попадает в
  source, logs или verification evidence.
- `PRD-FR-034`: город по умолчанию — Худжанд, Таджикистан; выбранные координаты
  используются в запросе погоды. Settings сохраняют выбор страны и города, а
  bundled GeoNames `cities15000` охватывает все страны городами с населением
  более 15 000 человек и столицами. Выбор выполняется двумя последовательными
  offline-searchable списками: сначала страна, затем города выбранной страны;
  поиск не зависит от регистра. Список показывает русское название при его
  наличии, иначе canonical GeoNames name; search matches русское, canonical и
  ASCII names.
- `PRD-FR-035`: glass intensity имеет диапазон 0…1 и начальное значение 0.45.
  Slider обновляет live preview во время движения и сохраняет значение после
  окончания жеста.
- `PRD-FR-036`: preview использует production weather-card presentation,
  сегодняшнюю температуру либо fallback 24 °C, две перекрывающиеся стрелки и
  число температуры.
- `PRD-FR-037`: обязательные validation/error состояния Settings показываются
  inline и не используют modal dialogs: «API-ключ не указан», «Неверный
  API-ключ», «Нет подключения», «Город не найден», «Укажите время больше нуля».
  Значение, не прошедшее validation, не сохраняется; предыдущее корректное
  значение остаётся. Дополнительные settings без принятого решения не входят в
  V1.
- `PRD-FR-038`: каждое корректное изменение Settings сохраняется автоматически.
  В конце экрана находится кнопка со значком «назад»; она и системный Back
  возвращают на основной экран. Некорректное поле остаётся несохранённым с
  owning inline-error.
- `PRD-FR-039`: большие часы и основная дата используют timezone устройства.
  Weather dates, границы «сегодня/завтра», day/night selection и hourly slots
  используют timezone выбранного города из Yandex API.

## Non-functional Requirements

- `PRD-NFR-001`: целевая среда — Samsung GT-I9300I (`s3ve3gds`) с совместимой
  Android 11 custom ROM; основной язык UI — русский.
- `PRD-NFR-002`: часы остаются крупнейшим и визуально доминирующим элементом на
  1280×720; яркие карточки и pseudo-glass не должны снижать читаемость времени.
- `PRD-NFR-003`: основной UI неподвижен и не включает специальную защиту AMOLED
  от выгорания; этот риск принят источниками продукта.
- `PRD-NFR-004`: часы, timer lifecycle и отображение weather cache моложе 24
  часов не зависят от доступности сети.
- `PRD-NFR-005`: визуальные эффекты не используют тяжёлые realtime механизмы и
  не имеют приоритета над приемлемой работой на целевом устройстве.
- `PRD-NFR-006`: ручная device verification требуется для поведения или
  визуального результата, который нельзя надёжно доказать более дешёвой
  автоматической или host-side проверкой.

## Data / Domain Model

- **User Settings**: локальный API key, страна, город, координаты, три preset
  duration, выбранный alert sound, app alert volume и glass intensity.
- **Offline Location Entry**: country code, Russian/canonical country name,
  GeoNames city ID, Russian/canonical/ASCII city names и coordinates; runtime
  dataset содержит только записи и aliases, нужные для принятого каталога.
- **Weather Snapshot**: location, observation/update time, температура,
  давление, condition/day-night данные и доступные forecast fields.
- **Weather History**: до семи суток локальных snapshots и дневной strongest
  pressure trend для карточки «Вчера».
- **Forecast Day**: calendar date, day/night temperature and condition и
  optional moon-phase data в пределах принятого forecast horizon; первые два
  дня также предоставляют hourly values для выбранных восьми слотов.
- **Active Timer**: выбранный preset/color, configured duration, фактический
  start point и состояние `countdown|overdue`; при отсутствии таймера состояние
  `idle`.

Конкретные storage schemas, ownership boundaries и migration rules относятся к
SDD и не определяются этим PRD.

## UX / Interaction Flow

1. Пользователь открывает приложение и попадает на fullscreen main display.
2. Если Settings уже содержат location и API key, приложение показывает свежую
   или допустимую cached weather и пытается обновить её при доступной сети.
3. Тап по preset запускает countdown без промежуточного экрана.
4. Одиночный тап защищает работающий таймер от случайной отмены; двойной тап
   отменяет его после подсказки.
5. После нуля fullscreen overdue state и звук продолжаются до касания либо
   30-минутного предела звука.
6. Долгое удержание города открывает Settings; отсутствие города также делает
   короткий тап входом в Settings.
7. В Settings пользователь ищет и выбирает страну, затем ищет город только
   внутри выбранной страны; оба списка полностью работают offline.
8. Корректные изменения Settings сохраняются сразу; кнопка со значком «назад» в
   конце экрана или системный Back возвращают на main display.
9. Тап по «Сегодня» открывает восьмикарточный hourly screen при наличии данных.
10. Тап по «Завтра» или «Послезавтра» открывает один и тот же 10-дневный forecast
   screen при наличии данных; оба forecast screen используют одинаковый
   auto-close/tap/hold flow.

## Integrations / Dependencies

- Product integration target: Yandex Weather API с личным ключом пользователя,
  location coordinates и weather/forecast fields, достаточными для принятых
  карточек.
- Исходный Design Brief указывает endpoint
  `https://api.weather.yandex.ru/v2/forecast`, параметры `lat`/`lon` и заголовок
  `X-Yandex-Weather-Key`; актуальная официальная REST documentation подтверждает
  этот request shape и hourly response на 2026-08-03.
- Актуальный официальный REST contract поддерживает `hours=true` и возвращает
  24 hourly values для первых 2–3 дней, поэтому восемь принятых слотов текущего
  и следующего дня доступны без дополнительного weather source.
- Актуальная официальная карточка на 2026-08-02 заявляет прогноз до 10 дней, а
  не 14; V1 согласован с этим ограничением и использует 10 дней.
- Google Services и собственный backend запрещены. Конкретный offline city
  dataset — явно подтверждённый оператором GeoNames `cities15000` под CC BY
  4.0; build-time preparation использует только нужные русские/canonical/ASCII
  aliases из GeoNames alternate names, а не включает полный исходный архив в
  APK. Требуемая лицензией attribution показывается в Settings перед конечной
  кнопкой «назад».

## Edge Cases / Failure Handling

- Нет выбранного города: main display показывает «Выбрать город», weather cards
  не изображают недоступные данные, тап открывает Settings.
- Нет API key или запрос завершился ошибкой: часы и таймеры продолжают работать;
  допустимый cache сохраняется до 24 часов, после чего cards пустеют.
- Settings показывают точное контекстное inline-сообщение рядом с owning field
  или action; validation/API/network ошибки не открывают modal dialog и не
  уничтожают последнее корректное сохранённое значение.
- Нет сети: cached weather моложе 24 часов остаётся видимой, clock colon
  фиксируется на 38%, сетевые обновления не подменяются ошибочными данными.
- Нет достаточной pressure history: стрелки не показываются.
- До накопления первой локальной истории карточка «Вчера» сохраняет место и
  календарную дату, показывая только прозрачный контур.
- Временная остановка процесса во время таймера: восстановленный UI вычисляет
  правильное `countdown|overdue` состояние по сохранённому timer state.
- Reboot: автоматический restart или восстановление таймера не гарантируются V1.
- Неизвестное weather condition или отсутствующее optional API field не должно
  приводить к crash. Для неизвестного condition используется нейтральная
  иллюстрация облака без текстовой подписи; доступная температура и её фон
  сохраняются.
- Отсутствующее hourly поле не открывает недостоверный экран; пользователь
  получает сообщение «Почасовой прогноз еще не подгрузился».

## Acceptance Criteria

- `PRD-AC-001`: на 1280×720 часы `HH:mm` визуально доминируют и читаются с
  предполагаемой кухонной дистанции; landscape fullscreen сохраняется.
- `PRD-AC-002`: четыре weather cards соблюдают порядок, freshness и empty-state
  rules; «Сегодня» немного крупнее трёх одинаковых меньших карточек, offline
  cache моложе 24 часов отображается без сети, а first-run «Вчера» не вызывает
  layout shift и остаётся датированным пустым контуром.
- `PRD-AC-003`: temperature lookup воспроизводит все 78 принятых цветов и clamp
  крайних значений; glass intensity 0, 0.45 и 1 видимо меняет один и тот же
  pseudo-glass material без тяжёлых realtime effects.
- `PRD-AC-004`: каждый preset запускается одним тапом, одновременно работает
  только один timer, одиночный тап не отменяет countdown, двойной — отменяет.
- `PRD-AC-005`: после временной остановки процесса восстановленный timer
  показывает правильное оставшееся или полное elapsed time; по завершении
  отображается overdue state, а ограниченный 30 минутами звук запускается только
  когда его разрешают текущие Android silent/DND settings.
- `PRD-AC-006`: settings сохраняют все принятые значения, preview обновляется
  live, выбранный сигнал сохраняется из принятого набора, а API key отсутствует
  в APK/source/logs/evidence.
- `PRD-AC-006A`: каждое принятое Settings error condition показывает свой
  inline-текст, не открывает modal dialog и сохраняет предыдущее корректное
  значение после validation failure.
- `PRD-AC-006B`: country search offline и без учёта регистра фильтрует мировой
  список; после выбора страны city search показывает только её города и также
  не требует сети. Display предпочитает русский вариант с canonical fallback,
  а search совпадает с Russian/canonical/ASCII names.
- `PRD-AC-006C`: корректное изменение любого Settings value доступно после
  повторного открытия без отдельной save action; invalid value не заменяет
  сохранённое, а bottom back-icon button и system Back возвращают на main display.
- `PRD-AC-007`: forecast screen показывает 10 последовательных дней двумя
  рядами по пять и точно соблюдает auto-close/tap/hold flow при наличии данных;
  он одинаково открывается из «Завтра» и «Послезавтра», а при отсутствии
  прогноза не открывается и показывает принятое сообщение.
- `PRD-AC-007A`: hourly screen из «Сегодня» показывает ровно восемь принятых
  трёхчасовых слотов двумя рядами по четыре и использует тот же
  auto-close/tap/hold flow; hourly cards визуально совпадают с «Послезавтра»,
  кроме замены даты на время и отсутствующей стрелки; без hourly data экран не
  открывается и показывается принятое сообщение.
- `PRD-AC-008`: недоступность сети или weather service не нарушает часы,
  countdown, timer cancellation и overdue dismissal.
- `PRD-AC-009`: clock/date следуют timezone устройства, а weather day boundaries
  и hourly labels — API timezone выбранного города; переход даты в одном
  timezone не сдвигает данные другого.
- `PRD-AC-010`: неизвестный weather condition показывает нейтральное облако и
  сохраняет доступные temperature/color data без crash или придуманного текста.

## Verification Strategy

- Проверять чистую timer arithmetic, label formatting, temperature lookup,
  freshness, pressure thresholds и day/night selection автоматическими
  deterministic checks.
- Проверять persistence/recovery, weather mapping и failure handling через
  project-native integration checks с redacted fixtures, когда project scaffold
  и API contract будут выбраны с operator confirmation.
- Проверять target-device fullscreen, keep-screen-on, lifecycle/alarm behavior,
  sound ramp, readability и pseudo-glass только там, где host-side checks не
  доказывают результат.
- Использовать tier-appropriate evidence и запрашивать operator confirmation
  перед закрытием каждой последующей task согласно Constitution.

## Clarifications

### 2026-08-02 — Source consolidation

- Constitution check: passed against ratified `junior`/KISS principles.
- BR-001 decisions on the 78-color palette, clamp behavior, pseudo-glass and
  glass-intensity preview are incorporated without reinterpretation.
- No new operator product decision has been assumed during PRD bootstrap.

### 2026-08-02 — Clarification interview

- Forecast horizon: V1 использует доступные в актуальном Yandex Weather API 10
  дней; forecast screen показывает две строки по пять карточек. Исходное
  требование 14 дней заменено этим решением.
- Location coverage: V1 сохраняет отдельный выбор страны и города и использует
  bundled мировой offline-каталог.
- Location dataset: принят GeoNames `cities15000` — города с населением более
  15 000 человек плюс столицы, лицензия CC BY 4.0. Проверенный 2026-08-02 архив
  весит 3 304 030 байт, распакованный TSV — 8 398 411 байт; итоговый вклад в APK
  зависит от выбранного формата индекса.
- Selected-city tap: короткий тап по уже выбранному городу не выполняет
  действие; Settings открываются долгим удержанием, а weather refresh остаётся
  автоматическим.
- Missing long-term forecast: тап по «Завтра» или «Послезавтра» не открывает
  forecast screen и кратко показывает снизу «Долгосрочный прогноз еще не
  подгрузился».
- Timer sound policy: visual overdue state показывается всегда, но звук уважает
  системные silent mode и DND и воспроизводится только когда Android разрешает.
- Forecast entry points: «Завтра» и «Послезавтра» открывают один и тот же
  10-дневный screen и одинаково обрабатывают отсутствие данных.
- Hourly forecast: «Сегодня» открывает восемь карточек 06:00, 09:00, 12:00,
  15:00, 18:00, 21:00, 00:00 и 03:00 двумя рядами по четыре; последние два слота
  относятся к следующему дню. Выход совпадает с long-term forecast screen.

### 2026-08-03 — Clarification interview

- Missing hourly forecast: тап по «Сегодня» не открывает hourly screen и
  показывает снизу «Почасовой прогноз еще не подгрузился».
- Built-in sounds: V1 содержит «Классический», «Колокольчик» и «Электронный»;
  по умолчанию выбран «Классический».
- Settings errors: приняты контекстные inline-сообщения «API-ключ не указан»,
  «Неверный API-ключ», «Нет подключения», «Город не найден», «Укажите время
  больше нуля» без modal dialogs; validation failure не заменяет предыдущее
  корректное значение.
- First-run yesterday card: до накопления истории карточка остаётся на месте с
  прозрачным контуром и датой, без температуры, illustration и стрелки.
- Hourly card content: используется тот же visual style, что у карточки
  «Послезавтра» — температурный фон, glass-температура и weather illustration,
  без стрелки давления; дата заменена временем слота.
- Location selection: сначала используется регистронезависимый offline-search
  страны, затем такой же search по городам только выбранной страны.
- Location names: display предпочитает русское название и использует canonical
  GeoNames fallback; search сопоставляет Russian, canonical и ASCII aliases, а
  в APK включается только отфильтрованный runtime subset.
- Settings persistence and exit: корректные изменения auto-save; в конце экрана
  находится back-icon button, а system Back имеет то же действие. Invalid value
  остаётся несохранённым с inline-error.

### 2026-08-03 — Delegated simple decisions

- Operator delegated simple, low-impact PRD decisions to agent discretion under
  the ratified KISS Constitution; material scope/cost/key-behavior branches still
  require confirmation.
- Timezone ownership: clock/date use device timezone; weather dates, day/night
  and hourly slots use selected-city API timezone.
- Long-term range: 10 cards cover selected-city today plus the next nine days.
- Unknown weather condition: neutral cloud illustration, preserving available
  temperature and temperature color without text.
- GeoNames attribution is placed in Settings before the final back-icon button.

### 2026-08-03 — Post-clarification refinement

- Main weather-card sizing: «Сегодня» немного крупнее; «Вчера», «Завтра» и
  «Послезавтра» одинаковы между собой и немного меньше.

## Deferred Non-blocking Decisions

- Точное название приложения не влияет на decomposition и может быть выбрано
  оператором до packaging; текущий документ использует описательное название.

## Unresolved Blockers

- None.

PRD прошёл Constitution check, `clarification_status` установлен в `complete`;
handoff в `/spec-init` открыт.
