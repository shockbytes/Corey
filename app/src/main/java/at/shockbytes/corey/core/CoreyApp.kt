package at.shockbytes.corey.core

import at.shockbytes.core.ShockbytesApp
import at.shockbytes.corey.dagger.AppComponent
import at.shockbytes.corey.dagger.AppModule
import at.shockbytes.corey.dagger.DaggerAppComponent
import at.shockbytes.corey.dagger.WorkoutModule
import net.danlew.android.joda.JodaTimeAndroid

/**
 * Author:  Martin Macheiner
 * Date:    21.02.2017
 */
class CoreyApp : ShockbytesApp<AppComponent>() {

    override val useCrashlytics: Boolean = true
    override val useStrictModeInDebug: Boolean = false

    override fun initializeLibraries() {
        JodaTimeAndroid.init(this)
    }

    override fun setupCustomLogging() = Unit

    override fun setupInjectionAppComponent(): AppComponent {
        return DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .workoutModule(WorkoutModule(this))
                .build()
    }
}
