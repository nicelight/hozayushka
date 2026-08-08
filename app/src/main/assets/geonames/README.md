# GeoNames application asset boundary

`cities15000.tsv` is the immutable, tab-separated runtime catalog consumed by
`BundledLocationCatalog`. It is the transformed GeoNames `cities15000` subset
(the source archive is not shipped); each row contains country names, city
Russian/canonical/ASCII aliases, coordinates and the selected-city API
timezone. The Settings owner reads it offline and never mutates the asset.

The Settings surface renders the required GeoNames CC BY 4.0 attribution before
the final back-icon action. No network or Google Services boundary is used for
country/city search; provider refresh remains a separate Weather Context path.
