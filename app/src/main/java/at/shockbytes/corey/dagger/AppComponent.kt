package at.shockbytes.corey.dagger

import at.shockbytes.core.ShockbytesInjector
import at.shockbytes.corey.core.receiver.NotificationReceiver
import at.shockbytes.corey.core.receiver.OnBootReceiver
import at.shockbytes.corey.ui.activity.MainActivity
import at.shockbytes.corey.ui.fragment.MenuFragment
import at.shockbytes.corey.ui.fragment.SettingsFragment
import at.shockbytes.corey.ui.fragment.WorkoutFragment
import at.shockbytes.corey.ui.fragment.body.ProfileBodyFragmentView
import at.shockbytes.corey.ui.fragment.dialog.AddExercisesDialogFragment
import at.shockbytes.corey.ui.fragment.dialog.DesiredWeightDialogFragment
import at.shockbytes.corey.ui.fragment.dialog.InsertScheduleDialogFragment
import at.shockbytes.corey.ui.fragment.pager.BodyFragment
import at.shockbytes.corey.ui.fragment.pager.GoalsFragment
import at.shockbytes.corey.ui.fragment.pager.ScheduleFragment
import at.shockbytes.corey.ui.fragment.pager.WorkoutOverviewFragment
import at.shockbytes.corey.ui.fragment.workoutpager.TimeExercisePagerFragment
import dagger.Component
import javax.inject.Singleton

/**
 * @author Martin Macheiner
 * Date: 21.02.2017.
 */
@Singleton
@Component(modules = [
    AppModule::class,
    WorkoutModule::class,
    FirebaseModule::class,
    ViewModelModule::class
])
interface AppComponent: ShockbytesInjector {

    fun inject(activity: MainActivity)

    fun inject(fragment: BodyFragment)

    fun inject(fragment: ScheduleFragment)

    fun inject(fragment: WorkoutOverviewFragment)

    fun inject(fragment: WorkoutFragment)

    fun inject(fragment: TimeExercisePagerFragment)

    fun inject(fragment: SettingsFragment)

    fun inject(dialogFragment: AddExercisesDialogFragment)

    fun inject(dialogFragment: DesiredWeightDialogFragment)

    fun inject(dialogFragment: InsertScheduleDialogFragment)

    fun inject(broadcastReceiver: NotificationReceiver)

    fun inject(broadcastReceiver: OnBootReceiver)

    fun inject(fragment: ProfileBodyFragmentView)

    fun inject(fragment: GoalsFragment)

    fun inject(fragment: MenuFragment)
}
