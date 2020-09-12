package at.shockbytes.corey.data.nutrition

import org.joda.time.DateTime

data class NutritionDate(
        val year: Int = 0,
        val month: Int = 0,
        val day: Int = 0,
        val weekOfYear: Int = 0,
) {

    val dateTime: DateTime
        get() = DateTime(year, month, day, 0, 0)
}
