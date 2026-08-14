import com.hozayushka.app.adapters.weather.ProviderCurrentWeather;
import com.hozayushka.app.adapters.weather.ProviderDailyWeather;
import com.hozayushka.app.adapters.weather.ProviderWeatherData;
import com.hozayushka.app.adapters.weather.RedactedProviderPayload;
import com.hozayushka.app.adapters.weather.WeatherProvider;
import com.hozayushka.app.adapters.weather.WeatherProviderFailure;
import com.hozayushka.app.adapters.weather.WeatherProviderId;
import com.hozayushka.app.adapters.weather.WeatherProviderRequest;
import com.hozayushka.app.adapters.weather.WeatherProviderResult;
import com.hozayushka.app.settings.LocationContext;
import com.hozayushka.app.settings.WeatherAccessReader;
import com.hozayushka.app.settings.WeatherProviderSelection;
import com.hozayushka.app.weather.InMemoryWeatherCacheStore;
import com.hozayushka.app.weather.WeatherCacheRecord;
import com.hozayushka.app.weather.WeatherCapability;
import com.hozayushka.app.weather.WeatherFreshness;
import com.hozayushka.app.weather.WeatherProjection;
import com.hozayushka.app.weather.WeatherRefreshResult;
import com.hozayushka.app.weather.WeatherRefreshTrigger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import kotlin.jvm.functions.Function1;

/**
 * Host-only Attempt-2 verifier probe. It uses deterministic fake providers,
 * in-memory state and a synthetic key that is never printed or persisted.
 */
public final class VerifierAttempt2IdentityMatrixProbe {
    private static final long NOW_MILLIS = 1_704_196_800_000L;
    private static final LocationContext LOCATION_A = location(
        "Identity A", 40.2833, 69.6167, "1221965"
    );
    private static final LocationContext LOCATION_B = location(
        "Identity B", 38.5358, 68.7791, "1221874"
    );

    private final List<String> failures = new ArrayList<>();
    private int checks;

    public static void main(String[] args) {
        VerifierAttempt2IdentityMatrixProbe probe = new VerifierAttempt2IdentityMatrixProbe();
        probe.run();
    }

    private void run() {
        for (WeatherProviderId initialProvider : WeatherProviderId.values()) {
            verifyLocationSwitchDuringFetch(initialProvider, true);
            verifyLocationSwitchDuringFetch(initialProvider, false);
            verifyProviderSwitchDuringFetch(initialProvider, true);
            verifyProviderSwitchDuringFetch(initialProvider, false);
        }
        for (WeatherProviderId provider : WeatherProviderId.values()) {
            verifyLocationCannotChangeBetweenRequestConstructionAndIdentityCapture(provider);
        }

        String result = failures.isEmpty() ? "PASS" : "FAIL";
        System.out.println(
            "attempt2_identity_matrix=" + result
                + "; scenarios=10"
                + "; checks=" + checks
                + "; failures=" + failures.size()
                + "; location_switch_success_failure=4"
                + "; provider_switch_success_failure=4"
                + "; request_capture_window=2"
                + "; network_used=false; device_used=false; credential_value_recorded=false"
        );
        for (String failure : failures) {
            System.out.println("failure=" + failure);
        }
        if (!failures.isEmpty()) {
            System.exit(1);
        }
    }

