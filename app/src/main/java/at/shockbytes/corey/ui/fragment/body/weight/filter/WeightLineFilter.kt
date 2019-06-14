package at.shockbytes.corey.ui.fragment.body.weight.filter

import android.support.annotation.ColorRes
import android.support.annotation.StringRes
import at.shockbytes.corey.data.body.info.WeightPoint

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

    fun map(points: List<WeightPoint>): List<WeightPoint>

}