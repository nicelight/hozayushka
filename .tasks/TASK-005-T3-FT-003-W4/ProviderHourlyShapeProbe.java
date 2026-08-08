import com.hozayushka.app.adapters.weather.ProviderCurrentWeather;
import com.hozayushka.app.adapters.weather.ProviderDailyWeather;
import com.hozayushka.app.adapters.weather.ProviderHourlyWeather;
import com.hozayushka.app.adapters.weather.ProviderWeatherData;
import com.hozayushka.app.adapters.weather.RedactedProviderPayload;
import com.hozayushka.app.adapters.weather.WeatherProvider;
import com.hozayushka.app.adapters.weather.WeatherProviderRequest;
import com.hozayushka.app.adapters.weather.WeatherProviderResult;
import com.hozayushka.app.settings.InMemorySettingsStateStore;
import com.hozayushka.app.settings.LocationContext;
import com.hozayushka.app.settings.SettingsCapability;
import com.hozayushka.app.weather.InMemoryWeatherCacheStore;
import com.hozayushka.app.weather.WeatherCapability;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import kotlin.Unit;

/** Read-only semantic probe for the accepted full-day provider hourly shape. */
public final class ProviderHourlyShapeProbe {
    public static void main(String[] args) {
        var settings = new SettingsCapability(
            new InMemorySettingsStateStore(),
            () -> Unit.INSTANCE
        );
        settings.saveFoundationLocation(
            new LocationContext("Test city", 40.0, 69.0, "Asia/Dushanbe")
        );

        var today = LocalDate.of(2024, 1, 2);
        var hourly = new ArrayList<ProviderHourlyWeather>();
        for (int day = 0; day < 2; day++) {
            for (int hour = 0; hour < 24; hour++) {
                hourly.add(new ProviderHourlyWeather(
                    today.plusDays(day),
                    LocalTime.of(hour, 0),
                    2,
                    "cloud"
                ));
            }
        }

        var data = new ProviderWeatherData(
            "Asia/Dushanbe",
            new ProviderCurrentWeather(2, 100.0, "cloud"),
            List.of(new ProviderDailyWeather(today, 2, 1, "cloud", "cloud", null)),
            hourly
        );
        WeatherProvider provider = request -> new WeatherProviderResult(
            new RedactedProviderPayload(2, "cloud"),
            request.hasCredential(),
            request.redactedCredential(),
            data
        );
        var weather = new WeatherCapability(
            settings,
            new InMemoryWeatherCacheStore(),
            provider
        );
        long now = Instant.parse("2024-01-02T12:00:00Z").toEpochMilli();

        var refresh = weather.refresh(
            WeatherProviderRequest.Companion.fromSyntheticProbe(),
            now
        );
        boolean slotsPresent = List.of(6, 9, 12, 15, 18, 21).stream().allMatch(hour ->
            hourly.stream().anyMatch(value ->
                value.getDate().equals(today) && value.getTime().equals(LocalTime.of(hour, 0))
            )
        ) && hourly.stream().anyMatch(value ->
            value.getDate().equals(today.plusDays(1)) && value.getTime().equals(LocalTime.MIDNIGHT)
        ) && hourly.stream().anyMatch(value ->
            value.getDate().equals(today.plusDays(1)) && value.getTime().equals(LocalTime.of(3, 0))
        );

        System.out.println("provider_hourly_count=" + hourly.size());
        System.out.println("accepted_slots_present=" + slotsPresent);
        System.out.println("refresh_result=" + (refresh == null ? "NULL" : "NON_NULL"));
        System.out.println(
            "hourly_projection=" + (weather.hourlyProjection(now) == null ? "NULL" : "NON_NULL")
        );

        if (!slotsPresent || refresh == null || weather.hourlyProjection(now) == null) {
            throw new AssertionError(
                "A complete provider day shape containing every accepted slot must normalize to an hourly projection"
            );
        }
    }
}
