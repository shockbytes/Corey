package at.shockbytes.corey.dagger

import at.shockbytes.corey.R
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Logger
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import dagger.Module
import dagger.Provides
import timber.log.Timber
import javax.inject.Singleton

/**
 * Author:  Martin Macheiner
 * Date:    18.03.2018
 */
@Module
class FirebaseModule {

    @Provides
    @Singleton
    fun provideRemoteConfig(): FirebaseRemoteConfig {
        val configSettings = FirebaseRemoteConfigSettings.Builder().build()
        return FirebaseRemoteConfig.getInstance().apply {
            setConfigSettingsAsync(configSettings).addOnSuccessListener {
                Timber.d("RemoteConfigSettings set async")
            }
            setDefaults(R.xml.remote_config_defaults)
        }
    }

    @Provides
    @Singleton
    fun provideRealtimeDatabase(): FirebaseDatabase {
        return FirebaseDatabase.getInstance()
            .apply {
                setPersistenceEnabled(true)
                setLogLevel(Logger.Level.DEBUG)
            }
            .reference
            .database
    }

    companion object {

        const val REF_RESPONSE_TIME_METRICS = "/metrics/response_times/"
    }
}