package at.shockbytes.corey.data.nutrition

import kotlin.math.absoluteValue

sealed class NutritionBalance {

    abstract val kcal: Int

    abstract val rawKcal: Int

    class Positive(override val kcal: Int) : NutritionBalance() {
        override val rawKcal: Int
            get() = kcal
    }

    class Negative(override val kcal: Int) : NutritionBalance() {
        override val rawKcal: Int
            get() = -kcal
    }

    companion object {

        fun fromRawKcal(rawKcal: Int): NutritionBalance {
            return if (rawKcal > 0) {
                Positive(rawKcal)
            } else {
                Negative(rawKcal.absoluteValue)
            }
        }
    }
}
