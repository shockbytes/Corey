package at.shockbytes.corey.body.info

import at.shockbytes.util.AppUtils

/**
 * @author  Martin Macheiner
 * Date:    03.08.2016.
 */
data class BodyInfo(var weightPoints: List<WeightPoint> = listOf(),
                    var height: Double = 0.toDouble(),
                    var dreamWeight: Int = 0) {

    val startWeight: Double
        get() = if (weightPoints.isNotEmpty()) {
            weightPoints[0].weight
        } else 0.0

    val lowestWeight: Double
        get() = weightPoints.min()?.weight ?: startWeight

    val highestWeight: Double
        get() = weightPoints.max()?.weight ?: startWeight

    val latestBmi: Double
        get() {
            val weight = latestWeightPoint.weight
            return if (weight > 0 && height > 0) {
                AppUtils.roundDouble(weight / (height * height), 1)
            } else 0.0
        }

    val latestWeightPoint: WeightPoint
        get() = if (weightPoints.isNotEmpty()) {
            weightPoints[weightPoints.size - 1]
        } else WeightPoint(0, 0.0)

}
