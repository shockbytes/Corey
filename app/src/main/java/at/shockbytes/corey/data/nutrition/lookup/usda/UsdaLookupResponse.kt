package at.shockbytes.corey.data.nutrition.lookup.usda

data class UsdaLookupResponse(
    val fdcId: Long,
    val description: String,
    val foodNutrients: List<UsdaFoodNutrient>
) {
    val kcal: Int?
        get() = foodNutrients.find { it.code == UsdaApiNutrients.ENERGY.code }?.amount
}