package at.shockbytes.corey.common.core.running

import com.google.android.gms.maps.model.LatLng

/**
 * Author: Martin Macheiner
 * Date: 14.03.2018.
 */
data class CoreyLatLng(
    val latitude: Double,
    val longitude: Double,
    val time: Long
) {
    fun toLatLng(): LatLng = LatLng(latitude, longitude)
}
