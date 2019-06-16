package at.shockbytes.weather.owm.matcher

import at.shockbytes.weather.owm.OwmWeatherRecord

class AfternoonBestOwmForecastItemMatcher : BestOwmForecastItemMatcher {

    override fun isRangeMatch(owmWeatherRecord: OwmWeatherRecord, hour: Int): Boolean {
        return hour in 11..17
    }

    override fun findBestMatch(records: List<OwmWeatherRecord>): OwmWeatherRecord? {
        val middleIdx = Math.floor(records.size / 2.toDouble()).toInt()
        return if (middleIdx != 0) {
            records[middleIdx]
        } else null
    }
}