    private void verifyLocationSwitchDuringFetch(WeatherProviderId initialProvider, boolean staleSuccess) {
        String scenario = "location-" + id(initialProvider) + "-" + outcome(staleSuccess);
        MutableAccess access = new MutableAccess(LOCATION_A, selection(initialProvider));
        TwoStepProvider selected = new TwoStepProvider(
            initialProvider,
            () -> access.location = LOCATION_B,
            staleSuccess
        );
        GuardProvider other = new GuardProvider(other(initialProvider));
        InMemoryWeatherCacheStore store = new InMemoryWeatherCacheStore();
        WeatherCapability weather = capability(access, store, selected, other, initialProvider);

        WeatherRefreshResult baselineResult = weather.refreshIfNeeded(
            NOW_MILLIS, true, WeatherRefreshTrigger.LAUNCH
        );
        WeatherCacheRecord baseline = store.loadRecord();
        check(scenario, baselineResult != null && baseline != null, "baseline missing");
        check(scenario, baseline != null && baseline.getHistory().size() == 1, "baseline history size");

        WeatherRefreshResult staleResult = weather.refreshIfNeeded(
            NOW_MILLIS + 1L, true, WeatherRefreshTrigger.LOCATION_CHANGE
        );
        WeatherCacheRecord afterStale = store.loadRecord();
        WeatherProjection newIdentityProjection = weather.projection(NOW_MILLIS + 1L);

        check(scenario, staleResult == null, "stale response accepted");
        check(scenario, baseline != null && baseline.equals(afterStale), "cache/history mutated");
        check(scenario, newIdentityProjection.getFreshness() == WeatherFreshness.NO_DATA, "new location became FRESH");
        check(scenario, LOCATION_B.getCityLabel().equals(newIdentityProjection.getCityLabel()), "projection identity mismatch");
        check(scenario, weather.inlineErrorMessage() == null, "stale failure leaked to new location");
        check(scenario, selected.calls == 2, "selected provider call count");
        check(scenario, other.calls == 0, "other provider was called");

        access.location = LOCATION_A;
        WeatherProjection restored = weather.projection(NOW_MILLIS + 1L);
        check(scenario, restored.getFreshness() == WeatherFreshness.FRESH, "original cache no longer FRESH");
        check(scenario, hasTemperature(restored, 11), "original projection was replaced");
        check(scenario, store.loadRecord() != null && store.loadRecord().getHistory().size() == 1, "pressure history crossed location");
    }

    private void verifyProviderSwitchDuringFetch(WeatherProviderId initialProvider, boolean staleSuccess) {
        WeatherProviderId switchedProvider = other(initialProvider);
        String scenario = "provider-" + id(initialProvider) + "-to-" + id(switchedProvider) + "-" + outcome(staleSuccess);
        MutableAccess access = new MutableAccess(LOCATION_A, selection(initialProvider));
        TwoStepProvider selected = new TwoStepProvider(
            initialProvider,
            () -> access.selection = selection(switchedProvider),
            staleSuccess
        );
        GuardProvider other = new GuardProvider(switchedProvider);
        InMemoryWeatherCacheStore store = new InMemoryWeatherCacheStore();
        WeatherCapability weather = capability(access, store, selected, other, initialProvider);

        WeatherRefreshResult baselineResult = weather.refreshIfNeeded(
            NOW_MILLIS, true, WeatherRefreshTrigger.LAUNCH
        );
        WeatherCacheRecord baseline = store.loadRecord();
        check(scenario, baselineResult != null && baseline != null, "baseline missing");
        check(scenario, baseline != null && baseline.getHistory().size() == 1, "baseline history size");

        WeatherRefreshResult staleResult = weather.refreshIfNeeded(
            NOW_MILLIS + 1L, true, WeatherRefreshTrigger.PROVIDER_CHANGE
        );
        WeatherCacheRecord afterStale = store.loadRecord();
        WeatherProjection newIdentityProjection = weather.projection(NOW_MILLIS + 1L);

        check(scenario, staleResult == null, "stale response accepted");
        check(scenario, baseline != null && baseline.equals(afterStale), "cache/history mutated");
        check(scenario, newIdentityProjection.getFreshness() == WeatherFreshness.NO_DATA, "new provider became FRESH");
        check(scenario, weather.inlineErrorMessage() == null, "stale failure leaked to new provider");
        check(scenario, selected.calls == 2, "selected provider call count");
        check(scenario, other.calls == 0, "new/other provider was called");

        access.selection = selection(initialProvider);
        WeatherProjection restored = weather.projection(NOW_MILLIS + 1L);
        check(scenario, restored.getFreshness() == WeatherFreshness.FRESH, "original cache no longer FRESH");
        check(scenario, hasTemperature(restored, 11), "original projection was replaced");
        check(scenario, store.loadRecord() != null && store.loadRecord().getHistory().size() == 1, "pressure history crossed provider");
    }

