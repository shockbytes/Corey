package at.shockbytes.corey.dagger;

import javax.inject.Singleton;

import at.shockbytes.corey.core.MainActivity;
import at.shockbytes.corey.core.receiver.NotificationReceiver;
import at.shockbytes.corey.core.receiver.OnBootReceiver;
import at.shockbytes.corey.fragment.BodyFragment;
import at.shockbytes.corey.fragment.RunningFragment;
import at.shockbytes.corey.fragment.ScheduleFragment;
import at.shockbytes.corey.fragment.WorkoutOverviewFragment;
import at.shockbytes.corey.fragment.dialogs.AddExercisesDialogFragment;
import at.shockbytes.corey.fragment.dialogs.DesiredWeightDialogFragment;
import at.shockbytes.corey.fragment.dialogs.InsertScheduleDialogFragment;
import dagger.Component;

/**
 * @author Martin Macheiner
 *         Date: 21.02.2017.
 */
@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {

	void inject(MainActivity activity);

	void inject(BodyFragment fragment);

    void inject(ScheduleFragment fragment);

    void inject(WorkoutOverviewFragment fragment);

    void inject(RunningFragment fragment);

    void inject(AddExercisesDialogFragment dialogFragment);

    void inject(DesiredWeightDialogFragment dialogFragment);

    void inject(InsertScheduleDialogFragment dialogFragment);

    void inject(NotificationReceiver broadcastReceiver);

    void inject(OnBootReceiver broadcastReceiver);

}
