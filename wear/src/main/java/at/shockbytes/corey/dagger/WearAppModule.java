package at.shockbytes.corey.dagger;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Singleton;

import at.shockbytes.corey.common.core.util.ExerciseDeserializer;
import at.shockbytes.corey.common.core.workout.model.Exercise;
import at.shockbytes.corey.core.CommunicationManager;
import at.shockbytes.corey.util.MediaButtonHandler;
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
    public CommunicationManager provideCommunicationManager(SharedPreferences preferences,
                                                            Gson gson) {
        return new CommunicationManager(app.getApplicationContext(), preferences, gson);
    }

    @Provides
    @Singleton
    public Vibrator provideVibrator() {
        return (Vibrator) app.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Provides
    @Singleton
    public MediaButtonHandler provideMediaButtonHandler() {
        return new MediaButtonHandler(app.getApplicationContext());
    }

    @Provides
    @Singleton
    public Gson provideGson() {
        return new GsonBuilder()
                .registerTypeHierarchyAdapter(Exercise.class, new ExerciseDeserializer())
                .create();
    }
}
