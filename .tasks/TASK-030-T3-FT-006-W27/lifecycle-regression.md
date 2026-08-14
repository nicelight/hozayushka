# Lifecycle regression — attempt 1

Accepted `RED_NOT_APPLICABLE`: intentionally breaking Timer & Alert lifecycle
would falsify the existing contract and exceed this display-only task. The
alternative proof is fresh host regression plus exact source boundary review.

| Claim | Host observation | Result |
|---|---|---|
| AC-002 / REQ-011 one active timer | `TimerLifecycleTest.startingAnotherValidatedPresetReplacesTheSingleActiveRecord` reports one active presentation after replacing a validated preset; W27 only reads `timer.presetPresentationAt(now)` and `TimerSnapshot.activePresetSlot`. | PASS |
| AC-003 / REQ-013 protected gestures | `TimerLifecycleTest.singleTapPreservesCountdownAndDoubleTapCancels`, plus `DisplayProjectionTest.activeCountdownKeepsCityHoldAlongsideProtectedTimerTaps` and `activeCountdownDispatcherKeepsEveryCapturedSurfaceStreamToTerminalEvent`, keep single-tap hint/countdown and double-tap cancellation. Active surface routes through the existing dispatcher/detector. | PASS |
| AC-004 / REQ-014 temporary recovery | `TimerLifecycleTest.persistedStartAndDurationRehydrateCountdownOrOverdueAfterTemporaryInterruption` confirms countdown/overdue rehydration from the existing record; W27 refresh consumes `TimerSnapshot` and adds no lifecycle owner. | PASS |
| AC-005 / REQ-025 network independence | `TimerLifecycleTest.noProviderInputDoesNotAffectTimerAndAnyTapDismissesOverdue` uses only `InMemoryTimerStateStore`; countdown/overdue dismissal remains provider-independent. W27 skips weather-card binding while countdown and does not add a provider call. | PASS |

No timer state, arithmetic, persistence or lifecycle code was written by W27.
Target/custom-ROM interruption observation remains `DEFERRED`.
