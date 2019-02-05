package at.shockbytes.corey.data.schedule

import at.shockbytes.corey.common.core.util.Pokeable
import io.reactivex.Observable

/**
 * Author:  Martin Macheiner
 * Date:    21.02.2017
 */
interface ScheduleRepository : Pokeable {

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
}
