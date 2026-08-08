# Redacted ADB snippets — partial Reviewer run

Run date: 2026-08-08, `Asia/Dushanbe`.

No credential was entered, read, printed, or copied. SharedPreferences were not inspected.

## Runtime identity

```text
serial=emulator-5554 state=device
avd=Tecno_Pova_6_API_35 boot_completed=1
runtime_model=sdk_gphone64_x86_64
runtime_manufacturer=Google
runtime_product=sdk_gphone64_x86_64
runtime_device=emu64xa
api=35 release=15 abi=x86_64 abilist=x86_64,arm64-v8a
wm_size=1080x2436 wm_density=393
app_override_bounds=2436x1080 rotation=ROTATION_90
```

Selected AVD configuration facts:

```text
hw.device.manufacturer=TECNO
hw.device.name=tecno_pova_6
hw.lcd.width=1080
hw.lcd.height=2436
hw.lcd.density=393
image.sysdir.1=system-images/android-35/google_apis/x86_64/
tag.id=google_apis
target=android-35
```

## Normal Main Display layout evidence

Compact extract from `adb -s emulator-5554 shell dumpsys activity top`, with
the child order resolved against the current Main Display source:

```text
MainActivity bounds: 0,0-2436,1080
main shell:          32,24-2404,1056
header:              0,0-2152,516
clock row:           0,0-2152,431
date:                0,431-2152,516
city gesture view:   0,516-2152,516
timer hint view:     0,516-2152,516
forecast message:    0,516-2152,516
weather row:         0,516-2152,1032
```

The city, timer hint, and forecast message therefore have zero rendered
height on this generic emulator. The screenshots independently show the date
followed immediately by the weather row, with no visible city or hint.

## Foundation probe and redacted fixture

```text
Status: ok
LaunchState: COLD
Activity: com.hozayushka.app/.app.MainActivity

weather_refreshed_redacted
Settings: Khujand
Timer: IDLE elapsed=0 remaining=0
Weather: 21C/cloud
```

Probe hierarchy at 2436x1080:

```text
Seed Settings:            48,643-2388,761
Refresh Weather Fixture:  48,761-2388,879
Start 1s Timer:           48,879-2388,997
Rehydrate Timer:          48,997-2388,1048
Audio Probe:              48,1048-2388,1048
Cancel and Reset:         48,1048-2388,1048
```

The last two controls have zero height and were not touch- or focus-reachable.

## Temporary lifecycle observation

```text
before background: countdown=01:46 pid=5050
HOME: topResumedActivity=com.google.android.apps.nexuslauncher/.NexusLauncherActivity
hot resume: countdown=01:40 pid=5050
forced screen-off observation: mWakefulness=Asleep
screen-on/dismiss-keyguard: countdown=01:33 pid=5050
final: topResumedActivity=com.hozayushka.app/.app.MainActivity
final: mWakefulness=Awake timer=IDLE (normal clock screenshot)
```

This proves only the observed generic-emulator foreground and screen-off/on
path. It does not establish process-death restoration or Samsung/custom-ROM
behavior.
