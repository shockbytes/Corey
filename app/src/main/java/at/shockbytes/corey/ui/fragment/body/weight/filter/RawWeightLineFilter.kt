package at.shockbytes.corey.ui.fragment.body.weight.filter

import at.shockbytes.corey.R
import at.shockbytes.corey.data.body.info.WeightPoint

class RawWeightLineFilter: WeightLineFilter {

    override val filterName: String = "Raw"
    override val filterNameRes: Int = R.string.weight

    override val lineColor: Int = R.color.body_card_weight_history
    override val lineThickness: Float = 2f

    override fun map(points: List<WeightPoint>): List<WeightPoint> = points
}