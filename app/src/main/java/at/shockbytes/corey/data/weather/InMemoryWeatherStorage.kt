package at.shockbytes.corey.data.weather

import at.shockbytes.weather.CurrentWeather
import at.shockbytes.weather.WeatherForecast
import at.shockbytes.weather.WeatherStorage
import io.reactivex.Single

class InMemoryWeatherStorage : WeatherStorage {

    private var currentWeather: CurrentWeather? = null
    private var weatherForecast: WeatherForecast? = null

    override fun loadCachedCurrentWeather(): Single<CurrentWeather>? {
        return currentWeather?.let { current ->
            Single.just(current)
        }
    }

    override fun cacheCurrentWeather(weather: CurrentWeather) {
        this.currentWeather = weather
    }

    override fun cacheWeatherForecast(forecast: WeatherForecast) {
        this.weatherForecast = forecast
    }

    override fun loadCachedWeatherForecast(): Single<WeatherForecast>? {
        return weatherForecast?.let { forecast ->
            Single.just(forecast)
        }
    }
}