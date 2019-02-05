package at.shockbytes.corey.dagger

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.Vibrator
import android.preference.PreferenceManager
import at.shockbytes.corey.common.core.util.ExerciseDeserializer
import at.shockbytes.corey.common.core.workout.model.Exercise
import at.shockbytes.corey.core.CommunicationManager
import at.shockbytes.corey.util.MediaButtonHandler
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * @author Martin Macheiner
 * Date: 21.02.2017.
 */
@Module
class WearAppModule(private val app: Application) {

    @Provides
    @Singleton
    fun provideSharedPreferences(): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(app)
    }

    @Provides
    @Singleton
    fun provideCommunicationManager(
        preferences: SharedPreferences,
        gson: Gson
    ): CommunicationManager {
        return CommunicationManager(app.applicationContext, preferences, gson)
    }

    @Provides
    @Singleton
    fun provideVibrator(): Vibrator {
        return app.applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    @Provides
    @Singleton
    fun provideMediaButtonHandler(): MediaButtonHandler {
        return MediaButtonHandler(app.applicationContext)
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
                .registerTypeHierarchyAdapter(Exercise::class.java, ExerciseDeserializer())
                .create()
    }
}
