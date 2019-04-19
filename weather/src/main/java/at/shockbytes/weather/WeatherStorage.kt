package at.shockbytes.weather

import io.reactivex.Single

interface WeatherStorage {

    fun loadCachedCurrentWeather(): Single<CurrentWeather>?

    fun cacheCurrentWeather(weather: CurrentWeather)

    fun cacheWeatherForecast(forecast: WeatherForecast)

    fun loadCachedWeatherForecast(): Single<WeatherForecast>?
}