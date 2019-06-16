package at.shockbytes.corey.util

import android.content.Context
import at.shockbytes.corey.R
import at.shockbytes.corey.common.core.util.view.model.SpinnerData

/**
 * Author:  Martin Macheiner
 * Date:    14.03.2017
 */
object CoreyAppUtils {

    fun getBodyRegionSpinnerData(c: Context): List<SpinnerData> {

        val data = mutableListOf<SpinnerData>()
        val bodyRegion = c.resources.getStringArray(R.array.body_region)
        data.add(SpinnerData(c.getString(R.string.spinner_body_template), 0))
        data.add(SpinnerData(bodyRegion[0], R.drawable.ic_bodyregion_legs))
        data.add(SpinnerData(bodyRegion[1], R.drawable.ic_bodyregion_core))
        data.add(SpinnerData(bodyRegion[2], R.drawable.ic_bodyregion_arms))
        data.add(SpinnerData(bodyRegion[3], R.drawable.ic_bodyregion_chest))
        data.add(SpinnerData(bodyRegion[4], R.drawable.ic_bodyregion_whole))
        return data
    }

    fun getIntensitySpinnerData(c: Context): List<SpinnerData> {

        val data = ArrayList<SpinnerData>()
        val intensity = c.resources.getStringArray(R.array.training_intensity)
        data.add(SpinnerData(c.getString(R.string.spinner_intensity_template), 0))
        for (s in intensity) {
            data.add(SpinnerData(s, 0))
        }
        return data
    }
}
