package at.shockbytes.corey.dagger;

import javax.inject.Singleton;

import at.shockbytes.corey.core.MainActivity;
import at.shockbytes.corey.fragment.MainFragment;
import at.shockbytes.corey.fragment.WorkoutFragment;
import dagger.Component;

/**
 * @author Martin Macheiner
 *         Date: 21.02.2017.
 */
@Singleton
@Component(modules = {WearAppModule.class})
public interface WearAppComponent {

	void inject(MainActivity activity);

	void inject(MainFragment fragment);

    void inject(WorkoutFragment fragment);

}
