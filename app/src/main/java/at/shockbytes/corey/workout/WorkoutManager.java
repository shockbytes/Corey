package at.shockbytes.corey.workout;

import java.util.List;

import at.shockbytes.corey.storage.live.LiveWorkoutUpdateListener;
import at.shockbytes.corey.workout.model.Exercise;
import at.shockbytes.corey.workout.model.Workout;
import rx.Observable;

/**
 * @author Martin Macheiner
 *         Date: 21.02.2017.
 */

public interface WorkoutManager {

    void poke();

    void addWorkout(Workout w);

    void deleteWorkout(Workout w);

    void updateWorkout(Workout w);

    Observable<List<Workout>> getWorkouts();

    Observable<List<Exercise>> getExercises();

    void registerLiveForWorkoutUpdates(LiveWorkoutUpdateListener listener);

    void unregisterLiveForWorkoutUpdates(LiveWorkoutUpdateListener listener);

}
