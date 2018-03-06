package at.shockbytes.corey.schedule

import java.util.*

/**
 * @author  Martin Macheiner
 * Date:    28.02.2017.
 */

class ScheduleItem(var name: String = "", var day: Int = -1) {

    var id: String = UUID.randomUUID().toString()

    val isEmpty: Boolean
        get() = name.isEmpty()

    override fun equals(other: Any?): Boolean {
        return (other as? ScheduleItem)?.id?.equals(id) ?: false
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + day
        result = 31 * result + id.hashCode()
        return result
    }
}
