package at.shockbytes.corey.storage.live;

import at.shockbytes.corey.workout.model.Workout;

/**
 * @author Martin Macheiner
 *         Date: 27.02.2017.
 */

public interface LiveWorkoutUpdateListener {

    void onWorkoutAdded(Workout workout);

    void onWorkoutDeleted(Workout workout);

    void onWorkoutChanged(Workout workout);

}
