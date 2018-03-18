package at.shockbytes.corey.workout

import at.shockbytes.corey.common.core.workout.model.Exercise
import at.shockbytes.corey.common.core.workout.model.Workout
import io.reactivex.Observable

/**
 * @author Martin Macheiner
 * Date: 21.02.2017.
 */

interface WorkoutManager {

    val workouts: Observable<List<Workout>>

    val exercises: Observable<List<Exercise>>

    fun poke()

    fun storeWorkout(workout: Workout)

    fun deleteWorkout(workout: Workout)

    fun updateWorkout(workout: Workout)

    fun updatePhoneWorkoutInformation(workouts: Int, workoutTime: Int)

    fun updateWearWorkoutInformation(avgPulse: Int, workoutsWithPulse: Int, workoutTime: Int)

    fun registerLiveWorkoutUpdates(listener: LiveWorkoutUpdateListener)

    fun unregisterLiveWorkoutUpdates()

}
