package at.shockbytes.corey.common.core.location

import io.reactivex.Observable
import io.reactivex.Single

interface LocationRepository {

    fun getLastKnownLocation(): Single<Location>

    fun requestLocationUpdates(): Observable<Location>

    fun stopLocationUpdates()

    fun resolveLocation(loc: Location): Single<String>

    /**
     * @return Distance in meter
     */
    fun calculateDistanceToLastKnownLocation(location: Location): Single<Int>
}