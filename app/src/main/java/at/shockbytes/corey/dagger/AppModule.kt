package at.shockbytes.corey.dagger

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.Vibrator
import android.preference.PreferenceManager
import at.shockbytes.core.image.GlideImageLoader
import at.shockbytes.core.image.ImageLoader
import at.shockbytes.core.scheduler.AppSchedulerFacade
import at.shockbytes.core.scheduler.SchedulerFacade
import at.shockbytes.corey.R
import at.shockbytes.corey.common.core.location.GmsLocationRepository
import at.shockbytes.corey.common.core.location.LocationRepository
import at.shockbytes.corey.common.core.running.location.GooglePlayLocationManager
import at.shockbytes.corey.common.core.running.location.LocationManager
import at.shockbytes.corey.common.core.util.CoreySettings
import at.shockbytes.corey.common.core.util.ExerciseDeserializer
import at.shockbytes.corey.common.core.workout.model.Exercise
import at.shockbytes.corey.data.body.BodyRepository
import at.shockbytes.corey.data.reminder.DefaultReminderManager
import at.shockbytes.corey.data.reminder.ReminderManager
import at.shockbytes.corey.data.schedule.FirebaseScheduleRepository
import at.shockbytes.corey.data.schedule.ScheduleRepository
import at.shockbytes.corey.data.user.FirebaseUserRepository
import at.shockbytes.corey.data.user.UserRepository
import at.shockbytes.corey.wearable.AndroidWearManager
import at.shockbytes.corey.wearable.WearableManager
import at.shockbytes.corey.data.workout.WorkoutRepository
import at.shockbytes.corey.storage.KeyValueStorage
import at.shockbytes.corey.storage.SharedPreferencesKeyValueStorage
import at.shockbytes.corey.util.ReactiveFirebaseCoreySettings
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.Reusable
import javax.inject.Singleton

/**
 * Author:  Martin Macheiner
 * Date:    21.02.2017
 */
@Module
class AppModule(private val app: Application) {

    @Provides
    @Singleton
    fun provideSharedPreferences(): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(app)
    }

    @Provides
    @Singleton
    fun provideKeyValueStorage(prefs: SharedPreferences): KeyValueStorage {
        return SharedPreferencesKeyValueStorage(prefs)
    }

    @Provides
    @Singleton
    fun provideUserManager(): UserRepository {
        return FirebaseUserRepository(app.applicationContext)
    }

    @Provides
    @Singleton
    fun provideSchedulerFacade(): SchedulerFacade {
        return AppSchedulerFacade()
    }

    @Provides
    @Singleton
    fun provideScheduleManager(
        gson: Gson,
        workoutManager: WorkoutRepository,
        remoteConfig: FirebaseRemoteConfig,
        firebase: FirebaseDatabase,
        schedulerFacade: SchedulerFacade
    ): ScheduleRepository {
        return FirebaseScheduleRepository(
            app.applicationContext,
            gson,
            workoutManager,
            remoteConfig,
            firebase,
            schedulerFacade
        )
    }

    @Provides
    @Singleton
    fun provideWearableManager(workoutManager: WorkoutRepository, gson: Gson): WearableManager {
        return AndroidWearManager(app.applicationContext, workoutManager, gson)
    }

    @Provides
    @Singleton
    fun provideLocationManager(): LocationManager {
        return GooglePlayLocationManager(app.applicationContext)
    }

    @Provides
    @Singleton
    fun provideVibrator(): Vibrator {
        return app.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
                .registerTypeHierarchyAdapter(Exercise::class.java, ExerciseDeserializer())
                .create()
    }

    @Provides
    @Singleton
    fun provideCoreySettings(
            sharedPrefs: SharedPreferences,
            firebase: FirebaseDatabase
    ): CoreySettings {
        return ReactiveFirebaseCoreySettings(app.applicationContext, sharedPrefs, firebase)
    }

    @Provides
    @Singleton
    fun provideLocationRepository(): LocationRepository {
        return GmsLocationRepository(app.applicationContext)
    }

    @Provides
    @Reusable
    fun provideReminderManager(
        localStorage: KeyValueStorage,
        scheduleRepository: ScheduleRepository,
        bodyRepository: BodyRepository
    ): ReminderManager {
        return DefaultReminderManager(localStorage, scheduleRepository, bodyRepository)
    }

    @Provides
    @Singleton
    fun provideImageLoader(): ImageLoader {
        return GlideImageLoader(R.drawable.ic_placeholder)
    }
}
