package at.shockbytes.corey.data.schedule.weather

import at.shockbytes.weather.CurrentWeather
import io.reactivex.Single

interface ScheduleWeatherResolver {

    fun resolveWeatherForScheduleIndex(index: Int): Single<CurrentWeather>
}