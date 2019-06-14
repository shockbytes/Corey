package at.shockbytes.corey.ui.fragment.body.weight.filter

import at.shockbytes.corey.R
import at.shockbytes.corey.data.body.info.WeightPoint
import java.util.LinkedList

/**
 * Author:  Martin Macheiner
 * Date:    14.06.2019
 */
class RunningAverageWeightLineFilter(
    private val window: Int = 3
): WeightLineFilter {

    override val filterName: String = "Running Average"
    override val filterNameRes: Int = R.string.averaged

    override val lineColor: Int = R.color.material_teal_200
    override val lineThickness: Float = 3.5f

    override fun map(points: List<WeightPoint>): List<WeightPoint> {

        val mapped = mutableListOf<WeightPoint>()
        var sum = 0.0
        val buffer: LinkedList<Double> = LinkedList()

        for ((timestamp, weight) in points) {

            if (buffer.size == window) {
                sum -= buffer.removeFirst()
            }

            sum += weight
            buffer.addLast(weight)
            val averaged = sum / buffer.size

            mapped.add(WeightPoint(timestamp, averaged))
        }

        return mapped
    }
}