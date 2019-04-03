package at.shockbytes.corey.data.schedule.weather

import at.shockbytes.core.scheduler.SchedulerFacade
import at.shockbytes.corey.common.core.location.LocationRepository
import at.shockbytes.corey.common.core.util.CoreyUtils
import at.shockbytes.weather.CurrentWeather
import at.shockbytes.weather.OwmWeatherRepository
import io.reactivex.Single

class OwmScheduleWeatherResolver(
    private val owmWeatherRepository: OwmWeatherRepository,
    private val schedulers: SchedulerFacade,
    private val locationRepository: LocationRepository
) : ScheduleWeatherResolver {

    override fun resolveWeatherForScheduleIndex(index: Int): Single<CurrentWeather> {
        return locationRepository.getLastKnownLocation()
                .flatMap { location ->
                    locationRepository.resolveLocation(location)
                }
                .flatMap { place ->
                    owmWeatherRepository.getDailyWeatherForecast(place, FORECAST_DAYS)
                }
                .map { dailyWeatherForecast ->
                    val forecastDay = WeatherResolverHelper.indexRelativeToWeekDay(index, CoreyUtils.getDayOfWeek())
                    dailyWeatherForecast[forecastDay]
                }
                .subscribeOn(schedulers.io)
    }

    companion object {

        private const val FORECAST_DAYS = 7
    }
}