---
description: Honest Attempt 1 pre-change public baseline for TASK-015.
status: supporting-only
task_id: TASK-015-T3-FT-001-W12
attempt: 1
---
# Attempt 1 pre-change baseline

## Claim mapping

- `FT-001-AC-005 / REQ-004`: selected-city idle short tap remained on Main
  Display; active-city 800 ms hold opened existing Settings; system Back
  returned to the still-active countdown.
- `REQ-013` regression guard: non-city weather-card single tap preserved the
  countdown/hint path; the required current public double tap at a 120 ms
  interval returned to idle by the approximately 350 ms checkpoint.

## Basis and isolation

- Current pre-change source was built with `./gradlew clean assembleDebug`.
- APK SHA-256: `5cfb17a4c3d192b44583dce678b342588361bac35fb3bfd5ddf97e84820a7b80`.
- APK was installed on the authorized `emulator-5554` running
  `Tecno_Pova_6_API_35`, generic Google Android 15/API35 x86_64, using only
  public Activity/probe and UI input routes.
- Synthetic/redacted Khujand/weather fixture was retained. No credentials,
  SharedPreferences or capability-private state was read or mutated.

## Observations

- `attempt-1-red-idle-city-short-tap.png` SHA-256
  `e1c31e1ee4d8b4eca69c3be2830909de7d4a8af026d3249f5e1ab2de759f24c6`:
  Main Display remained visible after selected-city idle short tap.
- `attempt-1-pre-city-hold-settings.png` SHA-256
  `25ee1372023ce9d6eb56f3c4c4a5e96f9257891a5a0b185689a59203290c294b`:
  public 800 ms city hold reached Settings; visible text contained only the
  accepted redacted `API-ключ не указан` state.
- `attempt-1-pre-city-hold-back-countdown.png` SHA-256
  `927980e9706de557bb2cf9ed7f8a0f70bd0fa36341d363e62d925fe40c3aa494`:
  system Back returned to the active countdown.
- `attempt-1-red-noncity-double-350ms.png` SHA-256
  `7818cf19066b634ac819a30030c5476c08fcf9b4535dfb73e0524b8d1949dbc1`:
  current run showed idle Main Display by the requested checkpoint. The W11
  historical failure is not claimed as this attempt's RED.

## Evidence classification

This artifact is execution supporting evidence only. The pre-implementation
GREEN prevented manufacturing a RED for the already-green observed path; the
accepted W12 implementation/integration delta was still completed, and fresh
post-change GREEN plus independent `/verify` remain required.
