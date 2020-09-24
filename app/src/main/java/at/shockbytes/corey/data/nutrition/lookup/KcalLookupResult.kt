package at.shockbytes.corey.data.nutrition.lookup

data class KcalLookupResult(
        val dataSource: LookupDataSource,
        val items: List<KcalLookupItem>
)
