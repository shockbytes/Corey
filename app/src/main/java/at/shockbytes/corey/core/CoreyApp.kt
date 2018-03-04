package at.shockbytes.corey.core

import android.app.Application
import at.shockbytes.corey.dagger.AppComponent
import at.shockbytes.corey.dagger.AppModule
import at.shockbytes.corey.dagger.DaggerAppComponent
import at.shockbytes.corey.dagger.WorkoutModule
import io.realm.Realm
import net.danlew.android.joda.JodaTimeAndroid

/**
 * @author Martin Macheiner
 * Date: 21.02.2017.
 */
class CoreyApp : Application() {

    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()

        Realm.init(this)
        JodaTimeAndroid.init(this)

        appComponent = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .workoutModule(WorkoutModule(this))
                .build()
    }
}
