package at.shockbytes.corey.data.schedule.weather

import at.shockbytes.core.scheduler.SchedulerFacade
import at.shockbytes.corey.common.core.location.LocationRepository
import at.shockbytes.corey.common.core.util.CoreyUtils
import at.shockbytes.corey.common.core.util.UserSettings
import at.shockbytes.weather.CurrentWeather
import at.shockbytes.weather.TemperatureUnit
import at.shockbytes.weather.WeatherForecast
import at.shockbytes.weather.WeatherRepository
import at.shockbytes.weather.util.WeatherResolverHelper
import io.reactivex.Single

class DefaultScheduleWeatherResolver(
    private val weatherRepository: WeatherRepository,
    private val schedulers: SchedulerFacade,
    private val locationRepository: LocationRepository,
    private val userSettings: UserSettings
) : ScheduleWeatherResolver {

    override fun resolveWeatherForScheduleIndex(index: Int): Single<CurrentWeather> {
        return userSettings.isWeatherForecastEnabled
                .first(true)
                .flatMap { isEnabled ->
                    if (isEnabled) {
                        resolveWeatherForIndex(index)
                    } else {
                        Single.never()
                    }
                }
                .subscribeOn(schedulers.io)
                .observeOn(schedulers.ui)
    }

    private fun resolveWeatherForIndex(index: Int): Single<CurrentWeather> {
        return locationRepository
                .getLastKnownLocation()
                .flatMap { location ->
                    locationRepository.resolveLocation(location).subscribeOn(schedulers.io)
                }
                .flatMap { place ->
                    weatherRepository.getWeatherForecast(place)
                }
                .map { forecast ->

                    // TODO Check forecast day of month relative to current day

                    val forecastDay = WeatherResolverHelper.indexRelativeToWeekDay(index, CoreyUtils.getDayOfWeek())
                    if (forecastDay >= weatherRepository.forecastDays) {
                        throw IllegalStateException("$forecastDay > than supported forecast size ${weatherRepository.forecastDays}")
                    } else {
                        forecast[forecastDay].toCurrentWeather(forecast.validUntil, forecast.place)
                    }
                }
    }

    private fun WeatherForecast.ForecastItem.toCurrentWeather(
        validUntil: Long,
        locality: String
    ): CurrentWeather {
        return CurrentWeather(
                validUntil = validUntil,
                iconRes = this.icon,
                temperature = this.temperature,
                locality = locality,
                temperatureUnit = TemperatureUnit.CELSIUS
        )
    }
}