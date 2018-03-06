package at.shockbytes.corey.workout

import at.shockbytes.corey.common.core.workout.model.Exercise
import at.shockbytes.corey.common.core.workout.model.Workout
import at.shockbytes.corey.storage.live.LiveWorkoutUpdateListener
import io.reactivex.Observable

/**
 * @author Martin Macheiner
 * Date: 21.02.2017.
 */

interface WorkoutManager {

    val workouts: Observable<List<Workout>>

    val exercises: Observable<List<Exercise>>

    fun poke()

    fun addWorkout(w: Workout)

    fun deleteWorkout(w: Workout)

    fun updateWorkout(w: Workout)

    fun updatePhoneWorkoutInformation(workouts: Int, workoutTime: Int)

    fun registerLiveForWorkoutUpdates(listener: LiveWorkoutUpdateListener)

    fun unregisterLiveForWorkoutUpdates()

}
