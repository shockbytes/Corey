package at.shockbytes.corey.common.core.running

import at.shockbytes.corey.common.core.location.CoreyLocation
import com.google.android.gms.maps.model.LatLng

/**
 * Author:  Martin Macheiner
 * Date:    05.09.2017
 */
class Run(
    var locations: MutableList<CoreyLocation> = mutableListOf(),
    var id: Long = 0,
    private var startTime: Long = System.currentTimeMillis(),
    var distance: Double = 0.0,
    var time: Long = 0, // in ms
    var calories: Int = 0,
    var averagePace: String = "/"
) {

    val startLatLng: LatLng?
        get() = if (locations.size > 0) {
            LatLng(locations[0].lat, locations[0].lng)
        } else null

    val lastLatLng: LatLng?
        get() = if (locations.size > 0) {
            LatLng(locations[locations.size - 1].lat,
                    locations[locations.size - 1].lng)
        } else null

    fun addLocation(location: CoreyLocation) {
        locations.add(location)
    }
}
