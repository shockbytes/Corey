package at.shockbytes.corey.dagger

import at.shockbytes.corey.core.receiver.NotificationReceiver
import at.shockbytes.corey.core.receiver.OnBootReceiver
import at.shockbytes.corey.ui.activity.MainActivity
import at.shockbytes.corey.ui.fragment.BodyFragment
import at.shockbytes.corey.ui.fragment.ScheduleFragment
import at.shockbytes.corey.ui.fragment.WorkoutFragment
import at.shockbytes.corey.ui.fragment.WorkoutOverviewFragment
import at.shockbytes.corey.ui.fragment.dialogs.AddExercisesDialogFragment
import at.shockbytes.corey.ui.fragment.dialogs.DesiredWeightDialogFragment
import at.shockbytes.corey.ui.fragment.dialogs.InsertScheduleDialogFragment
import dagger.Component
import javax.inject.Singleton

/**
 * @author Martin Macheiner
 * Date: 21.02.2017.
 */
@Singleton
@Component(modules = [AppModule::class, WorkoutModule::class])
interface AppComponent {

    fun inject(activity: MainActivity)

    fun inject(fragment: BodyFragment)

    fun inject(fragment: ScheduleFragment)

    fun inject(fragment: WorkoutOverviewFragment)

    fun inject(fragment: WorkoutFragment)

    fun inject(dialogFragment: AddExercisesDialogFragment)

    fun inject(dialogFragment: DesiredWeightDialogFragment)

    fun inject(dialogFragment: InsertScheduleDialogFragment)

    fun inject(broadcastReceiver: NotificationReceiver)

    fun inject(broadcastReceiver: OnBootReceiver)

}
