package at.shockbytes.corey.ui.adapter.nutrition

import at.shockbytes.corey.data.nutrition.NutritionPerDay
import at.shockbytes.corey.data.nutrition.PhysicalActivity
import org.joda.time.format.DateTimeFormat

data class NutritionAdapterItem(
    private val nutritionPerDay: NutritionPerDay
) {

    val formattedDate: String
        get() {
            val fmt = DateTimeFormat.forPattern("MMM dd, EEEE")
            return fmt.print(nutritionPerDay.date.dateTime)
        }

    /**
     * WeekOfYear, Year
     */
    val weekBundle: Pair<Int, Int>
        get() = Pair(nutritionPerDay.date.weekOfYear, nutritionPerDay.date.year)

    val intake: List<NutritionIntakeAdapterItem>
        get() = nutritionPerDay.intake
            .sortedBy { it.time.ordinal }
            .groupBy { it.time.nameRes }
            .map { (timeNameRes, entriesForTime) ->
                entriesForTime
                    .mapTo(mutableListOf(NutritionIntakeAdapterItem.Header(timeNameRes))) { entry ->
                        NutritionIntakeAdapterItem.Intake(entry)
                    }
            }
            .flatten()

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