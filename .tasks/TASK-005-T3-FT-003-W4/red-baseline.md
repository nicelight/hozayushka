# Attempt 1 claim-scoped RED baseline

- claim mapping: FT-003-AC-001..005 / REQ-009, REQ-022, REQ-026
- command: `if rg -n 'HourlyForecast|hourlyProjection|openHourly|ForecastSessionState|forecast_unavailable' app/src/main app/src/test; then echo 'accepted FT-003 hourly surface unexpectedly exists'; exit 1; else echo 'RED: no accepted FT-003 hourly session, complete-hourly projection, or shared exit state in current scaffold'; fi`
- cwd: `/home/serg/Projects/Mobile_APPS/hozayushka`
- result: exit `0`; honest pre-implementation RED observed before production changes.
- observation: `RED: no accepted FT-003 hourly session, complete-hourly projection, or shared exit state in current scaffold`
- isolation: read-only source probe; no external side effect or secret input.
