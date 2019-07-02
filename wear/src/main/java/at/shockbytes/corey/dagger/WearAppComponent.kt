package at.shockbytes.corey.dagger

import at.shockbytes.corey.ui.activity.WearMainActivity
import at.shockbytes.corey.ui.fragment.WearRunningFragment
import at.shockbytes.corey.ui.fragment.WorkoutFragment
import at.shockbytes.corey.ui.fragment.workoutpager.WearTimeExercisePagerFragment
import dagger.Component
import javax.inject.Singleton

/**
 * Author:  Martin Macheiner
 * Date:    21.02.2017
 */
@Singleton
@Component(modules = [(WearAppModule::class), (WearViewModelModule::class)])
interface WearAppComponent {

    fun inject(activity: WearMainActivity)

    fun inject(fragment: WorkoutFragment)

    fun inject(fragment: WearRunningFragment)

    fun inject(fragment: WearTimeExercisePagerFragment)
}
