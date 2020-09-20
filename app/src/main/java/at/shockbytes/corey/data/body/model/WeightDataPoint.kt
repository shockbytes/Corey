package at.shockbytes.corey.data.body.model

import at.shockbytes.corey.common.core.util.FindClosestDiffable

data class WeightDataPoint(
    val timeStamp: Long = 0,
    val weight: Double = 0.0
) : Comparable<WeightDataPoint>, FindClosestDiffable {

    override fun compareTo(other: WeightDataPoint): Int {
        return (timeStamp - other.timeStamp).toInt()
    }

    override val diffValue: Double
        get() = timeStamp.toDouble()
}
