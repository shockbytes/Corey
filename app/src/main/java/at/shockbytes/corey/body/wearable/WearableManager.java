package at.shockbytes.corey.body.wearable;

import android.support.v4.app.FragmentActivity;

import java.util.List;

import at.shockbytes.corey.common.core.workout.model.Workout;

/**
 * @author Martin Macheiner
 *         Date: 18.03.2017.
 */

public interface WearableManager {

    void connect(FragmentActivity activity);

    void synchronizeWorkouts(List<Workout> workouts);

    void onPause();

}
