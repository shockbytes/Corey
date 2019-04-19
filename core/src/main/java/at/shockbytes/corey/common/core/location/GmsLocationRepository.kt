package at.shockbytes.corey.common.core.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import com.google.android.gms.location.FusedLocationProviderClient
import io.reactivex.Observable
import io.reactivex.Single

class GmsLocationRepository(private val context: Context) : LocationRepository {

    private val fusedLocationClient = FusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    override fun getLastKnownLocation(): Single<Location> {
        return Single.create { emitter ->
            fusedLocationClient.lastLocation.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val location = task.result
                    location?.let { loc ->
                        emitter.onSuccess(Location(loc.latitude, loc.longitude))
                    } ?: emitter.onError(NullPointerException())
                } else {
                    val exception = task.exception ?: NullPointerException()
                    emitter.onError(exception)
                }
            }
        }
    }

    override fun requestLocationUpdates(): Observable<Location> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun stopLocationUpdates() {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun resolveLocation(loc: Location): Single<String> {
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

    override fun calculateDistanceToLastKnownLocation(location: Location): Single<Int> {
        return getLastKnownLocation()
            .map { lastLocation -> distanceBetween(lastLocation, location).toInt() }
    }

    private fun distanceBetween(start: Location, end: Location): Float {
        val res = FloatArray(1)
        android.location.Location.distanceBetween(start.lat, start.lng, end.lat, end.lng, res)
        return res[0] // .div(1000f)
    }
}