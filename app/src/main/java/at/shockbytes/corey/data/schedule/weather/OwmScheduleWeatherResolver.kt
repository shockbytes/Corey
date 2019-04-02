package at.shockbytes.corey.data.schedule.weather

import at.shockbytes.core.scheduler.SchedulerFacade
import at.shockbytes.weather.CurrentWeather
import at.shockbytes.weather.OwmWeatherRepository
import io.reactivex.Single

class OwmScheduleWeatherResolver(
    private val owmWeatherRepository: OwmWeatherRepository,
    private val schedulers: SchedulerFacade
) : ScheduleWeatherResolver {

    override fun resolveWeatherForScheduleIndex(index: Int): Single<CurrentWeather> {

        // TODO Checkout place
        val place = "Vienna"
        val forecastDay = indexRelativeToForecast(index)

        return owmWeatherRepository.getDailyWeatherForecast(place, FORECAST_DAYS)
                .map { dailyWeatherForecast -> dailyWeatherForecast[forecastDay] }
                .subscribeOn(schedulers.io)
    }

    private fun indexRelativeToForecast(index: Int): Int {
        return 0
    }

    companion object {

        private const val FORECAST_DAYS = 7
    }
}