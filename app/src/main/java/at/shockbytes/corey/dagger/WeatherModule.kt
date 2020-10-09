package at.shockbytes.corey.dagger

import at.shockbytes.core.scheduler.SchedulerFacade
import at.shockbytes.corey.common.core.location.LocationRepository
import at.shockbytes.corey.common.core.util.UserSettings
import at.shockbytes.corey.data.schedule.weather.DefaultScheduleWeatherResolver
import at.shockbytes.corey.data.schedule.weather.ScheduleWeatherResolver
import at.shockbytes.corey.data.weather.InMemoryWeatherStorage
import at.shockbytes.weather.OwmWeatherRepository
import at.shockbytes.weather.WeatherRepository
import at.shockbytes.weather.WeatherStorage
import at.shockbytes.weather.WeatherValidityOptions
import at.shockbytes.weather.owm.OwmWeatherApi
import at.shockbytes.weather.owm.matcher.AfternoonBestOwmForecastItemMatcher
import at.shockbytes.weather.owm.matcher.BestOwmForecastItemMatcher
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.Reusable
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Module
class WeatherModule {

    @Provides
    @Reusable
    fun provideOwmWeatherApi(okHttpClient: OkHttpClient): OwmWeatherApi {
        return Retrofit.Builder()
                .baseUrl(OwmWeatherApi.SERVICE_ENDPOINT)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(Gson()))
                .build()
                .create(OwmWeatherApi::class.java)
    }

    @Provides
    @Reusable
    fun provideScheduleWeatherResolver(
        weatherRepository: WeatherRepository,
        schedulers: SchedulerFacade,
        locationRepository: LocationRepository,
        userSettings: UserSettings
    ): ScheduleWeatherResolver {
        return DefaultScheduleWeatherResolver(weatherRepository, schedulers, locationRepository, userSettings)
    }

    @Provides
    @Reusable
    fun provideWeatherStorage(): WeatherStorage {
        return InMemoryWeatherStorage()
    }

    @Provides
    @Reusable
    fun provideValidityOptions(): WeatherValidityOptions {
        // Cache is 12 hours valid
        return WeatherValidityOptions(43200000L, 43200000L)
    }

    @Provides
    @Reusable
    fun provideOwmForecastItemMatcher(): BestOwmForecastItemMatcher {
        return AfternoonBestOwmForecastItemMatcher()
    }

    @Provides
    @Reusable
    fun provideWeatherRepository(
        owmWeatherApi: OwmWeatherApi,
        weatherStorage: WeatherStorage,
        validityOptions: WeatherValidityOptions,
        bestOwmForecastItemMatcher: BestOwmForecastItemMatcher
    ): WeatherRepository {
        return OwmWeatherRepository(
            owmWeatherApi,
            weatherStorage,
            validityOptions,
            bestOwmForecastItemMatcher
        )
    }
}