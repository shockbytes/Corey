package at.shockbytes.corey.data.reminder

import org.joda.time.DateTime
import org.joda.time.Duration

object ReminderHelper {

    fun getInitialDelayOffset(from: DateTime, hourToRemind: Int): Minutes {
        return if (from.hourOfDay < hourToRemind) {
            Minutes(Duration(from, from.withTimeAtStartOfDay().plusHours(hourToRemind)).standardMinutes)
        } else {
            Minutes(Duration(from, from.withTimeAtStartOfDay().plusDays(1).plusHours(hourToRemind)).standardMinutes)
        }
    }
}