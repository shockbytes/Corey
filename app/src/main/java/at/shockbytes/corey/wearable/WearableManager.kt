package at.shockbytes.corey.wearable

import at.shockbytes.corey.common.core.util.WatchInfo
import at.shockbytes.corey.common.core.workout.model.Workout

/**
 * Author:  Martin Macheiner
 * Date:    18.03.2017
 */
interface WearableManager {

    fun onStart(nodeListener: ((WatchInfo) -> Unit)? = null)

    fun onPause()

    fun synchronizeWorkouts(workouts: List<Workout>)
}
