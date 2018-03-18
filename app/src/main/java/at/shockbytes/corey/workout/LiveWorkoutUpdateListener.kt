package at.shockbytes.corey.workout

import at.shockbytes.corey.common.core.workout.model.Workout

/**
 * @author  Martin Macheiner
 * Date:    27.02.2017
 */

interface LiveWorkoutUpdateListener {

    fun onWorkoutAdded(workout: Workout)

    fun onWorkoutDeleted(workout: Workout)

    fun onWorkoutChanged(workout: Workout)

}
