package at.shockbytes.corey.core

import androidx.work.Configuration
import androidx.work.WorkManager
import at.shockbytes.core.ShockbytesApp
import at.shockbytes.corey.dagger.AppComponent
import at.shockbytes.corey.dagger.AppModule
import at.shockbytes.corey.dagger.DaggerAppComponent
import at.shockbytes.corey.dagger.WorkerFactory
import at.shockbytes.corey.dagger.WorkoutModule
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

    override fun onCreate() {
        super.onCreate()
        appComponent.inject(this)
        initializeWorkManager()
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

    override fun setupCustomLogging() = Unit

    override fun setupInjectionAppComponent(): AppComponent {
        return DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .workoutModule(WorkoutModule(this))
            .build()
    }
}