    private void verifyLocationCannotChangeBetweenRequestConstructionAndIdentityCapture(
        WeatherProviderId provider
    ) {
        String scenario = "request-construction-to-identity-capture-" + id(provider);
        SwitchingLocationAccess access = new SwitchingLocationAccess(
            LOCATION_A,
            LOCATION_B,
            selection(provider)
        );
        CapturingSuccessProvider selected = new CapturingSuccessProvider(provider);
        GuardProvider other = new GuardProvider(other(provider));
        InMemoryWeatherCacheStore store = new InMemoryWeatherCacheStore();
        WeatherCapability weather = capability(access, store, selected, other, provider);

        WeatherRefreshResult result = weather.refreshIfNeeded(
            NOW_MILLIS, true, WeatherRefreshTrigger.LAUNCH
        );
        WeatherCacheRecord stored = store.loadRecord();
        WeatherProjection projection = weather.projection(NOW_MILLIS);

        boolean oldRequestRelabeledAsNewIdentity =
            result != null
                && selected.requestLatitude == LOCATION_A.getLatitude()
                && selected.requestLongitude == LOCATION_A.getLongitude()
                && stored != null
                && LOCATION_B.getCityLabel().equals(stored.getSnapshot().getCityLabel())
                && projection.getFreshness() == WeatherFreshness.FRESH
                && hasTemperature(projection, 42);

        check(scenario, !oldRequestRelabeledAsNewIdentity, "old-coordinate response accepted under new location identity");
        check(scenario, result == null, "changed request identity returned success");
        check(scenario, stored == null, "changed request identity updated cache/history");
        check(scenario, projection.getFreshness() == WeatherFreshness.NO_DATA, "changed request identity appeared FRESH");
        check(scenario, other.calls == 0, "other provider was called");
    }

    private WeatherCapability capability(
        WeatherAccessReader access,
        InMemoryWeatherCacheStore store,
        WeatherProvider selected,
        WeatherProvider other,
        WeatherProviderId selectedId
    ) {
        if (selectedId == WeatherProviderId.OPEN_METEO) {
            return new WeatherCapability(access, store, selected, other, null);
        }
        return new WeatherCapability(access, store, other, selected, null);
    }

    private void check(String scenario, boolean condition, String message) {
        checks += 1;
        if (!condition) {
            failures.add(scenario + ": " + message);
        }
    }

    private static boolean hasTemperature(WeatherProjection projection, int temperature) {
        return projection.getCards().stream()
            .anyMatch(card -> Integer.valueOf(temperature).equals(card.getTemperatureCelsius()));
    }

    private static String id(WeatherProviderId provider) {
        return provider == WeatherProviderId.OPEN_METEO ? "open_meteo" : "open_weather";
    }

    private static String outcome(boolean success) {
        return success ? "stale-success" : "stale-failure";
    }

    private static WeatherProviderId other(WeatherProviderId provider) {
        return provider == WeatherProviderId.OPEN_METEO
            ? WeatherProviderId.OPEN_WEATHER
            : WeatherProviderId.OPEN_METEO;
    }

    private static WeatherProviderSelection selection(WeatherProviderId provider) {
        return provider == WeatherProviderId.OPEN_METEO
            ? WeatherProviderSelection.OPEN_METEO
            : WeatherProviderSelection.OPEN_WEATHER;
    }

    private static ProviderWeatherData data(int temperature, double pressure, WeatherProviderId provider) {
        String condition = provider == WeatherProviderId.OPEN_METEO ? "wmo:3" : "owm:803";
        LocalDate today = LocalDate.of(2024, 1, 2);
        List<ProviderDailyWeather> daily = new ArrayList<>();
        for (long offset = -1; offset <= 2; offset += 1) {
            daily.add(new ProviderDailyWeather(
                today.plusDays(offset),
                temperature,
                temperature,
                condition,
                condition,
                null,
                null,
                null
            ));
        }
        return new ProviderWeatherData(
            "Asia/Dushanbe",
            new ProviderCurrentWeather(temperature, pressure, condition),
            daily,
            Collections.emptyList()
        );
    }

    private static WeatherProviderResult successResult(
        WeatherProviderId provider,
        WeatherProviderRequest request,
        int temperature,
        double pressure
    ) {
        String condition = provider == WeatherProviderId.OPEN_METEO ? "wmo:3" : "owm:803";
        return new WeatherProviderResult(
            new RedactedProviderPayload(temperature, condition),
            request.hasCredential(),
            request.redactedCredential(),
            data(temperature, pressure, provider),
            null,
            provider
        );
    }

    private static WeatherProviderResult failureResult(
        WeatherProviderId provider,
        WeatherProviderRequest request
    ) {
        return new WeatherProviderResult(
            new RedactedProviderPayload(0, ""),
            request.hasCredential(),
            request.redactedCredential(),
            null,
            WeatherProviderFailure.NETWORK,
            provider
        );
    }

