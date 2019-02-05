package at.shockbytes.corey.common.core.running.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.SettingsClient

/**
 * Author:  Martin Macheiner
 * Date:    05.09.2017
 */
class GooglePlayLocationManager(context: Context) : LocationManager {

    override var isLocationUpdateRequested: Boolean = false

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var settingsClient: SettingsClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationSettingsRequest: LocationSettingsRequest

    private var currentLocation: Location? = null

    private var listener: LocationManager.OnLocationUpdateListener? = null

    init {
        initialize(context)
        isLocationUpdateRequested = false
    }

    private fun initialize(context: Context) {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        settingsClient = LocationServices.getSettingsClient(context)

        locationCallback = createLocationCallback()
        locationRequest = createLocationRequest()
        locationSettingsRequest = createLocationSettingsRequest()
    }

    private fun createLocationRequest(): LocationRequest {
        return LocationRequest()
                .setInterval(LocationManager.UPDATE_INTERVAL_IN_MILLISECONDS)
                .setFastestInterval(LocationManager.FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
    }

    private fun createLocationCallback(): LocationCallback {
        return object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                currentLocation = locationResult?.lastLocation
                if (currentLocation != null) {
                    listener?.onLocationUpdate(currentLocation!!)
                }
            }
        }
    }

    private fun createLocationSettingsRequest(): LocationSettingsRequest {
        return LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest).build()
    }

    @SuppressLint("MissingPermission")
    override fun start(listener: LocationManager.OnLocationUpdateListener) {
        this.listener = listener

        settingsClient.checkLocationSettings(locationSettingsRequest)
                .addOnSuccessListener {
                    isLocationUpdateRequested = true
                    fusedLocationClient.requestLocationUpdates(locationRequest,
                            locationCallback, Looper.myLooper())

                    this.listener?.onConnected()
                }
                .addOnFailureListener { e ->
                    isLocationUpdateRequested = false
                    this.listener?.onError(e)
                }
    }

    override fun stop() {

        fusedLocationClient.removeLocationUpdates(locationCallback)
                .addOnCompleteListener {
                    isLocationUpdateRequested = false
                    listener?.onDisconnected()
                    listener = null
                }
    }
}
