package at.shockbytes.corey.data.body.model

import at.shockbytes.corey.common.core.ActivityLevel
import at.shockbytes.corey.common.core.Gender
import at.shockbytes.util.AppUtils

/**
 * Author:  Martin Macheiner
 * Date:    03.08.2016.
 */
data class User(
        val weightDataPoints: List<WeightDataPoint> = listOf(),
        val height: Int = -1,
        val gender: Gender,
        val age: Int,
        val desiredWeight: Int,
        val activityLevel: ActivityLevel
) {

    val highestWeight: Double
        get() = weightDataPoints.maxByOrNull { it.weight }?.weight ?: 0.0

    val latestBmi: Double
        get() {
            val weight = latestWeightDataPoint?.weight ?: 0.0
            return if (weight > 0 && height > 0) {
                AppUtils.roundDouble(weight / (height.toDouble() * height.toDouble()), 1)
            } else 0.0
        }

    val currentWeight: Double
        get() = latestWeightDataPoint?.weight ?: 0.0

    val latestWeightDataPoint: WeightDataPoint?
        get() = if (weightDataPoints.isNotEmpty()) {
            weightDataPoints[weightDataPoints.size - 1]
        } else null
}
