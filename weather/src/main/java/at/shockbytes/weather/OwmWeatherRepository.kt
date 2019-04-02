package at.shockbytes.weather

import at.shockbytes.weather.owm.OwmCurrentWeather
import at.shockbytes.weather.owm.OwmDailyWeatherForecast
import at.shockbytes.weather.owm.OwmWeatherForecast
import at.shockbytes.weather.owm.OwmWeatherApi
import at.shockbytes.weather.owm.OwmWeatherIconMapper
import io.reactivex.Single

class OwmWeatherRepository(
    private val weatherApi: OwmWeatherApi,
    private val weatherStorage: WeatherStorage,
    private val validityOptions: WeatherValidityOptions
) : WeatherRepository {

    override fun getWeatherForecast(place: String): Single<WeatherForecast> {
        return weatherStorage.loadCachedWeatherForecast()
            ?.flatMap {
                if (it.validUntil < System.currentTimeMillis()) {
                    loadForecastFromApi(place)
                } else {
                    Single.just(it)
                }
            } ?: loadForecastFromApi(place)
    }

    override fun getCurrentWeather(lat: Double, lng: Double): Single<CurrentWeather> {
        return weatherStorage.loadCachedCurrentWeather()
            ?.flatMap {
                if (it.validUntil < System.currentTimeMillis()) {
                    loadCurrentWeatherFromApi(lat, lng)
                } else {
                    Single.just(it)
                }
            } ?: loadCurrentWeatherFromApi(lat, lng)
    }

    override fun getDailyWeatherForecast(place: String, days: Int): Single<DailyWeatherForecast> {
        return weatherStorage.loadCachedDailyWeatherForecast()
                ?.flatMap {
                    if (it.validUntil < System.currentTimeMillis()) {
                        loadDailyForecastFromApi(place, days)
                    } else {
                        Single.just(it)
                    }
                } ?: loadDailyForecastFromApi(place, days)
    }

    // ------------------------------------------------------------------------------

    private fun loadForecastFromApi(place: String): Single<WeatherForecast> {
        return weatherApi
                .getWeatherForecast(place, OwmWeatherApi.API_KEY, "metric")
                .map { owmWeather -> mapToWeatherForecast(owmWeather) }
                .doOnSuccess { forecast -> weatherStorage.cacheWeatherForecast(forecast) }
    }

    private fun loadDailyForecastFromApi(place: String, days: Int): Single<DailyWeatherForecast> {
        return weatherApi
                .getDailyWeatherForecast(place, OwmWeatherApi.API_KEY, "metric", days)
                .map { owmWeather -> mapToDailyWeatherForecast(owmWeather) }
                .doOnSuccess { forecast -> weatherStorage.cacheDailyWeatherForecast(forecast) }
    }

    private fun loadCurrentWeatherFromApi(lat: Double, lng: Double): Single<CurrentWeather> {
        return weatherApi
            .getCurrentWeather(lat, lng, OwmWeatherApi.API_KEY, "metric")
            .map { c ->
                mapToCurrentWeather(c)
            }
            .doOnSuccess { current -> weatherStorage.cacheCurrentWeather(current) }
    }

    private fun mapToCurrentWeather(c: OwmCurrentWeather): CurrentWeather {

        val validityDate = validityOptions.getCurrentWeatherValidityDate()
        val iconRes = OwmWeatherIconMapper.mapOwmIconToDrawable(c.icon)
        val temperature = c.temperature

        return CurrentWeather(
            validityDate,
            iconRes,
            temperature
        )
    }

    private fun mapToWeatherForecast(owmWeather: OwmWeatherForecast): WeatherForecast {
        val validityDate = validityOptions.getForecastValidityDate()
        return WeatherForecast(
            validityDate,
            owmWeather.prepare().map { r ->
                WeatherForecast.ForecastItem(
                    r.temperatureAsInt,
                    OwmWeatherIconMapper.mapOwmIconToDrawable(r.weatherIconUrl)
                )
            }
        )
    }

    private fun mapToDailyWeatherForecast(owmWeather: OwmDailyWeatherForecast): DailyWeatherForecast {
        // TODO
        val validityDate = validityOptions.getForecastValidityDate()
        return DailyWeatherForecast(validityDate)
    }
}