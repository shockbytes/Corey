package at.shockbytes.corey.data.nutrition.lookup.usda

data class UsdaSearchResponse(
    val totalHits: Int,
    val currentPage: Int,
    val totalPages: Int,
    val foods: List<Foods>
) {

    val fdcIds: List<String>
        get() = foods.map { it.fdcId }

    data class Foods(
        val fdcId: String,
        val description: String,
        val additionalDescriptions: String
    )
}
