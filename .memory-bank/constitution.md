---
description: Project Constitution — governing principles for AI-first development.
status: active
version: 1
project_principles: ratified
ratified: 2026-08-02
last_updated: 2026-08-02
---
# Project Constitution

## Purpose

This Constitution defines the non-negotiable principles for planning,
implementation, verification, and synchronization in this project. Product
behavior belongs in the Product Brief, PRD, requirements, and canonical specs;
workflow details belong in the existing tier and execution policies.

## Core Principles

### I. Junior Project, KISS First

- Project level: `junior` — a personal, single-user application without
  enterprise process requirements.
- Use the simplest implementation that satisfies accepted current requirements.
- DO NOT overengineer, add speculative scope, or introduce infrastructure for
  hypothetical future needs.

### II. Accepted Sources Before Implementation

- Derive work from explicit product, requirement, feature, task, spec, and
  workflow artifacts; do not invent product or architecture decisions.
- Unknown material decisions remain explicit questions or blockers.
- Use the schema-backed `T0|T1|T2|T3` task model and its existing verification
  routes; do not create a second lifecycle or risk model.

### III. Bounded Agent Autonomy and Human Checkpoints

- Agents may choose reversible, low-impact implementation tactics only inside
  accepted requirements and boundaries.
- Obtain operator confirmation before selecting or adding dependencies, making
  a substantial technical decision, or changing product/UX behavior, public
  contracts, architecture, data ownership, or security posture.
- After evidence is ready, obtain operator confirmation before marking every
  task `done`. This project rule also applies where a generic workflow would
  otherwise permit automatic or fast-lane closure.
- Preserve every stricter tier-specific checkpoint, including the required T3
  human checkpoint.

### IV. Definition of Done

A task is done only when:

- its accepted acceptance criteria are satisfied;
- applicable project-native build, lint, and test checks pass;
- tier-appropriate evidence is recorded without weakening meaningful checks;
- a physical-device check is completed when the required outcome cannot be
  established reliably by cheaper automated or host-side checks;
- affected durable documentation is synchronized at the workflow-defined
  boundary; and
- the operator explicitly confirms closure.

Do not require test categories or device checks that do not prove a concrete
requirement or regression risk.

### V. Product Non-Negotiables

- Timer countdown, restoration, completion, and audible alert behavior must be
  verified against the accepted product requirements before release acceptance.
- The user-provided weather API key remains local and must not be embedded in
  the APK, committed to source, or exposed through logs or evidence.
- Readability and acceptable operation on the target 1280×720 Android 11 device
  take precedence over heavy visual effects. Time remains the dominant visual
  element.
- Do not add blanket enterprise requirements for backward compatibility,
  formal accessibility, no-data-loss machinery, or generalized security work
  unless accepted product scope later requires them.

### VI. Durable Knowledge and Evidence

- `.memory-bank/` is the durable source for project WHY/WHERE and governing
  decisions; code remains implementation truth.
- Read the smallest sufficient authoritative context and follow direct
  task-linked canonical specs for execution and verification.
- Synchronize affected Memory Bank, task state, and evidence after meaningful
  work according to existing workflow boundaries.

## Governance Decisions

Accepted by the operator on 2026-08-02:

- project level `junior` with KISS and explicit prohibition of overengineering;
- confirmation before dependencies, substantial technical decisions, and every
  task closure;
- risk-based Definition of Done with device verification only when cheaper
  checks cannot prove the outcome;
- focused product non-negotiables without additional enterprise controls.

## Governance

- This Constitution has precedence over workflow defaults and generated plans.
- AGENTS.md, MBB, specs, contracts, states, testing, and workflow policies
  refine these principles and must not weaken or contradict them.
- Amendments require an explicit operator decision, rationale, a version update,
  and reconciliation of affected durable documents.

**Version**: 1 | **Ratified**: 2026-08-02 | **Last updated**: 2026-08-02
