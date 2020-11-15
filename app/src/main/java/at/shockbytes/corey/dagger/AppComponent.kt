package at.shockbytes.corey.dagger

import at.shockbytes.core.ShockbytesInjector
import at.shockbytes.corey.core.CoreyApp
import at.shockbytes.corey.ui.activity.MainActivity
import at.shockbytes.corey.ui.fragment.AddNutritionEntryFragment
import at.shockbytes.corey.ui.fragment.MenuFragment
import at.shockbytes.corey.ui.fragment.ReminderFragment
import at.shockbytes.corey.ui.fragment.SettingsFragment
import at.shockbytes.corey.ui.fragment.WorkoutFragment
import at.shockbytes.corey.ui.fragment.body.GoalsFragment
import at.shockbytes.corey.ui.fragment.body.ProfileBodyFragmentView
import at.shockbytes.corey.ui.fragment.dialog.AddExercisesDialogFragment
import at.shockbytes.corey.ui.fragment.dialog.DesiredWeightDialogFragment
import at.shockbytes.corey.ui.fragment.dialog.InsertScheduleDialogFragment
import at.shockbytes.corey.ui.fragment.dialog.NutritionLookupBottomsheetFragment
import at.shockbytes.corey.ui.fragment.tab.BodyFragment
import at.shockbytes.corey.ui.fragment.tab.NutritionFragment
import at.shockbytes.corey.ui.fragment.tab.RunningFragment
import at.shockbytes.corey.ui.fragment.tab.ScheduleFragment
import at.shockbytes.corey.ui.fragment.tab.WorkoutOverviewFragment
import at.shockbytes.corey.ui.fragment.workoutpager.TimeExercisePagerFragment
import dagger.Component
import javax.inject.Singleton

/**
 * Author:  Martin Macheiner
 * Date:    21.02.2017
 */
@Singleton
@Component(modules = [
    AppModule::class,
    WorkoutModule::class,
    FirebaseModule::class,
    ViewModelModule::class,
    WeatherModule::class,
    NetModule::class,
    WorkerModule::class,
    BodyModule::class,
    NutritionModule::class
])
interface AppComponent : ShockbytesInjector {

    fun inject(app: CoreyApp)

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

    fun inject(fragment: ProfileBodyFragmentView)

    fun inject(fragment: GoalsFragment)

    fun inject(fragment: MenuFragment)

    fun inject(fragment: ReminderFragment)

    fun inject(fragment: RunningFragment)

    fun inject(fragment: NutritionFragment)

    fun inject(fragment: AddNutritionEntryFragment)

    fun inject(fragment: NutritionLookupBottomsheetFragment)
}
