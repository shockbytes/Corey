package at.shockbytes.corey.data.schedule

import io.reactivex.Completable
import io.reactivex.Observable

/**
 * Author:  Martin Macheiner
 * Date:    21.02.2017
 */
interface ScheduleRepository {

    val schedule: Observable<List<ScheduleItem>>

    val schedulableItems: Observable<List<SchedulableItem>>

    fun insertScheduleItem(item: ScheduleItem): ScheduleItem

    fun updateScheduleItem(item: ScheduleItem)

    fun deleteScheduleItem(item: ScheduleItem)

    fun deleteAll(): Completable
}
