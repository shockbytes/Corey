package at.shockbytes.corey.storage.live

import at.shockbytes.corey.common.core.workout.model.Workout

/**
 * @author Martin Macheiner
 * Date: 27.02.2017.
 */

interface LiveWorkoutUpdateListener {

    fun onWorkoutAdded(workout: Workout)

    fun onWorkoutDeleted(workout: Workout)

    fun onWorkoutChanged(workout: Workout)

}
