package at.shockbytes.corey.data.schedule

import io.reactivex.Observable

interface SchedulableItemResolver {

    fun resolveSchedulableItems(): Observable<List<SchedulableItem>>
}