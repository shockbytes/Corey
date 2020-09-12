package at.shockbytes.corey.data.nutrition

data class NutritionPerDay(
        val date: NutritionDate,
        val intake: List<NutritionEntry>,
        val burned: List<PhysicalActivity>
) {

    val balance: NutritionBalance
        get() {
            val diff = intake.sumBy { it.kcal } - burned.sumBy { it.kcal }
            return NutritionBalance.fromRawKcal(diff)
        }
}
