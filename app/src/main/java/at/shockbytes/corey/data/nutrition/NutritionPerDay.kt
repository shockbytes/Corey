package at.shockbytes.corey.data.nutrition

import kotlin.math.absoluteValue

data class NutritionPerDay(
        val date: NutritionDate,
        val intake: List<NutritionEntry>,
        val burned: List<PhysicalActivity>
) {

    val balance: NutritionBalance
        get() {
            val diff = intake.sumBy { it.kcal } - burned.sumBy { it.kcal }

            return if (diff > 0) {
                NutritionBalance.Positive(diff)
            } else {
                NutritionBalance.Negative(diff.absoluteValue)
            }
        }
}
