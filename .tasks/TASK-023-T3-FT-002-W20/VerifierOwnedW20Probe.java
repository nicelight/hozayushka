import com.hozayushka.app.adapters.weather.RedactedProviderPayload;
import com.hozayushka.app.adapters.weather.WeatherProvider;
import com.hozayushka.app.adapters.weather.WeatherProviderFailure;
import com.hozayushka.app.adapters.weather.WeatherProviderId;
import com.hozayushka.app.adapters.weather.WeatherProviderRequest;
import com.hozayushka.app.adapters.weather.WeatherProviderResult;
import com.hozayushka.app.settings.InMemorySettingsStateStore;
import com.hozayushka.app.settings.LocationContext;
import com.hozayushka.app.settings.SettingsCapability;
import com.hozayushka.app.settings.WeatherProviderSelection;
import com.hozayushka.app.timer.InMemoryTimerStateStore;
import com.hozayushka.app.timer.TimerCapability;
import com.hozayushka.app.timer.TimerGesture;
import com.hozayushka.app.timer.TimerGestureResult;
import com.hozayushka.app.timer.TimerSnapshot;
import com.hozayushka.app.weather.InMemoryWeatherCacheStore;
import com.hozayushka.app.weather.WeatherCacheRecord;
import com.hozayushka.app.weather.WeatherCapability;
import com.hozayushka.app.weather.WeatherFreshness;
import com.hozayushka.app.weather.WeatherRefreshTrigger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;

/** Disposable verifier-owned host probe; it never prints the synthetic key. */
public final class VerifierOwnedW20Probe {
    private static final long NOW = 1_704_196_800_000L;

