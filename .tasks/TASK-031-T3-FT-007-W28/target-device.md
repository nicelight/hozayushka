---
description: Deferred target, device and audio evidence for W28.
status: supporting
---
# W28 target / device / audio route

Status: `DEFERRED`.

This attempt intentionally did not launch an emulator/AVD/QEMU, use a physical
device or adb, invoke live network/provider calls, read credentials, or run an
audio runtime. Host geometry and fake/host audio are not runtime or physical
audibility `PASS`.

Unavailable evidence:

- Samsung GT-I9300I / Android 11 custom-ROM `1280×720` fullscreen and actual
  text readability;
- custom-ROM lifecycle interruption/resume and system-bar behavior;
- physical audio audibility, silent/DND/route behavior and ramp/cap behavior.

Residual risk: the adaptive host geometry may still need target-specific
readability/fullscreen validation, and W23's physical audio route remains
unverified. No new product scope or target decision is selected here.
