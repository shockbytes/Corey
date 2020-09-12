package at.shockbytes.corey.ui.adapter.nutrition

import at.shockbytes.corey.data.nutrition.NutritionEntry

sealed class NutritionIntakeAdapterItem {

    data class Header(val time: String): NutritionIntakeAdapterItem()

    data class Intake(val entry: NutritionEntry): NutritionIntakeAdapterItem()
}
