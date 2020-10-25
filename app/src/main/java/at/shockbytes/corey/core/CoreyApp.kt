package at.shockbytes.corey.core

import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.work.Configuration
import androidx.work.WorkManager
import at.shockbytes.core.ShockbytesApp
import at.shockbytes.corey.dagger.*
import at.shockbytes.corey.data.settings.CoreySettings
import at.shockbytes.corey.data.settings.ThemeState
import net.danlew.android.joda.JodaTimeAndroid
import javax.inject.Inject

/**
 * Author:  Martin Macheiner
 * Date:    21.02.2017
 */
class CoreyApp : ShockbytesApp<AppComponent>() {

    override val useCrashlytics: Boolean = true
    override val useStrictModeInDebug: Boolean = false

    @Inject
    lateinit var myWorkerFactory: WorkerFactory

    @Inject
    lateinit var coreySettings: CoreySettings

    override fun onCreate() {
        super.onCreate()
        appComponent.inject(this)
        initializeWorkManager()
        setupDarkMode()
    }

    override fun initializeLibraries() {
        JodaTimeAndroid.init(this)
    }

    private fun initializeWorkManager() {
        WorkManager.initialize(
            this,
            Configuration.Builder()
                .setWorkerFactory(myWorkerFactory)
                .build()
        )
    }

    private fun setupDarkMode() {
        setupTheme(coreySettings.themeState)
    }

    private fun setupTheme(theme: ThemeState) {
        Toast.makeText(this, theme.name, Toast.LENGTH_SHORT).show()
        AppCompatDelegate.setDefaultNightMode(theme.themeMode)
    }


    override fun setupCustomLogging() = Unit

    override fun setupInjectionAppComponent(): AppComponent {
        return DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .workoutModule(WorkoutModule(this))
            .bodyModule(BodyModule(this))
            .build()
    }
}
