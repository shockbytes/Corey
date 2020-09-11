package at.shockbytes.corey.data.nutrition

sealed class NutritionBalance {

    abstract val kcal: Int

    class Positive(override val kcal: Int): NutritionBalance()

    class Negative(override val kcal: Int): NutritionBalance()
}
