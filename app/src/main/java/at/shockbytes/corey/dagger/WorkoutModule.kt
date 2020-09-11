package at.shockbytes.corey.dagger

import android.app.Application
import android.content.SharedPreferences
import at.shockbytes.corey.common.core.location.LocationRepository
import at.shockbytes.corey.data.body.BodyRepository
import at.shockbytes.corey.data.body.GoogleFitBodyRepository
import at.shockbytes.corey.common.core.running.DefaultRunningManager
import at.shockbytes.corey.common.core.running.RunningManager
import at.shockbytes.corey.data.body.bmr.Bmr
import at.shockbytes.corey.data.body.bmr.BmrComputation
import at.shockbytes.corey.data.body.bmr.RevisedHarrisBenedictBmrComputation
import at.shockbytes.corey.data.goal.FirebaseGoalsRepository
import at.shockbytes.corey.data.goal.GoalsRepository
import at.shockbytes.corey.data.workout.FirebaseWorkoutRepository
import at.shockbytes.corey.data.workout.WorkoutRepository
import at.shockbytes.corey.storage.running.RunningStorageRepository
import at.shockbytes.corey.storage.running.SharedPreferencesRunningStorageRepository
import at.shockbytes.corey.ui.fragment.body.weight.filter.RawWeightLineFilter
import at.shockbytes.corey.ui.fragment.body.weight.filter.RunningAverageWeightLineFilter
import at.shockbytes.corey.ui.fragment.body.weight.filter.WeightLineFilter
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.Reusable
import javax.inject.Singleton

/**
 * Author:  Martin Macheiner
 * Date:    04.03.2018
 */
@Module
class WorkoutModule(private val app: Application) {

    @Provides
    @Singleton
    fun provideWorkoutManager(
        gson: Gson,
        remoteConfig: FirebaseRemoteConfig,
        firebase: FirebaseDatabase
    ): WorkoutRepository {
        return FirebaseWorkoutRepository(app.applicationContext, gson, remoteConfig, firebase)
    }

    @Provides
    fun provideBmrComputation(): BmrComputation {
        return RevisedHarrisBenedictBmrComputation()
    }

    @Provides
    @Singleton
    fun provideBodyManager(
        preferences: SharedPreferences,
        firebase: FirebaseDatabase,
        bmrComputation: BmrComputation
    ): BodyRepository {
        return GoogleFitBodyRepository(app.applicationContext, preferences, firebase, bmrComputation)
    }

    @Provides
    @Singleton
    fun provideGoalsRepository(firebase: FirebaseDatabase): GoalsRepository {
        return FirebaseGoalsRepository(firebase)
    }

    @Provides
    @Singleton
    fun provideRunningManager(locationRepository: LocationRepository): RunningManager {
        return DefaultRunningManager(locationRepository)
    }

    @Provides
    @Reusable
    fun provideRunningStorageRepository(
        sharedPreferences: SharedPreferences
    ): RunningStorageRepository {
        return SharedPreferencesRunningStorageRepository(sharedPreferences)
    }

    @Provides
    @Reusable
    fun provideWeightLineFilters(): Array<WeightLineFilter> {
        return arrayOf(RawWeightLineFilter(), RunningAverageWeightLineFilter())
    }
}