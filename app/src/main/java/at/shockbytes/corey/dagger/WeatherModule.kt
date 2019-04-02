package at.shockbytes.corey.dagger

import at.shockbytes.corey.data.schedule.weather.ScheduleWeatherResolver
import at.shockbytes.corey.data.schedule.weather.TestScheduleWeatherResolver
import at.shockbytes.corey.data.weather.InMemoryWeatherStorage
import at.shockbytes.weather.OwmWeatherRepository
import at.shockbytes.weather.WeatherRepository
import at.shockbytes.weather.WeatherStorage
import at.shockbytes.weather.WeatherValidityOptions
import at.shockbytes.weather.owm.OwmWeatherApi
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
    fun provideWeatherRepository(
        owmWeatherApi: OwmWeatherApi,
        weatherStorage: WeatherStorage,
        validityOptions: WeatherValidityOptions
    ): WeatherRepository {
        return OwmWeatherRepository(owmWeatherApi, weatherStorage, validityOptions)
    }

    @Provides
    @Reusable
    fun provideScheduleWeatherResolver(): ScheduleWeatherResolver {
        return TestScheduleWeatherResolver()
    }
}