    public static void main(String[] args) {
        final String syntheticKey = Integer.toUnsignedString(
                VerifierOwnedW20Probe.class.getName().hashCode(), 16
        ) + "-" + Long.toHexString(NOW);
        final LocationContext location = new LocationContext(
                "Verifier city", 40.0, 69.0, "UTC", "TJ", "Tajikistan", "verifier-city",
                "Verifier city", "Verifier city"
        );
        final InMemorySettingsStateStore settingsStore = new InMemorySettingsStateStore();
        final InMemoryWeatherCacheStore weatherStore = new InMemoryWeatherCacheStore();
        final RecordingProvider openMeteo = new RecordingProvider(WeatherProviderId.OPEN_METEO);
        final RecordingProvider openWeather = new RecordingProvider(WeatherProviderId.OPEN_WEATHER);
        final AtomicInteger saveCallbacks = new AtomicInteger();
        final WeatherCapability[] weatherRef = new WeatherCapability[1];

        SettingsCapability settings = new SettingsCapability(
                settingsStore,
                noop(),
                null,
                noop(),
                new Function0<Unit>() {
                    @Override public Unit invoke() {
                        saveCallbacks.incrementAndGet();
                        weatherRef[0].refreshIfNeeded(NOW, true, WeatherRefreshTrigger.PROVIDER_CHANGE);
                        return Unit.INSTANCE;
                    }
                }
        );
        settings.saveFoundationLocation(location);
        settings.updateWeatherProvider(WeatherProviderSelection.OPEN_WEATHER);
        weatherRef[0] = new WeatherCapability(
                settings, weatherStore, openMeteo, openWeather, null
        );

        require(weatherRef[0].refreshIfNeeded(NOW, true, WeatherRefreshTrigger.PROVIDER_CHANGE) == null,
                "missing-key refresh unexpectedly succeeded");
        require("OpenWeather: API-ключ не указан".equals(weatherRef[0].inlineErrorMessage()),
                "missing-key error not current");
        require(openWeather.calls == 0 && openMeteo.calls == 0, "missing-key called a provider");

        for (int i = 1; i <= syntheticKey.length(); i++) {
            require(!settings.hasStoredOpenWeatherApiKey(), "typed prefix committed a key");
            require(saveCallbacks.get() == 0, "typed prefix requested a save callback");
            require(openWeather.calls == 0 && openMeteo.calls == 0, "typed prefix called a provider");
        }

        require(settings.updateOpenWeatherApiKey(syntheticKey).getAccepted(), "valid commit rejected");
        require(saveCallbacks.get() == 1, "valid commit callback count != 1");
        require(openWeather.calls == 1 && openMeteo.calls == 0, "selected dispatch counts incorrect");
        require(openWeather.credentialPresent && "[REDACTED]".equals(openWeather.redactedCredential),
                "credential observation was not presence-only/redacted");
        require(weatherRef[0].inlineErrorMessage() == null, "successful refresh kept missing-key error");
        require(weatherRef[0].projection(NOW).getFreshness() == WeatherFreshness.FRESH,
                "successful refresh is not fresh");
        WeatherCacheRecord beforeFailure = weatherStore.loadRecord();
        require(beforeFailure != null && beforeFailure.getProvider() == WeatherProviderId.OPEN_WEATHER,
                "selected provider identity missing");
        require(location.getCityLabel().equals(beforeFailure.getSnapshot().getCityLabel()),
                "location identity changed");

        RecordingProvider inertOpenWeather = new RecordingProvider(WeatherProviderId.OPEN_WEATHER);
        AtomicInteger inertCallbacks = new AtomicInteger();
        SettingsCapability inert = new SettingsCapability(
                new InMemorySettingsStateStore(), noop(), null, noop(),
                new Function0<Unit>() {
                    @Override public Unit invoke() { inertCallbacks.incrementAndGet(); return Unit.INSTANCE; }
                }
        );
        require(!inert.updateOpenWeatherApiKey(syntheticKey).getAccepted(), "Open-Meteo valid key was accepted");
        inert.updateWeatherProvider(WeatherProviderSelection.OPEN_WEATHER);
        require(!inert.updateOpenWeatherApiKey("   ").getAccepted(), "blank key was accepted");
        require(!inert.updateOpenWeatherApiKey("bad key").getAccepted(), "invalid key was accepted");
        require(inertCallbacks.get() == 0 && !inert.hasStoredOpenWeatherApiKey(), "inert path changed state");
        inert.updateWeatherProvider(WeatherProviderSelection.OPEN_METEO);
        require(!inert.updateOpenWeatherApiKey(syntheticKey).getAccepted(), "Open-Meteo key path accepted");
        require(inertCallbacks.get() == 0 && inertOpenWeather.calls == 0, "inert path requested work");

        openWeather.failNext = true;
        require(settings.updateOpenWeatherApiKey(syntheticKey).getAccepted(), "repeated valid save rejected");
        require(saveCallbacks.get() == 2 && openWeather.calls == 2 && openMeteo.calls == 0,
                "repeated selected failure call counts incorrect");
        require(weatherRef[0].inlineErrorMessage() != null
                        && weatherRef[0].inlineErrorMessage().startsWith("OpenWeather:"),
                "selected failure was not provider-specific");
        require(beforeFailure.equals(weatherStore.loadRecord()), "selected failure replaced matching state");
        require(settings.selectedWeatherProvider() == WeatherProviderSelection.OPEN_WEATHER,
                "selected provider changed after failure");
        require(location.equals(settings.currentLocation()), "selected location changed after failure");

        List<Long> ticks = Arrays.asList(0L, 1_000L, 5_000L, 60_000L, 60_001L);
        TimerCapability control = new TimerCapability(new InMemoryTimerStateStore(), null, null, null);
        control.start(NOW, 60_000L);
        List<TimerSnapshot> controlTrace = trace(control, NOW, ticks);
        TimerGestureResult controlCancel = control.handleGesture(NOW + 1_000L, TimerGesture.DOUBLE_TAP);
        TimerCapability treatment = new TimerCapability(new InMemoryTimerStateStore(), null, null, null);
        treatment.start(NOW, 60_000L);
        openWeather.failNext = false;
        require(settings.updateOpenWeatherApiKey(syntheticKey).getAccepted(), "treatment activation rejected");
        List<TimerSnapshot> treatmentTrace = trace(treatment, NOW, ticks);
        TimerGestureResult treatmentCancel = treatment.handleGesture(NOW + 1_000L, TimerGesture.DOUBLE_TAP);
        require(controlTrace.equals(treatmentTrace) && controlCancel.equals(treatmentCancel),
                "timer control/treatment traces differ");

        TimerCapability overdueControl = new TimerCapability(new InMemoryTimerStateStore(), null, null, null);
        overdueControl.start(NOW, 1_000L);
        TimerGestureResult controlDismissal = overdueControl.handleGesture(NOW + 1_001L, TimerGesture.SINGLE_TAP);
        TimerCapability overdueTreatment = new TimerCapability(new InMemoryTimerStateStore(), null, null, null);
        overdueTreatment.start(NOW, 1_000L);
        require(settings.updateOpenWeatherApiKey(syntheticKey).getAccepted(), "overdue treatment activation rejected");
        TimerGestureResult treatmentDismissal = overdueTreatment.handleGesture(NOW + 1_001L, TimerGesture.SINGLE_TAP);
        require(controlDismissal.equals(treatmentDismissal), "overdue dismissal differs after refresh");

        System.out.println("probe=PASS");
        System.out.println("pre_commit=validation_only; saves=0; callback_requests=0; provider_calls=openweather:0,openmeteo:0");
        System.out.println("commit=one_callback; provider_calls=openweather:1,openmeteo:0; fresh=true; missing_key_cleared=true");
        System.out.println("inert=invalid_blank_openmeteo; callbacks=0; provider_calls=0");
        System.out.println("failure=selected_openweather_only; fallback=false; matching_state_preserved=true; identity_preserved=true");
        System.out.println("timer=control_treatment_equal; double_tap_cancel_equal=true; overdue_dismissal_equal=true");
        System.out.println("timer_control_trace=" + traceSummary(controlTrace));
        System.out.println("timer_treatment_trace=" + traceSummary(treatmentTrace));
        System.out.println("secret=synthetic_presence_only; request_observation=[REDACTED]; raw_value_recorded=false");
    }

