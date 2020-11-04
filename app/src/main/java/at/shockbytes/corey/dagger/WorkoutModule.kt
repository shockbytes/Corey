package at.shockbytes.corey.dagger

import android.app.Application
import android.content.SharedPreferences
import at.shockbytes.corey.common.core.location.LocationRepository
import at.shockbytes.corey.common.core.running.DefaultRunningManager
import at.shockbytes.corey.common.core.running.RunningManager
import at.shockbytes.corey.data.firebase.FirebaseDatabaseAccess
import at.shockbytes.corey.data.google.CoreyGoogleApiClient
import at.shockbytes.corey.data.workout.FirebaseWorkoutRepository
import at.shockbytes.corey.data.workout.WorkoutRepository
import at.shockbytes.corey.data.workout.external.DummyExternalWorkoutRepository
import at.shockbytes.corey.data.workout.external.ExternalWorkoutRepository
import at.shockbytes.corey.storage.running.RunningStorageRepository
import at.shockbytes.corey.storage.running.SharedPreferencesRunningStorageRepository
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
        firebase: FirebaseDatabaseAccess
    ): WorkoutRepository {
        return FirebaseWorkoutRepository(app.applicationContext, gson, remoteConfig, firebase)
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
    fun provideExternalWorkoutRepository(
        coreyGoogleApiClient: CoreyGoogleApiClient
    ): ExternalWorkoutRepository {
        return DummyExternalWorkoutRepository()
    }
}