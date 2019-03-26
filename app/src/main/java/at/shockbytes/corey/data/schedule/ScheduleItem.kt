package at.shockbytes.corey.data.schedule

import java.util.UUID

/**
 * Author:  Martin Macheiner
 * Date:    28.02.2017
 */
data class ScheduleItem(
    val name: String = "",
    val day: Int = -1,
    val id: String = UUID.randomUUID().toString()
) {

    val isEmpty: Boolean
        get() = name.isEmpty()
}
