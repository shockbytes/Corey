package at.shockbytes.weather.owm
/**
 * Author:  Martin Macheiner
 * Date:    21.09.2017.
 */
class OwmWeatherForecast {

    private val list: MutableList<OwmWeatherRecord> = mutableListOf()

    val forecastCount: Int
        get() = list.size

    fun prepare(): List<OwmWeatherRecord> {
        if (list.size > 5) {
            val compressed = mutableListOf<OwmWeatherRecord>()
            var i = 0
            while (i < list.size) {
                compressed.add(list[i])
                i += list.size / 5
            }
            list.clear()
            list.addAll(compressed)
        }
        return list
    }

    fun getForecastFor(day: Int): OwmWeatherRecord {
        val idx = day.coerceIn(day until forecastCount)
        return list[idx]
    }

    override fun toString(): String {
        var str = "Size: " + list.size + "\n"
        for (record in list) {
            str += record.toString() + "\n"
        }
        return str
    }
}