    private static List<TimerSnapshot> trace(TimerCapability timer, long start, List<Long> ticks) {
        List<TimerSnapshot> result = new ArrayList<>();
        for (Long tick : ticks) result.add(timer.snapshotAt(start + tick));
        return result;
    }

    private static String traceSummary(List<TimerSnapshot> trace) {
        StringBuilder result = new StringBuilder();
        for (TimerSnapshot snapshot : trace) {
            if (result.length() > 0) result.append(",");
            result.append(snapshot.getState().name())
                    .append(":")
                    .append(snapshot.getElapsedMillis())
                    .append(":")
                    .append(snapshot.getRemainingMillis());
        }
        return result.toString();
    }

    private static Function0<Unit> noop() {
        return new Function0<Unit>() {
            @Override public Unit invoke() { return Unit.INSTANCE; }
        };
    }

    private static void require(boolean value, String message) {
        if (!value) throw new AssertionError(message);
    }

    private static final class RecordingProvider implements WeatherProvider {
        private final WeatherProviderId providerId;
        int calls;
        boolean credentialPresent;
        String redactedCredential;
        boolean failNext;

        private RecordingProvider(WeatherProviderId providerId) { this.providerId = providerId; }

        @Override public WeatherProviderId getProviderId() { return providerId; }

        @Override public WeatherProviderResult fetch(WeatherProviderRequest request) {
            calls++;
            credentialPresent = request.hasCredential();
            redactedCredential = request.redactedCredential();
            if (failNext) {
                failNext = false;
                return new WeatherProviderResult(
                        new RedactedProviderPayload(21, "cloud"), credentialPresent, redactedCredential,
                        null, WeatherProviderFailure.NETWORK, providerId
                );
            }
            return new WeatherProviderResult(
                    new RedactedProviderPayload(21, "cloud"), credentialPresent, redactedCredential,
                    null, null, providerId
            );
        }
    }
}
