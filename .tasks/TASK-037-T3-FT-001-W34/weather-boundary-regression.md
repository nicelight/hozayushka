---
task_id: TASK-037-T3-FT-001-W34
attempt: 1
status: supporting
---
# Weather Context/provider boundary regression

`RED_NOT_APPLICABLE`: W34 changes only Main Display View allocation. No
WeatherCapability/provider/adapter/cache/history/freshness/data-availability
path is a task-owned behavior write. The mixed fixture preserves an empty
Yesterday projection and does not synthesize weather values. Full host tests
passed (`119/119`), and the exact behavior diff is restricted to
`DisplayCapability.kt` plus `DisplayProjectionTest.kt`.
