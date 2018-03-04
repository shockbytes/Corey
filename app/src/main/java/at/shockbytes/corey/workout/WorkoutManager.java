package at.shockbytes.corey.workout;

import java.util.List;

import at.shockbytes.corey.common.core.workout.model.Exercise;
import at.shockbytes.corey.common.core.workout.model.Workout;
import at.shockbytes.corey.storage.live.LiveWorkoutUpdateListener;
import io.reactivex.Observable;

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

    void updatePhoneWorkoutInformation(int workouts, int workoutTime);

    void registerLiveForWorkoutUpdates(LiveWorkoutUpdateListener listener);

    void unregisterLiveForWorkoutUpdates(LiveWorkoutUpdateListener listener);

}
