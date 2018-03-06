package at.shockbytes.corey.storage.live

import at.shockbytes.corey.schedule.ScheduleItem

/**
 * @author Martin Macheiner
 * Date: 27.02.2017.
 */

interface LiveScheduleUpdateListener {

    fun onScheduleItemAdded(item: ScheduleItem)

    fun onScheduleItemDeleted(item: ScheduleItem)

    fun onScheduleItemChanged(item: ScheduleItem)

}
