---
description: Product Requirements Document.
status: draft
type: prd
clarification_status: complete
constitution_checked: true
last_updated: 2026-08-14
---
# Product Requirements Document: кухонные часы с погодой и таймерами

## Source Inputs

- Решение оператора от 2026-08-10: заменить Yandex Weather двумя явно
  выбираемыми providers — default Open-Meteo без пользовательского API key и
  optional OpenWeather с локальным ключом владельца; не выполнять
  cross-provider failover и не синтезировать недоступные данные.
- Принятый independent authority verdict от 2026-08-10
  `AUTHORIZED_BY_EXISTING_DECISIONS`: существующие `PRD-FR-019A/B`,
  `PRD-FR-022` и `PRD-AC-007A` уже определяют поведение при неполном
  OpenWeather hourly response и не требуют нового продуктового решения.
- [.memory-bank/analysis/product-brief.md](analysis/product-brief.md): исходный
  Product Brief с `Decision: proceed`; его Yandex-only wording и исходный
  forecast horizon superseded решением оператора от 2026-08-10 и этим PRD.
- [`IDEA.md`](../IDEA.md): подробный пользовательский Design Brief и исходные
  правила поведения V1; его Yandex-only integration wording superseded тем же
  решением оператора.
- [.memory-bank/analysis/brainstorming/BR-001.md](analysis/brainstorming/BR-001.md):
  принятые решения о температурной палитре и pseudo-glass.
- [.memory-bank/constitution.md](constitution.md): ратифицированные governing
  principles уровня `junior`, KISS, human checkpoints и Definition of Done.
