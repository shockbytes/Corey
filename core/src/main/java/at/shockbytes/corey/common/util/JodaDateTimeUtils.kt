package at.shockbytes.corey.common.util

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

object JodaDateTimeUtils {

    private val monthAndYearFormatter = DateTimeFormat.forPattern("yyyy.MM")
    private val displayableMonthAndYearFormatter = DateTimeFormat.forPattern("MMM yyyy")

    fun parseMonthAndYearFormat(date: String): DateTime {
        return monthAndYearFormatter.parseDateTime(date)
    }

    fun formatMonthAndYearString(date: String): String {
        return displayableMonthAndYearFormatter.print(monthAndYearFormatter.parseDateTime(date))
    }

    fun toMonthAndYearFormattedString(dateTime: DateTime): String {
        return monthAndYearFormatter.print(dateTime)
    }

    fun getDayOfMonthFromTimestamp(timestamp: Long): Int {
        return DateTime(timestamp).dayOfMonth
    }

    fun getHourFromTimestamp(timestamp: Long): Int {
        return DateTime(timestamp).hourOfDay
    }
}