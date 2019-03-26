package at.shockbytes.weather.owm

class OwmCurrentWeather {

    var weather: List<Weather?> = listOf()

    var main: Main? = null

    val icon: String?
        get() = weather.firstOrNull()?.icon

    var name: String? = null

    val temperature: Int
        get() = main?.temp?.toInt() ?: 0

    data class Weather(val icon: String?)

    data class Main(val temp: Double, val humidity: Double)
}