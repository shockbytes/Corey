package at.shockbytes.weather.owm

import at.shockbytes.corey.common.util.JodaDateTimeUtils
import at.shockbytes.weather.WeatherForecast
import at.shockbytes.weather.owm.matcher.BestOwmForecastItemMatcher
import at.shockbytes.weather.util.WeatherResolverHelper

/**
 * Author:  Martin Macheiner
 * Date:    21.09.2017.
 */
class OwmWeatherForecast {

    private val list: MutableList<OwmWeatherRecord> = mutableListOf()

    val forecastCount: Int
        get() = list.size

    fun toForecastItems(matcher: BestOwmForecastItemMatcher): List<WeatherForecast.ForecastItem> {

        return list
                .groupBy { record ->
                    JodaDateTimeUtils.getDayOfMonthFromTimestamp(record.timestamp)
                }
                .entries
                .mapNotNull { (dayOfMonth, owmRecords) ->

                    // Can be null, because sometimes there is no forecast item for the right date
                    with(WeatherResolverHelper.findBestWeatherRecordForTimestamps(owmRecords, matcher)) {
                        this?.let {
                            WeatherForecast.ForecastItem(
                                    timestamp = timestamp,
                                    dayOfMonth = dayOfMonth,
                                    temperature = temperatureAsInt,
                                    icon = OwmWeatherIconMapper.mapOwmIconToDrawable(weatherIconUrl)
                            )
                        }
                    }
                }
    }

    override fun toString(): String {
        var str = "Size: " + list.size + "\n"
        for (record in list) {
            str += record.toString() + "\n"
        }
        return str
    }
}