package at.shockbytes.corey.body.wearable;

import android.support.v4.app.FragmentActivity;

import java.util.List;

import at.shockbytes.corey.common.core.workout.model.Workout;

/**
 * @author Martin Macheiner
 *         Date: 18.03.2017.
 */

public interface WearableManager {

    interface OnWearableDataListener {

        void onWearableDataAvailable(int avgPulse, int workouts, int workoutTime);

    }

    void connectIfDeviceAvailable(FragmentActivity activity, OnWearableDataListener wearableDataListener);

    void synchronizeWorkouts(List<Workout> workouts);

    void synchronizeCountdownAndVibration(int countdown, boolean isVibrationEnabled);

    void onPause();

}
