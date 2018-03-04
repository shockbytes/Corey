package at.shockbytes.corey.wearable

import android.support.v4.app.FragmentActivity

import at.shockbytes.corey.common.core.workout.model.Workout

/**
 * @author Martin Macheiner
 * Date: 18.03.2017.
 */

interface WearableManager {

    fun connect(activity: FragmentActivity)

    fun synchronizeWorkouts(workouts: List<Workout>)

    fun onPause()

}
