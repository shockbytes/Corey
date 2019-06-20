package at.shockbytes.corey.common.core.location

import io.reactivex.Observable
import io.reactivex.Single

interface LocationRepository {

    fun getLastKnownLocation(): Single<CoreyLocation>

    fun requestLocationUpdates(): Observable<CoreyLocation>

    fun stopLocationUpdates()

    fun resolveLocation(loc: CoreyLocation): Single<String>

    /**
     * @return Distance in meter
     */
    fun calculateDistanceToLastKnownLocation(location: CoreyLocation): Single<Int>

    fun distanceBetween(start: CoreyLocation, end: CoreyLocation): Float
}