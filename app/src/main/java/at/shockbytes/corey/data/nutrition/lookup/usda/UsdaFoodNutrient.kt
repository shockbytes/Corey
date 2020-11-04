package at.shockbytes.corey.data.nutrition.lookup.usda

import com.google.gson.annotations.SerializedName

data class UsdaFoodNutrient(
    @SerializedName("number") val code: Int,
    val name: String,
    val amount: Int,
    val unitName: String
)