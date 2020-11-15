package at.shockbytes.corey.ui.adapter.nutrition

import androidx.annotation.StringRes
import at.shockbytes.corey.data.nutrition.NutritionEntry

sealed class NutritionIntakeAdapterItem {

    data class Header(@StringRes val timeNameRes: Int) : NutritionIntakeAdapterItem()

    data class Intake(val entry: NutritionEntry) : NutritionIntakeAdapterItem()
}
