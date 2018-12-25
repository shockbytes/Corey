package at.shockbytes.corey.data.schedule

import java.util.*

/**
 * Author:  Martin Macheiner
 * Date:    28.02.2017
 */
data class ScheduleItem(
        var name: String = "",
        var day: Int = -1,
        var id: String = UUID.randomUUID().toString()) {

    val isEmpty: Boolean
        get() = name.isEmpty()

}
