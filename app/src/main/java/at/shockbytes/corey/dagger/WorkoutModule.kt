package at.shockbytes.corey.dagger

import android.app.Application
import at.shockbytes.corey.body.BodyManager
import at.shockbytes.corey.body.GoogleFitBodyManager
import at.shockbytes.corey.common.core.running.DefaultRunningManager
import at.shockbytes.corey.common.core.running.RunningManager
import at.shockbytes.corey.storage.StorageManager
import at.shockbytes.corey.workout.DefaultWorkoutManager
import at.shockbytes.corey.workout.WorkoutManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * @author Martin Macheiner
 * Date: 04-Mar-18.
 */

@Module
class WorkoutModule(private val app: Application) {

    @Provides
    @Singleton
    fun provideWorkoutManager(storageManager: StorageManager): WorkoutManager {
        return DefaultWorkoutManager(storageManager)
    }

    @Provides
    @Singleton
    fun provideBodyManager(storageManager: StorageManager): BodyManager {
        return GoogleFitBodyManager(app.applicationContext, storageManager)
    }

    @Provides
    @Singleton
    fun provideRunningManager(): RunningManager {
        return DefaultRunningManager()
    }


}