package at.shockbytes.corey.dagger;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Singleton;

import at.shockbytes.corey.body.wearable.AndroidWearManager;
import at.shockbytes.corey.body.wearable.WearableManager;
import at.shockbytes.corey.storage.RealmFireStorageManager;
import at.shockbytes.corey.storage.StorageManager;
import at.shockbytes.corey.common.core.util.ExerciseDeserializer;
import at.shockbytes.corey.util.schedule.DefaultScheduleManager;
import at.shockbytes.corey.util.schedule.ScheduleManager;
import at.shockbytes.corey.workout.DefaultWorkoutManager;
import at.shockbytes.corey.workout.WorkoutManager;
import at.shockbytes.corey.body.BodyManager;
import at.shockbytes.corey.body.GoogleFitBodyManager;
import at.shockbytes.corey.common.core.workout.model.Exercise;
import dagger.Module;
import dagger.Provides;

/**
 * @author Martin Macheiner
 *         Date: 21.02.2017.
 */
@Module
public class AppModule {

    private Application app;

    public AppModule(Application app) {
        this.app = app;
    }

    @Provides
    @Singleton
    public SharedPreferences provideSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(app);
    }

    @Provides
    @Singleton
    public WorkoutManager provideTrainingManager(StorageManager storageManager) {
        return new DefaultWorkoutManager(storageManager);
    }

    @Provides
    @Singleton
    public ScheduleManager provideScheduleManager(StorageManager storageManager,
                                                  SharedPreferences preferences) {
        return new DefaultScheduleManager(storageManager, app.getApplicationContext(), preferences);
    }

    @Provides
    @Singleton
    public BodyManager provideBodyManager(StorageManager storageManager) {
        return new GoogleFitBodyManager(app.getApplicationContext(), storageManager);
    }

    @Provides
    @Singleton
    public StorageManager provideStorageManager(SharedPreferences preferences, Gson gson) {
        return new RealmFireStorageManager(app.getApplicationContext(), gson, preferences);
    }

    @Provides
    @Singleton
    public WearableManager provideWearableManager(WorkoutManager workoutManager,
                                                  StorageManager storageManager,
                                                  SharedPreferences preferences, Gson gson) {
        return new AndroidWearManager(app.getApplicationContext(), workoutManager, storageManager,
                preferences, gson);
    }

    @Provides
    @Singleton
    public Gson provideGson() {
        return new GsonBuilder()
                .registerTypeHierarchyAdapter(Exercise.class, new ExerciseDeserializer())
                .create();
    }

}
