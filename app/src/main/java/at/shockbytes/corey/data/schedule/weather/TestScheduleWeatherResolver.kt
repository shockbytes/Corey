package at.shockbytes.corey.data.schedule.weather

import at.shockbytes.corey.R
import at.shockbytes.weather.CurrentWeather
import at.shockbytes.weather.TemperatureUnit
import io.reactivex.Single

class TestScheduleWeatherResolver : ScheduleWeatherResolver {

    override fun resolveWeatherForScheduleIndex(index: Int): Single<CurrentWeather> {
        return Single.just(
                CurrentWeather(
                        validUntil = System.currentTimeMillis(),
                        locality = "Vienna",
                        temperature = 17,
                        temperatureUnit = TemperatureUnit.CELSIUS,
                        iconRes = R.drawable.weather_clear
                )
        )
    }
}