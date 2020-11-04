package at.shockbytes.corey.dagger

import at.shockbytes.core.scheduler.SchedulerFacade
import at.shockbytes.corey.data.body.BodyRepository
import at.shockbytes.corey.data.body.bmr.BmrComputation
import at.shockbytes.corey.data.firebase.FirebaseDatabaseAccess
import at.shockbytes.corey.data.nutrition.FirebaseNutritionRepository
import at.shockbytes.corey.data.nutrition.NutritionRepository
import at.shockbytes.corey.data.nutrition.lookup.KcalLookup
import at.shockbytes.corey.data.nutrition.lookup.edamam.EdamamApi
import at.shockbytes.corey.data.nutrition.lookup.edamam.EdamamKcalLookup
import at.shockbytes.corey.data.nutrition.lookup.usda.UsdaApi
import at.shockbytes.corey.data.workout.external.ExternalWorkoutRepository
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
    @Singleton
    fun provideNutritionRepository(
        firebase: FirebaseDatabaseAccess,
        schedulers: SchedulerFacade,
        externalWorkoutRepository: ExternalWorkoutRepository,
        bodyRepository: BodyRepository,
        bmrComputation: BmrComputation
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
    fun provideUsdaApi(okHttpClient: OkHttpClient): UsdaApi {
        return Retrofit.Builder()
            .baseUrl(UsdaApi.SERVICE_ENDPOINT)
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .build()
            .create(UsdaApi::class.java)
    }

    @Provides
    @Singleton
    fun provideEdamamApi(okHttpClient: OkHttpClient): EdamamApi {
        return Retrofit.Builder()
            .baseUrl(EdamamApi.SERVICE_ENDPOINT)
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .build()
            .create(EdamamApi::class.java)
    }

    @Provides
    fun provideKcalLookup(
        api: EdamamApi,
        schedulers: SchedulerFacade
    ): KcalLookup {
        return EdamamKcalLookup(api, schedulers)
    }
}