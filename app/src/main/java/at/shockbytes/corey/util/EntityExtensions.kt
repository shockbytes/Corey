package at.shockbytes.corey.util

import at.shockbytes.corey.data.schedule.ScheduleItem

fun ScheduleItem.isItemOfCurrentDay(currentDay: Int): Boolean {
    return day == currentDay && !isEmpty
}