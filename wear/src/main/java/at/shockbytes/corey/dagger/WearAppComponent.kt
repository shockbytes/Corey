package at.shockbytes.corey.dagger

import at.shockbytes.corey.ui.activity.MainActivity
import at.shockbytes.corey.ui.fragment.WorkoutFragment
import dagger.Component
import javax.inject.Singleton

/**
 * @author  Martin Macheiner
 * Date:    21.02.2017.
 */
@Singleton
@Component(modules = [(WearAppModule::class)])
interface WearAppComponent {

    fun inject(activity: MainActivity)

    fun inject(fragment: WorkoutFragment)

}
