package at.shockbytes.weather

import io.reactivex.Single

interface WeatherRepository {

    val forecastDays: Int

    fun getWeatherForecast(place: String): Single<WeatherForecast>

    fun getCurrentWeather(lat: Double, lng: Double): Single<CurrentWeather>
}