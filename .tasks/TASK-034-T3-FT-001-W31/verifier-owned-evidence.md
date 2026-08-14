---
description: Independent verifier-owned functional evidence for TASK-034-T3-FT-001-W31.
status: final
task_id: TASK-034-T3-FT-001-W31
tier: T3
---
# Verifier-owned evidence — TASK-034-T3-FT-001-W31

## Independent physical observation

Only `adb -s 1156725456009666` was used. The target was an unlocked TECNO LI6:
`get-state=device`, focused `com.hozayushka.app/.app.MainActivity`,
`isKeyguardShowing=false`, screen ON, `mCurrentOrientation=1`, runtime frame
`2460x1080`. No emulator, AVD, QEMU, other serial, network, provider call or
credential was used.

Fresh verifier screenshot:
[physical-main-verify.png](physical-main-verify.png), SHA-256
`2ee86d4cbb11ad423f224c1c36c00d01c6388b3f09d2459eeee21205f1347ba2`.
Visual inspection shows the complete `00:00` string readable and contained in
the upper central region, all four card shells present, city/date above
Yesterday, weather illustrations secondary, and three separate controls on
the right without overlap.

The executor's claim-linked physical RED/GREEN pair was independently opened
and checked as supporting provenance: `physical-main-before.png` and
`physical-main-after.png` are both `2460x1080`, and the receipt reports
`650x201 -> 725x218` complete clock glyphs and `71x70 -> 45x43` largest icon
footprints on the same serial/frame. The before/after screenshots visibly
show the claimed hierarchy correction; the fresh screenshot above is the new
verifier-owned current-state observation.

## Independent host observation

Fresh clean build and focused test XML:
`app/build/test-results/testDebugUnitTest/TEST-com.hozayushka.app.DisplayProjectionTest.xml`.
The XML reports `tests=26`, `failures=0`, `errors=0` and records W31 geometry:

- `2460x1080`: clock region `[551,24,2208,405]`, four cards beginning at
  `y=405`, ordered slots, and three controls `[2208,186,2428,406]`,
  `[2208,430,2428,650]`, `[2208,674,2428,894]`.
- `1280x720`: clock region `[273,24,1028,272]`, four cards beginning at
  `y=272`, ordered slots, and three controls `[1038,36,1238,236]`,
  `[1038,260,1238,460]`, `[1038,484,1238,684]`.
- `NO_DATA`, partial and populated fixture output keeps all four ordered
  shells, preserves missing values as shells, and avoids layout shift.

## Boundary and gate observation

The W31 attempt's attributed behavior paths are exactly the two declared
boundary files. Current workspace `git diff --name-only` also contains broad
pre-existing migration/documentation dirt explicitly recorded by the executor;
those paths are not W31-attributed changes. Source review of the current
Display boundary found only Main Display geometry/presentation and test support
relevant to W31; Weather Context/provider, Timer & Alert lifecycle/dispatch,
fullscreen/runtime owner and public-boundary code remain outside the W31
behavior surface.

Fresh gate results are recorded in [host-gates.md](host-gates.md); all required
commands exited 0. The clean APK SHA-256 was
`60121bf8e5d4edc2da807efe7af968550c36dc06b63431f7fd05b76a95520064`.

## Claim mapping

- `FT-001-AC-002 / REQ-002 / REQ-023`: physical and host geometry support
  complete clock containment/dominance and materially smaller secondary icons.
- `FT-001-AC-002 / REQ-002 / REQ-005`: physical screenshot plus fresh fixture
  output support city/date placement and four ordered stable slots.
- `REQ-001`: physical observation records actual landscape fullscreen frame;
  no host-only result was promoted to runtime proof.
- Timer/Weather read-only regressions: focused/full host suites and exact
  boundary review support unchanged ownership; no provider or timer operation
  was invoked.
