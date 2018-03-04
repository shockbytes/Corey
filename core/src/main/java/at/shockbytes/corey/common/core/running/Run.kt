package at.shockbytes.corey.common.core.running

import android.location.Location
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.parcel.Parcelize


/**
 * @author Martin Macheiner
 * Date: 05.09.2017.
 */

@Parcelize
class Run(private var locations: MutableList<Location> = mutableListOf(),
          var id: Long = 0,
          private var startTime: Long = System.currentTimeMillis(),
          var distance: Double = 0.0,
          var time: Long = 0, // in ms
          var calories: Int = 0,
          var averagePace: String = "/") : Parcelable {


    internal val currentPaceDistance: Double
        get() = if (locations.size < LOCATIONS_FOR_CURRENT_PACE) {
            0.0
        } else locations[locations.size - LOCATIONS_FOR_CURRENT_PACE]
                .distanceTo(locations[locations.size - 1]) / 1000.0

    internal val currentPaceTime: Long
        get() = if (locations.size < LOCATIONS_FOR_CURRENT_PACE) {
            0
        } else locations[locations.size - 1].time - locations[locations.size - LOCATIONS_FOR_CURRENT_PACE].time

    val startLatLng: LatLng?
        get() = if (locations.size > 0) {
            LatLng(locations[0].latitude, locations[0].longitude)
        } else null

    val lastLatLng: LatLng?
        get() = if (locations.size > 0) {
            LatLng(locations[locations.size - 1].latitude,
                    locations[locations.size - 1].longitude)
        } else null

    fun addLocation(location: Location) {
        locations.add(location)
    }

    companion object {

        private const val LOCATIONS_FOR_CURRENT_PACE = 10
    }

}
