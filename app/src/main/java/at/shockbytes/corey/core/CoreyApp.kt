package at.shockbytes.corey.core

import android.app.Application
import at.shockbytes.corey.dagger.AppComponent
import at.shockbytes.corey.dagger.AppModule
import at.shockbytes.corey.dagger.DaggerAppComponent
import at.shockbytes.corey.dagger.WorkoutModule
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import net.danlew.android.joda.JodaTimeAndroid


/**
 * @author  Martin Macheiner
 * Date:    21.02.2017
 */
class CoreyApp : Application() {

    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()

        JodaTimeAndroid.init(this)

        Fabric.with(Fabric.Builder(this)
                .kits(Crashlytics())
                .debuggable(true)
                .build())

        appComponent = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .workoutModule(WorkoutModule(this))
                .build()
    }
}
