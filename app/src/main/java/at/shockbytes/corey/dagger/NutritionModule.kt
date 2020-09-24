package at.shockbytes.corey.dagger

import at.shockbytes.core.scheduler.SchedulerFacade
import at.shockbytes.corey.data.body.BodyRepository
import at.shockbytes.corey.data.body.bmr.BmrComputation
import at.shockbytes.corey.data.nutrition.FirebaseNutritionRepository
import at.shockbytes.corey.data.nutrition.NutritionRepository
import at.shockbytes.corey.data.nutrition.lookup.KcalLookup
import at.shockbytes.corey.data.nutrition.lookup.usda.UsdaApi
import at.shockbytes.corey.data.nutrition.lookup.usda.UsdaKcalLookup
import at.shockbytes.corey.data.workout.external.ExternalWorkoutRepository
import at.shockbytes.weather.owm.OwmWeatherApi
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class NutritionModule {

    @Provides
    fun provideNutritionRepository(
            firebase: FirebaseDatabase,
            schedulers: SchedulerFacade,
            externalWorkoutRepository: ExternalWorkoutRepository,
            bodyRepository: BodyRepository,
            bmrComputation: BmrComputation,
    ): NutritionRepository {
        return FirebaseNutritionRepository(
                firebase,
                schedulers,
                externalWorkoutRepository,
                bodyRepository,
                bmrComputation
        )
    }


    @Provides
    @Singleton
    fun provideOwmWeatherApi(okHttpClient: OkHttpClient): UsdaApi {
        return Retrofit.Builder()
                .baseUrl(UsdaApi.SERVICE_ENDPOINT)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(Gson()))
                .build()
                .create(UsdaApi::class.java)
    }

    @Provides
    fun provideKcalLookup(
            usdaApi: UsdaApi,
            schedulers: SchedulerFacade
    ): KcalLookup {
        return UsdaKcalLookup(usdaApi, schedulers)
    }
}