    private static LocationContext location(
        String label,
        double latitude,
        double longitude,
        String cityId
    ) {
        return new LocationContext(
            label,
            latitude,
            longitude,
            "Asia/Dushanbe",
            "TJ",
            "Tajikistan",
            cityId,
            label,
            label
        );
    }

    private static final class MutableAccess implements WeatherAccessReader {
        private LocationContext location;
        private WeatherProviderSelection selection;
        private final String syntheticKey = Long.toHexString(System.nanoTime()) + "-probe";

        private MutableAccess(LocationContext location, WeatherProviderSelection selection) {
            this.location = location;
            this.selection = selection;
        }

        @Override
        public LocationContext currentLocation() {
            return location;
        }

        @Override
        public WeatherProviderSelection selectedWeatherProvider() {
            return selection;
        }

        @Override
        public <T> T withSelectedOpenWeatherApiKey(Function1<? super String, ? extends T> block) {
            if (selection != WeatherProviderSelection.OPEN_WEATHER) {
                return null;
            }
            return block.invoke(syntheticKey);
        }

        @Override
        public boolean hasWeatherApiKey() {
            return selection == WeatherProviderSelection.OPEN_WEATHER;
        }
    }

    private static final class SwitchingLocationAccess implements WeatherAccessReader {
        private final LocationContext beforeRequest;
        private final LocationContext afterRequest;
        private final WeatherProviderSelection selection;
        private final String syntheticKey = Long.toHexString(System.nanoTime()) + "-probe";
        private int locationReads;

        private SwitchingLocationAccess(
            LocationContext beforeRequest,
            LocationContext afterRequest,
            WeatherProviderSelection selection
        ) {
            this.beforeRequest = beforeRequest;
            this.afterRequest = afterRequest;
            this.selection = selection;
        }

        @Override
        public LocationContext currentLocation() {
            locationReads += 1;
            return locationReads == 1 ? beforeRequest : afterRequest;
        }

        @Override
        public WeatherProviderSelection selectedWeatherProvider() {
            return selection;
        }

        @Override
        public <T> T withSelectedOpenWeatherApiKey(Function1<? super String, ? extends T> block) {
            if (selection != WeatherProviderSelection.OPEN_WEATHER) {
                return null;
            }
            return block.invoke(syntheticKey);
        }

        @Override
        public boolean hasWeatherApiKey() {
            return selection == WeatherProviderSelection.OPEN_WEATHER;
        }
    }

    private static final class TwoStepProvider implements WeatherProvider {
        private final WeatherProviderId provider;
        private final Runnable secondCallHook;
        private final boolean secondCallSuccess;
        private int calls;

        private TwoStepProvider(
            WeatherProviderId provider,
            Runnable secondCallHook,
            boolean secondCallSuccess
        ) {
            this.provider = provider;
            this.secondCallHook = secondCallHook;
            this.secondCallSuccess = secondCallSuccess;
        }

        @Override
        public WeatherProviderId getProviderId() {
            return provider;
        }

        @Override
        public WeatherProviderResult fetch(WeatherProviderRequest request) {
            calls += 1;
            if (calls == 1) {
                return successResult(provider, request, 11, 1000.0);
            }
            secondCallHook.run();
            return secondCallSuccess
                ? successResult(provider, request, 42, 1010.0)
                : failureResult(provider, request);
        }
    }

    private static final class GuardProvider implements WeatherProvider {
        private final WeatherProviderId provider;
        private int calls;

        private GuardProvider(WeatherProviderId provider) {
            this.provider = provider;
        }

        @Override
        public WeatherProviderId getProviderId() {
            return provider;
        }

        @Override
        public WeatherProviderResult fetch(WeatherProviderRequest request) {
            calls += 1;
            throw new AssertionError("non-selected provider invoked");
        }
    }

    private static final class CapturingSuccessProvider implements WeatherProvider {
        private final WeatherProviderId provider;
        private double requestLatitude;
        private double requestLongitude;

        private CapturingSuccessProvider(WeatherProviderId provider) {
            this.provider = provider;
        }

        @Override
        public WeatherProviderId getProviderId() {
            return provider;
        }

        @Override
        public WeatherProviderResult fetch(WeatherProviderRequest request) {
            requestLatitude = request.getLatitude();
            requestLongitude = request.getLongitude();
            return successResult(provider, request, 42, 1010.0);
        }
    }
}
