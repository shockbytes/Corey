package at.shockbytes.corey.common.core.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject

class GmsLocationRepository(private val context: Context) : LocationRepository {

    private val fusedLocationClient = FusedLocationProviderClient(context)

    private val locationCallback: LocationCallback = object : LocationCallback() {

        override fun onLocationResult(result: LocationResult?) {
            super.onLocationResult(result)
        }
    }

    private val locationSubject = PublishSubject.create<CoreyLocation>()

    @SuppressLint("MissingPermission")
    override fun getLastKnownLocation(): Single<CoreyLocation> {
        return Single.create { emitter ->
            fusedLocationClient.lastLocation.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val location = task.result
                    location?.let { loc ->
                        emitter.onSuccess(CoreyLocation(loc.latitude, loc.longitude, loc.time))
                    } ?: emitter.onError(NullPointerException())
                } else {
                    val exception = task.exception ?: NullPointerException()
                    emitter.onError(exception)
                }
            }
        }
    }

    override fun requestLocationUpdates(): Observable<CoreyLocation> {
        // TODO This is an important line
        //  fusedLocationClient.requestLocationUpdates(locationCallback, PendingIn)
        return locationSubject
    }

    override fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun resolveLocation(loc: CoreyLocation): Single<String> {
        return Single.create { emitter ->
            val address = Geocoder(context)
                .getFromLocation(loc.lat, loc.lng, 1)
                .firstOrNull()
                ?.locality

            if (address != null) {
                emitter.onSuccess(address)
            } else {
                emitter.onError(NullPointerException())
            }
        }
    }

    override fun calculateDistanceToLastKnownLocation(location: CoreyLocation): Single<Int> {
        return getLastKnownLocation()
            .map { lastLocation -> distanceBetween(lastLocation, location).toInt() }
    }

    override fun distanceBetween(start: CoreyLocation, end: CoreyLocation): Float {
        val res = FloatArray(1)
        android.location.Location.distanceBetween(start.lat, start.lng, end.lat, end.lng, res)
        return res[0] // .div(1000f)
    }
}