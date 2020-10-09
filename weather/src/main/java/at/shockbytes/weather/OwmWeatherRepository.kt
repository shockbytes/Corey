package at.shockbytes.weather

import at.shockbytes.weather.owm.OwmCurrentWeather
import at.shockbytes.weather.owm.OwmWeatherForecast
import at.shockbytes.weather.owm.OwmWeatherApi
import at.shockbytes.weather.owm.OwmWeatherIconMapper
import at.shockbytes.weather.owm.matcher.BestOwmForecastItemMatcher
import io.reactivex.Single

class OwmWeatherRepository(
    private val weatherApi: OwmWeatherApi,
    private val weatherStorage: WeatherStorage,
    private val validityOptions: WeatherValidityOptions,
    private val bestOwmForecastItemMatcher: BestOwmForecastItemMatcher
) : WeatherRepository {

    override val forecastDays: Int = 5

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

    // ------------------------------------------------------------------------------

    private fun loadForecastFromApi(place: String): Single<WeatherForecast> {
        return weatherApi
                .getWeatherForecast(place, OwmWeatherApi.API_KEY, "metric")
                .map { owmWeather -> mapToWeatherForecast(owmWeather, place) }
                .doOnSuccess { forecast -> weatherStorage.cacheWeatherForecast(forecast) }
    }

    private fun loadCurrentWeatherFromApi(lat: Double, lng: Double): Single<CurrentWeather> {
        return weatherApi
            .getCurrentWeather(lat, lng, OwmWeatherApi.API_KEY, "metric")
            .map(::mapToCurrentWeather)
            .doOnSuccess { current -> weatherStorage.cacheCurrentWeather(current) }
    }

    private fun mapToCurrentWeather(c: OwmCurrentWeather): CurrentWeather {

        val validityDate = validityOptions.getCurrentWeatherValidityDate()
        val iconRes = OwmWeatherIconMapper.mapOwmIconToDrawable(c.icon)
        val temperature = c.temperature

        return CurrentWeather(
            validityDate,
            iconRes,
            temperature,
            TemperatureUnit.CELSIUS
        )
    }

    private fun mapToWeatherForecast(owmWeather: OwmWeatherForecast, place: String): WeatherForecast {
        val validityDate = validityOptions.getForecastValidityDate()
        return WeatherForecast(
            validityDate,
            owmWeather.toForecastItems(bestOwmForecastItemMatcher),
            place = place
        )
    }
}