package at.shockbytes.weather.owm.matcher

import at.shockbytes.weather.owm.OwmWeatherRecord

interface BestOwmForecastItemMatcher {

    fun isRangeMatch(owmWeatherRecord: OwmWeatherRecord, hour: Int): Boolean

    fun findBestMatch(records: List<OwmWeatherRecord>): OwmWeatherRecord?
}