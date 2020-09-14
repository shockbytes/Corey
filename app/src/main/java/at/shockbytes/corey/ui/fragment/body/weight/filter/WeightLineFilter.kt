package at.shockbytes.corey.ui.fragment.body.weight.filter

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import at.shockbytes.corey.data.body.model.WeightDataPoint

/**
 * Author:  Martin Macheiner
 * Date:    14.06.2019
 */
interface WeightLineFilter {

    val filterName: String

    @get:StringRes
    val filterNameRes: Int

    @get:ColorRes
    val lineColor: Int

    val lineThickness: Float

    fun map(points: List<WeightDataPoint>): List<WeightDataPoint>
}