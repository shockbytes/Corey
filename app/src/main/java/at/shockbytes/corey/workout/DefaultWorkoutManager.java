package at.shockbytes.corey.workout;

import java.util.List;

import javax.inject.Inject;

import at.shockbytes.corey.common.core.workout.model.Exercise;
import at.shockbytes.corey.common.core.workout.model.Workout;
import at.shockbytes.corey.storage.StorageManager;
import at.shockbytes.corey.storage.live.LiveWorkoutUpdateListener;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Martin Macheiner
 *         Date: 22.02.2017.
 */

public class DefaultWorkoutManager implements WorkoutManager {

    private StorageManager storageManager;

    @Inject
    public DefaultWorkoutManager(StorageManager storageManager) {
        this.storageManager = storageManager;
    }

    @Override
    public void poke() {
        storageManager.pokeExercisesAndSchedulingItems();
    }

    @Override
    public void addWorkout(Workout t) {
        storageManager.storeWorkout(t);
    }

    @Override
    public void deleteWorkout(Workout t) {
        storageManager.deleteWorkout(t);
    }

    @Override
    public void updateWorkout(Workout t) {
        storageManager.updateWorkout(t);
    }

    @Override
    public Observable<List<Workout>> getWorkouts() {
        return storageManager.getWorkouts()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<Exercise>> getExercises() {
        return storageManager.getExercises()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.computation());
    }

    @Override
    public void updatePhoneWorkoutInformation(int workouts, int workoutTime) {
        storageManager.updatePhoneWorkoutInformation(workouts, workoutTime);
    }

    @Override
    public void registerLiveForWorkoutUpdates(LiveWorkoutUpdateListener listener) {
        storageManager.registerLiveWorkoutUpdates(listener);
    }

    @Override
    public void unregisterLiveForWorkoutUpdates(LiveWorkoutUpdateListener listener) {
        storageManager.unregisterLiveWorkoutUpdates(listener);
    }

}
