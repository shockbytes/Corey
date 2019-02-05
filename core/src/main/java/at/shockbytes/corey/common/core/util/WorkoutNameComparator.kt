package at.shockbytes.corey.common.core.util

import at.shockbytes.corey.common.core.workout.model.Workout
import java.util.Comparator

/**
 * Author:  Martin Macheiner
 * Date:    29.03.2017
 */
class WorkoutNameComparator : Comparator<Workout> {

    override fun compare(workout: Workout, t1: Workout): Int {
        return workout.displayableName.compareTo(t1.displayableName, ignoreCase = true)
    }
}
