package com.hozayushka.app.settings

import android.content.Context
import java.io.InputStream
import java.util.Locale

/** Immutable GeoNames-derived record used only by the Settings owner. */
data class LocationCatalogEntry(
    val countryCode: String,
    val countryRussianName: String?,
    val countryCanonicalName: String,
    val cityId: String,
    val cityRussianName: String?,
    val cityCanonicalName: String,
    val cityAsciiName: String,
    val latitude: Double,
    val longitude: Double,
    val apiTimeZone: String,
) {
    val countryDisplayName: String
        get() = countryRussianName.takeIf { !it.isNullOrBlank() } ?: countryCanonicalName

    val cityDisplayName: String
        get() = cityRussianName.takeIf { !it.isNullOrBlank() } ?: cityCanonicalName

    fun matchesCity(query: String): Boolean = matches(query, cityRussianName, cityCanonicalName, cityAsciiName)

    fun toLocationContext(): LocationContext = LocationContext(
        countryCode = countryCode,
        countryLabel = countryDisplayName,
        cityId = cityId,
        cityLabel = cityDisplayName,
        canonicalCityName = cityCanonicalName,
        asciiCityName = cityAsciiName,
        latitude = latitude,
        longitude = longitude,
        apiTimeZone = apiTimeZone,
    )

    private companion object {
        fun matches(query: String, vararg values: String?): Boolean {
            val normalizedQuery = normalize(query)
            if (normalizedQuery.isEmpty()) return true
            return values.filterNotNull().any { normalize(it).contains(normalizedQuery) }
        }

        fun normalize(value: String): String = value.trim().lowercase(Locale.ROOT)
    }
}

data class CountryCatalogEntry(
    val code: String,
    val russianName: String?,
    val canonicalName: String,
) {
    val displayName: String
        get() = russianName.takeIf { !it.isNullOrBlank() } ?: canonicalName

    fun matches(query: String): Boolean {
        val normalized = query.trim().lowercase(Locale.ROOT)
        if (normalized.isEmpty()) return true
        return listOfNotNull(russianName, canonicalName, code)
            .any { it.lowercase(Locale.ROOT).contains(normalized) }
    }
}

/**
 * Read-only offline catalog. The app never mutates the parsed records and city
 * search cannot run without a selected country code.
 */
class BundledLocationCatalog private constructor(
    private val entries: List<LocationCatalogEntry>,
) {
    val countries: List<CountryCatalogEntry> = entries
        .distinctBy { it.countryCode }
        .map { entry ->
            CountryCatalogEntry(entry.countryCode, entry.countryRussianName, entry.countryCanonicalName)
        }
        .sortedBy { it.displayName }

    fun searchCountries(query: String): List<CountryCatalogEntry> = countries
        .filter { it.matches(query) }

    fun searchCities(countryCode: String?, query: String): List<LocationCatalogEntry> {
        if (countryCode.isNullOrBlank()) return emptyList()
        return entries
            .asSequence()
            .filter { it.countryCode.equals(countryCode, ignoreCase = true) }
            .filter { it.matchesCity(query) }
            .distinctBy { it.cityId }
            .sortedBy { it.cityDisplayName }
            .toList()
    }

    fun defaultLocation(): LocationCatalogEntry? = entries.firstOrNull { it.cityId == DEFAULT_KHUJAND_CITY_ID }

    companion object {
        private const val DEFAULT_KHUJAND_CITY_ID = "1514879"
        private const val EXPECTED_COLUMNS = 10

        fun fromAsset(context: Context, assetPath: String = "geonames/cities15000.tsv"): BundledLocationCatalog =
            context.assets.open(assetPath).use(::fromInputStream)

        fun fromInputStream(input: InputStream): BundledLocationCatalog =
            input.bufferedReader(Charsets.UTF_8).use { fromTsv(it.readText()) }

        fun fromTsv(content: String): BundledLocationCatalog {
            val parsed = content.lineSequence()
                .filter { it.isNotBlank() && !it.trimStart().startsWith("#") }
                .mapNotNull(::parseLine)
                .toList()
            require(parsed.isNotEmpty()) { "GeoNames catalog must contain at least one record" }
            return BundledLocationCatalog(parsed)
        }

        private fun parseLine(line: String): LocationCatalogEntry? {
            val columns = line.split('\t')
            if (columns.size != EXPECTED_COLUMNS) return null
            return runCatching {
                LocationCatalogEntry(
                    countryCode = columns[0].trim(),
                    countryRussianName = columns[1].trim().ifBlank { null },
                    countryCanonicalName = columns[2].trim(),
                    cityId = columns[3].trim(),
                    cityRussianName = columns[4].trim().ifBlank { null },
                    cityCanonicalName = columns[5].trim(),
                    cityAsciiName = columns[6].trim(),
                    latitude = columns[7].trim().toDouble(),
                    longitude = columns[8].trim().toDouble(),
                    apiTimeZone = columns[9].trim(),
                )
            }.getOrNull()
        }
    }
}
