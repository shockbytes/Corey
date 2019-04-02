package at.shockbytes.corey.dagger

import at.shockbytes.corey.data.schedule.weather.ScheduleWeatherResolver
import at.shockbytes.corey.data.schedule.weather.TestScheduleWeatherResolver
import dagger.Module
import dagger.Provides
import dagger.Reusable

@Module
class WeatherModule {

    @Provides
    @Reusable
    fun provideScheduleWeatherResolver(): ScheduleWeatherResolver {
        return TestScheduleWeatherResolver()
    }
}