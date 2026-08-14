import com.hozayushka.app.adapters.weather.ProviderCurrentWeather;
import com.hozayushka.app.adapters.weather.ProviderDailyWeather;
import com.hozayushka.app.adapters.weather.ProviderWeatherData;
import com.hozayushka.app.adapters.weather.RedactedProviderPayload;
import com.hozayushka.app.adapters.weather.WeatherProvider;
import com.hozayushka.app.adapters.weather.WeatherProviderId;
import com.hozayushka.app.adapters.weather.WeatherProviderRequest;
import com.hozayushka.app.adapters.weather.WeatherProviderResult;
import com.hozayushka.app.settings.InMemorySettingsStateStore;
import com.hozayushka.app.settings.LocationContext;
import com.hozayushka.app.settings.SettingsCapability;
import com.hozayushka.app.weather.InMemoryWeatherCacheStore;
import com.hozayushka.app.weather.WeatherCacheRecord;
import com.hozayushka.app.weather.WeatherCapability;
import com.hozayushka.app.weather.WeatherFreshness;
import com.hozayushka.app.weather.WeatherProjection;
import com.hozayushka.app.weather.WeatherRefreshTrigger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;

/** Host-only verifier probe. It performs no network, Android runtime, or credential operation. */
public final class VerifierResponseIdentityProbe {
    private static final long NOW_MILLIS = 1_704_196_800_000L;

    public static void main(String[] args) {
        Function0<Unit> noOp = () -> Unit.INSTANCE;
        SettingsCapability settings = new SettingsCapability(
            new InMemorySettingsStateStore(),
            noOp,
            null,
            noOp
        );
        LocationContext requestedLocation = location("Requested city", 40.2833, 69.6167, "1221965");
        LocationContext selectedBeforeResponse = location("New selected city", 38.5358, 68.7791, "1221874");
        settings.saveFoundationLocation(requestedLocation);

        WeatherProvider openMeteo = new WeatherProvider() {
            @Override
            public WeatherProviderId getProviderId() {
                return WeatherProviderId.OPEN_METEO;
            }

            @Override
            public WeatherProviderResult fetch(WeatherProviderRequest request) {
                settings.saveFoundationLocation(selectedBeforeResponse);
                ProviderWeatherData data = dataForRequestedLocation();
                return new WeatherProviderResult(
                    new RedactedProviderPayload(42, "wmo:3"),
                    false,
                    null,
                    data,
                    null,
                    WeatherProviderId.OPEN_METEO
                );
            }
        };
        WeatherProvider mustNotRun = new WeatherProvider() {
            @Override
            public WeatherProviderId getProviderId() {
                return WeatherProviderId.OPEN_WEATHER;
            }

            @Override
            public WeatherProviderResult fetch(WeatherProviderRequest request) {
                throw new AssertionError("non-selected provider was invoked");
            }
        };

        InMemoryWeatherCacheStore cacheStore = new InMemoryWeatherCacheStore();
        WeatherCapability weather = new WeatherCapability(
            settings,
            cacheStore,
            openMeteo,
            mustNotRun,
            null
        );
        weather.refreshIfNeeded(NOW_MILLIS, true, WeatherRefreshTrigger.LAUNCH);
        WeatherProjection currentProjection = weather.projection(NOW_MILLIS);
        WeatherCacheRecord stored = cacheStore.loadRecord();

        boolean oldResponseRelabeledAsNewLocation =
            currentProjection.getFreshness() == WeatherFreshness.FRESH
                && selectedBeforeResponse.getCityLabel().equals(currentProjection.getCityLabel())
                && currentProjection.getCards().stream()
                    .anyMatch(card -> Integer.valueOf(42).equals(card.getTemperatureCelsius()));
        boolean oldPressureRelabeledAsNewHistory =
            stored != null
                && selectedBeforeResponse.getCityLabel().equals(stored.getSnapshot().getCityLabel())
                && stored.getHistory().size() == 1
                && stored.getLocationIdentity().equals(stored.getHistory().get(0).getLocationIdentity());

        System.out.println(
            "response_identity_probe=" + (oldResponseRelabeledAsNewLocation ? "FAIL" : "PASS")
                + "; requested_location_changed_before_response=true"
                + "; selected_projection_fresh=" + (currentProjection.getFreshness() == WeatherFreshness.FRESH)
                + "; selected_city_label_matches_new="
                + selectedBeforeResponse.getCityLabel().equals(currentProjection.getCityLabel())
                + "; old_pressure_labeled_as_new_history=" + oldPressureRelabeledAsNewHistory
                + "; credential_used=false; network_used=false"
        );
        if (oldResponseRelabeledAsNewLocation || oldPressureRelabeledAsNewHistory) {
            System.exit(1);
        }
    }

    private static ProviderWeatherData dataForRequestedLocation() {
        LocalDate today = LocalDate.of(2024, 1, 2);
        List<ProviderDailyWeather> daily = new ArrayList<>();
        for (long offset = -1; offset <= 2; offset += 1) {
            daily.add(new ProviderDailyWeather(
                today.plusDays(offset),
                42,
                41,
                "wmo:3",
                "wmo:3",
                null,
                null,
                null
            ));
        }
        return new ProviderWeatherData(
            "Asia/Dushanbe",
            new ProviderCurrentWeather(42, 1000.0, "wmo:3"),
            daily,
            Collections.emptyList()
        );
    }

    private static LocationContext location(String label, double latitude, double longitude, String cityId) {
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
}
