package at.shockbytes.corey.common.core.util

import org.joda.time.Period
import org.joda.time.PeriodType
import org.joda.time.format.PeriodFormatterBuilder

/**
 * Author:  Martin Macheiner
 * Date:    10.09.2017
 */
object RunUtils {

    fun calculatePace(timeInMs: Long, distance: Double): String {

        if (distance <= 0) {
            return "-:--"
        }
        val kmMillis = (timeInMs / distance).toLong()
        return formatPaceMillisToString(kmMillis)
    }

    fun calculateCaloriesBurned(distance: Double, weightOfRunner: Double): Int {
        val burned = distance * weightOfRunner * 1.036
        return Math.floor(burned).toInt()
    }

    private fun formatPaceMillisToString(kmMillis: Long): String {

        val formatter = PeriodFormatterBuilder()
                .minimumPrintedDigits(2)
                .appendMinutes()
                .appendSeparator(":")
                .appendSeconds()
                .toFormatter()

        val minutesSeconds = PeriodType.time()
                .withMillisRemoved()
                .withHoursRemoved()

        val kmPeriod = Period(kmMillis, minutesSeconds)
        return formatter.print(kmPeriod)
    }
}
