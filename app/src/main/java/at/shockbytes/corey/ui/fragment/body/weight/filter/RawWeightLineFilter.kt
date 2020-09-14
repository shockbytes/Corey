package at.shockbytes.corey.ui.fragment.body.weight.filter

import at.shockbytes.corey.R
import at.shockbytes.corey.data.body.model.WeightDataPoint

class RawWeightLineFilter : WeightLineFilter {

    override val filterName: String = "Raw"
    override val filterNameRes: Int = R.string.weight

    override val lineColor: Int = R.color.material_blue
    override val lineThickness: Float = 2f

    override fun map(points: List<WeightDataPoint>): List<WeightDataPoint> = points
}