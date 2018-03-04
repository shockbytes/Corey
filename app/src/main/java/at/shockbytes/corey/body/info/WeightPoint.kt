package at.shockbytes.corey.body.info

data class WeightPoint(val timeStamp: Long = 0, val weight: Double = 0.0): Comparable<WeightPoint> {

    override fun compareTo(other: WeightPoint): Int {
        return (timeStamp - other.timeStamp).toInt()
    }
}
