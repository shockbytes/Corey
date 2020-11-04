package at.shockbytes.corey.dagger

import at.shockbytes.corey.R
import at.shockbytes.corey.data.firebase.SingleUserFirebaseAccess
import at.shockbytes.corey.data.firebase.FirebaseDatabaseAccess
import at.shockbytes.corey.data.firebase.UserScopedFirebaseDatabaseAccess
import at.shockbytes.corey.data.settings.CoreySettings
import at.shockbytes.corey.data.settings.FirebaseDatabaseAccessMode
import com.google.firebase.auth.FirebaseAuth
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
            setDefaultsAsync(R.xml.remote_config_defaults).addOnSuccessListener {
                Timber.d("Defaults set async")
            }
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

    @Provides
    @Singleton
    fun provideFirebaseDatabaseAccess(
        coreySettings: CoreySettings,
        database: FirebaseDatabase
    ): FirebaseDatabaseAccess {

        return when (coreySettings.firebaseAccessMode) {
            FirebaseDatabaseAccessMode.SINGLE_ACCESS -> SingleUserFirebaseAccess(database)
            FirebaseDatabaseAccessMode.USER_SCOPED_ACCESS -> {
                UserScopedFirebaseDatabaseAccess(database, FirebaseAuth.getInstance())
            }
        }
    }

    companion object {

        const val REF_RESPONSE_TIME_METRICS = "/metrics/response_times/"
    }
}