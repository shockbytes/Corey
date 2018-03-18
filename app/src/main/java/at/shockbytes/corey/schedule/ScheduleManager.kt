package at.shockbytes.corey.schedule

import io.reactivex.Observable

/**
 * @author Martin Macheiner
 * Date: 21.02.2017.
 */

interface ScheduleManager {

    val schedule: Observable<List<ScheduleItem>>

    val itemsForScheduling: Observable<List<String>>

    val isWorkoutNotificationDeliveryEnabled: Boolean

    val isWeighNotificationDeliveryEnabled: Boolean

    val dayOfWeighNotificationDelivery: Int

    fun poke()

    fun insertScheduleItem(item: ScheduleItem): ScheduleItem

    fun updateScheduleItem(item: ScheduleItem)

    fun deleteScheduleItem(item: ScheduleItem)

    fun postWeighNotification()

    fun tryPostWorkoutNotification()

    fun registerLiveScheduleUpdates(listener: LiveScheduleUpdateListener)

    fun unregisterLiveScheduleUpdates()

}
