package at.shockbytes.corey.dagger;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import javax.inject.Singleton;

import at.shockbytes.corey.workout.DefaultWearableWorkoutManager;
import at.shockbytes.corey.workout.WearableWorkoutManager;
import dagger.Module;
import dagger.Provides;

/**
 * @author Martin Macheiner
 *         Date: 21.02.2017.
 */
@Module
public class WearAppModule {

    private Application app;

    public WearAppModule(Application app) {
        this.app = app;
    }

    @Provides
    @Singleton
    public SharedPreferences provideSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(app);
    }

    @Provides
    @Singleton
    public WearableWorkoutManager provideTrainingManager() {
        return new DefaultWearableWorkoutManager();
    }


}
