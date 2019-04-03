package at.shockbytes.corey.data.weather

import at.shockbytes.weather.CurrentWeather
import at.shockbytes.weather.WeatherForecast
import at.shockbytes.weather.WeatherStorage
import io.reactivex.Single

class RealmWeatherStorage : WeatherStorage {

    override fun loadCachedCurrentWeather(): Single<CurrentWeather> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun cacheCurrentWeather(weather: CurrentWeather) {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun cacheWeatherForecast(forecast: WeatherForecast) {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun loadCachedWeatherForecast(): Single<WeatherForecast> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }
}