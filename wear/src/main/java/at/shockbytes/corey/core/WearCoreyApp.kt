package at.shockbytes.corey.core

import android.app.Application

import net.danlew.android.joda.JodaTimeAndroid

import at.shockbytes.corey.dagger.DaggerWearAppComponent
import at.shockbytes.corey.dagger.WearAppComponent
import at.shockbytes.corey.dagger.WearAppModule

/**
 * @author Martin Macheiner
 * Date: 21.02.2017.
 */
class WearCoreyApp : Application() {

    lateinit var appComponent: WearAppComponent
        private set

    override fun onCreate() {
        super.onCreate()

        JodaTimeAndroid.init(this)

        appComponent = DaggerWearAppComponent.builder()
                .wearAppModule(WearAppModule(this))
                .build()
    }
}
