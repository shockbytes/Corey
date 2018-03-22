package at.shockbytes.corey.schedule

import at.shockbytes.corey.common.core.util.Pokeable
import io.reactivex.Observable

/**
 * @author Martin Macheiner
 * Date: 21.02.2017.
 */

interface ScheduleManager : Pokeable {

    val schedule: Observable<List<ScheduleItem>>

    val itemsForScheduling: Observable<List<String>>

    val isWorkoutNotificationDeliveryEnabled: Boolean

    val isWeighNotificationDeliveryEnabled: Boolean

    val dayOfWeighNotificationDelivery: Int

    fun insertScheduleItem(item: ScheduleItem): ScheduleItem

    fun updateScheduleItem(item: ScheduleItem)

    fun deleteScheduleItem(item: ScheduleItem)

    fun postWeighNotification()

    fun postWorkoutNotification()

    fun registerLiveScheduleUpdates(listener: LiveScheduleUpdateListener)

    fun unregisterLiveScheduleUpdates()

}
