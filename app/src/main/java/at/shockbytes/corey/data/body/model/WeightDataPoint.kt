package at.shockbytes.corey.data.body.model

data class WeightDataPoint(
    val timeStamp: Long = 0,
    val weight: Double = 0.0
) : Comparable<WeightDataPoint> {

    override fun compareTo(other: WeightDataPoint): Int {
        return (timeStamp - other.timeStamp).toInt()
    }
}
