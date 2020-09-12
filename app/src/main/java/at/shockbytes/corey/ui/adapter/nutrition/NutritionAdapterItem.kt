package at.shockbytes.corey.ui.adapter.nutrition

import at.shockbytes.corey.data.nutrition.NutritionEntry
import at.shockbytes.corey.data.nutrition.NutritionPerDay
import at.shockbytes.corey.data.nutrition.PhysicalActivity
import org.joda.time.format.DateTimeFormat

data class NutritionAdapterItem(
        private val nutritionPerDay: NutritionPerDay
) {

    val formattedDate: String
        get() {
            val fmt = DateTimeFormat.forPattern("MMM dd")
            return fmt.print(nutritionPerDay.date.dateTime)
        }

    /**
     * WeekOfYear, Year
     */
    val weekBundle: Pair<Int, Int>
        get() = Pair(nutritionPerDay.date.weekOfYear, nutritionPerDay.date.year)

    val intake: List<NutritionEntry>
        get() = nutritionPerDay.intake

    val burned: List<PhysicalActivity>
        get() = nutritionPerDay.burned

    val balance: CharSequence
        get() = nutritionPerDay.balance.formatted()

    companion object {

        fun fromNutritionPerDay(npd: NutritionPerDay): NutritionAdapterItem {
            return NutritionAdapterItem(npd)
        }
    }
}