package at.shockbytes.corey.dagger

import android.app.Application
import android.content.SharedPreferences
import at.shockbytes.corey.body.BodyRepository
import at.shockbytes.corey.body.GoogleFitBodyRepository
import at.shockbytes.corey.common.core.running.DefaultRunningManager
import at.shockbytes.corey.common.core.running.RunningManager
import at.shockbytes.corey.workout.FirebaseWorkoutRepository
import at.shockbytes.corey.workout.WorkoutRepository
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * @author  Martin Macheiner
 * Date:    04.03.2018
 */

@Module
class WorkoutModule(private val app: Application) {

    @Provides
    @Singleton
    fun provideWorkoutManager(gson: Gson,
                              remoteConfig: FirebaseRemoteConfig,
                              firebase: FirebaseDatabase): WorkoutRepository {
        return FirebaseWorkoutRepository(app.applicationContext, gson, remoteConfig, firebase)
    }

    @Provides
    @Singleton
    fun provideBodyManager(preferences: SharedPreferences,
                           firebase: FirebaseDatabase): BodyRepository {
        return GoogleFitBodyRepository(app.applicationContext, preferences, firebase)
    }

    @Provides
    @Singleton
    fun provideRunningManager(): RunningManager {
        return DefaultRunningManager()
    }


}