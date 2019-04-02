package at.shockbytes.corey.data.schedule.weather

import at.shockbytes.weather.CurrentWeather
import io.reactivex.Single

class OwmScheduleWeatherResolver : ScheduleWeatherResolver {

    override fun resolveWeatherForScheduleIndex(index: Int): Single<CurrentWeather> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }
}