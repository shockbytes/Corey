package at.shockbytes.corey.data.body.model

import at.shockbytes.corey.common.core.ActivityLevel
import at.shockbytes.corey.common.core.Gender
import at.shockbytes.corey.common.roundDouble
import at.shockbytes.corey.common.core.CoreyDate
import org.joda.time.DateTime
import org.joda.time.Years
import kotlin.math.pow

/**
 * Author:  Martin Macheiner
 * Date:    03.08.2016.
 */
data class User(
    val weightDataPoints: List<WeightDataPoint> = listOf(),
    val height: Int = -1,
    val gender: Gender,
    val birthday: CoreyDate,
    val desiredWeight: Int,
    val activityLevel: ActivityLevel
) {

    val age: Int
        get() = Years.yearsBetween(birthday.dateTime, DateTime.now()).years

    val highestWeight: Double
        get() = weightDataPoints.maxByOrNull { it.weight }?.weight ?: 0.0

    val currentBMI: Double
        get() {
            val weight = latestWeightDataPoint?.weight ?: 0.0
            return if (weight > 0 && height > 0) {

                val heightInM = height.toDouble().div(100)
                (weight / heightInM.pow(2)).roundDouble(digits = 1)
            } else 0.0
        }

    val currentWeight: Double
        get() = latestWeightDataPoint?.weight ?: 0.0

    val latestWeightDataPoint: WeightDataPoint?
        get() = if (weightDataPoints.isNotEmpty()) {
            weightDataPoints[weightDataPoints.size - 1]
        } else null
}
