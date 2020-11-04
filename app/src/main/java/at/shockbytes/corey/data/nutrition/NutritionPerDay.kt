package at.shockbytes.corey.data.nutrition

import at.shockbytes.corey.common.core.CoreyDate

data class NutritionPerDay(
    val date: CoreyDate,
    val intake: List<NutritionEntry>,
    val burned: List<PhysicalActivity>
) {

    val balance: NutritionBalance
        get() {
            val diff = intake.sumBy { it.kcal } - burned.sumBy { it.kcal }
            return NutritionBalance.fromRawKcal(diff)
        }
}
