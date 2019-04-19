package at.shockbytes.weather.owm

import at.shockbytes.weather.R

object OwmWeatherIconMapper {

    fun mapOwmIconToDrawable(icon: String?): Int {
        return when (icon) {
            "01d" -> R.drawable.weather_clear
            "01n" -> R.drawable.weather_clear_night
            "02d" -> R.drawable.weather_few_clouds
            "02n" -> R.drawable.weather_few_clouds_night
            "03d" -> R.drawable.weather_clouds
            "03n" -> R.drawable.weather_clouds_night
            "04d" -> R.drawable.weather_haze
            "04n" -> R.drawable.weather_haze
            "09d" -> R.drawable.weather_showers_day
            "09n" -> R.drawable.weather_showers_night
            "10d" -> R.drawable.weather_rain_day
            "10n" -> R.drawable.weather_rain_night
            "11d" -> R.drawable.weather_storm_day
            "11n" -> R.drawable.weather_storm_night
            "13d" -> R.drawable.weather_snow
            "13n" -> R.drawable.weather_snow
            "50d" -> R.drawable.weather_mist
            "50n" -> R.drawable.weather_mist
            else -> R.drawable.weather_none_available
        }
    }
}