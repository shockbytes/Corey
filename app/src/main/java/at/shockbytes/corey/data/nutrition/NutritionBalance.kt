package at.shockbytes.corey.data.nutrition

import android.graphics.Color
import at.shockbytes.core.util.CoreUtils.colored
import kotlin.math.absoluteValue

sealed class NutritionBalance {

    abstract val kcal: Int
    abstract val rawKcal: Int

    abstract fun formatted(): CharSequence

    class Positive(override val kcal: Int) : NutritionBalance() {
        override val rawKcal: Int
            get() = kcal

        override fun formatted(): CharSequence {
            return "+${kcal}kcal".colored(Color.RED)
        }
    }

    class Negative(override val kcal: Int) : NutritionBalance() {
        override val rawKcal: Int
            get() = -kcal

        override fun formatted(): CharSequence {
            return "-${kcal}kcal".colored(Color.GREEN)
        }
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
