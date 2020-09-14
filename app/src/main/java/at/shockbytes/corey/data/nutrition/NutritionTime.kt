package at.shockbytes.corey.data.nutrition

import androidx.annotation.StringRes
import at.shockbytes.corey.R

enum class NutritionTime(@StringRes val nameRes: Int, val code: String) {
    MORNING(R.string.nutrition_time_morning, "morning"),
    LUNCH(R.string.nutrition_time_lunch, "lunch"),
    EVENING(R.string.nutrition_time_evening, "evening");

    companion object {

        fun fromCode(code: String): NutritionTime {
            return values().find { it.code == code }
                    ?: throw IllegalStateException("Unknown nutrition code $code")
        }
    }
}
