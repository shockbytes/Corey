package at.shockbytes.corey.common.core.running.location

import android.location.Location

/**
 * Author:  Martin Macheiner
 * Date:    05.09.2017
 */
interface LocationManager {

    val isLocationUpdateRequested: Boolean

    interface OnLocationUpdateListener {

        fun onConnected()

        fun onDisconnected()

        fun onError(e: Exception)

        fun onLocationUpdate(location: Location)
    }

    fun start(listener: OnLocationUpdateListener)

    fun stop()

    companion object {

        // 1 seconds
        const val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 1000

        // Half of normal update time
        const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2
    }
}