- [Open-Meteo Weather Forecast API](https://open-meteo.com/en/docs): официальный
  Forecast API с current/hourly/daily variables и горизонтом до 16 дней.
- [Open-Meteo About](https://open-meteo.com/en/about) и
  [Terms](https://open-meteo.com/en/terms): официальный no-key режим и условия
  Free API для non-commercial use.
- [OpenWeather One Call API 3.0](https://openweathermap.org/api/one-call-3):
  официальный contract current weather, 48-hour hourly и 8-day daily forecast,
  обязательного API key и отдельной `One Call by Call` subscription.
- [OpenWeather One Call 3.0 transfer guide](https://openweathermap.org/api/one-call-transfer):
  официальные условия отдельной subscription и 1 000 calls/day free allowance.
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
- Backend, cloud sync, accounts, multi-user или общий встроенный API key для
  provider, которому требуются credentials.
- Автоматический cross-provider failover, объединение ответов разных providers
  в один forecast или скрытая смена выбранного provider при ошибке.
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
- **Open-Meteo**: default внешний weather provider без пользовательского API key
  для принятого персонального non-commercial use.
- **OpenWeather**: optional внешний weather provider, который владелец выбирает
  явно и использует со своим локальным API key и активной One Call 3.0
  subscription.
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

 сегодня, завтра, послезавтра. «Сегодня» немного крупнее; «Вчера», «Завтра» и
 «Послезавтра» имеют одинаковый размер и немного меньше «Сегодня».
 `PRD-FR-007`: основной экран всегда сохраняет порядок карточек: вчера,
  сегодня, завтра, послезавтра. Weather-card band занимает 25–30% полной
  высоты landscape-экрана; все четыре shell-карточки имеют одинаковую высоту и
  нижнее выравнивание, поэтому «Вчера» никогда не выше остальных. «Сегодня»
  может сохранять принятую более широкую/плотную presentation-вариацию без
  изменения равной высоты shell.
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
- `PRD-FR-013`: текущая погода и давление запрашиваются только у выбранного
  provider после запуска, смены города, смены provider и каждые 30 минут при
  доступной сети. Последний успешный normalized result выбранного provider
  хранится локально; ошибка не запускает запрос к другому provider и не
  заменяет cache частичным или чужим ответом.
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
  forecast screen при наличии полного набора принятых почасовых данных
  выбранного provider.
- `PRD-FR-019B`: hourly forecast screen показывает восемь карточек двумя рядами
  по четыре. Первый ряд: 06:00, 09:00, 12:00, 15:00 текущего дня; второй ряд:
  18:00, 21:00 текущего дня, 00:00 и 03:00 следующего дня. Каждая карточка
  повторяет стиль «Послезавтра»: температурный фон, glass-температура и weather
  illustration без стрелки давления; вместо календарной даты показывается время
  слота. Для optional OpenWeather набор считается полным только тогда, когда
  response содержит данные для всех восьми фиксированных city-local слотов,
  включая уже истёкшие слоты текущего дня.
- `PRD-FR-019C`: hourly forecast screen использует тот же exit flow, что и
  long-term screen: auto-close через 3 секунды без взаимодействия, одиночный
  тап отменяет auto-close и показывает подсказку, двойной тап закрывает, hold
  удерживает экран открытым, release сразу закрывает.
- `PRD-FR-019`: короткий тап по карточке «Завтра» или «Послезавтра» открывает
  один и тот же long-term forecast screen только при наличии полного
  provider-supported набора daily records: 10 для Open-Meteo или 8 для
  OpenWeather.
- `PRD-FR-020`: long-term forecast screen сохраняет 10 позиций двумя рядами по
  пять без стрелок давления. Для default Open-Meteo все 10 карточек охватывают
  текущий календарный день выбранного города и девять следующих дней. Для
  optional OpenWeather заполняются только 8 официально поддерживаемых daily
  records — текущий день и семь следующих; две последние позиции остаются
  unavailable/empty без температуры и weather illustration. Они не
  синтезируются и не заполняются данными Open-Meteo. Каждая доступная карточка
  содержит `dd`, температурный фон, температуру и weather illustration с тем же
  day/night правилом.
- `PRD-FR-021`: без взаимодействия экран закрывается через 3 секунды. Одиночный
  тап отменяет auto-close и показывает «Дважды нажмите, чтобы закрыть»; двойной
  тап закрывает экран. Удержание оставляет экран открытым, отпускание сразу его
  закрывает.
- `PRD-FR-022`: при тапе по «Завтра» или «Послезавтра» без полного
  provider-supported набора (10 Open-Meteo records или 8 OpenWeather records)
  forecast screen не открывается, снизу кратко появляется сообщение
  «Долгосрочный прогноз еще не подгрузился». Ожидаемое отсутствие девятой и
  десятой OpenWeather positions не считается неполнотой его поддерживаемого
  набора. При тапе по «Сегодня» без полного принятого hourly data экран также
  не открывается, снизу появляется сообщение «Почасовой прогноз еще не
  подгрузился». Для optional OpenWeather отсутствие любого из восьми слотов
  `PRD-FR-019B`, включая уже истёкший слот текущего дня, означает неполный
  hourly data set. Недостающий слот никогда не синтезируется и не заполняется
  данными Open-Meteo.

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

- `PRD-FR-032`: Settings позволяют выбрать `Open-Meteo|OpenWeather`; default —
  Open-Meteo. OpenWeather активируется только явным выбором владельца и требует
  его личный API key; Open-Meteo не показывает и не валидирует обязательный key.
  Settings также позволяют выбирать страну и город, настраивать три preset
  duration, выбирать звук, менять app alert volume и glass intensity, а также
  показывают требуемую Open-Meteo attribution рядом с уже принятой GeoNames
  attribution. Ошибка выбранного provider не меняет selection автоматически.
- `PRD-FR-033`: OpenWeather API key хранится локально, не встраивается в APK и
  не попадает в source, logs или verification evidence. Open-Meteo в принятом
  default Free API режиме не создаёт secret и не использует этот key.
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
  inline и не используют modal dialogs: «API-ключ не указан» и «Неверный
  API-ключ» применяются только к выбранному OpenWeather; «Нет подключения»,
  «Город не найден» и «Укажите время больше нуля» сохраняют прежний смысл.
  Provider/API failure честно идентифицирует выбранный provider и не сообщает
  об успешном fallback. Значение, не прошедшее validation, не сохраняется;
  предыдущее корректное значение остаётся. Дополнительные settings без
  принятого решения не входят в V1.
- `PRD-FR-038`: каждое корректное изменение Settings сохраняется автоматически.
  В конце экрана находится кнопка со значком «назад»; она и системный Back
  возвращают на основной экран. Некорректное поле остаётся несохранённым с
  owning inline-error.
- `PRD-FR-039`: большие часы и основная дата используют timezone устройства.
  Weather dates, границы «сегодня/завтра», day/night selection и hourly slots
  используют timezone выбранного города из ответа выбранного provider.

## Non-functional Requirements

- `PRD-NFR-001`: целевая среда — Samsung GT-I9300I (`s3ve3gds`) с совместимой
  Android 11 custom ROM; основной язык UI — русский.
 1280×720; яркие карточки и pseudo-glass не должны снижать читаемость времени.
 `PRD-NFR-002`: часы остаются крупнейшим и визуально доминирующим элементом на
  1280×720; clock zone занимает оставшиеся 70–75% высоты после
  25–30% weather-card band, а яркие карточки и pseudo-glass не должны снижать
  читаемость времени.
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

- **User Settings**: выбранный weather provider с default `Open-Meteo`,
  optional локальный OpenWeather API key, страна, город, координаты, три preset
  duration, выбранный alert sound, app alert volume и glass intensity.
- **Offline Location Entry**: country code, Russian/canonical country name,
  GeoNames city ID, Russian/canonical/ASCII city names и coordinates; runtime
  dataset содержит только записи и aliases, нужные для принятого каталога.
- **Weather Snapshot**: provider identity, location, observation/update time,
  температура, давление, condition/day-night данные и доступные forecast fields.
  Cache считается применимым только к тому provider, от которого был получен.
- **Weather History**: до семи суток локальных provider-identified snapshots и
  дневной strongest pressure trend для карточки «Вчера»; данные разных providers
  не смешиваются в одном trend comparison.
- **Forecast Day**: calendar date, day/night temperature and condition и
  optional moon-phase data в пределах provider-supported forecast horizon;
  Open-Meteo даёт 10 используемых daily positions, OpenWeather — 8 доступных и
  две явно unavailable/empty positions в общей десятипозиционной проекции.
- **Active Timer**: выбранный preset/color, configured duration, фактический
  start point и состояние `countdown|overdue`; при отсутствии таймера состояние
  `idle`.

Конкретные storage schemas, ownership boundaries и migration rules относятся к
SDD и не определяются этим PRD.

## UX / Interaction Flow

1. Пользователь открывает приложение и попадает на fullscreen main display.
2. Если Settings содержат location и валидные prerequisites выбранного provider
   (для default Open-Meteo key не нужен; для OpenWeather нужен локальный key и
   активная One Call 3.0 subscription), приложение показывает fresh или
   допустимую cached weather только этого provider и пытается обновить её при
   доступной сети.
3. Тап по preset запускает countdown без промежуточного экрана.
4. Одиночный тап защищает работающий таймер от случайной отмены; двойной тап
   отменяет его после подсказки.
5. После нуля fullscreen overdue state и звук продолжаются до касания либо
   30-минутного предела звука.
6. Долгое удержание города открывает Settings; отсутствие города также делает
   короткий тап входом в Settings.
7. В Settings пользователь оставляет default Open-Meteo либо явно выбирает
   OpenWeather и вводит свой API key; затем ищет и выбирает страну и город.
   Оба location-списка полностью работают offline.
8. Корректные изменения Settings сохраняются сразу; смена provider запрашивает
   refresh только у нового выбранного provider. Кнопка со значком «назад» в
   конце экрана или системный Back возвращают на main display.
9. Тап по «Сегодня» открывает восьмикарточный hourly screen только при наличии
   полного набора всех восьми фиксированных city-local слотов `PRD-FR-019B`.
   Если хотя бы один слот отсутствует, экран не открывается и показывает
   сообщение из `PRD-FR-022`.
10. Тап по «Завтра» или «Послезавтра» открывает один и тот же long-term screen с
    10 positions: Open-Meteo заполняет все 10, OpenWeather — только первые 8 и
    оставляет последние 2 unavailable/empty. Оба forecast screen используют
    одинаковый auto-close/tap/hold flow.

## Integrations / Dependencies

- Default integration target — Open-Meteo Weather Forecast API. Официальные
  docs на 2026-08-10 заявляют current/hourly/daily weather variables и forecast
  до 16 дней; приложение использует только принятые 10 daily positions.
  Официальный Free API не требует API key для non-commercial use. Его Terms
  ограничивают этот режим менее чем 10 000 calls/day, 5 000/hour и 600/minute,
  требуют CC BY 4.0 attribution и не гарантируют доступность или точность.
  Принятое персональное single-user приложение соответствует заявленному
  non-commercial use; 30-минутная application refresh policy сохраняется.
- Optional integration target — OpenWeather One Call API 3.0. Официальный
  current-and-forecast endpoint требует `appid={API key}` и предоставляет
  current weather, 48-hour hourly и 8-day daily forecast. Доступ требует
  OpenWeather account, личный key и отдельную `One Call by Call` subscription;
  на 2026-08-10 она включает 1 000 calls/day бесплатно, после чего provider
  тарифицирует дополнительные calls. Официальный transfer guide также указывает
  обязательные payment-card details для активации этой subscription.
- Provider выбирается владельцем в Settings. Запросы, cache identity, ошибки и
  forecast availability относятся только к выбранному provider; автоматический
  fallback, объединение daily/hourly records и подстановка данных другого
  provider запрещены. Рекомендация OpenWeather обновлять данные каждые 10 минут
  не меняет принятую 30-минутную application policy.
- Google Services и собственный backend запрещены. Конкретный offline city
  dataset — явно подтверждённый оператором GeoNames `cities15000` под CC BY
  4.0; build-time preparation использует только нужные русские/canonical/ASCII
  aliases из GeoNames alternate names, а не включает полный исходный архив в
  APK. Требуемые GeoNames и Open-Meteo attribution показываются в Settings
  перед конечной кнопкой «назад».

## Edge Cases / Failure Handling

- Нет выбранного города: main display показывает «Выбрать город», weather cards
  не изображают недоступные данные, тап открывает Settings.
- Для default Open-Meteo отсутствие API key не является ошибкой. Для выбранного
  OpenWeather отсутствующий/невалидный key или неактивная One Call subscription
  дают OpenWeather-specific failure без запроса к Open-Meteo.
- Ошибка выбранного provider не нарушает часы и таймеры, не меняет provider
  selection и не перезаписывает последний valid normalized cache этого
  provider. Совпадающий по provider cache остаётся доступен до 24 часов; cache
  другого provider не показывается как fallback, а при отсутствии подходящего
  cache cards переходят в принятое empty state.
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
- Для optional OpenWeather отсутствие хотя бы одного из восьми фиксированных
  city-local hourly слотов, включая уже истёкший слот текущего дня, не открывает
  недостоверный экран; пользователь получает сообщение «Почасовой прогноз еще
  не подгрузился». Недостающие слоты не синтезируются и не заимствуются у
  Open-Meteo.
- OpenWeather возвращает только 8 daily records: long-term screen честно
  показывает их в первых восьми positions, а девятую и десятую оставляет
  unavailable/empty; эти позиции не считаются provider error и никогда не
  синтезируются.

## Acceptance Criteria

 предполагаемой кухонной дистанции; landscape fullscreen сохраняется.
 `PRD-AC-001`: на 1280×720 часы `HH:mm` визуально доминируют и читаются с
  предполагаемой кухонной дистанции; landscape fullscreen сохраняется, а
  weather-card band занимает 25–30% высоты и clock zone 70–75%.
 rules; «Сегодня» немного крупнее трёх одинаковых меньших карточек, offline
 `PRD-AC-002`: четыре weather cards соблюдают порядок, equal-height/bottom-
  alignment, freshness и empty-state rules; «Сегодня» может быть шире или
  плотнее без более высокой shell-карточки, offline
  cache выбранного provider моложе 24 часов отображается без сети, cache другого
  provider не подставляется, а first-run «Вчера» не вызывает layout shift и
  остаётся датированным пустым контуром.
- `PRD-AC-003`: temperature lookup воспроизводит все 78 принятых цветов и clamp
  крайних значений; glass intensity 0, 0.45 и 1 видимо меняет один и тот же
  pseudo-glass material без тяжёлых realtime effects.
- `PRD-AC-004`: каждый preset запускается одним тапом, одновременно работает
  только один timer, одиночный тап не отменяет countdown, двойной — отменяет.
- `PRD-AC-005`: после временной остановки процесса восстановленный timer
  показывает правильное оставшееся или полное elapsed time; по завершении
  отображается overdue state, а ограниченный 30 минутами звук запускается только
  когда его разрешают текущие Android silent/DND settings.
- `PRD-AC-006`: Settings по умолчанию используют Open-Meteo без API key,
  позволяют явно выбрать OpenWeather и сохранить его личный локальный key,
  сохраняют остальные принятые значения, обновляют preview live и сохраняют
  выбранный сигнал, показывают Open-Meteo attribution; реальный OpenWeather key
  отсутствует в APK/source/logs/evidence.
- `PRD-AC-006A`: каждое принятое Settings error condition показывает свой
  inline-текст, не открывает modal dialog и сохраняет предыдущее корректное
  значение после validation failure; key errors применяются только к
  OpenWeather, а provider failure не сообщает о fallback и не меняет selection.
- `PRD-AC-006B`: country search offline и без учёта регистра фильтрует мировой
  список; после выбора страны city search показывает только её города и также
  не требует сети. Display предпочитает русский вариант с canonical fallback,
  а search совпадает с Russian/canonical/ASCII names.
- `PRD-AC-006C`: корректное изменение любого Settings value доступно после
  повторного открытия без отдельной save action; invalid value не заменяет
  сохранённое, а bottom back-icon button и system Back возвращают на main display.
- `PRD-AC-007`: long-term screen сохраняет 10 positions двумя рядами по пять и
  точно соблюдает auto-close/tap/hold flow. Open-Meteo заполняет 10
  последовательных дней; OpenWeather заполняет только официально
  поддерживаемые первые 8, а последние 2 остаются unavailable/empty без
  synthesized или cross-provider data. Экран одинаково открывается из «Завтра»
  и «Послезавтра» при полном provider-supported наборе, а при его отсутствии не
  открывается и показывает принятое сообщение.
- `PRD-AC-007A`: hourly screen из «Сегодня» показывает ровно восемь принятых
  трёхчасовых слотов двумя рядами по четыре и использует тот же
  auto-close/tap/hold flow; hourly cards визуально совпадают с «Послезавтра»,
  кроме замены даты на время и отсутствующей стрелки. Для optional OpenWeather
  полный hourly data set содержит все восемь фиксированных city-local слотов
  `PRD-FR-019B`; если любой слот отсутствует, включая уже истёкший слот текущего
  дня, экран не открывается и показывается «Почасовой прогноз еще не
  подгрузился». Недостающий слот не синтезируется и не заимствуется у
  Open-Meteo.
- `PRD-AC-008`: недоступность сети или выбранного weather provider не нарушает
  часы, countdown, timer cancellation и overdue dismissal; selection остаётся
  неизменным, last valid normalized cache выбранного provider сохраняется, а
  запрос или данные другого provider не используются автоматически.
- `PRD-AC-009`: clock/date следуют timezone устройства, а weather day boundaries
  и hourly labels — timezone выбранного города из selected-provider response;
  переход даты в одном
  timezone не сдвигает данные другого.
- `PRD-AC-010`: неизвестный weather condition показывает нейтральное облако и
  сохраняет доступные temperature/color data без crash или придуманного текста.

## Verification Strategy

- Проверять чистую timer arithmetic, label formatting, temperature lookup,
  freshness, pressure thresholds, day/night selection, default/explicit
  provider selection, 10-versus-8 daily horizon и отсутствие cross-provider
  fallback автоматическими deterministic checks.
- Проверять persistence/recovery, provider-specific weather mapping, cache
  identity и failure handling через project-native integration checks с
  Open-Meteo no-key fixtures и OpenWeather synthetic/redacted-key fixtures.
  Live provider calls, subscription activation и реальный key не входят в
  verification evidence.
- Статически проверять, что Open-Meteo path не требует credential, а
  OpenWeather key отсутствует в source, APK/resources, logs и evidence; условия
  использования и attribution проверяются по официальным links из Source Inputs.
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

- Forecast layout: принято 10 positions в двух строках по пять вместо исходных
  14. Provider basis и полнота этих positions superseded решением 2026-08-10:
  Open-Meteo заполняет 10, OpenWeather — 8 с двумя empty positions.
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
  long-term screen с 10 positions и одинаково обрабатывают отсутствие полного
  provider-supported набора.
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
  корректное значение. Решение 2026-08-10 ограничивает два key-specific
  сообщения выбранным OpenWeather.
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
- Long-term range: layout содержит 10 positions от selected-city today; решение
  2026-08-10 сохраняет 10 заполненных дней для Open-Meteo и оставляет последние
  две positions empty для 8-day OpenWeather response.
- Unknown weather condition: neutral cloud illustration, preserving available
  temperature and temperature color without text.
- GeoNames attribution is placed in Settings before the final back-icon button.

### 2026-08-03 — Post-clarification refinement

- Main weather-card sizing: «Сегодня» немного крупнее; «Вчера», «Завтра» и
  «Послезавтра» одинаковы между собой и немного меньше.

### 2026-08-14 — Accepted Main Display composition contract

- The operator superseded the prior vertical-card-size ambiguity: the weather
  band MUST occupy 25–30% of total landscape height, the clock zone MUST use
  the remaining 70–75%, and all four weather-card shells MUST have equal
  height and common bottom alignment. The accepted Today presentation may
  differ in width/density only; it MUST NOT make Yesterday or any other card
  taller. Main Display remains the shell owner; Weather Context and Timer &
  Alert ownership and runtime/provider behavior do not change.

### 2026-08-10 — Accepted provider migration

- Operator decision supersedes all Yandex-only and single-provider wording in
  earlier PRD inputs: supported selections are Open-Meteo and OpenWeather.
- Default provider is Open-Meteo. It requires no user API key for the accepted
  personal non-commercial use and preserves the existing 10-day experience,
  although the official Forecast API can return up to 16 days.
- Open-Meteo Free API Terms verified on 2026-08-10: non-commercial use, less
  than 10 000 calls/day, 5 000/hour and 600/minute, CC BY 4.0 attribution, and
  no availability/accuracy warranty.
- OpenWeather is optional and becomes selected only by explicit user action. It
  uses the owner's local API key and requires an OpenWeather account plus the
  separate `One Call by Call` subscription. Official sources verified on
  2026-08-10 state current weather, 48-hour hourly forecast, 8-day daily
  forecast and 1 000 calls/day included free; the transfer guide states that
  subscription activation requires payment-card details.
- OpenWeather's 8-day daily capability does not change the ten-position product
  layout: eight positions contain provider data and the last two remain
  unavailable/empty. No day is synthesized or borrowed from Open-Meteo.
- Refresh remains after launch, valid city/provider change and every 30 minutes.
  The selected provider does not change automatically after failure; only its
  matching last valid normalized cache may remain visible through the accepted
  24-hour freshness window.
- Constitution check: passed. The migration preserves the personal single-user
  boundary, KISS, offline clock/timer behavior and local-secret/redaction rules;
  those secret rules apply only to OpenWeather in the accepted provider set.

### 2026-08-10 — OpenWeather hourly completeness repair

- Accepted independent authority verdict: `AUTHORIZED_BY_EXISTING_DECISIONS`.
  `PRD-FR-019A/B`, `PRD-FR-022` and `PRD-AC-007A` already require the complete
  fixed eight-slot set and already define the unavailable behavior, so
  OpenWeather's next-48-hours limitation requires no new product decision.
- An optional OpenWeather hourly response is complete only when it contains all
  eight fixed `PRD-FR-019B` city-local slots. If any slot is absent, including
  an already elapsed current-day slot, `PRD-FR-022` applies: the hourly screen
  does not open and shows «Почасовой прогноз еще не подгрузился».
- Missing slots are never synthesized or borrowed from Open-Meteo. Constitution
  and remaining PRD validation pass; no unresolved blocker remains.

## Deferred Non-blocking Decisions

- Точное название приложения не влияет на decomposition и может быть выбрано
  оператором до packaging; текущий документ использует описательное название.

## Unresolved Blockers

- None.

PRD прошёл Constitution check, `clarification_status` установлен в `complete`;
после APPROVE `/review-feat-plan` следующий owner — `/spec-design`.
