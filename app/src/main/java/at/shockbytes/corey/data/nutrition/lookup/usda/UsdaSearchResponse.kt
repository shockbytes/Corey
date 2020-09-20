package at.shockbytes.corey.data.nutrition.lookup.usda

data class UsdaSearchResponse(
        val totalHits: Int,
        val currentPage: Int,
        val totalPages: Int,
        val foods: List<Foods>
) {

    data class Foods(
            val fdcId: String,
            val description: String,
    )
}
