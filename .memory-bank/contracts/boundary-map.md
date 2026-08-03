---
description: Lightweight responsibility and scope boundary notes for decomposition, implementation, and verification.
status: draft
last_updated: 2026-08-03
---
# Boundary Map

## Purpose
- Keep lightweight boundary notes that help agents avoid crossing ownership, responsibility, or write-scope lines during decomposition and task execution.
- Use this file as an existing contract/spec input when task records need `purpose`, `success_outcome`, `anti_goals`, `runtime_context.write_boundary`, `runtime_context.forbidden_scope`, or `runtime_context.stop_conditions`.

## Boundary Notes
| Boundary | Purpose | Direction | Owner | Known Constraints | Questions |
|---|---|---|---|---|---|
| User interaction -> local product state | Change accepted settings, start/cancel timer, choose location, open forecast views | inbound | Application | Single owner; valid Settings auto-save; invalid values retain the last valid value; API key stays local | Exact storage ownership and screen/module split are deferred to /spec-design. |
| Application -> weather provider | Read current weather, pressure, forecast and available hourly/day-night fields | outbound request / inbound response | Application integration boundary | Personal key is supplied by the user; cached data remains usable offline up to 24 hours; no backend or shared key | Exact field mapping, retry policy and provider contract belong to downstream SDD. |
| Application <-> Android OS | Obtain device time, lifecycle/network signals and permitted audio environment | bidirectional | Android OS for platform services; application for product behavior | Landscape fullscreen, keep-screen-on, temporary process stop recovery; reboot recovery is out of scope; silent/DND rules are OS-owned | Target custom ROM behavior and device verification route remain design/testing questions. |
| Bundled location data -> location selector | Provide offline country and scoped city search with coordinates and aliases | read-only | Application-owned packaged data | Country is selected first; city search is limited to that country; Google Services are excluded | Packaging/index format and attribution placement are implementation/design details already bounded by the PRD. |

## Preliminary Responsibility Hints

- The application owns user Settings, timer lifecycle, local weather cache/history and presentation state.
- The weather provider remains the external source for fetched weather/forecast values; the application owns freshness evaluation, local history and fallback behavior.
- Android OS owns lifecycle, device timezone, network availability and whether alert audio is allowed; the application owns the product-visible timer and overdue state.
- Detailed API, storage, module, schema and security mechanism decisions are intentionally not made in this pre-PRD map.

## Runtime Context Hints
- Write boundary hints: keep changes within the owning product concern (clock/display, weather/cache, timer/alert, settings/location, or forecast view); do not cross into backend, account, reboot-recovery or unaccepted V2 scope.
- Forbidden scope hints: no API key literals, logs/evidence containing secrets, Google Services, new network/backend boundary, or heavy realtime visual effects.
- Stop condition hints: stop and route back to product clarification if a change alters actors, forecast horizon, timer cancellation semantics, offline freshness, API-key handling, or V1 non-goals.

## Update Rules
- Keep entries evidence-backed and short.
- Do not add endpoint lists, OpenAPI details, request/response schemas, auth policy, error-code design, or implementation pseudocode here.
- Do not create new task fields for boundaries; link this file through existing task fields such as `source_artifacts`, `normative_inputs`, `constraints`, `invariants`, or `verification_targets`, and copy executable scope into `runtime_context` when needed.
