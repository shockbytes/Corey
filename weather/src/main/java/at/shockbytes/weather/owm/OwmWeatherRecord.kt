package at.shockbytes.weather.owm

import com.google.gson.annotations.SerializedName
import java.util.Date

/**
 * Author:  Martin Macheiner
 * Date:    21.09.2017
 */
class OwmWeatherRecord {

    private val dt: Long = 0

    @SerializedName("main")
    private val temperature: Temperature? = null

    private val weather: List<WeatherInfo>

    val timestamp: Long
        get() = dt * 1000L

    val temperatureAsInt: Int
        get() = temperature?.temp?.toInt() ?: 0

    val weatherIconUrl: String?
        get() = if (weather.isNotEmpty()) {
            weather[0].icon
        } else null

    init {
        weather = ArrayList()
    }

    override fun toString(): String {
        return "Time: " + Date(timestamp).toString() + " WeatherIcon: " + weather[0].icon + " / " + temperature!!.toString()
    }

    private class WeatherInfo {
        var icon: String? = null
    }

    private class Temperature {

        internal var temp: Double = 0.toDouble()
        internal var humidity: Double = 0.toDouble()

        override fun toString(): String {
            return "Temp: $temp / Humidity: $humidity"
        }
    }
}