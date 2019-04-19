package at.shockbytes.weather.util

import at.shockbytes.corey.common.util.JodaDateTimeUtils
import at.shockbytes.weather.owm.OwmWeatherRecord
import at.shockbytes.weather.owm.matcher.BestOwmForecastItemMatcher

object WeatherResolverHelper {

    private const val DAYS_PER_WEEK = 7

    fun indexRelativeToWeekDay(index: Int, currentDayOfWeek: Int): Int {

        return if (index > currentDayOfWeek) {
            index - currentDayOfWeek
        } else {
            index + DAYS_PER_WEEK - currentDayOfWeek
        }
    }

    fun findBestWeatherRecordForTimestamps(
        records: List<OwmWeatherRecord>,
        matcher: BestOwmForecastItemMatcher
    ): OwmWeatherRecord? {

        val reducedRecords = records
                .map { owmWeatherRecord ->
                    val hour = JodaDateTimeUtils.getHourFromTimestamp(owmWeatherRecord.timestamp)
                    Pair(owmWeatherRecord, hour)
                }
                .filter { (weatherRecord, hour) ->
                    matcher.isRangeMatch(weatherRecord, hour)
                }
                .map { (record, _) ->
                    record
                }

        return matcher.findBestMatch(reducedRecords)
    }
}