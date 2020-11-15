package at.shockbytes.corey.ui.fragment.body.weight

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import at.shockbytes.corey.data.body.model.WeightDataPoint

data class WeightHistoryLine(
    @StringRes val name: Int,
    val points: List<WeightDataPoint>,
    @ColorRes val lineColor: Int,
    val lineThickness: Float
)